package ch.realmtechServer.ecs.system;

import ch.realmtechCommuns.packet.clientPacket.ChunkADamnePacket;
import ch.realmtechCommuns.packet.clientPacket.ChunkAMonterPacket;
import ch.realmtechServer.PhysiqueWorldHelper;
import ch.realmtechServer.ServerContext;
import ch.realmtechServer.ecs.component.*;
import ch.realmtechServer.level.cell.BreakCell;
import ch.realmtechServer.level.cell.Cells;
import ch.realmtechServer.level.cell.CreatePhysiqueBody;
import ch.realmtechServer.level.map.WorldMap;
import ch.realmtechServer.level.worldGeneration.PerlinNoise;
import ch.realmtechServer.options.DataCtrl;
import ch.realmtechServer.registery.CellRegisterEntry;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.systems.DelayedIteratingSystem;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@All(PlayerConnexionComponent.class)
public class MapSystemServer extends IteratingSystem {
    private final static String TAG = MapSystemServer.class.getSimpleName();
    private final static Logger logger = LoggerFactory.getLogger(MapSystemServer.class);
//    @Wire
//    private SoundManager soundManager;
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    @Wire(name = "physicWorld")
    private World physicWorld;
    @Wire
    private DataCtrl dataCtrl;
    @Wire
    private BodyDef bodyDef;
    @Wire
    private FixtureDef fixtureDef;

    private ComponentMapper<InfMapComponent> mInfMap;
    private ComponentMapper<InfMetaDonneesComponent> mMetaDonnees;
    private ComponentMapper<InfChunkComponent> mChunk;
    private ComponentMapper<InfCellComponent> mCell;
    private ComponentMapper<PositionComponent> mPosition;
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<PlayerComponent> mPlayer;
    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<CraftingTableComponent> mCraftingTable;
    private ComponentMapper<CellBeingMineComponent> mCellBeingMine;
    private ComponentMapper<Box2dComponent> mBox2d;
    private ComponentMapper<PlayerConnexionComponent> mPlayerConnexion;

    protected void process(int playerId) {
        int mapId = world.getSystem(TagManager.class).getEntityId("infMap");
        InfMapComponent infMapComponent = mInfMap.get(mapId);
        PositionComponent positionPlayerComponent = mPosition.get(playerId);
        PlayerConnexionComponent playerConnexionComponent = mPlayerConnexion.get(playerId);
        int chunkPosX = MapManager.getChunkPos((int) positionPlayerComponent.x);
        int chunkPosY = MapManager.getChunkPos((int) positionPlayerComponent.y);
        if (playerConnexionComponent.ancienChunkPos == null || !(playerConnexionComponent.ancienChunkPos[0] == chunkPosX && playerConnexionComponent.ancienChunkPos[1] == chunkPosY)) {
            List<Integer> chunkADamner = trouveChunkADamner(infMapComponent, chunkPosX, chunkPosY);

            int indexDamner = 0;
            stop:
            for (int i = -dataCtrl.option.renderDistance.get() + chunkPosX; i <= dataCtrl.option.renderDistance.get() + chunkPosX; i++) {
                for (int j = -dataCtrl.option.renderDistance.get() + chunkPosY; j <= dataCtrl.option.renderDistance.get() + chunkPosY; j++) {
                    final boolean changement = chunkSansChangement(infMapComponent, i, j);
                    if (changement) {
                        int newChunkId = getOrGenerateChunk(mapId, i, j);
                        InfChunkComponent infChunkComponent = mChunk.get(newChunkId);
                        if (indexDamner < chunkADamner.size()) {
                            Integer oldChunk = chunkADamner.get(indexDamner++);
                            InfChunkComponent infChunkComponentOld = mChunk.get(oldChunk);
                            serverContext.getServerHandler().sendPacketTo(new ChunkADamnePacket(
                                    infChunkComponentOld.chunkPosX,
                                    infChunkComponentOld.chunkPosY
                            ), playerConnexionComponent.channel);
                            serverContext.getServerHandler().sendPacketTo(new ChunkAMonterPacket(
                                    infChunkComponent.chunkPosX,
                                    infChunkComponent.chunkPosY,
                                    infChunkComponent.toBytes(mCell)
                            ), playerConnexionComponent.channel);
                            replaceChunk(infMapComponent.infChunks, oldChunk, newChunkId);
                            damneChunk(oldChunk, infMapComponent);
                        } else {
                            serverContext.getServerHandler().sendPacketTo(new ChunkAMonterPacket(
                                    infChunkComponent.chunkPosX,
                                    infChunkComponent.chunkPosY,
                                    infChunkComponent.toBytes(mCell)
                            ), playerConnexionComponent.channel);
                            infMapComponent.infChunks = world.getSystem(MapManager.class).ajouterChunkAMap(infMapComponent.infChunks, newChunkId);
                        }
                    }
                    // la limite d'update de chunk pour ce process est atteint
                    if (indexDamner >= dataCtrl.option.chunkParUpdate.get()) {
                        break stop;
                    }
                }
            }
            if (indexDamner < chunkADamner.size()) {
                for (int i = indexDamner; i < chunkADamner.size(); i++) {
                    final int chunkId = chunkADamner.get(i);
                    damneChunk(chunkId, infMapComponent);
                    infMapComponent.infChunks = world.getSystem(MapManager.class).supprimerChunkAMap(infMapComponent.infChunks, chunkId);
                }
            }
            if (playerConnexionComponent.ancienChunkPos == null) {
                playerConnexionComponent.ancienChunkPos = new int[2];
            }
            playerConnexionComponent.ancienChunkPos[0] = chunkPosX;
            playerConnexionComponent.ancienChunkPos[1] = chunkPosY;
        }
    }

    /**
     * Permet de savoir si le chunk n'a pas besoin d'être changé
     */
    private boolean chunkSansChangement(InfMapComponent infMapComponent, int i, int j) {
        boolean trouve = false;
        for (int k = 0; k < infMapComponent.infChunks.length; k++) {
            InfChunkComponent infChunkComponent = mChunk.get(infMapComponent.infChunks[k]);
            if (infChunkComponent.chunkPosX == i && infChunkComponent.chunkPosY == j) {
                trouve = true;
                break;
            }
        }
        return !trouve;
    }

    private List<Integer> trouveChunkADamner(InfMapComponent infMapComponent, int chunkPosX, int chunkPosY) {
        List<Integer> ret = new ArrayList<>(2 * dataCtrl.option.renderDistance.get() + 1);
        for (int i = 0; i < infMapComponent.infChunks.length; i++) {
            if (!chunkEstDansLaRenderDistance(infMapComponent.infChunks[i], chunkPosX, chunkPosY)) {
                ret.add(infMapComponent.infChunks[i]);
            }
        }
        return ret;
    }

    private void damneChunk(int chunkId, InfMapComponent infMapComponent) {
        try {
            world.getSystem(SaveInfManager.class).saveInfChunk(chunkId, SaveInfManager.getSavePath(mMetaDonnees.get(infMapComponent.infMetaDonnees).saveName));
        } catch (IOException e) {
            InfChunkComponent infChunkComponent = mChunk.get(chunkId);
            logger.error("Le chunk {},{} n'a pas été sauvegardé correctement", infChunkComponent.chunkPosX, infChunkComponent.chunkPosY);
        }
        world.getSystem(MapManager.class).supprimeChunk(chunkId);
    }



    private boolean chunkEstDansLaRenderDistance(int chunkId, int posX, int posY) {
        InfChunkComponent infChunkComponent = mChunk.get(chunkId);
        int dstX = Math.abs(posX - infChunkComponent.chunkPosX);
        int dstY = Math.abs(posY - infChunkComponent.chunkPosY);
        // à remplacer pour une valuer dans une configuration
        return dstX <= 4 /*dataCtrl.option.renderDistance.get()*/ && dstY <= 4 /*dataCtrl.option.renderDistance.get()*/;
    }

    private int getOrGenerateChunk(int mapId, int chunkX, int chunkY) {
        InfMetaDonneesComponent infMetaDonneesComponent = mMetaDonnees.get(mInfMap.get(mapId).infMetaDonnees);
        int chunkId;
        try {
            chunkId = world.getSystem(SaveInfManager.class).readSavedInfChunk(chunkX, chunkY, infMetaDonneesComponent.saveName);
        } catch (FileNotFoundException e) {
            chunkId = world.getSystem(MapManager.class).generateNewChunk(mapId, chunkX, chunkY);
            try {
                world.getSystem(SaveInfManager.class).saveInfChunk(chunkId, SaveInfManager.getSavePath(infMetaDonneesComponent.saveName));
            } catch (IOException ex) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return chunkId;
    }

    private void newCellInChunk(int chunkId, CellRegisterEntry cellRegisterEntry, byte innerX, byte innerY) {
        InfChunkComponent infChunkComponent = mChunk.get(chunkId);
        int cellId = world.getSystem(MapManager.class).newCell(chunkId, infChunkComponent.chunkPosX, infChunkComponent.chunkPosY, innerX, innerY, cellRegisterEntry);
        int[] newCellsArray = new int[infChunkComponent.infCellsId.length + 1];
        System.arraycopy(infChunkComponent.infCellsId, 0, newCellsArray, 0, infChunkComponent.infCellsId.length);
        newCellsArray[newCellsArray.length - 1] = cellId;
        infChunkComponent.infCellsId = newCellsArray;
    }

    public int getCell(int[] chunks, int worldPosX, int worldPosY, byte layer) {
        int chunk = getChunk(chunks, worldPosX, worldPosY);
        int ret = -1;
        if (chunk != -1) {
            ret = getCell(chunk, worldPosX, worldPosY, layer);
        }
        return ret;
    }

    public int getCell(int chunkId, int worldPosX, int worldPosY, byte layer) {
        int ret = -1;
        int[] cells = mChunk.get(chunkId).infCellsId;
        byte innerChunkX = MapManager.getInnerChunk(worldPosX);
        byte innerChunkY = MapManager.getInnerChunk(worldPosY);
        for (int i = 0; i < cells.length; i++) {
            InfCellComponent infCellComponent = mCell.get(cells[i]);
            if (infCellComponent.getInnerPosX() == innerChunkX && infCellComponent.getInnerPosY() == innerChunkY && infCellComponent.cellRegisterEntry.getCellBehavior().getLayer() == layer) {
                ret = cells[i];
                break;
            }
        }
        return ret;
    }

    public int findChunk(int[] chunks, int cellId) {
        for (int chunk : chunks) {
            InfChunkComponent infChunkComponent = mChunk.get(chunk);
            for (int cell : infChunkComponent.infCellsId) {
                if (cell == cellId) {
                    return chunk;
                }
            }
        }
        throw new NoSuchElementException("Le chunk qui correspond à la cellule n'a pas été trouvé");
    }

    public int getCell(int chunk, byte innerX, byte innerY, byte layer) {
        int ret = -1;
        int[] cells = mChunk.get(chunk).infCellsId;
        for (int i = 0; i < cells.length; i++) {
            InfCellComponent infCellComponent = mCell.get(cells[i]);
            if (infCellComponent.getInnerPosX() == innerX && infCellComponent.getInnerPosY() == innerY && infCellComponent.cellRegisterEntry.getCellBehavior().getLayer() == layer) {
                ret = cells[i];
                break;
            }
        }
        return ret;
    }

    /**
     * @return chunk id. -1 si pas trouvé
     */
    public int getChunk(int[] chunks, int worldPosX, int worldPosY) {
        int ret = -1;
        int chunkX = MapManager.getChunkPos(worldPosX);
        int chunkY = MapManager.getChunkPos(worldPosY);
        for (int i = 0; i < chunks.length; i++) {
            InfChunkComponent infChunkComponent = mChunk.get(chunks[i]);
            if (infChunkComponent.chunkPosX == chunkX && infChunkComponent.chunkPosY == chunkY) {
                ret = chunks[i];
                break;
            }
        }
        return ret;
    }

    public int getChunk(int[] chunks, float gameCoordinateX, float gameCoordinateY) {
        int ret = -1;
        int chunkX = MapManager.getChunkPos(gameCoordinateX);
        int chunkY = MapManager.getChunkPos(gameCoordinateY);
        for (int i = 0; i < chunks.length; i++) {
            InfChunkComponent infChunkComponent = mChunk.get(chunks[i]);
            if (infChunkComponent.chunkPosX == chunkX && infChunkComponent.chunkPosY == chunkY) {
                ret = chunks[i];
                break;
            }
        }
        return ret;
    }
    private void replaceChunk(int[] chunks, int oldChunk, int newChunkId) {
        for (int i = 0; i < chunks.length; i++) {
            if (chunks[i] == oldChunk) {
                chunks[i] = newChunkId;
                return;
            }
        }
    }

    public int getTopCell(int chunk, byte innerX, byte innerY) {
        int ret = -1;
        for (byte i = Cells.Layer.BUILD_DECO.layer; i >= 0; i--) {
            int cellId = world.getSystem(MapSystemServer.class).getCell(chunk, innerX, innerY, i);
            if (cellId != -1) {
                ret = cellId;
                break;
            }
        }
        return ret;
    }


    public void breakCell(int chunk, int cellId, int playerId) {
        InfCellComponent infCellComponent = mCell.get(cellId);
        BreakCell breakCellEvent = infCellComponent.cellRegisterEntry.getCellBehavior().getBreakCellEvent();
        if (breakCellEvent != null) {
//            if (breakCellEvent.breakCell(world, chunk, cellId, mItem.get(world.getSystem(ItemBarManager.class).getSelectItem()), mPlayer.get(playerId))) {
//                soundManager.playCellBreak();
//            }
        }
    }

    public boolean placeItemToBloc(final int playerId, final int button, final int[] chunks, final float gameCoordinateX, final float gameCoordinateY, int selectedItem) {
//        if (selectedItem > 0) {
//            final ItemRegisterEntry selectedItemEntry = mItem.get(world.getSystem(ItemBarManager.class).getSelectItem()).itemRegisterEntry;
//            if (selectedItemEntry.getItemBehavior().getPlaceCell() != null) {
//                final byte innerX = getInnerChunk(gameCoordinateX);
//                final byte innerY = getInnerChunk(gameCoordinateY);
//                final int chunkId = getChunk(chunks, gameCoordinateX, gameCoordinateY);
//                if (getCell(chunkId, innerX, innerY, selectedItemEntry.getItemBehavior().getPlaceCell().getCellBehavior().getLayer()) == -1) {
//                    newCellInChunk(chunkId, selectedItemEntry.getItemBehavior().getPlaceCell(), innerX, innerY);
//                    return true;
//                }
//            }
//        }
        return false;
    }

//    public void interagieClickDroit(int playerId, int button, int[] infChunks, float x, float y, int selectItem) {
//        final int chunk = getChunk(infChunks, x, y);
//        final int topCell = getTopCell(chunk, getInnerChunk(x), getInnerChunk(y));
//        InfCellComponent infCellComponent = mCell.get(topCell);
//        if (infCellComponent.cellRegisterEntry.getCellBehavior().getInteragieClickDroit() != null) {
//            infCellComponent.cellRegisterEntry.getCellBehavior().getInteragieClickDroit().accept(world, topCell);
//        } else {
//            if (world.getSystem(MapSystem.class).placeItemToBloc(context.getEcsEngine().getPlayerId(), button, context.getEcsEngine().getWorld().getMapper(InfMapComponent.class).get(context.getEcsEngine().getMapId()).infChunks, x, y, context.getSystem(ItemBarManager.class).getSelectItem())) {
//                world.getSystem(InventoryManager.class).removeOneItem(context.getSystem(ItemBarManager.class).getSelectStack());
//            }
//        }
//    }

    public boolean addCellBeingMine(int cellId) {
        boolean ret = false;
        if (!mCellBeingMine.has(cellId)) {
            mCellBeingMine.create(cellId).set(0, mCell.get(cellId).cellRegisterEntry.getCellBehavior().getBreakStepNeed());
            ret = true;
        }
        return ret;
    }

//    public static Vector3 getGameCoordinate(RealmTech context, Vector2 screenCoordinate) {
//        return context.getGameStage().getCamera().unproject(new Vector3(screenCoordinate, 0));
//    }
//
//    public static int getTopCell(RealmTech context, int chunk, Vector2 screenCoordinate) {
//        Vector3 gameCoordinate = getGameCoordinate(context, screenCoordinate);
//        return context.getEcsEngine().getWorld().getSystem(MapSystem.class).getTopCell(chunk, getInnerChunk(gameCoordinate.x), getInnerChunk(gameCoordinate.y));
//    }
//
//    public static int getChunk(RealmTech context, Vector2 screenCoordinate) {
//        int[] infChunks = getChunkInUse(context);
//        Vector3 gameCoordinate = getGameCoordinate(context, screenCoordinate);
//        return context.getEcsEngine().getWorld().getSystem(MapSystem.class).getChunk(infChunks, gameCoordinate.x, gameCoordinate.y);
//    }
//
//    public static int[] getChunkInUse(RealmTech context) {
//        return context.getEcsEngine().getMapEntity().getComponent(InfMapComponent.class).infChunks;
//    }
}

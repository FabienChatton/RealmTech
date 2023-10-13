package ch.realmtechServer.ecs.system;

import ch.realmtechServer.ServerContext;
import ch.realmtechServer.divers.Position;
import ch.realmtechServer.ecs.component.*;
import ch.realmtechServer.options.DataCtrl;
import ch.realmtechServer.packet.clientPacket.ChunkAMonterPacket;
import ch.realmtechServer.packet.clientPacket.ChunkAReplacePacket;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.List;

@All(PlayerConnexionComponent.class)
public class MapSystemServer extends IteratingSystem {
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
        InfMapComponent infMapComponent = mInfMap.get(world.getSystem(TagManager.class).getEntityId("infMap"));
        int[] infChunks = infMapComponent.infChunks;
        InfMetaDonneesComponent infMetaDonnesComponent = infMapComponent.getMetaDonnesComponent(world);
        PositionComponent positionPlayerComponent = mPosition.get(playerId);
        PlayerConnexionComponent playerConnexionComponent = mPlayerConnexion.get(playerId);
        int chunkPosX = MapManager.getChunkPos((int) positionPlayerComponent.x);
        int chunkPosY = MapManager.getChunkPos((int) positionPlayerComponent.y);
        if (playerConnexionComponent.ancienChunkPos == null || !(playerConnexionComponent.ancienChunkPos[0] == chunkPosX && playerConnexionComponent.ancienChunkPos[1] == chunkPosY)) {
            List<Position> chunkADamnerPos = trouveChunkADamner(playerConnexionComponent.chunkPoss, chunkPosX, chunkPosY);

            int indexDamner = 0;
            for (int i = -dataCtrl.option.renderDistance.get() + chunkPosX; i <= dataCtrl.option.renderDistance.get() + chunkPosX; i++) {
                for (int j = -dataCtrl.option.renderDistance.get() + chunkPosY; j <= dataCtrl.option.renderDistance.get() + chunkPosY; j++) {
                    final boolean changement = chunkSansChangement(playerConnexionComponent.chunkPoss, i, j);
                    if (changement) {
                        int newChunkId = getOrGenerateChunk(infMetaDonnesComponent, i, j);
                        InfChunkComponent infChunkComponent = mChunk.get(newChunkId);
                        int newChunkPosX = infChunkComponent.chunkPosX;
                        int newChunkPosY = infChunkComponent.chunkPosY;
                        if (indexDamner < chunkADamnerPos.size()) {
                            Position chunkPos = chunkADamnerPos.get(indexDamner++);
                            int oldChunk = world.getSystem(MapManager.class).getChunk(infChunks, chunkPos.x(), chunkPos.y());
                            InfChunkComponent infChunkComponentOld = mChunk.get(oldChunk);
                            int oldChunkPosX = infChunkComponentOld.chunkPosX;
                            int oldChunkPosy = infChunkComponentOld.chunkPosY;
                            serverContext.getServerHandler().sendPacketTo(new ChunkAReplacePacket(
                                    newChunkPosX,
                                    newChunkPosY,
                                    infChunkComponent.toBytes(mCell),
                                    oldChunkPosX,
                                    oldChunkPosy
                            ), playerConnexionComponent.channel);
                            Position.replace(playerConnexionComponent.chunkPoss, oldChunkPosX, oldChunkPosy, newChunkPosX, newChunkPosY);
                        } else {
                            serverContext.getServerHandler().sendPacketTo(new ChunkAMonterPacket(
                                    newChunkPosX,
                                    newChunkPosY,
                                    infChunkComponent.toBytes(mCell)
                            ), playerConnexionComponent.channel);
                            playerConnexionComponent.chunkPoss.add(new Position(newChunkPosX, newChunkPosY));
                        }
                    }
                    // la limite d'update de chunk pour ce process est atteint
//                    if (indexDamner >= dataCtrl.option.chunkParUpdate.get()) {
//                        break stop;
//                    }
                }
            }
//            if (indexDamner < chunkADamner.size()) {
//                for (int i = indexDamner; i < chunkADamner.size(); i++) {
//                    final int chunkId = chunkADamner.get(i);
//                    damneChunk(chunkId, infMetaDonnesComponent);
//                    playerConnexionComponent.infChunks = world.getSystem(MapManager.class).supprimerChunkAMap(playerConnexionComponent.infChunks, chunkId);
//                }
//            }
            if (playerConnexionComponent.ancienChunkPos == null) {
                playerConnexionComponent.ancienChunkPos = new int[2];
            }
            playerConnexionComponent.ancienChunkPos[0] = chunkPosX;
            playerConnexionComponent.ancienChunkPos[1] = chunkPosY;
        }
    }

    private boolean chunkSansChangement(List<Position> chunkPoss, int i, int j) {
        boolean trouve = false;
        for (Position position : chunkPoss) {
            if (position.x() == i && position.y() == j) {
                trouve = true;
                break;
            }
        }
        return !trouve;
    }

    private List<Position> trouveChunkADamner(List<Position> poss, int chunkPosX, int chunkPosY) {
        List<Position> ret = new ArrayList<>(2 * dataCtrl.option.renderDistance.get() + 1);
        for (Position position : poss) {
            if (!chunkEstDansLaRenderDistance(position, chunkPosX, chunkPosY)) {
                ret.add(position);
            }
        }
        return ret;
    }

    public int[] damneChunkServer(int[] infChunks, int chunkId, InfMetaDonneesComponent infMetaDonneesComponent) {
        try {
            world.getSystem(SaveInfManager.class).saveInfChunk(chunkId, SaveInfManager.getSavePath(infMetaDonneesComponent.saveName));
        } catch (IOException e) {
            InfChunkComponent infChunkComponent = mChunk.get(chunkId);
            logger.error("Le chunk {},{} n'a pas été sauvegardé correctement", infChunkComponent.chunkPosX, infChunkComponent.chunkPosY);
        }
        return world.getSystem(MapManager.class).supprimerChunkAMap(infChunks, chunkId);
    }

    public boolean chunkEstDansLaRenderDistance(Position position, int posX, int posY) {
        int dstX = Math.abs(posX - position.x());
        int dstY = Math.abs(posY - position.y());
        return dstX <=dataCtrl.option.renderDistance.get() && dstY <= dataCtrl.option.renderDistance.get();
    }

    private int getOrGenerateChunk(InfMetaDonneesComponent infMetaDonneesComponent, int chunkX, int chunkY) {
        int chunkId;
        try {
            chunkId = world.getSystem(SaveInfManager.class).readSavedInfChunk(chunkX, chunkY, infMetaDonneesComponent.saveName);
        } catch (FileNotFoundException | BufferUnderflowException e) {
            if (e instanceof BufferUnderflowException) logger.error("Le chunk {},{} est corrompu", chunkX, chunkY);
            chunkId = world.getSystem(MapManager.class).generateNewChunk(infMetaDonneesComponent, chunkX, chunkY);
            try {
                world.getSystem(SaveInfManager.class).saveInfChunk(chunkId, SaveInfManager.getSavePath(infMetaDonneesComponent.saveName));
            } catch (IOException ex) {
                logger.error(e.getMessage(), ex);
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return chunkId;
    }

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

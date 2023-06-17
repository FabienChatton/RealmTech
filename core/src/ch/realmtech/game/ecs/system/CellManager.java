package ch.realmtech.game.ecs.system;

import ch.realmtech.game.ecs.component.CellComponent;
import ch.realmtech.game.ecs.component.ChunkComponent;
import ch.realmtech.game.ecs.component.ToSaveComponent;
import ch.realmtech.game.level.map.WorldMap;
import ch.realmtech.game.level.worldGeneration.PerlinNoise;
import ch.realmtech.game.mod.RealmTechCoreCell;
import ch.realmtech.game.mod.RealmTechCoreMod;
import ch.realmtech.game.registery.CellRegisterEntry;
import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.link.EntityLinkManager;
import com.artemis.link.LinkAdapter;
import com.artemis.utils.IntBag;

public class CellManager extends EntityLinkManager {
    private ComponentMapper<ChunkComponent> mChunk;
    private ComponentMapper<CellComponent> mCell;
    private Archetype defaultCellArchetype;

    @Override
    protected void initialize() {
        super.initialize();
        defaultCellArchetype = new ArchetypeBuilder()
                .add(CellComponent.class)
                .add(ToSaveComponent.class)
                .build(world);

        register(CellComponent.class, new LinkAdapter() {
            @Override
            public void onLinkEstablished(int sourceId, int targetId) {
                //System.out.println("onLinkEstablished");
                ChunkComponent chunkComponent = mChunk.get(targetId);
                CellComponent cellComponent = mCell.get(sourceId);
                world.getSystem(WorldMapManager.class).placeOnMap(
                        WorldMapManager.getWorldPossX(chunkComponent.chunkPossX, cellComponent.innerChunkPossX),
                        WorldMapManager.getWorldPossY(chunkComponent.chunkPossY, cellComponent.innerChunkPossY),
                        cellComponent.layer,
                        cellComponent.cell
                );
            }

            @Override
            public void onLinkKilled(int sourceId, int targetId) {
                if (sourceId != -1 && targetId != -1) {
                    ChunkComponent chunkComponent = mChunk.get(targetId);
                    CellComponent cellComponent = mCell.get(sourceId);
                    world.getSystem(WorldMapManager.class).placeOnMap(
                            WorldMapManager.getWorldPossX(chunkComponent.chunkPossX, cellComponent.innerChunkPossX),
                            WorldMapManager.getWorldPossY(chunkComponent.chunkPossY, cellComponent.innerChunkPossY),
                            cellComponent.layer,
                            null
                    );
                }
                world.delete(sourceId);
            }

            @Override
            public void onTargetDead(int sourceId, int deadTargetId) {
                world.delete(sourceId);
            }

            @Override
            public void onTargetChanged(int sourceId, int targetId, int oldTargetId) {
                System.out.println("onTargetChanged");
                System.out.println("s " + sourceId + " t " + targetId + " old " + oldTargetId);
            }
        });
    }

    /**
     * Génère toutes les cellules d'un chunk.
     *
     * @param chunkId     L'id du chunk parent.
     * @param perlinNoise Le bruit auquel la cellule va pouvoir de référer pour ce créer.
     */
    public void generateNewCells(int chunkId, PerlinNoise perlinNoise) {
        final ChunkComponent parentChunkComponent = mChunk.get(chunkId);
        IntBag cells = new IntBag(WorldMap.CHUNK_SIZE * WorldMap.CHUNK_SIZE);
        for (byte innerChunkX = 0; innerChunkX < WorldMap.CHUNK_SIZE; innerChunkX++) {
            for (byte innerChunkY = 0; innerChunkY < WorldMap.CHUNK_SIZE; innerChunkY++) {
                int worldX = WorldMapManager.getWorldPossX(parentChunkComponent.chunkPossX, innerChunkX);
                int worldY = WorldMapManager.getWorldPossY(parentChunkComponent.chunkPossY, innerChunkY);
                final CellRegisterEntry cellRegisterEntry;
                if (perlinNoise.getGrid()[worldX][worldY] > 0f && perlinNoise.getGrid()[worldX][worldY] < 0.5f) {
                    cellRegisterEntry = RealmTechCoreMod.REALM_TECH_CORE_CELL_REGISTRY.get(RealmTechCoreCell.GRASS_CELL);
                } else if (perlinNoise.getGrid()[worldX][worldY] >= 0.5f) {
                    cellRegisterEntry = RealmTechCoreMod.REALM_TECH_CORE_CELL_REGISTRY.get(RealmTechCoreCell.SAND_CELL);
                } else {
                    cellRegisterEntry = RealmTechCoreMod.REALM_TECH_CORE_CELL_REGISTRY.get(RealmTechCoreCell.WATER_CELL);
                }
                cells.add(newCell(chunkId, innerChunkX, innerChunkY, (byte) 0, cellRegisterEntry));
            }
        }
        inserted(cells);
    }

    /**
     * Créer une nouvelle cellule.
     *
     * @param parentChunkId     Le chunk parent.
     * @param innerChunkX       La coordonnée x dans le chunk.
     * @param innerChunkY       La coordonnée y dans le chunk.
     * @param layer             Le layer de la future cellule
     * @param cellRegisterEntry Le registre sur lequel la création de la cellule va se baser
     * @return L'id de la nouvelle cellule créer.
     */
    public int newCell(int parentChunkId, byte innerChunkX, byte innerChunkY, byte layer, CellRegisterEntry cellRegisterEntry) {
        final int cell;
        if (cellRegisterEntry.getArchetype() != null) {
            cell = world.create(cellRegisterEntry.getArchetype());
        } else {
            cell = world.create(defaultCellArchetype);
        }
        CellComponent cellComponent = world.edit(cell).create(CellComponent.class);
        cellComponent.set(parentChunkId, innerChunkX, innerChunkY, layer, cellRegisterEntry);
        return cell;
    }

    /**
     * Récupère l'id de la cellule à la coordonnée et au niveau spécifiés.
     *
     * @param worldX La coordonnée du monde x.
     * @param worldY La coordonnée du monde y.
     * @param layer  le niveau de la carte.
     * @return return l'id de la cellule ou -1 si aucune cellule n'a été trouvée à la coordonnée spécifiée.
     */
    public int getCell(int worldX, int worldY, byte layer) {
        int chunkId = world.getSystem(ChunkManager.class).getChunk(worldX, worldY);
        for (int cellId : getCells(chunkId).getData()) {
            byte innerX = WorldMapManager.getInnerChunkX(worldX);
            byte innerY = WorldMapManager.getInnerChunkY(worldY);
            if (cellId == 0) continue;
            CellComponent cellComponent = mCell.create(cellId);
            if (cellComponent.innerChunkPossX == innerX &&
                    cellComponent.innerChunkPossY == innerY &&
                    cellComponent.layer == layer) {
                return cellId;
            }
        }
        return -1;
    }

    /**
     * Récupère l'id cellule qui est la plus en haut.
     *
     * @param worldX La coordonnée du monde X.
     * @param worldY La coordonnée du monde Y.
     * @return return l'id de la cellule qui est au sommet ou -1 si aucune cellule n'est à cette coordonnée.
     */
    public int getTopCell(int worldX, int worldY) {
        int ret = -1;
        for (byte i = 0; i < WorldMap.NUMBER_LAYER; i++) {
            int topCell = getCell(worldX, worldY, i);
            if (topCell == -1) {
                break;
            } else {
                ret = topCell;
            }
        }
        return ret;
    }

    /**
     * Récupère le layer au-dessus de la cellule la plus haute.
     *
     * @param worldX La coordonnée X du monde.
     * @param worldY La coordonnée Y du monde.
     * @return -1 si une cellule se trouve déjà sur le niveau maximum.
     */
    public byte getOnTopLayer(int worldX, int worldY) {
        byte ret = -1;
        for (byte layer = WorldMap.NUMBER_LAYER - 1; layer >= 0; layer--) {
            int topCellId = getCell(worldX, worldY, layer);
            if (topCellId == -1) {
                ret = layer;
            } else {
                break;
            }
        }
        return ret;
    }

    /**
     * Donne toutes les cellules qui appartiennent un chunk
     *
     * @param partentChunkId le chunk id du parent
     * @return un bag qui contient toutes les cellules du chunk partent passé en parameter
     */
    public IntBag getCells(int partentChunkId) {
        IntBag ret = new IntBag();
        IntBag cells = world.getAspectSubscriptionManager().get(Aspect.all(CellComponent.class)).getEntities();
        for (int cell : cells.getData()) {
            if (!mCell.has(cell)) {
                continue;
            }
            CellComponent cellComponent = mCell.create(cell);
            if (cellComponent.parentChunk == partentChunkId) {
                ret.add(cell);
            }
        }
        return ret;
    }

    /**
     * Donne le hash du mod et du nom de la cellule
     *
     * @param cellRegisterEntry Le nom du registre qu'ont souhait connaitre la clé (mod + nom cellule)
     * @return Le hash du mod avec le nom de la cellule ou -1 si le registre global ne contient pas ce registre.
     */
    public int getModAndCellHash(CellRegisterEntry cellRegisterEntry) {
        int ret = -1;
        for (String cellKey : RealmTechCoreMod.REALM_TECH_CORE_CELL_REGISTRY.keySet()) {
            if (RealmTechCoreMod.REALM_TECH_CORE_CELL_REGISTRY.get(cellKey) == cellRegisterEntry) {
                ret = cellKey.hashCode();
                break;
            }
        }
        return ret;
    }

}

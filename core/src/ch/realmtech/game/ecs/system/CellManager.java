package ch.realmtech.game.ecs.system;

import ch.realmtech.game.ecs.component.CellComponent;
import ch.realmtech.game.ecs.component.ChunkComponent;
import ch.realmtech.game.level.cell.CellType;
import ch.realmtech.game.level.map.WorldMap;
import ch.realmtech.game.level.worldGeneration.PerlinNoise;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.link.EntityLinkManager;
import com.artemis.link.LinkAdapter;
import com.artemis.utils.IntBag;

public class CellManager extends EntityLinkManager {
    private ComponentMapper<ChunkComponent> mChunk;
    private ComponentMapper<CellComponent> mCell;

    public void register() {
        register(CellComponent.class, new LinkAdapter() {
            @Override
            public void onLinkEstablished(int sourceId, int targetId) {
                //System.out.println("onLinkEstablished");
                ChunkComponent chunkComponent = mChunk.get(targetId);
                CellComponent cellComponent = mCell.get(sourceId);
                world.getSystem(WorldMapManager.class).placeOnMap(
                        getWorldPossX(chunkComponent.chunkPossX, cellComponent.innerChunkPossX),
                        getWorldPossY(chunkComponent.chunkPossY, cellComponent.innerChunkPossY),
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
                            getWorldPossX(chunkComponent.chunkPossX, cellComponent.innerChunkPossX),
                            getWorldPossY(chunkComponent.chunkPossY, cellComponent.innerChunkPossY),
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
     * @param chunkId L'id du chunk parent.
     * @param perlinNoise Le bruit auquel la cellule va pouvoir de référer pour ce créer.
     */
    public void generateNewCells(int chunkId, PerlinNoise perlinNoise) {
        final ChunkComponent parentChunkComponent = mChunk.get(chunkId);
        IntBag cells = new IntBag(WorldMap.CHUNK_SIZE * WorldMap.CHUNK_SIZE);
        for (byte innerChunkX = 0; innerChunkX < WorldMap.CHUNK_SIZE; innerChunkX++) {
            for (byte innerChunkY = 0; innerChunkY < WorldMap.CHUNK_SIZE; innerChunkY++) {
                int worldX = getWorldPossX(parentChunkComponent.chunkPossX, innerChunkX);
                int worldY = getWorldPossY(parentChunkComponent.chunkPossY, innerChunkY);
                CellType cellType;
                if (perlinNoise.getGrid()[worldX][worldY] > 0f && perlinNoise.getGrid()[worldX][worldY] < 0.5f) {
                    cellType = CellType.GRASS;
                } else if (perlinNoise.getGrid()[worldX][worldY] >= 0.5f) {
                    cellType = CellType.SAND;
                } else {
                    cellType = CellType.WATER;
                }
                cells.add(newCell(chunkId, innerChunkX, innerChunkY, (byte) 0, cellType));
            }
        }
        inserted(cells);
    }

    /**
     * Créer une nouvelle cellule.
     *
     * @param parentChunkId Le chunk parent.
     * @param innerChunkX   La coordonnée x dans le chunk.
     * @param innerChunkY   La coordonnée y dans le chunk.
     * @param layer         Le layer de la future cellule
     * @param cellType      Le type de la future cellule.
     * @return L'id de la nouvelle cellule créer.
     */
    public int newCell(int parentChunkId, byte innerChunkX, byte innerChunkY, byte layer, CellType cellType) {
        int cell = world.create();
        CellComponent cellComponent = world.edit(cell).create(CellComponent.class);
        world.inject(cellComponent);
        cellComponent.init(parentChunkId, innerChunkX, innerChunkY, layer, cellType);
        return cell;
    }

    public int getWorldPossX(int chunkPossX, int innerChunkX) {
        return chunkPossX * WorldMap.CHUNK_SIZE + innerChunkX;
    }

    public int getWorldPossY(int chunkPossY, int innerChunkY) {
        return chunkPossY * WorldMap.CHUNK_SIZE + innerChunkY;
    }

    /**
     * Récupère l'id de la cellule à la coordonnée et au niveau spécifiés.
     * @param worldX La coordonnée du monde x.
     * @param worldY La coordonnée du monde y.
     * @param layer le niveau de la carte.
     * @return return l'id de la cellule ou -1 si aucune cellule n'a été trouvée à la coordonnée spécifiée.
     */
    public int getCell(int worldX, int worldY, byte layer) {
        int chunkId = world.getSystem(ChunkManager.class).getChunk(worldX, worldY);
        for (int cellId : getCells(chunkId).getData()) {
            byte innerX = getInnerChunkX(worldX);
            byte innerY = getInnerChunkY(worldY);
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
     * Récupère une position Y dans un chunk via la position Y du monde.
     * @param worldY La position Y dans le monde.
     * @return La position Y dans le chunk.
     */
    public byte getInnerChunkY(int worldY) {
        return (byte) (worldY % WorldMap.CHUNK_SIZE);
    }

    /**
     * Récupère une position X dans un chunk via la position X du monde.
     * @param worldX La position X dans le monde.
     * @return La position X dans le chunk.
     */
    public byte getInnerChunkX(int worldX) {
        return (byte) (worldX % WorldMap.CHUNK_SIZE);
    }

    /**
     * Récupère l'id cellule qui est la plus en haut.
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

    public byte getInnerChunkPossY(byte innerChunkPoss) {
        return (byte) (innerChunkPoss & 0x0F);
    }

    public byte getInnerChunkPossX(byte innerChunkPoss) {
        return (byte) ((innerChunkPoss >> 4) & 0x0F);
    }

    public byte getInnerChunkPoss(byte innerChunkPossX, byte innerChunkPossY) {
        return (byte) ((innerChunkPossX << 4) + innerChunkPossY);
    }
}

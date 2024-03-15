package ch.realmtech.server.newMod.CellsEntry;

import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.component.CellComponent;
import ch.realmtech.server.ecs.component.InfChunkComponent;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.level.cell.EditEntity;
import ch.realmtech.server.newRegistry.NewCellEntry;
import com.badlogic.gdx.graphics.Color;

public class TorchCellEntry extends NewCellEntry {
    public TorchCellEntry() {
        super("Torch", "torch-01", CellBehavior.builder(Cells.Layer.BUILD_DECO)
                .canPlaceCellOnTop(false)
                .editEntity(new EditEntity() {
                    @Override
                    public void createEntity(ExecuteOnContext executeOnContext, int entityId) {
                        executeOnContext.onClient((systemsAdminClient, world) -> {
                            CellComponent cellComponent = world.getMapper(CellComponent.class).get(entityId);
                            InfChunkComponent chunkComponent = world.getMapper(InfChunkComponent.class).get(cellComponent.chunkId);
                            int worldX = MapManager.getWorldPos(chunkComponent.chunkPosX, cellComponent.getInnerPosX());
                            int worldY = MapManager.getWorldPos(chunkComponent.chunkPosY, cellComponent.getInnerPosY());
                            systemsAdminClient.getLightManager().createLight(entityId, Color.valueOf("ef540b"), 15, worldX, worldY);
                        });
                    }

                    @Override
                    public void deleteEntity(ExecuteOnContext executeOnContext, int entityId) {
                        executeOnContext.onClient((systemsAdminClient, world) -> systemsAdminClient.getLightManager().disposeLight(entityId));
                    }

                })
                .breakWith(ItemType.HAND, "realmtech.items.Torch")
                .build());
    }

    @Override
    public int getId() {
        return 1395027571;
    }
}

package ch.realmtech.server.inventory;

import ch.realmtech.server.ecs.plugin.forclient.SystemsAdminClientForClient;
import ch.realmtech.server.registry.ItemEntry;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Null;

import java.util.UUID;
import java.util.function.BiPredicate;

public final class DisplayInventoryArgs {
    private final UUID inventoryUuid;
    private final Table inventoryTable;
    private final boolean clickAndDropSrc;
    private final boolean clickAndDropDst;
    @Null
    private final BiPredicate<SystemsAdminClientForClient, ItemEntry> dstRequirePredicate;

    private DisplayInventoryArgs(UUID inventoryUuid, Table inventoryTable, boolean clickAndDropSrc, boolean clickAndDropDst, BiPredicate<SystemsAdminClientForClient, ItemEntry> dstRequirePredicate) {
        this.inventoryUuid = inventoryUuid;
        this.inventoryTable = inventoryTable;
        this.clickAndDropSrc = clickAndDropSrc;
        this.clickAndDropDst = clickAndDropDst;
        this.dstRequirePredicate = dstRequirePredicate;
    }

    public static DisplayInventoryArgsBuilder builder(UUID inventoryUuid, Table inventoryTable) {
        return new DisplayInventoryArgsBuilder(inventoryUuid, inventoryTable);
    }

    public UUID inventoryUuid() {
        return inventoryUuid;
    }

    public Table inventoryTable() {
        return inventoryTable;
    }

    public boolean isClickAndDropSrc() {
        return clickAndDropSrc;
    }

    public boolean isClickAndDropDst() {
        return clickAndDropDst;
    }

    public BiPredicate<SystemsAdminClientForClient, ItemEntry> getDstRequirePredicate() {
        return dstRequirePredicate;
    }

    public static class DisplayInventoryArgsBuilder {
        private final UUID inventoryUuid;
        private final Table inventoryTable;
        private boolean clickAndDropSrc = true;
        private boolean clickAndDropDst = true;
        private BiPredicate<SystemsAdminClientForClient, ItemEntry> dstRequirePredicate = null;

        private DisplayInventoryArgsBuilder(UUID inventoryUuid, Table inventoryTable) {
            this.inventoryUuid = inventoryUuid;
            this.inventoryTable = inventoryTable;
        }

        public DisplayInventoryArgsBuilder notClickAndDropSrc() {
            clickAndDropSrc = false;
            return this;
        }

        public DisplayInventoryArgsBuilder notClickAndDropDst() {
            clickAndDropDst = false;
            return this;
        }

        public DisplayInventoryArgsBuilder dstRequire(BiPredicate<SystemsAdminClientForClient, ItemEntry> dstRequirePredicate) {
            this.dstRequirePredicate = dstRequirePredicate;
            return this;
        }

        public DisplayInventoryArgsBuilder icon() {
            notClickAndDropSrc();
            notClickAndDropDst();
            return this;
        }

        public DisplayInventoryArgs build() {
            return new DisplayInventoryArgs(inventoryUuid, inventoryTable, clickAndDropSrc, clickAndDropDst, dstRequirePredicate);
        }
    }
}
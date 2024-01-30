package ch.realmtech.server.inventory;

import ch.realmtech.server.ecs.plugin.forclient.SystemsAdminClientForClient;
import ch.realmtech.server.registery.ItemRegisterEntry;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Null;

import java.util.function.BiPredicate;

public final class DisplayInventoryArgs {
    private final int inventoryId;
    private final Table inventoryTable;
    private final boolean clickAndDropSrc;
    private final boolean clickAndDropDst;
    @Null
    private final BiPredicate<SystemsAdminClientForClient, ItemRegisterEntry> dstRequirePredicate;

    private DisplayInventoryArgs(int inventoryId, Table inventoryTable, boolean clickAndDropSrc, boolean clickAndDropDst, BiPredicate<SystemsAdminClientForClient, ItemRegisterEntry> dstRequirePredicate) {
        this.inventoryId = inventoryId;
        this.inventoryTable = inventoryTable;
        this.clickAndDropSrc = clickAndDropSrc;
        this.clickAndDropDst = clickAndDropDst;
        this.dstRequirePredicate = dstRequirePredicate;
    }

    public static DisplayInventoryArgsBuilder builder(int inventoryId, Table inventoryTable) {
        return new DisplayInventoryArgsBuilder(inventoryId, inventoryTable);
    }

    public int inventoryId() {
        return inventoryId;
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

    public BiPredicate<SystemsAdminClientForClient, ItemRegisterEntry> getDstRequirePredicate() {
        return dstRequirePredicate;
    }

    public static class DisplayInventoryArgsBuilder {
        private final int inventoryId;
        private final Table inventoryTable;
        private boolean clickAndDropSrc = true;
        private boolean clickAndDropDst = true;
        private BiPredicate<SystemsAdminClientForClient, ItemRegisterEntry> dstRequirePredicate = null;

        private DisplayInventoryArgsBuilder(int inventoryId, Table inventoryTable) {
            this.inventoryId = inventoryId;
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

        public DisplayInventoryArgsBuilder dstRequire(BiPredicate<SystemsAdminClientForClient, ItemRegisterEntry> dstRequirePredicate) {
            this.dstRequirePredicate = dstRequirePredicate;
            return this;
        }

        public DisplayInventoryArgsBuilder icon() {
            notClickAndDropSrc();
            notClickAndDropDst();
            return this;
        }

        public DisplayInventoryArgs build() {
            return new DisplayInventoryArgs(inventoryId, inventoryTable, clickAndDropSrc, clickAndDropDst, dstRequirePredicate);
        }
    }
}
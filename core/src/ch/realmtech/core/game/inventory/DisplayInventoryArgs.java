package ch.realmtech.core.game.inventory;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public final class DisplayInventoryArgs {
    private final int inventoryId;
    private final Table inventoryTable;
    private final boolean clickAndDropSrc;
    private final boolean clickAndDropDst;

    private DisplayInventoryArgs(int inventoryId, Table inventoryTable, boolean clickAndDropSrc, boolean clickAndDropDst) {
        this.inventoryId = inventoryId;
        this.inventoryTable = inventoryTable;
        this.clickAndDropSrc = clickAndDropSrc;
        this.clickAndDropDst = clickAndDropDst;
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

    public boolean clickAndDropSrc() {
        return clickAndDropSrc;
    }

    public boolean clickAndDropDst() {
        return clickAndDropDst;
    }


    public static class DisplayInventoryArgsBuilder {
        private int inventoryId;
        private Table inventoryTable;
        private boolean clickAndDropSrc = true;
        private boolean clickAndDropDst = true;
        private boolean icon = false;


        public DisplayInventoryArgsBuilder(int inventoryId, Table inventoryTable) {
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

        public DisplayInventoryArgsBuilder icon() {
            icon = true;
            notClickAndDropSrc();
            notClickAndDropDst();
            return this;
        }

        public DisplayInventoryArgs build() {
            return new DisplayInventoryArgs(inventoryId, inventoryTable, clickAndDropSrc, clickAndDropDst);
        }
    }
}
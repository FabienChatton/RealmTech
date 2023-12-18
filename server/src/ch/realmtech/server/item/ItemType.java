package ch.realmtech.server.item;

public enum ItemType {
    UNBREAKABLE((byte) 0),
    HAND((byte) 1),
    SHOVEL((byte) 2),
    PICKAXE((byte) 3);
    private final byte flag;

    ItemType(byte flag) {
        this.flag = flag;
    }

    public byte getFlag() {
        return flag;
    }
}

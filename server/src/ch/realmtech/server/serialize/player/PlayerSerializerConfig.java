package ch.realmtech.server.serialize.player;

public class PlayerSerializerConfig {
    private int playerId;
    /** @since V2 */
    private boolean writeInventory = true;

    public PlayerSerializerConfig(byte playerSerializerConfigByte) {
        initFromByte(playerSerializerConfigByte);
    }

    public PlayerSerializerConfig() {
    }

    public int getPlayerId() {
        return playerId;
    }

    public PlayerSerializerConfig playerId(int playerId) {
        this.playerId = playerId;
        return this;
    }

    public PlayerSerializerConfig writeInventory(boolean writeInventory) {
        this.writeInventory = writeInventory;
        return this;
    }

    public boolean isWriteInventory() {
        return writeInventory;
    }

    public byte toByte() {
        byte ret = 0;
        if (isWriteInventory()) {
            ret |= 1 << 0;
        }
        return ret;
    }

    public void initFromByte(byte b) {
        writeInventory((b & 0b00000001) == 1);
    }
}

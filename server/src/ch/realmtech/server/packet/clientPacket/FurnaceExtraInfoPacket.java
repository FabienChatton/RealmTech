package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class FurnaceExtraInfoPacket implements ClientPacket {
    private final UUID furnaceUuid;
    private final int lastRemainingTickToBurnFull;
    private final int lastTickProcessFull;

    public FurnaceExtraInfoPacket(UUID furnaceUuid, int lastRemainingTickToBurnFull, int lastTickProcessFull) {
        this.furnaceUuid = furnaceUuid;
        this.lastRemainingTickToBurnFull = lastRemainingTickToBurnFull;
        this.lastTickProcessFull = lastTickProcessFull;
    }

    public FurnaceExtraInfoPacket(ByteBuf byteBuf) {
        this.furnaceUuid = ByteBufferHelper.readUUID(byteBuf);
        this.lastRemainingTickToBurnFull = byteBuf.readInt();
        this.lastTickProcessFull = byteBuf.readInt();
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.furnaceExtraInfo(furnaceUuid, lastRemainingTickToBurnFull, lastTickProcessFull);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeUUID(byteBuf, furnaceUuid);
        byteBuf.writeInt(lastRemainingTickToBurnFull);
        byteBuf.writeInt(lastTickProcessFull);
    }

    @Override
    public int getSize() {
        int uuidLength = Long.BYTES * 2;
        return uuidLength + 2 * Integer.BYTES;
    }
}

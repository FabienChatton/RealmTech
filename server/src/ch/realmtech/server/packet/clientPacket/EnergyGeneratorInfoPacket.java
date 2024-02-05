package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class EnergyGeneratorInfoPacket implements ClientPacket {
    private final UUID energyGeneratorUuid;
    private final int remainingTickToBurn;
    private final int lastRemainingTickToBurn;

    public EnergyGeneratorInfoPacket(UUID energyGeneratorUuid, int remainingTickToBurn, int lastRemainingTickToBurn) {
        this.energyGeneratorUuid = energyGeneratorUuid;
        this.remainingTickToBurn = remainingTickToBurn;
        this.lastRemainingTickToBurn = lastRemainingTickToBurn;
    }

    public EnergyGeneratorInfoPacket(ByteBuf byteBuf) {
        this.energyGeneratorUuid = ByteBufferHelper.readUUID(byteBuf);
        this.remainingTickToBurn = byteBuf.readInt();
        this.lastRemainingTickToBurn = byteBuf.readInt();
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.energyGeneratorSetInfo(energyGeneratorUuid, remainingTickToBurn, lastRemainingTickToBurn);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeUUID(byteBuf, energyGeneratorUuid);
        byteBuf.writeInt(remainingTickToBurn);
        byteBuf.writeInt(lastRemainingTickToBurn);
    }

    @Override
    public int getSize() {
        int uuidLength = Long.BYTES * 2;
        int storedLength = Long.BYTES;
        return uuidLength + storedLength + 2 * Integer.BYTES;
    }
}

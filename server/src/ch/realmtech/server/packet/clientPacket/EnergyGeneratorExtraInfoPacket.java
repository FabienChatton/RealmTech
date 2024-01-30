package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class EnergyGeneratorExtraInfoPacket implements ClientPacket {
    private final UUID energyGeneratorUuid;
    private final int remainingTickToBurn;

    public EnergyGeneratorExtraInfoPacket(UUID energyGeneratorUuid, int remainingTickToBurn) {
        this.energyGeneratorUuid = energyGeneratorUuid;
        this.remainingTickToBurn = remainingTickToBurn;
    }

    public EnergyGeneratorExtraInfoPacket(ByteBuf byteBuf) {
        energyGeneratorUuid = ByteBufferHelper.readUUID(byteBuf);
        remainingTickToBurn = byteBuf.readInt();
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.energyGeneratorExtraInfo(energyGeneratorUuid, remainingTickToBurn);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeUUID(byteBuf, energyGeneratorUuid);
        byteBuf.writeInt(remainingTickToBurn);
    }

    @Override
    public int getSize() {
        int uuidLength = Long.BYTES * 2;
        int remainingTickToBurnLength = Integer.BYTES;
        return uuidLength + remainingTickToBurnLength;
    }
}

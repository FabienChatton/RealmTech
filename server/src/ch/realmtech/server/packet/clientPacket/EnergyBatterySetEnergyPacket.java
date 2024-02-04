package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class EnergyBatterySetEnergyPacket implements ClientPacket {

    private final UUID energyBatteryUuid;
    private final long stored;

    public EnergyBatterySetEnergyPacket(UUID energyBatteryUuid, long stored) {
        this.energyBatteryUuid = energyBatteryUuid;
        this.stored = stored;
    }

    public EnergyBatterySetEnergyPacket(ByteBuf byteBuf) {
        this.energyBatteryUuid = ByteBufferHelper.readUUID(byteBuf);
        this.stored = byteBuf.readLong();
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.energyBatterySetEnergy(energyBatteryUuid, stored);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeUUID(byteBuf, energyBatteryUuid);
        byteBuf.writeLong(stored);
    }

    @Override
    public int getSize() {
        return Long.BYTES * 3;
    }
}

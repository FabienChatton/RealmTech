package ch.realmtechCommuns.packet;

import com.artemis.World;

public interface ClientPacket extends Packet {
    void executeOnClient(World world);
}

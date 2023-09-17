package ch.realmtechCommuns.packet;

import ch.realmtechCommuns.packet.clientPacket.ClientExecute;

public interface ClientPacket extends Packet {
    void executeOnClient(ClientExecute clientExecute);
}

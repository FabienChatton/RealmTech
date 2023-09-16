package ch.realmtechCommuns.packet;

import ch.realmtechCommuns.packet.clientPacket.ClientExecute;

public interface ClientPacket<T extends ClientExecute> extends Packet {
    void executeOnClient(T clientExecute);
}

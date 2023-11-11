package ch.realmtech.server.packet;


import ch.realmtech.server.packet.clientPacket.ClientExecute;

public interface ClientPacket extends Packet {
    void executeOnClient(ClientExecute clientExecute);
}

package ch.realmtechServer.packet;


import ch.realmtechServer.packet.clientPacket.ClientExecute;

public interface ClientPacket extends Packet {
    void executeOnClient(ClientExecute clientExecute);
}

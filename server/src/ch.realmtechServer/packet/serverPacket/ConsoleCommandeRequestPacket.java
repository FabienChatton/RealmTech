package ch.realmtechServer.packet.serverPacket;

import ch.realmtechServer.divers.ByteBufferStringHelper;
import ch.realmtechServer.packet.ServerPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class ConsoleCommandeRequestPacket implements ServerPacket {
    private final String stringCommande;

    public ConsoleCommandeRequestPacket(String stringCommande) {
        this.stringCommande = stringCommande;
    }

    public ConsoleCommandeRequestPacket(ByteBuf byteBuf) {
        stringCommande = ByteBufferStringHelper.getString(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferStringHelper.writeString(byteBuf, stringCommande);
    }

    @Override
    public int getSize() {
        return stringCommande.length();
    }

    @Override
    public void executeOnServer(Channel clientChannel, ServerExecute serverExecute) {
        serverExecute.consoleCommande(clientChannel, stringCommande);
    }
}

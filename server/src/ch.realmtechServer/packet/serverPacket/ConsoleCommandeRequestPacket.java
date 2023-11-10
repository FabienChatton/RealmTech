package ch.realmtechServer.packet.serverPacket;

import ch.realmtechServer.divers.ByteBufferHelper;
import ch.realmtechServer.packet.ServerPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class ConsoleCommandeRequestPacket implements ServerPacket {
    private final String stringCommande;

    public ConsoleCommandeRequestPacket(String stringCommande) {
        this.stringCommande = stringCommande;
    }

    public ConsoleCommandeRequestPacket(ByteBuf byteBuf) {
        stringCommande = ByteBufferHelper.getString(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeString(byteBuf, stringCommande);
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

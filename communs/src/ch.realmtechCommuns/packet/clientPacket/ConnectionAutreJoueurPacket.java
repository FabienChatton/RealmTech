package ch.realmtechCommuns.packet.clientPacket;

import ch.realmtechCommuns.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

public class ConnectionAutreJoueurPacket implements ClientPacket<ClientExecute> {
    private final float x;
    private final float y;

    public ConnectionAutreJoueurPacket(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public ConnectionAutreJoueurPacket(ByteBuf byteBuf) {
        this.x = byteBuf.readFloat();
        this.y = byteBuf.readFloat();
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.connectionAutreJoueur(x, y);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeFloat(x);
        byteBuf.writeFloat(y);
    }
}

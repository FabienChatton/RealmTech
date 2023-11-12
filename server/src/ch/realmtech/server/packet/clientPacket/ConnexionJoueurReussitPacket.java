package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

/**
 * Le serveur donne se packet quand le joueur initie la connexion et que la connexion, et réussie.
 * Reçoit les informations sur sa position dans le monde et sont UUID.
 */
public class ConnexionJoueurReussitPacket implements ClientPacket {
    private final ConnexionJoueurReussitArg connexionJoueurReussitArg;

    public ConnexionJoueurReussitPacket(ConnexionJoueurReussitArg connexionJoueurReussitArg) {
        this.connexionJoueurReussitArg = connexionJoueurReussitArg;
    }

    public ConnexionJoueurReussitPacket(ByteBuf byteBuf) {
        float x = byteBuf.readFloat();
        float y = byteBuf.readFloat();
        UUID playerUuid = ByteBufferHelper.readUUID(byteBuf);
        int inventoryBytesLength = byteBuf.readInt();
        byte[] inventory = new byte[inventoryBytesLength];
        byteBuf.readBytes(inventory);
        UUID inventoryUuid = ByteBufferHelper.readUUID(byteBuf);
        UUID inventoryCraftUuid = ByteBufferHelper.readUUID(byteBuf);
        UUID inventoryCraftResultUuid = ByteBufferHelper.readUUID(byteBuf);
        connexionJoueurReussitArg = new ConnexionJoueurReussitArg(x, y, playerUuid,
                inventory,
                inventoryUuid,
                inventoryCraftUuid,
                inventoryCraftResultUuid
        );
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.connexionJoueurReussit(connexionJoueurReussitArg);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeFloat(connexionJoueurReussitArg.x);
        byteBuf.writeFloat(connexionJoueurReussitArg.y);
        ByteBufferHelper.writeUUID(byteBuf, connexionJoueurReussitArg.playerUuid);
        byteBuf.writeInt(connexionJoueurReussitArg.inventoryBytes.length);
        byteBuf.writeBytes(connexionJoueurReussitArg.inventoryBytes);
        ByteBufferHelper.writeUUID(byteBuf, connexionJoueurReussitArg.inventoryUuid);
        ByteBufferHelper.writeUUID(byteBuf, connexionJoueurReussitArg.inventoryCraftUuid);
        ByteBufferHelper.writeUUID(byteBuf, connexionJoueurReussitArg.inventoryCraftResultUuid);
    }

    public record ConnexionJoueurReussitArg(float x, float y, UUID playerUuid,
            byte[] inventoryBytes,
            UUID inventoryUuid,
            UUID inventoryCraftUuid,
            UUID inventoryCraftResultUuid
    ) { }
    @Override
    public int getSize() {
        return Float.SIZE * 2 + Long.SIZE * 2 + Integer.SIZE + connexionJoueurReussitArg.inventoryBytes.length * Byte.SIZE + Long.SIZE * 2 * 2;
    }
}

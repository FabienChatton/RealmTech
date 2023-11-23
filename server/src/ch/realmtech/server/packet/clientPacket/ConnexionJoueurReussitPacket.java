package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ClientPacket;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
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
        SerializedApplicationBytes applicationInventoryBytes = new SerializedApplicationBytes(new byte[inventoryBytesLength]);
        byteBuf.readBytes(applicationInventoryBytes.applicationBytes());
        UUID inventoryUuid = ByteBufferHelper.readUUID(byteBuf);
        UUID inventoryCraftUuid = ByteBufferHelper.readUUID(byteBuf);
        UUID inventoryCraftResultUuid = ByteBufferHelper.readUUID(byteBuf);
        UUID inventoryCursorUuid = ByteBufferHelper.readUUID(byteBuf);
        connexionJoueurReussitArg = new ConnexionJoueurReussitArg(x, y, playerUuid,
                applicationInventoryBytes,
                inventoryUuid,
                inventoryCraftUuid,
                inventoryCraftResultUuid,
                inventoryCursorUuid
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
        byteBuf.writeInt(connexionJoueurReussitArg.applicationInventoryBytes.applicationBytes().length);
        byteBuf.writeBytes(connexionJoueurReussitArg.applicationInventoryBytes.applicationBytes());
        ByteBufferHelper.writeUUID(byteBuf, connexionJoueurReussitArg.inventoryUuid);
        ByteBufferHelper.writeUUID(byteBuf, connexionJoueurReussitArg.inventoryCraftUuid);
        ByteBufferHelper.writeUUID(byteBuf, connexionJoueurReussitArg.inventoryCraftResultUuid);
        ByteBufferHelper.writeUUID(byteBuf, connexionJoueurReussitArg.inventoryCursorUuid);
    }

    public record ConnexionJoueurReussitArg(float x, float y, UUID playerUuid,
            SerializedApplicationBytes applicationInventoryBytes,
            UUID inventoryUuid,
            UUID inventoryCraftUuid,
            UUID inventoryCraftResultUuid,
            UUID inventoryCursorUuid
    ) { }
    @Override
    public int getSize() {
        return Float.SIZE * 2 + Long.SIZE * 2 + Integer.SIZE + connexionJoueurReussitArg.applicationInventoryBytes.applicationBytes().length * Byte.SIZE + Long.SIZE * 2 * 4;
    }
}

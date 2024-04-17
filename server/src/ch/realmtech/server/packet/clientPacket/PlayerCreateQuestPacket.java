package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ClientPacket;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import io.netty.buffer.ByteBuf;

public class PlayerCreateQuestPacket implements ClientPacket {
    private final SerializedApplicationBytes questSerializedApplicationBytes;

    public PlayerCreateQuestPacket(SerializedApplicationBytes questSerializedApplicationBytes) {
        this.questSerializedApplicationBytes = questSerializedApplicationBytes;
    }

    public PlayerCreateQuestPacket(ByteBuf byteBuf) {
        questSerializedApplicationBytes = ByteBufferHelper.readSerializedApplicationBytes(byteBuf);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.playerCreateQuest(questSerializedApplicationBytes);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeSerializedApplicationBytes(byteBuf, questSerializedApplicationBytes);
    }

    @Override
    public int getSize() {
        return 0;
    }
}

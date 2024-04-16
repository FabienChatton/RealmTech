package ch.realmtech.server.packet.serverPacket;

import ch.realmtech.server.packet.ServerPacket;
import ch.realmtech.server.packet.clientPacket.QuestSetCompleted;
import ch.realmtech.server.registry.QuestEntry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class QuestCheckboxCompletedPacket implements ServerPacket {
    private final int questEntryId;

    public QuestCheckboxCompletedPacket(QuestEntry questEntry) {
        this.questEntryId = questEntry.getId();
    }

    public QuestCheckboxCompletedPacket(ByteBuf byteBuf) {
        questEntryId = byteBuf.readInt();
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeInt(questEntryId);
    }

    @Override
    public int getSize() {
        return Integer.BYTES;
    }

    @Override
    public void executeOnServer(Channel clientChannel, ServerExecute serverExecute) {
        long completedTimestamp = serverExecute.getContext().getSystemsAdminServer().getQuestManagerServer().completeQuest(clientChannel, questEntryId);
        serverExecute.getContext().getServerConnexion().sendPacketTo(new QuestSetCompleted(questEntryId, completedTimestamp), clientChannel);
    }
}

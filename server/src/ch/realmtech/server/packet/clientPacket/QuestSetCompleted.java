package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

public class QuestSetCompleted implements ClientPacket {
    private final int questEntryId;
    private final long completedTimestamp;

    public QuestSetCompleted(int questEntryId, long completedTimestamp) {
        this.questEntryId = questEntryId;
        this.completedTimestamp = completedTimestamp;
    }

    public QuestSetCompleted(ByteBuf byteBuf) {
        questEntryId = byteBuf.readInt();
        completedTimestamp = byteBuf.readLong();
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.questSetCompleted(questEntryId, completedTimestamp);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeInt(questEntryId);
        byteBuf.writeLong(completedTimestamp);
    }

    @Override
    public int getSize() {
        return Integer.BYTES + Long.BYTES;
    }
}

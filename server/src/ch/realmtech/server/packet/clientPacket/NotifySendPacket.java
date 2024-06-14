package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.divers.Notify;
import ch.realmtech.server.packet.ClientPacket;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import io.netty.buffer.ByteBuf;

public class NotifySendPacket implements ClientPacket {
    private final SerializedApplicationBytes notifySerializedApplicationBytes;

    public NotifySendPacket(ServerContext serverContext, Notify notify) {
        notifySerializedApplicationBytes = serverContext.getSerializerController().getNotifySerializerController().encode(notify);
    }

    public NotifySendPacket(ByteBuf byteBuf) {
        notifySerializedApplicationBytes = ByteBufferHelper.readSerializedApplicationBytes(byteBuf);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.notifySend(notifySerializedApplicationBytes);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeSerializedApplicationBytes(byteBuf, notifySerializedApplicationBytes);
    }

    @Override
    public int getSize() {
        return notifySerializedApplicationBytes.getLength();
    }
}

package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

public class WriteToConsolePacket implements ClientPacket {
    private final String consoleMessageToWrite;

    public WriteToConsolePacket(String consoleMessageToWrite) {
        this.consoleMessageToWrite = consoleMessageToWrite;
    }

    public WriteToConsolePacket(ByteBuf byteBuf) {
        consoleMessageToWrite = ByteBufferHelper.getString(byteBuf);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.writeOnConsoleMessage(consoleMessageToWrite);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeString(byteBuf, consoleMessageToWrite);
    }

    @Override
    public int getSize() {
        return consoleMessageToWrite.length();
    }
}

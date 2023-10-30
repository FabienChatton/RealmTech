package ch.realmtechServer.packet.clientPacket;

import ch.realmtechServer.divers.ByteBufferStringHelper;
import ch.realmtechServer.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

public class WriteToConsolePacket implements ClientPacket {
    private final String consoleMessageToWrite;

    public WriteToConsolePacket(String consoleMessageToWrite) {
        this.consoleMessageToWrite = consoleMessageToWrite;
    }

    public WriteToConsolePacket(ByteBuf byteBuf) {
        consoleMessageToWrite = ByteBufferStringHelper.getString(byteBuf);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.writeOnConsoleMessage(consoleMessageToWrite);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferStringHelper.writeString(byteBuf, consoleMessageToWrite);
    }

    @Override
    public int getSize() {
        return consoleMessageToWrite.length();
    }
}

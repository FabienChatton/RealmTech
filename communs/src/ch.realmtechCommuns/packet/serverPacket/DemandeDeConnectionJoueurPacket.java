package ch.realmtechCommuns.packet.serverPacket;

import ch.realmtechCommuns.ecs.system.PhysicEntityManager;
import ch.realmtechCommuns.packet.ServerPacket;
import ch.realmtechCommuns.packet.ServerResponseHandler;
import ch.realmtechCommuns.packet.clientPacket.ConnectionJoueurReussitPacket;
import com.artemis.World;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class DemandeDeConnectionJoueurPacket implements ServerPacket {

    public DemandeDeConnectionJoueurPacket() {
    }

    public DemandeDeConnectionJoueurPacket(ByteBuf byteBuf) {

    }

    @Override
    public void write(ByteBuf byteBuf) {

    }

    @Override
    public void executeOnServer(ChannelHandlerContext ctx, World world, ServerResponseHandler serverResponseHandler) {
        world.getSystem(PhysicEntityManager.class).createPlayer(ctx.channel());
        serverResponseHandler.sendPacketTo(new ConnectionJoueurReussitPacket(), ctx.channel());
    }
}

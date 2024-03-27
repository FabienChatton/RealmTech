package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.packet.ClientPacket;
import com.badlogic.gdx.math.Vector2;
import io.netty.buffer.ByteBuf;

import java.util.Arrays;

public class ParticleAddPacket implements ClientPacket {
    private final Particles particle;
    private final Vector2 gameCoordinate;

    public ParticleAddPacket(Particles particle, Vector2 gameCoordinate) {
        this.particle = particle;
        this.gameCoordinate = gameCoordinate;
    }

    public ParticleAddPacket(ByteBuf byteBuf) {
        int particleId = byteBuf.readInt();
        particle = Arrays.stream(Particles.values()).filter((particle) -> particle.id == particleId).findFirst().orElseThrow();

        float x = byteBuf.readFloat();
        float y = byteBuf.readFloat();
        gameCoordinate = new Vector2(x, y);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.addParticle(particle, gameCoordinate);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeInt(particle.id);
        byteBuf.writeFloat(gameCoordinate.x);
        byteBuf.writeFloat(gameCoordinate.y);
    }

    @Override
    public int getSize() {
        return Integer.BYTES + Float.BYTES * 2;
    }

    public enum Particles {
        HIT(1);

        private final int id;

        Particles(int id) {
            this.id = id;
        }
    }
}

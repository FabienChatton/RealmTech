package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.server.packet.serverPacket.TimeGetRequestPacket;
import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;

public class TimeSystemSimulation extends BaseSystem {
    @Wire(name = "context")
    private RealmTech context;
    private float accumulatedDelta;
    private int tickNextTimeGetRequest;
    @Override
    protected void processSystem() {
        accumulatedDelta += world.getDelta();
        if (tickNextTimeGetRequest-- == 0) {
            context.getConnexionHandler().sendAndFlushPacketToServer(new TimeGetRequestPacket());
            tickNextTimeGetRequest = 60;
        }
    }

    public void setAccumulatedDelta(float accumulatedDelta) {
        this.accumulatedDelta = accumulatedDelta;
    }

    public float getAccumulatedDelta() {
        return accumulatedDelta;
    }
}

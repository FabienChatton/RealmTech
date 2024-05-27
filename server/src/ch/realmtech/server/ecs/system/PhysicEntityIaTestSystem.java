package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.enemy.EnemyComponent;
import ch.realmtech.server.packet.clientPacket.EnemySetPacket;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import com.artemis.Component;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.Bag;

@All({PositionComponent.class, EnemyComponent.class})
public class PhysicEntityIaTestSystem extends IteratingSystem {
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    @Override
    protected void process(int entityId) {
        Bag<Component> bag = new Bag<>();
        world.getEntity(entityId).getComponents(bag);
        SerializedApplicationBytes physicEntityBytes = world.getRegistered(SerializerController.class).getEnemySerializerController().encode(entityId);
        serverContext.getServerConnexion().broadCastPacket(new EnemySetPacket(physicEntityBytes));
    }
}

package ch.realmtech.core.game.ecs.system;


import ch.realmtech.server.ecs.component.PlayerComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.plugin.SystemServerTickSlave;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

@SystemServerTickSlave
@All(PlayerComponent.class)
public class PlayerMouvementTextureSystem extends IteratingSystem {
    private ComponentMapper<PlayerComponent> mPlayer;
    private ComponentMapper<PositionComponent> mPos;
    private List<Vector2> oldPoss;

    @Override
    protected void initialize() {
        super.initialize();
        oldPoss = new FixList<>(10);
    }

    @Override
    protected void process(int entityId) {
        PlayerComponent playerComponent = mPlayer.get(entityId);
        PositionComponent positionComponent = mPos.get(entityId);

        oldPoss.add(new Vector2(positionComponent.x, positionComponent.y));
        playerComponent.moveLeft = oldPoss.get(0).x > positionComponent.x;
        playerComponent.moveDown = oldPoss.get(0).y > positionComponent.y;
        playerComponent.moveUp = oldPoss.get(0).y < positionComponent.y;
        playerComponent.moveRight = oldPoss.get(0).x < positionComponent.x;

    }
}

class FixList<T> extends ArrayList<T> {
    private final int maxCapacity;

    public FixList(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    @Override
    public boolean add(T t) {
        boolean ret = super.add(t);
        if (size() > maxCapacity) {
            removeRange(0, size() - maxCapacity);
        }
        return ret;
    }
}

package ch.realmtech.game.ecs.component;

import ch.realmtech.game.ecs.PoolableComponent;

public class PossitionComponent implements PoolableComponent {
    public float x;
    public float y;

    @Override
    public void reset() {
        x = 0;
        y = 0;
    }
}

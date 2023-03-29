package ch.realmtech.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public interface PoolableComponent extends Pool.Poolable, Component {
}

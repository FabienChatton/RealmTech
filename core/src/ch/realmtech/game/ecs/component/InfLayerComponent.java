package ch.realmtech.game.ecs.component;

import com.artemis.PooledComponent;
import com.artemis.annotations.EntityId;

/**
 * Le niveau du layer.
 * <ol start="0">
 *  <li>ground</li>
 *  <li>ground deco</li>
 *  <li>build</li>
 *  <li>build deco</li>
 * </ol>
 */
public class InfLayerComponent extends PooledComponent {
    public byte layer;
    @EntityId
    public int[] infCells;

    public InfLayerComponent set(byte layer, int[] infCells) {
        this.layer = layer;
        this.infCells = infCells;
        return this;
    }

    @Override
    protected void reset() {
        layer = -1;
        infCells = null;
    }
}

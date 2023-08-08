package ch.realmtech.game.ecs.component;

import com.artemis.PooledComponent;
import com.artemis.World;
import com.artemis.annotations.EntityId;

public class InfMapComponent extends PooledComponent {
    public int[] infChunks;
    @EntityId
    public int infMetaDonnees;

    public InfMapComponent set(int[] infChunks, int infMetaDonneesComponent) {
        this.infChunks = infChunks;
        this.infMetaDonnees = infMetaDonneesComponent;
        return this;
    }

    public InfMetaDonneesComponent getMetaDonnesComponent(World world) {
        return world.getMapper(InfMetaDonneesComponent.class).get(infMetaDonnees);
    }

    @Override
    protected void reset() {
        infChunks = null;
        infMetaDonnees = -1;
    }
}

package ch.realmtech.game.ecs.component;

import com.artemis.PooledComponent;
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

    @Override
    protected void reset() {
        infChunks = null;
        infMetaDonnees = -1;
    }
}

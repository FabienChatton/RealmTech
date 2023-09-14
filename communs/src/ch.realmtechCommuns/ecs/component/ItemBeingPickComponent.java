package ch.realmtechCommuns.ecs.component;

import com.artemis.Component;

public class ItemBeingPickComponent extends Component {
    /**
     * l'entité qui est en train de prendre cet item
     */
    public int picker;

    public void set(int picker) {
        this.picker = picker;
    }
}

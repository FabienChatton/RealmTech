package ch.realmtech.server.ecs.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class FurnaceIconsComponent extends Component {
    @EntityId
    private int iconFire;

    @EntityId
    private int iconProcess;

    public FurnaceIconsComponent set(int iconFire, int iconProcess) {
        this.iconFire = iconFire;
        this.iconProcess = iconProcess;
        return this;
    }

    public int getIconFire() {
        return iconFire;
    }

    public int getIconProcess() {
        return iconProcess;
    }
}

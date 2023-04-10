package ch.realmtech.game.ecs.component;

import ch.realmtech.RealmTech;
import com.artemis.Component;

import java.io.File;

public class SaveComponent extends Component {

    public File file;

    public RealmTech context;

    public void init(RealmTech context, File file) {
        this.context = context;
        this.file = file;
    }
}

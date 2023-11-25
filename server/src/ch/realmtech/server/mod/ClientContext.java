package ch.realmtech.server.mod;

import ch.realmtech.server.ecs.GetWorld;
import ch.realmtech.server.inventory.AddAndDisplayInventoryArgs;
import com.artemis.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.function.Supplier;

public interface ClientContext extends GetWorld {
    int getPlayerId();
    void openPlayerInventory(Supplier<AddAndDisplayInventoryArgs> openPlayerInventorySupplier);
    Skin getSkin();
}

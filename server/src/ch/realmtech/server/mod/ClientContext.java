package ch.realmtech.server.mod;

import ch.realmtech.server.ecs.Context;
import ch.realmtech.server.ecs.GetWorld;
import ch.realmtech.server.inventory.AddAndDisplayInventoryArgs;
import ch.realmtech.server.newRegistry.NewRegistry;
import ch.realmtech.server.packet.ServerPacket;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ClientContext extends GetWorld, Context {
    int getPlayerId();
    void openPlayerInventory(Supplier<AddAndDisplayInventoryArgs> openPlayerInventorySupplier);
    Skin getSkin();
    void sendRequest(ServerPacket packet);
    void writeToConsole(String s);
    NewRegistry<?> getRootRegistry();

    void getWorldOr(Consumer<GetWorld> world);
}

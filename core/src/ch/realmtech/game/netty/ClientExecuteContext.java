package ch.realmtech.game.netty;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.system.PhysicPlayerManagerClient;
import ch.realmtech.screen.ScreenType;
import ch.realmtechCommuns.ecs.component.PlayerComponent;
import ch.realmtechCommuns.ecs.component.PlayerConnectionComponent;
import ch.realmtechCommuns.packet.clientPacket.ClientExecute;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.Gdx;

import java.util.UUID;

public class ClientExecuteContext implements ClientExecute {
    private final RealmTech context;

    public ClientExecuteContext(RealmTech context) {
        this.context = context;
    }

    @Override
    public void connectionJoueurReussit(float x, float y, UUID uuid) {
        context.getEcsEngine().getSystem(PhysicPlayerManagerClient.class).createPlayerClient(x, y, uuid);
        Gdx.app.postRunnable(() -> context.setScreen(ScreenType.GAME_SCREEN));
    }

    @Override
    public void connectionAutreJoueur(float x, float y, UUID uuid) {
        int mainPlayerId = PlayerComponent.getMainPlayer(context.getEcsEngine().getWorld());
        ComponentMapper<PlayerConnectionComponent> mPlayerConnection = context.getEcsEngine().getWorld().getMapper(PlayerConnectionComponent.class);
        if (mPlayerConnection.get(mainPlayerId).uuid != uuid) {
            context.getEcsEngine().getSystem(PhysicPlayerManagerClient.class).createPlayerClient(x, y, uuid);
        }
    }
}

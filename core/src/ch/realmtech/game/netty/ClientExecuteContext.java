package ch.realmtech.game.netty;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.system.PlayerManagerClient;
import ch.realmtech.screen.ScreenType;
import ch.realmtechCommuns.packet.clientPacket.ClientExecute;
import ch.realmtechServer.ecs.component.InfChunkComponent;
import ch.realmtechServer.ecs.component.InfMapComponent;
import ch.realmtechServer.ecs.system.MapManager;
import ch.realmtechServer.ecs.system.MapSystemServer;
import com.artemis.Entity;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;

import java.util.HashMap;
import java.util.UUID;

public class ClientExecuteContext implements ClientExecute {
    private final RealmTech context;

    public ClientExecuteContext(RealmTech context) {
        this.context = context;
    }

    @Override
    public void connexionJoueurReussit(float x, float y, UUID uuid) {
        context.getEcsEngine().getSystem(PlayerManagerClient.class).createPlayerClient(x, y, uuid);
        Gdx.app.postRunnable(() -> context.setScreen(ScreenType.GAME_SCREEN));
    }

    @Override
    public void autreJoueur(float x, float y, UUID uuid) {
        if (context.getEcsEngine() == null) return;
        HashMap<UUID, Integer> players = context.getEcsEngine().getSystem(PlayerManagerClient.class).getPlayers();
        if (!players.containsKey(uuid)) {
            context.getEcsEngine().getSystem(PlayerManagerClient.class).createPlayerClient(x, y, uuid);
        }
        context.getEcsEngine().getSystem(PlayerManagerClient.class).setPlayerPos(x, y, uuid);
    }

    @Override
    public void chunkAMounter(int chunkPosX, int chunkPosY, byte[] chunkBytes) {
        int chunkId = InfChunkComponent.fromByte(context.getEcsEngine().getWorld(), chunkBytes, chunkPosX, chunkPosY);
        Entity infMapEntity = context.getEcsEngine().getSystem(TagManager.class).getEntity("infMap");
        context.getEcsEngine().getSystem(MapManager.class).ajouterChunkAMap(infMapEntity.getComponent(InfMapComponent.class).infChunks, chunkId);
    }

    @Override
    public void chunkADamner(int chunkPosX, int chunkPosY) {
        context.getEcsEngine().getSystem(MapManager.class).damneChunkClient(chunkPosX, chunkPosY);
    }
}

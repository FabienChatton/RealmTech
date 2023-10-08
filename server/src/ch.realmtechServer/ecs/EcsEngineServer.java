package ch.realmtechServer.ecs;

import ch.realmtechServer.ServerContext;
import ch.realmtechServer.ecs.system.*;
import ch.realmtechServer.mod.RealmTechCorePlugin;
import ch.realmtechServer.options.DataCtrl;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EcsEngineServer {
    private final static Logger logger = LoggerFactory.getLogger(EcsEngineServer.class);
    private World world;
    private com.badlogic.gdx.physics.box2d.World physicWorld;
    private final BodyDef bodyDef;
    private final FixtureDef fixtureDef;
    private final List<Runnable> nextTickServer;

    public EcsEngineServer(ServerContext serverContext) throws IOException {
        logger.trace("debut de l'initialisation du ecs");
        Box2D.init();
        DataCtrl dataCtrl = new DataCtrl();
        physicWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0, 0), true);
        bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();
        nextTickServer = Collections.synchronizedList(new ArrayList<>());
        WorldConfiguration worldConfiguration = new WorldConfigurationBuilder()
                .dependsOn(RealmTechCorePlugin.class)

                // manageur
                .with(new TagManager())
                .with(new ItemManager())
                .with(new InventoryManager())
                .with(new PhysiqueContactListenerManager())
                .with(new SaveInfManager())

                // system
                .with(new MapSystemServer())
                .with(new CraftingPlayerSystem())
//                .with(new ItemBeingPickAnimationSystem())
                .with(new PickUpOnGroundItemSystem())
//                .with(new PlayerMouvementSystem())
                .with(new Box2dFrotementSystem())
                // render
//                .with(new PlayerTextureAnimated())
                .with(new UpdateBox2dWithPosition())
//                .with(new CameraFollowPlayerSystem())
//                .with(new MapRendererSystem())
//                .with(new CellBeingMineRenderSystem())
//                .with(new CellHoverEtWailaSystem())
//                .with(new TextureRenderer())

                // ui
//                .with(new PlayerInventorySystem())
//                .with(new ItemBarManager())

                // server
//                .with(new CellBeingMineSystem())
                //.with(new PhysiqueWorldStepSystem())
                .with(new FurnaceSystem())

                .with(new MapManager())
                .with(new PlayerManagerServer())
                .build();
        worldConfiguration.register("serverContext", serverContext);
        worldConfiguration.register("physicWorld", physicWorld);
        worldConfiguration.register(bodyDef);
        worldConfiguration.register(fixtureDef);
        worldConfiguration.register(dataCtrl);
        this.world = new World(worldConfiguration);
        logger.trace("fin de l'initialisation du ecs");
    }

    public void prepareSaveToLoad(String saveName) throws IOException {
        logger.info("Chargement de la carte \"{}\"", saveName);
        int mapId = world.getSystem(SaveInfManager.class).generateOrLoadSave(saveName);
        world.getSystem(TagManager.class).register("infMap", mapId);
        logger.info("La carte \"{}\" a été chargée", saveName);
    }

    public void saveMap() throws IOException {
        int infMap = world.getSystem(TagManager.class).getEntityId("infMap");
        world.getSystem(SaveInfManager.class).saveInfMap(infMap);
        logger.info("carte sauvegarde");
    }

    public World getWorld() {
        return world;
    }
    public void processNextTickRunnable() {
        synchronized (nextTickServer) {
            nextTickServer.forEach(Runnable::run);
            nextTickServer.clear();
        }
    }

    public void nextTickServer(Runnable runnable) {
        nextTickServer.add(runnable);
    }
}

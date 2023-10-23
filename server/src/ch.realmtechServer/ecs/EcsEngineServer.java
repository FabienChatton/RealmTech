package ch.realmtechServer.ecs;

import ch.realmtechServer.ServerContext;
import ch.realmtechServer.ctrl.ItemManager;
import ch.realmtechServer.ecs.system.*;
import ch.realmtechServer.mod.RealmTechCorePlugin;
import ch.realmtechServer.options.DataCtrl;
import ch.realmtechServer.packet.clientPacket.TickBeatPacket;
import com.artemis.Entity;
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
    private ServerContext serverContext;
    private World world;
    private com.badlogic.gdx.physics.box2d.World physicWorld;
    private final BodyDef bodyDef;
    private final FixtureDef fixtureDef;
    private final static List<Runnable> nextTickServerPre = Collections.synchronizedList(new ArrayList<>());
    private final ItemManager itemManager;

    public EcsEngineServer(ServerContext serverContext) throws IOException {
        logger.trace("debut de l'initialisation du ecs");
        this.serverContext = serverContext;
        Box2D.init();
        DataCtrl dataCtrl = new DataCtrl();
        physicWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0, 0), true);
        bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();
        itemManager = new ItemManagerServer();
        WorldConfiguration worldConfiguration = new WorldConfigurationBuilder()
                .dependsOn(RealmTechCorePlugin.class)

                // manageur
                .with(new TagManager())
                .with(itemManager)
                .with(new InventoryManager())
                .with(new PhysiqueContactListenerManager())
                .with(new SaveInfManager())
                .with(new PlayerMouvementSystemServer())

                // system
                .with(new MapSystemServer())
//                .with(new MapUnloadUnusedChunkSystem())
                .with(new CraftingPlayerSystem())
//                .with(new ItemBeingPickAnimationSystem())
                .with(new PickUpOnGroundItemSystem())
//                .with(new PlayerMouvementSystem())
                .with(new Box2dFrotementSystem())
                .with(new ItemOnGroundPosSyncSystem())
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
                .with(new PhysiqueWorldStepSystem())
                //.with(new FurnaceSystem())

                .with(new MapManager())
                .with(new PlayerManagerServer())
                .build();
        worldConfiguration.register("serverContext", serverContext);
        worldConfiguration.register("physicWorld", physicWorld);
        worldConfiguration.register(bodyDef);
        worldConfiguration.register(fixtureDef);
        worldConfiguration.register(dataCtrl);
        worldConfiguration.register(itemManager);
        this.world = new World(worldConfiguration);
        physicWorld.setContactListener(world.getSystem(PhysiqueContactListenerManager.class));
        logger.trace("fin de l'initialisation du ecs");
    }

    public void process(float deltaTime) {
        long t1 = System.currentTimeMillis();
        List<Runnable> copyPre;
        synchronized (nextTickServerPre) {
            copyPre = List.copyOf(nextTickServerPre);
            nextTickServerPre.clear();
        }
        processNextTickRunnable(copyPre);
        world.setDelta(deltaTime);
        world.process();

        long t2 = System.currentTimeMillis();
        serverContext.getServerHandler().broadCastPacket(new TickBeatPacket((t2 - t1) / 1000f));
        // pour débug
//        IntBag items = world.getAspectSubscriptionManager().get(Aspect.all(ItemComponent.class)).getEntities();
//        ComponentMapper<PositionComponent> mPos = world.getMapper(PositionComponent.class);
//        int[] itemsData = items.getData();
//        for (int i = 0; i < items.size(); i++) {
//            PositionComponent positionComponent = mPos.get(itemsData[i]);
//            logger.info(positionComponent.x + "," + positionComponent.y);
//        }
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

    public void processNextTickRunnable(List<Runnable> runnables) {
        try {
            runnables.forEach(Runnable::run);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void nextTickServer(Runnable runnable) {
        nextTickServerPre.add(runnable);
    }

    public Entity getMapEntity() {
        return world.getSystem(TagManager.class).getEntity("infMap");
    }
}

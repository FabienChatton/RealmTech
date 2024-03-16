package ch.realmtech.core.game.ecs;

import box2dLight.RayHandler;
import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.console.CommandClientExecute;
import ch.realmtech.core.game.ecs.plugin.ExecuteOnContextClient;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.core.game.ecs.plugin.strategy.InGameSystemOnInventoryOpen;
import ch.realmtech.core.game.ecs.plugin.strategy.SystemDisableOnInventoryOpen;
import ch.realmtech.core.game.ecs.plugin.strategy.SystemEnableOnInventoryOpen;
import ch.realmtech.core.game.ecs.plugin.strategy.TickEmulationInvocationStrategy;
import ch.realmtech.core.game.ecs.system.*;
import ch.realmtech.core.game.monitoring.ServerTickBeatMonitoring;
import ch.realmtech.core.game.netty.ClientConnexion;
import ch.realmtech.server.ecs.GetWorld;
import ch.realmtech.server.mod.RealmTechCorePlugin;
import ch.realmtech.server.serialize.SerializerController;
import com.artemis.*;
import com.artemis.managers.TagManager;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ECSEngine implements Disposable, GetWorld {
    private final static Logger logger = LoggerFactory.getLogger(ECSEngine.class);

    private final RealmTech context;

    private final BodyDef bodyDef;
    private final FixtureDef fixtureDef;
    private final World world;
    public final com.badlogic.gdx.physics.box2d.World physicWorld;
    private final InGameSystemOnInventoryOpen systemDisableOnPlayerInventoryOpen;
    private final SystemEnableOnInventoryOpen systemEnableOnPlayerInventoryOpen;
    private final ClientConnexion clientConnexion;
    private final List<Runnable> nextFrameRunnable;
    public final ServerTickBeatMonitoring serverTickBeatMonitoring;
    private final SystemsAdminClient systemAdminClient;
    private final CommandClientExecute commandClientExecute;
    private final SerializerController serializerController;
    private final RayHandler rayHandler;
    private final ExecuteOnContextClient executeOnContextClient;
    private final TickEmulationInvocationStrategy tickEmulationInvocationStrategy;

    public ECSEngine(final RealmTech context, ClientConnexion clientConnexion) throws Exception {
        this.context = context;
        this.clientConnexion = clientConnexion;
        tickEmulationInvocationStrategy = new TickEmulationInvocationStrategy();
        physicWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0, 0), true);
        rayHandler = new RayHandler(physicWorld);
        bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();
        nextFrameRunnable = Collections.synchronizedList(new ArrayList<>());
        commandClientExecute = new CommandClientExecute(context);
        serverTickBeatMonitoring = new ServerTickBeatMonitoring();
        serializerController = new SerializerController();
        systemAdminClient = new SystemsAdminClient();
        executeOnContextClient = new ExecuteOnContextClient();
        WorldConfiguration worldConfiguration = new WorldConfigurationBuilder()
                .dependsOn(RealmTechCorePlugin.class)
                .with(systemAdminClient)

                // server
                //.withTick(new FurnaceSystem())
                .build();
        systemDisableOnPlayerInventoryOpen = new SystemDisableOnInventoryOpen(
                List.of(
                        PlayerInputSystem.class,
                        CellBeingMineSystem.class,
                        CellBeingMineRenderSystem.class,
                        WailaSystem.class,
                        CellHoverSystem.class
                )
        );
        systemEnableOnPlayerInventoryOpen = new SystemEnableOnInventoryOpen(
                List.of(
                        InventoryNeiSystem.class
                )
        );
        worldConfiguration.register("physicWorld", physicWorld);
        worldConfiguration.register("gameStage", context.getGameStage());
        worldConfiguration.register("context", context);
        worldConfiguration.register("gameCamera", context.getGameStage().getCamera());
        worldConfiguration.register("inGameSystemOnInventoryOpen", systemDisableOnPlayerInventoryOpen);
        worldConfiguration.register("inGameSystemOnInventoryOpenEnable", systemEnableOnPlayerInventoryOpen);
        worldConfiguration.register("uiStage", context.getUiStage());
        worldConfiguration.register(context.getTextureAtlas());
        worldConfiguration.register(context.getSkin());
        worldConfiguration.register(context.getSoundManager());
        worldConfiguration.register(context.getInputManager());
        worldConfiguration.register(bodyDef);
        worldConfiguration.register(fixtureDef);
        worldConfiguration.register("itemManager", systemAdminClient.getItemManagerClient());
        worldConfiguration.register(systemAdminClient);
        worldConfiguration.register("systemsAdmin", systemAdminClient);
        worldConfiguration.register(serializerController);
        worldConfiguration.register(rayHandler);
        worldConfiguration.register("executeOnContext", executeOnContextClient);
        worldConfiguration.register("rootRegistry", context.getRootRegistry());

        worldConfiguration.setInvocationStrategy(tickEmulationInvocationStrategy);
        world = new World(worldConfiguration);

        executeOnContextClient.initialize(world);
        serializerController.initialize(world);
        systemEnableOnPlayerInventoryOpen.initialize(world);
    }

    public void process(float delta) {
        List<Runnable> copyNextFrameRunnable;
        synchronized (nextFrameRunnable) {
            copyNextFrameRunnable = List.copyOf(nextFrameRunnable);
            nextFrameRunnable.clear();
        }
        if (context.getEcsEngine() == null) return;
        copyNextFrameRunnable.forEach(Runnable::run);
        if (context.getEcsEngine() == null) return;
        world.setDelta(delta);
        world.process();
    }

    @Override
    public void dispose() {
        world.dispose();
        physicWorld.dispose();
        try {
            clientConnexion.close();
            serverTickBeatMonitoring.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        System.gc();
    }

    public void clearAllEntity() {
        final IntBag entities = world.getAspectSubscriptionManager().get(Aspect.all()).getEntities();
        for (int entity : entities.getData()) {
            world.delete(entity);
        }
    }

    @Override
    public World getWorld() {
        return world;
    }

    public int getMapId() {
        return world.getSystem(TagManager.class).getEntityId("infMap");
    }

    public Entity getMapEntity() {
        return world.getSystem(TagManager.class).getEntity("infMap");
    }


//    public void dropCurentPlayerItem() {
//        ItemComponent itemComponent = world.getSystem(ItemBarManager.class).getSelectItemComponent();
//        if (itemComponent != null) {
//            world.getSystem(InventoryManager.class).removeOneItem(world.getSystem(ItemBarManager.class).getSelectStack());
//            Vector3 gameCoo = context.getGameStage().getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
//            world.getSystem(ItemManagerClient.class).newItemOnGround(gameCoo.x, gameCoo.y, itemComponent.itemRegisterEntry);
//            context.getSoundManager().playItemDrop();
//        }
//    }

    public <T extends BaseSystem> T getSystem(Class<T> type) {
        return world.getSystem(type);
    }

    public InGameSystemOnInventoryOpen getSystemDisableOnPlayerInventoryOpen() {
        return systemDisableOnPlayerInventoryOpen;
    }

    public BodyDef getBodyDef() {
        return bodyDef;
    }

    public FixtureDef getFixtureDef() {
        return fixtureDef;
    }

    public ClientConnexion getClientConnexion() {
        return clientConnexion;
    }
    public Vector2 getGameCoordinate(Vector2 screenCoordinate) {
        Vector3 unproject = context.getGameStage().getCamera().unproject(new Vector3(screenCoordinate, 0));
        return new Vector2(unproject.x, unproject.y);
    }
    public void nextFrame(Runnable runnable) {
        synchronized (nextFrameRunnable) {
            nextFrameRunnable.add(runnable);
        }
    }

    public CommandClientExecute getCommandClientExecute() {
        return commandClientExecute;
    }

    public SerializerController getSerializerController() {
        return serializerController;
    }


    public SystemsAdminClient getSystemsAdminClient() {
        return systemAdminClient;
    }

    public TickEmulationInvocationStrategy getTickEmulationInvocationStrategy() {
        return tickEmulationInvocationStrategy;
    }
}

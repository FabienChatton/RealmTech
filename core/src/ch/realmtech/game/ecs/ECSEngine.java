package ch.realmtech.game.ecs;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.system.*;
import ch.realmtech.game.monitoring.ServerTickBeatMonitoring;
import ch.realmtech.game.netty.RealmTechClientConnexionHandler;
import ch.realmtech.strategy.DefaultInGameSystemOnInventoryOpen;
import ch.realmtech.strategy.InGameSystemOnInventoryOpen;
import ch.realmtech.strategy.ServerInvocationStrategy;
import ch.realmtech.strategy.WorldConfigurationBuilderServer;
import ch.realmtechServer.ecs.system.*;
import ch.realmtechServer.mod.PlayerFootStepSound;
import ch.realmtechServer.mod.RealmTechCorePlugin;
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

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ECSEngine implements Disposable {
    private final static Logger logger = LoggerFactory.getLogger(ECSEngine.class);

    private final RealmTech context;

    private final BodyDef bodyDef;
    private final FixtureDef fixtureDef;
    private final World world;
    public final com.badlogic.gdx.physics.box2d.World physicWorld;
    private final InGameSystemOnInventoryOpen inGameSystemOnInventoryOpen;
    private final ServerInvocationStrategy serverInvocationStrategy;
    private final RealmTechClientConnexionHandler connexionHandler;
    private final List<Runnable> nextFrameRunnable;
    public final ServerTickBeatMonitoring serverTickBeatMonitoring;

    public ECSEngine(final RealmTech context, RealmTechClientConnexionHandler connexionHandler) {
        this.context = context;
        this.connexionHandler = connexionHandler;
        this.serverInvocationStrategy = new ServerInvocationStrategy();
        physicWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0, 0), true);
        bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();
        nextFrameRunnable = Collections.synchronizedList(new ArrayList<>());
        serverTickBeatMonitoring = new ServerTickBeatMonitoring();
        WorldConfiguration worldConfiguration = new WorldConfigurationBuilderServer(serverInvocationStrategy)
                .dependsOn(RealmTechCorePlugin.class)
                .withFrame(new PlayerManagerClient())

                // manageur
                .withFrame(new TagManager())
                .withFrame(new ItemManagerClient())
                .withFrame(new InventoryManager())
                .withFrame(new PhysiqueContactListenerManager())
                .withFrame(new SaveInfManager())
                .withFrame(new MapManager())

                // system
                .withFrame(new PlayerInputSystem())
                .withFrame(new PlayerMouvementTextureSystem())
//                .withClient(new MapSystem())
                .withFrame(new CraftingPlayerSystem())
                //.withFrame(new ItemBeingPickAnimationSystem())
                //.withFrame(new PickUpOnGroundItemSystem())
                .withFrame(new PlayerMouvementSystem())
                .withFrame(new Box2dFrotementSystem())
                // render
                .withFrame(new PlayerTextureAnimated())
                .withFrame(new UpdateBox2dWithPosition())
                .withFrame(new CameraFollowPlayerSystem())
                .withFrame(new MapRendererSystem())
                .withFrame(new CellBeingMineRenderSystem())
//                .withClient(new CellHoverEtWailaSystem())
                .withFrame(new TextureRenderer())

                // ui
                .withFrame(new PlayerInventorySystem())
//                .withClient(new ItemBarManager())

                // server
                .withTick(new CellBeingMineSystem())
                .withTick(new PhysiqueWorldStepSystem())
                //.withTick(new FurnaceSystem())
                .build();
        inGameSystemOnInventoryOpen = new DefaultInGameSystemOnInventoryOpen(
                new ArrayList<>(List.of(
                        PlayerInputSystem.class,
                        PhysiqueWorldStepSystem.class,
                        CellBeingMineSystem.class,
                        CellBeingMineRenderSystem.class
//                        CellHoverEtWailaSystem.class
                ))
        );
        worldConfiguration.register("physicWorld", physicWorld);
        worldConfiguration.register("gameStage", context.getGameStage());
        worldConfiguration.register("context", context);
        worldConfiguration.register("gameCamera", context.getGameStage().getCamera());
        worldConfiguration.register("inGameSystemOnInventoryOpen", inGameSystemOnInventoryOpen);
        worldConfiguration.register("uiStage", context.getUiStage());
        worldConfiguration.register(context.getTextureAtlas());
        worldConfiguration.register(context.getSkin());
        worldConfiguration.register(context.getSoundManager());
        worldConfiguration.register(context.getInputManager());
        worldConfiguration.register(context.getDataCtrl());
        worldConfiguration.register(bodyDef);
        worldConfiguration.register(fixtureDef);

        worldConfiguration.setInvocationStrategy(serverInvocationStrategy);
        world = new World(worldConfiguration);
        physicWorld.setContactListener(world.getSystem(PhysiqueContactListenerManager.class));

    }

    public void process(float delta) {
        world.setDelta(delta);
        world.process();
        synchronized (nextFrameRunnable) {
            nextFrameRunnable.forEach(Runnable::run);
            nextFrameRunnable.clear();
        }
    }

    @Override
    public void dispose() {
        world.dispose();
        physicWorld.dispose();
        try {
            connexionHandler.close();
        } catch (IOException e) {
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

    public int getPlayerId() {
        return world.getSystem(TagManager.class).getEntityId(PlayerManagerClient.MAIN_PLAYER_TAG);
    }

    public Entity getPlayerEntity() {
        return world.getSystem(TagManager.class).getEntity(PlayerManagerClient.MAIN_PLAYER_TAG);
    }


    public World getWorld() {
        return world;
    }

    public void togglePlayerInventoryWindow() {
        world.getSystem(PlayerInventorySystem.class).toggleInventoryWindow(world.getSystem(PlayerInventorySystem.class).getDisplayInventoryPlayer());
    }

    public int getMapId() {
        return world.getSystem(TagManager.class).getEntityId("infMap");
    }

    public Entity getMapEntity() {
        return world.getSystem(TagManager.class).getEntity("infMap");
    }


    public void playFootStep(PlayerFootStepSound footStep) {
        context.getSoundManager().playFootStep(footStep.playerFootStepSound(), footStep.volume());
    }

    public int readSavedInfChunk(int chunkX, int chunkY, String saveName) throws IOException {
        return world.getSystem(SaveInfManager.class).readSavedInfChunk(chunkX, chunkY, saveName);
    }

    public void saveInfChunk(int infChunkId, Path rootSaveDirPath) throws IOException {
        world.getSystem(SaveInfManager.class).saveInfChunk(infChunkId, rootSaveDirPath);
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

    public InGameSystemOnInventoryOpen getInGameSystemOnInventoryOpen() {
        return inGameSystemOnInventoryOpen;
    }

    public BodyDef getBodyDef() {
        return bodyDef;
    }

    public FixtureDef getFixtureDef() {
        return fixtureDef;
    }

    public ServerInvocationStrategy getServerInvocationStrategy() {
        return serverInvocationStrategy;
    }
    public RealmTechClientConnexionHandler getConnexionHandler() {
        return connexionHandler;
    }
    public Vector2 getGameCoordinate(Vector2 screenCoordinate) {
        Vector3 unproject = context.getGameStage().getCamera().unproject(new Vector3(screenCoordinate, 0));
        return new Vector2(unproject.x, unproject.y);
    }
    public int getTopCell(int chunk, Vector2 screenCoordinate) {
        Vector2 gameCoordinate = getGameCoordinate(screenCoordinate);
        return getWorld().getSystem(MapManager.class).getTopCell(chunk, MapManager.getInnerChunk(gameCoordinate.x), MapManager.getInnerChunk(gameCoordinate.y));
    }
    public void nextFrame(Runnable runnable) {
        nextFrameRunnable.add(runnable);
    }
}

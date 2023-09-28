package ch.realmtech.game.ecs;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.system.*;
import ch.realmtech.game.netty.RealmtechClientConnexionHandler;
import ch.realmtech.strategy.DefaultInGameSystemOnInventoryOpen;
import ch.realmtech.strategy.InGameSystemOnInventoryOpen;
import ch.realmtech.strategy.ServerInvocationStrategy;
import ch.realmtech.strategy.WorldConfigurationBuilderServer;
import ch.realmtechServer.ecs.component.*;
import ch.realmtechServer.ecs.system.*;
import ch.realmtechServer.mod.PlayerFootStepSound;
import ch.realmtechServer.mod.RealmTechCorePlugin;
import com.artemis.*;
import com.artemis.managers.TagManager;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.io.IOException;
import java.nio.file.Path;

public final class ECSEngine implements Disposable {
    private final static String TAG = ECSEngine.class.getSimpleName();

    private final RealmTech context;

    private final BodyDef bodyDef;
    private final FixtureDef fixtureDef;
    private final World world;
    public final com.badlogic.gdx.physics.box2d.World physicWorld;
    private final InGameSystemOnInventoryOpen inGameSystemOnInventoryOpen;
    private final ServerInvocationStrategy serverInvocationStrategy;
    private final RealmtechClientConnexionHandler connexionHandler;

    public ECSEngine(final RealmTech context, RealmtechClientConnexionHandler connexionHandler) {
        this.context = context;
        this.connexionHandler = connexionHandler;
        this.serverInvocationStrategy = new ServerInvocationStrategy();
        physicWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0, 0), true);
        bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();
        WorldConfiguration worldConfiguration = new WorldConfigurationBuilderServer(serverInvocationStrategy)
                .dependsOn(RealmTechCorePlugin.class)
                .withClient(new PlayerManagerClient())

                // manageur
                .withClient(new TagManager())
                .withClient(new ItemManager())
                .withClient(new InventoryManager())
                .withClient(new PhysiqueContactListenerManager())
                .withClient(new SaveInfManager())
                .withClient(new MapManager())

                // system
                .withClient(new PlayerInputSystem())
//                .withClient(new MapSystem())
                .withClient(new CraftingPlayerSystem())
                .withClient(new ItemBeingPickAnimationSystem())
                .withClient(new PickUpOnGroundItemSystem())
                .withClient(new PlayerMouvementSystem())
                .withClient(new Box2dFrotementSystem())
                // render
                .withClient(new PlayerTextureAnimated())
                .withClient(new UpdateBox2dWithPosition())
                .withClient(new CameraFollowPlayerSystem())
                .withClient(new ChunkRendererSystem())
                .withClient(new CellBeingMineRenderSystem())
//                .withClient(new CellHoverEtWailaSystem())
                .withClient(new TextureRenderer())

                // ui
                .withClient(new PlayerInventorySystem())
//                .withClient(new ItemBarManager())

                // server
                .withServer(new CellBeingMineSystem())
                .withServer(new PhysiqueWorldStepSystem())
                .withServer(new FurnaceSystem())
                .build();
        inGameSystemOnInventoryOpen = new DefaultInGameSystemOnInventoryOpen(
                PlayerInputSystem.class,
                PhysiqueWorldStepSystem.class,
                CellBeingMineSystem.class,
                CellBeingMineRenderSystem.class,
                CellHoverEtWailaSystem.class
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
    }

    @Deprecated
    public int createPlayer(float x, float y) {
        return 0;
    }

    @Override
    public void dispose() {
        world.dispose();
        physicWorld.dispose();
        try {
            connexionHandler.close();
        } catch (IOException e) {
            Gdx.app.error(TAG, e.getMessage(), e);
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
        return world.getSystem(TagManager.class).getEntityId(PlayerComponent.TAG);
    }

    public Entity getPlayerEntity() {
        return world.getSystem(TagManager.class).getEntity(PlayerComponent.TAG);
    }


    public World getWorld() {
        return world;
    }

    public ItemManager getItemManager() {
        return world.getSystem(ItemManager.class);
    }

    public void togglePlayerInventoryWindow() {
        world.getSystem(PlayerInventorySystem.class).toggleInventoryWindow(world.getSystem(PlayerInventorySystem.class).getDisplayInventoryPlayer());
    }

    public void loadInfFile(String saveName) throws IOException {
        int mapId = world.getSystem(SaveInfManager.class).readInfMap(saveName);
        mapRequirementBeforeShow(mapId);
    }

    public void generateNewSave(String name) throws IOException {
        int mapId = world.getSystem(SaveInfManager.class).generateNewSave(name);
        InfMapComponent infMapComponent = world.getMapper(InfMapComponent.class).get(mapId);
        int chunkId = world.getSystem(MapManager.class).generateNewChunk(mapId, 0, 0);
        infMapComponent.infChunks = new int[]{chunkId};
        mapRequirementBeforeShow(mapId);
        world.getSystem(SaveInfManager.class).saveInfMap(mapId);
    }

    public void mapRequirementBeforeShow(int mapId) {
        world.getSystem(TagManager.class).register("infMap", mapId);
        InfMetaDonneesComponent metaDonnesComponent = world.getMapper(InfMapComponent.class).get(mapId).getMetaDonnesComponent(world);
        int playerId = createPlayer(metaDonnesComponent.playerPositionX, metaDonnesComponent.playerPositionY);
        Entity playerEntity = world.getEntity(playerId);
        PositionComponent playerPositionComponent = playerEntity.getComponent(PositionComponent.class);
        //connexionHandler.sendAndFlushPacketToServer(new PlayerconnexionPacket(playerPositionComponent.x, playerPositionComponent.y));

    }

    public void saveInfMap() throws IOException {
        int mapId = getMapId();
        world.getSystem(SaveInfManager.class).saveInfMap(mapId);
    }

    public int getMapId() {
        return world.getSystem(TagManager.class).getEntityId("infMap");
    }

    public Entity getMapEntity() {
        return world.getSystem(TagManager.class).getEntity("infMap");
    }


    /**
     * Donne l'id de la cellule via ses cordonnées dans le monde.
     *
     * @return l'id de la cellule ou -1 si pas trouvé.
     */
    public int getCell(float worldPosX, float worldPosY, byte layer) {
        return world.getSystem(MapSystemServer.class).getCell(world.getMapper(InfMapComponent.class).get(world.getSystem(TagManager.class).getEntityId("infMap")).infChunks, (int) worldPosX, (int) worldPosY, layer);
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

    public void savePlayerInventory() throws IOException {
        InventoryComponent playerInventory = getPlayerEntity().getComponent(InventoryComponent.class);
        int mapId = getMapId();
        world.getSystem(SaveInfManager.class).savePlayerInventory(playerInventory, mapId);
    }

    public void dropCurentPlayerItem() {
        ItemComponent itemComponent = world.getSystem(ItemBarManager.class).getSelectItemComponent();
        if (itemComponent != null) {
            world.getSystem(InventoryManager.class).removeOneItem(world.getSystem(ItemBarManager.class).getSelectStack());
            Vector3 gameCoo = context.getGameStage().getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            world.getSystem(ItemManager.class).newItemOnGround(gameCoo.x, gameCoo.y, itemComponent.itemRegisterEntry);
            context.getSoundManager().playItemDrop();
        }
    }

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
    public RealmtechClientConnexionHandler getConnexionHandler() {
        return connexionHandler;
    }
}

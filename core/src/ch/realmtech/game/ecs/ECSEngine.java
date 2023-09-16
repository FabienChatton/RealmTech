package ch.realmtech.game.ecs;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.system.*;
import ch.realmtech.game.netty.RealmtechClientConnectionHandler;
import ch.realmtech.strategy.DefaultInGameSystemOnInventoryOpen;
import ch.realmtech.strategy.InGameSystemOnInventoryOpen;
import ch.realmtech.strategy.ServerInvocationStrategy;
import ch.realmtech.strategy.WorldConfigurationBuilderServer;
import ch.realmtechCommuns.ecs.component.*;
import ch.realmtechCommuns.ecs.system.*;
import ch.realmtechCommuns.mod.PlayerFootStepSound;
import ch.realmtechCommuns.mod.RealmTechCorePlugin;
import com.artemis.*;
import com.artemis.managers.TagManager;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
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
    private final RealmtechClientConnectionHandler connectionHandler;

    public ECSEngine(final RealmTech context, RealmtechClientConnectionHandler connectionHandler) {
        this.context = context;
        this.connectionHandler = connectionHandler;
        this.serverInvocationStrategy = new ServerInvocationStrategy();
        physicWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0, 0), true);
        bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();
        WorldConfiguration worldConfiguration = new WorldConfigurationBuilderServer(serverInvocationStrategy)
                .dependsOn(RealmTechCorePlugin.class)
                // manageur
                .withClient(new TagManager())
                .withClient(new ItemManager())
                .withClient(new InventoryManager())
                .withClient(new PhysiqueContactListenerManager())
                .withClient(new SaveInfManager())

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
                .withClient(new UpdateBox2dWithTextureSystem())
                .withClient(new CameraFollowPlayerSystem())
//                .withClient(new MapRendererSystem())
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

    public ECSEngine(final RealmTech context) throws IOException {
        this(context, new RealmtechClientConnectionHandler(context));
    }

    public void process(float delta) {
        world.setDelta(delta);
        world.process();
    }

    public int createPlayer(float x, float y) {
//        final float playerWorldWith = 0.9f;
//        final float playerWorldHigh = 0.9f;
//        if (world.getSystem(TagManager.class).getEntity(PlayerComponent.TAG) != null) {
//            int playerId = world.getSystem(TagManager.class).getEntityId(PlayerComponent.TAG);
//            world.getSystem(TagManager.class).unregister(PlayerComponent.TAG);
//            physicWorld.destroyBody(world.edit(playerId).create(Box2dComponent.class).body);
//            world.delete(playerId);
//        }
//        int playerId = world.create();
//        world.getSystem(TagManager.class).register(PlayerComponent.TAG, playerId);
//
//        PhysiqueWorldHelper.resetBodyDef(bodyDef);
//        PhysiqueWorldHelper.resetFixtureDef(fixtureDef);
//        bodyDef.type = BodyDef.BodyType.DynamicBody;
//        Body bodyPlayer = ajouteEntitePhysique(bodyDef);
//        bodyPlayer.setUserData(playerId);
//        PolygonShape playerShape = new PolygonShape();
//        playerShape.setAsBox(playerWorldWith / 2f, playerWorldHigh / 2f);
//        fixtureDef.shape = playerShape;
//        fixtureDef.filter.categoryBits = PhysiqueWorldHelper.BIT_PLAYER;
//        fixtureDef.filter.maskBits = PhysiqueWorldHelper.BIT_WORLD | PhysiqueWorldHelper.BIT_GAME_OBJECT;
//        bodyPlayer.createFixture(fixtureDef);
//
//        playerShape.dispose();
//
//        // box2d component
//        Box2dComponent box2dComponent = world.edit(playerId).create(Box2dComponent.class);
//        box2dComponent.set(playerWorldWith, playerWorldHigh, bodyPlayer);
//
//        // player component
//        PlayerComponent playerComponent = world.edit(playerId).create(PlayerComponent.class);
//
//        // movement component
//        MovementComponent movementComponent = world.edit(playerId).create(MovementComponent.class);
//        movementComponent.set(10, 10);
//
//        // position component
//        PositionComponent positionComponent = world.edit(playerId).create(PositionComponent.class);
//        positionComponent.set(box2dComponent, x, y);
//
//        // inventory component
//        InventoryComponent inventoryComponent = world.edit(playerId).create(InventoryComponent.class);
//        inventoryComponent.set(InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW, InventoryComponent.DEFAULT_NUMBER_OF_ROW, InventoryComponent.DEFAULT_BACKGROUND_TEXTURE_NAME);
//        try {
//            inventoryComponent.inventory = world.getSystem(SaveInfManager.class).getPlayerSaveInventory(getMapId());
//        } catch (FileNotFoundException ignored) {
//
//        } catch (IOException e) {
//            Gdx.app.error(TAG, "l'inventaire du joueur n'a pas pu être récupérer de la présente sauvegarde", e);
//        }
//
//        // pick up item component
//        PickerGroundItemComponent pickerGroundItemComponent = world.edit(playerId).create(PickerGroundItemComponent.class);
//        pickerGroundItemComponent.set(10);
//
////        // texture component
////        TextureComponent textureComponent = world.edit(playerId).create(TextureComponent.class);
////        final TextureRegion texture = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu");
////        textureComponent.set(texture);
//
//        // animation
//        TextureComponent textureComponent = world.edit(playerId).create(TextureComponent.class);
//        textureComponent.scale = 0.05f;
//        TextureAtlas.AtlasRegion textureFront0 = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu-front-0");
//        TextureAtlas.AtlasRegion textureFront1 = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu-front-1");
//        TextureAtlas.AtlasRegion textureFront2 = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu-front-2");
//        playerComponent.animationFront = new TextureRegion[]{textureFront0, textureFront1, textureFront2};
//        TextureAtlas.AtlasRegion textureLeft0 = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu-left-0");
//        TextureAtlas.AtlasRegion textureLeft1 = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu-left-1");
//        TextureAtlas.AtlasRegion textureLeft2 = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu-left-2");
//        playerComponent.animationLeft = new TextureRegion[]{textureLeft0, textureLeft1, textureLeft2};
//        TextureAtlas.AtlasRegion textureBack0 = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu-back-0");
//        TextureAtlas.AtlasRegion textureBack1 = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu-back-1");
//        TextureAtlas.AtlasRegion textureBack2 = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu-back-2");
//        playerComponent.animationBack = new TextureRegion[]{textureBack0, textureBack1, textureBack2};
//        TextureAtlas.AtlasRegion textureRight0 = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu-right-0");
//        TextureAtlas.AtlasRegion textureRight1 = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu-right-1");
//        TextureAtlas.AtlasRegion textureRight2 = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu-right-2");
//        playerComponent.animationRight = new TextureRegion[]{textureRight0, textureRight1, textureRight2};
//
//        // default crafting table
//        int defaultCraftingTable = world.create();
//        int defaultResultInventory = world.create();
//        world.edit(playerId).create(CraftingTableComponent.class).set(defaultCraftingTable, defaultResultInventory, CraftStrategy.craftingStrategyCraftingTable());
//        world.edit(defaultCraftingTable).create(InventoryComponent.class).set(2, 2, InventoryComponent.DEFAULT_BACKGROUND_TEXTURE_NAME);
//        world.edit(defaultResultInventory).create(InventoryComponent.class).set(1, 1, InventoryComponent.DEFAULT_BACKGROUND_TEXTURE_NAME);
//        world.edit(defaultCraftingTable).create(CraftingComponent.class).set(RealmTechCoreMod.CRAFT, defaultResultInventory);
//        return playerId;
        return 0;
    }

    @Override
    public void dispose() {
        world.dispose();
        physicWorld.dispose();
        try {
            connectionHandler.close();
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

    public void loadInfFile(Path savePath) throws IOException {
        int mapId = world.getSystem(SaveInfManager.class).readInfMap(savePath);
        mapRequirementBeforeShow(mapId);
    }

    public void generateNewSave(String name) throws IOException {
        int mapId = world.getSystem(SaveInfManager.class).generateNewSave(name);
        InfMapComponent infMapComponent = world.getMapper(InfMapComponent.class).get(mapId);
        int chunkId = world.getSystem(MapSystem.class).generateNewChunk(mapId, 0, 0);
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
        //connectionHandler.sendAndFlushPacketToServer(new PlayerConnectionPacket(playerPositionComponent.x, playerPositionComponent.y));

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
        return world.getSystem(MapSystem.class).getCell(world.getMapper(InfMapComponent.class).get(world.getSystem(TagManager.class).getEntityId("infMap")).infChunks, (int) worldPosX, (int) worldPosY, layer);
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
}

package ch.realmtech.game.ecs;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.*;
import ch.realmtech.game.ecs.system.*;
import ch.realmtech.game.mod.PlayerFootStepSound;
import ch.realmtech.game.mod.RealmTechCoreMod;
import ch.realmtech.game.mod.RealmTechCorePlugin;
import com.artemis.*;
import com.artemis.managers.TagManager;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

public final class ECSEngine {
    private final static String TAG = ECSEngine.class.getSimpleName();

    private final static byte BIT_PLAYER = 1 << 1;
    private final static byte BIT_WORLD = 1 << 2;
    private final static byte BIT_GAME_OBJECT = 1 << 3;
    private final RealmTech context;

    private final BodyDef bodyDef;
    private final FixtureDef fixtureDef;
    private final World world;
    public final com.badlogic.gdx.physics.box2d.World physicWorld;

    public ECSEngine(final RealmTech context) {
        this.context = context;
        physicWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0, 0), true);
        WorldConfiguration worldConfiguration = new WorldConfigurationBuilder()
                .dependsOn(RealmTechCorePlugin.class)
                // manageur
                .with(new TagManager())
                .with(new ItemManager())
                .with(new InventoryManager())
                .with(new PhysiqueContactListenerManager())
                .with(new SaveInfManager())

                // system
                .with(new MapSystem())
                .with(new CraftingPlayerSystem())
                .with(new ItemBeingPickAnimationSystem())
                .with(new SoundManager())
                .with(new PickUpOnGroundItemSystem())
                .with(new PlayerMouvementSystem())
                .with(new PhysiqueWorldStepSystem())
                // render
                .with(new PlayerTextureAnimated())
                .with(new UpdateBox2dWithTextureSystem())
                .with(new CameraFollowPlayerSystem())
                .with(new MapRendererSystem())
                .with(new TextureRenderer())

                // ui
                .with(new PlayerInventorySystem())
                .with(new ItemBarManager())
                .build();
        worldConfiguration.register("physicWorld", physicWorld);
        worldConfiguration.register("gameStage", context.getGameStage());
        worldConfiguration.register("context", context);
        worldConfiguration.register("gameCamera", context.getGameStage().getCamera());
        worldConfiguration.register(context.getTextureAtlas());
        world = new World(worldConfiguration);
        physicWorld.setContactListener(world.getSystem(PhysiqueContactListenerManager.class));
        bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();
    }

    public void process(float delta) {
        world.setDelta(delta);
        world.process();
    }

    private void resetBodyDef() {
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0, 0);
        bodyDef.angle = 0;
        bodyDef.linearVelocity.set(0, 0);
        bodyDef.angularVelocity = 0;
        bodyDef.linearDamping = 0;
        bodyDef.angularDamping = 0;
        bodyDef.allowSleep = true;
        bodyDef.awake = true;
        bodyDef.fixedRotation = false;
        bodyDef.bullet = false;
        bodyDef.active = true;
        bodyDef.gravityScale = 1;
    }

    private void resetFixtureDef() {
        fixtureDef.shape = null;
        fixtureDef.friction = 0.2f;
        fixtureDef.restitution = 0;
        fixtureDef.density = 0;
        fixtureDef.isSensor = false;
        fixtureDef.filter.categoryBits = 0;
        fixtureDef.filter.maskBits = 0;
    }

    /**
     *
     * @return l'id du player
     */
    public int createPlayer() {
        final int playerWorldWith = 1;
        final int playerWorldHigh = 1;
        if (world.getSystem(TagManager.class).getEntity(PlayerComponent.TAG) != null) {
            int playerId = world.getSystem(TagManager.class).getEntityId(PlayerComponent.TAG);
            world.getSystem(TagManager.class).unregister(PlayerComponent.TAG);
            physicWorld.destroyBody(world.edit(playerId).create(Box2dComponent.class).body);
            world.delete(playerId);
        }
        int playerId = world.create();
        world.getSystem(TagManager.class).register(PlayerComponent.TAG, playerId);

        resetBodyDef();
        resetFixtureDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Body bodyPlayer = physicWorld.createBody(bodyDef);
        bodyPlayer.setUserData(playerId);
        PolygonShape playerShape = new PolygonShape();
        playerShape.setAsBox(playerWorldWith / 2f, playerWorldHigh / 2f);
        fixtureDef.shape = playerShape;
        fixtureDef.filter.categoryBits = BIT_PLAYER;
        fixtureDef.filter.maskBits = BIT_WORLD | BIT_GAME_OBJECT;
        bodyPlayer.createFixture(fixtureDef);

        playerShape.dispose();

        // box2d component
        Box2dComponent box2dComponent = world.edit(playerId).create(Box2dComponent.class);
        box2dComponent.set(playerWorldWith, playerWorldHigh, bodyPlayer);

        // player component
        PlayerComponent playerComponent = world.edit(playerId).create(PlayerComponent.class);

        // movement component
        MovementComponent movementComponent = world.edit(playerId).create(MovementComponent.class);
        movementComponent.set(10, 10);

        // position component
        PositionComponent positionComponent = world.edit(playerId).create(PositionComponent.class);

        // inventory component
        InventoryComponent inventoryComponent = world.edit(playerId).create(InventoryComponent.class);
        inventoryComponent.set(InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW, InventoryComponent.DEFAULT_NUMBER_OF_ROW, "water-01");
        try {
            inventoryComponent.inventory = world.getSystem(SaveInfManager.class).getPlayerSaveInventory(getMapId());
        } catch (FileNotFoundException ignored) {

        } catch (IOException e) {
            Gdx.app.error(TAG, "l'inventaire du joueur n'a pas pu être récupérer de la présente sauvegarde", e);
        }

        // pick up item component
        PickerGroundItemComponent pickerGroundItemComponent = world.edit(playerId).create(PickerGroundItemComponent.class);
        pickerGroundItemComponent.set(10);

//        // texture component
//        TextureComponent textureComponent = world.edit(playerId).create(TextureComponent.class);
//        final TextureRegion texture = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu");
//        textureComponent.set(texture);

        // animation
        TextureComponent textureComponent = world.edit(playerId).create(TextureComponent.class);
        textureComponent.scale = 0.05f;
        TextureAtlas.AtlasRegion textureFront0 = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu-front-0");
        TextureAtlas.AtlasRegion textureFront1 = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu-front-1");
        TextureAtlas.AtlasRegion textureFront2 = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu-front-2");
        playerComponent.animationFront = new TextureRegion[]{textureFront0, textureFront1, textureFront2};
        TextureAtlas.AtlasRegion textureLeft0 = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu-left-0");
        TextureAtlas.AtlasRegion textureLeft1 = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu-left-1");
        TextureAtlas.AtlasRegion textureLeft2 = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu-left-2");
        playerComponent.animationLeft = new TextureRegion[]{textureLeft0, textureLeft1, textureLeft2};
        TextureAtlas.AtlasRegion textureBack0 = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu-back-0");
        TextureAtlas.AtlasRegion textureBack1 = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu-back-1");
        TextureAtlas.AtlasRegion textureBack2 = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu-back-2");
        playerComponent.animationBack = new TextureRegion[]{textureBack0, textureBack1, textureBack2};
        TextureAtlas.AtlasRegion textureRight0 = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu-right-0");
        TextureAtlas.AtlasRegion textureRight1 = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu-right-1");
        TextureAtlas.AtlasRegion textureRight2 = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu-right-2");
        playerComponent.animationRight = new TextureRegion[]{textureRight0, textureRight1, textureRight2};

        // default crafting table
        int defaultCraftingTable = world.create();
        int defaultResultInventory = world.create();
        world.edit(playerId).create(CraftingTableComponent.class).set(defaultCraftingTable, defaultResultInventory);
        world.edit(defaultCraftingTable).create(InventoryComponent.class).set(2, 2, "water-01");
        world.edit(defaultResultInventory).create(InventoryComponent.class).set(1, 1, "water-01");
        world.edit(defaultCraftingTable).create(CraftingComponent.class).set(RealmTechCoreMod.CRAFT, defaultResultInventory);
        return playerId;
    }
    public void dispose() {
        world.dispose();
        physicWorld.dispose();
        System.gc();
    }

    public void clearAllEntity() {
        final IntBag entities = world.getAspectSubscriptionManager().get(Aspect.all()).getEntities();
        for (int entity : entities.getData()) {
            world.delete(entity);
        }
    }
    public Body createBox2dItem(int itemId, float worldX, float worldY, TextureRegion texture) {
        resetBodyDef();
        resetFixtureDef();
        bodyDef.position.set(worldX, worldY);
        bodyDef.gravityScale = 0;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Body itemBody = physicWorld.createBody(bodyDef);
        itemBody.setUserData(itemId);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(texture.getRegionWidth() / RealmTech.PPM, texture.getRegionHeight() / RealmTech.PPM);
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = BIT_GAME_OBJECT;
        fixtureDef.filter.maskBits = BIT_WORLD | BIT_GAME_OBJECT | BIT_PLAYER;
        itemBody.createFixture(fixtureDef);
        polygonShape.dispose();
        return itemBody;
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
        world.getSystem(PlayerInventorySystem.class).toggleInventoryWindow(world.getSystem(PlayerInventorySystem.class).getDisplayInventoryPlayerArgs());
        world.getSystem(SoundManager.class).playOpenInventory();
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
        createPlayer();
    }

    public void saveInfMap() throws IOException {
        int mapId = getMapId();
        world.getSystem(SaveInfManager.class).saveInfMap(mapId);
    }

    public int getMapId() {
        return world.getSystem(TagManager.class).getEntityId("infMap");
    }


    /**
     * Donne l'id de la cellule via ses cordonnées dans le monde.
     * @return l'id de la cellule ou -1 si pas trouvé.
     */
    public int getCell(float worldPosX, float worldPosY, byte layer) {
        return world.getSystem(MapSystem.class).getCell(world.getMapper(InfMapComponent.class).get(world.getSystem(TagManager.class).getEntityId("infMap")).infChunks ,(int) worldPosX, (int) worldPosY, layer);
    }

    public void playFootStep(PlayerFootStepSound footStep) {
        world.getSystem(SoundManager.class).playFootStep(footStep.playerFootStepSound(), footStep.volume());
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
        world.getSystem(ItemManager.class).dropCurentPlayerItem();
    }

    public <T extends BaseSystem> T getSystem(Class<T> type) {
        return world.getSystem(type);
    }
}

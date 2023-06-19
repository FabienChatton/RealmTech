package ch.realmtech.game.ecs;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.*;
import ch.realmtech.game.ecs.system.*;
import ch.realmtech.game.mod.PlayerFootStepSound;
import ch.realmtech.game.mod.RealmTechCoreMod;
import ch.realmtech.game.mod.RealmTechCorePlugin;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

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
                .with(new WorldContactListenerManager())
                .with(new SaveInfManager())

                // system
                .with(new WorldMapSystem())
                .with(new CraftingSystem())
                .with(new ItemBeingPickAnimationSystem())
                .with(new SoundManager())
                .with(new PickUpOnGroundItemSystem())
                .with(new PlayerMouvementSystem())
                .with(new PhysiqueWorldStepSystem())
                .with(new UpdateBox2dWithTextureSystem())
                .with(new WorldMapRendererSystem())
                .with(new CameraFollowPlayerSystem())
                .with(new RendererTextureInGameSystem())

                // ui
                .with(new PlayerInventoryManager())
                .with(new ItemBarManager())
                .build();
        worldConfiguration.register("physicWorld", physicWorld);
        worldConfiguration.register("gameStage", context.getGameStage());
        worldConfiguration.register("context", context);
        worldConfiguration.register("gameCamera", context.getGameStage().getCamera());
        worldConfiguration.register(context.getTextureAtlas());
        world = new World(worldConfiguration);
        physicWorld.setContactListener(world.getSystem(WorldContactListenerManager.class));
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
        inventoryComponent.set(InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW, InventoryComponent.DEFAULT_NUMBER_OF_ROW, context.getTextureAtlas().findRegion("water-01"));

        // pick up item component
        PickerGroundItemComponent pickerGroundItemComponent = world.edit(playerId).create(PickerGroundItemComponent.class);
        pickerGroundItemComponent.set(10);

        // texture component
        TextureComponent textureComponent = world.edit(playerId).create(TextureComponent.class);
        final TextureRegion texture = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu");
        textureComponent.set(texture);

        // default crafting table
        int defaultCraftingTable = world.create();
        int defaultResultInventory = world.create();
        CraftingComponent craftingComponent = world.edit(defaultCraftingTable).create(CraftingComponent.class);
        world.edit(defaultResultInventory).create(InventoryComponent.class).set(1,1, context.getTextureAtlas().findRegion("water-01"));
        craftingComponent.set(RealmTechCoreMod.REALM_TECH_CORE_CRAFTING_RECIPE_ENTRY, defaultResultInventory);
        InventoryComponent craftingInventoryComponent = world.edit(defaultCraftingTable).create(InventoryComponent.class);
        craftingInventoryComponent.set(3, 3,context.getTextureAtlas().findRegion("water-01"));
        world.getSystem(TagManager.class).register("crafting", defaultCraftingTable);
        world.getSystem(TagManager.class).register("crafting-result-inventory", defaultResultInventory);

        return playerId;
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


    public World getWorld() {
        return world;
    }

    public ItemManager getItemManager() {
        return world.getSystem(ItemManager.class);
    }

    public void togglePlayerInventoryWindow() {
        world.getSystem(PlayerInventoryManager.class).toggleInventoryWindow(getPlayerId());
        world.getSystem(SoundManager.class).playOpenInventory();
    }

    public void loadInfFile(Path path) throws IOException {
        int mapId = world.getSystem(SaveInfManager.class).readInfMap(path);
        mapRequirementBeforeShow(mapId);
    }

    public void generateNewSave(String name) throws IOException {
        int mapId = world.getSystem(SaveInfManager.class).generateNewSave(name);
        InfMapComponent infMapComponent = world.getMapper(InfMapComponent.class).get(mapId);
        int chunkId = world.getSystem(WorldMapSystem.class).generateNewChunk(mapId, 0, 0);
        infMapComponent.infChunks = new int[]{chunkId};
        mapRequirementBeforeShow(mapId);
        world.getSystem(SaveInfManager.class).saveInfMap(mapId);
    }

    public void mapRequirementBeforeShow(int mapId) {
        createPlayer();
        world.getSystem(TagManager.class).register("infMap", mapId);
    }

    public void saveInfMap() throws IOException {
        int mapId = world.getSystem(TagManager.class).getEntityId("infMap");
        world.getSystem(SaveInfManager.class).saveInfMap(mapId);
    }

    /**
     * Donne l'id de la cellule via ses cordonnées dans le monde.
     * @param worldPosX
     * @param worldPosY
     * @return l'id de la cellule ou -1 si pas trouvé.
     */
    public int getCell(float worldPosX, float worldPosY) {
        return world.getSystem(WorldMapSystem.class).getCell(world.getSystem(TagManager.class).getEntityId("infMap") ,(int) worldPosX, (int) worldPosY);
    }

    public void playFootStep(PlayerFootStepSound footStep) {
        world.getSystem(SoundManager.class).playFootStep(footStep.playerFootStepSound(), footStep.volume());
    }

    public int readSavedInfChunk(int chunkX, int chunkY, Path rootSaveDirPath) throws IOException {
        return world.getSystem(SaveInfManager.class).readSavedInfChunk(chunkX, chunkY, rootSaveDirPath);
    }

    public void saveInfChunk(int infChunkId, Path rootSaveDirPath) throws IOException {
        world.getSystem(SaveInfManager.class).saveInfChunk(infChunkId, rootSaveDirPath);
    }
}

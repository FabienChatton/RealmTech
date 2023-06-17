package ch.realmtech.game.ecs;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.*;
import ch.realmtech.game.ecs.system.*;
import ch.realmtech.game.level.map.WorldMap;
import ch.realmtech.game.mod.RealmTechCoreMod;
import ch.realmtech.game.mod.RealmTechCorePlugin;
import com.artemis.World;
import com.artemis.*;
import com.artemis.managers.TagManager;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.io.File;
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
    private Body bodyWorldBorder;
    private final World world;
    //private int playerEntity = -1;
    private int save = -1;
    private int worldMap = -1;

    public ECSEngine(final RealmTech context) {
        this.context = context;
        WorldConfiguration worldConfiguration = new WorldConfigurationBuilder()
                .dependsOn(RealmTechCorePlugin.class)
                // manageur
                .with(new TagManager())
                .with(new CellManager())
                .with(new ChunkManager())
                .with(new ItemManager())
                .with(new SaveManager())
                .with(new WorldMapManager())
                .with(new InventoryManager())
                .with(new WorldContactListenerManager())
                .with(new SaveInfManager())

                // system
                .with(new CraftingSystem())
                .with(new ItemBeingPickAnimationSystem())
                .with(new SoundManager())
                .with(new PickUpOnGroundItemSystem())
                .with(new PlayerMouvementSystem())
                .with(new WorldStepSystem())
                .with(new UpdateBox2dWithTextureSystem())
                .with(new WorldMapRendererSystem())
                .with(new CameraFollowPlayerSystem())
                .with(new RendererTextureInGameSystem())

                // ui
                //.with(new PlayerInventoryManager())
                //.with(new ItemBarManager())
                .build();
        worldConfiguration.register("physicWorld", context.physicWorld);
        worldConfiguration.register("gameStage", context.getGameStage());
        worldConfiguration.register("context", context);
        worldConfiguration.register("gameCamera", context.getGameStage().getCamera());
        worldConfiguration.register(context.getTextureAtlas());
        world = new World(worldConfiguration);
        context.physicWorld.setContactListener(world.getSystem(WorldContactListenerManager.class));
        bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();
        world.getSystem(CellManager.class);
        world.getSystem(ChunkManager.class);
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

    public void createPlayer() {
        final int playerWorldWith = 1;
        final int playerWorldHigh = 1;
        if (world.getSystem(TagManager.class).getEntity(PlayerComponent.TAG) != null) {
            int playerId = world.getSystem(TagManager.class).getEntityId(PlayerComponent.TAG);
            world.getSystem(TagManager.class).unregister(PlayerComponent.TAG);
            context.physicWorld.destroyBody(world.edit(playerId).create(Box2dComponent.class).body);
            world.delete(playerId);
        }
        int playerId = world.create();
        world.getSystem(TagManager.class).register(PlayerComponent.TAG, playerId);

        resetBodyDef();
        resetFixtureDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Body bodyPlayer = context.physicWorld.createBody(bodyDef);
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
    }

    public void spawnPlayer(Vector2 spawnPoint) {
        Box2dComponent box2dComponent = world.edit(world.getSystem(TagManager.class).getEntityId(PlayerComponent.TAG))
                .create(Box2dComponent.class);
        box2dComponent.body.setTransform(spawnPoint.x, spawnPoint.y, box2dComponent.body.getAngle());
    }

    public void newSaveInitWorld(File saveFile) {
        if (save != -1) {
            world.delete(save);
            world.delete(worldMap);
        }
        save = world.create();
        worldMap = world.create();
        world.edit(save).create(SaveComponent.class).set(context, saveFile);
        world.edit(worldMap).create(WorldMapComponent.class).saveId = save;

        world.getSystem(WorldMapManager.class).init(worldMap, WorldMap.WORLD_WITH, WorldMap.WORLD_HIGH, 32, 32, WorldMap.NUMBER_LAYER);
    }

    public void generateNewWorldMap() {
        world.getSystem(WorldMapManager.class).generateNewWorldMap(worldMap);
    }

    // TODO a changer de place car ne contient pas d'entités a ajouter au system
    public void generateBodyWorldBorder(int x, int y, int x2, int y2) {
        if (bodyWorldBorder != null) {
            context.physicWorld.destroyBody(bodyWorldBorder);
            bodyWorldBorder = null;
        }
        resetBodyDef();
        resetFixtureDef();
        bodyDef.position.set(x, y);
        bodyDef.gravityScale = 0;
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyWorldBorder = context.physicWorld.createBody(bodyDef);
        ChainShape chain = new ChainShape();
        chain.createChain(new float[]{x, x, x, x2, x2, y2, y2, y, y, x});
        fixtureDef.shape = chain;
        fixtureDef.filter.categoryBits = BIT_WORLD;
        fixtureDef.filter.maskBits = -1;
        bodyWorldBorder.createFixture(fixtureDef);
        chain.dispose();
    }

    public Body createBox2dItem(int itemId, float worldX, float worldY, TextureRegion texture) {
        resetBodyDef();
        resetFixtureDef();
        bodyDef.position.set(worldX, worldY);
        bodyDef.gravityScale = 0;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Body itemBody = context.physicWorld.createBody(bodyDef);
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

    public void saveInfChunk(ChunkComponent chunkComponent) {

    }

    public Entity getPlayer() {
        return world.getEntity(world.getSystem(TagManager.class).getEntityId(PlayerComponent.TAG));
    }

    public int getPlayerId() {
        return world.getSystem(TagManager.class).getEntityId(PlayerComponent.TAG);
    }

    public SaveComponent getWorkingSave() {
        return world.getEntity(save).getComponent(SaveComponent.class);
    }

    public WorldMap getWorkingMap() {
        if (worldMap == -1){
            return null;
        }
        Entity worldMapEntity = world.getEntity(worldMap);
        return worldMapEntity == null ? null : worldMapEntity.getComponent(WorldMapComponent.class).worldMap;
    }

    public WorldMapManager getWorldMapManager() {
        return world.getSystem(WorldMapManager.class);
    }

    public void saveWorldMap() throws IOException {
        world.getSystem(SaveManager.class).saveWorldMap(save);
        getPlayer().getComponent(InventoryComponent.class).inventory = null;
        IntBag entitiesToRemove = world.getAspectSubscriptionManager().get(Aspect.all()).getEntities();
        for (int toRemove : entitiesToRemove.getData()) {
            if (toRemove != world.getSystem(TagManager.class).getEntityId(PlayerComponent.TAG)) {
                world.delete(toRemove);
            }
        }
    }

    public void loadSaveOnWorkingSave() throws IOException{
        world.getSystem(SaveManager.class).loadSave(
                world.getEntity(worldMap).getComponent(WorldMapComponent.class),
                getWorkingSave().file
        );
        world.getSystem(WorldMapManager.class).placeWorldMap(worldMap);
    }

    public CellManager getCellManager() {
        return world.getSystem(CellManager.class);
    }

    public <T extends BaseSystem> T getSystem(Class<T> system) {
        return world.getSystem(system);
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
        int worldId = world.getSystem(SaveInfManager.class).readInfMap(path);
        world.getSystem(WorldMapManager.class).mountInfMap(worldId);
    }

    public void generateNewSave(String name) throws IOException {
        int mapId = world.getSystem(SaveInfManager.class).generateNewSave(name);
        InfMapComponent infMapComponent = world.getMapper(InfMapComponent.class).get(mapId);
        int chunkId = world.getSystem(WorldMapManager.class).generateNewChunk(infMapComponent.infMetaDonnees, 0, 0);
        infMapComponent.infChunks = new int[]{chunkId};
        infMapComponent.worldMap = new WorldMap();
        world.getSystem(WorldMapManager.class).mountInfMap(mapId);
        world.getSystem(SaveInfManager.class).saveInfMap(mapId, name);
        world.getSystem(WorldMapManager.class).placeWorldInfMap(mapId);
    }
}

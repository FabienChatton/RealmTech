package ch.realmtech.game.ecs;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.*;
import ch.realmtech.game.ecs.system.*;
import ch.realmtech.game.level.map.WorldMap;
import com.artemis.World;
import com.artemis.*;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.io.File;
import java.io.IOException;

public final class ECSEngine {
    private final static String TAG = ECSEngine.class.getSimpleName();

    private final static byte BIT_PLAYER = 1 << 1;
    private final static byte BIT_WORLD = 1 << 2;
    private final static byte BIT_GAME_OBJECT = 1 << 3;
    private final RealmTech context;

    private final BodyDef bodyDef;
    private final FixtureDef fixtureDef;
    private Body bodyWorldBorder;
    private final World ecsWorld;
    private int playerEntity = -1;
    private int save = -1;
    private int worldMap = -1;

    public ECSEngine(final RealmTech context) {
        this.context = context;
        WorldConfiguration worldConfiguration = new WorldConfigurationBuilder()
                .with(new CellManager())
                .with(new ChunkManager())
                .with(new SaveManager())
                .with(new WorldMapManager())
                .with(new PlayerMouvementSystem())
                .with(new WorldStepSystem())
                .with(new UpdateBox2dWithTextureSystem())
                .with(new CameraFollowPlayerSystem())
                .with(new RendererTextureInGameSystem())
                .build();
        worldConfiguration.register("physicWorld", context.physicWorld);
        worldConfiguration.register("gameStage", context.getGameStage());
        worldConfiguration.register("context", context);
        worldConfiguration.register("gameCamera", context.getGameStage().getCamera());
        worldConfiguration.register(context.getTextureAtlas());
        ecsWorld = new World(worldConfiguration);
        bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();
        ecsWorld.getSystem(CellManager.class).register();
        ecsWorld.getSystem(ChunkManager.class).register();
        createPlayer();
    }

    public void process(float delta) {
        ecsWorld.setDelta(delta);
        ecsWorld.process();
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

    private void createPlayer() {
        final int playerWorldWith = 1;
        final int playerWorldHigh = 1;
        if (playerEntity != -1) {
            context.physicWorld.destroyBody(ecsWorld.edit(playerEntity).create(Box2dComponent.class).body);
            ecsWorld.delete(playerEntity);
        }
        playerEntity = ecsWorld.create();

        resetBodyDef();
        resetFixtureDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Body bodyPlayer = context.physicWorld.createBody(bodyDef);
        PolygonShape playerShape = new PolygonShape();
        playerShape.setAsBox(playerWorldWith / 2f, playerWorldHigh / 2f);
        fixtureDef.shape = playerShape;
        fixtureDef.filter.categoryBits = BIT_PLAYER;
        fixtureDef.filter.maskBits = BIT_WORLD | BIT_GAME_OBJECT;
        bodyPlayer.createFixture(fixtureDef);

        playerShape.dispose();

        // box2d component
        Box2dComponent box2dComponent = ecsWorld.edit(playerEntity).create(Box2dComponent.class);
        box2dComponent.init(playerWorldWith, playerWorldHigh, bodyPlayer);

        // player component
        PlayerComponent playerComponent = ecsWorld.edit(playerEntity).create(PlayerComponent.class);

        // movement component
        MovementComponent movementComponent = ecsWorld.edit(playerEntity).create(MovementComponent.class);
        movementComponent.init(10, 10);

        // position component
        PositionComponent possitionComponent = ecsWorld.edit(playerEntity).create(PositionComponent.class);

        // texture component
        TextureComponent textureComponent = ecsWorld.edit(playerEntity).create(TextureComponent.class);
        final TextureRegion texture = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu");
        textureComponent.init(texture);
    }

    public void spawnPlayer(Vector2 spawnPoint) {
        Box2dComponent box2dComponent = ecsWorld.edit(playerEntity).create(Box2dComponent.class);
        box2dComponent.body.setTransform(spawnPoint.x, spawnPoint.y, box2dComponent.body.getAngle());
    }

    public void newSaveInitWorld(File saveFile) {
        if (save != -1) {
            ecsWorld.delete(save);
            ecsWorld.delete(worldMap);
        }
        save = ecsWorld.create();
        worldMap = ecsWorld.create();
        ecsWorld.edit(save).create(SaveComponent.class).init(context, saveFile);
        ecsWorld.edit(worldMap).create(WorldMapComponent.class).saveId = save;

        ecsWorld.getSystem(WorldMapManager.class).init(worldMap, WorldMap.WORLD_WITH, WorldMap.WORLD_HIGH, 32, 32, WorldMap.NUMBER_LAYER);
    }

    public void generateNewWorldMap() {
        ecsWorld.getSystem(WorldMapManager.class).generateNewWorldMap(worldMap);
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

    public Entity getPlayer() {
        return ecsWorld.getEntity(playerEntity);
    }

    public SaveComponent getWorkingSave() {
        return ecsWorld.getEntity(save).getComponent(SaveComponent.class);
    }

    public WorldMap getWorkingMap() {
        if (worldMap == -1){
            return null;
        }
        Entity worldMapEntity = ecsWorld.getEntity(worldMap);
        return worldMapEntity == null ? null : worldMapEntity.getComponent(WorldMapComponent.class).worldMap;
    }

    public WorldMapManager getWorldMapManager() {
        return ecsWorld.getSystem(WorldMapManager.class);
    }

    public void saveWorldMap() throws IOException {
        ecsWorld.getSystem(SaveManager.class).saveWorldMap(save);
        IntBag entitiesToRemove = ecsWorld.getAspectSubscriptionManager().get(Aspect.one(CellComponent.class, ChunkComponent.class)).getEntities();
        for (int toRemove : entitiesToRemove.getData()) {
            if (toRemove != playerEntity) {
                ecsWorld.delete(toRemove);
            }
        }
    }

    public void loadSaveOnWorkingSave() throws IOException{
        ecsWorld.getSystem(SaveManager.class).loadSave(
                ecsWorld.getEntity(worldMap).getComponent(WorldMapComponent.class),
                getWorkingSave().file
        );
    }

    public CellManager getCellManager() {
        return ecsWorld.getSystem(CellManager.class);
    }

    public <T extends BaseSystem> T getSystem(Class<T> system) {
        return ecsWorld.getSystem(system);
    }

    public World getEcsWorld() {
        return ecsWorld;
    }
}

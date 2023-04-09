package ch.realmtech.game.ecs;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.*;
import ch.realmtech.game.ecs.system.*;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

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
    private int playerEntity;

    public ECSEngine(final RealmTech context) {
        this.context = context;
        WorldConfiguration worldConfiguration = new WorldConfigurationBuilder()
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
        ecsWorld = new World(worldConfiguration);
        bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();
    }

    public void process(float delta) {
        ecsWorld.setDelta(delta);
        ecsWorld.process();
    }

    private void resetBodyDef() {
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0,0);
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

    private void resetFixtureDef(){
        fixtureDef.shape = null;
        fixtureDef.friction = 0.2f;
        fixtureDef.restitution = 0;
        fixtureDef.density = 0;
        fixtureDef.isSensor = false;
        fixtureDef.filter.categoryBits = 0;
        fixtureDef.filter.maskBits = 0;
    }

    public void createBodyPlayer(){
        final int playerWorldWith = 1;
        final int playerWorldHigh = 1;
        if (ecsWorld.getEntity(playerEntity) != null) {
            context.physicWorld.destroyBody(ecsWorld.edit(playerEntity).create(Box2dComponent.class).body);
            ecsWorld.delete(playerEntity);
        }
        playerEntity = ecsWorld.create();

        resetBodyDef();
        resetFixtureDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Vector2 spawnPoint = context.getSave().getTiledMap().getProperties().get("spawn-point", Vector2.class);
        bodyDef.position.set(spawnPoint.x,spawnPoint.y);
        Body bodyPlayer = context.physicWorld.createBody(bodyDef);
        PolygonShape playerShape = new PolygonShape();
        playerShape.setAsBox(playerWorldWith/2f, playerWorldHigh/2f);
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
        movementComponent.init(10,10);

        // position component
        PositionComponent possitionComponent = ecsWorld.edit(playerEntity).create(PositionComponent.class);

        // texture component
        TextureComponent textureComponent = ecsWorld.edit(playerEntity).create(TextureComponent.class);
        final TextureRegion texture = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu");
        textureComponent.init(texture);
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
        chain.createChain(new float[]{x,x,x,x2,x2,y2,y2,y,y,x});
        fixtureDef.shape = chain;
        fixtureDef.filter.categoryBits = BIT_WORLD;
        fixtureDef.filter.maskBits = -1;
        bodyWorldBorder.createFixture(fixtureDef);
        chain.dispose();
    }

    public Entity getPlayer() {
        return ecsWorld.getEntity(playerEntity);
    }
}

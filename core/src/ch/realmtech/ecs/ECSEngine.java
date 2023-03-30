package ch.realmtech.ecs;

import ch.realmtech.RealmTech;
import ch.realmtech.ecs.component.*;
import ch.realmtech.ecs.system.PlayerMouvementSystem;
import ch.realmtech.ecs.system.RendererTextureInGameSystem;
import ch.realmtech.ecs.system.WorldStepSystem;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.*;

public final class ECSEngine extends PooledEngine {
    public final static ComponentMapper<PossitionComponent> POSSITION_COMPONENT_MAPPER = ComponentMapper.getFor(PossitionComponent.class);
    public final static ComponentMapper<TextureComponent> TEXTURE_COMPONENT_MAPPER = ComponentMapper.getFor(TextureComponent.class);
    public final static ComponentMapper<PlayerComponent> PLAYER_COMPONENT_MAPPER = ComponentMapper.getFor(PlayerComponent.class);
    public final static ComponentMapper<MovementComponent> MOVEMENT_COMPONENT_MAPPER = ComponentMapper.getFor(MovementComponent.class);
    public static final ComponentMapper<Box2dComponent> BOX2D_COMPONENT_MAPPER = ComponentMapper.getFor(Box2dComponent.class);
    private final static byte BIT_PLAYER = 1 << 1;
    private final static byte BIT_WORLD = 1 << 2;
    private final static byte BIT_GAME_OBJECT = 1 << 3;
    private final RealmTech context;

    private final BodyDef bodyDef;
    private final FixtureDef fixtureDef;

    public ECSEngine(final RealmTech context) {
        this.context = context;
        addSystem(new RendererTextureInGameSystem(context.getGameStage().getBatch()));
        addSystem(new PlayerMouvementSystem(context));
        addSystem(new WorldStepSystem(context.world));
        bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();
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

    public void createPlayer(){
        final int playerWorldWith = 1;
        final int playerWorldHigh = 1;
        Entity player = new Entity();

        resetBodyDef();
        resetFixtureDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Body playerBody = context.world.createBody(bodyDef);
        PolygonShape playerShape = new PolygonShape();
        playerShape.setAsBox(playerWorldWith/2f, playerWorldHigh/2f);
        fixtureDef.shape = playerShape;
        fixtureDef.filter.categoryBits = BIT_PLAYER;
        fixtureDef.filter.maskBits = BIT_WORLD | BIT_GAME_OBJECT;
        playerBody.createFixture(fixtureDef);

        playerShape.dispose();

        final Box2dComponent box2dComponent = new Box2dComponent(playerWorldWith,playerWorldHigh,playerBody);
        player.add(box2dComponent);
        player.add(new PlayerComponent());
        player.add(new MovementComponent(10,10));
        player.add(new PossitionComponent());
        player.add(new TextureComponent(new Texture(Gdx.files.internal("reimu.png"))));
        addEntity(player);
    }
}

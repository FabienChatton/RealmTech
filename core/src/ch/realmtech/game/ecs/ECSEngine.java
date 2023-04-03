package ch.realmtech.game.ecs;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.*;
import ch.realmtech.game.ecs.system.*;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public final class ECSEngine extends PooledEngine {
    private final static String TAG = ECSEngine.class.getSimpleName();
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
    private Body bodyPlayer;
    private Body bodyWorldBorder;

    public ECSEngine(final RealmTech context) {
        this.context = context;
        addSystem(new PlayerMouvementSystem(context));
        addSystem(new WorldStepSystem(context.world));
        addSystem(new UpdateBox2dWithTextureSystem());
        addSystem(new RendererTextureInGameSystem(context.getGameStage()));
        addSystem(new CameraFollowPlayerSystem((OrthographicCamera) context.getGameStage().getCamera()));
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

    public void createBodyPlayer(){
        if (bodyPlayer != null) {
            Gdx.app.log(TAG,"Un joueur existe déjà. Le nouveau joueur n'a pas été créer");
            return;
        }
        final int playerWorldWith = 1;
        final int playerWorldHigh = 1;
        Entity player = new Entity();

        resetBodyDef();
        resetFixtureDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Vector2 spawnPoint = context.gameMap.getProperties().get("spawn-point", Vector2.class);
        bodyDef.position.set(spawnPoint.x,spawnPoint.y);
        bodyPlayer = context.world.createBody(bodyDef);
        PolygonShape playerShape = new PolygonShape();
        playerShape.setAsBox(playerWorldWith/2f, playerWorldHigh/2f);
        fixtureDef.shape = playerShape;
        fixtureDef.filter.categoryBits = BIT_PLAYER;
        fixtureDef.filter.maskBits = BIT_WORLD | BIT_GAME_OBJECT;
        bodyPlayer.createFixture(fixtureDef);

        playerShape.dispose();

        final Box2dComponent box2dComponent = new Box2dComponent(playerWorldWith,playerWorldHigh,bodyPlayer);
        player.add(box2dComponent);
        player.add(new PlayerComponent());
        player.add(new MovementComponent(10,10));
        player.add(new PossitionComponent());
        player.add(new TextureComponent(new Texture(Gdx.files.internal("reimu.png"))));
        addEntity(player);
    }

    // TODO a changer de place car ne contient pas d'entités a ajouter au system
    public void generateBodyWorldBorder(int x, int y, int x2, int y2) {
        if (bodyWorldBorder != null) {
            context.world.destroyBody(bodyWorldBorder);
            bodyWorldBorder = null;
        }
        resetBodyDef();
        resetFixtureDef();
        bodyDef.position.set(x, y);
        bodyDef.gravityScale = 0;
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyWorldBorder = context.world.createBody(bodyDef);
        ChainShape chain = new ChainShape();
        chain.createChain(new float[]{x,x,x,x2,x2,y2,y2,y,y,x});
        fixtureDef.shape = chain;
        fixtureDef.filter.categoryBits = BIT_WORLD;
        fixtureDef.filter.maskBits = -1;
        bodyWorldBorder.createFixture(fixtureDef);
        chain.dispose();
    }
}

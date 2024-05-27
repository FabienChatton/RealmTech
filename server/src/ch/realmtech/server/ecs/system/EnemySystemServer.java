package ch.realmtech.server.ecs.system;

import ch.realmtech.server.PhysiqueWorldHelper;
import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.Box2dComponent;
import ch.realmtech.server.ecs.component.EnemyHitPlayerComponent;
import ch.realmtech.server.ecs.component.LifeComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.enemy.EnemyComponent;
import ch.realmtech.server.enemy.EnemySteerable;
import ch.realmtech.server.enemy.EnemyTelegraph;
import ch.realmtech.server.mod.options.server.mob.MaxDstSpawnPlayerOptionEntry;
import ch.realmtech.server.mod.options.server.mob.MaxEnemyCountOptionEntry;
import ch.realmtech.server.mod.options.server.mob.MinDstSpawnPlayerOptionEntry;
import ch.realmtech.server.mod.options.server.mob.NaturalSpawnCoolDownOptionEntry;
import ch.realmtech.server.registry.RegistryUtils;
import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.EntityEdit;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import java.util.Random;
import java.util.UUID;

public class EnemySystemServer extends BaseSystem {
    @Wire(name = "physicWorld")
    private com.badlogic.gdx.physics.box2d.World physicWorld;
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    @Wire
    private FixtureDef fixtureDef;
    @Wire
    private BodyDef bodyDef;
    @Wire
    private SystemsAdminServer systemsAdminServer;
    private ComponentMapper<PositionComponent> mPos;
    private ComponentMapper<Box2dComponent> mBox2d;
    private final MessageManager messageManager = MessageManager.getInstance();
    private long lastNaturalSpawn = 0;
    private Random random;
    private NaturalSpawnCoolDownOptionEntry naturalSpawnCoolDownOptionEntry;
    private MaxDstSpawnPlayerOptionEntry maxDstSpawnPlayerOptionEntry;
    private MinDstSpawnPlayerOptionEntry minDstSpawnPlayerOptionEntry;
    private MaxEnemyCountOptionEntry maxEnemyCountOptionEntry;

    @Override
    protected void initialize() {
        super.initialize();
        messageManager.setDebugEnabled(false);
        random = new Random();

        naturalSpawnCoolDownOptionEntry = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), NaturalSpawnCoolDownOptionEntry.class);
        maxDstSpawnPlayerOptionEntry = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), MaxDstSpawnPlayerOptionEntry.class);
        minDstSpawnPlayerOptionEntry = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), MinDstSpawnPlayerOptionEntry.class);
        maxEnemyCountOptionEntry = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), MaxEnemyCountOptionEntry.class);
    }

    @Override
    protected void processSystem() {
        naturalSpawnEnemy();
        messageManager.update();
    }

    private void naturalSpawnEnemy() {
        if (systemsAdminServer.getTimeSystem().isNight() && System.currentTimeMillis() - lastNaturalSpawn > naturalSpawnCoolDownOptionEntry.getValue()) {
            IntBag enemyEntities = world.getAspectSubscriptionManager().get(Aspect.all(EnemyComponent.class)).getEntities();
            if (enemyEntities.size() > maxEnemyCountOptionEntry.getValue()) {
                return;
            }
            IntBag players = systemsAdminServer.getPlayerManagerServer().getPlayers();

            for (int i = 0; i < players.size(); i++) {
                int playerId = players.get(i);

                Vector2 playerVector = mPos.get(playerId).toVector2();
                Vector2 enemySpawnVector = new Vector2();
                enemySpawnVector.add(random.nextInt(minDstSpawnPlayerOptionEntry.getValue(), maxDstSpawnPlayerOptionEntry.getValue()), 0);
                enemySpawnVector.rotateDeg(random.nextFloat(360));
                Vector2 enemySpawnPos = playerVector.cpy().add(enemySpawnVector);
                spawnEnemy(enemySpawnPos.x, enemySpawnPos.y);
                lastNaturalSpawn = System.currentTimeMillis();
            }
        }
    }

    public int spawnEnemy(float x, float y) {
        int mobId = world.create();
        PhysiqueWorldHelper.resetBodyDef(bodyDef);
        PhysiqueWorldHelper.resetFixtureDef(fixtureDef);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Body bodyMob = physicWorld.createBody(bodyDef);
        bodyMob.setUserData(mobId);

        PolygonShape physicContactShape = new PolygonShape();
        physicContactShape.setAsBox(0.9f, 0.9f);
        fixtureDef.shape = physicContactShape;
        fixtureDef.filter.categoryBits = PhysiqueWorldHelper.BIT_GAME_OBJECT;
        fixtureDef.filter.maskBits = PhysiqueWorldHelper.BIT_WORLD | PhysiqueWorldHelper.BIT_GAME_OBJECT;
        bodyMob.createFixture(fixtureDef);
        bodyMob.setTransform(x, y, bodyMob.getAngle());

        PhysiqueWorldHelper.resetFixtureDef(fixtureDef);
        PolygonShape playerContactShape = new PolygonShape();
        playerContactShape.setAsBox(0.1f, 0.1f);
        fixtureDef.shape = playerContactShape;
        fixtureDef.filter.categoryBits = PhysiqueWorldHelper.BIT_GAME_OBJECT;
        fixtureDef.filter.maskBits = PhysiqueWorldHelper.BIT_PLAYER;
        bodyMob.createFixture(fixtureDef);

        playerContactShape.dispose();
        EntityEdit mobEdit = world.edit(mobId);
        mobEdit.create(EnemyComponent.class).set(new EnemyTelegraph(mobId, serverContext), new EnemySteerable(bodyMob, 4), systemsAdminServer.getIaMobFocusPlayerSystem().enemyFocusPlayer(), EnemyComponent.ZOMBIE_FLAG);
        mobEdit.create(EnemyHitPlayerComponent.class);

        //iaComponent.getIaTestSteerable().setSteeringBehavior(new Seek<>(iaComponent.getIaTestSteerable(), new Box2dLocation(target)));
        mobEdit.create(Box2dComponent.class).set(0.9f, 0.9f, bodyMob);
        mobEdit.create(LifeComponent.class).set(10);
        PositionComponent positionComponent = mobEdit.create(PositionComponent.class);
        serverContext.getSystemsAdminServer().getUuidEntityManager().registerEntityIdWithUuid(UUID.randomUUID(), mobId);
        positionComponent.set(x, y);
        return mobId;
    }
}

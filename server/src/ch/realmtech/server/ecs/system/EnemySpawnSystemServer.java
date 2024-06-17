package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.enemy.EnemyComponent;
import ch.realmtech.server.mod.mobs.ChickenMobEntry;
import ch.realmtech.server.mod.mobs.ZombieMobEntry;
import ch.realmtech.server.mod.options.server.mob.*;
import ch.realmtech.server.registry.MobEntry;
import ch.realmtech.server.registry.RegistryUtils;
import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import java.util.Random;

public class EnemySpawnSystemServer extends BaseSystem {
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
    private ComponentMapper<EnemyComponent> mEnemy;
    private final MessageManager messageManager = MessageManager.getInstance();
    private long lastNaturalSpawn = 0;
    private Random random;
    private NaturalSpawnCoolDownOptionEntry naturalSpawnCoolDownOptionEntry;
    private MaxDstSpawnPlayerOptionEntry maxDstSpawnPlayerOptionEntry;
    private MinDstSpawnPlayerOptionEntry minDstSpawnPlayerOptionEntry;
    private MaxEnemyCountOptionEntry maxEnemyCountOptionEntry;
    private EnemyDispawnDstOptionEntry enemyDispawnDstOptionEntry;

    @Override
    protected void initialize() {
        super.initialize();
        messageManager.setDebugEnabled(false);
        random = new Random();

        naturalSpawnCoolDownOptionEntry = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), NaturalSpawnCoolDownOptionEntry.class);
        maxDstSpawnPlayerOptionEntry = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), MaxDstSpawnPlayerOptionEntry.class);
        minDstSpawnPlayerOptionEntry = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), MinDstSpawnPlayerOptionEntry.class);
        maxEnemyCountOptionEntry = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), MaxEnemyCountOptionEntry.class);
        enemyDispawnDstOptionEntry = RegistryUtils.findEntryOrThrow(systemsAdminServer.getRootRegistry(), EnemyDispawnDstOptionEntry.class);
    }

    @Override
    protected void processSystem() {
        naturalSpawnEnemy();
        naturalDeSpawnEnemy();
        messageManager.update();
    }

    private void naturalSpawnEnemy() {
        int enemyCount = world.getAspectSubscriptionManager().get(Aspect.all(EnemyComponent.class, EnemyHitPlayerComponent.class)).getEntities().size();
        int passiveMobCount = world.getAspectSubscriptionManager().get(Aspect.all(EnemyComponent.class).exclude(EnemyHitPlayerComponent.class)).getEntities().size();

        testSpawnMob(RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), ChickenMobEntry.class), passiveMobCount, 5);
        if (systemsAdminServer.getTimeSystem().isNight()) {
            testSpawnMob(RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), ZombieMobEntry.class), enemyCount, maxEnemyCountOptionEntry.getValue());
        }
    }

    private void naturalDeSpawnEnemy() {
        IntBag enemyEntities = world.getAspectSubscriptionManager().get(Aspect.all(EnemyComponent.class)).getEntities();
        for (int i = 0; i < enemyEntities.size(); i++) {
            int enemyId = enemyEntities.get(i);
            PositionComponent enemyPos = mPos.get(enemyId);
            Vector2 enemyVec = enemyPos.toVector2();
            int playerId = getClosesPlayer(enemyVec);
            if (playerId != -1) {
                PositionComponent playerPos = mPos.get(playerId);
                float dst = playerPos.toVector2().dst(enemyVec);
                if (dst > enemyDispawnDstOptionEntry.getValue()) {
                    systemsAdminServer.getMobSystem().destroyEnemyServer(enemyId);
                }
            }
        }
    }

    private void testSpawnMob(MobEntry mobEntry, int numberOfMob, int maxMob) {
        if (System.currentTimeMillis() - lastNaturalSpawn > naturalSpawnCoolDownOptionEntry.getValue()) {
            if (numberOfMob >= maxMob) {
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
                spawnEnemy(enemySpawnPos.x, enemySpawnPos.y, mobEntry);
                lastNaturalSpawn = System.currentTimeMillis();
            }
        }
    }

    public int getClosesPlayer(Vector2 origin) {
        IntBag players = world.getAspectSubscriptionManager().get(Aspect.all(
                PlayerConnexionComponent.class
        ).exclude(
                PlayerDeadComponent.class
        )).getEntities();

        int[] playerData = players.getData();
        float minPlayerDst = Float.MAX_VALUE;
        int minPlayerId = -1;
        for (int i = 0; i < players.size(); i++) {
            int playerId = playerData[i];
            PositionComponent playerPositionComponent = mPos.get(playerId);
            float dst = origin.dst(playerPositionComponent.toVector2());

            if (dst < minPlayerDst) {
                minPlayerDst = dst;
                minPlayerId = playerId;
            }
        }

        return minPlayerId;
    }

    public void spawnEnemy(float x, float y, MobEntry mobEntry) {
        int mobId = world.create();
        mobEntry.getMobBehavior().getEditEntity().createEntity(serverContext.getExecuteOnContext(), mobId);
        Box2dComponent box2dComponent = mBox2d.get(mobId);
        box2dComponent.body.setTransform(x, y, box2dComponent.body.getAngle());
    }
}

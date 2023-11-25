package ch.realmtech.server.ecs;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.ecs.system.SaveInfManager;
import ch.realmtech.server.mod.RealmTechCorePlugin;
import ch.realmtech.server.options.DataCtrl;
import ch.realmtech.server.packet.clientPacket.TickBeatPacket;
import ch.realmtech.server.serialize.SerializerController;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public final class EcsEngineServer implements GetWorld {
    private final static Logger logger = LoggerFactory.getLogger(EcsEngineServer.class);
    private ServerContext serverContext;
    private World world;
    private com.badlogic.gdx.physics.box2d.World physicWorld;
    private final BodyDef bodyDef;
    private final FixtureDef fixtureDef;
    private final List<Runnable> nextTickServerPre = Collections.synchronizedList(new ArrayList<>());
    private final Map<Long, List<Runnable>> nextTickSchedule = Collections.synchronizedMap(new HashMap<>());
    private static long tickCount;
    private final SystemsAdminServer systemsAdminServer;
    private final SerializerController serializerController;

    public EcsEngineServer(ServerContext serverContext) throws IOException {
        logger.trace("debut de l'initialisation du ecs");
        this.serverContext = serverContext;
        Box2D.init();
        DataCtrl dataCtrl = new DataCtrl();
        physicWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0, 0), true);
        bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();
        serializerController = new SerializerController(this);

        systemsAdminServer = new SystemsAdminServer();
        WorldConfiguration worldConfiguration = new WorldConfigurationBuilder()
                .dependsOn(RealmTechCorePlugin.class)
                .with(systemsAdminServer)
                .build();

        worldConfiguration.register("serverContext", serverContext);
        worldConfiguration.register("physicWorld", physicWorld);
        worldConfiguration.register(bodyDef);
        worldConfiguration.register(fixtureDef);
        worldConfiguration.register(dataCtrl);
        worldConfiguration.register(systemsAdminServer);
        worldConfiguration.register("itemManager", systemsAdminServer.itemManagerServer);
        worldConfiguration.register("systemsAdmin", systemsAdminServer);
        worldConfiguration.register(serializerController);
        this.world = new World(worldConfiguration);
        logger.trace("fin de l'initialisation du ecs");
    }

    public void process(float deltaTime) {
        long t1 = System.currentTimeMillis();

        List<Runnable> copyPre;
        synchronized (nextTickServerPre) {
            copyPre = List.copyOf(nextTickServerPre);
            nextTickServerPre.clear();
        }

        List<Runnable> schedule;
        synchronized (nextTickSchedule) {
            List<Runnable> runnableSchedule = nextTickSchedule.get(tickCount);
            if (runnableSchedule != null) {
                schedule = runnableSchedule;
                nextTickSchedule.put(tickCount, null);
            } else {
                schedule = List.of();
            }
        }

        List<Runnable> listToRun = new ArrayList<>();
        listToRun.addAll(copyPre);
        listToRun.addAll(schedule);

        processNextTickRunnable(listToRun);
        world.setDelta(deltaTime);
        world.process();
        ++tickCount;

        long t2 = System.currentTimeMillis();
        serverContext.getServerHandler().broadCastPacket(new TickBeatPacket((t2 - t1) / 1000f));
    }

    public void prepareSaveToLoad(String saveName) throws IOException {
        logger.info("Chargement de la carte \"{}\"", saveName);
        int mapId = world.getSystem(SaveInfManager.class).generateOrLoadSave(saveName);
        world.getSystem(TagManager.class).register("infMap", mapId);
        logger.info("La carte \"{}\" a été chargée", saveName);
    }

    public void saveMap() throws IOException {
        int infMap = world.getSystem(TagManager.class).getEntityId("infMap");
        world.getSystem(SaveInfManager.class).saveInfMap(infMap);
        logger.info("carte sauvegarde");
    }
    public World getWorld() {
        return world;
    }

    public void processNextTickRunnable(List<Runnable> runnables) {
        runnables.forEach(Runnable::run);
    }

    public void nextTick(Runnable runnable) {
        synchronized (nextTickServerPre) {
            nextTickServerPre.add(runnable);
        }
    }

    public void nextTickSchedule(int tickFutureCount, Runnable runnable) {
        long futureTick = tickCount + tickFutureCount;
        synchronized (nextTickSchedule) {
            List<Runnable> runnableSchedule = nextTickSchedule.get(futureTick);
            if (runnableSchedule == null) {
                nextTickSchedule.put(futureTick, new ArrayList<>(List.of(runnable)));
            } else {
                runnableSchedule.add(runnable);
            }
        }
    }

    public Entity getMapEntity() {
        return world.getSystem(TagManager.class).getEntity("infMap");
    }

    public SystemsAdminServer getSystemsAdminServer() {
        return systemsAdminServer;
    }

    public SerializerController getSerializerController() {
        return serializerController;
    }
}

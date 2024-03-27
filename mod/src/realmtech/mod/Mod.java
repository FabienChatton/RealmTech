package realmtech.mod;

import ch.realmtech.server.ecs.Context;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.ecs.system.TimeSystem;
import ch.realmtech.server.mod.AssetsProvider;
import ch.realmtech.server.mod.EvaluateAfter;
import ch.realmtech.server.mod.ModInitializer;
import ch.realmtech.server.registry.Entry;
import ch.realmtech.server.registry.InvalideEvaluate;
import ch.realmtech.server.registry.Registry;
import ch.realmtech.server.registry.RegistryUtils;
import com.artemis.BaseSystem;

@AssetsProvider
public class Mod implements ModInitializer {

    @Override
    public String getModId() {
        return "test";
    }

    @Override
    public void initializeModRegistry(Registry<?> modRegistry, Context context) {
        Registry<Entry> customSystems = Registry.createRegistry(modRegistry, "customSystems");
        customSystems.addEntry(new Entry("CustomSystem") {
            @Override
            @EvaluateAfter("#systems")
            public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
                SystemsAdminServer systemsAdminServer = RegistryUtils.evaluateSafe(rootRegistry, SystemsAdminServer.class);
                systemsAdminServer.putCustomSystem(new CustomTimeSystem());

                systemsAdminServer.putCustomSystem(new AddCustomSystem());
            }
        });

        Registry<Entry> testCustomSystem = Registry.createRegistry(modRegistry, "testCustomSystem");
        testCustomSystem.addEntry(new TestCustomSystem());
    }

    static class CustomTimeSystem extends TimeSystem {
        @Override
        public float getAccumulatedDelta() {
            return 69;
        }
    }

    static class AddCustomSystem extends BaseSystem {

        @Override
        protected void processSystem() {
            System.out.println("nice");
        }
    }

    static class TestCustomSystem extends Entry {

        public TestCustomSystem() {
            super("TestCustomSystem");
        }

        @Override
        @EvaluateAfter("#customSystems")
        public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
            SystemsAdminServer systemsAdminServer = RegistryUtils.evaluateSafe(rootRegistry, SystemsAdminServer.class);
            System.out.println(systemsAdminServer.getCustomSystem(TimeSystem.class).getAccumulatedDelta());
        }
    }
}

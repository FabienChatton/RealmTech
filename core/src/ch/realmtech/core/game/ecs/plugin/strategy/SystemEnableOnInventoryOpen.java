package ch.realmtech.core.game.ecs.plugin.strategy;

import com.artemis.BaseSystem;
import com.artemis.World;
import com.badlogic.gdx.InputProcessor;

import java.util.ArrayList;
import java.util.List;

public class SystemEnableOnInventoryOpen {

    private final List<Class<? extends BaseSystem>> inGameSystem;

    public SystemEnableOnInventoryOpen(List<Class<? extends BaseSystem>> inGameSystem) {
        this.inGameSystem = inGameSystem;
    }

    public void initialize(World world) {
        onInventoryClose(world);
    }

    public List<InputProcessor> onInventoryOpen(World world) {
        List<InputProcessor> inputProcessors = new ArrayList<>();
        for (Class<? extends BaseSystem> inGameSystem : inGameSystem) {
            BaseSystem system = world.getSystem(inGameSystem);
            system.setEnabled(true);
            if (system instanceof OnPlayerInventoryOpenGetInputProcessor systemWithInputProcessor) {
                inputProcessors.addAll(systemWithInputProcessor.getInputProcessors());
            }
        }
        return inputProcessors;
    }

    public void onInventoryClose(World world) {
        for (Class<? extends BaseSystem> inGameSystem : inGameSystem) {
            BaseSystem system = world.getSystem(inGameSystem);
            system.setEnabled(false);
        }
    }
}

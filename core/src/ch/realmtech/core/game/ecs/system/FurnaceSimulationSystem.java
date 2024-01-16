package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.game.ecs.component.FurnaceExtraInfoComponent;
import ch.realmtech.server.ecs.component.FurnaceComponent;
import ch.realmtech.server.ecs.plugin.SystemTickEmulation;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;

@SystemTickEmulation
@All({FurnaceComponent.class, FurnaceExtraInfoComponent.class})
public class FurnaceSimulationSystem extends IteratingSystem {
    private ComponentMapper<FurnaceComponent> mFurnace;
    private ComponentMapper<FurnaceExtraInfoComponent> mFurnaceExtraInfo;
    @Override
    protected void process(int entityId) {
        FurnaceComponent furnaceComponent = mFurnace.get(entityId);
        FurnaceExtraInfoComponent furnaceExtraInfoComponent = mFurnaceExtraInfo.get(entityId);

        if (furnaceComponent.remainingTickToBurn > 0) {
            furnaceComponent.remainingTickToBurn--;
        }

        if (furnaceComponent.remainingTickToBurn > 0) {
            if (furnaceComponent.tickProcess < furnaceExtraInfoComponent.lastTickProcessFull) {
                furnaceComponent.tickProcess++;
            } else {
                furnaceComponent.tickProcess = 0;
            }
        }
    }
}

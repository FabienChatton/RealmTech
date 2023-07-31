package ch.realmtech.game.level.cell;

import ch.realmtech.game.ecs.component.ItemComponent;
import ch.realmtech.game.ecs.component.PlayerComponent;
import com.artemis.World;

public interface BreakCell {
    boolean breakCell(World world, int chunkId, int cellId, ItemComponent itemUseByPlayer, PlayerComponent playerComponent);
}

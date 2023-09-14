package ch.realmtechCommuns.level.cell;


import ch.realmtechCommuns.ecs.component.ItemComponent;
import ch.realmtechCommuns.ecs.component.PlayerComponent;
import com.artemis.World;

public interface BreakCell {
    boolean breakCell(World world, int chunkId, int cellId, ItemComponent itemUseByPlayer, PlayerComponent playerComponent);
}

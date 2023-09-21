package ch.realmtechServer.level.cell;


import ch.realmtechServer.ecs.component.ItemComponent;
import ch.realmtechServer.ecs.component.PlayerComponent;
import com.artemis.World;

public interface BreakCell {
    boolean breakCell(World world, int chunkId, int cellId, ItemComponent itemUseByPlayer, PlayerComponent playerComponent);
}

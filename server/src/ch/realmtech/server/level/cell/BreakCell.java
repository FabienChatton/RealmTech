package ch.realmtech.server.level.cell;


import ch.realmtech.server.registry.ItemEntry;
import com.artemis.World;
import com.badlogic.gdx.utils.Null;

public interface BreakCell {
    boolean breakCell(CellManager cellManager, World world, int chunkId, int cellId, @Null ItemEntry itemUseByPlayer, int playerSrc);
}

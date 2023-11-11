package ch.realmtech.server.level.cell;


import ch.realmtech.server.registery.ItemRegisterEntry;
import com.artemis.World;

public interface BreakCell {
    boolean breakCell(CellManager cellManager, World world, int chunkId, int cellId, ItemRegisterEntry itemUseByPlayer);
}

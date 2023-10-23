package ch.realmtechServer.level.cell;


import ch.realmtechServer.registery.ItemRegisterEntry;
import com.artemis.World;

public interface BreakCell {
    boolean breakCell(CellManager cellManager, World world, int chunkId, int cellId, ItemRegisterEntry itemUseByPlayer);
}

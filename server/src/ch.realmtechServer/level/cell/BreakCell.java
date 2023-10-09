package ch.realmtechServer.level.cell;


import ch.realmtechServer.ctrl.ItemManager;
import ch.realmtechServer.registery.ItemRegisterEntry;
import com.artemis.World;

public interface BreakCell {
    boolean breakCell(ItemManager itemManager, World world, int chunkId, int cellId, ItemRegisterEntry itemUseByPlayer);
}

package ch.realmtech.server.serialize.cell;

import ch.realmtech.server.registery.CellRegisterEntry;
import com.artemis.World;
import com.badlogic.gdx.utils.Null;

import java.util.function.BiConsumer;

public final class CellArgs {
    private final CellRegisterEntry cellRegisterEntry;
    private final byte innerChunk;
    @Null
    private final BiConsumer<World, Integer> overrideEdit;

    public CellArgs(CellRegisterEntry cellRegisterEntry, byte innerChunk, BiConsumer<World, Integer> overrideEdit) {
        this.cellRegisterEntry = cellRegisterEntry;
        this.innerChunk = innerChunk;
        this.overrideEdit = overrideEdit;
    }

    public CellArgs(CellRegisterEntry cellRegisterEntry, byte innerChunk) {
        this(cellRegisterEntry, innerChunk, null);
    }

    public CellRegisterEntry getCellRegisterEntry() {
        return cellRegisterEntry;
    }

    public byte getInnerChunk() {
        return innerChunk;
    }

    public BiConsumer<World, Integer> getOverrideEdit() {
        return overrideEdit;
    }
}

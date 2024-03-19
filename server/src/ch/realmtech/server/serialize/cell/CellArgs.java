package ch.realmtech.server.serialize.cell;

import ch.realmtech.server.level.cell.EditEntityCreate;
import ch.realmtech.server.registry.CellEntry;
import com.badlogic.gdx.utils.Null;

import java.util.Optional;

public final class CellArgs {
    private final CellEntry cellRegisterEntry;
    private final byte innerChunk;
    @Null
    private final EditEntityCreate editEntityArgs;

    public CellArgs(CellEntry cellRegisterEntry, byte innerChunk, EditEntityCreate editEntityArgs) {
        this.cellRegisterEntry = cellRegisterEntry;
        this.innerChunk = innerChunk;
        this.editEntityArgs = editEntityArgs;
    }

    public CellArgs(CellEntry cellRegisterEntry, byte innerChunk) {
        this(cellRegisterEntry, innerChunk, null);
    }

    public CellEntry getCellRegisterEntry() {
        return cellRegisterEntry;
    }

    public byte getInnerChunk() {
        return innerChunk;
    }

    public Optional<EditEntityCreate> getEditEntityArgs() {
        return Optional.ofNullable(editEntityArgs);
    }
}

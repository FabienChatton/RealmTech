package ch.realmtech.server.serialize.cell;

import ch.realmtech.server.level.cell.EditEntityCreate;
import ch.realmtech.server.newRegistry.NewCellEntry;
import com.badlogic.gdx.utils.Null;

import java.util.Optional;

public final class CellArgs {
    private final NewCellEntry cellRegisterEntry;
    private final byte innerChunk;
    @Null
    private final EditEntityCreate editEntityArgs;

    public CellArgs(NewCellEntry cellRegisterEntry, byte innerChunk, EditEntityCreate editEntityArgs) {
        this.cellRegisterEntry = cellRegisterEntry;
        this.innerChunk = innerChunk;
        this.editEntityArgs = editEntityArgs;
    }

    public CellArgs(NewCellEntry cellRegisterEntry, byte innerChunk) {
        this(cellRegisterEntry, innerChunk, null);
    }

    public NewCellEntry getCellRegisterEntry() {
        return cellRegisterEntry;
    }

    public byte getInnerChunk() {
        return innerChunk;
    }

    public Optional<EditEntityCreate> getEditEntityArgs() {
        return Optional.ofNullable(editEntityArgs);
    }
}

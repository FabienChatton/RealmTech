package ch.realmtech.server.serialize.cell;

import ch.realmtech.server.level.cell.EditEntity;
import ch.realmtech.server.registery.CellRegisterEntry;
import com.badlogic.gdx.utils.Null;

import java.util.Optional;

public final class CellArgs {
    private final CellRegisterEntry cellRegisterEntry;
    private final byte innerChunk;
    @Null
    private final EditEntity editEntityArgs;

    public CellArgs(CellRegisterEntry cellRegisterEntry, byte innerChunk, EditEntity editEntityArgs) {
        this.cellRegisterEntry = cellRegisterEntry;
        this.innerChunk = innerChunk;
        this.editEntityArgs = editEntityArgs;
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
    public Optional<EditEntity> getEditEntityArgs() {
        return Optional.ofNullable(editEntityArgs);
    }
}

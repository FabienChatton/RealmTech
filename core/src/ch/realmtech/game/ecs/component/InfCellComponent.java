package ch.realmtech.game.ecs.component;

import ch.realmtech.game.registery.CellRegisterEntry;
import com.artemis.PooledComponent;

public class InfCellComponent extends PooledComponent {
    public byte innerPosX;
    public byte innerPosY;
    public CellRegisterEntry cellRegisterEntry;

    public InfCellComponent set(byte innerPosX, byte innerPosY, CellRegisterEntry cellRegisterEntry) {
        this.innerPosX = innerPosX;
        this.innerPosY = innerPosY;
        this.cellRegisterEntry = cellRegisterEntry;
        return this;
    }

    @Override
    protected void reset() {
        innerPosX = 0;
        innerPosY = 0;
    }
}

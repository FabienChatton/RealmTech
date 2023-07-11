package ch.realmtech.game.clickAndDrop;

import ch.realmtech.game.ecs.component.ItemComponent;
import ch.realmtech.game.ecs.system.InventoryManager;
import ch.realmtech.game.registery.ItemRegisterEntry;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Null;

public class ClickAndDropActor extends Actor {
    private final int[] stack;
    private final BitmapFont bitmapFont;
    private final ComponentMapper<ItemComponent> mItem;
    @Null
    private final Table tableImage;

    public ClickAndDropActor(int[] stack, ComponentMapper<ItemComponent> mItem, Table tableImage) {
        this.stack = stack;
        this.mItem = mItem;
        bitmapFont = new BitmapFont();
        this.tableImage = tableImage;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (stack[0] != 0) {
            if (getWidth() == 0) {
                final ItemRegisterEntry itemRegisterEntry = mItem.get(stack[0]).itemRegisterEntry;
                setWidth(itemRegisterEntry.getTextureRegion().getRegionWidth());
                setHeight(itemRegisterEntry.getTextureRegion().getRegionHeight());
            }
        }
        if (tableImage != null) {
            setPosition(tableImage.getX() + 358, tableImage.getY() + 155); // TODO trouver un fois à quoi correspondent ces valeurs magiques
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (stack[0] != 0) {
            final ItemRegisterEntry itemRegisterEntry = mItem.get(stack[0]).itemRegisterEntry;
            batch.draw(mItem.get(stack[0]).itemRegisterEntry.getTextureRegion(), getX(), getY());
            bitmapFont.draw(batch, Integer.toString(InventoryManager.tailleStack(stack)), getX(), getY() + itemRegisterEntry.getTextureRegion().getRegionHeight());
        }
    }

    public int[] getStack() {
        return stack;
    }
}

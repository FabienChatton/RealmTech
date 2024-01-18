package ch.realmtech.core.game.clickAndDrop;

import ch.realmtech.core.RealmTech;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.plugin.SystemsAdminClientForClient;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.registery.ItemRegisterEntry;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Null;

import java.util.function.BiPredicate;

public class ClickAndDropActor extends Actor {
    private final int[] stack;
    private final BitmapFont bitmapFont;
    private final ComponentMapper<ItemComponent> mItem;
    @Null
    private final Table tableImage;
    private final RealmTech context;
    private final BiPredicate<SystemsAdminClientForClient, ItemRegisterEntry> dstRequirePredicate;

    public ClickAndDropActor(RealmTech context, int[] stack, ComponentMapper<ItemComponent> mItem, Table tableImage, BiPredicate<SystemsAdminClientForClient, ItemRegisterEntry> dstRequirePredicate) {
        this.stack = stack;
        this.mItem = mItem;
        bitmapFont = new BitmapFont();
        this.tableImage = tableImage;
        this.context = context;
        this.dstRequirePredicate = dstRequirePredicate;
    }

    public BiPredicate<SystemsAdminClientForClient, ItemRegisterEntry> getDstRequirePredicate() {
        return dstRequirePredicate;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (stack[0] != 0) {
            if (getWidth() == 0) {
                final ItemRegisterEntry itemRegisterEntry = mItem.get(stack[0]).itemRegisterEntry;
                setWidth(itemRegisterEntry.getTextureRegion(context.getTextureAtlas()).getRegionWidth());
                setHeight(itemRegisterEntry.getTextureRegion(context.getTextureAtlas()).getRegionHeight());
            }
        }
        if (tableImage != null) {
            final Vector2 vector2 = tableImage.localToStageCoordinates(new Vector2(tableImage.getX(), tableImage.getY()));
            final float x = vector2.x - tableImage.getX();
            final float y = vector2.y - tableImage.getY();
            setPosition(x, y);
            setZIndex(1);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (stack[0] != 0) {
            final ItemRegisterEntry itemRegisterEntry = mItem.get(stack[0]).itemRegisterEntry;
            batch.draw(itemRegisterEntry.getTextureRegion(context.getTextureAtlas()), getX(), getY());
            if (!itemRegisterEntry.getItemBehavior().isIcon()) {
                bitmapFont.draw(batch, Integer.toString(InventoryManager.tailleStack(stack)), getX(), getY() + itemRegisterEntry.getTextureRegion(context.getTextureAtlas()).getRegionHeight());
            }
        }
    }

    public int[] getStack() {
        return stack;
    }
}

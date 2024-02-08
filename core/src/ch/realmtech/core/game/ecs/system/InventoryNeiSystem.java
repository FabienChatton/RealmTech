package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.plugin.strategy.OnPlayerInventoryOpenGetInputProcessor;
import ch.realmtech.core.helper.ButtonsMenu;
import ch.realmtech.core.helper.OnClick;
import ch.realmtech.server.mod.RealmTechCoreMod;
import ch.realmtech.server.registery.ItemRegisterEntry;
import ch.realmtech.server.registery.RegistryEntry;
import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import java.util.ArrayList;
import java.util.List;

public class InventoryNeiSystem extends BaseSystem implements OnPlayerInventoryOpenGetInputProcessor {
    @Wire(name = "context")
    private RealmTech context;

    private Stage inventoryNeiStage;
    private Batch inventoryNeiBatch;

    private int neiItemPageIndex;
    private int neiItemPageMaxIndex;

    @Override
    protected void initialize() {
        super.initialize();
        inventoryNeiBatch = new SpriteBatch();
        inventoryNeiStage = new Stage(context.getUiStage().getViewport(), inventoryNeiBatch);

        Table neiRootTable = new Table(context.getSkin());
        neiRootTable.setFillParent(true);
        neiRootTable.right();
        inventoryNeiStage.addActor(neiRootTable);

        Table neiItemsTable = new Table(context.getSkin());
        ArrayList<Table> pages = addActorItemToNeiTable();
        neiItemPageIndex = 0;
        neiItemPageMaxIndex = pages.size();
        neiItemsTable.add(pages.get(neiItemPageIndex));
        neiRootTable.add(neiItemsTable).expandY().top();
        neiRootTable.row();

        Table neiFooterTable = new Table(context.getSkin());
        Table neiFooterButton = new Table(context.getSkin());
        TextButton neiItemNextPage = new ButtonsMenu.TextButtonMenu(context, ">", new OnClick((event, x, y) -> {
            if (neiItemPageIndex < neiItemPageMaxIndex - 1) {
                neiItemsTable.clear();
                neiItemsTable.add(pages.get(++neiItemPageIndex));
            }
        }));
        TextButton neiItemBackPage = new ButtonsMenu.TextButtonMenu(context, "<", new OnClick((event, x, y) -> {
            if (neiItemPageIndex > 0) {
                neiItemsTable.clear();
                neiItemsTable.add(pages.get(--neiItemPageIndex));
            }
        }));
        neiFooterButton.add(neiItemBackPage).expandX().left();
        neiFooterButton.add(neiItemNextPage).expandX().right();
        neiFooterTable.add(neiFooterButton).expandX().fillX();
        neiRootTable.add(neiFooterTable).fillX().bottom();
        neiRootTable.row();
    }

    private ArrayList<Table> addActorItemToNeiTable() {
        List<RegistryEntry<ItemRegisterEntry>> items = RealmTechCoreMod.ITEMS.getEnfants();
        int itemPerRow = 4;
        int itemPerColum = 16;
        int maxWidth = 32 * itemPerRow;
        int maxHeight = 32 * itemPerColum;
        int pagesLength = Math.max(1, items.size() / (itemPerRow * itemPerColum));
        int width = 0;
        int itemIndex = 0;
        ArrayList<Table> pages = new ArrayList<>(pagesLength);
        for (int i = 0; i < pagesLength; i++) {
            Table page = new Table(context.getSkin());
            pages.add(page);
            for (int height = 0; itemIndex < items.size() && height < maxHeight; itemIndex++) {
                RegistryEntry<ItemRegisterEntry> item = items.get(itemIndex);
                Image actorImage = new Image(item.getEntry().getTextureRegion(context.getTextureAtlas()));
                actorImage.setSize(32, 32);
                if (width < maxWidth) {
                    width += 32;
                } else {
                    page.row();
                    width = 0;
                    height += 32;
                }
                page.add(actorImage);
            }
        }
        return pages;
    }

    @Override
    protected void processSystem() {
        inventoryNeiStage.draw();
    }

    @Override
    protected void dispose() {
        super.dispose();
        inventoryNeiBatch.dispose();
        inventoryNeiStage.dispose();
    }

    @Override
    public List<InputProcessor> getInputProcessors() {
        return List.of(inventoryNeiStage);
    }
}

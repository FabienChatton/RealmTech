package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.InfCellComponent;
import ch.realmtech.game.ecs.component.InfChunkComponent;
import ch.realmtech.game.ecs.component.ItemComponent;
import ch.realmtech.game.item.ItemType;
import ch.realmtech.game.registery.CellRegisterEntry;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class CellHoverEtWailaSystem extends BaseSystem {
    private final static String TAG = CellHoverEtWailaSystem.class.getSimpleName();
    @Wire(name = "context")
    private RealmTech context;
    private Table wailaStageTable;
    private Window wailaWindow;
    private Stage wailaStage;
    private Label wailaCellInfoHash;
    private Label wailaCellInfoCanBreak;
    private Image wailaCellImage;
    private ComponentMapper<InfCellComponent> mCell;
    private ComponentMapper<InfChunkComponent> mChunk;
    private ComponentMapper<ItemComponent> mItem;

    @Override
    protected void initialize() {
        super.initialize();
        wailaStage = new Stage(context.getUiStage().getViewport(), context.getUiStage().getBatch());
        wailaStageTable = new Table(context.getSkin());
        wailaStage.addActor(wailaStageTable);
        wailaStageTable.setFillParent(true);
        wailaWindow = new Window("", context.getSkin());
        wailaStageTable.add(wailaWindow).expandY().top();
        wailaCellImage = new Image();
        wailaWindow.getTitleTable().add(wailaCellImage).right();
        wailaWindow.getTitleTable().getChildren().reverse();
        wailaCellInfoHash = new Label(null, context.getSkin());
        wailaCellInfoCanBreak = new Label(null, context.getSkin());
        wailaCellInfoHash.setFontScale(0.5f);
        wailaCellInfoCanBreak.setFontScale(0.5f);
        wailaWindow.add(wailaCellInfoHash).row();
        wailaWindow.add(wailaCellInfoCanBreak);

    }

    @Override
    protected void begin() {
        super.begin();
        context.getGameStage().getBatch().begin();
    }

    @Override
    protected void end() {
        super.end();
        context.getGameStage().getBatch().end();
    }

    @Override
    protected void processSystem() {
        // cell hover
        Vector2 screenCoordinate = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        int chunk = MapSystem.getChunk(context, screenCoordinate);
        if (chunk == -1) return;
        int cell = MapSystem.getTopCell(context, chunk, screenCoordinate);
        if (cell == -1) return;
        InfChunkComponent infChunkComponent = mChunk.get(chunk);
        InfCellComponent infCellComponent = mCell.get(cell);
        TextureAtlas.AtlasRegion region = context.getTextureAtlas().findRegion("cellOver-01");
        context.getGameStage().getBatch().draw(region,
                MapSystem.getWorldPos(infChunkComponent.chunkPosX, infCellComponent.getInnerPosX()),
                MapSystem.getWorldPos(infChunkComponent.chunkPosY, infCellComponent.getInnerPosY()),
                RealmTech.PPM * RealmTech.UNITE_SCALE / 2,
                RealmTech.PPM * RealmTech.UNITE_SCALE / 2
        );

        // waila
        wailaWindow.getTitleLabel().setText(infCellComponent.cellRegisterEntry.toString());
        wailaCellImage.setDrawable(new TextureRegionDrawable(infCellComponent.cellRegisterEntry.getTextureRegion(context)));
        wailaCellInfoHash.setText("ID: " + CellRegisterEntry.hashString(infCellComponent.cellRegisterEntry.toString()));

        ItemType curentItemType;
        if (!mItem.has(world.getSystem(ItemBarManager.class).getSelectItem())) {
            curentItemType = ItemType.TOUS;
        } else {
            ItemComponent curentItemComponent = mItem.get(world.getSystem(ItemBarManager.class).getSelectItem());
            curentItemType = curentItemComponent.itemRegisterEntry.getItemBehavior().getItemType();
        }
        ItemType itemTypeToMine;
        if (infCellComponent.cellRegisterEntry.getCellBehavior().getBreakWith() != null) {
            itemTypeToMine = infCellComponent.cellRegisterEntry.getCellBehavior().getBreakWith();
        } else {
            itemTypeToMine = ItemType.RIEN;
        }
        if (curentItemType == itemTypeToMine || infCellComponent.cellRegisterEntry.getCellBehavior().getBreakWith() == ItemType.TOUS) {
            wailaCellInfoCanBreak.setColor(Color.GREEN);
        } else {
            wailaCellInfoCanBreak.setColor(Color.RED);
        }
        if (itemTypeToMine != null) {
            wailaCellInfoCanBreak.setText("break with: " + itemTypeToMine.toString().toLowerCase());
        }

        wailaStage.draw();
    }
}

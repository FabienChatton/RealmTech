package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.server.ecs.component.CellComponent;
import ch.realmtech.server.ecs.component.InfChunkComponent;
import ch.realmtech.server.ecs.component.InfMapComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.registery.CellRegisterEntry;
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
    @Wire(name = "context")
    private RealmTech context;
    @Wire
    private SystemsAdminClient systemsAdminClient;
    private TextureAtlas textureAtlas;
    private Table wailaStageTable;
    private Window wailaWindow;
    private Stage wailaStage;
    private Label wailaCellInfoHash;
    private Label wailaCellInfoCanBreak;
    private Image wailaCellImage;
    private ComponentMapper<CellComponent> mCell;
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
        wailaStage.draw();
    }

    @Override
    protected void processSystem() {
        // cell hover
        int[] infChunks = context.getEcsEngine().getMapEntity().getComponent(InfMapComponent.class).infChunks;
        Vector2 screenCoordinate = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        Vector2 gameCoordinate = context.getEcsEngine().getGameCoordinate(screenCoordinate);
        int worldPosX = MapManager.getWorldPos(gameCoordinate.x);
        int worldPosY = MapManager.getWorldPos(gameCoordinate.y);

        int chunk = systemsAdminClient.mapManager.getChunk(MapManager.getChunkPos(worldPosX), MapManager.getChunkPos(worldPosY), infChunks);
        if (chunk == -1) return;
        int topCell = systemsAdminClient.mapManager.getTopCell(chunk, MapManager.getInnerChunk(worldPosX), MapManager.getInnerChunk(worldPosY));
        if (topCell == -1) return;
        InfChunkComponent infChunkComponent = mChunk.get(chunk);
        CellComponent cellComponent = mCell.get(topCell);
        TextureAtlas.AtlasRegion region = context.getTextureAtlas().findRegion("cellOver-01");
        context.getGameStage().getBatch().draw(
                region,
                worldPosX,
                worldPosY,
                RealmTech.PPM * RealmTech.UNITE_SCALE / 2,
                RealmTech.PPM * RealmTech.UNITE_SCALE / 2
        );

        // waila
        wailaWindow.getTitleLabel().setText(cellComponent.cellRegisterEntry.toString());
        wailaCellImage.setDrawable(new TextureRegionDrawable(cellComponent.cellRegisterEntry.getTextureRegion(textureAtlas)));
        wailaCellInfoHash.setText("Id: " + CellRegisterEntry.hashString(cellComponent.cellRegisterEntry.toString()));

        ItemType curentItemType;
        if (!mItem.has(systemsAdminClient.getItemBarManager().getSelectItem())) {
            curentItemType = ItemType.TOUS;
        } else {
            ItemComponent curentItemComponent = mItem.get(systemsAdminClient.getItemBarManager().getSelectItem());
            curentItemType = curentItemComponent.itemRegisterEntry.getItemBehavior().getItemType();
        }
        ItemType itemTypeToMine;
        if (cellComponent.cellRegisterEntry.getCellBehavior().getBreakWith() != null) {
            itemTypeToMine = cellComponent.cellRegisterEntry.getCellBehavior().getBreakWith();
        } else {
            itemTypeToMine = ItemType.RIEN;
        }
        if (curentItemType == itemTypeToMine || cellComponent.cellRegisterEntry.getCellBehavior().getBreakWith() == ItemType.TOUS) {
            wailaCellInfoCanBreak.setColor(Color.GREEN);
        } else {
            wailaCellInfoCanBreak.setColor(Color.RED);
        }
        if (itemTypeToMine != null) {
            wailaCellInfoCanBreak.setText("break with: " + itemTypeToMine.toString().toLowerCase());
        }
    }
}

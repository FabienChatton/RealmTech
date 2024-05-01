package ch.realmtech.core.screen;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.helper.Popup;
import ch.realmtech.core.input.InputMapper;
import ch.realmtech.core.screen.uiComponent.ConsoleUi;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.packet.serverPacket.GetPlayerInventorySessionPacket;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GameScreen extends AbstractScreen {
    private final static Logger logger = LoggerFactory.getLogger(GameScreen.class);

    private final Box2DDebugRenderer box2DDebugRenderer;
    private final Table debugTable;
    private final Label fpsLabel;
    private final Label gameCoo;
    private final Label pointerGameCoo;
    private final Label chunkPos;
    private final Label innerChunk;
    private final Label tps;
    private final Label reciveDataSize;
    private final Label sendDataSize;
    private final Label topCellId;
    private final Label versionLabel;
    private ConsoleUi consoleUi;

    public GameScreen(RealmTech context) throws IOException {
        super(context);
        box2DDebugRenderer = new Box2DDebugRenderer();
        debugTable = new Table(skin);
        fpsLabel = new Label("", skin);
        gameCoo = new Label("", skin);
        pointerGameCoo = new Label("", skin);
        chunkPos = new Label("", skin);
        innerChunk = new Label("", skin);
        tps = new Label(null, skin);
        reciveDataSize = new Label(null, skin);
        sendDataSize = new Label(null, skin);
        topCellId = new Label(null, skin);
        versionLabel = new Label("Version : " + RealmTech.REALMTECH_VERSION, skin);
        consoleUi = new ConsoleUi(skin, context);

        debugTable.add(fpsLabel).left().row();
        debugTable.add(versionLabel).left().row();
        debugTable.add(gameCoo).left().row();
        debugTable.add(pointerGameCoo).left().row();
        debugTable.add(chunkPos).left().row();
        debugTable.add(innerChunk).left().row();
        debugTable.add(tps).left().row();
        debugTable.add(topCellId).left().row();
        debugTable.add(reciveDataSize).left().row();
        debugTable.add(sendDataSize).left().row();
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(context.getInputManager());
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            context.getSystemsAdminClient().getPlayerInventorySystem().closePlayerInventory();
            context.setScreen(ScreenType.GAME_PAUSE);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            if (uiTable.getChildren().contains(debugTable, true)) {
                uiTable.clear();
                uiStage.setDebugAll(false);
            } else {
                uiTable.add(debugTable).expand().left().top();
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                    uiStage.setDebugAll(true);
                }
            }
        }
        // open inventory
        if (Gdx.input.isKeyJustPressed(InputMapper.openInventory.getKey()) && canInteractWithWorld()) {
            if (!context.getSystemsAdminClient().getPlayerInventorySystem().isEnabled()) {
                context.getClientConnexion().sendAndFlushPacketToServer(new GetPlayerInventorySessionPacket());
                context.getSystemsAdminClient().getPlayerInventorySystem().openPlayerInventory(context.getSystemsAdminClient().getPlayerInventorySystem().getDisplayInventoryPlayer());
            } else {
                context.getSystemsAdminClient().getPlayerInventorySystem().closePlayerInventory();
            }
        }

        if (Gdx.input.isKeyJustPressed(InputMapper.openQuest.getKey())) {
            if (!canInteractWithWorld()) {
                return;
            }
            
            if (context.getSystemsAdminClient().getQuestPlayerSystem().isEnabled()) {
                context.getSystemsAdminClient().getQuestPlayerSystem().closeQuest();
            } else {
                context.getSystemsAdminClient().getQuestPlayerSystem().openQuest();
            }
        }


        if (Gdx.input.isKeyJustPressed(Input.Keys.GRAVE)) {
            if (consoleUi.getConsoleWindow().getParent() == null && canInteractWithWorld()) {
                uiStage.addActor(consoleUi.getConsoleWindow());
                Gdx.input.setInputProcessor(uiStage);
            } else {
                consoleUi.getConsoleWindow().remove();
                Gdx.input.setInputProcessor(context.getInputManager());
            }
        }

//        if (Gdx.input.isKeyJustPressed(context.getOption().keyDropItem.get())) {
//            context.getEcsEngine().dropCurentPlayerItem();
//        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1))
            context.getSystemsAdminClient().getItemBarSystem().setSlotSelected((byte) 0);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2))
            context.getSystemsAdminClient().getItemBarSystem().setSlotSelected((byte) 1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3))
            context.getSystemsAdminClient().getItemBarSystem().setSlotSelected((byte) 2);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4))
            context.getSystemsAdminClient().getItemBarSystem().setSlotSelected((byte) 3);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5))
            context.getSystemsAdminClient().getItemBarSystem().setSlotSelected((byte) 4);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6))
            context.getSystemsAdminClient().getItemBarSystem().setSlotSelected((byte) 5);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7))
            context.getSystemsAdminClient().getItemBarSystem().setSlotSelected((byte) 6);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8))
            context.getSystemsAdminClient().getItemBarSystem().setSlotSelected((byte) 7);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9))
            context.getSystemsAdminClient().getItemBarSystem().setSlotSelected((byte) 8);
        if (Gdx.input.isKeyJustPressed(Input.Keys.PAGE_UP))
            context.getSystemsAdminClient().getItemBarSystem().slotSelectedUp();
        if (Gdx.input.isKeyJustPressed(Input.Keys.PAGE_DOWN))
            context.getSystemsAdminClient().getItemBarSystem().slotSelectedDown();

        context.getWorldOr((ecsEngine) -> context.nextFrame(() -> {
            try {
                PositionComponent positionComponent = ecsEngine.getWorld().getMapper(PositionComponent.class).get(context.getSystemsAdminClient().getPlayerManagerClient().getMainPlayer());
                Vector2 screenCoordinate = new Vector2(Gdx.input.getX(), Gdx.input.getY());
                Vector2 pointerGameCoordinate = context.getEcsEngine().getGameCoordinate(screenCoordinate);
                int worldPosX = MapManager.getWorldPos(positionComponent.x);
                int worldPosY = MapManager.getWorldPos(positionComponent.y);
                int worldPosXPointer = MapManager.getWorldPos(pointerGameCoordinate.x);
                int worldPosYPointer = MapManager.getWorldPos(pointerGameCoordinate.y);
                int chunkId = context.getSystemsAdminClient().getMapManager().getChunkByWorldPos(worldPosX, worldPosY, context.getSystemsAdminClient().getMapManager().getInfMap().infChunks);
                fpsLabel.setText(String.format("FPS : %d", Gdx.graphics.getFramesPerSecond()));
                gameCoo.setText(String.format("WorldPosX : %d\nWorldPosY : %d", worldPosX, worldPosY));
                pointerGameCoo.setText(String.format("PointerGameX: %f\nPointerGameY: %f", pointerGameCoordinate.x, pointerGameCoordinate.y));
                chunkPos.setText(String.format("Chunk Pos X : %d\nChunk Pos Y : %d", MapManager.getChunkPos(worldPosX), MapManager.getChunkPos(worldPosY)));
                innerChunk.setText(String.format("Inner X : %d\nInner Y : %d", MapManager.getInnerChunk(worldPosX), MapManager.getInnerChunk(worldPosY)));
                tps.setText(String.format("TPS : %d ", context.getEcsEngine().serverTickBeatMonitoring.getTickBeatPerSeconde()));
                topCellId.setText(String.format("Cell Id: %d", chunkId != -1 ? context.getSystemsAdminClient().getMapManager().getTopCell(chunkId, MapManager.getInnerChunk(worldPosXPointer), MapManager.getInnerChunk(worldPosYPointer)) : -1));
                reciveDataSize.setText(String.format("RDS(o) : %d", context.getEcsEngine().serverTickBeatMonitoring.getPacketDataReciveSizePerSeconde()));
                sendDataSize.setText(String.format("SDS(o) : %d", context.getEcsEngine().serverTickBeatMonitoring.getPacketDataSendPerSeconde()));
            } catch (Exception e) {
                logger.warn("Can not render debug: {}", e.getMessage(), e);
            }
        }));
    }

    @Override
    public void draw() {
        try {
            context.process(Gdx.graphics.getDeltaTime());
            uiStage.draw();
            if (uiStage.isDebugAll()) {
                box2DDebugRenderer.render(context.getEcsEngine().physicWorld, gameCamera.combined);
            }
        } catch (Exception e) {
            context.getEcsEngine().dispose();
            logger.error(e.getMessage(), e);
            context.setScreen(ScreenType.MENU);
            Popup.popupErreur(context, e.getMessage(), context.getUiStage());
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        box2DDebugRenderer.dispose();
    }

    public void writeToConsole(String s) {
        consoleUi.writeToConsole(s);
    }

    public boolean canInteractWithWorld() {
        return consoleUi.getConsoleWindow().getParent() == null && !context.getSystemsAdminClient().getQuestPlayerSystem().isEnabled();
    }
}

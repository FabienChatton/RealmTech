package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.server.registry.QuestCategory;
import ch.realmtech.server.registry.QuestEntry;
import ch.realmtech.server.registry.RegistryUtils;
import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

import java.util.List;

public class QuestSystem extends BaseSystem {
    @Wire(name = "context")
    private RealmTech context;

    private Window questWindow;
    private ScrollPane questTitleScroll;
    private Table questContent;
    private QuestEntry selectedQuestOld = null;
    private Stage questStage;
    private Image questStageImage;
    private FrameBuffer fbo;

    @Override
    protected void initialize() {
        super.initialize();
        setEnabled(false);

        questStage = new Stage();
        questStage.setDebugAll(true);
        Table rootTable = new Table();
        rootTable.setFillParent(true);

        Table table = new Table();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        table.add(new Label("bonjour bonjour bonjour bonjour bonjour bonjour", context.getSkin())).row();
        ScrollPane questItemScrollPane = new ScrollPane(table);
        rootTable.add(questItemScrollPane);
        questStage.addActor(rootTable);

        questWindow = new Window("Quest", context.getSkin());
        questWindow.setFillParent(true);

        Table questTitleScrollTable = new Table(context.getSkin());
        List<? extends QuestCategory> questCategories = RegistryUtils.findEntries(context.getRootRegistry(), "#questsCategory");
        for (QuestCategory questEntry : questCategories) {
            TextButton questTitleButton = new TextButton(questEntry.getDisplayTitle(), context.getSkin());
            questTitleButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    //setSelectedQuest(quest);
                    return true;
                }
            });
            questTitleScrollTable.add(questTitleButton).padBottom(10f).left().top();
            questTitleScrollTable.row();
        }

        questTitleScroll = new ScrollPane(questTitleScrollTable);
        questWindow.add(questTitleScroll).left().top();

        questContent = new Table(context.getSkin());
        questWindow.add(questContent).fill().expand();

        questStageImage = new Image();
        questContent.add(questStageImage);

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    }

    @Override
    protected void processSystem() {
        fbo.begin();
        Gdx.gl.glClearColor(1, 1, 1, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        questStage.getViewport().setScreenPosition(10, 10);
        questStage.act(Gdx.graphics.getDeltaTime());
        questStage.draw();
        fbo.end();
        TextureRegion textureRegion = new TextureRegion(fbo.getColorBufferTexture());
        textureRegion.flip(false, true);
        questStageImage.setDrawable(new TextureRegionDrawable(textureRegion));
    }

    public void setSelectedQuest(QuestEntry selectedQuest) {
        questContent.clear();

        if (selectedQuestOld == null || selectedQuestOld != selectedQuest) {
            Table questContentTable = new Table(context.getSkin());
            questContentTable.add(new Label(selectedQuest.getTitle(), context.getSkin())).expandX().center();
            questContentTable.row();

            TypingLabel contentLabel = new TypingLabel(selectedQuest.getContent(), context.getSkin());
            contentLabel.setWrap(true);

            ScrollPane contentScrollPane = new ScrollPane(contentLabel);
            questContentTable.add(contentScrollPane).expand().fillX().left().top();
            questContent.add(questContentTable).expand().fill().left().top();
            selectedQuestOld = selectedQuest;
        } else {
            selectedQuestOld = null;
        }
    }

    public void openQuest() {
//         Gdx.input.setInputProcessor(context.getUiStage());
        Gdx.input.setInputProcessor(questStage);
        context.getUiStage().addActor(questWindow);
        setEnabled(true);
    }

    public void closeQuest() {
        Gdx.input.setInputProcessor(context.getInputManager());
        setEnabled(false);
        questWindow.remove();
    }
}

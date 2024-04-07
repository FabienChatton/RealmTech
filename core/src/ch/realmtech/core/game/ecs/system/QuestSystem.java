package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.server.registry.QuestCategory;
import ch.realmtech.server.registry.QuestEntry;
import ch.realmtech.server.registry.RegistryUtils;
import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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
    private InputMultiplexer questInputMultiplexer;

    @Override
    protected void initialize() {
        super.initialize();
        setEnabled(false);
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        questStage = new Stage();

        questWindow = new Window("Quest", context.getSkin());
        questWindow.setFillParent(true);

        Table questTitleScrollTable = new Table(context.getSkin());
        List<? extends QuestCategory> questCategories = RegistryUtils.findEntries(context.getRootRegistry(), "#questsCategory");
        for (QuestCategory questCategory : questCategories) {
            TextButton questTitleButton = new TextButton(questCategory.getDisplayTitle(), context.getSkin());
            questTitleButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    setSelectedQuestCategory(questCategory);
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
        questInputMultiplexer = new InputMultiplexer(context.getUiStage(), questStage);
    }

    @Override
    protected void processSystem() {
        context.getUiStage().setDebugAll(true);
        questStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        questStage.act(Gdx.graphics.getDeltaTime());
        fbo.begin();
        Gdx.gl.glClearColor(1, 1, 1, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        questStage.draw();
        fbo.end();
        TextureRegion textureRegion = new TextureRegion(fbo.getColorBufferTexture());
        textureRegion.flip(false, true);
        questStageImage.setDrawable(new TextureRegionDrawable(textureRegion));
    }

    private void setSelectedQuestCategory(QuestCategory questCategory) {
        questContent.clear();
        questContent.add(questStageImage).fill();
        Table rootTable = new Table();
        rootTable.setFillParent(true);

        Table table = new Table();
        for (QuestEntry questEntry : questCategory.getQuestInThisCategory()) {
            Label questLabel = new Label(questEntry.getTitle(), context.getSkin());
            questLabel.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    setSelectedQuest(questEntry);
                }
            });
            table.add(questLabel).row();
        }
        ScrollPane questItemScrollPane = new ScrollPane(table);
        rootTable.add(questItemScrollPane);
        questStage.addActor(rootTable);

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
        Gdx.input.setInputProcessor(questInputMultiplexer);
        context.getUiStage().addActor(questWindow);
        setEnabled(true);
    }

    public void closeQuest() {
        Gdx.input.setInputProcessor(context.getInputManager());
        setEnabled(false);
        questWindow.remove();

    }
}

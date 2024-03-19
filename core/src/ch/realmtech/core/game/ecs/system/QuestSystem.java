package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.server.newRegistry.NewEntry;
import ch.realmtech.server.newRegistry.NewQuestEntry;
import ch.realmtech.server.newRegistry.RegistryUtils;
import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

import java.util.List;

public class QuestSystem extends BaseSystem {
    @Wire(name = "context")
    private RealmTech context;

    private Window questWindow;
    private ScrollPane questTitleScroll;
    private Table questContent;
    private NewQuestEntry selectedQuestOld = null;

    @Override
    protected void initialize() {
        super.initialize();
        setEnabled(false);
        questWindow = new Window("Quest", context.getSkin());
        questWindow.setFillParent(true);

        Table questTitleScrollTable = new Table(context.getSkin());
        List<? extends NewEntry> questEntries = RegistryUtils.findEntries(context.getRootRegistry(), "#quests");
        for (NewEntry questEntry : questEntries) {
            NewQuestEntry quest = (NewQuestEntry) questEntry;
            TextButton questTitleButton = new TextButton(quest.getTitle(), context.getSkin());
            questTitleButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    setSelectedQuest(quest);
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
    }

    @Override
    protected void processSystem() {

    }

    public void setSelectedQuest(NewQuestEntry selectedQuest) {
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
        Gdx.input.setInputProcessor(context.getUiStage());
        context.getUiStage().addActor(questWindow);
        setEnabled(true);
    }

    public void closeQuest() {
        Gdx.input.setInputProcessor(context.getInputManager());
        setEnabled(false);
        questWindow.remove();
    }
}

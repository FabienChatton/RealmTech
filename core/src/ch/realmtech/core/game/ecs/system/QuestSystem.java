package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.server.registry.QuestCategory;
import ch.realmtech.server.registry.QuestEntry;
import ch.realmtech.server.registry.RegistryUtils;
import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

import java.util.List;

public class QuestSystem extends BaseSystem {
    @Wire(name = "context")
    private RealmTech context;

    private Window questWindow;
    private QuestEntry selectedQuestOld = null;

    @Override
    protected void initialize() {
        super.initialize();
        setEnabled(false);

        questWindow = new Window("Quest", context.getSkin());
        questWindow.setFillParent(true);
    }

    @Override
    protected void processSystem() {

    }

    private void setSelectedQuestListCategory() {
        questWindow.clear();

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

        ScrollPane questTitleScroll = new ScrollPane(questTitleScrollTable);
        questWindow.add(questTitleScroll).expand().left().top();
    }

    private void setSelectedQuestCategory(QuestCategory questCategory) {
        questWindow.clear();

        Table table = new Table();
        List<QuestEntry> questInThisCategory = questCategory.getQuestInThisCategory().stream().sorted(QuestSystem::sortPosQuestEntry).toList();
        Cell<Image> previousImageCell = null;
        for (int i = 0; i < questInThisCategory.size(); i++) {
            QuestEntry questEntry = questInThisCategory.get(i);
            Image questIcon = new Image(context.getTextureAtlas().findRegion(questEntry.getTextureRegionForIcon()));
            questIcon.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    setSelectedQuest(questEntry);
                }
            });
            Cell<Image> imageCell = table.add(questIcon).bottom().left();
            if (i > 0) {
                QuestEntry previousQuestEntry = questInThisCategory.get(i - 1);
                Vector2 pad = questEntry.getPos().sub(previousQuestEntry.getPos());
                imageCell.padLeft(pad.x - 32);
                imageCell.padBottom(pad.y + previousImageCell.getPadBottom());
            }
            previousImageCell = imageCell;
        }

        ScrollPane questItemScrollPane = new ScrollPane(table);
        questWindow.add(questItemScrollPane).expand().fill().row();

        TextButton listCategory = new TextButton("quest category", context.getSkin());
        listCategory.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                setSelectedQuestListCategory();
                return true;
            }
        });
        questWindow.add(listCategory);
    }

    public void setSelectedQuest(QuestEntry selectedQuest) {
        questWindow.clear();

        if (selectedQuestOld == null || selectedQuestOld != selectedQuest) {
            Table questContentTable = new Table(context.getSkin());
            questContentTable.add(new Label(selectedQuest.getTitle(), context.getSkin())).expandX().center();
            questContentTable.row();

            TypingLabel contentLabel = new TypingLabel(selectedQuest.getContent(), context.getSkin());
            contentLabel.setWrap(true);

            ScrollPane contentScrollPane = new ScrollPane(contentLabel);
            questContentTable.add(contentScrollPane).expand().fillX().left().top();
            questWindow.add(questContentTable).expand().fill().left().top();
            selectedQuestOld = selectedQuest;
        } else {
            selectedQuestOld = null;
        }
    }

    public void openQuest() {
        Gdx.input.setInputProcessor(context.getUiStage());
        context.getUiStage().addActor(questWindow);
        setSelectedQuestListCategory();
        setEnabled(true);
    }

    public void closeQuest() {
        Gdx.input.setInputProcessor(context.getInputManager());
        setEnabled(false);
        questWindow.remove();

    }

    private static int sortPosQuestEntry(QuestEntry q1, QuestEntry q2) {
        int compare = 0;
        compare = Double.compare(q1.getPos().x, q2.getPos().x);
        if (compare == 0) {
            compare = Double.compare(q1.getPos().y, q2.getPos().y);
        }
        return compare;
    }
}

package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.core.helper.OnClick;
import ch.realmtech.core.helper.Popup;
import ch.realmtech.server.ecs.component.QuestPlayerPropertyComponent;
import ch.realmtech.server.mod.quests.QuestManagerEntry;
import ch.realmtech.server.packet.serverPacket.QuestCheckboxCompletedPacket;
import ch.realmtech.server.registry.QuestCategory;
import ch.realmtech.server.registry.QuestEntry;
import ch.realmtech.server.registry.RegistryUtils;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static ch.realmtech.core.helper.ButtonsMenu.TextButtonMenu;

public class QuestPlayerSystem extends BaseSystem {
    private final static Logger logger = LoggerFactory.getLogger(QuestPlayerSystem.class);
    @Wire(name = "context")
    private RealmTech context;
    @Wire
    private SystemsAdminClient systemsAdminClient;

    private Window questWindow;
    private QuestCategory selectedCategoryOld = null;
    private QuestEntry questOver;
    private Window questOverTitle;
    private QuestManagerEntry questManagerEntry;
    private ComponentMapper<QuestPlayerPropertyComponent> mQuestPlayerProperty;

    @Override
    protected void initialize() {
        super.initialize();
        setEnabled(false);

        questWindow = new Window("Quest", context.getSkin());
        questWindow.setFillParent(true);
        questOverTitle = new Window("", context.getSkin());
        questOverTitle.setHeight(0);
        questManagerEntry = RegistryUtils.findEntryOrThrow(context.getRootRegistry(), QuestManagerEntry.class);
    }

    @Override
    protected void processSystem() {
        if (questOver != null) {
            if (!context.getUiStage().getActors().contains(questOverTitle, true)) {
                context.getUiStage().addActor(questOverTitle);
            }
            questOverTitle.setPosition(Gdx.input.getX() - questOverTitle.getTitleLabel().getWidth() / 2f, Gdx.graphics.getHeight() - Gdx.input.getY() + questOverTitle.getTitleLabel().getHeight());
            questOverTitle.getTitleLabel().setText(questOver.getTitle());
        } else {
            if (context.getUiStage().getActors().contains(questOverTitle, true)) {
                questOverTitle.remove();
            }
        }
    }

    private void setSelectedQuestListCategory() {
        questWindow.clear();

        Table questTitleScrollTable = new Table(context.getSkin());
        for (QuestCategory questCategory : questManagerEntry.getQuestCategories()) {
            TextButtonMenu questTitleButton = new TextButtonMenu(context, questCategory.getDisplayTitle(), new OnClick((event, x, y) -> setSelectedQuestCategory(questCategory)));
            questTitleScrollTable.add(questTitleButton).padBottom(10f).left().top();
            questTitleScrollTable.row();
        }

        ScrollPane questTitleScroll = new ScrollPane(questTitleScrollTable);
        questWindow.add(questTitleScroll).expand().left().top();
    }

    private void setSelectedQuestCategory(QuestCategory questCategory) {
        questWindow.clear();

        Table table = new Table();
        List<QuestEntry> questInThisCategory = questCategory.getQuestInThisCategory().stream().sorted(QuestPlayerSystem::sortPosQuestEntry).toList();
        Cell<Image> previousImageCell = null;
        for (int i = 0; i < questInThisCategory.size(); i++) {
            QuestEntry questEntry = questInThisCategory.get(i);
            Image questIcon = new Image(context.getTextureAtlas().findRegion(questEntry.getTextureRegionForIcon()));
            questIcon.addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    super.enter(event, x, y, pointer, fromActor);
                    questOver = questEntry;
                    float width = Popup.getWidth(context, questEntry.getTitle());
                    questOverTitle.setWidth(width);
                    questOverTitle.getTitleLabel().setWidth(width);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    super.exit(event, x, y, pointer, toActor);
                    questOver = null;
                }

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

        TextButton listCategory = new TextButtonMenu(context, "quest category", new OnClick((event, x, y) -> setSelectedQuestListCategory()));
        questWindow.add(listCategory);
        selectedCategoryOld = questCategory;
    }

    public void setSelectedQuest(QuestEntry selectedQuest) {
        questWindow.clear();

        Table questContentTable = new Table(context.getSkin());
        questContentTable.add(new Label(selectedQuest.getTitle(), context.getSkin())).expandX().center();
        questContentTable.row();

        TypingLabel contentLabel = new TypingLabel(selectedQuest.getContent(), context.getSkin());
        contentLabel.setWrap(true);

        ScrollPane contentScrollPane = new ScrollPane(contentLabel);
        questContentTable.add(contentScrollPane).expand().fillX().left().top();
        questWindow.add(questContentTable).expand().fill().left().top().row();

        CheckBox isCompletedCheckBox = new CheckBox("isCompleted", context.getSkin());
        int mainPlayerId = systemsAdminClient.getPlayerManagerClient().getMainPlayer();
        if (mQuestPlayerProperty.has(mainPlayerId)) {
            QuestPlayerPropertyComponent questPlayerPropertyComponent = mQuestPlayerProperty.get(mainPlayerId);
            questPlayerPropertyComponent.findQuestPlayerProperty(selectedQuest).ifPresentOrElse((questPlayerProperty) -> {
                        if (questPlayerProperty.isCompleted()) {
                            isCompletedCheckBox.setChecked(true);
                            isCompletedCheckBox.setDisabled(true);
                        }
                    },
                    () -> logger.info("Can not find quest property: {}", selectedQuest));
        }

        isCompletedCheckBox.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!isCompletedCheckBox.isChecked()) {
                    context.sendRequest(new QuestCheckboxCompletedPacket(selectedQuest));
                    isCompletedCheckBox.setChecked(true);
                    // attends la réponse pour compléter dans la property
                    return true;
                } else {
                    return false;
                }
            }
        });
        questWindow.add(isCompletedCheckBox).padRight(10);
        questWindow.add(new TextButtonMenu(context, "Back", new OnClick((event, x, y) -> setSelectedQuestCategory(selectedCategoryOld)))).bottom();
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

package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.quest.QuestTitleTable;
import ch.realmtech.server.mod.RealmTechCoreMod;
import ch.realmtech.server.registery.QuestEntry;
import ch.realmtech.server.registery.RegistryEntry;
import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class QuestSystem extends BaseSystem {
    @Wire(name = "context")
    private RealmTech context;

    private Window questWindow;
    private ScrollPane questTitleScroll;

    @Override
    protected void initialize() {
        super.initialize();
        setEnabled(false);
        questWindow = new Window("Quest", context.getSkin());
        questWindow.setFillParent(true);

        Table questTitleScrollTable = new Table(context.getSkin());
        for (RegistryEntry<QuestEntry> questEntry : RealmTechCoreMod.QUESTS.getEnfants()) {
            questTitleScrollTable.add(new QuestTitleTable(context, questEntry)).top();
            questTitleScrollTable.row();
        }

        questTitleScroll = new ScrollPane(questTitleScrollTable);
        questWindow.add(questTitleScroll).left().top();
    }

    @Override
    protected void processSystem() {

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

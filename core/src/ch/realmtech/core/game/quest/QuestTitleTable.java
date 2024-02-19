package ch.realmtech.core.game.quest;

import ch.realmtech.core.RealmTech;
import ch.realmtech.server.registery.QuestEntry;
import ch.realmtech.server.registery.RegistryEntry;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class QuestTitleTable extends Table {
    private final RegistryEntry<QuestEntry> questEntry;

    public QuestTitleTable(RealmTech context, RegistryEntry<QuestEntry> questEntry) {
        this.questEntry = questEntry;
        add(new Label(questEntry.getEntry().getTitle(), context.getSkin()));
    }

    public RegistryEntry<QuestEntry> getQuestEntry() {
        return questEntry;
    }
}

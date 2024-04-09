package ch.realmtech.server.registry;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public abstract class QuestEntry extends Entry {
    private final String title;
    private final String content;
    private final String category;
    private final Vector2 pos;
    private List<QuestEntry> dependQuests;

    public QuestEntry(String name, String category, String title, String content) {
        super(name);
        this.title = title;
        this.content = content;
        this.category = category;
        pos = new Vector2(0, 0);
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        List<QuestEntry> dependQuests = new ArrayList<>();
        for (String dependQuestFqrn : getDependQuestsFqrn()) {
            QuestEntry dependQuestEntry = RegistryUtils.evaluateSafe(rootRegistry, dependQuestFqrn, QuestEntry.class);
            dependQuests.add(dependQuestEntry);
        }
        this.dependQuests = dependQuests;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getCategory() {
        return category;
    }

    public String getTextureRegionForIcon() {
        return "default-texture";
    }

    protected List<String> getDependQuestsFqrn() {
        return List.of();
    }

    protected void setPos(float x, float y) {
        pos.set(x, y);
    }

    public Vector2 getPos() {
        return pos.cpy();
    }

    public List<QuestEntry> getDependQuests() {
        return dependQuests;
    }
}

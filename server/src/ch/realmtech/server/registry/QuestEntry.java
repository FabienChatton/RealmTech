package ch.realmtech.server.registry;

import ch.realmtech.server.mod.questsValidator.ItemInInventoryValidator;
import ch.realmtech.server.mod.questsValidator.QuestEntryValidator;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class QuestEntry extends Entry {
    private final String title;
    private final String content;
    private final String category;
    private final Vector2 pos;
    private List<QuestEntry> dependQuests;
    private String[] dependQuestsFqrn;
    private List<QuestEntryValidator> questEntryValidators;
    private String[] requireItemsFqrn;

    public QuestEntry(String name, String category, String title, String content) {
        super(name);
        this.title = title;
        this.content = content;
        this.category = category;
        pos = new Vector2(0, 0);
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        if (dependQuestsFqrn != null) {
            List<QuestEntry> dependQuests = new ArrayList<>(dependQuestsFqrn.length);
            for (String dependQuestFqrn : dependQuestsFqrn) {
                QuestEntry dependQuestEntry = RegistryUtils.evaluateSafe(rootRegistry, dependQuestFqrn, QuestEntry.class);
                dependQuests.add(dependQuestEntry);
            }
            this.dependQuests = dependQuests;
        } else {
            dependQuests = Collections.emptyList();
        }

        if (requireItemsFqrn != null) {
            questEntryValidators = new ArrayList<>(requireItemsFqrn.length);
            for (String requireItem : requireItemsFqrn) {
                ItemInInventoryValidator itemInInventoryValidator = new ItemInInventoryValidator(requireItem);
                itemInInventoryValidator.evaluate(rootRegistry);
                questEntryValidators.add(itemInInventoryValidator);
            }
        } else {
            questEntryValidators = Collections.emptyList();
        }
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

    protected void setPos(float x, float y) {
        pos.set(x, y);
    }

    protected void setDependQuestsFqrn(String... dependQuestsFqrn) {
        this.dependQuestsFqrn = dependQuestsFqrn;
    }

    protected void setRequireItemsFqrn(String... requireItemsFqrn) {
        this.requireItemsFqrn = requireItemsFqrn;
    }

    public Vector2 getPos() {
        return pos.cpy();
    }

    public List<QuestEntry> getDependQuests() {
        return dependQuests;
    }

    public List<QuestEntryValidator> getQuestEntryValidators() {
        return questEntryValidators;
    }
}

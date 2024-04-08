package ch.realmtech.server.registry;

public abstract class QuestEntry extends Entry {
    private final String title;
    private final String content;
    private final String category;

    public QuestEntry(String name, String category, String title, String content) {
        super(name);
        this.title = title;
        this.content = content;
        this.category = category;
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {

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
}

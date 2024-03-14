package ch.realmtech.server.newRegistry;

public abstract class NewQuestEntry extends NewEntry {
    private final String title;
    private final String content;

    public NewQuestEntry(String name, String title, String content) {
        super(name);
        this.title = title;
        this.content = content;
    }

    @Override
    public void evaluate(NewRegistry<?> rootRegistry) throws InvalideEvaluate {

    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}

package ch.realmtech.server.ecs.plugin.commun;

public enum ContextType {
    CLIENT("core"),
    SERVER("server");
    private final String packageName;

    ContextType(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }
}

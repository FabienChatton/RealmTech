package ch.realmtech.server.newRegistry;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class NewEntry {
    private final String name;
    private NewRegistry<?> parentRegistry;

    public NewEntry(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    void setParentRegistry(final NewRegistry<?> parentRegistry) {
        this.parentRegistry = parentRegistry;
    }

    public String toFqrn() {
        return parentRegistry.toFqrn() + "." + name;
    }

    public int getId() {
        return toFqrn().hashCode();
    }

    @Override
    public String toString() {
        return parentRegistry != null ? toFqrn() : name;
    }

    public abstract void evaluate(NewRegistry<?> rootRegistry) throws InvalideEvaluate;

    public abstract TextureRegion getTextureRegion(TextureAtlas textureAtlas);
}

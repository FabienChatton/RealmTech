package ch.realmtech.server.enemy;

import ch.realmtech.server.ecs.component.TextureAnimationComponent;
import ch.realmtech.server.ecs.component.TextureComponent;
import ch.realmtech.server.level.cell.EditEntity;
import com.artemis.EntityEdit;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class EnemySimpleTextureAnimated implements EnemyTexture {
    private final TextureRegionLazy textureRegionLazy;
    private final float scale;

    public EnemySimpleTextureAnimated(float scale, String... textureRegionNames) {
        this.scale = scale;
        textureRegionLazy = new TextureRegionLazy(textureRegionNames);
    }

    public EditEntity createTexture() {
        return EditEntity.create((executeOnContext, entityId) -> {
            executeOnContext.onClientWorld((clientForClient, world) -> {
                EntityEdit edit = world.edit(entityId);
                TextureAtlas textureAtlas = world.getRegistered(TextureAtlas.class);

                TextureComponent textureComponent = edit.create(TextureComponent.class);
                textureComponent.scale = scale;

                TextureAnimationComponent textureAnimationComponent = edit.create(TextureAnimationComponent.class);
                textureAnimationComponent.animationFront = textureRegionLazy.getTextureRegions(textureAtlas);
            });
        });
    }
}

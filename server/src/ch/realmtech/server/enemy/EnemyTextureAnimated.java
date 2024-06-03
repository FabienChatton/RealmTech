package ch.realmtech.server.enemy;

import ch.realmtech.server.ecs.component.TextureAnimationComponent;
import ch.realmtech.server.ecs.component.TextureComponent;
import ch.realmtech.server.level.cell.EditEntity;
import com.artemis.EntityEdit;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class EnemyTextureAnimated implements EnemyTexture {
    private final TextureRegionLazy textureRegionLazyFront;
    private final TextureRegionLazy textureRegionLazyBack;
    private final TextureRegionLazy textureRegionLazyLeft;
    private final TextureRegionLazy textureRegionLazyRight;
    private final float scale;

    public EnemyTextureAnimated(TextureRegionLazy textureRegionLazyFront, TextureRegionLazy textureRegionLazyBack, TextureRegionLazy textureRegionLazyLeft, TextureRegionLazy textureRegionLazyRight) {
        this(1, textureRegionLazyFront, textureRegionLazyBack, textureRegionLazyLeft, textureRegionLazyRight);
    }

    public EnemyTextureAnimated(float scale, TextureRegionLazy textureRegionLazyFront, TextureRegionLazy textureRegionLazyBack, TextureRegionLazy textureRegionLazyLeft, TextureRegionLazy textureRegionLazyRight) {
        this.textureRegionLazyFront = textureRegionLazyFront;
        this.textureRegionLazyBack = textureRegionLazyBack;
        this.textureRegionLazyLeft = textureRegionLazyLeft;
        this.textureRegionLazyRight = textureRegionLazyRight;
        this.scale = scale;
    }

    @Override
    public EditEntity createTexture() {
        return EditEntity.create((executeOnContext, entityId) -> {
            executeOnContext.onClientWorld((clientForClient, world) -> {
                EntityEdit edit = world.edit(entityId);
                TextureAtlas textureAtlas = world.getRegistered(TextureAtlas.class);

                TextureComponent textureComponent = edit.create(TextureComponent.class);
                textureComponent.scale = scale;

                TextureAnimationComponent textureAnimationComponent = edit.create(TextureAnimationComponent.class);
                textureAnimationComponent.animationFront = textureRegionLazyFront.getTextureRegions(textureAtlas);
                textureAnimationComponent.animationBack = textureRegionLazyBack.getTextureRegions(textureAtlas);
                textureAnimationComponent.animationLeft = textureRegionLazyLeft.getTextureRegions(textureAtlas);
                textureAnimationComponent.animationRight = textureRegionLazyRight.getTextureRegions(textureAtlas);
            });
        });
    }

}

package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.component.TextureComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;

@All({PositionComponent.class, TextureComponent.class})
public class TextureRenderer extends IteratingSystem {
    @Wire(name = "gameStage")
    private Stage gameStage;

    private ComponentMapper<TextureComponent> mTexture;
    private ComponentMapper<TextureImportant> mTextureImportant;
    private ComponentMapper<PositionComponent> mPosition;

    @Override
    protected void process(int entityId) {
        TextureComponent textureComponent = mTexture.create(entityId);
        if (textureComponent.texture != null) {
            PositionComponent positionComponent = mPosition.create(entityId);

            TextureRegion textureRegion;
            if (mTextureImportant.has(entityId)) {
                TextureImportant textureImportantComponent = mTextureImportant.get(entityId);
                TextureRegion textureImportant = textureImportantComponent.getTexture();
                if (textureImportant != null) {
                    textureRegion = textureImportant;
                } else {
                    textureRegion = textureComponent.texture;
                    mTextureImportant.remove(entityId);
                }
            } else {
                textureRegion = textureComponent.texture;
            }

            gameStage.getBatch().draw(
                    textureRegion,
                    positionComponent.x,
                    positionComponent.y,
                    textureComponent.texture.getRegionWidth() * RealmTech.UNITE_SCALE * textureComponent.scale,
                    textureComponent.texture.getRegionHeight() * RealmTech.UNITE_SCALE * textureComponent.scale
            );
        }
    }
}

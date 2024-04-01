package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.component.TextureColorComponent;
import ch.realmtech.core.game.ecs.component.TextureImportantComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.component.TextureComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;

@All({PositionComponent.class, TextureComponent.class})
public class TextureRenderer extends IteratingSystem {
    @Wire(name = "gameStage")
    private Stage gameStage;

    private ComponentMapper<TextureComponent> mTexture;
    private ComponentMapper<TextureImportantComponent> mTextureImportant;
    private ComponentMapper<PositionComponent> mPosition;
    private ComponentMapper<TextureColorComponent> mTextureColor;

    @Override
    protected void process(int entityId) {
        TextureComponent textureComponent = mTexture.create(entityId);
        if (textureComponent.texture != null) {
            PositionComponent positionComponent = mPosition.create(entityId);

            TextureRegion textureRegion;
            if (mTextureImportant.has(entityId)) {
                TextureImportantComponent textureImportantComponent = mTextureImportant.get(entityId);
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

            boolean hasColor = mTextureColor.has(entityId);
            if (hasColor) {
                gameStage.getBatch().setColor(mTextureColor.get(entityId).getColor());
            }

            gameStage.getBatch().draw(
                    textureRegion,
                    positionComponent.x,
                    positionComponent.y,
                    textureComponent.texture.getRegionWidth() * RealmTech.UNITE_SCALE * textureComponent.scale,
                    textureComponent.texture.getRegionHeight() * RealmTech.UNITE_SCALE * textureComponent.scale
            );

            if (hasColor) {
                gameStage.getBatch().setColor(Color.WHITE);
            }
        }
    }
}

package ch.realmtech.game.ecs.system;

import ch.realmtech.game.ecs.component.PositionComponent;
import ch.realmtech.game.ecs.component.TextureComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.Stage;

@All({PositionComponent.class, TextureComponent.class})
public class TextureRenderer extends IteratingSystem {
    @Wire(name = "gameStage")
    private Stage gameStage;

    private ComponentMapper<TextureComponent> mTexture;
    private ComponentMapper<PositionComponent> mPosition;

    @Override
    protected void begin() {
        gameStage.getBatch().setProjectionMatrix(gameStage.getCamera().combined);
        gameStage.getCamera().update();
        gameStage.getBatch().begin();
    }

    @Override
    protected void process(int entityId) {
        TextureComponent textureComponent = mTexture.create(entityId);
        if (textureComponent.texture != null) {
            float realWidth = ((float) textureComponent.texture.getRegionWidth()) * textureComponent.scale;
            float realHeight = ((float) textureComponent.texture.getRegionHeight()) * textureComponent.scale;
            PositionComponent positionComponent = mPosition.create(entityId);
            gameStage.getBatch().draw(
                    textureComponent.texture,
                    positionComponent.x,
                    positionComponent.y,
                    realWidth,
                    realHeight
            );
        }
    }

    @Override
    protected void end() {
        gameStage.getBatch().end();
    }
}

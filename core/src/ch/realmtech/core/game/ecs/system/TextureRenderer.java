package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.component.TextureComponent;
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
            PositionComponent positionComponent = mPosition.create(entityId);

            gameStage.getBatch().draw(
                    textureComponent.texture,
                    positionComponent.x,
                    positionComponent.y,
                    textureComponent.texture.getRegionWidth() * RealmTech.UNITE_SCALE * textureComponent.scale,
                    textureComponent.texture.getRegionHeight() * RealmTech.UNITE_SCALE * textureComponent.scale
            );
        }
    }

    @Override
    protected void end() {
        gameStage.getBatch().end();
    }
}

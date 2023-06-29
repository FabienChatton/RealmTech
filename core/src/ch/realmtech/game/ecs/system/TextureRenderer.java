package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.PositionComponent;
import ch.realmtech.game.ecs.component.TextureComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;

@All({PositionComponent.class, TextureComponent.class})
public class TextureRenderer extends IteratingSystem {
    @Wire(name = "context")
    RealmTech context;

    private ComponentMapper<TextureComponent> mTexture;
    private ComponentMapper<PositionComponent> mPosition;

    @Override
    protected void begin() {
        context.getGameStage().getBatch().setProjectionMatrix(context.getGameStage().getCamera().combined);
        context.getGameStage().getCamera().update();
        context.getGameStage().getBatch().begin();
    }

    @Override
    protected void process(int entityId) {
        TextureComponent textureComponent = mTexture.create(entityId);
        if (textureComponent.texture != null) {
            float realWidth = ((float) textureComponent.texture.getRegionWidth()) * textureComponent.scale;
            float realHeight = ((float) textureComponent.texture.getRegionHeight()) * textureComponent.scale;
            PositionComponent positionComponent = mPosition.create(entityId);
            context.getGameStage().getBatch().draw(
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
        context.getGameStage().getBatch().end();
    }
}

package ch.realmtech.game.ecs.system;

import ch.realmtech.game.ecs.component.PositionComponent;
import ch.realmtech.game.ecs.component.TextureComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.Stage;

import static ch.realmtech.RealmTech.PPM;

@All({PositionComponent.class, TextureComponent.class})
public class RendererTextureInGameSystem extends IteratingSystem {
    @Wire(name = "gameStage")
    Stage gameStage;

    ComponentMapper<TextureComponent> mTexture;
    ComponentMapper<PositionComponent> mPosition;

    @Override
    protected void process(int entityId) {
        TextureComponent textureComponent = mTexture.create(entityId);
        PositionComponent positionComponent = mPosition.create(entityId);
        gameStage.getCamera().update();
        gameStage.getBatch().setProjectionMatrix(gameStage.getCamera().combined);
        gameStage.getBatch().begin();
        gameStage.getBatch().draw(textureComponent.texture, positionComponent.x, positionComponent.y, textureComponent.texture.getRegionWidth() / PPM, textureComponent.texture.getRegionHeight() / PPM);
        gameStage.getBatch().end();
    }
}

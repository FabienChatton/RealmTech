package ch.realmtech.ecs.system;

import ch.realmtech.ecs.ECSEngine;
import ch.realmtech.ecs.component.PossitionComponent;
import ch.realmtech.ecs.component.TextureComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;

import static ch.realmtech.RealmTech.PPM;
public class RendererTextureInGameSystem extends IteratingSystem {
    private final Stage gameStage;
    public RendererTextureInGameSystem(Stage gameStage) {
        super(Family.all(PossitionComponent.class).all(TextureComponent.class).get(),10);
        this.gameStage = gameStage;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TextureComponent textureComponent = ECSEngine.TEXTURE_COMPONENT_MAPPER.get(entity);
        PossitionComponent possitionComponent = ECSEngine.POSSITION_COMPONENT_MAPPER.get(entity);
        gameStage.getCamera().update();
        gameStage.getBatch().setProjectionMatrix(gameStage.getCamera().combined);
        gameStage.getBatch().begin();
        gameStage.getBatch().draw(textureComponent.texture, possitionComponent.x, possitionComponent.y, textureComponent.texture.getWidth() / PPM, textureComponent.texture.getHeight() / PPM);
        gameStage.getBatch().end();
    }
}

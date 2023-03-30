package ch.realmtech.ecs.system;

import ch.realmtech.ecs.ECSEngine;
import ch.realmtech.ecs.component.PossitionComponent;
import ch.realmtech.ecs.component.TextureComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Batch;
import static ch.realmtech.RealmTech.PPM;
public class RendererTextureInGameSystem extends IteratingSystem {
    private final Batch batch;
    public RendererTextureInGameSystem(Batch batch) {
        super(Family.all(PossitionComponent.class).all(TextureComponent.class).get(),10);
        this.batch = batch;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TextureComponent textureComponent = ECSEngine.TEXTURE_COMPONENT_MAPPER.get(entity);
        PossitionComponent possitionComponent = ECSEngine.POSSITION_COMPONENT_MAPPER.get(entity);
        batch.begin();
        batch.draw(textureComponent.texture, possitionComponent.x, possitionComponent.y, textureComponent.texture.getWidth() / PPM, textureComponent.texture.getHeight() / PPM);
        batch.end();
    }
}

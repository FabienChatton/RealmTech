package ch.realmtech.ecs;

import ch.realmtech.RealmTech;
import ch.realmtech.ecs.component.MovementComponent;
import ch.realmtech.ecs.component.PlayerComponent;
import ch.realmtech.ecs.component.PossitionComponent;
import ch.realmtech.ecs.component.TextureComponent;
import ch.realmtech.ecs.system.PlayerMouvementSystem;
import ch.realmtech.ecs.system.RendererTextureInGameSystem;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public final class ECSEngine extends PooledEngine {
    public final static ComponentMapper<PossitionComponent> POSSITION_COMPONENT_MAPPER = ComponentMapper.getFor(PossitionComponent.class);
    public final static ComponentMapper<TextureComponent> TEXTURE_COMPONENT_MAPPER = ComponentMapper.getFor(TextureComponent.class);
    public final static ComponentMapper<PlayerComponent> PLAYER_COMPONENT_MAPPER = ComponentMapper.getFor(PlayerComponent.class);
    public final static ComponentMapper<MovementComponent> MOVEMENT_COMPONENT_MAPPER = ComponentMapper.getFor(MovementComponent.class);

    private final RealmTech context;
    public ECSEngine(final RealmTech context) {
        this.context = context;
        addSystem(new RendererTextureInGameSystem(context.getGameStage().getBatch()));
        addSystem(new PlayerMouvementSystem(context));
    }

    public void createPlayer(){
        Entity player = new Entity();
        player.add(new PlayerComponent());
        player.add(new MovementComponent(10));
        player.add(new PossitionComponent());
        player.add(new TextureComponent(new Texture(Gdx.files.internal("lain.png"))));
        addEntity(player);
    }
}

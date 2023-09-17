package ch.realmtech.game.ecs.system;

import ch.realmtechCommuns.ecs.component.PlayerComponent;
import ch.realmtechCommuns.ecs.component.TextureComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;

@All({TextureComponent.class, PlayerComponent.class})
public class PlayerTextureAnimated extends IteratingSystem {
    @Wire(name = "gameStage")
    private Stage gameStage;
    private ComponentMapper<TextureComponent> mTexture;
    private ComponentMapper<PlayerComponent> mPlayer;

    private void updateAnimation(PlayerComponent playerComponent, TextureComponent textureComponent) {
        final TextureRegion textureRegion;
        if (++playerComponent.animationIndex >= playerComponent.animationFront.length) {
            playerComponent.animationIndex = 1;
        }
        if (playerComponent.moveUp) {
            textureRegion = playerComponent.animationBack[playerComponent.animationIndex];
        } else if (playerComponent.moveLeft) {
            textureRegion = playerComponent.animationLeft[playerComponent.animationIndex];
        } else if (playerComponent.moveDown) {
            textureRegion = playerComponent.animationFront[playerComponent.animationIndex];
        } else if (playerComponent.moveRight) {
            textureRegion = playerComponent.animationRight[playerComponent.animationIndex];
        } else {
            textureRegion = playerComponent.animationFront[0];
        }
        textureComponent.texture = textureRegion;
    }

    @Override
    protected void process(int entityId) {
        PlayerComponent playerComponent = mPlayer.get(entityId);
        playerComponent.cooldown -= Gdx.graphics.getDeltaTime();
        if (playerComponent.cooldown <= 0) {
            TextureComponent textureComponent = mTexture.get(entityId);
            updateAnimation(playerComponent, textureComponent);
            playerComponent.cooldown = playerComponent.laps;
        }
    }
}

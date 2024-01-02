package ch.realmtech.core.game.ecs.system;

import ch.realmtech.server.ecs.component.PlayerComponent;
import ch.realmtech.server.ecs.component.TextureComponent;
import ch.realmtech.server.ecs.system.PlayerMouvementSystemServer;
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

    @Override
    protected void process(int entityId) {
        PlayerComponent playerComponent = mPlayer.get(entityId);
        byte movementCode = PlayerMouvementSystemServer.getKeysInputPlayerMouvement(playerComponent.moveLeft, playerComponent.moveDown, playerComponent.moveUp, playerComponent.moveRight);
        if (movementCode != playerComponent.lastDirection) {
            playerComponent.cooldown = 0;
            playerComponent.lastDirection = movementCode;
        } else {
            playerComponent.cooldown -= Gdx.graphics.getDeltaTime();
        }
        if (playerComponent.cooldown <= 0) {
            TextureComponent textureComponent = mTexture.get(entityId);
            updateAnimation(playerComponent, textureComponent);
            playerComponent.cooldown = playerComponent.laps;
        }
    }

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
}

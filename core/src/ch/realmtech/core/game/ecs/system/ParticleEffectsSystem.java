package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.server.ecs.plugin.forclient.ParticleEffectForClient;
import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class ParticleEffectsSystem extends BaseSystem implements ParticleEffectForClient {
    @Wire(name = "context")
    private RealmTech context;

    private ParticleEffectPool hitParticleEffectPool;
    private ParticleEffect hitParticleEffect;
    private Array<PooledEffect> activeParticleEffect;

    @Override
    protected void initialize() {
        activeParticleEffect = new Array<>();
        // hitParticleEffect.load();
        hitParticleEffect = new ParticleEffect();
        hitParticleEffectPool = new ParticleEffectPool(hitParticleEffect, 1, 10);

    }

    @Override
    protected void processSystem() {
        for (int i = 0; i < activeParticleEffect.size; i++) {
            PooledEffect particleEffect = activeParticleEffect.get(i);
            particleEffect.draw(context.getGameStage().getBatch());
            if (particleEffect.isComplete()) {
                particleEffect.free();
                activeParticleEffect.removeIndex(i);
            }
        }
    }

    @Override
    public void createHitEffect(Vector2 gameCoordinate) {
        PooledEffect newHitParticle = hitParticleEffectPool.obtain();
        newHitParticle.setPosition(gameCoordinate.x, gameCoordinate.y);
        activeParticleEffect.add(newHitParticle);
    }
}

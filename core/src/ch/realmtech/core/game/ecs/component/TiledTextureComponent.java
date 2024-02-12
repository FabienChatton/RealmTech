package ch.realmtech.core.game.ecs.component;

import com.artemis.PooledComponent;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TiledTextureComponent extends PooledComponent {
    private TextureRegion tiledTextureRegion;

    public TiledTextureComponent set(TextureRegion tiledTextureRegion) {
        this.tiledTextureRegion = tiledTextureRegion;
        return this;
    }


    public TextureRegion getTiledTextureRegion() {
        return tiledTextureRegion;
    }

    @Override
    protected void reset() {
        tiledTextureRegion = null;
    }
}

package ch.realmtech.core.shader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

public abstract class Shaders implements Disposable {

    public ShaderProgram shaderProgram;

    static {
        ShaderProgram.pedantic = false;
    }

    @Override
    public void dispose() {
        shaderProgram.dispose();
    }
}

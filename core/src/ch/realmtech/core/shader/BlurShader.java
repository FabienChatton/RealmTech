package ch.realmtech.core.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

public class BlurShader extends Shaders implements Disposable {
    public static final int FBO_SIZE = 1024;
    public static final float MAX_BLUR = 2f;

    public BlurShader() {
        ShaderProgram defaultShader = SpriteBatch.createDefaultShader();
        FileHandle frag = Gdx.files.local("shader/fragment-blur.glsl");
        shaderProgram = new ShaderProgram(defaultShader.getVertexShaderSource(), frag.readString());
        shaderProgram.bind();
        shaderProgram.setUniformf("resolution", FBO_SIZE);
        shaderProgram.setUniformf("radius", 1f);
    }

}

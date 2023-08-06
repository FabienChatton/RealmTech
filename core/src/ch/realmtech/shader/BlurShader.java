package ch.realmtech.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

public class BlurShader extends Shaders implements Disposable {
    public static final int FBO_SIZE = 1024;
    public static final float MAX_BLUR = 2f;

    public BlurShader() {
        FileHandle vert = Gdx.files.local("shader/vertex.glsl");
        FileHandle frag = Gdx.files.local("shader/fragment.glsl");
        shaderProgram = new ShaderProgram(vert, frag);
        shaderProgram.bind();
        shaderProgram.setUniformf("dir", 0f, 0f);
        shaderProgram.setUniformf("resolution", FBO_SIZE);
        shaderProgram.setUniformf("radius", 1f);
    }

}

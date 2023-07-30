package ch.realmtech.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

public class BlurShader implements Disposable {
    public ShaderProgram shaderProgram;

    public BlurShader() {
        FileHandle vert = Gdx.files.local("shader/vertex.glsl");
        FileHandle frag = Gdx.files.local("shader/fragment.glsl");
        shaderProgram = new ShaderProgram(vert, frag);
    }

    @Override
    public void dispose() {
        shaderProgram.dispose();
    }
}

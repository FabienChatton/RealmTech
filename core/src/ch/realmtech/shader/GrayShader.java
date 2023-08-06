package ch.realmtech.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class GrayShader extends Shaders {

    public GrayShader() {
        FileHandle vert = Gdx.files.local("shader/vertex.glsl");
        FileHandle frag = Gdx.files.local("shader/gray.glsl");
        shaderProgram = new ShaderProgram(vert, frag);
        shaderProgram.bind();
    }
}

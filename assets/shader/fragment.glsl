// https://www.shadertoy.com/view/Xltfzj
#ifdef GL_ES
#define PRECISION mediump
precision PRECISION float;
precision PRECISION int;
#else
#define PRECISION
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture;


void main()
{
    float Pi = 6.28318530718;// Pi*2

    // GAUSSIAN BLUR SETTINGS {{{
    float Directions = 16.0;// BLUR DIRECTIONS (Default 16.0 - More is better but slower)
    float Quality = 3.0;// BLUR QUALITY (Default 4.0 - More is better but slower)
    float Size = 8.0;// BLUR SIZE (Radius)
    // GAUSSIAN BLUR SETTINGS }}}

    vec2 Radius = Size/v_texCoords.xy;

    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv = v_texCoords;
    // Pixel colour
    vec4 Color = texture2D(u_texture, uv);

    // Blur calculations
    for (float d=0.0; d<Pi; d+=Pi/Directions)
    {
        for (float i=1.0/Quality; i<=1.0; i+=1.0/Quality)
        {
            Color += texture2D(u_texture, uv+vec2(cos(d), sin(d))*Radius*i);
        }
    }

    // Output to screen
    Color /= Quality * Directions - 15.0;
    gl_FragColor = Color;
}
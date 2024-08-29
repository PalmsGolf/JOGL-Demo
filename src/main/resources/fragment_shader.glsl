#version 330 core

in vec2 TexCoord;
out vec4 fragColor;

uniform sampler2D texture;
uniform float time;

void main() {
    vec2 waveTexCoord = TexCoord;
    waveTexCoord.x += sin(TexCoord.y * 10.0 + time * 2.0) * 0.05;

    // Wave effect
    fragColor = texture(texture, waveTexCoord);
}
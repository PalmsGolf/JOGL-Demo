#version 330 core

in vec2 TexCoord;
out vec4 fragColor;

uniform sampler2D texture;

void main() {
    vec2 waveTexCoord = TexCoord;
    fragColor = texture(texture, waveTexCoord);
}
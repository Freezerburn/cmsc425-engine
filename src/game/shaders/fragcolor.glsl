#version 330

smooth in vec4 theColor;

out vec4 outputColor;

uniform float fragLoopDuration;
uniform float time;

const vec4 firstColor = vec4(1.0f, 1.0f, 1.0f, 1.0f);
const vec4 secondColor = vec4(0.0f, 1.0f, 0.0f, 1.0f);

void main() {
    float lerpTime = time * 1.5f / fragLoopDuration;
    //outputColor = theColor;
    outputColor = mix(theColor,
        mix(firstColor, secondColor, sin(lerpTime)),
        sin(lerpTime));
}

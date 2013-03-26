#version 330

layout(location = 0) in vec4 position;
layout(location = 1) in vec4 color;

smooth out vec4 theColor;
uniform float loopDuration;
uniform float time;

uniform mat4 camera;
uniform mat4 projection;
uniform mat4 model;

uniform mat4 cameraToClipMatrix;

void main() {
    float timeScale = 3.14159f * 2.0f / loopDuration;
    vec4 totalOffset = vec4(
        cos(time * 1.5f * timeScale) * 0.5f,
        sin(time * 1.5f * timeScale) * 0.5f,
        0.0f,
        0.0f);
    //gl_Position = projection * camera * model * (position);
    gl_Position = cameraToClipMatrix * camera * model * (position);
    theColor = color;
}

#version 330

layout(location = 0) in vec3 position;
layout(location = 1) in vec4 color;
layout(location = 2) in vec3 vertNormal;

smooth out vec4 theColor;
in vec2 vertTexCoord;
out vec2 fragTexCoord;
out vec3 fragNormal;
//out vec3 fragVert2;
out vec3 fragPos;
out vec3 normal;

uniform float loopDuration;
uniform float time;

uniform mat4 camera;
uniform mat4 projection;
uniform mat4 preModel;
uniform mat4 model;

uniform mat4 cameraToClipMatrix;

void main() {
    float timeScale = 3.14159f * 2.0f / loopDuration;
    vec4 totalOffset = vec4(
        cos(time * 1.5f * timeScale) * 0.5f,
        sin(time * 1.5f * timeScale) * 0.5f,
        0.0f,
        0.0f);
    //gl_Position = projection * camera * (preModel * model) * (position);
    gl_Position = cameraToClipMatrix * camera * (preModel * model) * vec4(position, 1);
    theColor = color;
    fragTexCoord = vertTexCoord;
    fragNormal = vertNormal;

    //calculate normal in world coordinates
    mat3 normalMatrix = transpose(inverse(mat3(model)));
    normal = normalize(normalMatrix * vertNormal);

    fragPos = position;
    //calculate the location of this fragment (pixel) in world coordinates
    //fragVert2 = vec3(model * vec4(position, 1));
}

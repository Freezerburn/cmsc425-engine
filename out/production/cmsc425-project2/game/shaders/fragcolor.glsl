#version 330

smooth in vec4 theColor;

uniform sampler2D tex;
in vec2 fragTexCoord;
in vec3 fragNormal;
//in vec3 fragVert2;
in vec3 fragPos;
in vec3 normal;
out vec4 outputColor;

uniform float fragLoopDuration;
uniform float time;
uniform mat4 model;

uniform vec3 lightPosition;
uniform vec3 lightIntensities;

//const vec4 firstColor = vec4(1.0f, 1.0f, 1.0f, 1.0f);
//const vec4 secondColor = vec4(0.0f, 1.0f, 0.0f, 1.0f);

void main() {
    vec3 fragVert2 = vec3(model * vec4(fragPos, 1));

    // calculate the vector from this pixels surface to the light source
    vec3 surfaceToLight = lightPosition - fragVert2;
    //vec3 surfaceToLight = fragVert2 - lightPosition;

    // calculate the cosine of the angle of incidence (brightness)
    float brightness = dot(normal, surfaceToLight) / length(surfaceToLight);
    //float brightness = dot(normal, surfaceToLight);
    brightness = clamp(brightness, 0, 1);

    //float lerpTime = time * 1.5f / fragLoopDuration;
    //outputColor = theColor;
    vec4 materialAmbientColor = vec4(0.2, 0.2, 0.2, 1) * brightness;
    float distance = length(surfaceToLight);
    outputColor = materialAmbientColor + brightness * vec4(lightIntensities, 1) * texture(tex, fragTexCoord);
    //outputColor = texture(tex, fragTexCoord);
    //outputColor = mix(theColor,
        //mix(firstColor, secondColor, sin(lerpTime)),
        //sin(lerpTime));
}

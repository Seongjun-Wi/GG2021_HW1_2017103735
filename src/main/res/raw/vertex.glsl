#version 300 es
//P. vertex.glsl code 작성

uniform mat4 worldMat, viewMat, projMat;
uniform vec3 lightPos;

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec2 texCoord;

out vec3 v_normal;
out vec2 v_texCoord;
out vec3 v_lightDir;

void main() {
    gl_Position = vec4(position, 1.0);
}
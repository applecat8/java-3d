#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 vertexNormal;

out vec2 outTexCoord;
out vec3 mvVertexNormal;
out vec3 mvVertexPos;

uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;

void main()
{
    vec4 mvPos = modelViewMatrix*  vec4(position, 1.0);
    gl_Position = projectionMatrix * mvPos;
    outTexCoord = texCoord;
    // 因为我们确实希望法线被旋转和缩放，但我们不希望它被平移，所以我们只对它的方向感兴趣，而不是它的位置。
    // 这是通过将 w 分量设置为 0 来实现的，这是使用齐次坐标的优势之一，通过设置 w 分量，我们可以控制应用哪些变换。
    mvVertexNormal = normalize(modelViewMatrix * vec4(vertexNormal, 0.0)).xyz;
    mvVertexPos = mvPos.xyz;
}

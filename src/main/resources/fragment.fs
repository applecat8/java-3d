#version 330

in vec2 outTexCoord;
in vec3 mvVertexNormal;
in vec3 mvVertexPos;

out vec4 fragColor;

struct Attenuation
{
    float constant;
    float linear;
    float exponent;
};

struct PointLight
{
    vec3 colour; // 点光源的颜色
    // Light position is assumed to be in view coordinates
    vec3 position;
    float intensity; // 强度
    Attenuation att; // 电光源的衰减
};

struct Material //材质
{
    vec4 ambient; // 环境光
    vec4 diffuse; // 漫反射
    vec4 specular; // 镜面反射
    int hasTexture;
    float reflectance; // 反射率
};

uniform sampler2D texture_sampler;
uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform PointLight pointLight;
uniform vec3 camera_pos;

vec4 ambientC;
vec4 diffuseC;
vec4 speculrC;

void setupColours(Material material, vec2 textCoord)
{
    if (material.hasTexture == 1)
    {
        ambientC = texture(texture_sampler, textCoord);
        diffuseC = ambientC;
        speculrC = ambientC;
    }
    else
    {
        ambientC = material.ambient;
        diffuseC = material.diffuse;
        speculrC = material.specular;
    }
}

// 将点光源、顶点位置及其法线作为其输入，返回为之前描述的漫反射和镜面反射光分量计算的颜色贡献
vec4 calcPointLight(PointLight light, vec3 position, vec3 normal)
{
    vec4 diffuseColour = vec4(0, 0, 0, 0);
    vec4 specColour = vec4(0, 0, 0, 0);

    // 漫反射光
    vec3 light_direction = light.position - position;
    vec3 to_light_source  = normalize(light_direction);
    float diffuseFactor = max(dot(normal, to_light_source ), 0.0);
    diffuseColour = diffuseC * vec4(light.colour, 1.0) * light.intensity * diffuseFactor;

    // 镜面反射
    vec3 camera_direction = normalize(-position);
    vec3 from_light_source = -to_light_source;
    vec3 reflected_light = normalize(reflect(from_light_source, normal));
    float specularFactor = max( dot(camera_direction, reflected_light), 0.0); // 计算反射因子, 取 0 ~ 1
    specularFactor = pow(specularFactor, specularPower); // 反射的强度因素
    specColour = speculrC * specularFactor * material.reflectance * vec4(light.colour, 1.0);

    // 光的衰减
    float distance = length(light_direction);
    float attenuationInv = light.att.constant + light.att.linear * distance +
        light.att.exponent * distance * distance;
    return (diffuseColour + specColour) / attenuationInv;
}

void main()
{
    setupColours(material, outTexCoord);

    vec4 diffuseSpecularComp = calcPointLight(pointLight, mvVertexPos, mvVertexNormal);

    fragColor = ambientC * vec4(ambientLight, 1) + diffuseSpecularComp;
}
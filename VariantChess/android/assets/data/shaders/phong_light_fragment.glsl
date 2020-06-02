#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
varying vec3 v_vertPosWorld;
varying vec3 v_vertNVWorld;
uniform vec3 u_color;

struct PointLight
{
    vec3 color;
    vec3 position;
    float intensity;
};

uniform PointLight u_pointLight;

void main() {
    vec3  toLightVector  = normalize(u_pointLight.position - v_vertPosWorld.xyz);
    float lightIntensity = max(0.0, dot(v_vertNVWorld, toLightVector));
    vec3  finalCol       = u_color * lightIntensity * u_pointLight.color;
    gl_FragColor         = vec4(finalCol.rgb * lightIntensity, 1.0);
}

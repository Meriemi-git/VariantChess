#ifdef GL_ES
precision mediump float;
precision mediump sampler2D;
#endif

varying  vec2  v_texCoords;
varying  float v_lightIntensity;
uniform vec3 u_color;

void main() {
    //vec4 texCol  = texture2D(u_texture, v_texCoords.xy);
    gl_FragColor  = vec4(u_color * v_lightIntensity, 1.0);
}

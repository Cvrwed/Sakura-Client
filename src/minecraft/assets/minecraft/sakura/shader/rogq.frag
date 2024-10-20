#version 120

uniform vec2 u_size;
uniform float u_radius;
uniform float u_border_size;
uniform vec4 u_color_1;
uniform vec4 u_color_2;

void main(void) {
    float a = gl_TexCoord[0].s * 0.5 + gl_TexCoord[0].t * 0.5;
    float b = 1.0 - abs(1.0 - a * 2.0);
    vec4 color = mix(u_color_1, u_color_2, b);

    vec2 position = (abs(gl_TexCoord[0].st - 0.5) + 0.5) * u_size;

    float distance = length(max(position - (u_size - vec2(u_radius + u_border_size)), 0.0)) - u_radius;

    float alpha = smoothstep(0.0, 1.0, distance + 0.5) - smoothstep(0.0, 1.0, distance + 0.5 - u_border_size);
    gl_FragColor = vec4(color.rgb, color.a * alpha);
}
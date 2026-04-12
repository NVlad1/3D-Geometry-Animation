precision mediump float;

varying vec3 fragColour;

void main()
{
    gl_FragColor = vec4(fragColour, 1.0);
}
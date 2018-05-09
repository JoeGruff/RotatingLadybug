precision mediump float;
uniform sampler2D uTexture;
varying vec2 vTexCoordinate;
varying vec4 vColor;
varying float vHasTexture;
void main()
{
    gl_FragColor = vColor * texture2D(uTexture, vTexCoordinate) * vHasTexture + (1.0 - vHasTexture) * vColor;
    //gl_FragColor = vColor;
}
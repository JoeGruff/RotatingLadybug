uniform mat4 uMVPMatrix;
uniform mat4 uMVMMatrix;
uniform mat4 uBoneMatrix[4];
uniform vec3 uLightPos;

attribute vec4 aColor;
attribute vec4 aPosition;
attribute vec3 aNormal;
attribute vec2 aTexCoordinate;
attribute float aIndex;

varying vec4 vColor;
varying vec2 vTexCoordinate;
varying float vHasTexture;

void main()
{
    int index = int(aIndex);
    mat4 matrix = uBoneMatrix[index];
    if (aIndex == 3.0) {
        vHasTexture = 1.0;
        } else {
        vHasTexture = 0.0;
        }
    vec4 bonedPosition = aPosition * matrix;
    vec3 bonedNormal = vec3(vec4(aNormal, 0.0) * matrix);
    vec3 modelViewVertex = vec3(uMVMMatrix * bonedPosition);
    vec3 modelViewNormal = vec3(uMVMMatrix * vec4(bonedNormal, 0.0));
    float distance = length(uLightPos - modelViewVertex);
    vec3 lightVector = normalize(uLightPos - modelViewVertex);
    float diffuse = max(dot(modelViewNormal, lightVector), 0.2);
    //diffuse = diffuse * 1.5 / (1.0 + (0.25 * distance * distance));
    diffuse = diffuse * 3.5 / (1.0 + (0.25 * distance * distance));
    vColor = aColor * diffuse;
    //vColor = aColor;
    vTexCoordinate = aTexCoordinate;
    gl_Position = uMVPMatrix * bonedPosition;
    //gl_Position = aPosition;
}
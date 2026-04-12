attribute vec4 a_Position;
attribute vec3 a_Normal;
uniform mat4 u_Matrix;
uniform vec3 u_Color;
varying vec3 fragColour;
 
void main()
{
    gl_Position = u_Matrix * a_Position;
    gl_PointSize = 5.0;
    if (a_Normal == vec3(0.0, 0.0, 0.0)){
        fragColour = u_Color;
    } else {
        vec3 ambientLightIntensity = vec3(0.15, 0.15, 0.15);
        vec3 inverseLightDirection = vec3(0.0, 0.0, 1.0);
        vec3 diffuseLightIntensity = vec3(1.0, 1.0, 1.0);
        vec3 transformedVertexNormal = (u_Matrix * vec4(a_Normal, 0.0)).xyz;
        if (dot(transformedVertexNormal, inverseLightDirection) > 0.0){
            inverseLightDirection = vec3(0.0, 0.0, 0.0);
        }
        vec3 vertexAmbientReflectionConstant = u_Color;
        vec3 vertexDiffuseReflectionConstant = u_Color;
        float normalDotLight = max(0.0, dot(a_Normal, inverseLightDirection));
        fragColour = normalDotLight * vertexDiffuseReflectionConstant * diffuseLightIntensity;
        fragColour += vertexAmbientReflectionConstant * ambientLightIntensity;
        clamp(fragColour, 0.0, 1.0);
    }
}
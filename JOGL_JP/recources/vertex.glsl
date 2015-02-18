// ------   Vertex-Shader  ---------
 
#version 130
uniform mat4 viewMatrix, projMatrix;      // Transformations-Matrizen
in vec4 vertexPosition, vertexColor;      // Vertex-Attributes
out vec4 Color;
 
void main()
{  gl_Position = projMatrix * viewMatrix * vertexPosition ;
   Color = vertexColor;
}

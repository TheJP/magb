// ------   Vertex-Shader  ---------
 
#version 130
uniform mat4 viewMatrix, projMatrix, texMatrix;           // Transformations-Matrizen
in vec4 vertexPosition, vertexColor, vertexTexCoord;      // Vertex-Attributes
out vec4 Color, TexCoord;
 
void main()
{  gl_Position = projMatrix * viewMatrix * vertexPosition ;
   Color = vertexColor;
   TexCoord = texMatrix * vertexTexCoord;
}

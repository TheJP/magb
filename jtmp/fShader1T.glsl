// ------   Fragment-Shader  ---------

#version 130
in  vec4 Color, TexCoord;           // Vertex-Attribute
out vec4 FragColor;

uniform int texFlag;
uniform sampler2D myTexture;

void main() 
{ 
   if ( texFlag == 1 )
     FragColor = min(Color * texture(myTexture, TexCoord.xy).rgba, vec4(1.0)); 
   else
     FragColor = Color; 
}       
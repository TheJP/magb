// -----------  Minimales JOGL-Programm mit Shadern fuer Texturen  ------------------------------
//                                                         E.Gutknecht, Apr 2015
//   adaptiert von:
//   http://forum.jogamp.org/Modern-JOGL-simple-texture-example-td4029964.html
//   (gbarbieri)
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Stack;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class GLMinimalT
       implements WindowListener, GLEventListener
{
    // --------------------  Globale Daten  ------------------------------

    String vShaderFileName = "vShader1T.glsl";        // VertexShader
    String fShaderFileName = "fShader1T.glsl";        // FragmetShader

    float left = -10, right = 10;                    // ViewingVolume Orthogonalprojektion
    float bottom, top;
    float near = -10, far = 1000;

    float dCam=10, elevation=10, azimut=30;            // Parameter Kamera-System

    // ------ Shader Variable Identifiers
    int projMatrixLoc, viewMatrixLoc, texMatrixLoc, textureLoc, texFlagLoc;           // Uniform Variables
    int vPositionLocation, vColorLocation, vTexCoordLocation;      // Vertex Attribute Variables


    int windowWidth = 800;
    int windowHeight = 600;
    float[] clearColor = {0,0,1,1};             // Fensterhintergrund (Blau)
    GLCanvas canvas;                            // OpenGL Window
    GLProfile glp;


    // -------  Vertex-Daten  ------------
    int vmax = 512;                             // max. Anzahl Vertices
    int vPositionSize = 4*Float.SIZE/8;         // Anz. Bytes der x,y,z,w (homogene Koordinaten)
    int vColorSize = 4*Float.SIZE/8;            // Anz. Bytes der rgba Werte
    int vTexCoordSize = 4*Float.SIZE/8;            // Anz. Bytes der rgba Werte
    int vertexSize = vPositionSize + vColorSize + vTexCoordSize;     // Anz. Bytes eines Vertex
    int bufSize = vmax*vertexSize;
    float[] currentColor = { 1,1,1,1};             // aktuelle Farbe fuer Vertices
    float[] currentTexCoord = { 0,0,0,1};          // aktuelle Textur-Koord fuer Vertices

    Texture currentTex;                            // Textur

    // -------  Vertex-Array fuer Position- Color- und TexCoord-Attribute  ------------
    FloatBuffer vertexBuf = Buffers.newDirectFloatBuffer(bufSize);
    int vaoId;                                  // VertexArray Object Identifier
    int vertexBufId;                            // Vertex Buffer Identifier

    // ------  Transformations-Matrizen  --------
    Mat4 viewMatrix = Mat4.ID;                 // ModelView-Transformation
    Mat4 texMatrix = Mat4.ID;                  // Transf. Textur-Koordinaten
    Stack<Mat4> matrixStack = new Stack<Mat4>();
    Stack<Mat4> texMatrixStack = new Stack<Mat4>();


    // -----------------------------  Methoden  --------------------------------

    void enableTexture(GL2 gl)                     // Textur in Shadern aktivieren
    {  gl.glUniform1i(texFlagLoc, 1);
    }


    void disableTexture(GL2 gl)
    {  gl.glUniform1i(texFlagLoc, 0);              // Textur in Shadern deaktivieren
    }


    //  ------  aktuelle Zeichenfarbe setzen  -----------
    public void setColor(float r, float g, float b, float a)
    {  currentColor[0] = r;
       currentColor[1] = g;
       currentColor[2] = b;
       currentColor[3] = a;
    }


    //  ------  aktuelle Textur-Koord setzen  -----------
    public void setTexCoord(float s, float t)
    {  currentTexCoord[0] = s;
       currentTexCoord[1] = t;
       currentTexCoord[2] = 0;
       currentTexCoord[3] = 1;
    }


    void putVertex(float x, float y, float z)      // Vertex-Daten in Buffer speichern
    {  vertexBuf.put(x);
       vertexBuf.put(y);
       vertexBuf.put(z);
       vertexBuf.put(1);
       vertexBuf.put(currentColor[0]);             // Farbe
       vertexBuf.put(currentColor[1]);
       vertexBuf.put(currentColor[2]);
       vertexBuf.put(currentColor[3]);
       vertexBuf.put(currentTexCoord[0]);          // Textur-Koord.
       vertexBuf.put(currentTexCoord[1]);
       vertexBuf.put(currentTexCoord[2]);
       vertexBuf.put(currentTexCoord[3]);
    }


    void rewindBuffer(GL2 gl)
    {  vertexBuf.rewind();
    }


    void copyBuffer(GL2 gl,int nVertices)            // Vertex-Array in OpenGL-Buffer kopieren
    {  vertexBuf.rewind();
       gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexBufId);
       gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, nVertices*vertexSize, vertexBuf);
    }


    void zeichneAchsen(GL2 gl, float a, float b, float c)
    {  vertexBuf.rewind();
       putVertex(0,0,0);           // Eckpunkte in VertexArray speichern
       putVertex(a,0,0);
       putVertex(0,0,0);
       putVertex(0,b,0);
       putVertex(0,0,0);
       putVertex(0,0,c);
       int nVertices = 6;
       copyBuffer(gl, nVertices);
       gl.glDrawArrays(GL2.GL_LINES, 0, nVertices);
    }


    void zeichneRechteck(GL2 gl, float a, float b)
    {  a *= 0.5f;
       b *= 0.5f;
       rewindBuffer(gl);
       setTexCoord(0,0);
       putVertex(-a,-b,0);
       setTexCoord(0,1);
       putVertex(-a,b,0);
       setTexCoord(1,1);
       putVertex(a,b,0);
       setTexCoord(1,0);
       putVertex(a,-b,0);
       int nVertices = 4;
       copyBuffer(gl, nVertices);
       gl.glDrawArrays(GL2.GL_QUADS, 0, nVertices);
    }


    public void setCameraSystem(GL2 gl, float r,           // Abstand der Kamera von O
                                float elevation,           // Elevationswinkel in Grad
                                float azimut)              // Azimutwinkel in Grad
    {  float toRad = (float)(Math.PI/180);
       float c = (float)Math.cos(toRad*elevation);
       float s = (float)Math.sin(toRad*elevation);
       float cc = (float)Math.cos(toRad*azimut);
       float ss = (float)Math.sin(toRad*azimut);
       viewMatrix = new Mat4(cc, -s*ss, c*ss, 0, 0, c, s, 0, -ss, -s*cc, c*cc, 0, 0, 0, -r, 1);
       gl.glUniformMatrix4fv(viewMatrixLoc, 1, false,
                             viewMatrix.toArray(), 0);
    }


    public void setCameraSystem(GL2 gl, Vec3 A, Vec3 B, Vec3 up)
    {
       viewMatrix = Mat4.lookAt(A,B,up);
       gl.glUniformMatrix4fv(viewMatrixLoc, 1, false,
                             viewMatrix.toArray(), 0);
    }


    public void translate(GL2 gl,                     // Verschiebung Objekt-System
                          float x, float y, float z)
    {  viewMatrix=viewMatrix.postMultiply(Mat4.translate(x,y,z));
       gl.glUniformMatrix4fv(viewMatrixLoc, 1, false,
                             viewMatrix.toArray(), 0);
    }


    public void rotate(GL2 gl,                         // Drehung des Objekt-Systems
                       float phi,                      // Drehwinkel in Grad
                       float x, float y, float z)
    {  viewMatrix=viewMatrix.postMultiply(Mat4.rotate(phi,x,y,z));
       gl.glUniformMatrix4fv(viewMatrixLoc, 1, false,
                             viewMatrix.toArray(), 0);
    }


    public void scale(GL2 gl,                          // Skalierung des Objekt-Systems
                       float x, float y, float z)
    {  viewMatrix=viewMatrix.postMultiply(Mat4.scale(x,y,z));
       gl.glUniformMatrix4fv(viewMatrixLoc, 1, false,
                             viewMatrix.toArray(), 0);
    }


    public void setTexMatrix(GL2 gl, Mat4 matrix)     // Transf. Matrix fuer Texture-Koord
    {  texMatrix = matrix;
       gl.glUniformMatrix4fv(texMatrixLoc, 1, false,
                             texMatrix.toArray(), 0);
    }

    void pushMatrix(GL2 gl)                // ModelView-Matrix speichern
    {  matrixStack.push(viewMatrix);
    }

    void popMatrix(GL2 gl)                 // ModelView-Matrix vom Stack holen
    {  viewMatrix = matrixStack.pop();
       gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix.toArray(), 0);
    }

    void pushTexMatrix(GL2 gl)                // Texture-Matrix speichern
    {  texMatrixStack.push(texMatrix);
    }

    void popTexMatrix(GL2 gl)                 // Texture-Matrix vom Stack holen
    {  texMatrix = texMatrixStack.pop();
       gl.glUniformMatrix4fv(texMatrixLoc, 1, false, texMatrix.toArray(), 0);
    }


    public void setProjection(GL2 gl, float left, float right,  // Grenzen des ViewingVolumes
                                      float bottom, float top,
                                      float near, float far)
    {   float m00 = 2.0f / (right-left);;
        float m11 = 2.0f / (top-bottom);
        float m22 = -2.0f / (far-near);
        float m03 = - (right + left) / (right-left);
        float m13 = - (top + bottom) / (top-bottom);
        float m23 = - (far + near) / (far-near);
        float m33 = 1;
        float[] projMatrix = {m00, 0, 0, 0, 0, m11, 0, 0, 0, 0, m22, 0, m03, m13, m23, m33 };
        gl.glUniformMatrix4fv(projMatrixLoc, 1, false, projMatrix, 0);
    }


    public void setPerspectiveProjection(GL2 gl, float left, float right,  // Grenzen des ViewingVolumes
                                      float bottom, float top,
                                      float near, float far)
    {   Mat4 projMatrix = Mat4.perspective(left,right,bottom,top,near,far);
        gl.glUniformMatrix4fv(projMatrixLoc, 1, false, projMatrix.toArray(), 0);
    }


    //  --------  Konstruktor  ---------------------
    public GLMinimalT()
    {  Frame f = new Frame("Java OpenGL");
       f.setSize(windowWidth, windowHeight);
       f.addWindowListener(this);
       glp = GLProfile.get(GLProfile.GL2GL3);
       GLCapabilities glCapabilities = new GLCapabilities(glp);
       canvas = new GLCanvas(glCapabilities);
       canvas.addGLEventListener(this);
       f.add(canvas);
       f.setVisible(true);
    };


    void setupGLBuffers(GL2 gl)                                 // OpenGL Buffer
    {  // ------  OpenGl-Objekte -----------
       int[] tmp = new int[1];
       gl.glGenVertexArrays(1, tmp, 0);                         // VertexArrayObject
       vaoId = tmp[0];
       gl.glBindVertexArray(vaoId);
       gl.glGenBuffers(1, tmp, 0);                              // VertexBuffer
       vertexBufId = tmp[0];
       gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexBufId);
       gl.glBufferData(GL2.GL_ARRAY_BUFFER, bufSize,            // Speicher allozieren
                            null, GL2.GL_STATIC_DRAW);
       gl.glEnableVertexAttribArray(vPositionLocation);
       gl.glEnableVertexAttribArray(vColorLocation);
       gl.glEnableVertexAttribArray(vTexCoordLocation);
       gl.glVertexAttribPointer(vPositionLocation, 4, GL2.GL_FLOAT, false, vertexSize, 0);
       gl.glVertexAttribPointer(vColorLocation, 4, GL2.GL_FLOAT, false, vertexSize, vPositionSize);
       gl.glVertexAttribPointer(vTexCoordLocation, 4, GL2.GL_FLOAT, false, vertexSize, vPositionSize+vColorSize);
   }


    String extractFileType(String s)
    {  for ( int i=s.length()-1; i >= 0; i--)
         if ( s.charAt(i) == '.' )
           return s.substring(i);
       return s;
    }


    void loadTextureFromFile(GL2 gl, String fileName)
    {  Texture t = null;
       String fileType = extractFileType(fileName);
       try
       {   t = TextureIO.newTexture(this.getClass().getResource(fileName), false, "."+fileType);
           t.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
           t.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
           t.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
           t.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);
       }
       catch (IOException | GLException ex)
       {  ex.printStackTrace();
       }
       gl.glActiveTexture(GL2.GL_TEXTURE0);
       t.enable(gl);
       t.bind(gl);
       gl.glUniform1i(textureLoc, 0);
       currentTex = t;
    }


    void loadTextureFromArray(GL2 gl, float[] texColors, int m, int n)
    {
       FloatBuffer texBuf = Buffers.newDirectFloatBuffer(texColors);
       int[] ids = new int[1];    // OpenGL-Identifiers
       gl.glGenTextures(1,ids,0);
       gl.glBindTexture(GL2.GL_TEXTURE_2D,ids[0]);
       gl.glTexImage2D(GL2.GL_TEXTURE_2D,0,GL2.GL_RGBA,m,n,0,GL2.GL_RGBA,GL2.GL_FLOAT,texBuf);
       gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 1);
       gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
       gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
       gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
       gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);
       gl.glActiveTexture(GL2.GL_TEXTURE0);
       gl.glUniform1i(textureLoc, 0);
    }


    @Override
    public void init(GLAutoDrawable drawable)
    {  GL2 gl = drawable.getGL().getGL2();
       System.out.println("OpenGl Version: " + gl.glGetString(GL.GL_VERSION));
       System.out.println("Shading Language: " + gl.glGetString(GL2ES2.GL_SHADING_LANGUAGE_VERSION));
       gl.glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);
       gl.glEnable(GL2.GL_DEPTH_TEST);
       int program = GLShaders.loadShaders(gl,vShaderFileName, fShaderFileName);
       // ----- get shader variable identifiers  -------------
       projMatrixLoc = gl.glGetUniformLocation(program, "projMatrix");
       viewMatrixLoc = gl.glGetUniformLocation(program, "viewMatrix");
       texMatrixLoc = gl.glGetUniformLocation(program, "texMatrix");
       textureLoc = gl.glGetUniformLocation(program, "myTexture");
       texFlagLoc = gl.glGetUniformLocation(program, "texFlag");
       vPositionLocation = gl.glGetAttribLocation(program, "vertexPosition");
       vColorLocation = gl.glGetAttribLocation(program, "vertexColor");
       vTexCoordLocation = gl.glGetAttribLocation(program, "vertexTexCoord");
       float[] identityMatrix = {1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1};
       gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, identityMatrix, 0);
       gl.glUniformMatrix4fv(projMatrixLoc, 1, false, identityMatrix, 0);
       gl.glUniformMatrix4fv(texMatrixLoc, 1, false, identityMatrix, 0);
       setupGLBuffers(gl);
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height)
    {  GL2 gl = drawable.getGL().getGL2();
       // Set the viewport to be the entire window
       gl.glViewport(0, 0, width, height);
       float aspect = height / (float)width;
       bottom = aspect * left;
       top = aspect * right;
       setProjection(gl,left,right,bottom,top,near,far);     // Orthogonalprojektion
    }


    @Override
    public void display(GLAutoDrawable drawable)
    { GL2 gl = drawable.getGL().getGL2();
      gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
      setCameraSystem(gl,dCam,elevation,azimut);
      setColor(0.8f, 0.8f, 0.8f, 1);
      zeichneAchsen(gl, 8,8,8);
      setColor(1.0f, 0.0f, 0.0f, 1);
      zeichneRechteck(gl,8,4);
   }


    @Override
    public void dispose(GLAutoDrawable drawable) { }


    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args)
    { new GLMinimalT();
    }

    //  ---------  Window-Events  --------------------

    public void windowClosing(WindowEvent e)
    {  System.exit(0);
    }
    public void windowActivated(WindowEvent e) {  }
    public void windowClosed(WindowEvent e) {  }
    public void windowDeactivated(WindowEvent e) {  }
    public void windowDeiconified(WindowEvent e) {  }
    public void windowIconified(WindowEvent e) {  }
    public void windowOpened(WindowEvent e) {  }

}
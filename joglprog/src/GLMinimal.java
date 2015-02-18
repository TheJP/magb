// -----------  Minimales 3D JOGL-Programm mit Shadern  ------------------------------
//                                                         E.Gutknecht, Feb 2015
//   adaptiert von:
//   http://www.lighthouse3d.com/cg-topics/code-samples/opengl-3-3-glsl-1-5-sample/
//
import java.awt.*;
import java.awt.event.*;

import javax.media.opengl.*;
import javax.media.opengl.awt.*;

import java.nio.*;

import com.jogamp.common.nio.*;

public class GLMinimal
       implements WindowListener, GLEventListener
{
    // --------------------  Globale Daten  ------------------------------

    String vShaderFileName = "vShader1.glsl";        // VertexShader
    String fShaderFileName = "fShader1.glsl";        // FragmetShader

    // ------ Shader Variable Identifiers
    int projMatrixLoc, viewMatrixLoc;           // Uniform Variables
    int vPositionLocation, vColorLocation;      // Vertex Attribute Variables


    int windowWidth = 800;
    int windowHeight = 600;
    float[] clearColor = {0,0,1,1};             // Fensterhintergrund (Blau)
    GLCanvas canvas;                            // OpenGL Window


    // -------  Vertex-Daten  ------------
    int vmax = 512;                             // max. Anzahl Vertices
    int vPositionSize = 4*Float.SIZE/8;         // Anz. Bytes der x,y,z,w (homogene Koordinaten)
    int vColorSize = 4*Float.SIZE/8;            // Anz. Bytes der rgba Werte
    int vertexSize = vPositionSize + vColorSize;     // Anz. Bytes eines Vertex
    int bufSize = vmax*vertexSize;
    float[] currentColor = { 1,1,1,1};          // aktuelle Farbe fuer Vertices

    // -------  Vertex-Array fuer Position- und Color-Attribute  ------------
    FloatBuffer vertexBuf = Buffers.newDirectFloatBuffer(bufSize);
    int vaoId;                                  // VertexArray Object Identifier
    int vertexBufId;                            // Vertex Buffer Identifier


    // -----------------------------  Methoden  --------------------------------


    //  ------  aktuelle Zeichenfarbe setzen  -----------
    public void setColor(float r, float g, float b, float a)
    {  currentColor[0] = r;
       currentColor[1] = g;
       currentColor[2] = b;
       currentColor[3] = a;
    }


    void putVertex(float x, float y, float z)      // Vertex-Daten in Buffer speichern
    {  vertexBuf.put(x);
       vertexBuf.put(y);
       vertexBuf.put(z);
       vertexBuf.put(1);
       vertexBuf.put(currentColor[0]);
       vertexBuf.put(currentColor[1]);
       vertexBuf.put(currentColor[2]);
       vertexBuf.put(currentColor[3]);
    }


    void copyBuffer(GL gl,int nVertices)            // Vertex-Array in OpenGL-Buffer kopieren
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


    public void setCameraSystem(GL2 gl, float r,           // Abstand der Kamera von O
                                float elevation,           // Elevationswinkel in Grad
                                float azimut)              // Azimutwinkel in Grad
    {  float toRad = (float)(Math.PI/180);
       float c = (float)Math.cos(toRad*elevation);
       float s = (float)Math.sin(toRad*elevation);
       float cc = (float)Math.cos(toRad*azimut);
       float ss = (float)Math.sin(toRad*azimut);
       float[] viewMatrix = {cc, -s*ss, c*ss, 0, 0, c, s, 0, -ss, -s*cc, c*cc, 0, 0, 0, -r, 1};
       gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix, 0);
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


    //  --------  Konstruktor  ---------------------
    public GLMinimal()
    {  Frame f = new Frame("Java OpenGL");
       f.setSize(windowWidth, windowHeight);
       f.addWindowListener(this);
       GLProfile glp = GLProfile.get(GLProfile.GL2GL3);
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
       gl.glVertexAttribPointer(vPositionLocation, 4, GL2.GL_FLOAT, false, vertexSize, 0);
       gl.glVertexAttribPointer(vColorLocation, 4, GL2.GL_FLOAT, false, vertexSize, vPositionSize);
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
       vPositionLocation = gl.glGetAttribLocation(program, "vertexPosition");
       vColorLocation = gl.glGetAttribLocation(program, "vertexColor");
       float[] identityMatrix = {1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1};
       gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, identityMatrix, 0);
       gl.glUniformMatrix4fv(projMatrixLoc, 1, false, identityMatrix, 0);
       setupGLBuffers(gl);
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height)
    {  GL2 gl = drawable.getGL().getGL2();
       // Set the viewport to be the entire window
       gl.glViewport(0, 0, width, height);
    }

    @Override
    public void display(GLAutoDrawable drawable)
    { GL2 gl = drawable.getGL().getGL2();
      gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
      setColor(0, 1, 1, 1);
      zeichneAchsen(gl, 4,4,4);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) { }


    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args)
    {
    	GLMinimal sample = new GLMinimal();
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
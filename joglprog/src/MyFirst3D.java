// -----------  Minimales 3D JOGL-Programm  (Dreieck im Raum)  ------------------------------
import javax.media.opengl.*;
public class MyFirst3D extends GLMinimal
{
    // --------------------  Globale Daten  ------------------------------

    float r=10, elevation=10, azimut=45;        // Lage des Kamera-Systems
    float left = -3, right = 3;                 // ViewingVolume im KameraSystem
    float bottom, top;
    float near = -10, far = 100;


    // -----------------------------  Methoden  --------------------------------

    void zeichneDreieck(GL2 gl)
    {  vertexBuf.rewind();
       putVertex(1,0,0);     // Eckpunkte
       putVertex(0,1,0);
       putVertex(0,0,1);
       int nVertices = 3;
       copyBuffer(gl,nVertices);
       gl.glDrawArrays(GL2.GL_TRIANGLES, 0, nVertices);
    }


    @Override
    public void display(GLAutoDrawable drawable)
    { GL2 gl = drawable.getGL().getGL2();
      gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
      setCameraSystem(gl, r, elevation, azimut);
      setColor(0, 1, 1, 1);
      zeichneAchsen(gl, 4,4,4);
      setColor(1, 0, 0, 1);
      zeichneDreieck(gl);
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height)
    {  GL2 gl = drawable.getGL().getGL2();
       // Set the viewport to be the entire window
       gl.glViewport(0, 0, width, height);
       float aspect = (float)height / width;
       bottom = aspect * left;
       top = aspect * right;
       // Set ViewingVolume for Orthogonalprojection
       setProjection(gl,left,right,bottom,top,near,far);
    }


    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args)
    { MyFirst3D sample = new MyFirst3D();
    }

}
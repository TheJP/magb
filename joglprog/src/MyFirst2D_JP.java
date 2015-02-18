// -----------  Minimales 2D JOGL-Programm  ------------------------------
//
import javax.media.opengl.*;

public class MyFirst2D_JP extends GLMinimal {

	private float left=-1, right=1, top, bottom, near=-100, far=100;

	void zeichneDreieck(GL2 gl) {
		vertexBuf.rewind();
		setColor(1, 0, 0, 1); // Zeichenfarbe (ROT)
		putVertex(-0.5f, -0.5f, 0); // Eckpunkte in VertexArray speichern
		putVertex(0.5f, -0.5f, 0);
		putVertex(0, 0.5f, 0);
		int nVertices = 3;
		copyBuffer(gl, nVertices); // VertexArray in OpenGL-Buffer kopieren
		gl.glDrawArrays(GL2.GL_TRIANGLES, 0, nVertices); // Dreieck zeichnen
	}

	@Override
	public void display(GLAutoDrawable drawable) // render image
	{
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		setColor(0, 1, 1, 1);
		zeichneDreieck(gl);
	}

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height)
    {  GL2 gl = drawable.getGL().getGL2();
       // Set the viewport to be the entire window
       gl.glViewport(0, 0, width, height);
       float aspect = (float)height/width;
       bottom = aspect * left;
       top = aspect * right;
       setProjection(gl, left, right, bottom, top, near, far);
    }

	// ----------- main-Methode ---------------------------

	public static void main(String[] args) {
		MyFirst2D_JP sample = new MyFirst2D_JP();
	}

}
// -----------  Erde mit Textur  ------------------------------
//                                                         E.Gutknecht, Apr 2015
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;

import com.jogamp.opengl.util.FPSAnimator;
public class Mercedes extends GLMinimalT
{
    // --------------------  Globale Daten  ------------------------------

    String textureFileName = "NightEarth.jpg";   // Textur-File
    float dCam=10, elevation=10, azimut=20;      // Parameter Kamera-System
    float drehWinkel = 0;

    // -------  Array fuer 8x8 Schachbrett-Textur  -----------------
    float r1=1.0f, g1=1.0f, b1=0.0f;             // Farbe1
    float r2=1.0f, g2=0.0f, b2=0.0f;             // Farbe2
    float[] texColors = {                     // 8x8 Schachbrett-Muster
             r1,g1,b1,1, r2,g2,b2,1, r1,g1,b1,1, r2,g2,b2,1, r1,g1,b1,1, r2,g2,b2,1, r1,g1,b1,1, r2,g2,b2,1,
             r2,g2,b2,1, r1,g1,b1,1, r2,g2,b2,1, r1,g1,b1,1, r2,g2,b2,1, r1,g1,b1,1, r2,g2,b2,1, r1,g1,b1,1,
             r1,g1,b1,1, r2,g2,b2,1, r1,g1,b1,1, r2,g2,b2,1, r1,g1,b1,1, r2,g2,b2,1, r1,g1,b1,1, r2,g2,b2,1,
             r2,g2,b2,1, r1,g1,b1,1, r2,g2,b2,1, r1,g1,b1,1, r2,g2,b2,1, r1,g1,b1,1, r2,g2,b2,1, r1,g1,b1,1,
             r1,g1,b1,1, r2,g2,b2,1, r1,g1,b1,1, r2,g2,b2,1, r1,g1,b1,1, r2,g2,b2,1, r1,g1,b1,1, r2,g2,b2,1,
             r2,g2,b2,1, r1,g1,b1,1, r2,g2,b2,1, r1,g1,b1,1, r2,g2,b2,1, r1,g1,b1,1, r2,g2,b2,1, r1,g1,b1,1,
             r1,g1,b1,1, r2,g2,b2,1, r1,g1,b1,1, r2,g2,b2,1, r1,g1,b1,1, r2,g2,b2,1, r1,g1,b1,1, r2,g2,b2,1,
             r2,g2,b2,1, r1,g1,b1,1, r2,g2,b2,1, r1,g1,b1,1, r2,g2,b2,1, r1,g1,b1,1, r2,g2,b2,1, r1,g1,b1,1,
                  };



    // -----------------------------  Methoden  --------------------------------

    void zeichneDreieck(GL2 gl, Vec3 a, Vec3 b, Vec3 c)
    {
       rewindBuffer(gl);
       putVertex(a.x,a.y,a.z);
       putVertex(b.x,b.y,b.z);
       putVertex(c.x,c.y,c.z);
       int nVertices = 3;
       copyBuffer(gl, nVertices);
       gl.glDrawArrays(GL2.GL_TRIANGLES, 0, nVertices);
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


   void rotFlaeche(GL2 gl,                           // Rotationsflaeche
                float[] x, float[] y,                // Kurve in xy-Ebene
                int n2)                              // Anzahl Streifen
    {  float todeg = (float)(180/Math.PI);
       float dtheta = (float)(2*Math.PI / n2);       // Drehwinkel
       float c = (float)Math.cos(dtheta);            // Drehmatrix
       float s = (float)Math.sin(dtheta);
       rewindBuffer(gl);
       int n1 = x.length;
       float ds = 1.0f / n2;                         // Zuwachs Textur-Koord. s (horizontal)
       float dt = 1.0f / (n1-1);                     // Zuwachs Textur-Koord. t (vertikal)
       for (int i=0; i < n1; i++)                    // TriangleStrip fuer ersten Streifen
       {  setTexCoord(0,1-i*dt);
          putVertex(x[i],y[i],0);
          setTexCoord(ds,1-i*dt);
          putVertex(c*x[i],y[i],-s*x[i]);            // gedrehter Punkt
       }
       int nVertices = 2*n1;
       copyBuffer(gl,nVertices);
       for (int j=0; j< n2; j++)                     // n2 Streifen mittels Drehung des ersten Streifens
       {  pushMatrix(gl);
          pushTexMatrix(gl);
          rotate(gl,j*dtheta*todeg,0,1,0);
          setTexMatrix(gl,texMatrix.postMultiply(Mat4.translate(j*ds,0,0)));
          gl.glDrawArrays(GL2.GL_TRIANGLE_STRIP,0,nVertices);
          popTexMatrix(gl);
          popMatrix(gl);
       }
    }


   void zeichneKugel(GL2 gl,float r, int n1, int n2)
    {  float[] x = new float[n1];      // Halbkreis in xy-Ebene von Nord- zum Suedpol
       float[] y = new float[n1];
       float dphi = (float)(Math.PI / (n1-1)), phi;
       for (int i = 0; i < n1; i++)
       {  phi  = (float)(0.5*Math.PI) - i*dphi;
          x[i] = r*(float)Math.cos(phi);
          y[i] = r*(float)Math.sin(phi);
       }
       rotFlaeche(gl,x,y,n2);
     }


    @Override
    public void init(GLAutoDrawable drawable)
    {  super.init(drawable);
       GL2 gl = drawable.getGL().getGL2();
//       loadTextureFromFile(gl, textureFileName);          // Textur von jpg-File einlesen
       loadTextureFromArray(gl, texColors,8,8);               // Textur von Array
       Mat4 T = Mat4.translate(0,1,0);
       T = T.postMultiply(Mat4.scale(1,-1,1));
       setTexMatrix(gl,T) ;
       FPSAnimator animator = new FPSAnimator(canvas,200,true);
       animator.start();
    }


    @Override
    public void display(GLAutoDrawable drawable)
    { GL2 gl = drawable.getGL().getGL2();
      gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
      setCameraSystem(gl,dCam,elevation,azimut);
      setColor(0.8f, 0.8f, 0.8f, 1);
      zeichneAchsen(gl, 8,8,8);
      rotate(gl,drehWinkel,0,1,0);                          // Objekt-System Erde
//      enableTexture(gl);
//      zeichneKugel(gl,2,20,20);
//      zeichneRechteck(gl,8,4);
      Vec3 a = new Vec3(1,0,0);
      Vec3 b = new Vec3(0,0,-1);
      Vec3 c = new Vec3(-1,0,0);
      Vec3 d = new Vec3(0,0,1);
      Vec3 s = new Vec3(0,5,0);
      pushMatrix(gl);
      for(int i = 0; i < 3; ++i){
    	  setColor(0, 1 * (i/3) + 0.2f, 0, 1);
          zeichneDreieck(gl, a, b, s);
    	  setColor(0, 1 * (i/3) + 0.25f, 0, 1);
          zeichneDreieck(gl, c, b, s);
    	  setColor(0, 1 * (i/3) + 0.3f, 0, 1);
          zeichneDreieck(gl, c, d, s);
    	  setColor(0, 1 * (i/3) + 0.35f, 0, 1);
          zeichneDreieck(gl, d, a, s);
          rotate(gl, 120, 0, 0, 1);
      }
      popMatrix(gl);
      disableTexture(gl);
      drehWinkel+=0.5f;
   }

    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args)
    { new Mercedes();
    }
}
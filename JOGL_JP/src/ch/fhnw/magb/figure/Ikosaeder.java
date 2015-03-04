package ch.fhnw.magb.figure;

import javax.media.opengl.GL2GL3;

import org.la4j.V;
import org.la4j.Vector;

import ch.fhnw.magb.GLColorBufferBase;
import ch.fhnw.magb.Utility;

public class Ikosaeder {

	//** Constants to draw the Ikosaeder **//

	private final static float x = 0.525731112119133606f;
	private final static float z = 0.850650808352039932f;
	private final static float[][] data = new float[][]{
		{-x, 0, z}, {x, 0, z}, {-x, 0, -z}, {x, 0, -z},
		{0, z, x}, {0, z, -x}, {0, -z, x}, {0, -z, -x},
		{z, x, 0}, {-z, x, 0}, {z, -x, 0}, {-z, -x, 0}
	};
	private final static int[][] indicies = new int[][]{
		{1,4,0}, {4,9,0}, {4,5,9}, {8,5,4}, {1,8,4},
		{1,10,8}, {10,3,8}, {8,3,5}, {3,2,5}, {3,7,2},
		{3,10,7}, {10,6,7}, {6,11,7}, {6,0,11}, {6,1,0},
		{10,1,6}, {11,0,9}, {2,11,9}, {5,2,9}, {11,2,7}	
	};

	public static void draw(GL2GL3 gl, GLColorBufferBase buffer, Vector toLight){
		draw(gl, buffer, toLight, data, indicies);
	}

	public static void draw(GL2GL3 gl, GLColorBufferBase buffer, Vector toLight, float[][] data, int[][] indicies){
		//Store original color, so it can be reseted to it
		float[] color = buffer.getColor();
		//Ensure that light is normalized
		toLight = toLight.divide(toLight.norm());
		//Definition of the plane, to which the shadow is projected
		Vector nE = V.v(0, 1, 0);  //Normal of the plane
		Vector pE = V.v(0, -1, 0); //p0 (Point on the plane)
		double d = nE.innerProduct(pE);
		//Add the triangles
		for(int i = 0; i < indicies.length; ++i){
			Vector a = V.v(Utility.floatToDouble(data[indicies[i][0]]));
			Vector b = V.v(Utility.floatToDouble(data[indicies[i][1]]));
			Vector c = V.v(Utility.floatToDouble(data[indicies[i][2]]));
			//** Draw Triangle **//
			//Set light
			float light = Utility.calculateLight(a, b, c, toLight);
			buffer.setColor(light * color[0], light * color[1], light * color[2], 1);
			buffer.addVertex(a);
			buffer.addVertex(b);
			buffer.addVertex(c);
			//** Draw Shadow **//
			buffer.setColor(0, 0, 0, 1);
			Utility.drawShadowVector(buffer, a, toLight, nE, d);
			Utility.drawShadowVector(buffer, b, toLight, nE, d);
			Utility.drawShadowVector(buffer, c, toLight, nE, d);
		}
		//Draw
	    buffer.copy(gl);
	    buffer.draw(gl, GL2GL3.GL_TRIANGLES);
	    //Reset color to the original value
	    buffer.setColor(color);
	}

	public static float[][] getData() {
		return data;
	}

	public static int[][] getIndicies() {
		return indicies;
	}
}

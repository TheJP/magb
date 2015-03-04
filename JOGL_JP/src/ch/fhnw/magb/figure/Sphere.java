package ch.fhnw.magb.figure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.media.opengl.GL2GL3;

import org.la4j.V;
import org.la4j.Vector;

import ch.fhnw.magb.GLColorBufferBase;
import ch.fhnw.magb.Utility;

public class Sphere {

	/**
	 * Splits every triangle of the given list into 4 new triangles.
	 * @param data
	 * @param indicies
	 */
	public static void subdivide(List<float[]> data, Queue<int[]> indicies){
		int triangles = indicies.size();
		for(int i = 0; i < triangles; ++i){
			int[] t = indicies.poll();
			//Read vertices of old triangle
			Vector v1 = V.v(Utility.floatToDouble(data.get(t[0])));
			Vector v2 = V.v(Utility.floatToDouble(data.get(t[1])));
			Vector v3 = V.v(Utility.floatToDouble(data.get(t[2])));
			//Calculate new vertices between given
			Vector v12 = v1.add(v2);
			Vector v23 = v2.add(v3);
			Vector v31 = v3.add(v1);
			v12 = v12.divide(v12.norm());
			v23 = v23.divide(v23.norm());
			v31 = v31.divide(v31.norm());
			//Add new vertices to data
			int iv12 = data.size(); data.add(Utility.matrixToArray(v12.toRowMatrix()));
			int iv23 = iv12+1; data.add(Utility.matrixToArray(v23.toRowMatrix()));
			int iv31 = iv23+1; data.add(Utility.matrixToArray(v31.toRowMatrix()));
			//Add new triangles
			indicies.add(new int[]{ t[0], iv12, iv31 });
			indicies.add(new int[]{ t[1], iv23, iv12 });
			indicies.add(new int[]{ t[2], iv31, iv23 });
			indicies.add(new int[]{ iv12, iv23, iv31 });
		}
	}

	public static void draw(GL2GL3 gl, GLColorBufferBase buffer, Vector toLight, int precision){
		//Prepare data
		List<float[]> data = new ArrayList<float[]>(Arrays.asList(Ikosaeder.getData()));
		Queue<int[]> indicies = new LinkedList<int[]>(Arrays.asList(Ikosaeder.getIndicies()));
		for(int i = 0; i < precision; ++i){
			subdivide(data, indicies);
		}
		//Draw sphere
		float[][] d = new float[data.size()][];
		int[][] i = new int[indicies.size()][];
		Ikosaeder.draw(gl, buffer, toLight, data.toArray(d), indicies.toArray(i));
	}
}

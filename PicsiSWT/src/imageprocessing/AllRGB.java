package imageprocessing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import utils.Parallel;
import utils.RGB2;

public class AllRGB implements IImageProcessor {

	@Override
	public boolean isEnabled(int imageType) {
		return true;
	}

	private void setPixel(ImageData data, int x, int y, int pixel){
		if(x >= 0 && x < data.width && y >= 0 && y < data.height){
			data.setPixel(x, y, 0xffffff);
		}
	}

	static class Triangle {

		private float detv0v1;
		private float detv0v2;
		private float detv1v2;
		private float x0;
		private float x1;
		private float x2;
		private float y0;
		private float y1;
		private float y2;
		public Triangle(float x0, float y0, float x1, float y1, float x2, float y2){
			this.x0 = x0;
			this.x1 = x1-x0;
			this.x2 = x2-x0;
			this.y0 = y0;
			this.y1 = y1-y0;
			this.y2 = y2-y0;
			//barycentric coordinates
			//http://mathworld.wolfram.com/TriangleInterior.html
			//http://stackoverflow.com/questions/13300904/determine-whether-point-lies-inside-triangle
//			alpha = (det(v v2) - det(v0 v2)) / det(v1 v2)
//			beta = - ( (det(v v1) - det(v0 v1)) / det(v1 v2) )
			detv0v1 = this.x0*this.y1 - this.y0*this.x1;
			detv0v2 = this.x0*this.y2 - this.y0*this.x2;
			detv1v2 = this.x1*this.y2 - this.y1*this.x2;
		}

		public boolean inside(float x, float y){
			float detvv2 = x*y2 - y*x2;
			float detvv1 = x*y1 - y*x1;
			float alpha = (detvv2 - detv0v2) / detv1v2;
			float beta = - ( (detvv1 - detv0v1) / detv1v2 );
			return alpha > 0 && beta > 0 && alpha + beta < 1;
		}
	}

	@Override
	public Image run(Image input, int imageType) {
		final ImageData outData = new ImageData(4096, 4096, 24, input.getImageData().palette);
		String[] options = new String[]{ "iterative", "logarithmic function" };
		int option = JOptionPane.showOptionDialog(null, "Choose the ImageType", "AllRGB", 0, JOptionPane.QUESTION_MESSAGE, null, options, "");

		switch (option) {
			//Iterative
			case 0:
				Parallel.For(0, outData.height, y -> {
					for(int x = 0; x < outData.width; ++x){
						outData.setPixel(x, y, x + (outData.width * y));
					}
				});
				break;
			case 1:
				List<RGB2> rgbs = new ArrayList<RGB2>(4096*4096);
				for(int x = 0; x < 0x1000000; ++x){
					rgbs.add(new RGB2(outData.palette.getRGB(x)));
				}
				rgbs.sort(new Comp3Inv());
				Iterator<RGB2> it = rgbs.iterator();
//				for(int y = 0; y < outData.height; ++y){
//					for(int x = 0; x < outData.width; ++x){
//						outData.setPixel(x, y, it.hasNext() ? outData.palette.getPixel(it.next().rgb) : 0);
//					}
//				}
				float m = 4096/2;
				boolean first = true;
				float x0 = 0, y0 = 0;
				for(double i = 0.01; i < 40.0; i+=0.01){
					double factor = Math.pow(Math.E, 0.25*i);
					double x = factor * Math.cos(i) + m;
					double y = factor * Math.sin(i) + m;
					if(first){
						first = false;
					} else {
						Triangle t = new Triangle(m, m, x0, y0, (float)x, (float)y);
						int maxx = Math.min(outData.width,  (int) Math.ceil(Math.max(m, Math.max(x, x0))));
						int maxy = Math.min(outData.height, (int) Math.ceil(Math.max(m, Math.max(y, y0))));
						for(int u = Math.max(0, (int) Math.floor(Math.min(m, Math.min(x, x0)))); u < maxx; ++u){
							for(int v = Math.max(0, (int) Math.floor(Math.min(m, Math.min(y, y0)))); v < maxy; ++v){
								if(t.inside(u, v) && outData.getPixel(u, v) == 0){
									outData.setPixel(u, v, it.hasNext() ? outData.palette.getPixel(it.next().rgb) : 0);
								}
							}
						}
					}
					x0 = (float) x;
					y0 = (float) y;
//					setPixel(outData, (int)x, (int)y, 0xffffff);
				}
				break;
			default:
				break;
		}

		return new Image(input.getDevice(), outData);
	}
	

	static class CompH implements Comparator<RGB2> {
		@Override
		public int compare(RGB2 o1, RGB2 o2) {
//			return Float.compare(Math.round(o1.h), Math.round(o2.h));
			return Float.compare(o1.h, o2.h);
		}
	}
	static class CompS implements Comparator<RGB2> {
		@Override
		public int compare(RGB2 o1, RGB2 o2) {
//			return Float.compare(Math.round(o1.s*100)/100, Math.round(o2.s*100)/100);
			return Float.compare(o1.s, o2.s);
		}
	}
	static class CompB implements Comparator<RGB2> {
		@Override
		public int compare(RGB2 o1, RGB2 o2) {
//			return Float.compare(Math.round(o1.b*100)/100, Math.round(o2.b*100)/100);
			return Float.compare(o1.b, o2.b);
		}
	}
	static class Comp3 implements Comparator<RGB2> {
		@Override
		public int compare(RGB2 o1, RGB2 o2) {
			return Float.compare(o1.b+o1.s+(o1.h/180f), o2.b+o2.s+(o2.h/180f));
		}
	}
	static class Comp3Inv implements Comparator<RGB2> {
		@Override
		public int compare(RGB2 o2, RGB2 o1) {
			return Float.compare(o1.b+o1.s+(o1.h/180f), o2.b+o2.s+(o2.h/180f));
		}
	}
}

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
				rgbs.sort(new Comp3());
				Iterator<RGB2> it = rgbs.iterator();
//				for(int y = 0; y < outData.height; ++y){
//					for(int x = 0; x < outData.width; ++x){
//						outData.setPixel(x, y, it.hasNext() ? outData.palette.getPixel(it.next().rgb) : 0);
//					}
//				}
				int m = 4096/2;
				for(double i = 0.01; i < 4096; i+=0.01){
					double factor = Math.pow(Math.E, 0.5*i);
					double x = factor * Math.cos(i) + (4096/2);
					double y = factor * Math.sin(i) + (4096/2);
					setPixel(outData, (int)x, (int)y, 0xffffff);
					setPixel(outData, (int)x+1, (int)y, 0xffffff);
					setPixel(outData, (int)x, (int)y+1, 0xffffff);
					setPixel(outData, (int)x+1, (int)y+1, 0xffffff);
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
}

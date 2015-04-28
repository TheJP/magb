package imageprocessing;

import main.PicsiSWT;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

public class GrayToBinary implements IImageProcessor {

	private final double[][] calc;
	private final double divider;

	public GrayToBinary() {
		calc = new double[][]{
			{ 0, 0, 0, 8, 4 },
			{ 2, 4, 8, 4, 2 },
			{ 1, 2, 4, 2, 1 }
		};
		divider = 42.0;
//		for(int y = 0; y < calc.length; ++y){
//			for(int x = 0; x < calc[y].length; ++x){
//				calc[y][x] /= 42;
//			}
//		}
	}

	@Override
	public boolean isEnabled(int imageType) {
		return imageType == PicsiSWT.IMAGE_TYPE_GRAY;
	}

	@Override
	public Image run(Image input, int imageType) {
		ImageData inData = input.getImageData();

		//stucki dithering
		double[][] fails = new double[3][inData.width];

		for(int v=0; v < inData.height; ++v){
			for (int u=0; u < inData.width; ++u) {
				int pixel = inData.getPixel(u,v);
				double y = pixel + (fails[0][u] / divider);
				double d = y - (y < 128.0 ? 0 : 255);
				for(int a = 0; a < calc.length; ++a){
					for(int b = Math.max(0, u - 2); b <= Math.min(fails[a].length - 1, u + 2); ++b){
						fails[a][b] += calc[a][b-(u-2)] * d;
					}
				}
				inData.setPixel(u, v, (y < 128.0 ? 0 : 255));
			}
			fails[0] = fails[1];
			fails[1] = fails[2];
			fails[2] = new double[inData.width];
		}

		return new Image(input.getDevice(), inData);
	}

}

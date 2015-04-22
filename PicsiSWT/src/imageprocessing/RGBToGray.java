package imageprocessing;

import main.PicsiSWT;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;

import utils.Parallel;

public class RGBToGray implements IImageProcessor {

	@Override
	public boolean isEnabled(int imageType) {
		return imageType == PicsiSWT.IMAGE_TYPE_RGB;
	}

	@Override
	public Image run(Image input, int imageType) {
		ImageData inData = input.getImageData();

		Parallel.For(0, inData.height, v -> {
			for (int u=0; u < inData.width; u++) {
				int pixel = inData.getPixel(u,v);
				RGB rgb = inData.palette.getRGB(pixel);
				int y = (int) (0.299 * rgb.red + 0.587 * rgb.green + 0.144 * rgb.blue);
				rgb.red = rgb.green = rgb.blue = y;
				inData.setPixel(u, v, inData.palette.getPixel(rgb));
			}
		});

		return new Image(input.getDevice(), inData);
	}

}

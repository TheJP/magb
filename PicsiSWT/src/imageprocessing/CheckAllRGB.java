package imageprocessing;

import main.PicsiSWT;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import utils.Parallel;

public class CheckAllRGB implements IImageProcessor {

	@Override
	public boolean isEnabled(int imageType) {
		return imageType == PicsiSWT.IMAGE_TYPE_RGB;
	}

	@Override
	public Image run(Image input, int imageType) {
		final ImageData inData = input.getImageData();
		final ImageData outData = new ImageData(100, 100, 24, input.getImageData().palette);

		long expected = 0;
		for(long i = 0; i <= 0xffffff; ++i){
			expected += i;
			//results in: [code]expected = expected % Long.MAX_VALUE;[/code] using the euclidean modulo in this case
			if(expected < 0){ expected -= Long.MAX_VALUE; }
		}
		System.out.println(expected);

		LongCollector actual = new LongCollector();
		Parallel.For(0, inData.height, actual, () -> new LongCollector(),
			//For Loop
			(y, localSum) -> {
				for(int x = 0; x < inData.width; ++x){
					localSum.l += inData.getPixel(x, y);
					if(localSum.l < 0){ localSum.l -= Long.MAX_VALUE; }
				}
		},
			//Reducer
			(result, add) -> {
				result.l += add.l;
				if(result.l < 0){ result.l -= Long.MAX_VALUE; }
		});
		System.out.println(actual.l);

		int color = expected == actual.l ? 0x00ff00 : 0x0000ff; /* 0x0000ff = red */
		for(int y = 0; y < 100; ++y){
			for(int x = 0; x < 100; ++x){
				outData.setPixel(x, y, color);
			}
		}

		return new Image(input.getDevice(), outData);
	}

	private static class LongCollector {
		public long l = 0;
	}
}

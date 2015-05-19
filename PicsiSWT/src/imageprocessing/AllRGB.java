package imageprocessing;

import javax.swing.JOptionPane;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import utils.Parallel;

public class AllRGB implements IImageProcessor {

	@Override
	public boolean isEnabled(int imageType) {
		return true;
	}

	@Override
	public Image run(Image input, int imageType) {
		final ImageData outData = new ImageData(4096, 4096, 24, input.getImageData().palette);
		String[] options = new String[]{ "Iterrative" };
		int option = JOptionPane.showOptionDialog(null, "Choose the ImageType", "AllRGB", 0, JOptionPane.QUESTION_MESSAGE, null, options, "");

		switch (option) {
			case 0:
				Parallel.For(0, outData.height, y -> {
					for(int x = 0; x < outData.width; ++x){
						outData.setPixel(x, y, x + (outData.width * y));
					}
				});
				break;
	
			default:
				break;
		}

		return new Image(input.getDevice(), outData);
	}

}

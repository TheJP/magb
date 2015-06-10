package imageprocessing;

import javax.swing.JOptionPane;

import main.PicsiSWT;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

public class Filter implements IImageProcessor {

	@Override
	public boolean isEnabled(int imageType) {
		return imageType != PicsiSWT.IMAGE_TYPE_INDEXED;
	}

	@Override
	public Image run(Image input, int imageType) {
		ImageData inData = (ImageData)input.getImageData();

		String[] options = new String[]{ "Box", "Gauss", "Laplace" };
		int option = JOptionPane.showOptionDialog(null, "Choose the Filter", "Filter", 0, JOptionPane.QUESTION_MESSAGE, null, options, "");

		ImageData outData;
		switch (option) {
		case 0:
			outData = ImageProcessing.convolve(inData, new double[][]{ { 1, 1, 1 }, { 1, 1, 1 }, { 1, 1, 1 } }, 9);
			break;
		case 1:
			outData = ImageProcessing.convolve(inData,
				new double[][]{ { 0, 1, 2, 1, 0 },
								{ 1, 3, 5, 3, 1 },
								{ 2, 5, 9, 5, 2 },
								{ 1, 3, 5, 3, 1 },
								{ 0, 1, 2, 1, 0 } }, 57);
			break;
		case 2:
			outData = ImageProcessing.convolve(inData,
				new double[][]{ {  0,  0, -1,  0,  0 },
								{  0, -1, -2, -1,  0 },
								{ -1, -2, 16, -2, -1 },
								{  0, -1, -2, -1,  0 },
								{  0,  0, -1,  0,  0 } });
			break;
		default:
			outData = null;
			throw new IllegalArgumentException();
		}

		return new Image(input.getDevice(), outData);
	}

}

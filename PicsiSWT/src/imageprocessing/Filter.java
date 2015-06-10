package imageprocessing;

import javax.swing.JOptionPane;

import main.PicsiSWT;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;

import utils.Parallel;

public class Filter implements IImageProcessor {

	@Override
	public boolean isEnabled(int imageType) {
		return imageType != PicsiSWT.IMAGE_TYPE_INDEXED;
	}

	@Override
	public Image run(Image input, int imageType) {
		ImageData inData = (ImageData)input.getImageData();

		String[] options = new String[]{ "Box", "Gauss", "Laplace", "Edge", "Partial-X", "Partial-Y" };
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
		case 3:
			final ImageData dx = ImageProcessing.convolve(inData, new double[][]{ { -1,  0,  1 }, { -1, 0, 1 }, { -1, 0, 1 } });
			final ImageData dy = ImageProcessing.convolve(inData, new double[][]{ { -1, -1, -1 }, {  0, 0, 0 }, {  1, 1, 1 } });
			final ImageData o = (ImageData)inData.clone();
			Parallel.For(0, o.height, v -> {
				for(int u = 0; u < o.width; ++u){
					RGB cx = dx.palette.getRGB(dx.getPixel(u, v));
					RGB cy = dy.palette.getRGB(dy.getPixel(u, v));
					o.setPixel(u, v, o.palette.getPixel(new RGB(
						ImageProcessing.clamp((int) Math.sqrt(cx.red*cx.red + cy.red*cy.red)),
						ImageProcessing.clamp((int) Math.sqrt(cx.green*cx.green + cy.green*cy.green)),
						ImageProcessing.clamp((int) Math.sqrt(cx.blue*cx.blue + cy.blue*cy.blue))
					)));
				}
			});
			outData = o;
			break;
		case 4:
			outData = ImageProcessing.convolve(inData, new double[][]{ { -1,  0,  1 }, { -1, 0, 1 }, { -1, 0, 1 } });
			break;
		case 5:
			outData = ImageProcessing.convolve(inData, new double[][]{ { -1, -1, -1 }, {  0, 0, 0 }, {  1, 1, 1 } });
			break;
		default:
			outData = null;
			throw new IllegalArgumentException();
		}

		return new Image(input.getDevice(), outData);
	}

}

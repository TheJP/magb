package imageprocessing;

import javax.swing.JOptionPane;

import main.PicsiSWT;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import utils.Parallel;

public class Rotate implements IImageProcessor {

	@Override
	public boolean isEnabled(int imageType) {
		return imageType != PicsiSWT.IMAGE_TYPE_INDEXED;
	}

	private double[][] createRotationMatrix(double dangle){
		double rangle = Math.toRadians(dangle);
		return new double[][]{
			{ Math.cos(rangle), -Math.sin(rangle) },
			{ Math.sin(rangle), Math.cos(rangle) }
		};
	}

	@Override
	public Image run(Image input, int imageType) {
		ImageData inData = (ImageData)input.getImageData();
		ImageData outData = (ImageData)input.getImageData().clone();

		double angle = 0.0;
		try {
			angle = Double.valueOf(
				JOptionPane.showInputDialog("Input Rotation angle:"));
		} catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Failed to convert angle. Please enter a valid number");
			return input;
		}

		final double[][] rotation = createRotationMatrix(angle);
		final int halfx = outData.width / 2;
		final int halfy = outData.height / 2;

		Parallel.For(0, outData.height, v -> {
			for (int u=0; u < outData.width; ++u) {
				//Translation (so the center is at 0/0)
				int x = u - halfx;
				int y = v - halfy;
				//Rotation (Matrix multiplication)
				int xs = (int)Math.round(rotation[0][0] * x + rotation[0][1] * y);
				int ys = (int)Math.round(rotation[1][0] * x + rotation[1][1] * y);
				//Translation back
				x = xs + halfx;
				y = ys + halfy;
				int pixel =
					x >= 0 && x < outData.width &&
					y >= 0 && y < outData.height ?
					inData.getPixel(x,y) : 0;
				outData.setPixel(u, v, pixel);
			}
		});

		return new Image(input.getDevice(), outData);
	}

}

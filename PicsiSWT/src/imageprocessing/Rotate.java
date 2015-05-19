package imageprocessing;

import static utils.Transformation.rotation;

import javax.swing.JOptionPane;

import main.PicsiSWT;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import utils.Transformation;

public class Rotate implements IImageProcessor {

	@Override
	public boolean isEnabled(int imageType) {
		return imageType != PicsiSWT.IMAGE_TYPE_INDEXED;
	}

	@Override
	public Image run(Image input, int imageType) {
		ImageData inData = (ImageData)input.getImageData();

		double angle = 0.0;
		try {
			angle = Double.valueOf(
				JOptionPane.showInputDialog("Input Rotation angle:"));
		} catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Failed to convert angle. Please enter a valid number");
			return input;
		}

		final Transformation rotation = rotation(angle);
		ImageData outData = rotation.targetToSourceMapping(inData);

		return new Image(input.getDevice(), outData);
	}

}

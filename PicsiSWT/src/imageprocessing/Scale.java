package imageprocessing;

import static utils.Transformation.scale;

import javax.swing.JOptionPane;

import main.PicsiSWT;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import utils.Transformation;
import utils.Transformation.Strategy;

public class Scale implements IImageProcessor {

	@Override
	public boolean isEnabled(int imageType) {
		return imageType != PicsiSWT.IMAGE_TYPE_INDEXED;
	}

	@Override
	public Image run(Image input, int imageType) {
		ImageData inData = (ImageData)input.getImageData();

		double s = 0.0;
		try {
			s = Double.valueOf(
				JOptionPane.showInputDialog("Input scale factor:"));
		} catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Failed to convert angle. Please enter a valid number");
			return input;
		}

		final Transformation t = scale(1/s);
		ImageData outData = t.targetToSourceMapping(inData, Strategy.Bilinear);

		return new Image(input.getDevice(), outData);
	}

}

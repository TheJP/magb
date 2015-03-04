package ch.fhnw.magb;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class DefaultMovingKeyListener extends KeyAdapter {

	/**
	 * Internal reference to the GLFrame.
	 */
	private GLFrame frame;

	public DefaultMovingKeyListener(GLFrame frame) {
		this.frame = frame;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		Camera camera = frame.getCamera();
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			camera.setElevation(camera.getElevation() + 1);
			break;
		case KeyEvent.VK_DOWN:
			camera.setElevation(camera.getElevation() - 1);
			break;
		case KeyEvent.VK_LEFT:
			camera.setAzimut(camera.getAzimut() + 1);
			break;
		case KeyEvent.VK_RIGHT:
			camera.setAzimut(camera.getAzimut() - 1);
			break;
		default:
			break;
		}
		frame.repaint();
	}

}

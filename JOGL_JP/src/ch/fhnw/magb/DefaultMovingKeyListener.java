package ch.fhnw.magb;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class DefaultMovingKeyListener extends KeyAdapter {

	private GLFrame frame;

	public DefaultMovingKeyListener(GLFrame frame) {
		this.frame = frame;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			frame.setElevation(frame.getElevation() + 1);
			break;
		case KeyEvent.VK_DOWN:
			frame.setElevation(frame.getElevation() - 1);
			break;
		case KeyEvent.VK_LEFT:
			frame.setAzimut(frame.getAzimut() + 1);
			break;
		case KeyEvent.VK_RIGHT:
			frame.setAzimut(frame.getAzimut() - 1);
			break;
		default:
			break;
		}
		frame.repaint();
	}

}

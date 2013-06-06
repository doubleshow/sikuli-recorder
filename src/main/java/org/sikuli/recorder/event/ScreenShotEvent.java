package org.sikuli.recorder.event;

import java.awt.image.BufferedImage;

public class ScreenShotEvent extends Event {

	private BufferedImage image;
	public BufferedImage getImage() {
		return image;
	}
	public void setImage(BufferedImage image) {
		this.image = image;
	}
}
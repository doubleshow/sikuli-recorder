package org.sikuli.recorder.detector;

import java.awt.image.BufferedImage;

import org.sikuli.api.DesktopScreenRegion;
import org.sikuli.api.ScreenRegion;
import org.sikuli.recorder.event.ScreenShotEvent;

public class ScreenshotEventDetector extends EventDetector {

	private ScreenRegion s;

	public void stop(){
		running = false;
		try {
			capturingThread.join();
		} catch (InterruptedException e) {
		}
	}

	volatile  boolean running = true;
	private Thread capturingThread;

	public void start(){
		s = new DesktopScreenRegion();				
		capturingThread = new Thread(){
			public void run(){
				while (running){		
					running = true;
					performScreenCapture();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
			}
		};
		capturingThread.start();
	}

	private void performScreenCapture(){		 
		BufferedImage image = s.capture();		
		ScreenShotEvent e = new ScreenShotEvent();
		e.setImage(image);
		eventDetected(e);
	}

}
package org.sikuli.recorder.detector;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.Timer;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.sikuli.api.DesktopScreenRegion;
import org.sikuli.api.ScreenRegion;
import org.sikuli.recorder.event.ScreenShotEvent;

public class ScreenshotEventDetector extends EventDetector {


	private Timer timerThread;
	private ScreenRegion s;

	public void start(){
		s = new DesktopScreenRegion();
		
		
//		try {
//			GlobalScreen.registerNativeHook();
//		}
//		catch (NativeHookException ex) {
//			System.err.println("There was a problem registering the native hook.");
//			System.err.println(ex.getMessage());
//
//			System.exit(1);
//		}
//
//		//Construct the example object.
//		MouseEventDetector example = new MouseEventDetector();
//
//		//Add the appropriate listeners for the example object.
//		GlobalScreen.getInstance().addNativeMouseListener(example);
//		GlobalScreen.getInstance().addNativeMouseMotionListener(example);
//		
		
		timerThread = new Timer(0, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent a) {
				performScreenCapture();				
			}		
		});
		
		timerThread.start();
		
		
		
	}
	
	private void performScreenCapture(){		 
		BufferedImage image = s.capture();		
		ScreenShotEvent e = new ScreenShotEvent();
		e.setImage(image);
		eventDetected(e);
		
//		String name = RecorderTimeStamp.get() + ".screenshot.png";			
//		System.out.println(name);
		
//		try {
//			ImageIO.write(image, "png", new File("output", name));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//		}
	}
		
}
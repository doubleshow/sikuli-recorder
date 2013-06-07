package org.sikuli.recorder;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.sikuli.recorder.detector.EventDetector;
import org.sikuli.recorder.detector.MouseEventDetector;
import org.sikuli.recorder.detector.ScreenshotEventDetector;
import org.sikuli.recorder.pptx.PPTXGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;


public class Recorder {


	public Recorder(){
		EventDetector c1 = new MouseEventDetector();
		EventDetector c2 = new ScreenshotEventDetector();		
		addEventDetector(c1);
		addEventDetector(c2);
	}

	public File getEventDir() {
		return writer.getEventDir();
	}

	private List<EventDetector> detectors = Lists.newArrayList();

	
	DefaultEventWriter writer = new DefaultEventWriter();
	public void addEventDetector(EventDetector d) {
		d.setWriter(writer);
		detectors.add(d);
	}

	public void startRecording(){
		for (EventDetector d : detectors){
			d.start();
		}
	}

	
	CountDownLatch escapeSignal = new CountDownLatch(1);
	
	public void stopRecording(){
		for (EventDetector d : detectors){
			d.stop();
		}		
	}
	
	public void start(){
		try {
			GlobalScreen.registerNativeHook();
		}
		catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());

			System.exit(1);
		}

		//Construct the example object and initialze native hook.
		GlobalScreen.getInstance().addNativeKeyListener(new HotKeyListener());

		try {
			escapeSignal.await();
		} catch (InterruptedException e) {
		}
		
		stopRecording();

		System.out.println("Recording is stopped.");
		 
	}

	class HotKeyListener implements NativeKeyListener {

		private Logger logger = LoggerFactory.getLogger(HotKeyListener.class); 

		public void nativeKeyPressed(NativeKeyEvent e) {

			boolean isMetaPressed = (e.getModifiers() & NativeKeyEvent.META_MASK) > 0;
			boolean isAltPressed = (e.getModifiers() & NativeKeyEvent.ALT_MASK) > 0;
			boolean isShiftPressed = (e.getModifiers() & NativeKeyEvent.SHIFT_MASK) > 0;
			boolean isCtrlPressed = (e.getModifiers() & NativeKeyEvent.CTRL_MASK) > 0;


			// CTRL+SHIFT+2
			if (e.getKeyCode() == NativeKeyEvent.VK_2 && isShiftPressed && isCtrlPressed){                	
				logger.info("Record");
				startRecording();
			}

			// CTRL+SHIFT+ESC
			if (e.getKeyCode() == NativeKeyEvent.VK_ESCAPE && isShiftPressed && isCtrlPressed){                	
				logger.info("Stop");
				GlobalScreen.unregisterNativeHook();
				escapeSignal.countDown();
			}

		}

		public void nativeKeyReleased(NativeKeyEvent e) {
			//	                System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
		}

		public void nativeKeyTyped(NativeKeyEvent e) {
			//	                System.out.println("Key Typed: " + e.getKeyText(e.getKeyCode()));
		}

	}

}
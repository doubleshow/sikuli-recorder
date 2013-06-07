package org.sikuli.recorder;

import java.io.File;
import java.util.List;

import org.sikuli.recorder.detector.EventDetector;
import org.sikuli.recorder.detector.MouseEventDetector;
import org.sikuli.recorder.detector.ScreenshotEventDetector;

import com.google.common.collect.Lists;


public class Recorder {
	
	
	public Recorder(){

	}

	public static void main(String[] args) {

		
		//rec.start();
		Recorder rec = new Recorder();
		
		EventDetector c1 = new MouseEventDetector();
		EventDetector c2 = new ScreenshotEventDetector();		
		
		rec.addEventDetector(c1);
		rec.addEventDetector(c2);
		
		rec.start();
	}
	
	private List<EventDetector> detectors = Lists.newArrayList();
	
	private void addEventDetector(EventDetector d) {
		detectors.add(d);		
	}
	
	public void start(){
		for (EventDetector d : detectors){
			d.start();
		}
	}
}
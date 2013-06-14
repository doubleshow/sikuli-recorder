package org.sikuli.recorder.examples;

import java.io.File;

import org.sikuli.api.DesktopScreenRegion;
import org.sikuli.recorder.Recorder;
import org.sikuli.recorder.html.HTMLGenerator;
import org.sikuli.recorder.pptx.PPTXGenerator;

public class HTMLGeneratorExample {
	
	public static void main(String[] args) {
		
		File eventDir = new File("tmp/example");
		eventDir.mkdirs();
		HTMLGenerator.generate(eventDir, new File("tmp/html"));
		
	}
}

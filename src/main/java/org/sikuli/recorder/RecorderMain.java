package org.sikuli.recorder;

import java.io.File;

import org.sikuli.recorder.pptx.PPTXGenerator;

import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;

public class RecorderMain {

	public static void main(String[] args) {	
		
		Command c = new Command();
	    //Args.usage(c);
		Args.parse(c, args);
	       
	    System.out.println("Press [Ctrl-Shift-2] to start recording");
	    System.out.println("Press [Ctrl-Shift-ESC] to stop recording");

		Recorder rec = new Recorder();
		rec.start();
		
		File eventDir = rec.getEventDir();		
		
		File output;
		if (c.output == null)
			output = new File(eventDir.getName() + ".pptx");
		else
			output = new File(c.output);
		
		
		System.out.println("Captured events are saved in " + eventDir);		
		System.out.println("Writing pptx to " + output);
		
		PPTXGenerator.generate(eventDir, output);
	}

	
	static class Command {
		 @Argument(value = "output", description = "This is the output file (e.g., output.pptx)", required = false)
	        private String output = null;
		 
	}

       
}

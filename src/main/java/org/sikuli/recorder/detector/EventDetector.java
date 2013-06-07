package org.sikuli.recorder.detector;

import org.sikuli.recorder.DefaultEventWriter;
import org.sikuli.recorder.EventWriter;
import org.sikuli.recorder.event.Event;

public class EventDetector {
	private EventWriter writer = null;
		
	public EventDetector(){
		writer = new DefaultEventWriter();
	}
	
	public void eventDetected(Event event){
		if (writer != null)
			writer.write(event);
	}
	
	public void start(){		
	}

	public void stop(){		
	}
	
	public void setWriter(EventWriter writer) {
		this.writer = writer;
	}
}
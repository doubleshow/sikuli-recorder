package org.sikuli.recorder.event;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class Events {
	static public List<Event> readEventsFrom(File inputDir){

		List<Event> events = Lists.newArrayList();


		File[] files = inputDir.listFiles();

		// sort the files so that they are chronologically ordered
		// we assume that the name ordering reflects its chronological order 
		Arrays.sort(files);

		for (File f : files){

			String s = f.getPath();

			if (s.contains("click.txt")){
				try {
					String jsonString = Files.toString(f, Charsets.US_ASCII);
					ClickEvent clickEvent = ClickEvent.createFromJSON(jsonString);
					events.add(clickEvent);
				} catch (IOException e1) {
				}

			}else if (s.contains("screenshot.png")){								
				ScreenShotEvent screenShotEvent = ScreenShotEvent.createFromFile(f);				
				events.add(screenShotEvent);				
			}
		}
		return events;
	}

	static public List<ClickEventGroup> getClickEventGroups(List<Event> events){

		List<ClickEventGroup >slideDataList = Lists.newArrayList(); 
		for (int i = 0; i < events.size(); ++i) {
			Event e = events.get(i);
			if (e instanceof ClickEvent){

				ClickEvent clickEvent = (ClickEvent) e;				
				ScreenShotEvent screenShotEventBefore = Events.findScreenShotEventBefore(events, i);
				if (screenShotEventBefore != null){					

					ClickEventGroup data = new ClickEventGroup();				
					data.setClickEvent(clickEvent);
					data.setScreenShotEventBefore(screenShotEventBefore);

					slideDataList.add(data);
				}
			}
		}

		return slideDataList;
	}
	
	static public ScreenShotEvent findScreenShotEventBefore(List<Event> events, int start) {
		for (int i = start; i >= 0; i--){
			Event e = events.get(i);
			if (e instanceof ScreenShotEvent)
				return (ScreenShotEvent) e;
		}
		return null;
	}



}
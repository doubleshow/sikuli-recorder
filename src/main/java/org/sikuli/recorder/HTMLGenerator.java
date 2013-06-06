package org.sikuli.recorder;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.sikuli.api.API;
import org.sikuli.recorder.event.ClickEvent;
import org.sikuli.recorder.event.Event;
import org.sikuli.recorder.event.ScreenShotEvent;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

import org.stringtemplate.v4.*;

public class HTMLGenerator {

	List<Event> events;

	public List<Event> readEventsFrom(File inputDir){

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


	public void generate(File inputDir, File outputDir){
		List<Event> events = readEventsFrom(inputDir);

		if (!outputDir.exists()){
			outputDir.mkdir();
		}

		File imageDir = new File(outputDir, "images");
		if (!imageDir.exists()){
			imageDir.mkdir();
		}


		ST pageListST = stg.getInstanceOf("page_list");
		ST indexST = stg.getInstanceOf("index");
		String firstPageUrl = null;

		int no = 1;
		for (int i = 0; i < events.size(); ++i) {

			Event e = events.get(i);

			if (e instanceof ClickEvent){

				ClickEvent clickEvent = (ClickEvent) e;				
				ScreenShotEvent screenShotEventBefore = findScreenShotEventBefore(events, i);
				if (screenShotEventBefore != null){					
				
				}

				// copy image file
				try {
					File src = screenShotEventBefore.getFile();
					File dest = new File(imageDir, src.getName());
					Files.copy(src, dest);
				} catch (IOException e2) {
					e2.printStackTrace();
				}				

				ST pageST = stg.getInstanceOf("page");
				pageST.add("x", clickEvent.getX()-25);
				pageST.add("y", clickEvent.getY()-25);
				pageST.add("xc", clickEvent.getX()-5);
				pageST.add("yc", clickEvent.getY()-5);
				pageST.add("imgurl", "images/" + screenShotEventBefore.getFile().getName());

				String pageName = "" + no;
				String pageUrl = pageName + ".html";

				File outFile = new File(outputDir, pageUrl);
				try {
					Files.write(pageST.render(), outFile, Charsets.UTF_8);
				} catch (IOException e1) {				

				}

				pageListST.addAggr("pages.{url,name}", pageUrl, pageName);

				if (firstPageUrl == null){
					firstPageUrl = pageUrl;
					indexST.add("firstPageUrl", firstPageUrl);
				}				

				// increment the page counter
				no++;
			}

		}



		File indexFile = new File(outputDir, "index.html");				
		File pageListFile = new File(outputDir, "page-list.html");
		try {			
			Files.write(pageListST.render(), pageListFile, Charsets.UTF_8);
			Files.write(indexST.render(), indexFile, Charsets.UTF_8);
		} catch (IOException e1) {				

		}

	}

	private ScreenShotEvent findScreenShotEventBefore(List<Event> events, int start) {
		for (int i = start; i >= 0; i--){
			Event e = events.get(i);
			if (e instanceof ScreenShotEvent)
				return (ScreenShotEvent) e;
		}
		return null;
	}

	static STGroup stg = new STGroupFile("org/sikuli/recorder/html.stg", "utf-8", '$', '$');

	public static void main(String[] args) throws MalformedURLException {

		File inputDir = new File("output/2013-06-06-15-14-21");		
		File outputDir = new File("html");
		
		HTMLGenerator g = new HTMLGenerator();		
		g.generate(inputDir, outputDir);
		
		URI uri = new File(outputDir, "index.html").toURI();
		URL url = uri.toURL();
		API.browse(url);
	}

}

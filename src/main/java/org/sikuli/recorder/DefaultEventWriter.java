package org.sikuli.recorder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.json.simple.JSONObject;
import org.sikuli.recorder.event.ClickEvent;
import org.sikuli.recorder.event.Event;
import org.sikuli.recorder.event.ScreenShotEvent;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class DefaultEventWriter implements EventWriter {

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss.SSS");	
	static public String getTimeStamp(){
		return sdf.format(new Date());	
	}


	private File outputDir;
	public DefaultEventWriter(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String name = sdf.format(new Date());	
		try {
			File temp = Utils.createTempDirectory();
			outputDir = new File(temp, name);
			outputDir.mkdir();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// TODO remove temp directory when done		
	}

	@Override
	public void write(Event event) {
		//System.out.println(getEventDir() + "/" + event.getClass());

		if (event.getClass().equals(ScreenShotEvent.class)){

			String name = getTimeStamp() + ".screenshot.png";

			BufferedImage image = ((ScreenShotEvent)event).getImage();
			try {
				ImageIO.write(image, "png", new File(getEventDir(), name));
			} catch (IOException e) {
			}

			System.out.println("ScreenShot --> " + name);

		} else if (event.getClass().equals(ClickEvent.class)){

			String name = getTimeStamp() + ".click.txt";

			JSONObject json = event.toJSON();
			try {
				Files.write(json.toJSONString(), new File(getEventDir(), name), Charsets.US_ASCII);
			} catch (IOException e) {
			}

			System.out.println("Click --> " + name);
		}
	}

	public File getEventDir() {
		return outputDir;
	}

	public void setEventDir(File outputDir) {
		this.outputDir = outputDir;
	}		
}
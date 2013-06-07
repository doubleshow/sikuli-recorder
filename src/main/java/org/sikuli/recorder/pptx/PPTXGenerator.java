package org.sikuli.recorder.pptx;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.sikuli.recorder.Utils;
import org.sikuli.recorder.Zip;
import org.sikuli.recorder.event.ClickEvent;
import org.sikuli.recorder.event.ClickEventGroup;
import org.sikuli.recorder.event.Event;
import org.sikuli.recorder.event.Events;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class PPTXGenerator {

	static STGroup group = new STGroupDir("org/sikuli/recorder/pptx","utf-8", '$', '$');


	static class BoxSTModel {
		private int x;
		private int y;
		private int cx;
		private int cy;
		public int getX() {
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		public int getY() {
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}
		public int getCx() {
			return cx;
		}
		public void setCx(int cx) {
			this.cx = cx;
		}
		public int getCy() {
			return cy;
		}
		public void setCy(int cy) {
			this.cy = cy;
		}
	}

	static class SlideSTModel {
		private int id;
		private int rid;
		private String name;
		private String imageName;
		private File imageSrc;
		private BoxSTModel box;

		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getRid() {
			return rid;
		}
		public void setRid(int rid) {
			this.rid = rid;
		}
		public String getImageName() {
			return imageName;
		}
		public void setImageName(String imageName) {
			this.imageName = imageName;
		}
		public File getImageSrc() {
			return imageSrc;
		}
		public void setImageSrc(File imageSrc) {
			this.imageSrc = imageSrc;
		}
		public BoxSTModel getBox() {
			return box;
		}
		public void setBox(BoxSTModel box) {
			this.box = box;
		}
	}



	public static void main(String[] args) throws IOException{

		File eventDir = new File("output/2013-06-06-15-14-21");
		File outputFile = new File("e.pptx");
		generate(eventDir, outputFile);
	}
	
	public static void generate(File eventDir, File outputFile){
		File pptxSkeletonDir;
		try {
			pptxSkeletonDir = createSkeletonDir();
			generateFiles(pptxSkeletonDir, eventDir);
			Zip.zipDir(pptxSkeletonDir, outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static File createSkeletonDir() throws IOException{
		InputStream inputStream = PPTXGenerator.class.getResourceAsStream("skeleton.pptx");
		File temp = File.createTempFile("temp","pptx");
		Utils.stream2file(inputStream, temp);
		File pptxSkeletonDir = Utils.createTempDirectory();
		Zip.unzip(temp, pptxSkeletonDir);
		return pptxSkeletonDir;
	}

	public static void generateFiles(File pptxSkeletonDir, File eventDir) throws IOException {

		File outputDir = pptxSkeletonDir;

		List<Event> events = Events.readEventsFrom(eventDir);
		List<ClickEventGroup> clickEventGroups = Events.getClickEventGroups(events);

		String L = File.separator;

		ST presentation_xml_ST = group.getInstanceOf("presentation_xml");	
		ST presentation_xml_rels_ST = group.getInstanceOf("presentation_xml_rels");
		ST content_types_xml_ST = group.getInstanceOf("content_types_xml");
		ST app_xml_ST = group.getInstanceOf("app_xml");

		List<SlideSTModel> slides = createSlideSTModels(clickEventGroups);

		//copyImagesToPPTX(slides);

		for (SlideSTModel slide : slides) {
			ST slide_xml_ST = group.getInstanceOf("slide_xml");
			ST slide_xml_rels_ST = group.getInstanceOf("slide_xml_rels");

			// copy image
			File dest = new File(outputDir, "ppt" + L + "media" + L + slide.getImageName());
			Files.copy(slide.getImageSrc(), dest);

			slide_xml_ST.add("slide", slide);
			slide_xml_rels_ST.add("slide", slide);

			File slideFile = new File(outputDir, "ppt" + L + "slides" + L + slide.getName());				
			File slide_relsFile = new File(outputDir, "ppt" + L + "slides" + L + "_rels" + L + slide.getName() + ".rels");

			Files.write(slide_xml_ST.render(), slideFile, Charsets.UTF_8);
			Files.write(slide_xml_rels_ST.render(), slide_relsFile, Charsets.UTF_8);		

			presentation_xml_ST.add("slide",slide);
			presentation_xml_rels_ST.add("slide",slide);
			content_types_xml_ST.add("slide", slide);				
		}

		app_xml_ST.add("count", slides.size());

		File presentationFile = new File(outputDir, "ppt" + L + "presentation.xml");
		File presentation_relsFile = new File(outputDir, "ppt" + L + "_rels" + L + "presentation.xml.rels");
		File content_types_file = new File(outputDir, "[Content_Types].xml");
		File app_file = new File(outputDir, "docProps" + L + "app.xml");

		Files.write(presentation_xml_ST.render(), presentationFile, Charsets.UTF_8);
		Files.write(presentation_xml_rels_ST.render(), presentation_relsFile, Charsets.UTF_8);
		Files.write(content_types_xml_ST.render(), content_types_file, Charsets.UTF_8);
		Files.write(app_xml_ST.render(), app_file, Charsets.UTF_8);
	}


	private static List<SlideSTModel> createSlideSTModels(List<ClickEventGroup> clickEventGroups) {
		int rid = 8;
		int id = 256; 
		int no = 1; // slide1.xml, slide2.xml ...
		List<SlideSTModel> slides = Lists.newArrayList();

		int imageHeight = 0;
		int imageWidth = 0; 				

		for (ClickEventGroup g : clickEventGroups ){

			File imageSrc = g.getScreenShotEventBefore().getFile();
			ClickEvent clickEvent = g.getClickEvent();
			int x = clickEvent.getX();
			int y = clickEvent.getY();

			// assume all images are of the same size
			// so we read the image dimensions only once			
			if (imageWidth == 0 && imageHeight == 0){
				BufferedImage image = null;
				try {
					image = ImageIO.read(imageSrc);
					imageHeight = image.getHeight();
					imageWidth = image.getWidth();
				} catch (IOException e) {
					e.printStackTrace();							
				}
			}

			// dimensions that will scale to fit the slide
			int imageCx = 9144000;
			int imageCy = 5833994;

			int boxX = (int) (1.0 * x *  imageCx / imageWidth);
			int boxY = (int) (1.0 * y *  imageCy / imageHeight);

			// fixed dimensions
			int boxCx = 317500;
			int boxCy = 324110;					

			// center the box
			boxX -= (boxCx/2);
			boxY -= (boxCy/2);

			String name = "slide" + no + ".xml"; // slide1.xml, slide2.xml ... etc
			String imageName = "image" + no + ".png";

			BoxSTModel box = new BoxSTModel();
			box.setX(boxX);
			box.setY(boxY);
			box.setCx(boxCx);
			box.setCy(boxCy);			

			SlideSTModel slide = new SlideSTModel();
			slide.setName(name);
			slide.setId(id);
			slide.setRid(rid);
			slide.setImageName(imageName);
			slide.setImageSrc(imageSrc);
			slide.setBox(box);

			rid++;
			id++;
			no++;

			slides.add(slide);
		}
		return slides;
	}	

}

package org.sikuli.recorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.sikuli.recorder.event.Event;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class PPTXGenerator {

	static STGroup group = new STGroupDir("org/sikuli/recorder","utf-8", '$', '$');

	
	
	static class SlideSTModel {
		private int id;
		private int rid;
		private String name;
		private String imageName;
		private File imageSrc;
		
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
	}
	
		
	public static void main(String[] args) throws IOException{

		File outputDir = new File("test/pptx");
		File inputDir = new File("test/src");

		deleteFolder(outputDir);
		copyFolder(inputDir, outputDir);


		File eventDir = new File("output/2013-06-06-15-14-21");
		List<Event> events = HTMLGenerator.readEventsFrom(eventDir);
		List<ClickEventGroup> clickEventGroups = HTMLGenerator.getClickEventGroups(events);
				
		
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
		
		
		for (ClickEventGroup g : clickEventGroups ){
		
			File imageSrc = g.getScreenShotEventBefore().getFile();
			
			String name = "slide" + no + ".xml"; // slide1.xml, slide2.xml ... etc
			String imageName = "image" + no + ".png";
			SlideSTModel slide = new SlideSTModel();
			slide.setName(name);
			slide.setId(id);
			slide.setRid(rid);
			slide.setImageName(imageName);
			slide.setImageSrc(imageSrc);
			
			rid++;
			id++;
			no++;
			
			slides.add(slide);
		}
		return slides;
	}	


	public static void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if(files!=null) { //some JVMs return null for empty dirs
			for(File f: files) {
				if(f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}


	public static void copyFolder(File src, File dest)
			throws IOException{

		if(src.isDirectory()){

			//if directory not exists, create it
			if(!dest.exists()){
				dest.mkdir();
				//System.out.println("Directory copied from " 
				//		+ src + "  to " + dest);
			}

			//list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				//construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				//recursive copy
				copyFolder(srcFile,destFile);
			}

		}else{
			//if file, then copy it
			//Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest); 

			byte[] buffer = new byte[1024];

			int length;
			//copy the file content in bytes 
			while ((length = in.read(buffer)) > 0){
				out.write(buffer, 0, length);
			}

			in.close();
			out.close();
			//System.out.println("File copied from " + src + " to " + dest);
		}
	}

}

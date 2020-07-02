package com.techm.orion.utility;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
//import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.commons.io.IOUtils;


public class TextReport {
	
	public static void main(String args[])throws IOException
	{	
		TextReport.writeFile("/opt/jboss/HP00487288/ATT/Orion","report.txt","asd");
			
	}
	
	public static void writeFile(String responseDownloadPath,String filename,String content) throws IOException
	{
		/*********************create text file code here****************************/
		//String FILENAME = "D:\\HP00487288\\ATT\\Orion\\report.txt";
		String FILENAME =responseDownloadPath+"/"+filename;
    	BufferedWriter bw = null;
		FileWriter fw = null;
		
		try {

			//content = "This is the content to write into file\n";
    		fw = new FileWriter(FILENAME);
			bw = new BufferedWriter(fw);
			bw.write(content);

			/*System.out.println("Done");
				
			FileInputStream inputStream = new FileInputStream(FILENAME);
			try {System.out.println("Done");
			   // String everything = IOUtils.toString(inputStream);
				@SuppressWarnings("deprecation")
				String everything = IOUtils.toString(inputStream);
				System.out.println(everything);
			   // List<String> lines = FileUtils.readLines(new File("untitled.txt"));
			   // List<String> lines = FileUtils.readLines(new File("untitled.txt"));
			} finally {
			    inputStream.close();
			}*/
		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}
    	
    	/*********************************************Ends here*********************************/ 	
    	

	}
	
	
}

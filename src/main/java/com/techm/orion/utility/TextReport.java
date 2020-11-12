package com.techm.orion.utility;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextReport {
	private static final Logger logger = LogManager.getLogger(TextReport.class);

	public static void writeFile(String path, String fileName, String content) {
		fileWriter(path, fileName, content, null);
	}

	public static void writeFile(String path, String fileName, String content, String type) {
		fileWriter(path, fileName, content, type);
	}

	private static void fileWriter(String path, String fileName, String content, String type) {
		FileWriter fileWriter = null;
		try {
			logger.info("TextReport - fileWriter - filename - " + fileName);
			String filePath = path + fileName;
			logger.info("TextReport - fileWriter - filePath - " + filePath);
			if ("configurationGeneration".equals(type)) {
				fileWriter = new FileWriter(filePath, true);
			} else if ("headerGeneration".equals(type)) {
				fileWriter = new FileWriter(filePath, false);
			} else {
				fileWriter = new FileWriter(filePath);
			}
			bufferedWriter(fileWriter, content);
		} catch (IOException exe) {
			logger.error("IOException while Writing the file - " + exe.getMessage());
		} finally {
			try {
				if (fileWriter != null) {
					fileWriter.close();
				}
			} catch (IOException exe) {
				logger.error("IOException while closing the fileWriter - " + exe.getMessage());
			}
		}
	}

	private static void bufferedWriter(FileWriter fileWriter, String content) {
		try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);) {
			// content = "This is the content to write into file\n";
			bufferedWriter.write(content);
		} catch (IOException exe) {
			logger.error("IOException while Writing the file - " + exe.getMessage());
		}
	}
	
	public static String readFile(String path)
	{
		String content=null;
		try {
			content=new String(Files.readAllBytes(Paths.get(path)));
		} catch (IOException e) {
			logger.info(e);
			e.printStackTrace();
		}
		return content;
	}

}

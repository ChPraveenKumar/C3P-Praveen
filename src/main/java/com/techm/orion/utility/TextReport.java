package com.techm.orion.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextReport {
	private static final Logger logger = LogManager.getLogger(TextReport.class);

	public static void writeFile(String responseDownloadPath, String filename,
			String content) {
		logger.info("TextReport - writeFile - filename - " + filename);
		/*
		 * Removed \\ from the code. This needs to be maintains at properties
		 * level to support different OS.
		 */
		String FILENAME = responseDownloadPath + filename;
		logger.info("TextReport - writeFile - filepath - " + FILENAME);
		try (FileWriter fileWriter = new FileWriter(FILENAME);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);) {
			// content = "This is the content to write into file\n";
			bufferedWriter.write(content);

		} catch (IOException exe) {
			logger.error("IOException while Writing the file - "
					+ exe.getMessage());
		}

	}

	public static void writeFile(String responseDownloadPath, String filename,
			String content, String type) {
		logger.info("TextReport - writeFile - filename - " + filename);
		/*
		 * Removed \\ from the code. This needs to be maintains at properties
		 * level to support different OS.
		 */
		String FILENAME = responseDownloadPath + filename;
		if (type.equalsIgnoreCase("configurationGeneration")) {
			try(FileWriter fileWriter = new FileWriter(FILENAME, true);
					BufferedWriter bufferedWriter = new BufferedWriter(
							fileWriter);)  {
				bufferedWriter.write(content);
			} catch (IOException ioe) {
				logger.error("IOException: " + ioe.getMessage());
			}
		} else if (type.equalsIgnoreCase("headerGeneration")) {
			File file = new File(FILENAME);
			try(FileWriter fileWriter = new FileWriter(FILENAME, false);
					BufferedWriter bufferedWriter = new BufferedWriter(
							fileWriter);) {
				bufferedWriter.write(content);


			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			logger.info("TextReport - writeFile - filepath - " + FILENAME);
			try (FileWriter fileWriter = new FileWriter(FILENAME);
					BufferedWriter bufferedWriter = new BufferedWriter(
							fileWriter);) {
				// content = "This is the content to write into file\n";
				bufferedWriter.write(content);

			} catch (IOException exe) {
				logger.error("IOException while Writing the file - "
						+ exe.getMessage());
			}
		}
	}

}

package com.techm.orion.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/DownloadBasicConfigFile")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class DownloadBasicConfigFile {
	public static String TSA_PROPERTIES_FILE = "TSA.properties";

	public static final Properties TSA_PROPERTIES = new Properties();

	@GET
	@Produces("application/x-download")
	@ResponseBody
	@RequestMapping(method = { RequestMethod.GET }, value = { "/getBasicConfigFile" })
	public void getBasicConfigFile(HttpServletResponse response, @RequestParam String requestId,
			@RequestParam String version) {
		try {
			DownloadBasicConfigFile.loadProperties();
			String path = DownloadBasicConfigFile.TSA_PROPERTIES.getProperty("responseDownloadPath") + requestId
					+ "V" + version + "_basicConfiguration.txt";
			File file = new File(path);
			if (!file.exists()) {

				response.setHeader("Access-Control-Expose-Headers", "*");
				response.sendError(0, "file not found");
			} else {
				response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
				response.setStatus(HttpServletResponse.SC_OK);
				response.setHeader("Content-Disposition",
						"attachment; filename=" + requestId + "V" + version + "_basicConfiguration.txt");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("txt/plain");
				FileInputStream fileIn = new FileInputStream(file);
				IOUtils.copy(fileIn, response.getOutputStream());
				fileIn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean loadProperties() throws IOException {
		InputStream tsaPropFile = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(TSA_PROPERTIES_FILE);

		try {
			TSA_PROPERTIES.load(tsaPropFile);
		} catch (IOException exc) {
			exc.printStackTrace();
			return false;
		}
		return false;
	}
}

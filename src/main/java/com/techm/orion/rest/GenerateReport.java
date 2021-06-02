package com.techm.orion.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.techm.orion.utility.TSALabels;

@Controller
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/GenerateReport")
public class GenerateReport {
	private static final Logger logger = LogManager.getLogger(GenerateReport.class);

	/*
	 * Owner: Rahul Tiwari Module: Generate Report Logic: To generate pdf from html
	 * data custom tests
	 */
	@POST
	@RequestMapping(value = "/generatePdf", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response generatePdf(HttpServletResponse response, @RequestBody String requestInfo)
			throws IOException, ParseException {
		Response build = null;
		// Provide the path of python script file location
		// String pythonScriptFolder = "D:/PDF_Ptyhon_Folder/inputfile.py";

		// Provide the path of html file location
		String home = TSALabels.DOWNLOAD_PATH.getValue();
		File downloadHtmlFilePath = new File(home + "report" + ".html");

		// Provide the name of generated pdf file Name with request and version
		String fileName = "Certification_Test_Report";
		String requestData = null;
		String requestId = null;
		String version = null;
		boolean isReportGenerated = false;

		try {

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(requestInfo);

			if (json != null) {
				requestData = (String) json.get("requestData");
				requestId = (String) json.get("requestId");
				version = (String) json.get("version");
			}

			// Write json(requestData) data into HTML File
			FileUtils.writeStringToFile(downloadHtmlFilePath, requestData);

			// To Generate pdf file from html file using python with path from
			// where we need to read html file and write PDF File
			StringBuilder stringbuilder = new StringBuilder();
			stringbuilder.append(home).append(requestId).append("_").append(fileName).append("_").append("V")
					.append(version).append(".pdf");
			isReportGenerated = generateReport(requestData, stringbuilder.toString());
			File file = new File(stringbuilder.toString());
			if (!file.exists()) {
				response.setHeader("error", "file not found");
				isReportGenerated = false;
			} else {
				// Download file using browse option
				response.setStatus(HttpServletResponse.SC_OK);
				response.setHeader("Access-Control-Allow-Origin", "*");
				response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/pdf");
				FileInputStream fileIn = new FileInputStream(file);
				IOUtils.copy(fileIn, response.getOutputStream());
				fileIn.close();
				isReportGenerated =true;
			}
		} catch (Exception e) {
			String cause = e.getMessage();
			if (cause.equals("python: not found"))
				logger.info("No python interpreter found.");
			logger.info("file is not found!");
			build = Response.status(404).entity(e.getMessage()).build();
		}
		build = Response.status(200).entity(isReportGenerated).build();
		return build;
	}
	
	@GET
	@RequestMapping(value = "/downloadCOBTemplate", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<FileSystemResource> downloadTemplateCOB() {
		String customerOnBoardingFileFolder = TSALabels.COBTemplate.getValue() + "CustomerOnboardTemplate.csv";
		File templateFile = null;
		try {
			templateFile = new File(customerOnBoardingFileFolder);

		} catch (Exception e) {
			logger.error("Error occurred while downloading file {}", e);
		}
		return ResponseEntity.ok()
				.header("Content-Disposition", "attachment; filename=" + templateFile.getName() + ".csv")
				.contentLength(templateFile.length()).contentType(MediaType.parseMediaType("text/csv"))
				.body(new FileSystemResource(templateFile));
	}
	
	// Endpoint for discrepancy report
	/*@POST
	@RequestMapping(value = "/generateDiscrepancyPdf", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response generateDiscrepancyPdf(HttpServletResponse response, @RequestBody String requestInfo)
			throws IOException, ParseException {
		Response build = null;
		// Provide the path of python script file location
		//String pythonScriptFolder = "D:/PDF_Ptyhon_Folder/inputfile.py";

		// Provide the path of html file location
		String home = System.getProperty("user.home");
		File downloadHtmlFilePath = new File(home + "/Downloads/" + "report" + ".html");

		// Provide the name of generated pdf file Name with request and version
		String fileName = "Certification_Test_Report";
		String requestData = null;
		String requestId = null;
		String version = null;
		String pythonScriptFolder = TSALabels.PYTHON_SCRIPT_PATH.getValue() + "pdfConverter.py";
		
		File pythonFileCheck = new File(pythonScriptFolder);
		try {
			if (!pythonFileCheck.exists()) {
				throw new Exception("file is not found!");
			}
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(requestInfo);

			if (json != null) {
				requestData = (String) json.get("requestData");
				requestId = (String) json.get("requestId");
				version = (String) json.get("version");
			}

			// Write json(requestData) data into HTML File
			FileUtils.writeStringToFile(downloadHtmlFilePath, requestData);

			// To Generate pdf file from html file using python with path from
			// where we need to read html file and write PDF File
			String[] cmd = { "python", pythonFileCheck.getPath(), downloadHtmlFilePath.getPath(),
					home + "/" + "Downloads" + "/" + requestId + "_" + fileName + "_" + "V" + version + ".pdf" };
			Process processInstance = Runtime.getRuntime().exec(cmd);
			Thread.sleep(1700);

			File file = new File(
					home + "/" + "Downloads" + "/" + requestId + "_" + fileName + "_" + "V" + version + ".pdf");
			if (!file.exists()) {
				response.setHeader("error", "file not found");

			} else {
				// Donwload file using browse option
				response.setStatus(HttpServletResponse.SC_OK);
				response.setHeader("Access-Control-Allow-Origin", "*");
				response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/pdf");
				FileInputStream fileIn = new FileInputStream(file);
				IOUtils.copy(fileIn, response.getOutputStream());
				fileIn.close();
				// logger.info("\n" + "end of displayFile Service ");
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(processInstance.getErrorStream()));
			String err = reader.readLine();
			while ((err = reader.readLine()) != null) {
				logger.info(err);
			}
		} catch (Exception e) {
			String cause = e.getMessage();
			if (cause.equals("python: not found"))
				logger.info("No python interpreter found.");
			build = Response.status(404).entity(e.getMessage()).build();
		}
		return build;
	}*/
	
	@SuppressWarnings({ "unchecked", "unused" })
	private boolean generateReport(String input, String output) {
		boolean isReportGenerated = false;
		try {
			RestTemplate restTemplate = new RestTemplate();
			JSONObject request = new JSONObject();
			request.put(new String("input"), input);
			request.put(new String("output"), output);
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(request, headers);
			String url = TSALabels.PYTHON_SERVICES.getValue() + TSALabels.PYTHON_REPORT.getValue();
			String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
			logger.info("response of generateReport is " + response);

			if (response.contains("Success")) {
				isReportGenerated = true;
			}
		} catch (HttpClientErrorException exe) {
			logger.error("HttpClientErrorException - generateReport -> " + exe.getMessage());
		} catch (Exception exe) {
			logger.error("Exception - generateReport->" + exe.getMessage());
			exe.printStackTrace();
		}
		logger.info("End - generateReport ->" + isReportGenerated);
		return isReportGenerated;
	}
}

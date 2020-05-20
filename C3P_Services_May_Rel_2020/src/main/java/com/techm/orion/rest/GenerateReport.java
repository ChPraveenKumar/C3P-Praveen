package com.techm.orion.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/GenerateReport")
public class GenerateReport  {
	
	/*
	 * Owner: Rahul Tiwari Module: Generate Report  Logic: To generate pdf from html data
	 * custom tests
	 */
	@POST
	@RequestMapping(value = "/generatePdf", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response generatePdf(@RequestBody String requestInfo) throws IOException, ParseException {
		Response build = null;
		// Provide the path of python script file location
		String pythonScriptFolder = "D:\\PDF_Ptyhon_Folder";

		// Provide the path of html file location
		String path = "D:\\PDF_Ptyhon_Folder\\report.html";
		
		// Provide the path of generated pdf file where we want to store
		String filePath = "D:\\PDF_Ptyhon_Folder\\";

		// Provide the name of generated pdf file Name with request and version
		String fileName = "Certification_Test_Report";
		String requestData = null;
		String requestId = null;
		String version = null;

		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(requestInfo);

		if (json != null) {
			requestData = (String) json.get("requestData");
			requestId = (String) json.get("requestId");
			version = (String) json.get("version");
		}
		try {
			// Write json(requestData) data into HTML File
			FileUtils.writeStringToFile(new File(path), requestData);
			
			// To Generate pdf file from html file using python with path from where we need to read html file and write PDF File
			String[] cmd = { "python", pythonScriptFolder + "\\inputfile.py", path, filePath+requestId + "_" +fileName + "_"+"V"+version + ".pdf" };
			Process processInstance = Runtime.getRuntime().exec(cmd);

			BufferedReader reader = new BufferedReader(new InputStreamReader(processInstance.getErrorStream()));
			String err = reader.readLine();
			while ((err = reader.readLine()) != null) {
				System.out.println(err);
			}
		} catch (Exception e) {
			String cause = e.getMessage();
			if (cause.equals("python: not found"))
				System.out.println("No python interpreter found.");
		}
		// build = Response.status(200).entity(jsonValue).build();
		return build;
	}
}

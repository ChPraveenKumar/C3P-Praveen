package com.techm.c3p.core.rest;

import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.techm.c3p.core.utility.ExcelToJSONConverter;

@Controller
@RequestMapping("/GetAllExcelData")
public class GetAllExcelData implements Observer {
	private static final Logger logger = LogManager.getLogger(GetAllExcelData.class);
	
	@Autowired
	private ExcelToJSONConverter excelToJSONConverter ;

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/get", headers = "content-type=multipart/*", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Response getAll(@RequestParam("file") MultipartFile file) {

		JSONObject obj = new JSONObject();

		try {
			
			JSONObject response = excelToJSONConverter.convert(file);
			obj.put(new String("output"), response.toString());

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

}

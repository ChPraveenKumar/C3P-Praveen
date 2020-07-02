package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.pojo.AlertInformationPojo;
import com.techm.orion.service.DcmConfigService;
import com.techm.orion.utility.ExcelToJSONConverter;

@Controller
@RequestMapping("/GetAllExcelData")
public class GetAllExcelData implements Observer {


    	@POST
    	@RequestMapping(value = "/get", headers = "content-type=multipart/*",method = RequestMethod.POST, produces = "application/json")
    	
	@ResponseBody
    public Response getAll(@RequestParam("file") MultipartFile file) {

	JSONObject obj = new JSONObject();
	String jsonMessage = "";
	String jsonArray="";

	

	try {
	   ExcelToJSONConverter converter=new ExcelToJSONConverter();
	   JSONObject response=converter.convert(file);
	   obj.put(new String("output"), response.toString());
	    
		} catch (Exception e) {
		    System.out.println(e);
		}

	return Response
		.status(200)
		.header("Access-Control-Allow-Origin", "*")
		.header("Access-Control-Allow-Headers",
			"origin, content-type, accept, authorization")
		.header("Access-Control-Allow-Credentials", "true")
		.header("Access-Control-Allow-Methods",
			"GET, POST, PUT, DELETE, OPTIONS, HEAD")
		.header("Access-Control-Max-Age", "1209600").entity(obj)
		.build();
    }

    @Override
    public void update(Observable o, Object arg) {
	// TODO Auto-generated method stub

    }

}

package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.GET;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.entitybeans.EIPAMEntity;
import com.techm.orion.repositories.EIPAMEntityRepository;
import com.techm.orion.service.DcmConfigService;

@Controller
@RequestMapping("/GetAllIpamData")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class GetAllIPAMData implements Observer {

	@Autowired
	EIPAMEntityRepository eipamEntityRepository;

	RequestInfoDao requestInfoDao = new RequestInfoDao();

	@GET
	
	@RequestMapping(value = "/getAll", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response getAll() {

		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String jsonArray = "";

		
		EIPAMEntity entity = new EIPAMEntity();
		List<EIPAMEntity> detailsList = new ArrayList<EIPAMEntity>();
		DcmConfigService dcmConfigService = new DcmConfigService();
		boolean ipvalue;
		try {
			detailsList = eipamEntityRepository.findAll();
		    if(1==entity.getStatus()){
		    	
				entity.setIpUsed(true);
				ipvalue =true;
				
				} 
			else {
				entity.setIpUsed(false);
				ipvalue =false;
				}
		    
			obj.put(new String("ipStatus"), ipvalue);
			jsonArray = new Gson().toJson(detailsList);
			obj.put(new String("output"), jsonArray);
		
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

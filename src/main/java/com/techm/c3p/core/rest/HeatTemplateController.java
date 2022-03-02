package com.techm.c3p.core.rest;


import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;




import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;




import com.google.gson.Gson;
import com.techm.c3p.core.entitybeans.HeatTemplate;
import com.techm.c3p.core.entitybeans.MasterCharacteristicsEntity;
import com.techm.c3p.core.pojo.CategoryDropDownPojo;
import com.techm.c3p.core.repositories.HeatTemplateRepository;
import com.techm.c3p.core.repositories.MasterCharacteristicsRepository;
import com.techm.c3p.core.service.CategoryDropDownService;


@Controller
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/heatTemplate")
public class HeatTemplateController implements Observer {
	private static final Logger logger = LogManager.getLogger(HeatTemplateController.class);

	@Autowired
	private HeatTemplateRepository heatTemplateRepo;
	
	@Autowired
	private MasterCharacteristicsRepository masterCharachteristicRepository;
	
	
	@Autowired
	private CategoryDropDownService categoryDropDownservice;
	
	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/getAll", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> getAll() {
		logger.info("Inside getAll heatTemplate");
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject obj = new JSONObject();
		try {
			
			List<HeatTemplate> allheatTemplate = heatTemplateRepo.findAll();
			logger.info("Heat Template values "+allheatTemplate);
			
			obj.put("networkFunction", allheatTemplate);
			responseEntity = new ResponseEntity<JSONObject>(obj, HttpStatus.OK);

		} catch (Exception exe) {
			logger.error("Exception occured in get all heat template method - " + exe.getMessage());
			JSONObject errObj = new JSONObject();
			errObj.put("Error", "Exception due to " + exe.getMessage());
			responseEntity = new ResponseEntity<JSONObject>(errObj, HttpStatus.BAD_REQUEST);
		}

		return responseEntity;

	}

	
	
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getAttributes", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> getAttributes(@RequestBody String request) {

		logger.info("Inside getAttributes "+request);
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject obj = new JSONObject();
		JSONArray features = new JSONArray();
		JSONParser parser = new JSONParser();
		String templateID = "", featureList = "", feature = "", rowId = "";
		List<MasterCharacteristicsEntity> masCharacteristics = new ArrayList<>();
		
		
		try {
		 	JSONObject json= (JSONObject) parser.parse(request);
			rowId = json.get("rowId").toString();
			templateID = json.get("variableTemplateId").toString();
			featureList = heatTemplateRepo.findByVariableTemplateId(templateID, rowId);
			
			feature = featureList.substring(1, featureList.length() - 1);
				String[] Feature = feature.split(",");
				logger.info("Feature value is "+Feature);
				for (String f : Feature) {
					List<JSONObject> masCharArray = new ArrayList<JSONObject>();
					JSONObject masCharList = new JSONObject();
					masCharList.put("fId", f);
					//features.put("fName", value);
					//features.put("fReplicationFlag", value);
					masCharacteristics = masterCharachteristicRepository.findByCFId(f);
					logger.info("getAttributes -> masCharacteristics "+masCharacteristics);
					for(MasterCharacteristicsEntity masCList : masCharacteristics) {
						
						JSONObject masObj = new JSONObject();
						masObj.put("categotyLabel", masCList.getcFId());
						masObj.put("cfId", masCList.getcFId());
						masObj.put("characteriscticsId", masCList.getcId());
						masObj.put("id", masCList.getcRowid());
						masObj.put("instanceNumber", masCList.getcFId());
						masObj.put("key", masCList.iscIsKey());
						masObj.put("label", masCList.getcName());
						masObj.put("name", masCList.getcFId());
						masObj.put("poolIds", masCList.getLinkedPools());
						masObj.put("replicationFalg", masCList.getcReplicationind());
						masObj.put("type", masCList.getcType());
						masObj.put("uIComponent", masCList.getcUicomponent());
						masObj.put("validations", masCList.getcValidations());
						/* using Category Name find all category Value */
						if (masCList.getcCategory()!= null) {
							List<CategoryDropDownPojo> allByCategoryName = categoryDropDownservice
									.getAllByCategoryName(masCList.getcCategory());
							masObj.put("categoryLabel",masCList.getcCategory());
							masObj.put("category",allByCategoryName);			
							}
						masCharArray.add(masObj);
						
						
					}
					masCharList.put("attribConfig", masCharArray);
					
					logger.info("getAttributes -> masCharList "+masCharList);
					features.add(masCharList);
				}
				
			obj.put(new String("features"), features);
			responseEntity = new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
		} catch (Exception e) {
			logger.info(e);
		}

		return responseEntity;

	}



	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}

	}

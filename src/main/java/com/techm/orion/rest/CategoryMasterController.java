package com.techm.orion.rest;

import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techm.orion.entitybeans.CategoryDropDownEntity;
import com.techm.orion.entitybeans.CategoryMasterEntity;
import com.techm.orion.repositories.CategoryDropDownDao;
import com.techm.orion.repositories.CategoryMasterDao;
import com.techm.orion.service.CategoryMasterService;
import com.techm.orion.service.TemplateManagementNewService;

@RestController
@RequestMapping
public class CategoryMasterController {

	@Autowired
	private CategoryMasterService categoryService;
	
	@Autowired
	private CategoryMasterDao categoryMasterDao;
	
	@Autowired
	private TemplateManagementNewService templateManagementNewService;
	
	@Autowired
	private CategoryDropDownDao categoryDropDownDao;
	
	@RequestMapping(value = "/category",method = RequestMethod.GET,produces = "application/json")
	public Response getAllCategory() {
		return Response.status(200).entity(categoryService.getAll()).build();
	}

	@POST
	@RequestMapping(value = "/masterOid", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<JSONObject> getMasterOids(@RequestBody String request) throws ParseException {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject json = templateManagementNewService.getMasterOids(request);
		if (json != null) {
			responseEntity = new ResponseEntity<JSONObject>(json, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(json, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}
	
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getCategoryList", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<JSONObject> getCategory(@RequestBody String request) throws ParseException {
		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();
		String userName = null, userRole = null, category = null;
		JSONParser parser = new JSONParser();
		json = (JSONObject) parser.parse(request);
		userName = json.get("userName").toString();
		userRole = json.get("userRole").toString();
		category = json.get("category").toString();
		CategoryMasterEntity categoryMasterEntity = categoryMasterDao.findCategoryId(category);
		List<CategoryDropDownEntity> categoryDropDownEntity = categoryDropDownDao
				.findAllByCategoryId(categoryMasterEntity.getId());
		categoryDropDownEntity.forEach(categoryDropEntity -> {
			JSONObject obj = new JSONObject();
			obj.put("id", categoryDropEntity.getId());
			obj.put("name", categoryDropEntity.getAttribValue());
			array.add(obj);
		});
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("response", array);
		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	}

	@POST
	@RequestMapping(value = "/saveMasterOIds", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<JSONObject> saveMasterOIdInfo(@RequestBody String request) throws ParseException {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject json = templateManagementNewService.saveMasterOids(request);
		if (json != null) {
			responseEntity = new ResponseEntity<JSONObject>(json, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(json, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}
}
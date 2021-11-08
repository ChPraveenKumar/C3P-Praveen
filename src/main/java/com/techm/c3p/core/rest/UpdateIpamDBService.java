package com.techm.c3p.core.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.c3p.core.entitybeans.EIPAMEntity;
import com.techm.c3p.core.repositories.EIPAMEntityRepository;

@Controller
@RequestMapping("/UpdateIpamDBService")
public class UpdateIpamDBService {
	private static final Logger logger = LogManager.getLogger(UpdateIpamDBService.class);
	@Autowired
	private EIPAMEntityRepository eipamEntityRepository;
	
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/	
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/update", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response update(@RequestBody String searchParameters) {
		JSONObject obj = new JSONObject();
		List<EIPAMEntity> detailList = new ArrayList<EIPAMEntity>();
		try {
			detailList = eipamEntityRepository.findAll();
			Gson gson = new Gson();
			EIPAMEntity dto = gson.fromJson(searchParameters, EIPAMEntity.class);
			boolean flag = false;
			if (flag == false) {
				String ipEdit = dto.getIp();
				String maskEdit = dto.getMask();

				for (int i = 0; i < detailList.size(); i++) {
					if (detailList.get(i).getCustomer().equalsIgnoreCase(dto.getCustomer())
							&& detailList.get(i).getSite().equalsIgnoreCase(dto.getSite())) {
						detailList.get(i).setIp(ipEdit);
						detailList.get(i).setMask(maskEdit);
						eipamEntityRepository.save(detailList);
					}

				}
			}

			if (flag == false) {

				obj.put(new String("output"), "Updated Successfully");

			} else {
				obj.put(new String("output"), "Error Saving Data");
			}

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response add(@RequestBody String searchParameters) {

		JSONObject obj = new JSONObject();
		EIPAMEntity entity = new EIPAMEntity();
		List<EIPAMEntity> exsistingRecs = new ArrayList<EIPAMEntity>();
		exsistingRecs = eipamEntityRepository.findAll();

		Gson gson = new Gson();

		EIPAMEntity dto = gson.fromJson(searchParameters, EIPAMEntity.class);
		String ipAdd = dto.getIp();

		boolean flag = false;

		for (int i = 0; i < exsistingRecs.size(); i++) {
			if (exsistingRecs.get(i).getIp().equalsIgnoreCase(ipAdd)) {
				flag = true;
				obj.put(new String("output"), "IP already exists");
				break;

			}

		}

		try {

			if (flag == false) {
				eipamEntityRepository.save(dto);
			}

			if (flag == false) {

				obj.put(new String("output"), "Added Successfully");
				entity.setStatus(0);
				eipamEntityRepository.save(dto);
			} else {
				obj.put(new String("output"), "IP already exists");
			}

		}

		catch (Exception e) {
			logger.error(e);
			return Response.status(200).entity("Data already added and mapped please change Data").build();

		}

		return Response.status(200).entity(obj).build();
	}

}

package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.RequestInfoEntity;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.RequestInfoDetailsRepositories;

@Controller
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/workbench")
public class WorkbenchController {
	private static final Logger logger = LogManager.getLogger(WorkbenchController.class);

	@Autowired
	private RequestInfoDetailsRepositories repo;

	@Autowired
	private DeviceDiscoveryRepository deviceInforepo;

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/getAll", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response deviceDiscovery() {

		JSONObject obj = new JSONObject();
		try {
			List<RequestInfoEntity> requestsSLGC = repo.findRequestsByKeyword("SLGC");
			List<RequestInfoEntity> requestsSNRC = repo.findRequestsByKeyword("SNRC");
			List<RequestInfoEntity> requestsSNNC = repo.findRequestsByKeyword("SNNC");
			List<RequestInfoEntity> requests = new ArrayList<RequestInfoEntity>();
			requests.addAll(requestsSLGC);
			requests.addAll(requestsSNRC);
			requests.addAll(requestsSNNC);
			for (RequestInfoEntity request : requests) {

				DeviceDiscoveryEntity device = deviceInforepo.findByDHostName(request.getHostName());
				if (device.getdNewDevice() == 0 && device.getdDeComm().equalsIgnoreCase("0")) {
					request.setCommissionFlag(true);
				} else {
					request.setCommissionFlag(false);

				}

			}
			obj.put("data", requests);
			logger.info("");
		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}
	
}

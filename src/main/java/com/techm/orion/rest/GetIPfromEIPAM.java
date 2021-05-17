package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

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
import com.techm.orion.entitybeans.EIPAMEntity;
import com.techm.orion.repositories.EIPAMEntityRepository;

@Controller
@RequestMapping("/GetIPfromEIPAM")
public class GetIPfromEIPAM implements Observer {
	private static final Logger logger = LogManager.getLogger(GetIPfromEIPAM.class);
	
	@Autowired	
	private EIPAMEntityRepository eipamEntityRepository;

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getip", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getIP(@RequestBody String searchParameters) {

		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String jsonArray = "", ip = "";

		String site = null, customer = null, service = null, region = null;
		int status;

		try {
			Gson gson = new Gson();
			EIPAMEntity dto = gson.fromJson(searchParameters, EIPAMEntity.class);
			site = dto.getSite();
			customer = dto.getCustomer();
			service = dto.getService();
			region = dto.getRegion();
			status = dto.getStatus();

			List<EIPAMEntity> detailsList = new ArrayList<EIPAMEntity>();
			Boolean flag = false;
			detailsList = eipamEntityRepository.findAll();
			EIPAMEntity detail = new EIPAMEntity();
			detail = null;
			// List<RequestInfoSO> detailsList = new ArrayList<RequestInfoSO>();

			try {
				// quick fix for json not getting serialized

				if (!site.isEmpty() && !customer.isEmpty() && !service.isEmpty() && !region.isEmpty())

				{

					for (int i = 0; i < detailsList.size(); i++) {
						if (detailsList.get(i).getSite().equals(site)
								&& detailsList.get(i).getCustomer().equals(customer)
								&& detailsList.get(i).getService().equals(service)
								&& detailsList.get(i).getRegion().equals(region)) {

							detail = eipamEntityRepository.findBySiteAndCustomerAndServiceAndRegion(site, customer,
									service, region);

						}

					}

				}

				// resultObject = requestInfoDao.getIPAMIPfromDB(site,
				// customer,service,region);

				// else
				// {
				// obj.put(new String("output"), "No IP found.");

				// }

				if (detail.getStatus() == 1) {
					obj.put(new String("output"), "All IP addresses allocated to this site are in use.");
				} else {
					jsonArray = new Gson().toJson(detail);
					obj.put(new String("output"), detail);
				}

			} catch (Exception e) {
				obj.put(new String("output"), "No IP found.");
				logger.error(e);
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

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

}

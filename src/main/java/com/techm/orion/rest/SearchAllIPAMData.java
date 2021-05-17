package com.techm.orion.rest;

import java.sql.Connection;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.connection.ConnectionFactory;
import com.techm.orion.entitybeans.EIPAMEntity;
import com.techm.orion.repositories.EIPAMEntityRepository;

@Controller
@RequestMapping("/SearchAllIpamData")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class SearchAllIPAMData implements Observer {
	private static final Logger logger = LogManager.getLogger(SearchAllIPAMData.class);
	
	@Autowired
	EIPAMEntityRepository eipamEntityRepository;

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/search", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Response getAll(@RequestBody String searchParameters) {

		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String jsonArray = "";
		String keyword = null;
		String site = null;
		String customer = null;
		String service = null;
		String ip = null;

		List<EIPAMEntity> EmptyList = new ArrayList<EIPAMEntity>();

		List<EIPAMEntity> detailsList = new ArrayList<EIPAMEntity>();
		Boolean flag = false;
		detailsList = eipamEntityRepository.findAll();

		try {
			Gson gson = new Gson();
			EIPAMEntity dto = gson.fromJson(searchParameters, EIPAMEntity.class);

			site = dto.getSite();
			customer = dto.getCustomer();
			service = dto.getService();
			ip = dto.getIp();

			if (site.isEmpty() && customer.isEmpty() && service.isEmpty() && ip.isEmpty()) {
				detailsList = eipamEntityRepository.findAll();
				flag = true;
			}

			else {
				Connection connection = ConnectionFactory.getConnection();
				int parameters_to_search = 0;

				if (!site.isEmpty()) {
					parameters_to_search++;
				}
				if (!customer.isEmpty()) {
					parameters_to_search++;
				}
				if (!service.isEmpty()) {
					parameters_to_search++;
				}
				if (!ip.isEmpty()) {
					parameters_to_search++;
				}

				if (parameters_to_search == 1)

				{
					if (!site.isEmpty()) {
						// site
						detailsList = eipamEntityRepository.findBySite(site);
						flag = true;
					}

					else if (!customer.isEmpty()) {

						detailsList = eipamEntityRepository.findByCustomer(customer);
						flag = true;
					}

					else if (!service.isEmpty()) {
						detailsList = eipamEntityRepository.findByService(service);
						flag = true;

					} else if (!ip.isEmpty()) {
						detailsList = eipamEntityRepository.findByIp(ip);
						flag = true;
					}
				}

				else if (parameters_to_search == 2) {

					if (!site.isEmpty() && !customer.isEmpty()) {
						// site and customer

						for (int i = 0; i < detailsList.size(); i++) {

							if (detailsList.get(i).getSite().equals(site)
									&& detailsList.get(i).getCustomer().equals(customer)) {

								detailsList = eipamEntityRepository.findByCustomerAndSite(customer, site);
								flag = true;

							}
						}
					}

					else if (!site.isEmpty() && !service.isEmpty()) {
						// site and service
						for (int i = 0; i < detailsList.size(); i++) {
							if (detailsList.get(i).getSite().equals(site)
									&& detailsList.get(i).getService().equals(service)) {

								detailsList = eipamEntityRepository.findByServiceAndSite(service, site);
								flag = true;
							}
						}
					}

					else if (!site.isEmpty() && !ip.isEmpty()) {
						// site and ip
						for (int i = 0; i < detailsList.size(); i++) {
							if (detailsList.get(i).getSite().equals(site) && detailsList.get(i).getIp().equals(ip)) {

								detailsList = eipamEntityRepository.findBySiteAndIp(site, ip);
								flag = true;
							}
						}

					}

					else if (!customer.isEmpty() && !service.isEmpty()) {
						// customer and service
						for (int i = 0; i < detailsList.size(); i++) {
							if (detailsList.get(i).getCustomer().equals(customer)
									&& detailsList.get(i).getService().equals(service)) {

								detailsList = eipamEntityRepository.findByCustomerAndService(customer, service);
								flag = true;
							}
						}

					}

					else if (!customer.isEmpty() && !ip.isEmpty()) {
						for (int i = 0; i < detailsList.size(); i++) {
							if (detailsList.get(i).getCustomer().equals(customer)
									&& detailsList.get(i).getIp().equals(ip)) {

								detailsList = eipamEntityRepository.findByCustomerAndIp(customer, ip);
								flag = true;
							}
						}
					}

					else if (!service.isEmpty() && !ip.isEmpty()) {
						// service and ip
						for (int i = 0; i < detailsList.size(); i++) {
							if (detailsList.get(i).getService().equals(service)
									&& detailsList.get(i).getIp().equals(ip)) {

								detailsList = eipamEntityRepository.findByServiceAndIp(service, ip);
								flag = true;
							}
						}
					}
				}

				else if (parameters_to_search == 3) {

					if (!site.isEmpty() && !customer.isEmpty() && !service.isEmpty()) {
						// site customer service
						for (int i = 0; i < detailsList.size(); i++) {
							if (detailsList.get(i).getSite().equals(site)
									&& detailsList.get(i).getCustomer().equals(customer)
									&& detailsList.get(i).getService().equals(service)) {

								detailsList = eipamEntityRepository.findBySiteAndCustomerAndService(site, customer,
										service);
								flag = true;
							}
						}
					}

					else if (!site.isEmpty() && !service.isEmpty() && !ip.isEmpty()) {

						for (int i = 0; i < detailsList.size(); i++) {
							if (detailsList.get(i).getSite().equals(site)
									&& detailsList.get(i).getService().equals(service)
									&& detailsList.get(i).getIp().equals(ip)) {

								detailsList = eipamEntityRepository.findBySiteAndServiceAndIp(site, service, ip);
								flag = true;
							}
						}
					}

					else if (!customer.isEmpty() && !service.isEmpty() && !ip.isEmpty()) {
						// customer service ip
						for (int i = 0; i < detailsList.size(); i++) {
							if (detailsList.get(i).getCustomer().equals(customer)
									&& detailsList.get(i).getService().equals(service)
									&& detailsList.get(i).getIp().equals(ip)) {
								detailsList = eipamEntityRepository.findByCustomerAndServiceAndIp(customer, service,
										ip);
								flag = true;
							}
						}
					}

					else if (!site.isEmpty() && !customer.isEmpty() && !ip.isEmpty()) {
						// site customer ip
						for (int i = 0; i < detailsList.size(); i++) {
							if (detailsList.get(i).getSite().equals(site)
									&& detailsList.get(i).getCustomer().equals(customer)
									&& detailsList.get(i).getIp().equals(ip)) {
								detailsList = eipamEntityRepository.findBySiteAndCustomerAndIp(site, customer, ip);
								flag = true;
							}
						}
					}
				}

				else if (parameters_to_search == 4) {
					if (!site.isEmpty() && !customer.isEmpty() && !ip.isEmpty() && !service.isEmpty())

					{

						for (int i = 0; i < detailsList.size(); i++) {
							if (detailsList.get(i).getSite().equals(site)
									&& detailsList.get(i).getCustomer().equals(customer)
									&& detailsList.get(i).getService().equals(service)
									&& detailsList.get(i).getIp().equals(ip)) {

								detailsList = eipamEntityRepository.findBySiteAndCustomerAndServiceAndIp(site, customer,
										service, ip);
								flag = true;
							}
						}

					}
				}

			}

			if (flag == true) {
				jsonArray = new Gson().toJson(detailsList);
				obj.put(new String("output"), jsonArray);
				flag = false;
			} else

			{

				jsonArray = new Gson().toJson(EmptyList);
				obj.put(new String("output"), jsonArray);
			}

		}

		catch (Exception e) {
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

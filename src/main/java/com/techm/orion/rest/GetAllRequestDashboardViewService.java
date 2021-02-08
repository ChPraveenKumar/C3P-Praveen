package com.techm.orion.rest;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.ElapsedTimeFormatPojo;
import com.techm.orion.pojo.RequestInfoSO;
import com.techm.orion.service.DcmConfigService;
import com.techm.orion.webService.GetAllDetailsService;

@Controller
@RequestMapping("/GetAllRequestDashboardViewService")
public class GetAllRequestDashboardViewService implements Observer {
	private static final Logger logger = LogManager.getLogger(GetAllRequestDashboardViewService.class);
	List<ElapsedTimeFormatPojo> elapsedtimings;

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/GetAllDashboardViewJSON", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response GetAllRequests() {
		DcmConfigService dcmConfigService = new DcmConfigService();
		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String jsonArray = "";
		int success = 0, failure = 0;

		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		List<RequestInfoSO> detailsList = new ArrayList<RequestInfoSO>();
		try {
			// quick fix for json not getting serialized

			detailsList = dcmConfigService.getAllDetails();

			// Constructing json object to include
			// versioning----------------------------------------------------------------------------------------------
			JSONObject requestListJsonObject = new JSONObject();

			// elapsedtimings = new ArrayList<ElapsedTimeFormatPojo>();
			// Logic to give number of success and filure requests
			for (int i = 0; i < detailsList.size(); i++) {
				if (!StringUtils.isEmpty(detailsList.get(i).getStatus())) {
					if (detailsList.get(i).getStatus().equalsIgnoreCase("Success")) {
						success++;

					} else {
						failure++;
					}
				} else {
					failure++;
				}
			}

			/*
			 * Collections.sort(elapsedtimings,new Comparator<ElapsedTimeFormatPojo>() {
			 * 
			 * @Override public int compare(ElapsedTimeFormatPojo o1, ElapsedTimeFormatPojo
			 * o2) { // TODO Auto-generated method stub if (o1.getElapsedTimeinMinutes() >
			 * o2.getElapsedTimeinMinutes()) { return -1; } else if
			 * (o1.getElapsedTimeinMinutes() < o2.getElapsedTimeinMinutes()) { return 1; }
			 * return 0; } }); Collections.reverse(detailsList);
			 */

			// Logic to count the repeated object NOT used as of now but will use if
			// required in future............................................
			/*
			 * List<RequestInfoSO>countedObject=new ArrayList<RequestInfoSO>();
			 * List<RequestVersionCountPojo>countPojo=new
			 * ArrayList<RequestVersionCountPojo>(); for(int i=0; i<detailsList.size();i++)
			 * { boolean flag=false; for(int j=0; j<countedObject.size();j++) {
			 * if(countedObject.get(j).getDisplay_request_id().equalsIgnoreCase(detailsList.
			 * get(i).getDisplay_request_id())) { flag=true; break; }
			 * 
			 * } if(flag == false) { RequestVersionCountPojo countPojoObj=new
			 * RequestVersionCountPojo(); int count=countNumberEqual(detailsList,
			 * detailsList.get(i)); countPojoObj.setCount(count);
			 * countPojoObj.setRequestObj(detailsList.get(i)); countPojo.add(countPojoObj);
			 * countedObject.add(detailsList.get(i));
			 * logger.info("Object ->"+detailsList.get(i).getDisplay_request_id()+" "
			 * +count); } }
			 */
			// Logic to construct JSON
			// Model...................................................................................................
			Collections.reverse(detailsList);
			List<RequestInfoSO> finalList = new ArrayList<RequestInfoSO>();
			RequestInfoSO comapreObj;
			List<RequestInfoSO> compareList = new ArrayList<RequestInfoSO>();
			for (int i = 0; i < detailsList.size(); i++) {
				comapreObj = new RequestInfoSO();
				comapreObj = detailsList.get(i);
				boolean isPresent = false;
				if (finalList.size() > 0) {
					for (int j = 0; j < finalList.size(); j++) {
						if (comapreObj.getDisplay_request_id()
								.equalsIgnoreCase(finalList.get(j).getDisplay_request_id())) {
							isPresent = true;
							break;
						}
					}
					if (isPresent == false) {
						for (int k = 0; k < detailsList.size(); k++) {
							if (comapreObj.getDisplay_request_id()
									.equalsIgnoreCase(detailsList.get(k).getDisplay_request_id())) {
								compareList.add(detailsList.get(k));
							}
						}
						finalList.add(compareList.get(0));
						compareList.clear();
					}
				} else {
					for (int k = 0; k < detailsList.size(); k++) {
						if (comapreObj.getDisplay_request_id()
								.equalsIgnoreCase(detailsList.get(k).getDisplay_request_id())) {
							compareList.add(detailsList.get(k));
						}
					}
					finalList.add(compareList.get(0));
					compareList.clear();
				}
			}

			jsonArray = new Gson().toJson(finalList);
			int hours, minutes, seconds;
			/*
			 * if(elapsedtimings.size()>0) { int
			 * avgT=elapsedtimings.get(elapsedtimings.size() -
			 * 1).getElapsedTimeinMinutes()+elapsedtimings.get(0).getElapsedTimeinMinutes()/
			 * 2; hours = avgT / 60; //since both are ints, you get an int minutes =avgT %
			 * 60; BigDecimal secondsPrecision = new BigDecimal((avgT - Math.floor(avgT)) *
			 * 100).setScale(2, RoundingMode.HALF_UP); seconds =
			 * secondsPrecision.intValue(); } else { hours=0; minutes=0; seconds=0; }
			 */

			obj.put(new String("output"), jsonArray);
			obj.put("SuccessfulRequests", success);
			obj.put("FailureRequests", failure);

			obj.put("MinElapsedTime", dcmConfigService.getMinElapsedTime(finalList));
			obj.put("MaxElapsedTime", dcmConfigService.getMaxElapsedTime(finalList));
			obj.put("AvgElapsedTime", dcmConfigService.getAvgElapsedTime(finalList));
			obj.put("TotalRequests", finalList.size());
		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	private String getElapsedTime(Date d1, Date d2) {
		String elapsedtime = null;
		// in milliseconds
		long diff = d2.getTime() - d1.getTime();

		long diffSeconds = diff / 1000 % 60;
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		long diffDays = diff / (24 * 60 * 60 * 1000);

		long daymin = diffDays * 1440;
		long hourmin = diffHours * 60;
		long secmin = (long) (diffSeconds * 0.016);
		long totalMins = daymin + hourmin + diffMinutes + secmin;

		long dayTohours = diffDays * 24;

		DecimalFormat formatter = new DecimalFormat("00");
		String sec = formatter.format(diffSeconds);
		String min = formatter.format(diffMinutes);
		String hrs = formatter.format(diffHours + dayTohours);

		elapsedtime = hrs + ":" + min + ":" + sec;

		ElapsedTimeFormatPojo time = new ElapsedTimeFormatPojo();
		time.setDisplayTime(elapsedtime);
		time.setElapsedTimeinMinutes((int) totalMins);
		elapsedtimings.add(time);
		return elapsedtime;
	}

	public String getProcessId(CreateConfigRequestDCM configRequest) throws IOException {

		GetAllDetailsService gads = new GetAllDetailsService();
		String requestIdForProcess = gads.createProcessForConfiguration(configRequest);

		return requestIdForProcess;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	public int countNumberEqual(List<RequestInfoSO> itemList, RequestInfoSO itemToCheck) {
		int count = 0;
		for (RequestInfoSO i : itemList) {
			if (i.getDisplay_request_id().equalsIgnoreCase(itemToCheck.getDisplay_request_id())) {
				count++;
			}
		}
		return count;
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getListForConfigurationFeature", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getListForConfigurationFeature(@RequestBody String templateFeatureRequest) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";

		JSONArray array = new JSONArray();

		JSONObject jsonObj;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(templateFeatureRequest);
			CreateConfigRequestDCM configReqToSendToC3pCode = new CreateConfigRequestDCM();
			DcmConfigService dcmConfigService = new DcmConfigService();
			configReqToSendToC3pCode.setRegion(json.get("region").toString().toUpperCase());
			configReqToSendToC3pCode.setVendor(json.get("vendor").toString().toUpperCase());
			configReqToSendToC3pCode.setModel(json.get("model").toString());
			configReqToSendToC3pCode.setOs(json.get("os").toString());
			configReqToSendToC3pCode.setOsVersion(json.get("osVersion").toString());
			List<String> featureList = dcmConfigService.getConfigurationFeature(configReqToSendToC3pCode.getRegion(),
					configReqToSendToC3pCode.getVendor(), configReqToSendToC3pCode.getModel(),
					configReqToSendToC3pCode.getOs(), configReqToSendToC3pCode.getOsVersion());

			if (featureList.size() > 0) {
				for (int i = 0; i < featureList.size(); i++) {
					jsonObj = new JSONObject();
					jsonObj.put("value", featureList.get(i));
					if (featureList.get(i).equalsIgnoreCase("Basic Configuration")) {
						jsonObj.put("selected", true);
						jsonObj.put("disabled", true);
					} else {
						jsonObj.put("selected", false);
						jsonObj.put("disabled", false);
					}

					array.put(jsonObj);
				}

				jsonArray = array.toString();
				obj.put(new String("Result"), "Success");
				obj.put(new String("Message"), "Success");
				obj.put(new String("featureList"), jsonArray);
			} else {
				obj.put(new String("Result"), "Failure");
				obj.put(new String("Message"), "Template not approved");
				obj.put(new String("featureList"), null);
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
}
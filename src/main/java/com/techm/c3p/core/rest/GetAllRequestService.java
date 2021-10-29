package com.techm.c3p.core.rest;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.c3p.core.pojo.CreateConfigRequestDCM;
import com.techm.c3p.core.pojo.ElapsedTimeFormatPojo;
import com.techm.c3p.core.pojo.RequestInfoSO;
import com.techm.c3p.core.service.DcmConfigService;
import com.techm.c3p.core.webservice.GetAllDetailsService;

@Controller
@RequestMapping("/GetAllRequestService")
public class GetAllRequestService {
	private static final Logger logger = LogManager.getLogger(GetAllRequestService.class);
	private List<ElapsedTimeFormatPojo> elapsedtimings;

	@Autowired
	private DcmConfigService dcmConfigService;
	@Autowired
	private GetAllDetailsService getAllDetailsService;
	

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/GetAllRequests", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response GetAllRequests() {
		
		JSONObject obj = new JSONObject();
		String jsonArray = "";
		int success = 0, failure = 0;
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		List<RequestInfoSO> detailsList = new ArrayList<RequestInfoSO>();
		try {
			// quick fix for json not getting serialized

			detailsList = dcmConfigService.getAllDetails();

			// Constructing json object to include
			// versioning----------------------------------------------------------------------------------------------

			elapsedtimings = new ArrayList<ElapsedTimeFormatPojo>();
			// Logic to give number of success and filure requests
			for (int i = 0; i < detailsList.size(); i++) {
				if (!StringUtils.isEmpty(detailsList.get(i).getStatus())) {
					if (detailsList.get(i).getStatus().equalsIgnoreCase("Success")) {
						success++;

						// Throws null pointer exception as it does not have end date of
						// processing!!!!!!!!!!!!!!!!!!!!!!!!!

						Date d1 = null;
						Date d2 = null;
						d1 = format.parse(detailsList.get(i).getDateOfProcessing());
						d2 = format.parse(detailsList.get(i).getEndDateofProcessing());

						detailsList.get(i).setElapsedTime(getElapsedTime(d1, d2));

					} else {
						failure++;
					}
				} else {
					failure++;
				}
			}

			Collections.sort(elapsedtimings, new Comparator<ElapsedTimeFormatPojo>() {

				@Override
				public int compare(ElapsedTimeFormatPojo o1, ElapsedTimeFormatPojo o2) {
					// TODO Auto-generated method stub
					if (o1.getElapsedTimeinMinutes() > o2.getElapsedTimeinMinutes()) {
						return -1;
					} else if (o1.getElapsedTimeinMinutes() < o2.getElapsedTimeinMinutes()) {
						return 1;
					}
					return 0;
				}
			});
			Collections.reverse(detailsList);
			jsonArray = new Gson().toJson(detailsList);
			int hours, minutes, seconds;
			if (elapsedtimings.size() > 0) {
				int avgT = elapsedtimings.get(elapsedtimings.size() - 1).getElapsedTimeinMinutes()
						+ elapsedtimings.get(0).getElapsedTimeinMinutes() / 2;
				hours = avgT / 60; // since both are ints, you get an int
				minutes = avgT % 60;
				BigDecimal secondsPrecision = new BigDecimal((avgT - Math.floor(avgT)) * 100).setScale(2,
						RoundingMode.HALF_UP);
				seconds = secondsPrecision.intValue();
			} else {
				hours = 0;
				minutes = 0;
				seconds = 0;
			}

			obj.put(new String("output"), jsonArray);
			obj.put("SuccessfulRequests", success);
			obj.put("FailureRequests", failure);
			if (elapsedtimings.size() > 0) {
				obj.put("MinElapsedTime", elapsedtimings.get(elapsedtimings.size() - 1).getDisplayTime());
				obj.put("MaxElapsedTime", elapsedtimings.get(0).getDisplayTime());
			} else {
				obj.put("MinElapsedTime", "00");
				obj.put("MaxElapsedTime", "00");
			}
			obj.put("AvgElapsedTime", String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":"
					+ String.format("%02d", seconds));
			obj.put("TotalRequests", detailsList.size());
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

		String requestIdForProcess = getAllDetailsService.createProcessForConfiguration(configRequest);

		return requestIdForProcess;
	}
}

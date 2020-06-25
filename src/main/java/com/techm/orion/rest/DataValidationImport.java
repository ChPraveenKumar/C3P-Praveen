package com.techm.orion.rest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.dao.TemplateSuggestionDao;
import com.techm.orion.entitybeans.RequestDetailsEntity;
import com.techm.orion.pojo.CreateConfigRequest;
import com.techm.orion.repositories.DeviceInterfaceRepo;
import com.techm.orion.repositories.DeviceTypeRepository;
import com.techm.orion.repositories.InternetInfoRepo;
import com.techm.orion.repositories.ModelsRepository;
import com.techm.orion.repositories.OSRepository;
import com.techm.orion.repositories.OSversionRepository;
import com.techm.orion.repositories.RegionsRepository;
import com.techm.orion.repositories.RequestDetailsExportRepo;
import com.techm.orion.repositories.RouterVfRepo;
import com.techm.orion.repositories.VendorRepository;
import com.techm.orion.repositories.WebServiceRepo;
import com.techm.orion.service.DcmConfigService;
import com.techm.orion.service.ExcelReader;

@RestController
@RequestMapping("/DataValidationImport")
public class DataValidationImport implements Observer {
	private static final Logger logger = LogManager.getLogger(DataValidationImport.class);

	// private static final String FILE_PATH = null;

	// private static final String STYLE_CELL_BORDERED = null;

	public static String TSA_PROPERTIES_FILE = "TSA.properties";

	public static final Properties TSA_PROPERTIES = new Properties();
	DcmConfigService dcmConfigService = new DcmConfigService();
	JSONObject jsonObj;
	JSONArray array = new JSONArray();
	TemplateSuggestionDao templateSuggestionDao = new TemplateSuggestionDao();
	@Autowired
	RequestDetailsExportRepo requestDetailsExportRepo;

	@Autowired
	DeviceInterfaceRepo deviceInterfaceRepo;

	@Autowired
	InternetInfoRepo internetInfoRepo;

	@Autowired
	RouterVfRepo routerVfRepo;

	@Autowired
	WebServiceRepo webServiceRepo;

	@Autowired
	VendorRepository vendorRepository;

	@Autowired
	DeviceTypeRepository deviceTypeRepository;

	@Autowired
	ModelsRepository modelsRepository;

	@Autowired
	OSRepository oSRepository;

	@Autowired
	OSversionRepository oSversionRepository;

	@Autowired
	RegionsRepository regionsRepository;

	RequestInfoDao requestInfoDao = new RequestInfoDao();
	CreateConfigRequest createConfigRequest = new CreateConfigRequest();

	@GET
	@Produces("application/json")
	@RequestMapping(value = "/milestonebits", method = RequestMethod.GET, produces = "application/json")
	public Response milestonebits(@RequestParam int request_info_id)
			throws IOException, SQLException, ParseException, java.text.ParseException {

		ExcelReader excelReader = new ExcelReader();
		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String jsonArray = "";
		String jsonArray1 = "";
		RequestDetailsEntity ExportList = new RequestDetailsEntity();
		ExportList = requestDetailsExportRepo.findByrequestinfoid(request_info_id);

		String popUpmilestone = ExportList.getValidationMilestoneBits();
		if (isNullOrEmpty(ExportList.getValidationMilestoneBits())) {
			obj.put(new String("PopUpMilestone"), popUpmilestone);
		}
		/*
		 * else { ExcelReader.validate(request_info_id);
		 * 
		 * }
		 */
		popUpmilestone = ExportList.getValidationMilestoneBits();
		obj.put(new String("PopUpMilestone"), popUpmilestone);

		char first = popUpmilestone.charAt(0);

		try {
			JSONObject names = new JSONObject();
			JSONObject values = new JSONObject();
			List<String> vendorlist = vendorRepository.findVendors();
			List<String> devicelist = deviceTypeRepository.findDevice();
			List<String> modellist = modelsRepository.findModel();
			List<String> oslist = oSRepository.findOs();
			List<String> osVersionlist = oSversionRepository.findOsVersion();
			List<String> regionlist = regionsRepository.findRegion();

			ExportList = requestDetailsExportRepo.findByrequestinfoid(request_info_id);

			String Device = ExportList.getDevice_type();
			String vendor = ExportList.getVendor();
			String model = ExportList.getModel();
			String os = ExportList.getOs();
			String osversion = ExportList.getOs_version();
			String region = ExportList.getRegion();

			// ArrayList<String> names = new ArrayList<String>();

			int request_id = ExportList.getRequestinfoid();

			String request = Integer.toString(request_id);

			String Vendor = ExportList.getVendor();
			// boolean flag1 = true;
			boolean flag1 = false;
			for (String string : vendorlist) {

				if (string.equals(Vendor)) {
					flag1 = true;
				}

			}
			if (flag1 == true) {
				names.put("Vendor", 1);
				values.put("Vendor_Name", vendor);
			} else {
				names.put("Vendor", 0);
				values.put("Vendor_Name", vendor);
			}
			boolean flag2 = false;
			String DeviceType = ExportList.getDevice_type();
			for (String string : devicelist) {
				if (string.equals(DeviceType)) {
					flag2 = true;
				}

			}
			if (flag2 == true) {
				names.put("Device", 1);
				values.put("Device_Name", Device);
			} else {
				names.put("Device", 0);
				values.put("Device_Name", Device);
			}
			boolean flag3 = false;
			String Model = ExportList.getModel();
			for (String string : modellist) {
				if (string.equals(Model)) {
					flag3 = true;

				}

			}
			if (flag3 == true) {
				names.put("Model", 1);
				values.put("Model_Name", model);
			} else {
				names.put("Model", 0);
				values.put("Model_Name", model);
			}
			boolean flag4 = false;
			String Os = ExportList.getOs();
			for (String string : oslist) {
				if (string.equals(Os)) {

					flag4 = true;

				}

			}
			if (flag4 == true) {
				names.put("os", 1);
				values.put("os_Name", os);
			}

			else {
				names.put("os", 0);
				values.put("os_Name", os);
			}
			boolean flag5 = false;
			String OsVersion = ExportList.getOs_version();
			for (String string : osVersionlist) {
				if (string.equals(OsVersion)) {

					flag5 = true;

				}

			}
			if (flag5 == true) {
				names.put("OS_Version", 1);
				values.put("os_Version_Name", osversion);
			} else {
				names.put("OS_Version", 0);
				values.put("os_Version_Name", osversion);
			}

			boolean flag6 = false;
			String Region = ExportList.getRegion();
			for (String string : regionlist) {
				if (string.equals(Region)) {
					flag6 = true;
				}

			}
			if (flag6 == true) {
				names.put("Region", 1);
				values.put("Region_Name", Region);
			} else {
				names.put("Region", 0);
				values.put("Region_Name", Region);
			}

			List<String> Validationlist = new ArrayList<String>();

			String MessageReport = "";
			names.put("Request Id", request_id);
			int Datavalid;
			if (flag1 == true && flag2 == true && flag3 == true && flag4 == true && flag5 == true && flag6 == true) {
				Datavalid = 1;

			} else {
				Datavalid = 0;

			}

			jsonArray = new Gson().toJson(names);
			jsonArray1 = new Gson().toJson(values);
			obj.put(new String("Values"), jsonArray1);
			obj.put(new String("output"), jsonArray);
		} catch (Exception e) {
			logger.error(e);
		}

		// /===
		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	public static boolean isNullOrEmpty(String str) {
		if (str != null && !str.isEmpty())
			return true;
		return false;
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}
}

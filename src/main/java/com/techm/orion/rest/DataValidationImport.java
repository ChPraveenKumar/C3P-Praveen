package com.techm.orion.rest;

import java.awt.Font;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.TreeMap;

import javax.persistence.Column;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Null;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.mapping.Set;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.apache.poi.ss.usermodel.Cell;

import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mysql.jdbc.Field;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.dao.TemplateSuggestionDao;
import com.techm.orion.entitybeans.DeviceInterfaceEntity;
import com.techm.orion.entitybeans.EIPAMEntity;
import com.techm.orion.entitybeans.InternetInfoEntity;
import com.techm.orion.entitybeans.RequestDetailsEntity;
import com.techm.orion.entitybeans.RouterVfEntity;
import com.techm.orion.entitybeans.Vendors;
import com.techm.orion.entitybeans.WebServiceEntity;
import com.techm.orion.exception.InvalidValueException;
import com.techm.orion.pojo.CreateConfigRequest;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.TemplateBasicConfigurationPojo;
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
import com.techm.orion.service.CSVWriteAndConnectPythonTemplateSuggestion;
import com.techm.orion.service.DcmConfigService;
import com.techm.orion.service.ExcelReader;

import org.apache.commons.validator.routines.InetAddressValidator;

@RestController
@RequestMapping("/DataValidationImport")
public class DataValidationImport implements Observer {

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
			throws IOException, SQLException, ParseException,
			java.text.ParseException {
		RequestDetailsEntity entity = new RequestDetailsEntity();
		DeviceInterfaceEntity ExportList2 = new DeviceInterfaceEntity();
		InternetInfoEntity ExportList3 = new InternetInfoEntity();
		Gson gson = new Gson();
		ExcelReader excelReader = new ExcelReader();
		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String jsonArray = "";
		String jsonArray1 = "";
		RequestDetailsEntity ExportList = new RequestDetailsEntity();
		ExportList = requestDetailsExportRepo
				.findByrequestinfoid(request_info_id);

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

				ExportList = requestDetailsExportRepo
						.findByrequestinfoid(request_info_id);

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
				if (flag1 == true && flag2 == true && flag3 == true
						&& flag4 == true && flag5 == true&& flag6==true) {
					Datavalid = 1;

				} else {
					Datavalid = 0;

				}

				jsonArray = new Gson().toJson(names);
				jsonArray1 = new Gson().toJson(values);
				obj.put(new String("Values"), jsonArray1);
				obj.put(new String("output"), jsonArray);
			} catch (Exception e) {
				System.out.println(e);
			}
		

		// /===
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



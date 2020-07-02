package com.techm.orion.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.techm.orion.ValidatorConfigService.ExcelFileValidation;
import com.techm.orion.entitybeans.RequestDetailsEntity;
import com.techm.orion.pojo.SearchParamPojo;
import com.techm.orion.repositories.DeviceInterfaceRepo;
import com.techm.orion.repositories.InternetInfoRepo;
import com.techm.orion.repositories.RequestDetailsImportRepo;
import com.techm.orion.repositories.RouterVfRepo;
import com.techm.orion.repositories.WebServiceRepo;
import com.techm.orion.service.ExcelReader;
import com.techm.orion.service.StorageService;

/*Class to handle import service(single and bulk) request with other functionality*/

@Controller
@RequestMapping("/ImportFile")
public class ImportFile {
	private static final Logger logger = LogManager.getLogger(ImportFile.class);
	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();
	List<String> files = new ArrayList<String>();

	@Autowired
	public RequestDetailsImportRepo requestDetailsImportRepo;

	@Autowired
	public DeviceInterfaceRepo deviceInterfaceRepo;

	@Autowired
	public InternetInfoRepo internetInfoRepo;

	@Autowired
	public RouterVfRepo routerVfRepo;

	@Autowired
	public WebServiceRepo webServiceRepo;

	@Autowired
	private ExcelReader excelReader;

	@Autowired
	StorageService storageService;

	/* Web service to get all request on Import Dash Board */

	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/getAll", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response getAll() {

		JSONObject obj = new JSONObject();

		String jsonArray = "";

		try {

			List<RequestDetailsEntity> requestDetailFinal = new ArrayList<RequestDetailsEntity>();
			List<RequestDetailsEntity> requestDetail = requestDetailsImportRepo.findAll();

			/* Iterating loop for required UI field for each request */
			for (int i = 0; i < requestDetail.size(); i++) {
				if (!(requestDetail.get(i).getImportid() == null)) {

					RequestDetailsEntity requestDetailsEntity = new RequestDetailsEntity();

					requestDetailsEntity.setImportid(requestDetail.get(i).getImportid());

					requestDetailsEntity.setRegion(requestDetail.get(i).getRegion());

					requestDetailsEntity.setVendor(requestDetail.get(i).getVendor());

					requestDetailsEntity.setModel(requestDetail.get(i).getModel());

					requestDetailsEntity.setRequestinfoid(requestDetail.get(i).getRequestinfoid());

					requestDetailsEntity.setImportStatus(requestDetail.get(i).getImportStatus());
					requestDetailsEntity.setDateofProcessing((requestDetail.get(i).getDateofProcessing()));
					requestDetailsEntity.setHostname(((requestDetail.get(i).getHostname())));
					requestDetailsEntity.setManagementIp(requestDetail.get(i).getManagementIp());
					requestDetailsEntity.setRequestCreatorName((((requestDetail.get(i).getRequestCreatorName()))));

					requestDetailFinal.add(requestDetailsEntity);

				}
			}
			jsonArray = new Gson().toJson(requestDetailFinal);
			obj.put(new String("output"), jsonArray);

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();
	}

	/* Web service call to upload file on local storage and save in Database */
	@POST
	@RequestMapping(value = "/fileupload", method = RequestMethod.POST)
	@ResponseBody
	public Response uploadFile(@ModelAttribute RequestDetailsEntity entity) {

		boolean isFlag = excelReader.saveDataFromUploadFile(entity.getFile());

		return null;

	}

	/* Web service call to search request based on user input */
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/search", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Response getAll(@RequestBody String searchParameters) {

		JSONObject obj = new JSONObject();
		Gson gson = new Gson();
		String jsonArray = "";
		String key = null, value = null;
		Boolean flag = false;

		List<RequestDetailsEntity> detailsList = new ArrayList<RequestDetailsEntity>();
		List<RequestDetailsEntity> detailsListFinal = new ArrayList<RequestDetailsEntity>();
		List<RequestDetailsEntity> emptyList = new ArrayList<RequestDetailsEntity>();
		detailsList = requestDetailsImportRepo.findAll();

		try {

			SearchParamPojo dto = gson.fromJson(searchParameters, SearchParamPojo.class);

			key = dto.getKey();
			value = dto.getValue();

			if (value != null && !value.isEmpty()) {
				/*
				 * Search request based on Region, Vendor, Status, Model, Import Id and
				 * Management IP
				 */
				if (key.equalsIgnoreCase("Region")) {
					detailsList = requestDetailsImportRepo.findByRegion(value);

					detailsListFinal = searchImportDashboard(detailsList);
					flag = true;

				} else if (key.equalsIgnoreCase("Vendor")) {
					detailsList = requestDetailsImportRepo.findByVendor(value);

					detailsListFinal = searchImportDashboard(detailsList);
					flag = true;

				} else if (key.equalsIgnoreCase("Status")) {
					detailsList = requestDetailsImportRepo.findByImportStatus(value);

					detailsListFinal = searchImportDashboard(detailsList);
					flag = true;

				} else if (key.equalsIgnoreCase("Model")) {
					detailsList = requestDetailsImportRepo.findByModel(value);

					detailsListFinal = searchImportDashboard(detailsList);
					flag = true;

				} else if (key.equalsIgnoreCase("Import ID")) {
					detailsList = requestDetailsImportRepo.findByImportid(value);

					detailsListFinal = searchImportDashboard(detailsList);
					flag = true;

				} else if (key.equalsIgnoreCase("Management IP")) {
					detailsList = requestDetailsImportRepo.findByManagementIp(value);

					detailsListFinal = searchImportDashboard(detailsList);
					flag = true;

				}

			}

			if (flag == true) {
				jsonArray = gson.toJson(detailsListFinal);
				obj.put(new String("output"), jsonArray);
				flag = false;
			} else

			{

				jsonArray = new Gson().toJson(emptyList);
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

	/* Method to iterate over all possible search result */
	private List<RequestDetailsEntity> searchImportDashboard(List<RequestDetailsEntity> detailsList) {

		List<RequestDetailsEntity> detailsListFinalAfterSave = new ArrayList<RequestDetailsEntity>();
		for (int i = 0; i < detailsList.size(); i++) {
			if (!(detailsList.get(i).getImportsource().equals("Manual"))) {
				RequestDetailsEntity requestDetailsEntity = new RequestDetailsEntity();

				requestDetailsEntity.setImportid(detailsList.get(i).getImportid());

				requestDetailsEntity.setRegion(detailsList.get(i).getRegion());

				requestDetailsEntity.setVendor(detailsList.get(i).getVendor());

				requestDetailsEntity.setModel(detailsList.get(i).getModel());
				requestDetailsEntity.setManagementIp(detailsList.get(i).getManagementIp());

				requestDetailsEntity.setImportStatus((detailsList.get(i).getImportStatus()));
				requestDetailsEntity.setDateofProcessing((detailsList.get(i).getDateofProcessing()));
				requestDetailsEntity.setHostname(((detailsList.get(i).getHostname())));
				requestDetailsEntity.setDateofProcessing((((detailsList.get(i).getRequestCreatorName()))));

				detailsListFinalAfterSave.add(requestDetailsEntity);

			}

		}
		return detailsListFinalAfterSave;
	}

	/* Web service call to validate file against predefine set of rules */
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/fileValidation", method = RequestMethod.POST)
	@ResponseBody
	public Response handleFileValidation(@RequestParam("file") MultipartFile file) {

		JSONObject obj = new JSONObject();

		try {

			String FILE_NAME = file.getOriginalFilename();
			/* Loading file path from properties file */
			ImportFile.loadProperties();
			String FILE_LOCAL_PATH = ImportFile.TSA_PROPERTIES.getProperty("importFilePath");
			String fileNameAsImport = getAlphaNumericString(8);
			/* Storing file on local system */
			storageService.store(file, fileNameAsImport);
			/* Updating file name with alphanumeric number */
			String TOTAL_FILE_PATH = FILE_LOCAL_PATH.concat(fileNameAsImport.concat("_").concat(FILE_NAME));

			files.add(file.getOriginalFilename());

			boolean flag = false;
			boolean flagCSV = false;

			ExcelFileValidation fileValidation = new ExcelFileValidation();

			String extension = FilenameUtils.getExtension(file.getOriginalFilename());

			Resource filePath = storageService.loadFile(TOTAL_FILE_PATH);

			if (extension.equals("xlsx")) {
				flag = true;
			}

			else if (extension.equals("xls")) {

				flag = true;
			} else if (extension.equals("csv")) {
				flagCSV = true;
			} else {
				throw new IOException("Invalid file type detected");
			}

			if (flag) {

				String validateFileColumn = fileValidation.validateColumnXLSX(filePath);
				if (validateFileColumn.equals("Valid")) {
					String validateNoOfRequest = fileValidation.validateNoOfRequestXLSX(filePath);
					if (validateNoOfRequest.equals("Valid Single Request")) {

						obj.put(new String("response"), "True");
						obj.put(new String("message"), "Valid Single Request");

					} else if (validateNoOfRequest.equals("Invalid Single Request")) {

						obj.put(new String("response"), "False");
						obj.put(new String("message"), "Invalid file format detected");

					} else if (validateNoOfRequest.equals("Valid Multiple Request")) {

						obj.put(new String("response"), "True");
						obj.put(new String("message"), "Valid Multiple Request");
					} else if (validateNoOfRequest.equals("Invalid Multiple Request")) {

						obj.put(new String("response"), "False");
						obj.put(new String("message"), "Invalid file format detected");

					}

				} else {

					obj.put(new String("response"), "False");
					obj.put(new String("message"), "Invalid file format detected");

				}
			} else if (flagCSV) {
				String validateFileColumn = fileValidation.validateColumnCSV(filePath);
				if (validateFileColumn.equals("Valid")) {
					String validateNoOfRequest = fileValidation.validateNoOfRequestCSV(filePath);
					if (validateNoOfRequest.equals("Valid Single Request")) {

						obj.put(new String("response"), "True");
						obj.put(new String("message"), "Valid Single Request");

					} else if (validateNoOfRequest.equals("Invalid Single Request")) {

						obj.put(new String("response"), "False");
						obj.put(new String("message"), "Invalid file format detected");

					} else if (validateNoOfRequest.equals("Valid Multiple Request")) {

						obj.put(new String("response"), "True");
						obj.put(new String("message"), "Valid Multiple Request");
					} else if (validateNoOfRequest.equals("Invalid Multiple Request")) {

						obj.put(new String("response"), "False");
						obj.put(new String("message"), "Invalid file format detected");

					}

				} else {

					obj.put(new String("response"), "False");
					obj.put(new String("message"), "Invalid file format detected");

				}
			}

		} catch (Exception e) {

			obj.put(new String("response"), "False");
			obj.put(new String("message"), "Invalid file type detected");

		}
		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	/* Method to properties file */
	public static boolean loadProperties() throws IOException {
		InputStream tsaPropFile = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(TSA_PROPERTIES_FILE);

		try {
			TSA_PROPERTIES.load(tsaPropFile);
		} catch (IOException exc) {
			exc.printStackTrace();
			return false;
		}
		return false;
	}

	/* Method to generate alphanumeric id for saving file on local storage */
	static String getAlphaNumericString(int n) {

		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";

		StringBuilder sb = new StringBuilder(n);

		for (int i = 0; i < n; i++) {

			int index = (int) (AlphaNumericString.length() * Math.random());

			sb.append(AlphaNumericString.charAt(index));
		}

		return sb.toString().concat("-1");
	}

}

package com.techm.c3p.core.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.techm.c3p.core.entitybeans.CustomerStagingEntity;
import com.techm.c3p.core.entitybeans.ImportMasterStagingEntity;
import com.techm.c3p.core.entitybeans.ImportStagingHistory;
import com.techm.c3p.core.repositories.ErrorValidationRepository;
import com.techm.c3p.core.repositories.ImportMasterStagingRepo;
import com.techm.c3p.core.repositories.ImportStagingHistoryRepo;
import com.techm.c3p.core.service.CustomerStagingInteface;
import com.techm.c3p.core.service.StorageService;
import com.techm.c3p.core.utility.C3PCoreAppLabels;
import com.techm.c3p.core.utility.WAFADateUtil;
import com.techm.c3p.core.validator.config.service.ExcelFileValidation;

/*Class to handle import service(single and bulk) request with other functionality*/

@Controller
@RequestMapping("/ImportFile")
public class ImportFile {
	
	private static final Logger logger = LogManager.getLogger(ImportFile.class);
	private List<String> files = new ArrayList<String>();

	/*@Autowired
	private RequestDetailsImportRepo requestDetailsImportRepo;

	@Autowired
	private ExcelReader excelReader;
*/
	@Autowired
	private StorageService storageService;
	
	@Autowired
	private ExcelFileValidation excelFileValidation;
	
	@Autowired
	private ErrorValidationRepository errorValidationRepository;
	
	@Autowired
	private CustomerStagingInteface customerStagingInteface;
	
	@Autowired
	private ImportMasterStagingRepo importMasterStagingRepo;
	
	@Autowired
	private ImportStagingHistoryRepo importStagingHistoryRepo;
	
	@Autowired
	private WAFADateUtil dateUtil;

/*	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/getAll", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response getAll() {

		JSONObject obj = new JSONObject();

		String jsonArray = "";

		try {

			List<RequestDetailsEntity> requestDetailFinal = new ArrayList<RequestDetailsEntity>();
			List<RequestDetailsEntity> requestDetail = requestDetailsImportRepo.findAll();

			 Iterating loop for required UI field for each request 
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

	@POST
	@RequestMapping(value = "/fileupload", method = RequestMethod.POST)
	@ResponseBody
	public Response uploadFile(@ModelAttribute RequestDetailsEntity entity, String userName) {

		boolean isFlag = excelReader.saveDataFromUploadFile(entity.getFile(), userName);

		return null;

	}

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
	} */
	
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/fileValidation", method = RequestMethod.POST)
	@ResponseBody
	public Response handleFileValidation(@RequestParam("file") MultipartFile file) {

		JSONObject obj = new JSONObject();

		try {

			String FILE_NAME = file.getOriginalFilename();
			//Loading file path from properties file 
			String fileNameAsImport = getAlphaNumericString(8);
			//Storing file on local system 
			storageService.store(file, fileNameAsImport);
			//Updating file name with alphanumeric number 
			String TOTAL_FILE_PATH = C3PCoreAppLabels.IMPORT_FILEPATH.getValue().concat(fileNameAsImport.concat("_").concat(FILE_NAME));

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
						obj.put(new String("message"), errorValidationRepository.findByErrorId("C3P_CB_005"));

					} else if (validateNoOfRequest.equals("Invalid Single Request")) {

						obj.put(new String("response"), "False");
						obj.put(new String("message"), errorValidationRepository.findByErrorId("C3P_CB_015"));

					} else if (validateNoOfRequest.equals("Valid Multiple Request")) {

						obj.put(new String("response"), "True");
						obj.put(new String("message"), errorValidationRepository.findByErrorId("C3P_CB_006"));
					} else if (validateNoOfRequest.equals("Invalid Multiple Request")) {

						obj.put(new String("response"), "False");
						obj.put(new String("message"), errorValidationRepository.findByErrorId("C3P_CB_015"));

					}

				} else {

					obj.put(new String("response"), "False");
					obj.put(new String("message"), errorValidationRepository.findByErrorId("C3P_CB_015"));

				}
			} else if (flagCSV) {
				String validateFileColumn = fileValidation.validateColumnCSV(filePath);
				if (validateFileColumn.equals("Valid")) {
					String validateNoOfRequest = fileValidation.validateNoOfRequestCSV(filePath);
					if (validateNoOfRequest.equals("Valid Single Request")) {

						obj.put(new String("response"), "True");
						obj.put(new String("message"), errorValidationRepository.findByErrorId("C3P_CB_005"));

					} else if (validateNoOfRequest.equals("Invalid Single Request")) {

						obj.put(new String("response"), "False");
						obj.put(new String("message"), errorValidationRepository.findByErrorId("C3P_CB_015"));

					} else if (validateNoOfRequest.equals("Valid Multiple Request")) {

						obj.put(new String("response"), "True");
						obj.put(new String("message"), errorValidationRepository.findByErrorId("C3P_CB_006"));
					} else if (validateNoOfRequest.equals("Invalid Multiple Request")) {

						obj.put(new String("response"), "False");
						obj.put(new String("message"), errorValidationRepository.findByErrorId("C3P_CB_015"));

					}

				} else {

					obj.put(new String("response"), "False");
					obj.put(new String("message"), errorValidationRepository.findByErrorId("C3P_CB_015"));

				}
			}

		} catch (Exception e) {

			obj.put(new String("response"), "False");
			obj.put(new String("message"), errorValidationRepository.findByErrorId("C3P_CB_015"));

		}
		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}
	static String getAlphaNumericString(int n) {

		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";

		StringBuilder sb = new StringBuilder(n);

		for (int i = 0; i < n; i++) {

			int index = (int) (AlphaNumericString.length() * Math.random());

			sb.append(AlphaNumericString.charAt(index));
		}

		return sb.toString().concat("-1");
	}
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/fileValidationCSVForCOB", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity handleFileValidationForCOB(@RequestParam("file") MultipartFile file) {

		logger.info("Inside fileValidationCSVForCOB Service");
		JSONObject obj = new JSONObject();
		Map<String, List<String>> validateFileColumn = null;
		boolean status = false;
		try {
			String FILE_NAME = file.getOriginalFilename();
			//Loading file path from properties file 
			String fileNameAsImport = getAlphaNumericString(8);
			//Storing file on local system 
			storageService.store(file, fileNameAsImport);
			//Updating file name with alphanumeric number 
			String TOTAL_FILE_PATH = C3PCoreAppLabels.IMPORT_FILEPATH.getValue().concat(fileNameAsImport.concat("_").concat(FILE_NAME));
			files.add(file.getOriginalFilename());
			boolean flagCSV = false;
			String extension = FilenameUtils.getExtension(file.getOriginalFilename());
			Resource filePath = storageService.loadFile(TOTAL_FILE_PATH);

			if (extension.equals("csv")) {
				flagCSV = true;
			} else {
				throw new IOException(errorValidationRepository.findByErrorId("C3P_CB_001"));
			}

			if (flagCSV) {
				validateFileColumn = excelFileValidation.validateColumnCSVForCOB(filePath);
				logger.info("validateFileColumn -->" + validateFileColumn);
				if (validateFileColumn.containsKey("Valid")) {
					status = true;
					obj.put(new String("response"), status);
					obj.put(new String("successMessage"), validateFileColumn.get("Valid"));
					obj.put(new String("errorMessage"), "");
				} else if (validateFileColumn.containsKey("C3P_CB_012")
						|| validateFileColumn.containsKey("C3P_CB_014")) {
					status = false;
					StringBuilder errorMessage = new StringBuilder();
					if (validateFileColumn.containsKey("C3P_CB_012")) {
						errorMessage.append(excelFileValidation.getErrorInformation("C3P_CB_012",
								validateFileColumn.get("C3P_CB_012")));
						errorMessage.append("\n");
					}
					if (validateFileColumn.containsKey("C3P_CB_014")) {
						errorMessage.append(excelFileValidation.getErrorInformation("C3P_CB_014",
								validateFileColumn.get("C3P_CB_014")));
					}
					obj.put(new String("response"), status);
					obj.put(new String("successMessage"), "");
					obj.put(new String("errorMessage"), errorMessage);
				} else if (validateFileColumn.containsKey("C3P_CB_013")) {
					status = false;
					obj.put(new String("response"), status);
					obj.put(new String("errorMessage"), excelFileValidation.getErrorInformation("C3P_CB_013",
							validateFileColumn.get("C3P_CB_013")));
					obj.put(new String("successMessage"), "");
				} else if (validateFileColumn.containsKey("C3P_CB_016")) {
					status = false;
					obj.put(new String("response"), status);
					obj.put(new String("errorMessage"), excelFileValidation.getErrorInformation("C3P_CB_016",
							validateFileColumn.get("C3P_CB_016")));
					obj.put(new String("successMessage"), "");
				} else if (validateFileColumn.containsKey("error")) {
					status = false;
					obj.put(new String("response"), status);
					obj.put(new String("errorMessage"), validateFileColumn.get("error").get(0));
					obj.put(new String("successMessage"), "");
				} else if (validateFileColumn.containsKey("error")) {
					status = false;
					obj.put(new String("response"), status);
					obj.put(new String("errorMessage"), validateFileColumn.get("error").get(0));
					obj.put(new String("successMessage"), "");
				} else {
					status = false;
					obj.put(new String("response"), status);
					obj.put(new String("errorMessage"), validateFileColumn);
					obj.put(new String("successMessage"), "");
				}
			}

		} catch (Exception e) {
			status = false;
			obj.put(new String("response"), status);
			obj.put(new String("errorMessage"), e.getMessage());
			obj.put(new String("successMessage"), "");
			logger.error("exception in fileValidationCSVForCOB service" + e.getMessage());
		}
		return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
	}
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/csvFileSaveInDB", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity uploadCSVFile(@RequestParam("file") MultipartFile file, @RequestParam String userName) {

		logger.info("Inside csvFileSaveInDB Service");
		JSONObject obj = new JSONObject();
		boolean success = false;
		List<Map<String, String>> consCSVData = null;
		try {
			consCSVData = excelFileValidation.consolidateCSVData(file);
			success = customerStagingInteface.saveDataFromUploadFile(consCSVData, userName);
            if(success== true)
            {
            	obj.put("status", errorValidationRepository.findByErrorId("C3P_CB_009"));
            	obj.put("statusCode", success );
            }
            else
            {
            	obj.put("status", errorValidationRepository.findByErrorId("C3P_CB_010"));
            	obj.put("statusCode", success );
            }
		} catch (Exception e) {
			obj.put(new String("status"), e.getMessage());
			obj.put(new String("statusCode"), success);
			logger.error("exception in csvFileSaveInDB service" + e.getMessage());

		}
		return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
	}
	
	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/getDashboardData", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity dashboardData(@RequestParam String user,
			@RequestParam String requestType) {
		logger.info("Inside getDashboardData Service");
		JSONObject obj = new JSONObject();
		List<ImportMasterStagingEntity> importMasterData =null;
		int myRequestCount, allRequestCount= 0;
		JSONArray outputArray = new JSONArray();
		JSONObject object = new JSONObject();
		String status="Success";
		try {
			myRequestCount = importMasterStagingRepo.myImportCountStatus(user);
			if("all".equalsIgnoreCase(requestType)) {
				user = "%";
				importMasterData =importMasterStagingRepo.findAllByStatus(status);
			}else	
			    importMasterData =importMasterStagingRepo.findAllByUserNameAndStatus(user,status);
			allRequestCount = importMasterStagingRepo.allImportCountStatus();
			for (ImportMasterStagingEntity entity : importMasterData) {
				object = new JSONObject();
				object.put("importId", entity.getImportId());
				if (entity.getExecutionDate() != null) {
					object.put("executionDate", dateUtil.dateTimeInAppFormat(entity.getExecutionDate().toString()));
				}
				object.put("status", entity.getStatus());
				object.put("totalDevices", entity.getTotalDevices());
				object.put("successORexception", entity.getCountSuccess() + " / " + entity.getCountException());
				object.put("newORexisting", entity.getCountNew() + " / " + entity.getCountExisting());
				object.put("createdBy", entity.getCreatedBy());		
				outputArray.add(object);
			}
            if(importMasterData !=null)
            {	
            	obj.put("myRequestCount", myRequestCount);
            	obj.put("allRequestCount", allRequestCount);
            	obj.put("dashBoardData", outputArray);
            }
            
		} catch (Exception e) {
			obj.put(new String("message"), e);
			logger.error("exception in getDashboardData service" + e.getMessage());

		}
		return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
	}
	
	@GET
	@RequestMapping(value = "/getMyDashboardData", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity discoverDashboard(@RequestParam String user,
			@RequestParam String requestType,@RequestParam String status) {

		logger.info("Inside getMyDashboardData Service");
		JSONObject obj = new JSONObject();
		List<ImportMasterStagingEntity> importMasterData =null;
		JSONArray outputArray = new JSONArray();
		JSONObject object = new JSONObject();
		int myRequestCount, allRequestCount=0;
		try {
			importMasterData =importMasterStagingRepo.findAllByUserNameAndStatus(user,status);
			myRequestCount = importMasterStagingRepo.myImportCountStatus(user);
			allRequestCount = importMasterStagingRepo.allImportCountStatus();
			for (ImportMasterStagingEntity entity : importMasterData) {
				object = new JSONObject();
				object.put("importId", entity.getImportId());
				object.put("executionDate", entity.getExecutionDate().toString());
				object.put("status", entity.getStatus());
				object.put("totalDevices", entity.getTotalDevices());
				object.put("successORexception", entity.getCountSuccess() + " / " + entity.getCountException());
				object.put("newORexisting", entity.getCountNew() + " / " + entity.getCountExisting());
				object.put("createdBy", entity.getCreatedBy());		
				outputArray.add(object);
			}
            if(importMasterData !=null)
            {
            	obj.put("myRequestCount", myRequestCount);
            	obj.put("allRequestCount", allRequestCount);
            	obj.put("dashBoardData", outputArray);
            }
		} catch (Exception e) {
			obj.put(new String("message"), e);
			logger.error("exception in getMyDashboardData service" + e.getMessage());
		}
		return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);

	}
	
	@GET
	@RequestMapping(value = "/generateReport", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity generateReportCOB(@RequestParam String importId) {

		logger.info("Inside generateReport Service");
		JSONObject obj = new JSONObject();
		List<ImportStagingHistory> listStaggingData =null;
		JSONArray staggingArrayData = new JSONArray();
		JSONArray staggingArrayStatus = new JSONArray();
		JSONObject object, status = null;
		List<ImportMasterStagingEntity> importMasterData =null;
		
		try {
			listStaggingData = importStagingHistoryRepo.findByImportId(importId);
			for (ImportStagingHistory entity : listStaggingData) {
				String[] resultStatus = entity.getRowStatus().split(",");
				object = new JSONObject();
				object.put("hostname", entity.getSeq_4());
				object.put("mgtmtIP", entity.getSeq_2());
				object.put("ipv6Value", entity.getSeq_3());
				object.put("result", resultStatus[1]);
				object.put("status", resultStatus[0]);
				object.put("rootCause", entity.getRowErrorCode());	
				staggingArrayData.add(object);
			}
			importMasterData =importMasterStagingRepo.getImportStaggingData(importId);
			for (ImportMasterStagingEntity entity : importMasterData) {
				status = new JSONObject();
				status.put("importId", entity.getImportId());
				status.put("executionDate", entity.getExecutionDate().toString());
				status.put("status", entity.getStatus());
				status.put("totalDevices", entity.getTotalDevices());
				status.put("exception", entity.getCountException());
				status.put("success", entity.getCountSuccess());
				status.put("new", entity.getCountNew());
				status.put("existing", entity.getCountExisting());
				status.put("createdBy", entity.getCreatedBy());
				staggingArrayStatus.add(status);
			}
            if(listStaggingData !=null)
            	obj.put("dashBoardData", staggingArrayData);
            	obj.put("dashBoardStatus", staggingArrayStatus);
		} catch (Exception e) {
			obj.put(new String("message"), e);
			logger.error("exception in generateReport service" + e.getMessage());
		}
		return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);

	}
}

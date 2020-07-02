package com.techm.orion.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.techm.orion.dao.TemplateSuggestionDao;
import com.techm.orion.entitybeans.DeviceInterfaceEntity;
import com.techm.orion.entitybeans.InternetInfoEntity;
import com.techm.orion.entitybeans.RequestDetailsEntity;
import com.techm.orion.entitybeans.RouterVfEntity;
import com.techm.orion.entitybeans.WebServiceEntity;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.Global;
import com.techm.orion.pojo.TemplateBasicConfigurationPojo;
import com.techm.orion.repositories.DeviceInterfaceRepo;
import com.techm.orion.repositories.DeviceTypeRepository;
import com.techm.orion.repositories.InternetInfoRepo;
import com.techm.orion.repositories.ModelsRepository;
import com.techm.orion.repositories.OSRepository;
import com.techm.orion.repositories.OSversionRepository;
import com.techm.orion.repositories.RegionsRepository;
import com.techm.orion.repositories.RequestDetailsExportRepo;
import com.techm.orion.repositories.RequestDetailsImportRepo;
import com.techm.orion.repositories.RouterVfRepo;
import com.techm.orion.repositories.TemplateFeatureRepo;
import com.techm.orion.repositories.VendorRepository;
import com.techm.orion.repositories.WebServiceRepo;
import com.techm.orion.rest.RequestDetailsExport;

/*Class to handle import (single, bulk file) validation, data save, milestone validation and communda flow invocation*/
@Service
public class ExcelReader {
	private static final Logger logger = LogManager.getLogger(ExcelReader.class);
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
	RequestDetailsExportRepo requestDetailsExportRepo;

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

	@Autowired
	TemplateFeatureRepo templateFeatureRepo;

	RequestDetailsEntity requestDetailsEntity;

	/* Method to save data from upload file */
	public boolean saveDataFromUploadFile(MultipartFile file) {

		boolean isFlag = false;
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		if (extension.equalsIgnoreCase("csv")) {
			isFlag = readDataFromCsv(file);
		} else if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")) {
			isFlag = readDataFromExcel(file);
		}

		return isFlag;
	}

	/* Method call to save data from excel file */
	@SuppressWarnings("null")
	private boolean readDataFromExcel(MultipartFile file) {
		try {
			Workbook workbook = getWorkBook(file);

			Sheet sheet = workbook.getSheetAt(0);

			DeviceInterfaceEntity deviceInterfaceEntity = new DeviceInterfaceEntity();
			RouterVfEntity routerVfEntity = new RouterVfEntity();
			WebServiceEntity webServiceEntity = new WebServiceEntity();

			InternetInfoEntity internetInfoEntity = new InternetInfoEntity();

			int rowNumber = 1;
			Row row = null;

			int ctr = 1;
			int requestInfoIdForBulk = 0;
			int noOfRows = 0;
			Cell cell = null;
			boolean isNull = true;
			List<String> header = new ArrayList<String>();
			List<String> rowValue;
			int numberOfCells = 0;

			while (isNull) {
				if ((row = sheet.getRow(ctr)) == null) {
					noOfRows = ctr - 1;

					isNull = false;
					break;
				} else if ((row = sheet.getRow(ctr)) != null) {
					cell = row.getCell(0);

					if (cell.toString().isEmpty()) {

						noOfRows = ctr - 1;

						isNull = false;
						break;

					}

					else {
						ctr++;
					}
				}

			}

			Iterator rowIterator = sheet.rowIterator();

			if (rowIterator.hasNext()) {
				Row headerRow = (Row) rowIterator.next();
				// get the number of cells in the header row
				numberOfCells = headerRow.getPhysicalNumberOfCells();
			}

			for (int i = 1; i < numberOfCells; i++) {
				row = sheet.getRow(0);
				// header= Arrays.asList(row.getCell(i).getStringCellValue());

				String headerValue = row.getCell(i).getStringCellValue();
				// header= Arrays.asList(headerValue);
				header.add(row.getCell(i).getStringCellValue());

			}

			for (int k = 1; k <= noOfRows; k++) {
				Row row1 = null;
				int j = 0;
				rowValue = new ArrayList<String>();
				row1 = sheet.getRow(k);
				for (int i = 1; i < numberOfCells; i++) {

					if (i == 8 || i == 10) {
						double cellValueAsDouble = row1.getCell(i).getNumericCellValue();
						String cellValueAsStringDouble = Double.toString(cellValueAsDouble);
						String cellValueAsString = cellValueAsStringDouble.substring(0, 4);
						rowValue.add(cellValueAsString);
					} else if (row1.getCell(i).getStringCellValue() == "") {
						rowValue.add(null);
					} else {
						rowValue.add(row1.getCell(i).getStringCellValue());
					}

				}

				Map<String, String> map = new LinkedHashMap<String, String>();

				for (int i1 = 0; i1 < (numberOfCells - 1); i1++) {

					map.put(header.get(i1), rowValue.get(i1));

				}
				for (int i1 = 0; i1 < noOfRows; i1++) {
					List<RequestDetailsEntity> requestDtlList = requestDetailsImportRepo.findAll();
					requestDetailsEntity = new RequestDetailsEntity();
					/* Setting up schedule time flag in DB */
					if (header.contains("To Be Scheduled")) {
						for (j = 0; j < header.size(); j++) {
							if (header.get(j).equalsIgnoreCase("To Be Scheduled")) {
								if ((rowValue.get(j) == null)) {
									requestDetailsEntity.setRequestType_Flag("M");
									break;
								} else {
									requestDetailsEntity.setRequestType_Flag("S");
									break;

								}

							}

						}

					}

					for (Map.Entry<String, String> entry : map.entrySet()) {
						// requestDetailsEntity = new RequestDetailsEntity();
						String keyHeader = entry.getKey();

						deviceInterfaceEntity.setRequestInfoId(requestDtlList.size() + 1);
						internetInfoEntity.setRequestInfoId(requestDtlList.size() + 1);
						routerVfEntity.setRequestInfoId(requestDtlList.size() + 1);
						webServiceEntity.setRequestInfoId(requestDtlList.size() + 1);
						requestDetailsEntity.setRequestinfoid(requestDtlList.size() + 1);
						requestDetailsEntity.setRequestCreatorName(Global.loggedInUser);
						requestDetailsEntity.setRequestVersion(1.0);
						requestDetailsEntity.setRequestOwner(Global.loggedInUser);

						if (i1 == 0) {
							requestDetailsEntity.setRequeststatus("In Progress");
							requestInfoIdForBulk = requestDetailsEntity.getRequestinfoid();
							requestDetailsEntity.setImportStatus("In Progress");
						} else {
							requestDetailsEntity.setRequeststatus("In Progress");
							requestDetailsEntity.setImportStatus("Awaiting");
						}
						requestDetailsEntity.setImportid((getAlphaNumericString(8)));

						requestDetailsEntity.setImportsource(FilenameUtils.getExtension(file.getOriginalFilename()));

						if (keyHeader.equalsIgnoreCase("Hostname")) {

							requestDetailsEntity.setHostname((entry.getValue()));

						} else if (keyHeader.equalsIgnoreCase("Customer Name*")) {
							requestDetailsEntity.setCustomer((((entry.getValue()))));

						} else if (keyHeader.equalsIgnoreCase("Site ID*")) {
							requestDetailsEntity.setSiteid((((entry.getValue()))));
						} else if (keyHeader.equalsIgnoreCase("Request Number")) {

							if ((entry.getValue()) == null) {
								requestDetailsEntity.setAlphanumericReqId(getAlphaNumericStringForSR(7));
							} else {
								requestDetailsEntity.setAlphanumericReqId(getAlphaNumericStringForSR(7));
							}

						}

						else if (keyHeader.equalsIgnoreCase("Region*")) {
							requestDetailsEntity.setRegion((((entry.getValue()))));
						} else if (keyHeader.equalsIgnoreCase("Vendor*")) {
							requestDetailsEntity.setVendor((((entry.getValue()))));
						} else if (keyHeader.equalsIgnoreCase("Device Type*")) {
							requestDetailsEntity.setDevice_type((((entry.getValue()))));
						} else if (keyHeader.equalsIgnoreCase("Model*")) {
							requestDetailsEntity.setModel((((entry.getValue()))));
						} else if (keyHeader.equalsIgnoreCase("OS*")) {
							requestDetailsEntity.setOs((((entry.getValue()))));
						} else if (keyHeader.equalsIgnoreCase("OS Version*")) {
							requestDetailsEntity.setOs_version((((entry.getValue()))));
						} else if (keyHeader.equalsIgnoreCase("Management IP*")) {
							requestDetailsEntity.setManagementIp((((entry.getValue()))));
						} else if (keyHeader.equalsIgnoreCase("Service*")) {
							requestDetailsEntity.setService((((entry.getValue()))));
						} else if (keyHeader.equalsIgnoreCase("Network Type*")) {
							requestDetailsEntity.setNetwork_Type((((entry.getValue()))));
						} else if (keyHeader.equalsIgnoreCase("To Be Scheduled")) {
							requestDetailsEntity.setScheduledTime((((entry.getValue()))));
						} else if (keyHeader.equalsIgnoreCase("Template ID")) {
							requestDetailsEntity.setTemplateIdUsed((((entry.getValue()))));
						} else if (keyHeader.equalsIgnoreCase("Banner")) {
							requestDetailsEntity.setBanner(((((entry.getValue())))));
						} else if (keyHeader.equalsIgnoreCase("LAN Interface Name")) {
							requestDetailsEntity.setLanInterface(((((entry.getValue())))));
						} else if (keyHeader.equalsIgnoreCase("LAN IP Address")) {
							requestDetailsEntity.setLanIp(((((entry.getValue())))));
						} else if (keyHeader.equalsIgnoreCase("LAN Subnet Mask")) {
							requestDetailsEntity.setLanMaskAddress(((((entry.getValue())))));
						} else if (keyHeader.equalsIgnoreCase("LAN Description")) {
							requestDetailsEntity.setLanDescription(((((entry.getValue())))));
						} else if (keyHeader.equalsIgnoreCase("Loopback Interface Name")) {
							requestDetailsEntity.setLoopBackType(((((entry.getValue())))));
						} else if (keyHeader.equalsIgnoreCase("Loopback IP Address")) {
							requestDetailsEntity.setLoopbackIPaddress(((((entry.getValue())))));
						} else if (keyHeader.equalsIgnoreCase("Loopback Subnet Mask")) {
							requestDetailsEntity.setLoopbackSubnetMask(((((entry.getValue())))));
						} else if (keyHeader.equalsIgnoreCase("VRF Name")) {
							requestDetailsEntity.setVrf_name(((((entry.getValue())))));
						} else if (keyHeader.equalsIgnoreCase("SNMP HostAddress")) {
							requestDetailsEntity.setSnmpHostAddress(((((entry.getValue())))));
						} else if (keyHeader.equalsIgnoreCase("SNMP String")) {
							requestDetailsEntity.setSnmpString(((((entry.getValue())))));
						}

						else if (keyHeader.equalsIgnoreCase("WAN Interface Name")) {

							deviceInterfaceEntity.setName(entry.getValue());

						} else if (keyHeader.equalsIgnoreCase("WAN Description")) {

							deviceInterfaceEntity.setDescription((entry.getValue()));

						} else if (keyHeader.equalsIgnoreCase("WAN IP Address")) {

							deviceInterfaceEntity.setIp(((entry.getValue())));

						} else if (keyHeader.equalsIgnoreCase("WAN Subnet Mask")) {

							deviceInterfaceEntity.setMask((((entry.getValue()))));

						} else if (keyHeader.equalsIgnoreCase("WAN Speed")) {

							deviceInterfaceEntity.setSpeed((((entry.getValue()))));

						} else if (keyHeader.equalsIgnoreCase("WAN Encapsulation")) {

							deviceInterfaceEntity.setEncapsulation((((entry.getValue()))));

						} else if (keyHeader.equalsIgnoreCase("WAN Bandwidth")) {

							deviceInterfaceEntity.setBandwidth((((entry.getValue()))));

						}

						else if (keyHeader.equalsIgnoreCase("Routing Protocol")) {

							internetInfoEntity.setRoutingProtocol((((entry.getValue()))));

						} else if (keyHeader.equalsIgnoreCase("BGP As Number")) {

							internetInfoEntity.setAsNumber((((entry.getValue()))));

						}

						else if (keyHeader.equalsIgnoreCase("BGP Neighbor1 IP Address")) {

							internetInfoEntity.setNeighbor1((((entry.getValue()))));

						}

						else if (keyHeader.equalsIgnoreCase("BGP Neighbor1 Remote AS")) {

							internetInfoEntity.setNeighbor1RemoteAS((((entry.getValue()))));

						} else if (keyHeader.equalsIgnoreCase("BGP Neighbor2 IP Address")) {

							internetInfoEntity.setNeighbor2((((entry.getValue()))));

						} else if (keyHeader.equalsIgnoreCase("BGP Neighbor2 Remote AS")) {

							internetInfoEntity.setNeighbor2RemoteAS((((entry.getValue()))));

						} else if (keyHeader.equalsIgnoreCase("BGP Network IP")) {

							internetInfoEntity.setNetworkIp((((entry.getValue()))));

						} else if (keyHeader.equalsIgnoreCase("BGP Network IP Mask")) {

							internetInfoEntity.setNetworkIpSubnetMask((((entry.getValue()))));

						}
						webServiceEntity.setStart_test(1);
						webServiceEntity.setGenerate_config(1);
						webServiceEntity.setDeliever_config(0);
						webServiceEntity.setHealth_checkup(0);
						webServiceEntity.setNetwork_test(0);
						webServiceEntity.setApplication_test(0);
						webServiceEntity.setCustomer_report(0);
						webServiceEntity.setFilename(0);
						webServiceEntity.setLatencyResultRes(0);
						webServiceEntity.setAlphanumericReqId(requestDetailsEntity.getAlphanumericReqId());
						webServiceEntity.setVersion(requestDetailsEntity.getRequestVersion());

					}

					/* calling repository to save data in Database */
					requestDetailsImportRepo.save(requestDetailsEntity);
					deviceInterfaceRepo.save(deviceInterfaceEntity);
					internetInfoRepo.save(internetInfoEntity);
					routerVfRepo.save(routerVfEntity);
					webServiceRepo.save(webServiceEntity);

				}
				List<RequestDetailsEntity> requestByRequestStatus = requestDetailsImportRepo.findAll();
				Boolean isCheck = false;
				int requestInfoId = 0;
				/*
				 * setting up "In Progress and Awaiting status" on Import Dash board
				 */
				for (int i = (requestInfoIdForBulk - 1); i < requestByRequestStatus.size(); i++) {
					String status = requestByRequestStatus.get(i).getImportStatus();
					if (status == null) {

						continue;
					}

					else if (status.equals("In Progress")) {
						requestInfoId = requestByRequestStatus.get(i).getRequestinfoid();
						isCheck = true;

					}

					while (isCheck) {

						/*
						 * calling validate method to perform milestone validation in back end
						 */
						validate(requestInfoId);
						/* Bulk and single import request execution */
						for (int i1 = (requestInfoIdForBulk - 1); i1 < requestByRequestStatus.size(); i1++) {

							String importStatusAfterValidation = requestByRequestStatus.get(i1).getImportStatus();
							String milestoneAfterValidation = requestByRequestStatus.get(i1)
									.getValidationMilestoneBits();
							if (importStatusAfterValidation == null) {

								continue;
							}
							/*
							 * Processing of one by one request on Import Dash Board
							 */
							else if ((importStatusAfterValidation.equals("Success")
									|| importStatusAfterValidation.equals("Awaiting"))
									&& milestoneAfterValidation.equals("11111")) {
								requestInfoId = requestByRequestStatus.get(i1).getRequestinfoid();

								if (importStatusAfterValidation.equals("Awaiting")) {

									String importStatus = "Success";
									int id = i1 + 1;
									requestDetailsImportRepo.updateImportStatus(importStatus, id);
								}

								TemplateSuggestionDao templateUsgaeCount = new TemplateSuggestionDao();
								CreateConfigRequestDCM createConfig = new CreateConfigRequestDCM();

								DcmConfigService importSRDcmConfigService = new DcmConfigService();

								DeviceInterfaceEntity ExportList2 = new DeviceInterfaceEntity();
								InternetInfoEntity ExportList3 = new InternetInfoEntity();
								/*
								 * Setting variable values to push them on communda
								 */
								createConfig.setCustomer(requestByRequestStatus.get(i1).getCustomer());
								createConfig.setBanner(requestByRequestStatus.get(i1).getBanner());
								createConfig.setDeviceType(requestByRequestStatus.get(i1).getDevice_type());
								createConfig.setModel(requestByRequestStatus.get(i1).getModel());
								createConfig.setOs(requestByRequestStatus.get(i1).getOs());
								createConfig.setRegion(requestByRequestStatus.get(i1).getRegion());
								createConfig.setService(requestByRequestStatus.get(i1).getService());
								createConfig.setOsVersion(requestByRequestStatus.get(i1).getOs_version());
								createConfig.setRequest_version(1.0);
								createConfig.setVersion_report("1.0");
								createConfig.setHostname(requestByRequestStatus.get(i1).getHostname());
								createConfig.setVpn(requestByRequestStatus.get(i1).getVpn());
								createConfig.setVendor(requestByRequestStatus.get(i1).getVendor());
								createConfig.setVrfName(requestByRequestStatus.get(i1).getVrf_name());
								createConfig.setIsAutoProgress(false);
								createConfig.setTemplateID(requestByRequestStatus.get(i1).getTemplateIdUsed());
								createConfig.setManagementIp(requestByRequestStatus.get(i1).getManagementIp());
								createConfig.setSiteid(requestByRequestStatus.get(i1).getSiteid());
								createConfig.setEnablePassword(requestByRequestStatus.get(i1).getEnable_password());
								createConfig.setRequestId(requestByRequestStatus.get(i1).getAlphanumericReqId());
								createConfig.setImportsource(requestByRequestStatus.get(i1).getImportsource());

								ExportList2 = deviceInterfaceRepo
										.findByRequestInfoId(requestByRequestStatus.get(i1).getRequestinfoid());
								ExportList3 = internetInfoRepo
										.findByRequestInfoId(requestByRequestStatus.get(i1).getRequestinfoid());

								createConfig.setNetworkIp(ExportList3.getNetworkIp());

								createConfig.setNetworkIp(ExportList3.getNetworkIp());
								createConfig.setNeighbor1(ExportList3.getNeighbor1());
								createConfig.setNeighbor2(ExportList3.getNeighbor2());
								createConfig.setNeighbor1_remoteAS(ExportList3.getNeighbor1RemoteAS());
								createConfig.setNeighbor2_remoteAS(ExportList3.getNeighbor2RemoteAS());
								createConfig.setNetworkIp_subnetMask(ExportList3.getNetworkIpSubnetMask());
								createConfig.setRoutingProtocol(ExportList3.getRoutingProtocol());
								createConfig.setBgpASNumber(ExportList3.getAsNumber());

								createConfig.setName(ExportList2.getName());
								createConfig.setDescription(ExportList2.getDescription());
								createConfig.setIp(ExportList2.getIp());
								createConfig.setMask(ExportList2.getMask());
								createConfig.setSpeed(ExportList2.getSpeed());
								createConfig.setBandwidth(ExportList2.getBandwidth());
								createConfig.setEncapsulation(ExportList2.getEncapsulation());
								createConfig.setLanInterface(requestByRequestStatus.get(i1).getLanInterface());
								createConfig.setLanDescription(requestByRequestStatus.get(i1).getLanDescription());
								createConfig.setLanIp(requestByRequestStatus.get(i1).getLanIp());
								createConfig.setVpn(requestByRequestStatus.get(i1).getVpn());
								createConfig.setLoopBackType(requestByRequestStatus.get(i1).getLoopBackType());
								createConfig
										.setLoopbackIPaddress(requestByRequestStatus.get(i1).getLoopbackIPaddress());
								createConfig
										.setLoopbackSubnetMask(requestByRequestStatus.get(i1).getLoopbackSubnetMask());
								createConfig.setLanMaskAddress(requestByRequestStatus.get(i1).getLanMaskAddress());
								createConfig.setRequestCreatedOn(requestByRequestStatus.get(i1).getDateofProcessing());

								if (requestByRequestStatus.get(i1).getScheduledTime() != null) {
									createConfig.setScheduledTime(requestByRequestStatus.get(i1).getScheduledTime());
								}
								/*
								 * Generation of configuration and header file on local drive
								 */
								importSRDcmConfigService.createTemplate(createConfig);
								/* Updating template uses data in Database */
								String templateIdUsed = createConfig.getTemplateID();
								templateUsgaeCount.insertTemplateUsageData(templateIdUsed);

								if (templateIdUsed != null) {

									int id = i1 + 1;
									requestDetailsImportRepo.updateSuggestedTemplateId(templateIdUsed, id);
								}

								/*
								 * Thread halt to restart communda in case of bulk SR
								 */
								try {

									Thread.sleep(70000);
								} catch (Exception e) {
									logger.error(e);
								}
								/* Invocation of communda flow */
								TelnetCommunicationSSHImportSR telnetCommunicationSSHImportSR = new TelnetCommunicationSSHImportSR(
										createConfig);
								telnetCommunicationSSHImportSR.setDaemon(true);
								telnetCommunicationSSHImportSR.start();
								/*
								 * Thread halt to process next SR before previous SR completion
								 */
								try {

									Thread.sleep(180000);
								} catch (Exception e) {
									logger.error(e);
								}

							}

						}
						isCheck = false;
					}

				}
			}
		} catch (Exception e) {
			logger.error(e);
			return false;
		}

		return true;
	}

	/* Method call to check for .xls and .xlsx file type */
	private Workbook getWorkBook(MultipartFile file) {
		Workbook workbook = null;
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());

		try {
			if (extension.equalsIgnoreCase("xls")) {
				workbook = new HSSFWorkbook(file.getInputStream());
			} else if (extension.equalsIgnoreCase("xlsx")) {
				workbook = new XSSFWorkbook(file.getInputStream());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return workbook;
	}

	/* Method call to save data from .csv file */
	private boolean readDataFromCsv(MultipartFile file) {
		try {

			InputStreamReader reader = new InputStreamReader(file.getInputStream());

			int requestInfoIdForBulk = 0;
			DeviceInterfaceEntity deviceInterfaceEntity = new DeviceInterfaceEntity();
			RouterVfEntity routerVfEntity = new RouterVfEntity();
			WebServiceEntity webServiceEntity = new WebServiceEntity();

			InternetInfoEntity internetInfoEntity = new InternetInfoEntity();

			CSVReader csvReader = new CSVReaderBuilder(reader).build();
			List<String[]> rows = csvReader.readAll();

			List<String> header = null;
			List<String> rowValue = null;

			int rowSize = rows.size();
			int j = 0;

			/* checking for single or bulk request type */
			if (rowSize == 2) {
				for (int i = 0; i < (rows.size() - 1); i++) {
					header = Arrays.asList(rows.get(i));

				}
			} else {
				for (int i = (rows.size() - 1); i >= 0; i--) {
					header = Arrays.asList(rows.get(i));

				}
			}
			/* Storing row wise value in Map */
			for (int i = 1; i < (rows.size()); i++) {

				rowValue = Arrays.asList(rows.get(i));

				Map<String, String> map = new LinkedHashMap<String, String>();

				for (int i1 = 0; i1 < header.size(); i1++) {

					if (rowValue.get(i1).isEmpty() || rowValue.get(i1).equals("null")) {
						rowValue.set(i1, null);
					}

					map.put(header.get(i1), rowValue.get(i1));
				}

				List<RequestDetailsEntity> requestDtlList = requestDetailsImportRepo.findAll();
				requestDetailsEntity = new RequestDetailsEntity();
				/* Setting up schedule time flag in DB */
				if (header.contains("To Be Scheduled")) {
					for (j = 0; j < header.size(); j++) {
						if (header.get(j).equalsIgnoreCase("To Be Scheduled")) {
							if ((rowValue.get(j) == null)) {
								requestDetailsEntity.setRequestType_Flag("M");
								break;
							} else {
								requestDetailsEntity.setRequestType_Flag("S");
								break;

							}

						}

					}

				}

				for (Map.Entry<String, String> entry : map.entrySet()) {

					String keyHeader = entry.getKey();

					deviceInterfaceEntity.setRequestInfoId(requestDtlList.size() + 1);
					internetInfoEntity.setRequestInfoId(requestDtlList.size() + 1);
					routerVfEntity.setRequestInfoId(requestDtlList.size() + 1);
					webServiceEntity.setRequestInfoId(requestDtlList.size() + 1);
					requestDetailsEntity.setRequestinfoid(requestDtlList.size() + 1);
					requestDetailsEntity.setRequestCreatorName(Global.loggedInUser);
					requestDetailsEntity.setRequestVersion(1.0);
					requestDetailsEntity.setRequestOwner(Global.loggedInUser);

					if (i == 1) {
						requestDetailsEntity.setRequeststatus("In Progress");
						requestInfoIdForBulk = requestDetailsEntity.getRequestinfoid();
						requestDetailsEntity.setImportStatus("In Progress");
					} else {
						requestDetailsEntity.setRequeststatus("In Progress");
						requestDetailsEntity.setImportStatus("Awaiting");
					}
					requestDetailsEntity.setImportid((getAlphaNumericString(8)));

					requestDetailsEntity.setImportsource(FilenameUtils.getExtension(file.getOriginalFilename()));

					if (keyHeader.equalsIgnoreCase("Hostname")) {

						requestDetailsEntity.setHostname((entry.getValue()));

					} else if (keyHeader.equalsIgnoreCase("Customer Name*")) {
						requestDetailsEntity.setCustomer((((entry.getValue()))));

					} else if (keyHeader.equalsIgnoreCase("Site ID*")) {
						requestDetailsEntity.setSiteid((((entry.getValue()))));
					} else if (keyHeader.equalsIgnoreCase("Request Number")) {

						if ((entry.getValue()) == null) {
							requestDetailsEntity.setAlphanumericReqId(getAlphaNumericStringForSR(7));
						} else {
							requestDetailsEntity.setAlphanumericReqId(getAlphaNumericStringForSR(7));
						}

					}

					else if (keyHeader.equalsIgnoreCase("Region*")) {
						requestDetailsEntity.setRegion((((entry.getValue()))));
					} else if (keyHeader.equalsIgnoreCase("Vendor*")) {
						requestDetailsEntity.setVendor((((entry.getValue()))));
					} else if (keyHeader.equalsIgnoreCase("Device Type*")) {
						requestDetailsEntity.setDevice_type((((entry.getValue()))));
					} else if (keyHeader.equalsIgnoreCase("Model*")) {
						requestDetailsEntity.setModel((((entry.getValue()))));
					} else if (keyHeader.equalsIgnoreCase("OS*")) {
						requestDetailsEntity.setOs((((entry.getValue()))));
					} else if (keyHeader.equalsIgnoreCase("OS Version*")) {
						requestDetailsEntity.setOs_version((((entry.getValue()))));
					} else if (keyHeader.equalsIgnoreCase("Management IP*")) {
						requestDetailsEntity.setManagementIp((((entry.getValue()))));
					} else if (keyHeader.equalsIgnoreCase("Service*")) {
						requestDetailsEntity.setService((((entry.getValue()))));
					} else if (keyHeader.equalsIgnoreCase("Network Type*")) {
						requestDetailsEntity.setNetwork_Type((((entry.getValue()))));
					} else if (keyHeader.equalsIgnoreCase("To Be Scheduled")) {
						requestDetailsEntity.setScheduledTime((((entry.getValue()))));
					} else if (keyHeader.equalsIgnoreCase("Template ID")) {
						requestDetailsEntity.setTemplateIdUsed((((entry.getValue()))));
					} else if (keyHeader.equalsIgnoreCase("Banner")) {
						requestDetailsEntity.setBanner(((((entry.getValue())))));
					} else if (keyHeader.equalsIgnoreCase("LAN Interface Name")) {
						requestDetailsEntity.setLanInterface(((((entry.getValue())))));
					} else if (keyHeader.equalsIgnoreCase("LAN IP Address")) {
						requestDetailsEntity.setLanIp(((((entry.getValue())))));
					} else if (keyHeader.equalsIgnoreCase("LAN Subnet Mask")) {
						requestDetailsEntity.setLanMaskAddress(((((entry.getValue())))));
					} else if (keyHeader.equalsIgnoreCase("LAN Description")) {
						requestDetailsEntity.setLanDescription(((((entry.getValue())))));
					} else if (keyHeader.equalsIgnoreCase("Loopback Interface Name")) {
						requestDetailsEntity.setLoopBackType(((((entry.getValue())))));
					} else if (keyHeader.equalsIgnoreCase("Loopback IP Address")) {
						requestDetailsEntity.setLoopbackIPaddress(((((entry.getValue())))));
					} else if (keyHeader.equalsIgnoreCase("Loopback Subnet Mask")) {
						requestDetailsEntity.setLoopbackSubnetMask(((((entry.getValue())))));
					} else if (keyHeader.equalsIgnoreCase("VRF Name")) {
						requestDetailsEntity.setVrf_name(((((entry.getValue())))));
					} else if (keyHeader.equalsIgnoreCase("SNMP HostAddress")) {
						requestDetailsEntity.setSnmpHostAddress(((((entry.getValue())))));
					} else if (keyHeader.equalsIgnoreCase("SNMP String")) {
						requestDetailsEntity.setSnmpString(((((entry.getValue())))));
					}

					else if (keyHeader.equalsIgnoreCase("WAN Interface Name")) {

						deviceInterfaceEntity.setName(entry.getValue());

					} else if (keyHeader.equalsIgnoreCase("WAN Description")) {

						deviceInterfaceEntity.setDescription((entry.getValue()));

					} else if (keyHeader.equalsIgnoreCase("WAN IP Address")) {

						deviceInterfaceEntity.setIp(((entry.getValue())));

					} else if (keyHeader.equalsIgnoreCase("WAN Subnet Mask")) {

						deviceInterfaceEntity.setMask((((entry.getValue()))));

					} else if (keyHeader.equalsIgnoreCase("WAN Speed")) {

						deviceInterfaceEntity.setSpeed((((entry.getValue()))));

					} else if (keyHeader.equalsIgnoreCase("WAN Encapsulation")) {

						deviceInterfaceEntity.setEncapsulation((((entry.getValue()))));

					} else if (keyHeader.equalsIgnoreCase("WAN Bandwidth")) {

						deviceInterfaceEntity.setBandwidth((((entry.getValue()))));

					}

					else if (keyHeader.equalsIgnoreCase("Routing Protocol")) {

						internetInfoEntity.setRoutingProtocol((((entry.getValue()))));

					} else if (keyHeader.equalsIgnoreCase("BGP As Number")) {

						internetInfoEntity.setAsNumber((((entry.getValue()))));

					}

					else if (keyHeader.equalsIgnoreCase("BGP Neighbor1 IP Address")) {

						internetInfoEntity.setNeighbor1((((entry.getValue()))));

					}

					else if (keyHeader.equalsIgnoreCase("BGP Neighbor1 Remote AS")) {

						internetInfoEntity.setNeighbor1RemoteAS((((entry.getValue()))));

					} else if (keyHeader.equalsIgnoreCase("BGP Neighbor2 IP Address")) {

						internetInfoEntity.setNeighbor2((((entry.getValue()))));

					} else if (keyHeader.equalsIgnoreCase("BGP Neighbor2 Remote AS")) {

						internetInfoEntity.setNeighbor2RemoteAS((((entry.getValue()))));

					} else if (keyHeader.equalsIgnoreCase("BGP Network IP")) {

						internetInfoEntity.setNetworkIp((((entry.getValue()))));

					} else if (keyHeader.equalsIgnoreCase("BGP Network IP Mask")) {

						internetInfoEntity.setNetworkIpSubnetMask((((entry.getValue()))));

					}
					webServiceEntity.setStart_test(1);
					webServiceEntity.setGenerate_config(1);
					webServiceEntity.setDeliever_config(0);
					webServiceEntity.setHealth_checkup(0);
					webServiceEntity.setNetwork_test(0);
					webServiceEntity.setApplication_test(0);
					webServiceEntity.setCustomer_report(0);
					webServiceEntity.setFilename(0);
					webServiceEntity.setLatencyResultRes(0);
					webServiceEntity.setAlphanumericReqId(requestDetailsEntity.getAlphanumericReqId());
					webServiceEntity.setVersion(requestDetailsEntity.getRequestVersion());

				}

				/* calling repository to save data in Database */
				requestDetailsImportRepo.save(requestDetailsEntity);
				deviceInterfaceRepo.save(deviceInterfaceEntity);
				internetInfoRepo.save(internetInfoEntity);
				routerVfRepo.save(routerVfEntity);
				webServiceRepo.save(webServiceEntity);

			}
			List<RequestDetailsEntity> requestByRequestStatus = requestDetailsImportRepo.findAll();
			Boolean isCheck = false;
			int requestInfoId = 0;
			/* setting up "In Progress and Awaiting status" on Import Dash board */
			for (int i = (requestInfoIdForBulk - 1); i < requestByRequestStatus.size(); i++) {
				String status = requestByRequestStatus.get(i).getImportStatus();
				if (status == null) {

					continue;
				}

				else if (status.equals("In Progress")) {
					requestInfoId = requestByRequestStatus.get(i).getRequestinfoid();
					isCheck = true;

				}
				while (isCheck) {

					/*
					 * calling validate method to perform milestone validation in back end
					 */
					validate(requestInfoId);
					/* Bulk and single import request execution */
					for (int i1 = (requestInfoIdForBulk - 1); i1 < requestByRequestStatus.size(); i1++) {

						String importStatusAfterValidation = requestByRequestStatus.get(i1).getImportStatus();
						String milestoneAfterValidation = requestByRequestStatus.get(i1).getValidationMilestoneBits();
						if (importStatusAfterValidation == null) {

							continue;
						}
						/* Processing of one by one request on Import Dash Board */
						else if ((importStatusAfterValidation.equals("Success")
								|| importStatusAfterValidation.equals("Awaiting"))
								&& milestoneAfterValidation.equals("11111")) {
							requestInfoId = requestByRequestStatus.get(i1).getRequestinfoid();

							if (importStatusAfterValidation.equals("Awaiting")) {

								String importStatus = "Success";
								int id = i1 + 1;
								requestDetailsImportRepo.updateImportStatus(importStatus, id);
							}

							TemplateSuggestionDao templateUsgaeCount = new TemplateSuggestionDao();
							CreateConfigRequestDCM createConfig = new CreateConfigRequestDCM();

							DcmConfigService importSRDcmConfigService = new DcmConfigService();

							DeviceInterfaceEntity ExportList2 = new DeviceInterfaceEntity();
							InternetInfoEntity ExportList3 = new InternetInfoEntity();
							/* Setting variable values to push them on communda */
							createConfig.setCustomer(requestByRequestStatus.get(i1).getCustomer());
							createConfig.setBanner(requestByRequestStatus.get(i1).getBanner());
							createConfig.setDeviceType(requestByRequestStatus.get(i1).getDevice_type());
							createConfig.setModel(requestByRequestStatus.get(i1).getModel());
							createConfig.setOs(requestByRequestStatus.get(i1).getOs());
							createConfig.setRegion(requestByRequestStatus.get(i1).getRegion());
							createConfig.setService(requestByRequestStatus.get(i1).getService());
							createConfig.setOsVersion(requestByRequestStatus.get(i1).getOs_version());
							createConfig.setRequest_version(1.0);
							createConfig.setVersion_report("1.0");
							createConfig.setHostname(requestByRequestStatus.get(i1).getHostname());
							createConfig.setVpn(requestByRequestStatus.get(i1).getVpn());
							createConfig.setVendor(requestByRequestStatus.get(i1).getVendor());
							createConfig.setVrfName(requestByRequestStatus.get(i1).getVrf_name());
							createConfig.setIsAutoProgress(false);
							createConfig.setTemplateID(requestByRequestStatus.get(i1).getTemplateIdUsed());
							createConfig.setManagementIp(requestByRequestStatus.get(i1).getManagementIp());
							createConfig.setSiteid(requestByRequestStatus.get(i1).getSiteid());
							createConfig.setEnablePassword(requestByRequestStatus.get(i1).getEnable_password());
							createConfig.setRequestId(requestByRequestStatus.get(i1).getAlphanumericReqId());
							createConfig.setImportsource(requestByRequestStatus.get(i1).getImportsource());

							ExportList2 = deviceInterfaceRepo
									.findByRequestInfoId(requestByRequestStatus.get(i1).getRequestinfoid());
							ExportList3 = internetInfoRepo
									.findByRequestInfoId(requestByRequestStatus.get(i1).getRequestinfoid());

							createConfig.setNetworkIp(ExportList3.getNetworkIp());

							createConfig.setNetworkIp(ExportList3.getNetworkIp());
							createConfig.setNeighbor1(ExportList3.getNeighbor1());
							createConfig.setNeighbor2(ExportList3.getNeighbor2());
							createConfig.setNeighbor1_remoteAS(ExportList3.getNeighbor1RemoteAS());
							createConfig.setNeighbor2_remoteAS(ExportList3.getNeighbor2RemoteAS());
							createConfig.setNetworkIp_subnetMask(ExportList3.getNetworkIpSubnetMask());
							createConfig.setRoutingProtocol(ExportList3.getRoutingProtocol());
							createConfig.setBgpASNumber(ExportList3.getAsNumber());

							createConfig.setName(ExportList2.getName());
							createConfig.setDescription(ExportList2.getDescription());
							createConfig.setIp(ExportList2.getIp());
							createConfig.setMask(ExportList2.getMask());
							createConfig.setSpeed(ExportList2.getSpeed());
							createConfig.setBandwidth(ExportList2.getBandwidth());
							createConfig.setEncapsulation(ExportList2.getEncapsulation());
							createConfig.setLanInterface(requestByRequestStatus.get(i1).getLanInterface());
							createConfig.setLanDescription(requestByRequestStatus.get(i1).getLanDescription());
							createConfig.setLanIp(requestByRequestStatus.get(i1).getLanIp());
							createConfig.setVpn(requestByRequestStatus.get(i1).getVpn());
							createConfig.setLoopBackType(requestByRequestStatus.get(i1).getLoopBackType());
							createConfig.setLoopbackIPaddress(requestByRequestStatus.get(i1).getLoopbackIPaddress());
							createConfig.setLoopbackSubnetMask(requestByRequestStatus.get(i1).getLoopbackSubnetMask());
							createConfig.setLanMaskAddress(requestByRequestStatus.get(i1).getLanMaskAddress());
							createConfig.setRequestCreatedOn(requestByRequestStatus.get(i1).getDateofProcessing());

							if (requestByRequestStatus.get(i1).getScheduledTime() != null) {
								createConfig.setScheduledTime(requestByRequestStatus.get(i1).getScheduledTime());
							}

							/*
							 * Generation of configuration and header file on local drive
							 */
							importSRDcmConfigService.createTemplate(createConfig);
							/* Updating template uses data in Database */
							String templateIdUsed = createConfig.getTemplateID();
							templateUsgaeCount.insertTemplateUsageData(templateIdUsed);

							if (templateIdUsed != null) {

								int id = i1 + 1;
								requestDetailsImportRepo.updateSuggestedTemplateId(templateIdUsed, id);
							}

							/*
							 * Thread halt to restart communda in case of bulk SR
							 */
							try {

								Thread.sleep(70000);
							} catch (Exception e) {
								logger.error(e);
							}
							/* Invocation of communda flow */
							TelnetCommunicationSSHImportSR telnetCommunicationSSHImportSR = new TelnetCommunicationSSHImportSR(
									createConfig);
							telnetCommunicationSSHImportSR.setDaemon(true);
							telnetCommunicationSSHImportSR.start();
							/*
							 * Thread halt to process next SR before previous SR completion
							 */
							try {

								Thread.sleep(180000);
							} catch (Exception e) {
								logger.error(e);
							}

						}

					}
					isCheck = false;
				}

			}

		} catch (Exception e) {
			logger.error(e);
			return false;
		}

		return true;
	}

	/* Method call to generate Import Id */
	static String getAlphaNumericString(int n) {

		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";

		StringBuilder sb = new StringBuilder(n);

		for (int i = 0; i < n; i++) {

			int index = (int) (AlphaNumericString.length() * Math.random());

			sb.append(AlphaNumericString.charAt(index));
		}

		return sb.toString().concat("-1");
	}

	/* Method call to generate service request Id */
	static String getAlphaNumericStringForSR(int n) {

		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";

		StringBuilder sb = new StringBuilder(n);

		for (int i = 0; i < n; i++) {

			int index = (int) (AlphaNumericString.length() * Math.random());

			sb.append(AlphaNumericString.charAt(index));
		}
		String firstName = "SR-";
		String secondName = sb.toString();
		return firstName.concat(secondName);
	}

	/* Method call for milestone validation */
	public void validate(@RequestParam int request_info_id)
			throws IOException, SQLException, ParseException, java.text.ParseException {

		RequestDetailsEntity ExportList = new RequestDetailsEntity();
		ExportList = requestDetailsExportRepo.findByrequestinfoid(request_info_id);

		ExportList = requestDetailsExportRepo.findByrequestinfoid(request_info_id);
		String Status = ExportList.getImportStatus();

		if (Status.equals("In Progress")) {

			int MileStone = ValidMilestone(request_info_id);
			int request_info_id_new = request_info_id + 1;
			while (MileStone == 1) {

				ExportList = requestDetailsExportRepo.findByrequestinfoid(request_info_id_new);
				MileStone = ValidMilestone(request_info_id_new);
				request_info_id_new = request_info_id_new + 1;
			}
		}

	}

	/* Method call to set certification bits */
	public int ValidMilestone(int request_info_id)
			throws IOException, SQLException, ParseException, java.text.ParseException {
		int TemplateData = 0, DataValidation = 0, TestandTurn = 0, Scheduler = 0, MileStoneData = 0, DataFormat = 0;
		RequestDetailsEntity ExportList = new RequestDetailsEntity();
		ExportList = requestDetailsExportRepo.findByrequestinfoid(request_info_id);
		String Status = ExportList.getRequeststatus();
		List<String> ValidationMilestoneBits = new ArrayList<String>();
		int DataFormatt = validateImport(request_info_id);

		ValidationMilestoneBits.add(0, "0");
		ValidationMilestoneBits.add(1, "2");
		ValidationMilestoneBits.add(2, "2");
		ValidationMilestoneBits.add(3, "2");
		ValidationMilestoneBits.add(4, "2");
		if (DataFormatt == 1) {
			ValidationMilestoneBits.set(0, "1");
			ValidationMilestoneBits.set(1, "0");
			ValidationMilestoneBits.set(2, "2");
			ValidationMilestoneBits.set(3, "2");
			ValidationMilestoneBits.set(4, "2");
			TemplateData = validateTemplate(request_info_id);
			if (TemplateData == 1) {
				ValidationMilestoneBits.set(1, "1");
				ValidationMilestoneBits.set(2, "0");
				ValidationMilestoneBits.set(3, "2");
				ValidationMilestoneBits.set(4, "2");
				DataValidation = validateData(request_info_id);
				if (TemplateData == 1 && DataValidation == 1) {

					ValidationMilestoneBits.set(2, "1");
					ValidationMilestoneBits.set(3, "0");
					ValidationMilestoneBits.set(4, "2");
					TestandTurn = validateTest(request_info_id);

					if (TemplateData == 1 && DataValidation == 1 && TestandTurn == 1) {
						ValidationMilestoneBits.set(3, "1");

						ValidationMilestoneBits.set(4, "0");

						Scheduler = validateScheduler(request_info_id);
						if (TemplateData == 1 && DataValidation == 1 && TestandTurn == 1 && Scheduler == 1) {
							ValidationMilestoneBits.set(4, "1");

							int request_info_id_new = request_info_id + 1;

							ExportList = requestDetailsExportRepo.findByrequestinfoid(request_info_id_new);
							if (ExportList == null) {
								DataFormat = 0;
							} else {
								String Statusnew = ExportList.getImportStatus();
								if (Statusnew.equals("Awaiting")) {
									DataFormat = 1;
								}
							}

						}
						if (TemplateData == 1 && DataValidation == 1 && TestandTurn == 1 && Scheduler == 3) {
							ValidationMilestoneBits.set(4, "3");
							int request_info_id_new = request_info_id + 1;

							ExportList = requestDetailsExportRepo.findByrequestinfoid(request_info_id_new);
							if (ExportList == null) {
								DataFormat = 0;
							} else {
								String Statusnew = ExportList.getImportStatus();
								if (Statusnew.equals("Awaiting")) {
									DataFormat = 1;
								}
							}

						}

					}
				}
			}
		}

		if (Status != null) {
			int request_info_id_new = request_info_id + 1;
			ExportList = requestDetailsExportRepo.findByrequestinfoid(request_info_id_new);
			if (ExportList == null) {
				DataFormat = 0;
			} else {
				String Statusnew = ExportList.getImportStatus();
				if (Statusnew.equals("Awaiting")) {
					DataFormat = 1;
				}
			}

		}
		int request_info_id_new = request_info_id;
		ExportList = requestDetailsExportRepo.findByrequestinfoid(request_info_id_new);
		String[] stringArray1 = new String[ValidationMilestoneBits.size()];
		logger.info("Validation Milestone Bits");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ValidationMilestoneBits.size(); i++) {
			stringArray1[i] = ValidationMilestoneBits.get(i);
			logger.info(stringArray1[i]);
			sb.append(stringArray1[i]);
		}
		ExportList.setValidationMilestoneBits(sb.toString());
		requestDetailsExportRepo.save(ExportList);
		return DataFormat;

	}

	/* Method call for data validation */
	@SuppressWarnings("unchecked")
	public int validateImport(int request_info_id) throws IOException {

		JSONObject obj = new JSONObject();

		String jsonArray = "";
		String jsonArray1 = "";
		RequestDetailsEntity ExportList = new RequestDetailsEntity();

		RequestDetailsExport.loadProperties();

		JSONObject names = new JSONObject();
		JSONObject values = new JSONObject();
		int Datavalid = 0;
		try {

			ExportList = requestDetailsExportRepo.findByrequestinfoid(request_info_id);

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

			int request_id = ExportList.getRequestinfoid();

			String request = Integer.toString(request_id);

			String Vendor = ExportList.getVendor();

			boolean flag1 = false;
			for (String string : vendorlist) {

				if (string.equals(Vendor)) {

					flag1 = true;
					logger.info("Matched VENDOR");
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
					logger.info("Matched device");

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
					logger.info("Matched model");
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
					logger.info("Matched os");
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
					logger.info("Matched os version");
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

			if (flag1 == true && flag2 == true && flag3 == true && flag4 == true && flag5 == true && flag6 == true) {
				Datavalid = 1;

			} else {
				Datavalid = 0;
				MessageReport = "Device Support Validation Failed";
				ExportList.setImportStatus(MessageReport);
				requestDetailsExportRepo.save(ExportList);

			}

			jsonArray = new Gson().toJson(names);
			jsonArray1 = new Gson().toJson(values);
			obj.put(new String("Values"), jsonArray1);
			obj.put(new String("output"), jsonArray);
			obj.put(new String("Datavalid"), Datavalid);
		} catch (Exception e) {
			logger.error(e);
		}

		return Datavalid;

	}

	/* Method for template suggestion */
	@SuppressWarnings({ "unchecked", "unchecked", "unchecked", "unchecked" })
	public int validateTemplate(int request_info_id) throws IOException, SQLException, ParseException {
		int TemplateSuggesstion = 0;

		JSONObject obj = new JSONObject();

		String jsonArray = "";

		RequestDetailsEntity ExportList = new RequestDetailsEntity();
		DeviceInterfaceEntity ExportList2 = new DeviceInterfaceEntity();
		InternetInfoEntity ExportList3 = new InternetInfoEntity();

		String TemplateList1;
		List<String> TemplateFeatures = new ArrayList<String>();

		RequestDetailsExport.loadProperties();

		ExportList = requestDetailsExportRepo.findByrequestinfoid(request_info_id);
		ExportList2 = deviceInterfaceRepo.findByRequestInfoId(request_info_id);
		ExportList3 = internetInfoRepo.findByRequestInfoId(request_info_id);

		String Template_id = ExportList.getTemplateIdUsed();

		boolean flag = false;
		int flag2 = 1;

		String Region = ExportList.getRegion();
		String vendor = ExportList.getVendor();
		String model = ExportList.getModel();
		String os = ExportList.getOs();
		String osversion = ExportList.getOs_version();

		if (isNullOrEmpty(Template_id)) {
			RequestDetailsExport.loadProperties();
			String path = RequestDetailsExport.TSA_PROPERTIES.getProperty("templateCreationPath") + "\\" + Template_id;
			File file = new File(path);
			String template = null;
			DcmConfigService dcmConfigService = new DcmConfigService();
			CreateConfigRequestDCM createConfigRequestDCM = new CreateConfigRequestDCM();

			createConfigRequestDCM.setModel(ExportList.getModel());
			createConfigRequestDCM.setOs(ExportList.getOs());
			createConfigRequestDCM.setOsVersion(ExportList.getOs_version());
			createConfigRequestDCM.setRegion(ExportList.getRegion());
			createConfigRequestDCM.setVendor(ExportList.getVendor());

			String templateId = dcmConfigService.getTemplateName(createConfigRequestDCM.getRegion(),
					createConfigRequestDCM.getVendor(), createConfigRequestDCM.getModel(),
					createConfigRequestDCM.getOs(), createConfigRequestDCM.getOsVersion());
			String Template_id1 = ExportList.getTemplateIdUsed().substring(0, 14);
			String versionLastString = ExportList.getTemplateIdUsed().substring(18, 19);

			int versionLastNumber = Integer.parseInt(versionLastString);

			List<String> feature_list = new ArrayList<String>();
			if (templateId.equals(Template_id1)) {

				if (file.exists()) {

					for (int i = 0; i <= versionLastNumber; i++) {

						String str1 = Integer.toString(i);
						String Template_id_check = Template_id1.concat("_V1.").concat(str1);
						TemplateList1 = templateFeatureRepo.findByCommand(Template_id_check);

						feature_list.add(TemplateList1);

					}

					logger.info("Features present in Given Template:");

					String Basic = "Basic Configuration";
					feature_list.add(Basic);

					List<String> feature = validateFeatures(request_info_id);
					logger.info("Features present in Import:");
					for (int i = 0; i < feature.size(); i++) {
						logger.info(feature.get(i));
					}

					List<List<String>> list = Arrays.asList(feature_list);
					List<List<String>> sublist = Arrays.asList(feature);
					List<String> one;
					List<String> two;
					one = new ArrayList<String>(feature_list);
					two = new ArrayList<String>(feature);
					Collections.sort(one);
					Collections.sort(two);

					logger.info(one.containsAll(two));
					boolean compare = one.containsAll(two);

					if (compare == true) {
						logger.info("present");
					} else {
						logger.info("not present");
						flag2 = 0;
					}

					if (flag2 == 0) {
						logger.info("Features Dont Match");
						flag = false;
						logger.info("Invalid");
						obj.put(new String("Result"), "Failure");
						obj.put(new String("Message"), "C3P does not support this template");
						TemplateSuggesstion = 0;
						obj.put(new String("TemplateSuggesstion"), TemplateSuggesstion);
						String MessageReport = "Template Validation Failed";
						ExportList.setImportStatus(MessageReport);
						requestDetailsExportRepo.save(ExportList);

					} else {
						flag = true;
						template = "Valid";
						obj.put(new String("Result"), "Success");
						obj.put(new String("Message"), "");
						TemplateSuggesstion = 1;
						obj.put(new String("TemplateSuggesstion"), TemplateSuggesstion);
					}

				}

				else {
					logger.info("No Approved template Found");
					flag = false;
					logger.info("Invalid");
					obj.put(new String("Result"), "Failure");
					obj.put(new String("Message"), "C3P does not support this template");
					TemplateSuggesstion = 0;
					obj.put(new String("TemplateSuggesstion"), TemplateSuggesstion);
					String MessageReport = "Template Validation Failed";
					ExportList.setImportStatus(MessageReport);
					requestDetailsExportRepo.save(ExportList);
				}
			} else {
				logger.info("Template DO Not Match Device Details");
				flag = false;
				logger.info("Invalid");
				obj.put(new String("Result"), "Failure");
				obj.put(new String("Message"), "C3P does not support this template");
				TemplateSuggesstion = 0;
				obj.put(new String("TemplateSuggesstion"), TemplateSuggesstion);
				String MessageReport = "Template Validation Failed";
				ExportList.setImportStatus(MessageReport);
				requestDetailsExportRepo.save(ExportList);
			}

		}

		else {

			DcmConfigService dcmConfigService = new DcmConfigService();
			TemplateSuggestionDao templateSuggestionDao = new TemplateSuggestionDao();

			JSONArray array = new JSONArray();

			JSONObject jsonObj;
			try {

				JSONParser parser = new JSONParser();

				CreateConfigRequestDCM createConfigRequestDCM = new CreateConfigRequestDCM();

				createConfigRequestDCM.setModel(ExportList.getModel());
				createConfigRequestDCM.setOs(ExportList.getOs());
				createConfigRequestDCM.setOsVersion(ExportList.getOs_version());
				createConfigRequestDCM.setRegion(ExportList.getRegion());
				createConfigRequestDCM.setVendor(ExportList.getVendor());

				String templateId = dcmConfigService.getTemplateName(createConfigRequestDCM.getRegion(),
						createConfigRequestDCM.getVendor(), createConfigRequestDCM.getModel(),
						createConfigRequestDCM.getOs(), createConfigRequestDCM.getOsVersion());

				List<String> feat1 = validateFeatures(request_info_id);

				logger.info("Features present in Import:");
				for (int i = 0; i < feat1.size(); i++) {

					logger.info(feat1.get(i));
				}

				if (feat1.size() > 0) {
					for (int i = 0; i < feat1.size(); i++) {
						jsonObj = new JSONObject();
						jsonObj.put("value", feat1.get(i));

						array.put(jsonObj);
					}
					jsonArray = array.toString();
					obj.put(new String("Result"), "Success");
					obj.put(new String("Message"), "Success");
					obj.put(new String("featureList"), jsonArray);
					obj.put(new String("templateId"), templateId);
				} else {
					obj.put(new String("Result"), "Failure");
					obj.put(new String("Message"), "No features Present.Create the template first");
					obj.put(new String("featureList"), null);
				}

			}

			catch (Exception e) {
				logger.error(e);
			}
			String jsonList = "";
			String TemplateList = "";
			try {

				JSONParser parser = new JSONParser();

				List<String> feat1 = validateFeatures(request_info_id);

				for (int i = 0; i < feat1.size(); i++) {

					feat1.get(i);
				}

				CreateConfigRequestDCM createConfigRequestDCM = new CreateConfigRequestDCM();

				createConfigRequestDCM.setModel(ExportList.getModel());
				createConfigRequestDCM.setOs(ExportList.getOs());
				createConfigRequestDCM.setOsVersion(ExportList.getOs_version());
				createConfigRequestDCM.setRegion(ExportList.getRegion());
				createConfigRequestDCM.setVendor(ExportList.getVendor());

				String templateId = dcmConfigService.getTemplateName(createConfigRequestDCM.getRegion(),
						createConfigRequestDCM.getVendor(), createConfigRequestDCM.getModel(),
						createConfigRequestDCM.getOs(), createConfigRequestDCM.getOsVersion());

				String[] features = feat1.toArray(new String[feat1.size()]);

				List<TemplateBasicConfigurationPojo> templateBasicConfigurationPojo = templateSuggestionDao
						.getDataGrid(features, templateId);
				List<String> templateList = new ArrayList<String>();
				for (TemplateBasicConfigurationPojo template : templateBasicConfigurationPojo) {
					templateList.add(template.getTemplateId());

				}
				logger.info("List of Templates Present:" + templateList);

				jsonList = new Gson().toJson(templateBasicConfigurationPojo);
				if (!(jsonList.equals("[]"))) {

					obj.put(new String("TemplateDetailList"), jsonList);
					obj.put(new String("Result"), "Success");
					obj.put(new String("Message"), "Success");
					ExportList.setTemplateIdUsed(TemplateList);
					logger.info("Matched Template Found for Selected features");
					if (TemplateList != null) {
						RequestDetailsExport.loadProperties();
						String path = RequestDetailsExport.TSA_PROPERTIES.getProperty("templateCreationPath") + "\\"
								+ TemplateList;
						File file = new File(path);
						String template = null;

						if (file.exists()) {
							flag = true;
							template = "Valid";
							obj.put(new String("Result"), "Success");
							obj.put(new String("Message"), "");
							TemplateSuggesstion = 1;

							obj.put(new String("TemplateSuggesstion"), TemplateSuggesstion);
							logger.info("Approved Template Found for Selected features");

						} else {
							flag = false;
							logger.info("No Approved Template Found for Selected features");
							logger.info("Invalid");
							obj.put(new String("Result"), "Failure");
							obj.put(new String("Message"), "C3P does not support this template");
							TemplateSuggesstion = 0;
							obj.put(new String("TemplateSuggesstion"), TemplateSuggesstion);
							String MessageReport = "Template Validation Failed";
							ExportList.setImportStatus(MessageReport);
							requestDetailsExportRepo.save(ExportList);
						}

						TemplateSuggesstion = 1;
						obj.put(new String("TemplateSuggesstion"), TemplateSuggesstion);

					}
				} else {
					obj.put(new String("Result"), "Failure");
					obj.put(new String("Message"), "No Data.Create the template first");
					obj.put(new String("TemplateDetailList"), null);
					TemplateSuggesstion = 0;
					obj.put(new String("TemplateSuggesstion"), TemplateSuggesstion);
					String MessageReport = "Template Validation Failed";
					ExportList.setImportStatus(MessageReport);
					requestDetailsExportRepo.save(ExportList);
					logger.info("No Matched Template Found for Selected features");
				}

			} catch (Exception e) {
				logger.error(e);
			}

		}

		return TemplateSuggesstion;

	}

	/* Method call for data validation */
	public int validateData(@RequestParam int request_info_id) {
		int Datavalidation = 0;
		RequestDetailsEntity entity = new RequestDetailsEntity();
		Gson gson = new Gson();

		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String jsonArray = "";

		RequestDetailsEntity ExportList = new RequestDetailsEntity();
		DeviceInterfaceEntity ExportList2 = new DeviceInterfaceEntity();
		InternetInfoEntity ExportList3 = new InternetInfoEntity();
		ExportList = requestDetailsExportRepo.findByrequestinfoid(request_info_id);

		try {
			RequestDetailsExport.loadProperties();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> Validation = validateDataFeatures(request_info_id);
		String result = "Invalid";

		String[] stringArray = new String[Validation.size()];

		for (int j = 0; j < Validation.size(); j++) {

			stringArray[j] = Validation.get(j);
			if (stringArray[j].equals("0")) {
				logger.info("INVALID DATA REPORT");
				result = "InValid";
				break;
			} else if (stringArray[j].equals("1")) {
				result = "Valid";
			}

		}

		if (result == "Valid") {
			Datavalidation = 1;
			obj.put(new String("Result"), "Success");
		} else {
			Datavalidation = 0;
			obj.put(new String("Result"), "Failure");
			String MessageReport = "Data Validation Failed";
			ExportList.setImportStatus(MessageReport);
			requestDetailsExportRepo.save(ExportList);
		}

		obj.put(new String("Datavalidation"), Datavalidation);

		return Datavalidation;

	}

	/* Method call for test validation */
	public int validateTest(@RequestParam int request_info_id) throws IOException, SQLException {
		int TestAndTurnUp = 0;
		RequestDetailsEntity entity = new RequestDetailsEntity();
		Gson gson = new Gson();

		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String jsonArray = "";

		RequestDetailsEntity ExportList = new RequestDetailsEntity();

		RequestDetailsExport.loadProperties();

		ExportList = requestDetailsExportRepo.findByrequestinfoid(request_info_id);

		List<String> Validation = validateDataFeatures(request_info_id);

		String[] stringArray = new String[Validation.size()];
		List<String> CertificationImportFlag = new ArrayList<String>();
		for (int j = 0; j < Validation.size(); j++) {
			stringArray[j] = Validation.get(j);
			if (stringArray[j].equals("Basic Configuration")) {

				CertificationImportFlag.add(0, "1"); // Interface Status
				CertificationImportFlag.add(1, "0"); // WAN Interface
				CertificationImportFlag.add(2, "1"); // Platform & IOS
				CertificationImportFlag.add(3, "0"); // BGP or Routing Protocol
				CertificationImportFlag.add(4, "0"); // Throughput
				CertificationImportFlag.add(5, "1"); // Frameloss
				CertificationImportFlag.add(6, "1"); // Latency

			} else if (stringArray[j].equals("WAN Interface")) {
				CertificationImportFlag.set(1, "1"); // WAN Interface
				CertificationImportFlag.set(4, "1"); // Throughput
			} else if (stringArray[j].equals("Routing Protocol")) {
				CertificationImportFlag.set(3, "1"); // BGP

			}
			// logger.info(stringArray[j]);
		}

		String[] stringArray1 = new String[CertificationImportFlag.size()];
		logger.info("Certification Flag");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < CertificationImportFlag.size(); i++) {
			stringArray1[i] = CertificationImportFlag.get(i);
			logger.info(stringArray1[i]);
			sb.append(stringArray1[i]);
		}

		String result = "Valid";

		if (result == "Valid") {
			TestAndTurnUp = 1;
			ExportList.setCertificationSelectionBit(sb.toString());
			obj.put(new String("Result"), "Success");
			requestDetailsExportRepo.save(ExportList);
		} else {
			TestAndTurnUp = 0;
			obj.put(new String("Result"), "Failure");
			ExportList.setCertificationSelectionBit(sb.toString());
			// String MessageReport = "Test and TurnUp Validation Failed";
			// ExportList.setImport_status(MessageReport);
			requestDetailsExportRepo.save(ExportList);
		}

		obj.put(new String("TestAndTurnUp"), TestAndTurnUp);

		return TestAndTurnUp;

	}

	/* Method call for scheduler */
	public int validateScheduler(int request_info_id)
			throws IOException, SQLException, java.text.ParseException, ParseException {
		int Scheduler = 0;
		RequestDetailsEntity entity = new RequestDetailsEntity();
		Gson gson = new Gson();

		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String jsonArray = "";

		RequestDetailsEntity ExportList = new RequestDetailsEntity();

		RequestDetailsExport.loadProperties();

		ExportList = requestDetailsExportRepo.findByrequestinfoid(request_info_id);
		String Scheduled;
		String Result = null, Message;
		String RunNow;
		boolean flag;
		if (ExportList.getRequestType_Flag().equals("M"))

		{

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			logger.info(dtf.format(now));

			RunNow = dtf.format(now);
			ExportList.setDateofProcessing(RunNow);
			requestDetailsExportRepo.save(ExportList);
			obj.put(new String("RunNow"), RunNow);
			obj.put(new String("SchedulerResult"), "Success");
			Result = "Valid";
			Message = "Valid Scheduled Time";
		}

		else {
			String path1 = null;
			Scheduled = ExportList.getScheduledTime();

			if (Scheduled.length() >= 20) {

				path1 = Scheduled.substring(0, 20);
				char ch;
				ch = path1.charAt(19);

				if (ch == 'P' || ch == ' ') {
					path1 = path1.replace(ch, ' ');
					ch = path1.charAt(19);

				} else if (ch == 'M') {
					path1 = Scheduled.substring(0, 18);
				}

				logger.info(path1);
			} else {

				path1 = Scheduled;

			}
			String Timeset = null;
			int match = 0;

			if (path1.matches(
					"^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])\\s([0-1]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)\\s*$")) {
				flag = true;
				Timeset = Scheduled.substring(0, 19);
				logger.info(Timeset);
				match = 1;
			} else if (path1.matches("^\\d{1,2}\\/\\d{1,2}\\/\\d{4}\\s([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")) {
				flag = true;
				match = 2;
				if (Scheduled.length() > 20) {
					String Timesetnew = Scheduled.substring(0, 20);
					logger.info(Timesetnew);
				}
				String Timesetnew = Scheduled;

				DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
				Date d2 = df.parse(Timesetnew);
				Timeset = df2.format(d2);

				logger.info(Timeset);

			} else {
				flag = false;
				logger.info("Invalid Date Format");
				Result = "Failed";
			}

			if (flag == true) {
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				LocalDateTime now = LocalDateTime.now();
				logger.info(dtf.format(now));
				RunNow = dtf.format(now);

				int Value = compareDates(RunNow, Timeset);
				if (Value == 1) {
					logger.info("Scheduled Date is VALID and after Current Date");

					String Timesetnew = ExportList.getScheduledTime();
					if (match == 1) {
						Timesetnew = ExportList.getScheduledTime().substring(0, 19);
						logger.info(Timesetnew);

					} else if (match == 2) {

						Timesetnew = Scheduled;
						Timeset = Timesetnew;
						DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
						Date d2 = df.parse(Timesetnew);
						Timeset = df2.format(d2);

						logger.info(Timeset);
					}

					DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
					LocalDateTime nowSchedule = LocalDateTime.now();
					RunNow = dtf.format(nowSchedule);

					ExportList.setScheduledTime(Timeset);
					ExportList.setDateofProcessing(RunNow);
					ExportList.setRequeststatus("Scheduled");
					ExportList.setEnd_date_of_processing("0000-00-00 00:00:00");

					requestDetailsExportRepo.save(ExportList);
					obj.put(new String("Scheduled"), Timeset);
					obj.put(new String("SchedulerResult"), "Success");
					Result = "Valid";
					Message = "Valid Scheduled Time";
					obj.put(new String("Message"), Message);
				} else {
					logger.info("Date of processing is NOT VALID");

					obj.put(new String("SchedulerResult"), "Failure");
					Result = "Invalid";
					Message = "Date should be greater than Current Time ";
					obj.put(new String("Message"), Message);

				}

			}
			if (flag == false) {
				Scheduler = 0;
				String MessageReport = "Scheduled Time Validation Failed";
				ExportList.setImportStatus(MessageReport);
				requestDetailsExportRepo.save(ExportList);
				Result = "Failed";
			}

		}

		if (Result == "Valid") {
			Scheduler = 1;
			if (ExportList.getImportStatus().equals("In Progress")) {
				ExportList.setImportStatus("Success");
				requestDetailsExportRepo.save(ExportList);
			} else {
				ExportList.setImportStatus("Awaiting");
				requestDetailsExportRepo.save(ExportList);

			}

		} else if (Result == "Invalid") {
			Scheduler = 3;
			String MessageReport = "Scheduled Time Validation Failed";
			ExportList.setImportStatus(MessageReport);
			requestDetailsExportRepo.save(ExportList);
		} else if (Result == "Failed") {
			Scheduler = 0;
		}

		obj.put(new String("Scheduler"), Scheduler);
		obj.put(new String("SchedulerResult"), Result);

		return Scheduler;

	}

	/* Method call to check for null or empty string */
	public static boolean isNullOrEmpty(String str) {
		if (str != null && !str.isEmpty())
			return true;
		return false;
	}

	/* Method call to validate template features */
	List<String> validateDataFeatures(int request_info_id) {

		RequestDetailsEntity ExportList = new RequestDetailsEntity();
		DeviceInterfaceEntity ExportList2 = new DeviceInterfaceEntity();
		InternetInfoEntity ExportList3 = new InternetInfoEntity();

		ExportList = requestDetailsExportRepo.findByrequestinfoid(request_info_id);
		ExportList2 = deviceInterfaceRepo.findByRequestInfoId(ExportList.getRequestinfoid());
		ExportList3 = internetInfoRepo.findByRequestInfoId(ExportList.getRequestinfoid());

		InetAddressValidator ipValidator = new InetAddressValidator();

		List<String> Valid = new ArrayList<String>();
		List<String> feat1 = validateFeatures(request_info_id);
		String[] stringArray1 = new String[feat1.size()];
		logger.info("Features for Validation");
		for (int i = 0; i < feat1.size(); i++) {
			stringArray1[i] = feat1.get(i);
			logger.info(stringArray1[i]);
		}

		boolean flagwan, flaglan, flagloop, flagrout, flagenable, flagsnmp, flagbanner, flagvrf = false;
		List<String> feat = new ArrayList<String>();
		String result = "Invalid";
		int length = stringArray1.length;

		for (int i = 0; i < length; i++) {

			if (stringArray1[i].equals("WAN Interface"))

			{
				if ((isNullOrEmpty(ExportList2.getName())) & (isNullOrEmpty(ExportList2.getDescription()))
						& ((ipValidator.isValid(ExportList2.getIp()))) & ((ipValidator.isValid(ExportList2.getMask())))
						& (isNullOrEmpty(ExportList2.getEncapsulation()))
						& (isNullOrEmpty(ExportList2.getBandwidth()))) {
					flagwan = true;
					String wan = "WAN Interface";
					logger.info("WAN feature is  VALID");

					Valid.add(0, "1");
					Valid.add(1, wan);

				} else {

					logger.info("WAN feature is INVALID");

					Valid.add(0, "0");
				}
			}
			if (stringArray1[i].equals("LAN Interface")) {
				if ((isNullOrEmpty(ExportList.getLanDescription())) & (isNullOrEmpty(ExportList.getLanInterface()))
						& (ipValidator.isValid(ExportList.getLanIp()))
						& (ipValidator.isValid(ExportList.getLanMaskAddress()))) {
					flaglan = true;
					String lan = "LAN Interface";
					logger.info("LAN feature is VALID");

					Valid.add(0, "1");
					Valid.add(1, lan);

				} else {
					logger.info("LAN feature is INVALID");

					Valid.add(0, "0");
				}
			}
			if (stringArray1[i].equals("Loopback Interface")) {

				if ((isNullOrEmpty(ExportList.getLoopbackIPaddress()))
						& (ipValidator.isValid(ExportList.getLoopbackSubnetMask()))
						& (isNullOrEmpty(ExportList.getLoopBackType()))) {
					flagloop = true;
					String loop = "Loopback Interface";
					logger.info("Loopback feature is VALID");

					Valid.add(0, "1");
					Valid.add(1, loop);

				} else {
					logger.info("Loopback feature is INVALID");

					Valid.add(0, "0");
				}
			}

			if (stringArray1[i].equals("Routing Protocol")) {
				int asnumber = Integer.parseInt(ExportList3.getAsNumber());
				if ((isNullOrEmpty(ExportList3.getRoutingProtocol())) & (isNullOrEmpty(ExportList3.getAsNumber()))
						& (isNullOrEmpty(ExportList3.getNeighbor1()))
						& (isNullOrEmpty(ExportList3.getNeighbor1RemoteAS()))
						& (isNullOrEmpty(ExportList3.getNeighbor2()))
						& (isNullOrEmpty(ExportList3.getNeighbor2RemoteAS()))
						& (ipValidator.isValid(ExportList3.getNetworkIpSubnetMask()))
						& (ipValidator.isValid(ExportList3.getNetworkIp()))) {
					flagrout = true;
					String rout = "Routing Protocol";
					logger.info("BGP feature is VALID");

					Valid.add(0, "1");
					Valid.add(1, rout);

				} else {

					logger.info("Routing Protocol feature is INVALID");

					Valid.add(0, "0");
				}
			}

			if (stringArray1[i].equals("Enable Password")) {

				if ((isNullOrEmpty(ExportList.getEnable_password()))) {
					flagenable = true;
					String pass = "Enable Password";
					logger.info("Enable password feature is VALID");

					Valid.add(0, "1");
					Valid.add(1, pass);

				} else {
					logger.info("Enable Password feature is INVALID");

					Valid.add(0, "0");
				}
			}

			if (stringArray1[i].equals("SNMP")) {
				if ((isNullOrEmpty(ExportList.getSnmpString())) & (!(ExportList.getSnmpHostAddress().isEmpty()))) {
					flagsnmp = true;
					String snmp = "SNMP";
					logger.info("snmp feature is VALID");

					Valid.add(0, "1");
					Valid.add(1, snmp);

				} else {
					logger.info("SNMP feature is INVALID");

					Valid.add(0, "0");
				}
			}

			if (stringArray1[i].equals("Banner")) {
				if ((isNullOrEmpty(ExportList.getBanner()))) {
					flagbanner = true;
					String banner = "Banner";
					logger.info("Banner feature is VALID");

					Valid.add(0, "1");
					Valid.add(1, banner);

				} else {

					logger.info("Banner feature is INVALID");

					Valid.add(0, "0");
				}
			}

			if (stringArray1[i].equals("VRF")) {
				if ((isNullOrEmpty(ExportList.getVrf_name()))) {

					flagvrf = true;
					String vrf = "VRF";
					logger.info("vrf feature is VALID");

					Valid.add(0, "1");
					Valid.add(1, vrf);

				} else {
					logger.info("VRF feature is NOT VALID");

					Valid.add(0, "0");
				}
			}
			if (stringArray1[i].equals("Basic Configuration")) {
				logger.info("Basic Configuration is VALID");
				String bc = "Basic Configuration";
				Valid.add(0, "1");
				Valid.add(1, bc);
			}

		}
		return Valid;

	}

	/* Method call to validate features */
	public List<String> validateFeatures(int request_info_id) {
		boolean flagwan, flaglan, flagloop, flagrout, flagenable, flagsnmp, flagbanner, flagvrf = false;
		List<String> feat = new ArrayList<String>();

		RequestDetailsEntity ExportList = new RequestDetailsEntity();
		DeviceInterfaceEntity ExportList2 = new DeviceInterfaceEntity();
		InternetInfoEntity ExportList3 = new InternetInfoEntity();

		ExportList = requestDetailsExportRepo.findByrequestinfoid(request_info_id);
		ExportList2 = deviceInterfaceRepo.findByRequestInfoId(request_info_id);
		ExportList3 = internetInfoRepo.findByRequestInfoId(request_info_id);

		if ((isNullOrEmpty(ExportList2.getName())) || (isNullOrEmpty(ExportList2.getDescription()))
				|| (isNullOrEmpty(ExportList2.getIp())) || (isNullOrEmpty(ExportList2.getMask()))
				|| (isNullOrEmpty(ExportList2.getEncapsulation())) || (isNullOrEmpty(ExportList2.getBandwidth()))) {
			flagwan = true;
			String wan = "WAN Interface";
			logger.info("WAN feature is PRESENT");
			feat.add(wan);

		} else {
			logger.info("WAN feature is NOT PRESENT");
		}

		if ((isNullOrEmpty(ExportList.getLanDescription())) || (isNullOrEmpty(ExportList.getLanInterface()))
				|| (isNullOrEmpty(ExportList.getLanIp())) || (isNullOrEmpty(ExportList.getLanMaskAddress()))) {
			flaglan = true;
			String lan = "LAN Interface";
			logger.info("LAN feature is PRESENT");
			feat.add(lan);
		} else {
			logger.info("LAN feature is NOT PRESENT");
		}

		if ((isNullOrEmpty(ExportList.getLoopbackIPaddress())) || (isNullOrEmpty(ExportList.getLoopbackSubnetMask()))
				|| (isNullOrEmpty(ExportList.getLoopBackType()))) {
			flagloop = true;
			String loop = "Loopback Interface";
			logger.info("Loopback feature is PRESENT");
			feat.add(loop);

		} else {
			logger.info("Loopback feature is NOT PRESENT");
		}
		if ((isNullOrEmpty(ExportList3.getRoutingProtocol()))
				// || (isNullOrEmpty(ExportList3.getAsNumber()))
				|| (isNullOrEmpty(ExportList3.getNeighbor1())) || (isNullOrEmpty(ExportList3.getNeighbor1RemoteAS()))
				|| (isNullOrEmpty(ExportList3.getNeighbor2())) || (isNullOrEmpty(ExportList3.getNeighbor2RemoteAS()))
				|| (isNullOrEmpty(ExportList3.getNetworkIpSubnetMask()))) {
			flagrout = true;
			String rout = "Routing Protocol";
			logger.info("Routing Protocol feature is PRESENT");
			feat.add(rout);

		} else {
			logger.info("BGP feature is NOT PRESENT");
		}
		if (isNullOrEmpty(ExportList.getEnable_password())) {
			flagenable = true;
			String pass = "Enable Password";
			logger.info("Enable Password feature is PRESENT");
			feat.add(pass);

		} else {
			logger.info("Enable password feature is NOT PRESENT");
		}

		if ((isNullOrEmpty(ExportList.getSnmpString())) || (isNullOrEmpty(ExportList.getSnmpHostAddress()))) {
			flagsnmp = true;
			String snmp = "SNMP";
			logger.info("SNMP feature is PRESENT");
			feat.add(snmp);

		} else {
			logger.info("snmp feature is NOT PRESENT");
		}

		if (isNullOrEmpty(ExportList.getBanner())) {
			flagbanner = true;
			String banner = "Banner";
			logger.info("Banner feature is PRESENT");
			feat.add(banner);

		} else {
			logger.info("banner feature is NOT PRESENT");
		}
		if (isNullOrEmpty(ExportList.getVrf_name())) {
			flagvrf = true;
			String vrf = "VRF";
			logger.info("VRF feature is PRESENT");
			feat.add(vrf);

		} else {
			logger.info("vrf feature is NOT PRESENT");
		}
		String Basic = "Basic Configuration";
		feat.add(Basic);

		return feat;
	}

	/* Method call to compare two dates in case of schedule request */
	public static int compareDates(String d1, String d2) throws java.text.ParseException, ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date1 = sdf.parse(d1);
		Date date2 = sdf.parse(d2);

		logger.info("Current Time:" + sdf.format(date1));
		logger.info("Scheduled Time:" + sdf.format(date2));

		int Valid = 0;

		if (date1.before(date2)) {
			logger.info("Scheduled time is after Current time");
			Valid = 1;
		}
		if (date1.after(date2)) {
			logger.info("Scheduled time is before Current time");
			logger.info("Invalid");
			Valid = 0;
		}
		return Valid;
	}

}

package com.techm.orion.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.opencsv.CSVWriter;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.entitybeans.DeviceInterfaceEntity;
import com.techm.orion.entitybeans.InternetInfoEntity;
import com.techm.orion.entitybeans.RequestDetailsEntity;
import com.techm.orion.repositories.DeviceInterfaceRepo;
import com.techm.orion.repositories.InternetInfoRepo;
import com.techm.orion.repositories.RequestDetailsExportRepo;
import com.techm.orion.repositories.RouterVfRepo;
import com.techm.orion.repositories.WebServiceRepo;
import com.techm.orion.service.DcmConfigService;

@RestController
@RequestMapping("/RequestDetailsExport")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class RequestDetailsExport implements Observer {
	
	private static final String FILE_PATH = null;

	private static final String STYLE_CELL_BORDERED = null;

	public static String TSA_PROPERTIES_FILE = "TSA.properties";

	public static final Properties TSA_PROPERTIES = new Properties();

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

	RequestInfoDao requestInfoDao = new RequestInfoDao();

	@GET
	@Produces("application/vnd.ms-excel")
	@RequestMapping(value = "/exportcsv", method = RequestMethod.GET, produces = "application/vnd.ms-excel")
	public void downloadExcelFile(HttpServletResponse response,
			@RequestParam String alphanumericReqId,
			@RequestParam Double requestVersion, String search)
			throws IOException {
		//boolean flag = false;
		RequestDetailsEntity entity = new RequestDetailsEntity();
		Gson gson = new Gson();

		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String jsonArray = "";

		RequestDetailsEntity ExportList = new RequestDetailsEntity();
		DeviceInterfaceEntity ExportList2 = new DeviceInterfaceEntity();
		InternetInfoEntity ExportList3 = new InternetInfoEntity();
		//RouterVfEntity ExportList4 = new RouterVfEntity();
		//WebServiceEntity ExportList5 = new WebServiceEntity();
		
		

		DcmConfigService dcmConfigService = new DcmConfigService();
		RequestDetailsExport.loadProperties();
		String path = RequestDetailsExport.TSA_PROPERTIES
				.getProperty("exportDownloadPath")
				+ "\\"
				+ alphanumericReqId
				+ "_RequestExport.csv";
		File file = new File(path);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
		
		
			
			ExportList = requestDetailsExportRepo
					.findByAlphanumericReqIdAndRequestVersion(
							alphanumericReqId, requestVersion);
			ExportList2 = deviceInterfaceRepo.findByRequestInfoId(ExportList
					.getRequestinfoid());
			ExportList3 = internetInfoRepo.findByRequestInfoId(ExportList
					.getRequestinfoid());
			//ExportList4 = routerVfRepo.findByRequestInfoId(ExportList
				//	.getRequest_info_id());
			//ExportList5 = webServiceRepo.findByRequestInfoId(ExportList
					//.getRequest_info_id());
			// Blank workbook
			XSSFWorkbook workbook = new XSSFWorkbook();

			// Create a blank sheet
			XSSFSheet sheet = workbook.createSheet("Request_Data");

		
			// This data needs to be written (Object[])
	

			try
			 {
			    // FileWriter writer = new FileWriter(path);
			     CSVWriter writer = new CSVWriter(new FileWriter(path));
			
			   
			     String [] RequestInfo = "SR#,Request Number,Hostname,Customer Name*,Site ID*,Region*,Vendor*,Device Type*,Model*,OS*,OS Version*,Management IP*,Service*,Network Type*,To Be Scheduled,Template ID,Banner".split(",");
							
			     String [] Wan="WAN Interface Name,WAN Description,WAN IP Address,WAN Subnet Mask,WAN Encapsulation,WAN Bandwidth".split(",");
							 
			     String [] Lan="LAN Interface Name,LAN IP Address,LAN Subnet Mask,LAN Description".split(","); 
								
				String [] Loop="Loopback Interface Name,Loopback IP Address,Loopback Subnet Mask".split(",");
				
				String [] Bgp="Routing Protocol,BGP As Number,BGP Neighbor1 IP Address,BGP Neighbor1 Remote AS,BGP Neighbor2 IP Address,BGP Neighbor2 Remote AS,BGP Network IP,BGP Network IP Mask".split(",");
								
				String [] snmp="VRF Name,SNMP HostAddress,SNMP String".split(",");
				
			     String [] combined = ArrayUtils.addAll(RequestInfo,Wan);
			     String [] combined1= ArrayUtils.addAll(combined,Lan);
			     String [] combined2= ArrayUtils.addAll(combined1,Loop);
			     String [] combined3= ArrayUtils.addAll(combined2,Bgp);
			     String [] combined4= ArrayUtils.addAll(combined3,snmp);
			  
			      writer.writeNext(combined4);
			   
			      writer.flush();
			      
			  	String Scheduled = null;
				String AsNumber= null;
				if(ExportList.getRequestType_Flag().equals("S"))
				{
					Scheduled = ExportList.getScheduledTime();
				}
				
				String bgp=ExportList3.getRoutingProtocol();
				
				if(bgp!=null)
				{
					if(ExportList3.getRoutingProtocol().equals("BGP"))
					{
					AsNumber = ExportList3.getAsNumber();
					
					}
				}
				
				if(ExportList.getLanDescription().equals("\n"))
				{
					String lanDesToTrim = ExportList.getLanDescription();
					ExportList.setLanDescription(lanDesToTrim.trim());
				}
			      
			      //String [] RequestInfovalue="";
			     
				Map<String, Object[]> data = new TreeMap<String, Object[]>();
				data.put("1",new String[]{});
				data.put("2",
						new String[] { "1",
								ExportList.getAlphanumericReqId(),
								ExportList.getHostname(),
								ExportList.getCustomer(),
								ExportList.getSiteid(),
								ExportList.getRegion(),
								ExportList.getVendor(), 
								ExportList.getDevice_type(),
								//ExportList.getDevice_name(),
								 ExportList.getModel(),
								ExportList.getOs(),
								ExportList.getOs_version(),
								ExportList.getManagementIp(),
								 ExportList.getService(),
								 "Legacy",
								 (String)Scheduled,
								ExportList.getTemplateIdUsed(),
								 
								ExportList.getBanner(),
								//ExportList.getEnable_password(),
//								 ExportList.getVpn(),
								 
							 //////////////////////////
							 
							ExportList2.getName(),
							ExportList2.getDescription(), 
							ExportList2.getIp(),
							ExportList2.getMask(), 
//							ExportList2.getSpeed(),
							ExportList2.getEncapsulation(),
							ExportList2.getBandwidth(),
							 ///////////////////
							ExportList.getLanInterface(),
							ExportList.getLanIp(),
							ExportList.getLanMaskAddress(),
							ExportList.getLanDescription(),
							/////////////
							
							ExportList.getLoopBackType(),
							ExportList.getLoopbackIPaddress(),
							ExportList.getLoopbackSubnetMask(),
						/////////////
							ExportList3.getRoutingProtocol(),
							(String)AsNumber,
							//ExportList3.getAsNumber(),
							ExportList3.getNeighbor1(),
							ExportList3.getNeighbor1RemoteAS(),
							ExportList3.getNeighbor2(),
							ExportList3.getNeighbor2RemoteAS(),
							ExportList3.getNetworkIp(),
							
							ExportList3.getNetworkIpSubnetMask(),
							
							////////////////////
							ExportList.getVrf_name(),
								
								ExportList.getSnmpHostAddress(),
								ExportList.getSnmpString(),
							});
				
				java.util.Set<String> keyset = data.keySet();
				
				int rownum = 0;
				for (String key : keyset) {

					
					Row row = sheet.createRow(rownum++);
					
					int num = 1;
					if (num == Integer.parseInt(key)) {
						XSSFFont font = workbook.createFont();// Create font
						CellStyle style6 = workbook.createCellStyle();
						style6.setFillForegroundColor(IndexedColors.GREY_25_PERCENT
								.getIndex());
						style6.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						style6.setBottomBorderColor(IndexedColors.BLACK
								.getIndex());
						style6.setLeftBorderColor(IndexedColors.BLACK
								.getIndex());
						style6.setRightBorderColor(IndexedColors.BLACK
								.getIndex());
						style6.setTopBorderColor(IndexedColors.BLACK.getIndex());
						style6.setBorderLeft(BorderStyle.THICK);
						style6.setBorderRight(BorderStyle.THICK);
						style6.setBorderTop(BorderStyle.THICK);
						style6.setBorderBottom(BorderStyle.THICK);
						style6.setFont(font);// set it to bold
						row.setRowStyle(style6);
						

					} else {
						List<String> list = new ArrayList<>();
						int cellnum = 0;
						Object[] objArr = data.get(key);
						for (Object obj1 : objArr) {
							Cell cell = (Cell) row.createCell(cellnum++);
							
							cell.getRow().setHeight((short) -1);
							
							if (obj1 == null || obj1.toString().isEmpty()||obj1== " " ||obj1.equals("")) {

								//cell.setCellValue("");
								list.add("");
								sheet.setColumnWidth(cellnum-1, 0);
								sheet.setColumnWidth(cellnum, 0);
								
							
							}
							else if (obj1 instanceof String) {

								//cell.setCellValue((String) obj1);
								list.add((String) obj1);
								sheet.autoSizeColumn(cellnum-1);
								
							}

							else if (obj1 instanceof Date) {
								//cell.setCellValue((Date) obj1);
								list.add((String) obj1);
								sheet.autoSizeColumn(cellnum-1);
							}

							else if (obj1 instanceof Boolean) {
								//cell.setCellValue((Boolean) obj1);
								list.add((String) obj1);
								sheet.autoSizeColumn(cellnum-1);
							} else if (obj1 instanceof Double) {
								//cell.setCellValue((Double) obj1);
								list.add((String) obj1);
								sheet.autoSizeColumn(cellnum-1);
							} else if (obj1 instanceof Integer) {
								//cell.setCellValue((Integer) obj1);
								list.add((String) obj1);
								sheet.autoSizeColumn(cellnum-1);
							}

					}
						String[] stringArray = list.toArray(new String[0]);
						writer.writeNext(stringArray);
						}
						}
				System.out
				.println("RequestDetailSheet.csv written Successfully");
			     writer.flush();
			     writer.close();
			 
			 }
		
			 catch(IOException e)
			 {
			      e.printStackTrace();
			 
			 }
		}
			 catch(IOException e)
			 {
			      e.printStackTrace();
			 
			 }
			
	
	
	response.setHeader("Access-Control-Expose-Headers",
			"Content-Disposition");
	response.setStatus(HttpServletResponse.SC_OK);

	response.setHeader("Content-Disposition", "attachment; filename="
			+ alphanumericReqId + "_RequestExport.csv");
	response.setCharacterEncoding("UTF-8");
	response.setContentType("application/vnd.ms-excel");
	FileInputStream fileIn;
	try {
		fileIn = new FileInputStream(file);

		IOUtils.copy(fileIn, response.getOutputStream());
		fileIn.close();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}	
	
	@GET
	@Produces("application/vnd.ms-excel")
	@RequestMapping(value = "/exportxls", method = RequestMethod.GET, produces = "application/vnd.ms-excel")
	public void downloadExcelFilexls(HttpServletResponse response,
			@RequestParam String alphanumericReqId,
			@RequestParam Double requestVersion, String search)
			throws IOException {
		//boolean flag = false;
		RequestDetailsEntity entity = new RequestDetailsEntity();
		Gson gson = new Gson();

		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String jsonArray = "";

		RequestDetailsEntity ExportList = new RequestDetailsEntity();
		DeviceInterfaceEntity ExportList2 = new DeviceInterfaceEntity();
		InternetInfoEntity ExportList3 = new InternetInfoEntity();
		//RouterVfEntity ExportList4 = new RouterVfEntity();
		//WebServiceEntity ExportList5 = new WebServiceEntity();
		
		

		DcmConfigService dcmConfigService = new DcmConfigService();
		RequestDetailsExport.loadProperties();
		String path = RequestDetailsExport.TSA_PROPERTIES
				.getProperty("exportDownloadPath")
				+ "\\"
				+ alphanumericReqId
				+ "_RequestExport.xlsx";
		File file = new File(path);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			ExportList = requestDetailsExportRepo
					.findByAlphanumericReqIdAndRequestVersion(
							alphanumericReqId, requestVersion);
			ExportList2 = deviceInterfaceRepo.findByRequestInfoId(ExportList
					.getRequestinfoid());
			ExportList3 = internetInfoRepo.findByRequestInfoId(ExportList
					.getRequestinfoid());
			//ExportList4 = routerVfRepo.findByRequestInfoId(ExportList
				//	.getRequest_info_id());
			//ExportList5 = webServiceRepo.findByRequestInfoId(ExportList
					//.getRequest_info_id());
			// Blank workbook
			XSSFWorkbook workbook = new XSSFWorkbook();

			// Create a blank sheet
			XSSFSheet sheet = workbook.createSheet("Request_Data");

		
			// This data needs to be written (Object[])
			Map<String, Object[]> data = new TreeMap<String, Object[]>();

			data.put("1", new Object[] { "SR#","Request Number",
					"Hostname","Customer Name*",
					"Site ID*",
					"Region*",
					"Vendor*",
					"Device Type*",
					//"Device Name", 
					"Model*", 
					"OS*",
					"OS Version*",
					"Management IP*",
					"Service*",
					"Network Type*",
					"To Be Scheduled", 
					"Template ID",
					
					
					"Banner",
					//"Enable Password", 
//					"VPN",
					//"End Date Of Processing", 
					
					//"Request Elapsed Time", 
					 
					 "WAN Interface Name", 
					 "WAN Description", 
					 "WAN IP Address", 
					 "WAN Subnet Mask", 
//					 "WAN Speed",
						"WAN Encapsulation", 
						"WAN Bandwidth",
					 
					
						"LAN Interface Name", 
						"LAN IP Address", 
						"LAN Subnet Mask",
						"LAN Description", 
						
						
						"Loopback Interface Name",
						"Loopback IP Address", 
						"Loopback Subnet Mask",
						
						
						"Routing Protocol",
						"BGP As Number", 
						"BGP Neighbor1 IP Address","BGP Neighbor1 Remote AS", "BGP Neighbor2 IP Address",
						 "BGP Neighbor2 Remote AS","BGP Network IP", 
						"BGP Network IP Mask", 
						
						
						"VRF Name",
					 
					"SNMP HostAddress", "SNMP String",
					
					
				
				

 
					//"Temp Elapsed Time",
					
					//"Zipcode",
					//"Managed", "Downtime Required", "Last Upgraded On",

					

					

					//"VRF RD-ASN/IP Address", "RouterVrfVpnDGateway",
					//"FastEthernetIp",

				//	"Start_test", "Generate_config", "Deliever_config",
				//	"Health_checkup", "Network_test", "Application_test",
				//	"Customer_report", "Filename", "LatencyResultRes",
				//	"Alphanumeric_Req_Id", "Version", "TextFound_DeliveryTest",
				//	"ErrorStatus_DeliveryTest", "ErrorDescription_DeliveryTest"

			});
			String Scheduled = null;
			String AsNumber= null;
			if(ExportList.getRequestType_Flag().equals("S"))
			{
				Scheduled = ExportList.getScheduledTime();
			}
			
			String bgp=ExportList3.getRoutingProtocol();
			
			if(bgp!=null)
			{
				if(ExportList3.getRoutingProtocol().equals("BGP"))
				{
				AsNumber = ExportList3.getAsNumber();
				
				}
			}
			
			if(ExportList.getLanDescription().equals("\n"))
			{
				String lanDesToTrim = ExportList.getLanDescription();
				ExportList.setLanDescription(lanDesToTrim.trim());
			}
		/*	if(ExportList3.getRoutingProtocol().equals("BGP"))
			{
			AsNumber = ExportList3.getAsNumber();
			
			}*/
			
			data.put(
					"2",
					new Object[] { "1",
							ExportList.getAlphanumericReqId(),
							ExportList.getHostname(),
							ExportList.getCustomer(),
							ExportList.getSiteid(),
							ExportList.getRegion(),
							ExportList.getVendor(), 
							ExportList.getDevice_type(),
							//ExportList.getDevice_name(),
							 ExportList.getModel(),
							ExportList.getOs(),
							ExportList.getOs_version(),
							ExportList.getManagementIp(),
							 ExportList.getService(),
							 "Legacy",
							 (Object)Scheduled,
							ExportList.getTemplateIdUsed(),
							 
							ExportList.getBanner(),
							//ExportList.getEnable_password(),
//							 ExportList.getVpn(),
							 //ExportList.getZipcode(), ExportList.getManaged(),
								//ExportList.getDowntimeRequired(),
								//ExportList.getLastUpgradedOn(),
						 //////////////////////////
						 
						ExportList2.getName(),
						ExportList2.getDescription(), 
						ExportList2.getIp(),
						ExportList2.getMask(), 
//						ExportList2.getSpeed(),
						ExportList2.getEncapsulation(),
						ExportList2.getBandwidth(),
						 ///////////////////
						ExportList.getLanInterface(),
						ExportList.getLanIp(),
						ExportList.getLanMaskAddress(),
						ExportList.getLanDescription(),
						/////////////
						
						ExportList.getLoopBackType(),
						ExportList.getLoopbackIPaddress(),
						ExportList.getLoopbackSubnetMask(),
						
						
						
						
						/////////////
						ExportList3.getRoutingProtocol(),
						(Object)AsNumber,
						//ExportList3.getAsNumber(),
						ExportList3.getNeighbor1(),
						ExportList3.getNeighbor1RemoteAS(),
						ExportList3.getNeighbor2(),
						ExportList3.getNeighbor2RemoteAS(),
						ExportList3.getNetworkIp(),
						
						ExportList3.getNetworkIpSubnetMask(),
						
						////////////////////
						ExportList.getVrf_name(),
							//ExportList.getEnd_date_of_processing(),
							//ExportList.getRequestVersion(),
							//ExportList.getRequest_parent_version(),
							//ExportList.getRequest_creator_name(),
							//ExportList.getRequest_elapsed_time(),
							ExportList.getSnmpHostAddress(),
							ExportList.getSnmpString(),
							
							
							//ExportList.getCertificationSelectionBit(),
							//ExportList.getRequestType_Flag(),
							//ExportList.getScheduledTime(),
							
							//ExportList.getReadFE(), ExportList.getReadSE(),
							//ExportList.getRequestOwner(),
							//ExportList.getTemp_elapsed_time(),
							//ExportList.getTemp_processing_time(),
							//ExportList.getOwnerRequest(),
							

						

							

						

					});

			// Iterate over data and write to sheet
			java.util.Set<String> keyset = data.keySet();
			int rownum = 0;
			for (String key : keyset) {

			
				Row row = sheet.createRow(rownum++);

				int num = 1;
				if (num == Integer.parseInt(key)) {

					XSSFFont font = workbook.createFont();// Create font
					font.setBold(true);// Make font bold
					// style6.setFont(font);// set it to bold

				} else {

					CellStyle style1 = workbook.createCellStyle();// Create
																	// style
					style1.setBottomBorderColor(IndexedColors.BLACK.getIndex());
					style1.setLeftBorderColor(IndexedColors.BLACK.getIndex());
					style1.setRightBorderColor(IndexedColors.BLACK.getIndex());
					style1.setTopBorderColor(IndexedColors.BLACK.getIndex());
					style1.setBorderLeft(BorderStyle.DOUBLE);
					style1.setBorderRight(BorderStyle.DOUBLE);
					style1.setBorderTop(BorderStyle.DOUBLE);
					style1.setBorderBottom(BorderStyle.DOUBLE);

				}

			
				
				Object[] objArr = data.get(key);
				Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
				int cellnum = 0;
				for (Object obj1 : objArr) {
					String nullString = null;
					Cell cell = (Cell) row.createCell(cellnum++);
					XSSFFont font = workbook.createFont();// Create font
					font.setBold(true);// Make font bold

					if (obj1 == "SR#" 
							|| obj1 == "Request Number"
							|| obj1 == "Hostname"
							|| obj1 == "Customer Name*"
							|| obj1 == "Site ID*"
							|| obj1 == "Region*"
							|| obj1 == "Vendor*"
							|| obj1 == "Device Type*" 
							//|| obj1 == "Device Name"
					        || obj1 == "Model*" 
					        || obj1 == "OS*"
					        || obj1 == "OS Version*"
					        || obj1 == "Management IP*"
					        || obj1 == "Service*" 
					        || obj1 == "Network Type*" 
					        || obj1 == "To Be Scheduled"
					        || obj1 == "Template ID"  
					        || obj1 == "Banner" 
						    || obj1 == "Enable Password"
//						    || obj1 == "VPN"
						
						
						||obj1 == "WAN Interface Name" || obj1 == "WAN Description" || obj1 == "WAN IP Address"
						|| obj1 == "WAN Subnet Mask" 
						//|| obj1 == "WAN Speed"
						|| obj1 == "WAN Encapsulation" || obj1 == "WAN Bandwidth"||
						
						
							obj1 == "LAN Interface Name" || obj1 =="LAN IP Address"
								|| obj1 == "LAN Subnet Mask"|| obj1 == 
								"LAN Description"||
								
								
								obj1 == "VRF Name" ||
						
									
									
										
					obj1 == "BGP Network IP" || obj1 == "BGP As Number"
										|| obj1 == "BGP Neighbor1 IP Address" || obj1 == "BGP Neighbor2 IP Address"
										|| obj1 == "BGP Neighbor1 Remote AS"
										|| obj1 == "BGP Neighbor2 Remote AS"
										|| obj1 == "BGP Network IP Mask"
										|| obj1 == "Routing Protocol"||
										
										
												obj1 == "Loopback Interface Name"
												|| obj1 == "Loopback IP Address"
												|| obj1 == "Loopback Subnet Mask"||
												
												
														obj1 == "SNMP HostAddress"
														|| obj1 == "SNMP String"	)
														
														
							
							//|| obj1 == "Zipcode"
							//|| obj1 == "Managed" || obj1 == "Downtime Required"
							//|| obj1 == "Last Upgraded On") 
						{
						CellStyle style6 = workbook.createCellStyle();
						style6.setFillForegroundColor(IndexedColors.GREY_25_PERCENT
								.getIndex());
						style6.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						style6.setBottomBorderColor(IndexedColors.BLACK
								.getIndex());
						style6.setLeftBorderColor(IndexedColors.BLACK
								.getIndex());
						style6.setRightBorderColor(IndexedColors.BLACK
								.getIndex());
						style6.setTopBorderColor(IndexedColors.BLACK.getIndex());
						style6.setBorderLeft(BorderStyle.THICK);
						style6.setBorderRight(BorderStyle.THICK);
						style6.setBorderTop(BorderStyle.THICK);
						style6.setBorderBottom(BorderStyle.THICK);
						style6.setFont(font);// set it to bold
						cell.setCellStyle(style6);

					}

				
					
					else if (rownum == 2) {
						CellStyle style9 = workbook.createCellStyle();
						style9.setBottomBorderColor(IndexedColors.BLACK
								.getIndex());
						style9.setLeftBorderColor(IndexedColors.BLACK
								.getIndex());
						style9.setRightBorderColor(IndexedColors.BLACK
								.getIndex());
						style9.setTopBorderColor(IndexedColors.BLACK.getIndex());
						style9.setBorderLeft(BorderStyle.THIN);
						style9.setBorderRight(BorderStyle.THIN);
						style9.setBorderTop(BorderStyle.THIN);
						style9.setBorderBottom(BorderStyle.THIN);
						cell.setCellStyle(style9);

					}
					
					cell.getRow().setHeight((short) -1);
					// cell.setCellStyle(style);

					if (obj1 == null || obj1.toString().isEmpty()||obj1== " " ||obj1.equals("")) {

						cell.setCellValue("");
						sheet.setColumnWidth(cellnum-1, 0);
						sheet.setColumnWidth(cellnum, 0);
						//cell.setCellStyle(style8);
					
					}
					else if (obj1 instanceof String) {

						cell.setCellValue((String) obj1);
						sheet.autoSizeColumn(cellnum-1);
						
					}

					else if (obj1 instanceof Date) {
						cell.setCellValue((Date) obj1);
						sheet.autoSizeColumn(cellnum-1);
					}

					else if (obj1 instanceof Boolean) {
						cell.setCellValue((Boolean) obj1);
						sheet.autoSizeColumn(cellnum-1);
					} else if (obj1 instanceof Double) {
						cell.setCellValue((Double) obj1);
						sheet.autoSizeColumn(cellnum-1);
					} else if (obj1 instanceof Integer) {
						cell.setCellValue((Integer) obj1);
						sheet.autoSizeColumn(cellnum-1);
					}

				}

				try {

					// Write the workbook in file system
					if (!file.exists()) {
						file.createNewFile();
					}
					FileOutputStream out = new FileOutputStream(file);
					// FileOutputStream out = new FileOutputStream(new File(
					// "D:/RequestExport/" + alphanumericReqId
					// + "_RequestExport.csv"));

					workbook.write(out);
					out.close();
					System.out
							.println("RequestDetailSheet.xlsx written Successfully");
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		
		}
		catch (Exception e) {
			System.out.println(e);
		}

		// File file = new File(file);

		response.setHeader("Access-Control-Expose-Headers",
				"Content-Disposition");
		response.setStatus(HttpServletResponse.SC_OK);

		response.setHeader("Content-Disposition", "attachment; filename="
				+ alphanumericReqId + "_RequestExport.xlsx");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/vnd.ms-excel");
		FileInputStream fileIn;
		try {
			fileIn = new FileInputStream(file);

			IOUtils.copy(fileIn, response.getOutputStream());
			fileIn.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	
	
	public static boolean loadProperties() throws IOException {
		InputStream tsaPropFile = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream(TSA_PROPERTIES_FILE);

		try {
			TSA_PROPERTIES.load(tsaPropFile);
		} catch (IOException exc) {
			exc.printStackTrace();
			return false;
		}
		return false;
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

}

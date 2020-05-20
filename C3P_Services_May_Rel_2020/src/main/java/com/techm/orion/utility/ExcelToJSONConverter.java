package com.techm.orion.utility;

import java.awt.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.net.util.SubnetUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;






import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.techm.orion.pojo.ExcelToJSONConfigPojo;
import com.techm.orion.service.DcmConfigService;
public class ExcelToJSONConverter {

    //private static final String FILE_NAME = "/resources/standard_variable_in _ipdp.xlsx";

	ClassLoader classLoader =  Thread.currentThread().getContextClassLoader();
	InputStream ExcelFileToRead = null;
	// File file = new File();
	ExcelToJSONConfigPojo pojo = new ExcelToJSONConfigPojo();
	ArrayList<ExcelToJSONConfigPojo> productsList;
	DcmConfigService dcmConfigService = new DcmConfigService();
	public JSONObject mainObject=new JSONObject();
	public JSONObject deviceinfoObj=new JSONObject();
	public JSONObject errorObj=new JSONObject();
	public JSONObject lanInterfaceObject=new JSONObject();
	
	public JSONObject interfaceandroutingObj=new JSONObject();
	public JSONObject vpnObj=new JSONObject();
	public JSONObject loopbackInt=new JSONObject();
	public JSONObject customerInfoObj=new JSONObject();
	public JSONObject wanInterfaceObj=new JSONObject();
	public JSONObject routingObj=new JSONObject();
	@SuppressWarnings("unchecked")
	public JSONObject convert(MultipartFile file) {
		Sheet sheetOfParams = null;
		String result = null;
		try {
			 //FileInputStream excelFile = new FileInputStream(new File(FILE_NAME));
			// ExcelFileToRead = classLoader.getResourceAsStream("Standard IPDP.xlsx");
			 //String work_book_name=getSheetFor
			Workbook  wb = new XSSFWorkbook(file.getInputStream());
			 //Workbook  wb = new XSSFWorkbook(ExcelFileToRead);
			System.out.println("workbook: " + wb);
			
			/* String sheetName = "Global Significant Info";

			 for (int i = wb.getNumberOfSheets() - 1; i >= 0; i--) {
		            XSSFSheet tmpSheet = (XSSFSheet) wb.getSheetAt(i);
		            if (tmpSheet.getSheetName().equals(sheetName)) {
		            	wb.removeSheetAt(i);
		            	sheetOfParams=tmpSheet;
		            }
					//System.out.println("workbook: " + wb);

		        }*/
			Sheet sheet = wb.getSheetAt(2);
			System.out.println("worksheet: " + sheet);
			Row row;
			Iterator<Row> iterator = sheet.iterator();
			productsList = new ArrayList<ExcelToJSONConfigPojo>();
			while (iterator.hasNext()) {
				pojo = new ExcelToJSONConfigPojo();
				Row nextRow = iterator.next();
				if(nextRow.getRowNum()==9 || nextRow.getRowNum()==10||nextRow.getRowNum()==11||nextRow.getRowNum()==12)
				{
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					Cell cell = cellIterator.next();
					Iterator cells = nextRow.cellIterator();
					cell = (Cell) cells.next();

					if (cell.getCellTypeEnum() == CellType.STRING) {
	                    System.out.print(cell.getStringCellValue() + "--");
	                } else if (cell.getCellTypeEnum() == CellType.NUMERIC) {
	                    System.out.print(cell.getNumericCellValue() + "--");
	                } else {
						// U Can Handel Boolean, Formula, Errors
					}
					pojo = new ExcelToJSONConfigPojo();
					pojo.setVariableName(new DataFormatter()
					.formatCellValue(nextRow.getCell(1)));
					pojo.setVariable(new DataFormatter().formatCellValue(nextRow
					.getCell(1)));
					pojo.setVariableValue(new DataFormatter()
					.formatCellValue(nextRow.getCell(2)));
					productsList.add(pojo);
					
				}
				if(nextRow.getRowNum()>=22){
					  
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				Cell cell = cellIterator.next();
				Iterator cells = nextRow.cellIterator();
				cell = (Cell) cells.next();

				if (cell.getCellTypeEnum() == CellType.STRING) {
                    System.out.print(cell.getStringCellValue() + "--");
                } else if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                    System.out.print(cell.getNumericCellValue() + "--");
                } else {
					// U Can Handel Boolean, Formula, Errors
				}
				pojo = new ExcelToJSONConfigPojo();
				pojo.setVariableName(new DataFormatter()
						.formatCellValue(nextRow.getCell(1)));
				pojo.setVariable(new DataFormatter().formatCellValue(nextRow
						.getCell(3)));
				pojo.setVariableValue(new DataFormatter()
						.formatCellValue(nextRow.getCell(2)));
				productsList.add(pojo);
				}
				
			}
			
			
			Sheet sheet0 = wb.getSheetAt(0);
			Iterator<Row> iterator1 = sheet0.iterator();
			while (iterator1.hasNext()) {
				pojo = new ExcelToJSONConfigPojo();
				Row nextRow = iterator1.next();
				if(nextRow.getRowNum()==6 || nextRow.getRowNum()==5 || nextRow.getRowNum()==7){
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					Cell cell = cellIterator.next();
					Iterator cells = nextRow.cellIterator();
					cell = (Cell) cells.next();

					if (cell.getCellTypeEnum() == CellType.STRING) {
	                    System.out.print(cell.getStringCellValue() + "--");
	                } else if (cell.getCellTypeEnum() == CellType.NUMERIC) {
	                    System.out.print(cell.getNumericCellValue() + "--");
	                } else {
						// U Can Handel Boolean, Formula, Errors
					}

					pojo.setVariableName(new DataFormatter()
							.formatCellValue(nextRow.getCell(1)));
					pojo.setVariable(new DataFormatter().formatCellValue(nextRow
							.getCell(3)));
					pojo.setVariableValue(new DataFormatter()
							.formatCellValue(nextRow.getCell(2)));
					productsList.add(pojo);
				}
			}
			
			
			// Convert List to JSON
			ObjectMapper mapper = new ObjectMapper();
			result = mapper.writeValueAsString(productsList);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		ArrayList<ExcelToJSONConfigPojo> finalList;
		JsonParser parser = new JsonParser(); 
		
		


		mainObject.put("orderID", "");
		try {
			boolean hasError=false;
			JsonArray jsonArray = (JsonArray) parser.parse(result);
			JSONArray errorArray=new JSONArray();
			finalList=new ArrayList<ExcelToJSONConfigPojo>();
			JSONObject flags=new JSONObject();
			String IPAMcustomerName=null,IPAMsiteID=null,IPAMipAdd=null,IPAMmask=null;
			ExcelToJSONConfigPojo substringpojo;
			boolean interfaceRoutinginfoRoutingFlag=false,interfaceRoutinginfoWanIntFlag=false,interfaceRoutinginfoVpnFlag=false,interfaceRoutinginfoEnablePasswordFlag=false,interfaceRoutinginfoBannerFlag=false,interfaceRoutinginfoLoopbackInterfaceFlag=false,interfaceRoutinginfoSnmpFlag=false;
			for(int i=0; i<jsonArray.size(); i++)
			{
				JsonObject jsonObject=(JsonObject) jsonArray.get(i);
				Gson g = new Gson();
				ExcelToJSONConfigPojo pojoObj = g.fromJson(jsonObject, ExcelToJSONConfigPojo.class);

				if(pojoObj.getVariableName().equalsIgnoreCase("Router Type:"))
				{
					String vendor=getVendorName(pojoObj.getVariableValue());
					substringpojo=new ExcelToJSONConfigPojo();
					substringpojo.setVariableName("vendor");
					substringpojo.setVariableValue(vendor.toUpperCase());
					deviceinfoObj.put("vendor", substringpojo.getVariableValue());
					finalList.add(substringpojo);
					
					String model=getModel(pojoObj.getVariableValue());
					if(Integer.parseInt(model) != 7200 && Integer.parseInt(model) != 1921 && Integer.parseInt(model) != 2901 && Integer.parseInt(model) != 3660)
					{
						hasError=true;
						JSONObject er=new JSONObject();
						er.put("model", model);
						errorArray.put(er);
						errorObj.put("errorField", errorArray);

					}
					else
					{
					substringpojo=new ExcelToJSONConfigPojo();
					substringpojo.setVariableName("model");
					substringpojo.setVariableValue(model.toUpperCase());
					deviceinfoObj.put("model", substringpojo.getVariableValue());
					lanInterfaceObject.put("lanInterfaceType","GigabitEthernet2/0");
					lanInterfaceObject.put("lanInterfaceDescription","");
					finalList.add(substringpojo);
					}
					if(vendor.equalsIgnoreCase("cisco"))
					{
						String os="IOS";
						substringpojo=new ExcelToJSONConfigPojo();
						substringpojo.setVariableName("os");
						substringpojo.setVariableValue("IOS");
						deviceinfoObj.put("os", os);
					}
					
					deviceinfoObj.put("deviceType", "Router");

				}
				else if(pojoObj.getVariableName().equalsIgnoreCase("Access Type:"))
				{
					if(pojoObj.getVariableValue().equalsIgnoreCase("TDM"))
					{
						pojoObj.setVariableName("WAN Int");
						pojoObj.setVariableValue("Serial1/0");
						wanInterfaceObj.put("wanInt", pojoObj.getVariableValue());
						finalList.add(pojoObj);
					}
					else
					{
						hasError=true;
						JSONObject er=new JSONObject();
						er.put("Access Type", pojoObj.getVariableValue());
						errorArray.put(er);
						errorObj.put("errorField", errorArray);
					}
					
				}
				else if(pojoObj.getVariableName().equalsIgnoreCase("Device Name"))
				{
					deviceinfoObj.put("hostname", pojoObj.getVariableValue());
					pojoObj.setVariableName("hostname");
					finalList.add(pojoObj);
				}
				else if(pojoObj.getVariableName().equalsIgnoreCase("VPN Name"))
				{
					vpnObj.put("vrfName", pojoObj.getVariableValue());
					pojoObj.setVariableName("vrfName");
					finalList.add(pojoObj);
				}
				else if(pojoObj.getVariableName().equalsIgnoreCase("Loopback 0"))
				{
					loopbackInt.put("loopInterfaceName", pojo.getVariableName());
					finalList.add(pojoObj);
				}
				else if(pojoObj.getVariableName().equalsIgnoreCase("Site Name"))
				{
					customerInfoObj.put("siteid", pojoObj.getVariableValue());
					IPAMsiteID=pojoObj.getVariableValue();
					pojoObj.setVariableName("siteid");
					finalList.add(pojoObj);
				}
				else if(pojoObj.getVariableName().equalsIgnoreCase("CE-PE/CPE-PE Addr"))
				{
					wanInterfaceObj.put("wanInterfaceIP", pojoObj.getVariableValue());
					IPAMipAdd=pojoObj.getVariableValue();
					finalList.add(pojoObj);
				}
				else if(pojoObj.getVariableName().equalsIgnoreCase("CE-PE/CPE-PE Subnet Mask"))
				{
					wanInterfaceObj.put("wanInterfaceSubnetMask", pojoObj.getVariableValue());
					IPAMmask=pojoObj.getVariableValue();
					finalList.add(pojoObj);
				}
				
				else if(pojoObj.getVariableName().equalsIgnoreCase("Bandwidth (bps)"))
				{
					String val=pojoObj.getVariableValue();
					String finalval=convertToKbps(val);
					pojoObj.setVariableValue(finalval);
					pojoObj.setVariableName("c3p_interface.name");
					wanInterfaceObj.put("bandwidth", finalval);
					finalList.add(pojoObj);
				}
				else if(pojoObj.getVariableName().equalsIgnoreCase("IP Address"))
				{
					lanInterfaceObject.put("lanInterfaceIP",pojoObj.getVariableValue());

					//routingObj.put("networkIP", pojoObj.getVariableValue());
					//pojoObj.setVariableName("internetLcVrf.networkIp");
					finalList.add(pojoObj);
				}
				else if(pojoObj.getVariableName().equalsIgnoreCase("Subnet Mask"))
				{
					lanInterfaceObject.put("lanInterfaceSubnetMask",pojoObj.getVariableValue());

					//routingObj.put("networkMask", pojoObj.getVariableValue());
					//pojoObj.setVariableName("internetLcVrf.networkIp_subnetMask");
					finalList.add(pojoObj);
				}
				else if(pojoObj.getVariableName().equalsIgnoreCase("Customer name:"))
				{
					customerInfoObj.put("customer", pojoObj.getVariableValue());
					IPAMcustomerName=pojoObj.getVariableValue();
					pojoObj.setVariableName("customer");
					finalList.add(pojoObj);
				}
				/*else if(pojoObj.getVariableName().equalsIgnoreCase("IP Address"))
				{
					lanInterfaceObject.put("lanInterfaceIP",pojoObj.getVariableValue());
					pojoObj.setVariableName("lanInterfaceIPAddress");
					finalList.add(pojoObj);
				}
				else if(pojoObj.getVariableName().equalsIgnoreCase("Subnet Mask"))
				{
					lanInterfaceObject.put("lanInterfaceSubnetMask",pojoObj.getVariableValue());
					pojoObj.setVariableName("lanInterfaceSubnetMask");
					finalList.add(pojoObj);
				}*/
				


				System.out.println("JSON");
			}
			//Logic to get networkIP and subnet mask.............................................................................................
			
			int networkSize=getNetworkSize(lanInterfaceObject.get("lanInterfaceSubnetMask").toString());
			String cidrString="";
			if(networkSize == 1)
			{
				cidrString="/32";
			}
			else if(networkSize == 2)
			{
				cidrString="/31";
			}
			else if(networkSize == 4)
			{
				cidrString="/30";
			}
			else if(networkSize == 8)
			{
				cidrString="/29";
			}
			String ipString=lanInterfaceObject.get("lanInterfaceIP").toString()+cidrString;
			SubnetUtils utils = new SubnetUtils(ipString);
		    utils.setInclusiveHostCount(true);
		    String[] allIps = utils.getInfo().getAllAddresses();
		    routingObj.put("networkIP", allIps[0]);
		    routingObj.put("networkMask", lanInterfaceObject.get("lanInterfaceSubnetMask").toString());
			
			/*ObjectMapper mapper = new ObjectMapper();
			result = mapper.writeValueAsString(finalList);*/
			System.out.println("JSON");
			if(routingObj.isEmpty())
			{
				flags.put("routingObj", false);
			}
			else
			{
				flags.put("routingObj", true);

			}
			if(wanInterfaceObj.isEmpty())
			{
				flags.put("wanInterfaceObj", false);

			}
			else
			{
				flags.put("wanInterfaceObj", true);

			}
			if(vpnObj.isEmpty())
			{
				flags.put("vpnObj", false);
			}
			else
			{
				flags.put("vpnObj", true);

			}
			if(loopbackInt.isEmpty())
			{
				flags.put("loopbackInt", false);
			}
			else
			{
				flags.put("loopbackInt", true);

			}
			if(lanInterfaceObject.isEmpty())
			{
				flags.put("lanInterface", false);

			}
			else
			{
				flags.put("lanInterface", true);

			}
			flags.put("enablePassword", false);
			flags.put("banner", false);
			flags.put("snmp", false);

			interfaceandroutingObj.put("routing", routingObj);
			interfaceandroutingObj.put("wanInterface", wanInterfaceObj);
			interfaceandroutingObj.put("vpn", vpnObj);
			interfaceandroutingObj.put("enablePassword", "");
			interfaceandroutingObj.put("banner", "");
			interfaceandroutingObj.put("loopbackInterface", loopbackInt);
			interfaceandroutingObj.put("InformationStatus", flags);
			interfaceandroutingObj.put("lanInterface", lanInterfaceObject);

			
			JSONObject snmp=new JSONObject();
			interfaceandroutingObj.put("snmp", snmp);

			if(hasError)
			{
				mainObject.put("result", "failure");
				mainObject.put("error", errorObj);
			}
			else
			{
			mainObject.put("result", "success");
			mainObject.put("interfaceRoutingInfo", interfaceandroutingObj);
			mainObject.put("customerInfo", customerInfoObj);
			mainObject.put("deviceInfo", deviceinfoObj);
			}
			//Add record in IPAM DB 
			
			dcmConfigService.addEIPAMRecord(IPAMcustomerName, IPAMsiteID,
					IPAMipAdd, IPAMmask,"MIS","CANADA");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mainObject;
	}
	private String convertToKbps(String val)
	{
		String res=null;
		Double num=Double.valueOf(val);
		double resNum=num*0.001;
		DecimalFormat format = new DecimalFormat("0.#");		
		res=String.valueOf(format.format(resNum));
		return res;
	}
	private String getVendorName(String variable)
	{
		String result=null;
		result=variable.substring(0, 5);
		return result;
	}
	private String getModel(String variable)
	{
		String result=null;
		result=variable.substring(5, 9);
		return result;
	}
	private int getNetworkSize(String subnet)
	{
		int resultSize=0;
		int[] ip = new int[4];
		String[] parts = subnet.split("\\.");

		for (int i = 0; i < 4; i++) {
		    ip[i] = Integer.parseInt(parts[i]);
		}
		resultSize=ip[3];
		resultSize=256-resultSize;
		return resultSize;
	}
}

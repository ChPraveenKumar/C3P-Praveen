package com.techm.orion.rest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.techm.orion.dao.RequestInfoDao;

@Controller
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/comparison")
public class ConfigComparisonService implements Observer {

	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();
	
	@POST
	@RequestMapping(value = "/configcomparison", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response configComparison(@RequestBody String configRequest) {
		
		JSONObject obj = new JSONObject();
		try {
			RequestInfoDao dao=new RequestInfoDao();
			ConfigComparisonService.loadProperties();
			String pythonScriptFolder = ConfigComparisonService.TSA_PROPERTIES
					.getProperty("pythonScriptPath");
			String standardConfigPath=ConfigComparisonService.TSA_PROPERTIES
					.getProperty("standardConfigPath");
			String currentConfigPath=ConfigComparisonService.TSA_PROPERTIES
					.getProperty("responseDownloadPath");
			String comparisonFolder=ConfigComparisonService.TSA_PROPERTIES
					.getProperty("comparisonHtmls");
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);
			String RequestId = json.get("testLabel").toString();
			String requestId = json.get("requestID").toString();

			String outputFile=null;
			//USCI7200IO12.4_NA_Test_1.0_Snippet_Router Uptime
			//get snippet from DB

			//RequestId="USCI7200IO12.4_NA_Test_1.0_Snippet_Router Uptime";
			String[] keys = RequestId.split("_");
			
			String data_type=keys[4];
			String label=keys[5];
			String test_name=keys[0]+"_"+keys[1]+"_"+keys[2];
			//String requestId="SR-DC394C0";
			String snippet=dao.getSnippet(data_type, label, test_name);
			//write it to temp file StandardConfiguration.txt
			String filepath1 = standardConfigPath+"\\StandardConfiguration.txt";
			FileWriter fw1 = null;
			BufferedWriter bw1 = null;
			File file1 = new File(filepath1);
			if (!file1.exists()) {
				file1.createNewFile();

				fw1 = new FileWriter(file1, true);
				bw1 = new BufferedWriter(fw1);
				bw1.append(snippet);
				bw1.close();
			} else {
				file1.delete();
				file1.createNewFile();

				fw1 = new FileWriter(file1, true);
				bw1 = new BufferedWriter(fw1);
				bw1.append(snippet);
				bw1.close();
			}
			
			
			 //Create temp current version file
            String filepath2 = currentConfigPath+"\\"+requestId+"V1.0"+"_CurrentVersionConfig.txt";
            String tempFilePath = currentConfigPath+"\\"+requestId+"V1.0"+"_Temp"+"_CurrentVersionConfig.txt";
            File tempFile = new File(tempFilePath);
            FileWriter fw = new FileWriter(tempFilePath);
            if (!tempFile.exists()) {
           	 tempFile.createNewFile();
            }
            else
            {
           	 tempFile.delete();
           	 tempFile.createNewFile();
            }
            String[] arrOfStr = snippet.split("\n");
            int size=arrOfStr.length;
            String firstLine=arrOfStr[0];
          
            File originalFile = new File(filepath2);
            BufferedReader b = new BufferedReader(new FileReader(originalFile));
            String readLine = "";
            Boolean flag=false;
            while ((readLine = b.readLine()) != null) {
               if(readLine.equalsIgnoreCase(firstLine))
               {
               	for(int i=0;i <size; i++)
               	{
               		fw.write(readLine+"\n");
               		readLine=b.readLine();
               	}
               	fw.close();
               	flag=true;
               	break;
               }
              if(flag==true)
            	  break;
            }
            //Find lines in the current config file
            
            
            //copy them to temp file
            
            
            //if destination html file exists delete it and create new
            File destFile = new File(comparisonFolder+ "\\"+requestId+"V1.0"+"_ComparisonSnippet"+ ".html" );
            if (!destFile.exists()) {
               }
               else
               {
            	   destFile.delete();
               }
            
            String[] cmd = {
                         "python",
                         pythonScriptFolder+"\\filediff.py",
                         "-m",
                         standardConfigPath+"\\StandardConfiguration.txt",
                         currentConfigPath+"\\"+requestId+"V1.0"+"_Temp"+"_CurrentVersionConfig.txt",
                         comparisonFolder
                                       + "\\"+requestId+"V1.0"+"_ComparisonSnippet"
                                       + ".html" };
			
			
			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String ret = in.readLine();

			BufferedReader bre = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));
			String line;
			
			if (bre.readLine() == null) {
				// update the success scenario in DB
				outputFile=comparisonFolder
				+ "\\"+requestId+"V1.0"+"_ComparisonSnippet"
				+ ".html";
				StringBuilder bldr = new StringBuilder();
				String str;

				BufferedReader in1 = new BufferedReader(new FileReader(outputFile));
				while((str = in1.readLine())!=null)
				      bldr.append(str);

				in1.close();

				String content = bldr.toString();
				 //String escapedHTML = StringEscapeUtils.escapeHtml4(content);
				Gson gson = new GsonBuilder().disableHtmlEscaping().create();
				//Gson gson = new GsonBuilder().disableHtmlEscaping().create();
				
				String jsonArray = gson.toJson(content);                  
                content=getFormattedDoc(content, "snippet",currentConfigPath+"\\"+RequestId+"_PreviousConfig.txt", currentConfigPath+"\\"+RequestId+"_CurrentVersionConfig.txt");

				obj.put(new String("output"), content);
			} else {
				// System.out.println("Error in comparison for "+files.get(0).substring(0,
				// 4)+"_"+files.get(1).substring(0, 4));
				System.out.println("Error");
				while ((line = bre.readLine()) != null) {
					System.out.println(line);
				}
				obj.put(new String("output"), "Error in processing the files");
			}
			bre.close();
			
			
		} catch (Exception e) {
			System.out.println(e);
		}

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

	
	
	
	
	@POST
	@RequestMapping(value = "/keywordSearch", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response keywordSearch(@RequestBody String configRequest) {
		
		JSONObject obj = new JSONObject();
		try {
			ConfigComparisonService.loadProperties();
			String pythonScriptFolder = ConfigComparisonService.TSA_PROPERTIES
					.getProperty("pythonScriptPath");
	
			String currentConfigPath=ConfigComparisonService.TSA_PROPERTIES
					.getProperty("responseDownloadPath");
			String comparisonFolder=ConfigComparisonService.TSA_PROPERTIES
					.getProperty("comparisonHtmls");
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);
			String RequestId = json.get("requestID").toString();
			String keyword=json.get("keyword").toString();
			String outputFile=null;
			String[] cmd = {
					"python",
					pythonScriptFolder+"\\keywordSearchFiltered.py",
					"-m",
					currentConfigPath+"\\"+RequestId+"V1.0"+"_CurrentVersionConfig.txt",
					comparisonFolder
							+ "\\"+RequestId+"_KeywordNetworkAudit"
							+ ".html",
							keyword};
			
			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String ret = in.readLine();

			BufferedReader bre = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));
			String line;
			
			if (bre.readLine() == null) {
				// update the success scenario in DB
				outputFile=comparisonFolder
				+ "\\"+RequestId+"_KeywordFinalReport"
				+ ".html";
				StringBuilder bldr = new StringBuilder();
				String str;

				BufferedReader in1 = new BufferedReader(new FileReader(outputFile));
				while((str = in1.readLine())!=null)
				      bldr.append(str);

				in1.close();

				String content = bldr.toString();
				
				Gson gson = new GsonBuilder().disableHtmlEscaping().create();
				  
				String jsonArray = gson.toJson(content);                  
				obj.put(new String("output"), jsonArray);
				
			} else {
				// System.out.println("Error in comparison for "+files.get(0).substring(0,
				// 4)+"_"+files.get(1).substring(0, 4));
				System.out.println("Error");
				while ((line = bre.readLine()) != null) {
					System.out.println(line);
				}
				obj.put(new String("output"), "Error in processing the files");
			}
			bre.close();
			
			
		} catch (Exception e) {
			System.out.println(e);
		}

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
	
	@POST
	@RequestMapping(value = "/keywordSearchFinalReport", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response keywordSearchFiltered(@RequestBody String configRequest) {
		
		JSONObject obj = new JSONObject();
		try {
			ConfigComparisonService.loadProperties();
			String pythonScriptFolder = ConfigComparisonService.TSA_PROPERTIES
					.getProperty("pythonScriptPath");
	
			String currentConfigPath=ConfigComparisonService.TSA_PROPERTIES
					.getProperty("responseDownloadPath");
			String comparisonFolder=ConfigComparisonService.TSA_PROPERTIES
					.getProperty("comparisonHtmls");
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);
			String RequestId = json.get("requestID").toString();
			String keyword=json.get("keyword").toString();
			String outputFile=null;
			String[] cmd = {
					"python",
					pythonScriptFolder+"\\keywordSearchFiltered.py",
					"-m",
					currentConfigPath+"\\"+RequestId+"V1.0"+"_CurrentVersionConfig.txt",
					comparisonFolder
							+ "\\"+RequestId+"_KeywordFinalReport"
							+ ".html",
							keyword};
			
			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String ret = in.readLine();

			BufferedReader bre = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));
			String line;
			
			if (bre.readLine() == null) {
				// update the success scenario in DB
				outputFile=comparisonFolder
				+ "\\"+RequestId+"_KeywordFinalReport"
				+ ".html";
				
				StringBuilder bldr = new StringBuilder();
				String str;
				BufferedReader in1 = new BufferedReader(new FileReader(outputFile));
				while((str = in1.readLine())!=null)
				      bldr.append(str);

				in1.close();

				String content = bldr.toString();
				
				 //String escapedHTML = StringEscapeUtils.escapeHtml4(content);
				Gson gson = new GsonBuilder().disableHtmlEscaping().create();
				//Gson gson = new GsonBuilder().disableHtmlEscaping().create();
				
				String jsonArray = gson.toJson(content);     
				
				obj.put(new String("output"), content);

			} else {
				// System.out.println("Error in comparison for "+files.get(0).substring(0,
				// 4)+"_"+files.get(1).substring(0, 4));
				System.out.println("Error");
				while ((line = bre.readLine()) != null) {
					System.out.println(line);
				}
				obj.put(new String("output"), "Error in processing the files");
			}
			bre.close();
			
			
		} catch (Exception e) {
			System.out.println(e);
		}

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
	 @POST
     @RequestMapping(value = "/configcomparisonbackupmilestone", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
     @ResponseBody
     public Response configComparisonbackupanddilevary(@RequestBody String configRequest) {
            
            JSONObject obj = new JSONObject();
            try {
                   RequestInfoDao dao=new RequestInfoDao();
                   ConfigComparisonService.loadProperties();
                   String pythonScriptFolder = ConfigComparisonService.TSA_PROPERTIES
                                .getProperty("pythonScriptPath");
                   String standardConfigPath=ConfigComparisonService.TSA_PROPERTIES
                                .getProperty("standardConfigPath");
                   String currentConfigPath=ConfigComparisonService.TSA_PROPERTIES
                                .getProperty("responseDownloadPath");
                   String comparisonFolder=ConfigComparisonService.TSA_PROPERTIES
                                .getProperty("comparisonHtmls");
                   JSONParser parser = new JSONParser();
                   JSONObject json = (JSONObject) parser.parse(configRequest);
                   String RequestId = json.get("requestID").toString();
                   String outputFile=null;
                   
                   //copy them to temp file
                   String[] cmd = {
                                "python",
                                pythonScriptFolder+"\\filediff.py",
                                "-m",
                                currentConfigPath+"\\"+RequestId+"_PreviousConfig.txt",
                                currentConfigPath+"\\"+RequestId+"_CurrentVersionConfig.txt",
                                comparisonFolder
                                              + "\\"+RequestId+"_Comparison"
                                              + ".html" };
                   
                   Process p = Runtime.getRuntime().exec(cmd);
                   BufferedReader in = new BufferedReader(new InputStreamReader(
                                p.getInputStream()));
                   String ret = in.readLine();

                   BufferedReader bre = new BufferedReader(new InputStreamReader(
                                p.getErrorStream()));
                   String line;
                   
                   if (bre.readLine() == null) {
                         // update the success scenario in DB
                         outputFile=comparisonFolder
                         + "\\"+RequestId+"_Comparison"
                         + ".html";
                         StringBuilder bldr = new StringBuilder();
                         String str;

                         BufferedReader in1 = new BufferedReader(new FileReader(outputFile));
                         while((str = in1.readLine())!=null)
                               bldr.append(str);

                         in1.close();

                         String content = bldr.toString();
                         //String escapedHTML = StringEscapeUtils.escapeHtml4(content);
                         Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                         //Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                         
                         String jsonArray = gson.toJson(content);                  
                         content=getFormattedDoc(content, "sr_backup",currentConfigPath+"\\"+RequestId+"_PreviousConfig.txt", currentConfigPath+"\\"+RequestId+"_CurrentVersionConfig.txt");
                       
                         obj.put(new String("output"), jsonArray);
                   } else {
                         // System.out.println("Error in comparison for "+files.get(0).substring(0,
                         // 4)+"_"+files.get(1).substring(0, 4));
                         System.out.println("Error");
                         while ((line = bre.readLine()) != null) {
                                System.out.println(line);
                         }
                         obj.put(new String("output"), "Error in processing the files");
                   }
                   bre.close();
                   
                   
            } catch (Exception e) {
                   System.out.println(e);
            }

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
	 @POST
     @RequestMapping(value = "/configcomparisonbackupandrestore", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
     @ResponseBody
     public Response configComparisonbackupandrestore(@RequestBody String configRequest) {
            
            JSONObject obj = new JSONObject();
            try {
                   RequestInfoDao dao=new RequestInfoDao();
                   ConfigComparisonService.loadProperties();
                   String pythonScriptFolder = ConfigComparisonService.TSA_PROPERTIES
                                .getProperty("pythonScriptPath");
                   String standardConfigPath=ConfigComparisonService.TSA_PROPERTIES
                                .getProperty("standardConfigPath");
                   String currentConfigPath=ConfigComparisonService.TSA_PROPERTIES
                                .getProperty("responseDownloadPath");
                   String comparisonFolder=ConfigComparisonService.TSA_PROPERTIES
                                .getProperty("comparisonHtmls");
                   JSONParser parser = new JSONParser();
                   JSONObject json = (JSONObject) parser.parse(configRequest);
                   String RequestId1 = json.get("requestID1").toString();
                   String RequestId2 = json.get("requestID2").toString();
                   String outputFile=null;
                
                   //copy them to temp file
                   String[] cmd = {
                                "python",
                                pythonScriptFolder+"\\filediff.py",
                                "-m",
                                currentConfigPath+"\\"+RequestId1+"V1.0"+"_PreviousConfig.txt",
                                currentConfigPath+"\\"+RequestId2+"V1.0"+"_PreviousConfig.txt",
                                comparisonFolder
                                              + "\\"+RequestId1+"_Comparison"
                                              + ".html" };
                   
                   Process p = Runtime.getRuntime().exec(cmd);
                   BufferedReader in = new BufferedReader(new InputStreamReader(
                                p.getInputStream()));
                   String ret = in.readLine();

                   BufferedReader bre = new BufferedReader(new InputStreamReader(
                                p.getErrorStream()));
                   String line;
                   
                   if (bre.readLine() == null) {
                         // update the success scenario in DB
                         outputFile=comparisonFolder
                         + "\\"+RequestId1+"_Comparison"
                         + ".html";
                         StringBuilder bldr = new StringBuilder();
                         String str;

                         BufferedReader in1 = new BufferedReader(new FileReader(outputFile));
                         while((str = in1.readLine())!=null)
                               bldr.append(str);

                         in1.close();

                         String content = bldr.toString();
                         //String escapedHTML = StringEscapeUtils.escapeHtml4(content);
                         Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                         //Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                         
                         String jsonArray = gson.toJson(content);                  

                         obj.put(new String("output"), content);
                   } else {
                         // System.out.println("Error in comparison for "+files.get(0).substring(0,
                         // 4)+"_"+files.get(1).substring(0, 4));
                         System.out.println("Error");
                         while ((line = bre.readLine()) != null) {
                                System.out.println(line);
                         }
                         obj.put(new String("output"), "Error in processing the files");
                   }
                   bre.close();
                   
                   
            } catch (Exception e) {
                   System.out.println(e);
            }

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
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
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
	public String getFormattedDoc(String content, String type, String previousConfigFilename, String currentConfigFilename)
	{
		String result=null;
		String contentCopy=content;
	
        	  switch(type)
        	  {
        	  case "sr_backup":
        		 /* if (element.text().contains("_PreviousConfig.txt")) {
                      System.out.println("Previous");
                      element=element.text("Previous");
                      ths.add(element);
                      body.insertChildren(0, ths);
                      document.insertChildren(0, ths);
                      
                  }
                  else if(element.text().contains("_CurrentVersionConfig.txt"))
                  {
                      System.out.println("Current");
                      element=element.text("Current");
                      ths.add(element);
                      body.insertChildren(0, ths);
                      document.insertChildren(0, ths);

                  }
        		  contentCopy=null;
        		  contentCopy=document.data();
        		  result=contentCopy;*/
        		  contentCopy=content.replaceAll(">", "> ");
        		  contentCopy=content.replaceAll("<", " <");
        		  contentCopy=content.replace(previousConfigFilename, "Previous Configuration");
        		  contentCopy=contentCopy.replace(currentConfigFilename, "Current Configuration");
        		  result=contentCopy;
        		  break;
        	  case "snippet":
        		  contentCopy=content.replaceAll(">", "> ");
        		  contentCopy=content.replaceAll("<", " <");
        		  contentCopy=content.replace(previousConfigFilename, "Standard Configuration");
        		  contentCopy=contentCopy.replace(currentConfigFilename, "Current Configuration");
        		  result=contentCopy;
        		  break;
        	  default:
        		  	result=content;
        	  }
             
        
		return result;
	}
}

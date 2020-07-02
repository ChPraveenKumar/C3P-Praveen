package com.techm.orion.rest;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.dao.TemplateManagementDao;
import com.techm.orion.pojo.Global;
import com.techm.orion.pojo.RequestInfoSO;
import com.techm.orion.pojo.TemplateBasicConfigurationPojo;
import com.techm.orion.pojo.UserPojo;
import com.techm.orion.pojo.UserValidationResultDetailPojo;

@Controller
@RequestMapping("/LoginService")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class LoginService implements Observer {

	RequestInfoDao requestInfoDao = new RequestInfoDao();
	TemplateManagementDao templateManagementDao = new TemplateManagementDao();

	@SuppressWarnings({ "unchecked", "null" })
	@POST
	@RequestMapping(value = "/login", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response login(@RequestBody String searchParameters) {

		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String jsonArray = "";
		UserValidationResultDetailPojo ip =null;
		int loginStatus ;
		String username = null, password = null;
		int notificationCount = 0, ajaxCallInterval = 0;
		InputStream inputStream;

		Properties prop = new Properties();
		String propFileName = "TSA.properties";

		int totalCount=0;
		try {
			Gson gson = new Gson();
			UserPojo dto = gson.fromJson(searchParameters, UserPojo.class);
			username = dto.getUsername();
			password = dto.getPassword();
	
			List<TemplateBasicConfigurationPojo> list = null;
			String jsonList, templateNameList;
			List<TemplateBasicConfigurationPojo> templateNames = new ArrayList<TemplateBasicConfigurationPojo>();
		
			if (username != null && !username.isEmpty()) {
				try {
					// quick fix for json not getting serialized
				/*	loginStatus = requestInfoDao.checkedUserStatus(username, password);
					
					if(loginStatus==0)
					{*/
					ip = requestInfoDao.checkUsersDB(username, password);
					
					if (ip.getMessage().equalsIgnoreCase("Success")) {
						Global.loggedInUser = username;
						
							
							// logic to get ajax call duration set statically in
							// properties
							inputStream = getClass().getClassLoader()
									.getResourceAsStream(propFileName);
							if (inputStream != null) {
								prop.load(inputStream);
							} else {
								throw new FileNotFoundException(
										"property file '"
												+ propFileName
												+ "' not found in the classpath");
							}
							ajaxCallInterval = Integer
									.parseInt(prop
											.getProperty("AjaxCallIntervalForPullNotification"));
						
					
						
						if(Global.loggedInUser.equalsIgnoreCase("feuser"))
						{
						//To get notifications assigned to FE for FE login;
						RequestInfoDao requestInfoDao=new RequestInfoDao();
						List<RequestInfoSO>feRequestList=new ArrayList<RequestInfoSO>();
						feRequestList=requestInfoDao.getFEAssignedRequestList();
						int numberOfNotificationsForFE=0;
						List<RequestInfoSO>feRequestListNum=new ArrayList<RequestInfoSO>();
						for(int i=0;i <feRequestList.size();i++)
						{
							if(feRequestList.get(i).getRead()==0)
							{
								feRequestListNum.add(feRequestList.get(i));
							}
						}
						numberOfNotificationsForFE=feRequestListNum.size();
						
						totalCount=notificationCount+numberOfNotificationsForFE;
						jsonArray = new Gson().toJson(ip);
						jsonList = new Gson().toJson(list);
						String jsonFeRequestList=new Gson().toJson(feRequestList);
						templateNameList = new Gson().toJson(templateNames);
						obj.put(new String("TemplateList"), templateNameList);
						obj.put(new String("AjaxCallForNotificationIntervalInSec"),
								ajaxCallInterval);
						obj.put(new String("FENotificationCount"), numberOfNotificationsForFE);
						obj.put(new String("NotificationCount"), totalCount);

						obj.put(new String("SENotificationCount"), 0);
						obj.put(new String("SERequestDetailedList"), "");

						obj.put(new String("TemplateNotificationCount"), 0);
						obj.put(new String("TemplateDetailedList"), "");
						
						obj.put(new String("FERequestDetailedList"), jsonFeRequestList);
						obj.put(new String("Message"), ip.getMessage());
						obj.put(new String("Result"), ip.isResult());
						obj.put(new String("PrivilegeLeve"), ip.getPrivilegeLevel());

						}
						else if(Global.loggedInUser.equalsIgnoreCase("seuser"))
						{
							RequestInfoDao requestInfoDao=new RequestInfoDao();
							List<RequestInfoSO>seRequestList=new ArrayList<RequestInfoSO>();
							seRequestList=requestInfoDao.getSEAssignedRequestList();
							int numberOfNotificationsForSE=0;
							List<RequestInfoSO>seRequestListNum=new ArrayList<RequestInfoSO>();
							for(int i=0;i <seRequestList.size();i++)
							{
								if(seRequestList.get(i).getRead()==0)
								{
									seRequestListNum.add(seRequestList.get(i));
								}
							}
							numberOfNotificationsForSE=seRequestListNum.size();
							
							totalCount=notificationCount+numberOfNotificationsForSE;
							jsonArray = new Gson().toJson(ip);
							jsonList = new Gson().toJson(list);
							String jsonFeRequestList=new Gson().toJson(seRequestList);
							
							templateNameList = new Gson().toJson(templateNames);
							obj.put(new String("TemplateList"), templateNameList);
							obj.put(new String("AjaxCallForNotificationIntervalInSec"),
									ajaxCallInterval);
							obj.put(new String("NotificationCount"), totalCount);

							obj.put(new String("FENotificationCount"), 0);
							obj.put(new String("FERequestDetailedList"), "");
							
							obj.put(new String("TemplateNotificationCount"), 0);
							obj.put(new String("TemplateDetailedList"), "");

							obj.put(new String("SENotificationCount"), numberOfNotificationsForSE);
							obj.put(new String("SERequestDetailedList"), jsonFeRequestList);
							obj.put(new String("Message"), ip.getMessage());
							obj.put(new String("Result"), ip.isResult());
							obj.put(new String("PrivilegeLeve"), ip.getPrivilegeLevel());

						}
						else
						{
							notificationCount = templateManagementDao
									.getNumberOfTemplatesForApprovalForLoggedInUser(username);

							list = templateManagementDao
									.getTemplatesForApprovalForLoggedInUser(Global.loggedInUser);
							for (int i = 0; i < list.size(); i++) {
									TemplateBasicConfigurationPojo pojo = new TemplateBasicConfigurationPojo();
									pojo.setTemplateId(list.get(i).getTemplateId());
									pojo.setVersion(list.get(i).getVersion());
									pojo.setRead(list.get(i).getRead());
									pojo.setStatus(list.get(i).getStatus());
									pojo.setEditable(list.get(i).isEditable());

									templateNames.add(pojo);
								
							}
							
							jsonArray = new Gson().toJson(ip);
							jsonList = new Gson().toJson(list);
							String jsonFeRequestList="undefined";
							totalCount=notificationCount;

							templateNameList = new Gson().toJson(templateNames);
							obj.put(new String("TemplateDetailedList"), jsonList);
							obj.put(new String("TemplateList"), templateNameList);
							obj.put(new String("AjaxCallForNotificationIntervalInSec"),
									ajaxCallInterval);
							obj.put(new String("TemplateNotificationCount"), notificationCount);
							obj.put(new String("FENotificationCount"), 0);
							obj.put(new String("NotificationCount"), totalCount);

							obj.put(new String("FERequestDetailedList"), "");
							

							obj.put(new String("SENotificationCount"), 0);
							obj.put(new String("SERequestDetailedList"), "");

							obj.put(new String("Message"), ip.getMessage());
							obj.put(new String("Result"), ip.isResult());
							obj.put(new String("PrivilegeLeve"), ip.getPrivilegeLevel());

						}
					}
					else
					{
						jsonArray = "undefined";
						jsonList ="undefined";
						String jsonFeRequestList="undefined";
						templateNameList = "";
						obj.put(new String("TemplateDetailedList"), jsonList);
						obj.put(new String("TemplateList"), templateNameList);
						obj.put(new String("AjaxCallForNotificationIntervalInSec"),
								ajaxCallInterval);
						obj.put(new String("TemplateNotificationCount"), 0);
						obj.put(new String("FENotificationCount"), 0);
						obj.put(new String("NotificationCount"), 0);

						obj.put(new String("FERequestDetailedList"), jsonFeRequestList);
						obj.put(new String("Message"), ip.getMessage());
						obj.put(new String("Result"), ip.isResult());
						obj.put(new String("PrivilegeLeve"), ip.getPrivilegeLevel());
					}
					
			/*		}
					else
					{
						//ip.setUserStatusMessage("User already logged in");
						obj.put(new String("Message"), "User already logged in on other device");
						obj.put(new String("Result"), "false");
					}*/
				} catch (Exception e) {
					System.out.println(e);
				}
			} else {
				try {
					/*
					 * detailsList = requestInfoDao.getAllResquestsFromDB();
					 * jsonArray = new Gson().toJson(detailsList); obj.put(new
					 * String("output"), jsonArray);
					 */
				} catch (Exception e) {
					System.out.print(e);
				}
			}
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

}

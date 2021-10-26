package com.techm.c3p.core.rest;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.c3p.core.pojo.UserManagementResulltDetailPojo;
import com.techm.c3p.core.pojo.UserPojo;
import com.techm.c3p.core.service.UserManagementInterface;
import com.techm.c3p.core.utility.C3PCoreAppLabels;

@Controller
@RequestMapping("/LoginService")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class LoginService {
	private static final Logger logger = LogManager.getLogger(LoginService.class);
	
	@Autowired
	private UserManagementInterface userCreateInterface;

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings({ "unchecked", "null" })
	@POST
	@RequestMapping(value = "/login", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response login(@RequestBody String searchParameters) {

		JSONObject obj = new JSONObject();
		String username = null, password = null, userRole = null;
		InputStream inputStream;
		Properties prop = new Properties();
		String propFileName = "C3PCoreApp.properties";
		try {
			Gson gson = new Gson();
			final String secretKey = C3PCoreAppLabels.SECRET_KEY.getValue();
			UserPojo dto = gson.fromJson(searchParameters, UserPojo.class);
			username = dto.getUsername();
			password = dto.getPassword();

			if (username != null && !username.isEmpty()) {
				try {
					UserManagementResulltDetailPojo userDetails = userCreateInterface.checkUserNamePassword(username,
							password, secretKey);

					if (userDetails != null && "Success".equalsIgnoreCase(userDetails.getMessage())) {
						userRole = userDetails.getRole();

						// logic to get ajax call duration set statically in
						// properties
						inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
						if (inputStream != null) {
							prop.load(inputStream);
						} else {
							throw new FileNotFoundException(
									"property file '" + propFileName + "' not found in the classpath");
						}

						if (userRole.equalsIgnoreCase("feuser")) {
							// To get notifications assigned to FE for FE login;
							obj.put(new String("Message"), userDetails.getMessage());
							obj.put(new String("Result"), userDetails.isResult());
							obj.put("userDetails", userDetails);
						} else if (userRole.equalsIgnoreCase("seuser")) {
							obj.put(new String("Message"), userDetails.getMessage());
							obj.put(new String("Result"), userDetails.isResult());
							obj.put("userDetails", userDetails);

						} else {
							obj.put(new String("Message"), userDetails.getMessage());
							obj.put(new String("Result"), userDetails.isResult());
							obj.put("userDetails", userDetails);

						}
					} else {
						obj.put(new String("Message"), userDetails.getMessage());
						obj.put(new String("Result"), userDetails.isResult());
						obj.put("userDetails", userDetails);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();
	}
}
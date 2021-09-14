package com.techm.orion.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.POST;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.techm.orion.connection.ConnectionFactory;
import com.techm.orion.connection.DBUtil;
import com.techm.orion.entitybeans.RequestInfoEntity;
import com.techm.orion.pojo.CreateScheduleReqPojo;
import com.techm.orion.repositories.RequestInfoDetailsRepositories;

@Controller
@RequestMapping("/CreateScheduleReqDBService")
public class CreateScheduleReqDBService {
	private static final Logger logger = LogManager.getLogger(CreateScheduleReqDBService.class);

	@Autowired
	RequestInfoDetailsRepositories requestnfoDao;

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/selectRequestInDB", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject selectRequestInDB(@RequestBody String request) {
		String scheduleDateTime = null;

		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);

			// Require requestId and version from camunda
			String businessKey = json.get("requestId").toString();
			String version = json.get("version").toString();
			
			
			String query = "select r_scheduled_time from c3p_t_request_info where r_request_type_flag = 'S' and r_request_version = ? and r_alphanumeric_req_id like ?";
					

			//String query = "select r_scheduled_time from c3p_t_request_info where r_request_type_flag = 'S' and r_request_version = ? and r_alphanumeric_req_id like ?";

			ResultSet rs = null;
			CreateScheduleReqPojo scheduleReqObj = null;

			try(Connection connection = ConnectionFactory.getConnection();
					PreparedStatement statement = connection.prepareStatement(query) ) {
				statement.setString(1, version);
				statement.setString(2, businessKey);
				rs = statement.executeQuery();

				while (rs.next()) {
					scheduleReqObj = new CreateScheduleReqPojo();
					scheduleReqObj.setScheduledTime(rs.getString("r_scheduled_time"));
				}

				if (scheduleReqObj == null || scheduleReqObj.getScheduledTime() == null) {
					scheduleReqObj = new CreateScheduleReqPojo();
					Double finalVersion = Double.parseDouble(version);
					RequestInfoEntity requestEntity = requestnfoDao
							.findByAlphanumericReqIdAndRequestVersionAndRequestTypeFlag(businessKey, finalVersion, "S");
					scheduleReqObj.setScheduledTime(requestEntity.getSceheduledTime().toString());
				}

				scheduleDateTime = scheduleReqObj.getScheduledTime();
		
			} catch (SQLException exe) {
				exe.printStackTrace();
				logger.error("SQL Exception in selectRequestInDB method "+exe.getMessage());
			} finally {
				DBUtil.close(rs);
			}
		} catch (Exception ex) {
			logger.error(ex);
		}

		
		JSONObject obj = new JSONObject();
		obj.put(new String("output"), scheduleDateTime);
		return obj;

	}


	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/insertRequestInDB", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public void insertRequestInDB(@RequestBody String request) {

		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);

			// Require requestId and version from camunda
			String businessKey = json.get("requestId").toString();
			String version = json.get("version").toString();
			String processId = json.get("processId").toString();
			String user = json.get("user") !=null ? json.get("user").toString() :"";

			ResultSet rs = null;
			String insertQuery = "INSERT INTO camundahistory(history_processId,history_requestId,history_versionId,history_user) VALUES(?,?,?,?)";
			String updateQuery = "update camundahistory set history_processId = ?,history_user = ? where history_requestId = ? and history_versionId= ?";
			String countQuery = "select count(history_processId) count from camundahistory where history_versionId = ? and history_requestId like ?";

			try(Connection connection = ConnectionFactory.getConnection();
					PreparedStatement preparedStmt1 = connection.prepareStatement(insertQuery);
					PreparedStatement preparedStmt2 = connection.prepareStatement(updateQuery);) {
				CreateScheduleReqPojo scheduleReqObj = null;
				String dbProcessID = null;

				try(PreparedStatement countPs = connection.prepareStatement(countQuery)) {
					scheduleReqObj = new CreateScheduleReqPojo();
					countPs.setString(1, version);
					countPs.setString(2, businessKey);
					rs = countPs.executeQuery();
					while (rs.next()) {					
						scheduleReqObj.setHistory_processId(rs.getString("count"));
					}

					dbProcessID = scheduleReqObj.getHistory_processId();
				} catch (SQLException exe) {
					logger.error("SQL Exception in insertRequestInDB count method "+exe.getMessage());
				} finally {
					DBUtil.close(rs);
				}
				logger.info("dbProcessID - "+dbProcessID);
				if ("0".equals(dbProcessID)) {
					preparedStmt1.setString(1, processId);
					preparedStmt1.setString(2, businessKey);
					preparedStmt1.setString(3, version);
					preparedStmt1.setString(4, user);
					preparedStmt1.executeUpdate();
				} else {
					preparedStmt2.setString(1, processId);
					preparedStmt2.setString(2, user);
					preparedStmt2.setString(3, businessKey);
					preparedStmt2.setString(4, version);
					preparedStmt2.executeUpdate();
				}
			} catch (SQLException exe) {
				logger.error("SQL Exception in insertRequestInDB method "+exe.getMessage());
			}
		} catch (Exception ex) {
			logger.error(ex);
			ex.printStackTrace();
		}

	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/updateTaskIDInDB", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public void updateTaskIDInDB(@RequestBody String request) throws Exception {

		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);

			// Require taskId and processId from camunda
			String taskId = json.get("taskId").toString();
			String processId = json.get("processId").toString();

			String query = "update camundahistory set history_userTaskId = ? where history_processId = ?";
			
			try(Connection connection = ConnectionFactory.getConnection();
					PreparedStatement preparedStmt = connection.prepareStatement(query);) {
				preparedStmt.setString(1, taskId);
				preparedStmt.setString(2, processId);
				preparedStmt.executeUpdate();

			} catch (SQLException exe) {
				logger.error("SQL Exception in updateTaskIDInDB method "+exe.getMessage());
			}

		} catch (Exception ex) {
			logger.error(ex);
		}

	}
}

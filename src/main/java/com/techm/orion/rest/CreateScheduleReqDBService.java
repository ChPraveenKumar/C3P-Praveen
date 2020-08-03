package com.techm.orion.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

	private Connection connection;
	Statement statement;

	@Autowired
	RequestInfoDetailsRepositories requestnfoDao;

	@POST
	@RequestMapping(value = "/selectRequestInDB", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public String selectRequestInDB(@RequestBody String request) throws Exception {
		String scheduleDateTime = null;

		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);

			// Require requestId and version from camunda
			String businessKey = json.get("requestId").toString();
			String version = json.get("version").toString();

			connection = ConnectionFactory.getConnection();
			String query = "select ScheduledTime from requestinfoso where RequestType_Flag = 'S' and request_version = '"
					+ version + "' and alphanumeric_req_id like '" + businessKey + "'";

			ResultSet rs = null;

			CreateScheduleReqPojo scheduleReqObj = null;

			try {

				statement = connection.createStatement();
				rs = statement.executeQuery(query);

				while (rs.next()) {
					scheduleReqObj = new CreateScheduleReqPojo();
					scheduleReqObj.setScheduledTime(rs.getString("ScheduledTime"));
				}

				if (scheduleReqObj == null || scheduleReqObj.getScheduledTime() == null) {
					scheduleReqObj = new CreateScheduleReqPojo();
					Double finalVersion = Double.parseDouble(version);
					RequestInfoEntity requestEntity = requestnfoDao
							.findByAlphanumericReqIdAndRequestVersionAndRequestTypeFlag(businessKey, finalVersion, "S");
					scheduleReqObj.setScheduledTime(requestEntity.getSceheduledTime().toString());
				}

				scheduleDateTime = scheduleReqObj.getScheduledTime();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				DBUtil.close(rs);
				DBUtil.close(statement);
				DBUtil.close(connection);
			}
		} catch (Exception ex) {
			logger.error(ex);
		}

		return scheduleDateTime;

	}

	@POST
	@RequestMapping(value = "/insertRequestInDB", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public void insertRequestInDB(@RequestBody String request) throws Exception {

		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);

			// Require requestId and version from camunda
			String businessKey = json.get("requestId").toString();
			String version = json.get("version").toString();
			String processId = json.get("processId").toString();
			String user = json.get("user").toString();

			connection = ConnectionFactory.getConnection();
			String query = null;
			ResultSet rs = null;
			PreparedStatement preparedStmt = null;

			try {
				String query1 = "select count(history_processId) count from camundahistory where history_versionId = '"
						+ version + "' and history_requestId like '" + businessKey + "'";

				CreateScheduleReqPojo scheduleReqObj = null;
				String dbProcessID = null;

				try {
					scheduleReqObj = new CreateScheduleReqPojo();
					statement = connection.createStatement();
					rs = statement.executeQuery(query1);

					while (rs.next()) {					
						scheduleReqObj.setHistory_processId(rs.getString("count"));
					}

					dbProcessID = scheduleReqObj.getHistory_processId();
				} catch (SQLException e) {
					logger.error(e);
				}
				logger.info("dbProcessID - "+dbProcessID);
				if ("0".equals(dbProcessID)) {
					query = "INSERT INTO camundahistory(history_processId,history_requestId,history_versionId,history_user) VALUES(?,?,?,?)";

					preparedStmt = connection.prepareStatement(query);
					preparedStmt.setString(1, processId);
					preparedStmt.setString(2, businessKey);
					preparedStmt.setString(3, version);
					preparedStmt.setString(4, user);
					preparedStmt.executeUpdate();

				} else {
					query = "update camundahistory set history_processId = ?,history_user = ? where history_requestId = ? and history_versionId= ?";

					preparedStmt = connection.prepareStatement(query);
					preparedStmt.setString(1, processId);
					preparedStmt.setString(2, user);
					preparedStmt.setString(3, businessKey);
					preparedStmt.setString(4, version);
					preparedStmt.executeUpdate();

					/*
					 * query = "delete from camundahistory where history_processId = ?";
					 * preparedStmt = connection.prepareStatement(query); preparedStmt.setString(1,
					 * processId); preparedStmt.execute("SET FOREIGN_KEY_CHECKS=0");
					 * preparedStmt.executeUpdate();
					 */
				}
			} catch (SQLException e) {
				logger.error(e);
			} finally {
				DBUtil.close(rs);
				DBUtil.close(statement);
				DBUtil.close(connection);
			}
		} catch (Exception ex) {
			logger.error(ex);
			ex.printStackTrace();
		}

	}

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

			connection = ConnectionFactory.getConnection();
			String query = null;
			ResultSet rs = null;
			PreparedStatement preparedStmt = null;

			try {

				statement = connection.createStatement();

				query = "update camundahistory set history_userTaskId = ? where history_processId = ?";

				preparedStmt = connection.prepareStatement(query);
				preparedStmt.setString(1, taskId);
				preparedStmt.setString(2, processId);
				preparedStmt.executeUpdate();

			} catch (SQLException e) {
				logger.error(e);
			} finally {
				DBUtil.close(rs);
				DBUtil.close(statement);
				DBUtil.close(connection);
			}

		} catch (Exception ex) {
			logger.error(ex);
		}

	}
}

package com.techm.orion.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.techm.orion.connection.ConnectionFactory;
import com.techm.orion.connection.DBUtil;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.pojo.SchedulerListPojo;

public class RequestSchedulerDao {

	private Connection connection;
	Statement statement;

	public String updateScheduledRequest(CreateConfigRequestDCM configRequest) throws SQLException {
		String result = "";
		connection = ConnectionFactory.getConnection();
		ResultSet rs = null;
		PreparedStatement preparedStmt = null;
		String query = null;
		try {
			query = "INSERT INTO scheduledrequesthistory(RequestID,version,Status,Next_Execution_Time,Last_Execution_Time)"
					+ "VALUES(?,?,?,?,?)";

			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, configRequest.getRequestId());
			preparedStmt.setString(2, Double.toString(configRequest.getRequest_version()));
			preparedStmt.setString(3, "Scheduled");

			if (configRequest.getScheduledTime() != null && configRequest.getScheduledTime() != "") {

				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

				try {
					java.util.Date parsedDate = sdf.parse(configRequest.getScheduledTime());

					java.sql.Timestamp timestampTimeForScheduled = new java.sql.Timestamp(parsedDate.getTime());
					preparedStmt.setTimestamp(4, timestampTimeForScheduled);

					java.sql.Timestamp timestampTimeForAction = new java.sql.Timestamp(new Date().getTime());
					preparedStmt.setTimestamp(5, timestampTimeForAction);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					result = "Failure";
					e.printStackTrace();
				}
			}
			preparedStmt.executeUpdate();
			result = "Success";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			result = "Failure";
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(preparedStmt);
			DBUtil.close(connection);
		}

		return result;
	}

	public List<SchedulerListPojo> getScheduledHistoryForRequest(String requestId, String version) {
		List<SchedulerListPojo> schedulerListPojolist = new ArrayList<SchedulerListPojo>();
		SchedulerListPojo schedulerListPojo = null;

		connection = ConnectionFactory.getConnection();
		String query = null;

		if (!version.contains(".")) {
			version = version + ".0";
		}
		query = "Select * from scheduledrequesthistory  where RequestID = ?  ORDER BY id DESC";
		ResultSet rs = null;
		PreparedStatement pst = null;

		try {

			pst = connection.prepareStatement(query);
			pst.setString(1, requestId);
			// pst.setString(2, version);

			rs = pst.executeQuery();
			while (rs.next()) {
				schedulerListPojo = new SchedulerListPojo();
				schedulerListPojo.setRequestId(rs.getString("RequestID"));
				schedulerListPojo.setVersion(rs.getString("version"));
				schedulerListPojo.setStatus(rs.getString("Status"));
				Timestamp timeNext = rs.getTimestamp("Next_Execution_Time");
				if (timeNext != null) {
					schedulerListPojo.setNextExecutionTime((covnertTStoString(timeNext)));
				}
				Timestamp timeLast = rs.getTimestamp("Last_Execution_Time");
				if (timeLast != null) {
					schedulerListPojo.setLastExecutionTime((covnertTStoString(timeLast)));
				}
				schedulerListPojolist.add(schedulerListPojo);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return schedulerListPojolist;
	}

	public String updateRescheduledRequest(String requestId, String version, String scheduledTime)
			throws ParseException {
		java.sql.Timestamp timestampTimeForScheduled = null;
		connection = ConnectionFactory.getConnection();
		String query = null;
		ResultSet rs = null;
		String queryInsert = null;
		String result = "";
		try {

			if (scheduledTime != null && scheduledTime != "") {

				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

				java.util.Date parsedDate = sdf.parse(scheduledTime);

				timestampTimeForScheduled = new java.sql.Timestamp(parsedDate.getTime());
			}
			query = "update requestinfoso set ScheduledTime = ?,request_status = ? where alphanumeric_req_id = ? and request_version = ? ";

			PreparedStatement pst = null;
			pst = connection.prepareStatement(query);
			pst.setTimestamp(1, timestampTimeForScheduled);
			pst.setString(2, "Scheduled");
			pst.setString(3, requestId);
			pst.setString(4, version);
			pst.executeUpdate();

			queryInsert = "INSERT INTO scheduledrequesthistory(RequestID,version,Status,Next_Execution_Time,Last_Execution_Time)"
					+ "VALUES(?,?,?,?,?)";

			pst = connection.prepareStatement(queryInsert);
			pst.setString(1, requestId);
			pst.setString(2, version);
			pst.setString(3, "Re-Scheduled");
			pst.setTimestamp(4, timestampTimeForScheduled);
			java.sql.Timestamp timestampTimeForAction = new java.sql.Timestamp(new Date().getTime());
			pst.setTimestamp(5, timestampTimeForAction);

			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			result = "Failed";
			e.printStackTrace();
		} finally {
			result = "Success";
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return result;
	}

	public String cancelScheduledRequest(String requestId, String version) throws ParseException {
		java.sql.Timestamp timestampTimeForScheduled = null;
		connection = ConnectionFactory.getConnection();
		String query = null;
		ResultSet rs = null;
		String queryInsert = null;
		String result = "";
		try {

			timestampTimeForScheduled = new java.sql.Timestamp(new Date().getTime());

			query = "update requestinfoso set request_status = ? where alphanumeric_req_id = ? and request_version = ? ";

			PreparedStatement pst = null;
			pst = connection.prepareStatement(query);
			pst.setString(1, "Cancelled");
			pst.setString(2, requestId);
			pst.setString(3, version);
			pst.executeUpdate();

			queryInsert = "INSERT INTO scheduledrequesthistory(RequestID,version,Status,Last_Execution_Time)"
					+ "VALUES(?,?,?,?)";

			pst = connection.prepareStatement(queryInsert);
			pst.setString(1, requestId);
			pst.setString(2, version);
			pst.setString(3, "Cancelled");
			// pst.setTimestamp(4, timestampTimeForScheduled);
			java.sql.Timestamp timestampTimeForAction = new java.sql.Timestamp(new Date().getTime());
			pst.setTimestamp(4, timestampTimeForAction);

			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			result = "Failed";
			e.printStackTrace();
		} finally {
			result = "Success";
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return result;
	}

	public String RunScheduledRequestUpdate(String requestId, String version) throws ParseException {

		connection = ConnectionFactory.getConnection();
		String query = null;
		ResultSet rs = null;
		String result = "";
		try {
			query = "update requestinfoso set request_status = ? where alphanumeric_req_id = ? and request_version = ? ";

			PreparedStatement pst = null;
			pst = connection.prepareStatement(query);
			pst.setString(1, "In Progress");
			pst.setString(2, requestId);
			pst.setString(3, version);
			pst.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			result = "Failed";
			e.printStackTrace();
		} finally {
			result = "Success";
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return result;
	}

	/* to get the data for request that are not in the UI */
	public CreateConfigRequestDCM getDataFromRequestInfo(String requestId, String version) {

		CreateConfigRequestDCM createConfigRequest = null;

		connection = ConnectionFactory.getConnection();
		String query = null;

		ResultSet rs = null;
		PreparedStatement pst = null;

		try {
			query = "Select * from requestinfoso  where alphanumeric_req_id = ? and request_version = ?";
			pst = connection.prepareStatement(query);
			pst.setString(1, requestId);
			pst.setString(2, version);

			rs = pst.executeQuery();
			while (rs.next()) {
				createConfigRequest = new CreateConfigRequestDCM();
				createConfigRequest.setRequest_parent_version(rs.getDouble("request_parent_version"));

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return createConfigRequest;
	}

	public String abortScheduledRequest(String requestId, String version) throws ParseException {
		java.sql.Timestamp timestampTimeForScheduled = null;
		connection = ConnectionFactory.getConnection();
		String query = null;
		ResultSet rs = null;
		String queryInsert = null;
		String result = "";
		String rowId = "";
		try {

			query = "Select request_info_id from requestinfoso  where alphanumeric_req_id = ? and request_version = ?";
			PreparedStatement pst = null;
			pst = connection.prepareStatement(query);
			pst.setString(1, requestId);
			pst.setString(2, version);

			rs = pst.executeQuery();
			while (rs.next()) {

				rowId = rs.getString("request_info_id");

			}
			query = "delete from webserviceinfo where alphanumeric_req_id = ? and version = ? ";

			pst = connection.prepareStatement(query);
			pst.setString(1, requestId);
			pst.setString(2, version);
			pst.execute("SET FOREIGN_KEY_CHECKS=0");
			// pst.execute("SET SQL_SAFE_UPDATES = 0");

			pst.executeUpdate();

			query = "delete from internetlcvrfso where request_info_id = ? ";

			pst = connection.prepareStatement(query);
			pst.setString(1, rowId);

			pst.execute("SET FOREIGN_KEY_CHECKS=0");
			// pst.execute("SET SQL_SAFE_UPDATES = 0");

			pst.executeUpdate();

			query = "delete from misarpeso where request_info_id = ? ";

			pst = connection.prepareStatement(query);
			pst.setString(1, rowId);

			pst.execute("SET FOREIGN_KEY_CHECKS=0");
			// pst.execute("SET SQL_SAFE_UPDATES = 0");

			pst.executeUpdate();

			query = "delete from deviceinterfaceso where request_info_id = ? ";

			pst = connection.prepareStatement(query);
			pst.setString(1, rowId);

			pst.execute("SET FOREIGN_KEY_CHECKS=0");
			// pst.execute("SET SQL_SAFE_UPDATES = 0");

			pst.executeUpdate();

			query = "delete from bannerdatatable where request_info_id = ? ";

			pst = connection.prepareStatement(query);
			pst.setString(1, rowId);

			pst.execute("SET FOREIGN_KEY_CHECKS=0");
			// pst.execute("SET SQL_SAFE_UPDATES = 0");

			pst.executeUpdate();
			query = "delete from requestinfoso where alphanumeric_req_id = ? and request_version = ? ";

			pst = connection.prepareStatement(query);
			pst.setString(1, requestId);
			pst.setString(2, version);
			pst.execute("SET FOREIGN_KEY_CHECKS=0");
			// pst.execute("SET SQL_SAFE_UPDATES = 0");

			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			result = "Failed";
			e.printStackTrace();
		} finally {
			result = "Success";
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return result;
	}

	public String covnertTStoString(Timestamp indate) {
		String dateString = null;
		Date date = new Date();
		date.setTime(indate.getTime());
		dateString = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(date);
		return dateString;
	}

	public String getProcessIdFromCamundaHistory(String requestId, String version) {
		String processId = null;
		connection = ConnectionFactory.getConnection();
		String query = "Select history_processId from camundahistory where history_requestId=? and history_versionId=?";
		ResultSet rs = null;
		PreparedStatement pst = null;

		try {
			pst = connection.prepareStatement(query);
			pst.setString(1, requestId);
			pst.setString(2, version);
			rs = pst.executeQuery();
			while (rs.next()) {

				processId = rs.getString("history_processId");

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}

		return processId;
	}

	public void deleteProcessIdFromCamundaHistory(String processId) {
		connection = ConnectionFactory.getConnection();
		String query = "delete from camundahistory where history_processId = ?";
		ResultSet rs = null;
		PreparedStatement pst = null;

		try {
			pst = connection.prepareStatement(query);
			pst.setString(1, processId);
			pst.execute("SET FOREIGN_KEY_CHECKS=0");
			pst.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
	}

	/* method overloading for UIRevamp */

	public String updateScheduledRequest(RequestInfoPojo configRequest) throws SQLException {
		String result = "";
		connection = ConnectionFactory.getConnection();
		ResultSet rs = null;
		PreparedStatement preparedStmt = null;
		String query = null;
		try {
			query = "INSERT INTO scheduledrequesthistory(RequestID,version,Status,Next_Execution_Time,Last_Execution_Time)"
					+ "VALUES(?,?,?,?,?)";

			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, configRequest.getAlphanumericReqId());
			preparedStmt.setString(2, Double.toString(configRequest.getRequestVersion()));
			preparedStmt.setString(3, "Scheduled");

			if (configRequest.getSceheduledTime() != null && configRequest.getSceheduledTime() != "") {

				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

				try {
					java.util.Date parsedDate = sdf.parse(configRequest.getSceheduledTime());

					java.sql.Timestamp timestampTimeForScheduled = new java.sql.Timestamp(parsedDate.getTime());
					preparedStmt.setTimestamp(4, timestampTimeForScheduled);

					java.sql.Timestamp timestampTimeForAction = new java.sql.Timestamp(new Date().getTime());
					preparedStmt.setTimestamp(5, timestampTimeForAction);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					result = "Failure";
					e.printStackTrace();
				}
			}
			preparedStmt.executeUpdate();
			result = "Success";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			result = "Failure";
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(preparedStmt);
			DBUtil.close(connection);
		}

		return result;
	}

}
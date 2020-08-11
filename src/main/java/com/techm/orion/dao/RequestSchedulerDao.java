package com.techm.orion.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techm.orion.connection.ConnectionFactory;
import com.techm.orion.connection.DBUtil;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.pojo.SchedulerListPojo;

public class RequestSchedulerDao {

	private static final Logger logger = LogManager.getLogger(RequestSchedulerDao.class);

	public String updateScheduledRequest(CreateConfigRequestDCM configRequest) throws SQLException {
		String result = "";
		ResultSet rs = null;
		String query = "INSERT INTO scheduledrequesthistory(RequestID,version,Status,Next_Execution_Time,Last_Execution_Time)"
				+ "VALUES(?,?,?,?,?)";
		try(Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			
			preparedStmt.setString(1, configRequest.getRequestId());
			preparedStmt.setString(2, Double.toString(configRequest.getRequest_version()));
			preparedStmt.setString(3, "Scheduled");

			if (configRequest.getScheduledTime() != null && !configRequest.getScheduledTime().isEmpty()) {
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				try {
					Date parsedDate = sdf.parse(configRequest.getScheduledTime());
					Timestamp timestampTimeForScheduled = new Timestamp(parsedDate.getTime());
					preparedStmt.setTimestamp(4, timestampTimeForScheduled);

					Timestamp timestampTimeForAction = new Timestamp(new Date().getTime());
					preparedStmt.setTimestamp(5, timestampTimeForAction);

				} catch (ParseException e) {
					result = "Failure";
					e.printStackTrace();
				}
			}
			preparedStmt.executeUpdate();
			result = "Success";
		} catch (SQLException exe) {
			result = "Failure";
			logger.error("SQL Exception in updateScheduledRequest method "+exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}

		return result;
	}

	public List<SchedulerListPojo> getScheduledHistoryForRequest(String requestId, String version) {
		List<SchedulerListPojo> schedulerListPojolist = new ArrayList<SchedulerListPojo>();
		SchedulerListPojo schedulerListPojo = null;
		if (!version.contains(".")) {
			version = version + ".0";
		}
		String query = "Select * from scheduledrequesthistory  where RequestID = ?  ORDER BY id DESC";
		ResultSet rs = null;
		try(Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {

			pst.setString(1, requestId);

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

		} catch (SQLException exe) {
			logger.error("SQL Exception in getScheduledHistoryForRequest method "+exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return schedulerListPojolist;
	}

	public String updateRescheduledRequest(String requestId, String version, String scheduledTime)
			throws ParseException {
		Timestamp timestampTimeForScheduled = null;
		String query = "update requestinfoso set ScheduledTime = ?,request_status = ? where alphanumeric_req_id = ? and request_version = ? ";
		String queryInsert = "INSERT INTO scheduledrequesthistory(RequestID,version,Status,Next_Execution_Time,Last_Execution_Time)"
				+ "VALUES(?,?,?,?,?)";

		String result = "";
		try(Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);
				PreparedStatement pst1 = connection.prepareStatement(queryInsert);) {

			if (scheduledTime != null && !scheduledTime.isEmpty()) {
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				Date parsedDate = sdf.parse(scheduledTime);
				timestampTimeForScheduled = new Timestamp(parsedDate.getTime());
			}

			pst.setTimestamp(1, timestampTimeForScheduled);
			pst.setString(2, "Scheduled");
			pst.setString(3, requestId);
			pst.setString(4, version);
			pst.executeUpdate();

			pst1.setString(1, requestId);
			pst1.setString(2, version);
			pst1.setString(3, "Re-Scheduled");
			pst1.setTimestamp(4, timestampTimeForScheduled);
			Timestamp timestampTimeForAction = new Timestamp(new Date().getTime());
			pst1.setTimestamp(5, timestampTimeForAction);

			pst1.executeUpdate();
		} catch (SQLException exe) {
			logger.error("SQL Exception in updateRescheduledRequest method "+exe.getMessage());
		}
		return result;
	}

	public String cancelScheduledRequest(String requestId, String version) throws ParseException {
		String query = "update requestinfoso set request_status = ? where alphanumeric_req_id = ? and request_version = ? ";
		String queryInsert = "INSERT INTO scheduledrequesthistory(RequestID,version,Status,Last_Execution_Time)"
				+ "VALUES(?,?,?,?)";
		String result = "";
		try(Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);
				PreparedStatement pst1 = connection.prepareStatement(queryInsert);) {

			pst.setString(1, "Cancelled");
			pst.setString(2, requestId);
			pst.setString(3, version);
			pst.executeUpdate();

			pst1.setString(1, requestId);
			pst1.setString(2, version);
			pst1.setString(3, "Cancelled");
			Timestamp timestampTimeForAction = new Timestamp(new Date().getTime());
			pst1.setTimestamp(4, timestampTimeForAction);

			pst1.executeUpdate();
		} catch (SQLException exe) {
			logger.error("SQL Exception in cancelScheduledRequest method "+exe.getMessage());
		}
		return result;
	}

	public String runScheduledRequestUpdate(String requestId, String version) throws ParseException {
		String query = "update requestinfoso set request_status = ? where alphanumeric_req_id = ? and request_version = ? ";
		String result = "";
		try(Connection	connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {			
			pst.setString(1, "In Progress");
			pst.setString(2, requestId);
			pst.setString(3, version);
			pst.executeUpdate();
			result = "Success";
		} catch (SQLException exe) {
			result = "Failed";
			logger.error("SQL Exception in runScheduledRequestUpdate method "+exe.getMessage());
		}
		return result;
	}

	/* to get the data for request that are not in the UI */
	public CreateConfigRequestDCM getDataFromRequestInfo(String requestId, String version) {
		CreateConfigRequestDCM createConfigRequest = null;
		String query = "Select * from requestinfoso  where alphanumeric_req_id = ? and request_version = ?";
		ResultSet rs = null;

		try(Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			pst.setString(1, requestId);
			pst.setString(2, version);
			rs = pst.executeQuery();
			while (rs.next()) {
				createConfigRequest = new CreateConfigRequestDCM();
				createConfigRequest.setRequest_parent_version(rs.getDouble("request_parent_version"));
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getDataFromRequestInfo method "+exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return createConfigRequest;
	}

	public String abortScheduledRequest(String requestId, String version) {
		String query = "Select request_info_id from requestinfoso  where alphanumeric_req_id = ? and request_version = ?";
		String delWebSerQuery = "delete from webserviceinfo where alphanumeric_req_id = ? and version = ? ";
		String delIntlcvrSoQuery = "delete from internetlcvrfso where request_info_id = ? ";
		String delMisarpsoQuery = "delete from misarpeso where request_info_id = ? ";
		String delDevIntSoQuery = "delete from deviceinterfaceso where request_info_id = ? ";
		String delBanDTQuery = "delete from bannerdatatable where request_info_id = ? ";
		String delReqInfoQuery = "delete from requestinfoso where alphanumeric_req_id = ? and request_version = ? ";
		ResultSet rs = null;
		String result = "";
		String rowId = "";
		try(Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {

			pst.setString(1, requestId);
			pst.setString(2, version);

			rs = pst.executeQuery();
			while (rs.next()) {
				rowId = rs.getString("request_info_id");
			}
			/* Delete webserviceinfo */
			try(PreparedStatement delWebPs = connection.prepareStatement(delWebSerQuery);) {
				delWebPs.setString(1, requestId);
				delWebPs.setString(2, version);
				delWebPs.execute("SET FOREIGN_KEY_CHECKS=0");
				delWebPs.executeUpdate();
			} catch (SQLException exe) {
				logger.error("SQL Exception in abortScheduledRequest delete webserviceinfo method "+exe.getMessage());
			}

			/* Delete internetlcvrfso */
			try(PreparedStatement delIntPs = connection.prepareStatement(delIntlcvrSoQuery);) {
				delIntPs.setString(1, rowId);
				delIntPs.execute("SET FOREIGN_KEY_CHECKS=0");
				delIntPs.executeUpdate();
			} catch (SQLException exe) {
				logger.error("SQL Exception in abortScheduledRequest delete internetlcvrfso method "+exe.getMessage());
			}
			
			/* Delete misarpeso */
			try(PreparedStatement delMisPs = connection.prepareStatement(delMisarpsoQuery);) {
				delMisPs.setString(1, rowId);
				delMisPs.execute("SET FOREIGN_KEY_CHECKS=0");
				delMisPs.executeUpdate();
			} catch (SQLException exe) {
				logger.error("SQL Exception in abortScheduledRequest delete misarpeso method "+exe.getMessage());
			}
			
			/* Delete deviceinterfaceso */
			try(PreparedStatement delDevPs = connection.prepareStatement(delDevIntSoQuery);) {
				delDevPs.setString(1, rowId);
				delDevPs.execute("SET FOREIGN_KEY_CHECKS=0");
				delDevPs.executeUpdate();
			} catch (SQLException exe) {
				logger.error("SQL Exception in abortScheduledRequest delete deviceinterfaceso method "+exe.getMessage());
			}
			
			/* Delete bannerdatatable */
			try(PreparedStatement delBanPs = connection.prepareStatement(delBanDTQuery);) {
				delBanPs.setString(1, rowId);
				delBanPs.execute("SET FOREIGN_KEY_CHECKS=0");
				delBanPs.executeUpdate();
			} catch (SQLException exe) {
				logger.error("SQL Exception in abortScheduledRequest delete bannerdatatable method "+exe.getMessage());
			}
			
			/* Delete requestinfoso */
			try(PreparedStatement delReqPs = connection.prepareStatement(delReqInfoQuery);) {
				delReqPs.setString(1, requestId);
				delReqPs.setString(2, version);
				delReqPs.execute("SET FOREIGN_KEY_CHECKS=0");
				delReqPs.executeUpdate();
			} catch (SQLException exe) {
				logger.error("SQL Exception in abortScheduledRequest delete requestinfoso method "+exe.getMessage());
			}
			result = "Success";
		} catch (SQLException exe) {
			result = "Failed";
			logger.error("SQL Exception in abortScheduledRequest method "+exe.getMessage());
		} finally {
			DBUtil.close(rs);
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
		String query = "Select history_processId from camundahistory where history_requestId=? and history_versionId=?";
		ResultSet rs = null;
		
		try(Connection	connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			pst.setString(1, requestId);
			pst.setString(2, version);
			rs = pst.executeQuery();
			while (rs.next()) {
				processId = rs.getString("history_processId");
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getProcessIdFromCamundaHistory method "+exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}

		return processId;
	}

	public void deleteProcessIdFromCamundaHistory(String processId) {
		String query = "delete from camundahistory where history_processId = ?";
		try(Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			pst.setString(1, processId);
			pst.execute("SET FOREIGN_KEY_CHECKS=0");
			pst.executeUpdate();
		} catch (SQLException exe) {
			logger.error("SQL Exception in deleteProcessIdFromCamundaHistory method "+exe.getMessage());
		}
	}

	/* method overloading for UIRevamp */
	public String updateScheduledRequest(RequestInfoPojo configRequest) throws SQLException {
		String result = "";
		String query = "INSERT INTO scheduledrequesthistory(RequestID,version,Status,Next_Execution_Time,Last_Execution_Time)"
				+ "VALUES(?,?,?,?,?)";
		try(Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setString(1, configRequest.getAlphanumericReqId());
			preparedStmt.setString(2, Double.toString(configRequest.getRequestVersion()));
			preparedStmt.setString(3, "Scheduled");

			if (configRequest.getSceheduledTime() != null && !configRequest.getSceheduledTime().isEmpty()) {
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				try {
					Date parsedDate = sdf.parse(configRequest.getSceheduledTime());
					Timestamp timestampTimeForScheduled = new Timestamp(parsedDate.getTime());
					preparedStmt.setTimestamp(4, timestampTimeForScheduled);

					Timestamp timestampTimeForAction = new Timestamp(new Date().getTime());
					preparedStmt.setTimestamp(5, timestampTimeForAction);
				} catch (ParseException exe) {
					result = "Failure";
					exe.printStackTrace();
				}
			}
			preparedStmt.executeUpdate();
			result = "Success";
		} catch (SQLException exe) {
			logger.error("SQL Exception in updateScheduledRequest method "+exe.getMessage());
		}

		return result;
	}

}
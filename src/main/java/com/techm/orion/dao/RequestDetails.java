package com.techm.orion.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techm.orion.connection.ConnectionFactory;
import com.techm.orion.connection.DBUtil;

/*
 * Owner: Rahul Tiwari Reason: Get configuration feature name and details from database
 * and get test name and version from database 
 * custom tests
 */
public class RequestDetails {
	private static final Logger logger = LogManager.getLogger(RequestDetails.class);
	/*
	 * Owner: Rahul Tiwari Module: TestAndDiagnosis Logic: Get test name and version
	 * based on request id custom tests
	 */
	public String getTestAndDiagnosisDetails(String requestId,double requestVersion) throws SQLException {
		StringBuilder builder = new StringBuilder();
		ResultSet resultSet = null;
		String query = "SELECT RequestId,TestsSelected FROM t_tststrategy_m_config_transaction where RequestId= ? and request_version =?";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setString(1, requestId);
			preparedStmt.setDouble(2, requestVersion);
			resultSet = preparedStmt.executeQuery();
			while (resultSet.next()) {
				builder.append(resultSet.getString("TestsSelected"));
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getTestAndDiagnosisDetails method "+exe.getMessage());
		} finally {
			DBUtil.close(resultSet);
		}
		return builder.toString();
	}

	/*
	 * Owner: Rahul Tiwari Module: Request Details Logic: Get configuration feature
	 * name and value based on requestid and tempalteId custom tests
	 */
	public Map<String, String> getConfigurationFeatureList(String requestId, String templateId) throws SQLException {
		Map<String, String> map = new TreeMap<String, String>();
		ResultSet resultSet = null;
		String query = "SELECT flist.command_parent_feature, info.master_label_value as value, attr.label as name "
				+ "FROM t_create_config_m_attrib_info info "
				+ "left join t_attrib_m_attribute attr on info.master_label_id=attr.id "
				+ "left join c3p_template_master_feature_list flist on attr.feature_id= flist.id "
				+ "where info.request_id= ? and info.template_id= ?";

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setString(1, requestId);
			preparedStmt.setString(2, templateId);

			resultSet = preparedStmt.executeQuery();
			while (resultSet.next()) {
				map.put(resultSet.getString("name"), resultSet.getString("value"));
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getConfigurationFeatureList method "+exe.getMessage());
		} finally {
			DBUtil.close(resultSet);
		}
		return map;
	}

	/*
	 * Owner: Rahul Tiwari Module: Request Details Logic: Get configuration feature
	 * name based on request id, temaplate id custom tests
	 */
	public List<String> getConfigurationFeature(String requestId, String templateId) throws SQLException {
		List<String> list = new ArrayList<String>();
		ResultSet resultSet = null;
		String query = "SELECT distinct flist.command_parent_feature as feature "
				+ "FROM t_create_config_m_attrib_info info "
				+ "left join t_attrib_m_attribute attr on info.master_label_id=attr.id "
				+ "left join c3p_template_master_feature_list flist on attr.feature_id= flist.id "
				+ "where info.request_id= ? and info.template_id= ?";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setString(1, requestId);
			preparedStmt.setString(2, templateId);

			resultSet = preparedStmt.executeQuery();
			while (resultSet.next()) {
				list.add('"' + resultSet.getString("feature").toString() + '"');
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getConfigurationFeature method "+exe.getMessage());
		} finally {
			DBUtil.close(resultSet);
		}
		return list;
	}

	/*
	 * Owner: Rahul Tiwari Module: Request Details Logic: Get configuration feature
	 * details based on request id, temaplate id and feature custom tests
	 */
	public Map<String, String> getConfigurationFeatureDetails(String requestId, String templateId, String feature)
			throws SQLException {
		Map<String, String> map = new TreeMap<String, String>();
		ResultSet resultSet = null;
		String query = "SELECT flist.command_parent_feature as feature, info.master_label_value as value, attr.label as name "
				+ "FROM t_create_config_m_attrib_info info "
				+ "left join t_attrib_m_attribute attr on info.master_label_id=attr.id "
				+ "left join c3p_template_master_feature_list flist on attr.feature_id= flist.id "
				+ "where info.request_id= ? and info.template_id= ? and flist.command_parent_feature = ? ";
		
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setString(1, requestId);
			preparedStmt.setString(2, templateId);
			preparedStmt.setString(3, feature);

			resultSet = preparedStmt.executeQuery();
			while (resultSet.next()) {
				map.put(resultSet.getString("name"), resultSet.getString("value"));

			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getConfigurationFeatureDetails method "+exe.getMessage());
		} finally {
			DBUtil.close(resultSet);
		}
		return map;
	}

}
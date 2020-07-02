package com.techm.orion.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.techm.orion.connection.ConnectionFactory;
import com.techm.orion.connection.DBUtil;

/*
 * Owner: Rahul Tiwari Reason: Get configuration feature name and details from database
 * and get test name and version from database 
 * custom tests
 */
public class RequestDetails {

	ResultSet resultSet = null;
	PreparedStatement preparedStmt = null;
	private Connection connection;
	Statement statement;

	/*
	 * Owner: Rahul Tiwari Module: TestAndDiagnosis Logic: Get test name and version
	 * based on request id custom tests
	 */
	public String getTestAndDiagnosisDetails(String requestId) throws SQLException {
		StringBuilder builder = new StringBuilder();
		connection = ConnectionFactory.getConnection();

		try {
			String query = "SELECT RequestId,TestsSelected FROM requestinfo.t_tststrategy_m_config_transaction where RequestId= ?";

			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, requestId);
			resultSet = preparedStmt.executeQuery();
			while (resultSet.next()) {
				builder.append(resultSet.getString("TestsSelected"));

			}
			preparedStmt.close();
		} finally {
			DBUtil.close(resultSet);
			DBUtil.close(preparedStmt);
			DBUtil.close(connection);
		}
		return builder.toString();
	}

	/*
	 * Owner: Rahul Tiwari Module: Request Details Logic: Get configuration feature
	 * name and value based on requestid and tempalteId custom tests
	 */
	public Map<String, String> getConfigurationFeatureList(String requestId, String templateId) throws SQLException {
		Map<String, String> map = new TreeMap<String, String>();
		connection = ConnectionFactory.getConnection();

		try {
			String query = "SELECT flist.command_parent_feature, info.master_label_value as value, attr.label as name "
					+ "FROM requestinfo.t_create_config_m_attrib_info info "
					+ "left join requestinfo.t_attrib_m_attribute attr on info.master_label_id=attr.id "
					+ "left join requestinfo.c3p_template_master_feature_list flist on attr.feature_id= flist.id "
					+ "where info.request_id= ? and info.template_id= ?";

			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, requestId);
			preparedStmt.setString(2, templateId);

			resultSet = preparedStmt.executeQuery();
			while (resultSet.next()) {
				map.put(resultSet.getString("name"), resultSet.getString("value"));

			}
			preparedStmt.close();
		} finally {
			DBUtil.close(resultSet);
			DBUtil.close(preparedStmt);
			DBUtil.close(connection);
		}
		return map;
	}

	/*
	 * Owner: Rahul Tiwari Module: Request Details Logic: Get configuration feature
	 * name based on request id, temaplate id custom tests
	 */
	public List<String> getConfigurationFeature(String requestId, String templateId) throws SQLException {
		List<String> list = new ArrayList<String>();

		connection = ConnectionFactory.getConnection();

		try {
			String query = "SELECT distinct flist.command_parent_feature as feature "
					+ "FROM requestinfo.t_create_config_m_attrib_info info "
					+ "left join requestinfo.t_attrib_m_attribute attr on info.master_label_id=attr.id "
					+ "left join requestinfo.c3p_template_master_feature_list flist on attr.feature_id= flist.id "
					+ "where info.request_id= ? and info.template_id= ?";

			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, requestId);
			preparedStmt.setString(2, templateId);

			resultSet = preparedStmt.executeQuery();
			while (resultSet.next()) {
				list.add('"' + resultSet.getString("feature").toString() + '"');
			}
			preparedStmt.close();
		} finally {
			DBUtil.close(resultSet);
			DBUtil.close(preparedStmt);
			DBUtil.close(connection);
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
		List<String> list = new ArrayList<String>();

		connection = ConnectionFactory.getConnection();

		try {
			String query = "SELECT flist.command_parent_feature as feature, info.master_label_value as value, attr.label as name "
					+ "FROM requestinfo.t_create_config_m_attrib_info info "
					+ "left join requestinfo.t_attrib_m_attribute attr on info.master_label_id=attr.id "
					+ "left join requestinfo.c3p_template_master_feature_list flist on attr.feature_id= flist.id "
					+ "where info.request_id= ? and info.template_id= ? and flist.command_parent_feature = ? ";

			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, requestId);
			preparedStmt.setString(2, templateId);
			preparedStmt.setString(3, feature);

			resultSet = preparedStmt.executeQuery();
			while (resultSet.next()) {
				map.put(resultSet.getString("name"), resultSet.getString("value"));

			}
			preparedStmt.close();
		} finally {
			DBUtil.close(resultSet);
			DBUtil.close(preparedStmt);
			DBUtil.close(connection);
		}
		return map;
	}

	/* Dhanshri Mane get Configuration details and attribute value */
	public JSONArray getFeatureDetails(String requestId) throws SQLException {

		connection = ConnectionFactory.getConnection();
		JSONArray finalObject = new JSONArray();
		try {
			String query = "SELECT feature.comand_display_feature as feature,attrib.label as name,master_label_value as value FROM t_create_config_m_attrib_info as info ,t_attrib_m_attribute as attrib ,c3p_template_master_feature_list as feature where request_id=? And info.master_label_id=attrib.id And feature.id=attrib.feature_id;";

			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, requestId);

			resultSet = preparedStmt.executeQuery();
			Set<String> featureName = new HashSet<String>();
			while (resultSet.next()) {
				featureName.add(resultSet.getString("feature"));
			}
			ResultSet resultSet1 = null;
			for (String feature : featureName) {
				resultSet1 = preparedStmt.executeQuery();
				try {
					JSONObject featureObject = new JSONObject();
					featureObject.put("featureName", feature);
					JSONArray finalFeatureObject = new JSONArray();
					while (resultSet1.next()) {
						String name = resultSet1.getString("feature");
						if (name.equals(feature)) {
							JSONObject attributeValue = new JSONObject();
							attributeValue.put("name", resultSet1.getString("name"));
							attributeValue.put("value", resultSet1.getString("value"));
							finalFeatureObject.add(attributeValue);
						}
					}
					featureObject.put("featureValue", finalFeatureObject);
					finalObject.add(featureObject);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			preparedStmt.close();
		} finally {
			DBUtil.close(resultSet);
			DBUtil.close(preparedStmt);
			DBUtil.close(connection);
		}
		return finalObject;
	}
}
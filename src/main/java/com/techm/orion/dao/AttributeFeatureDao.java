package com.techm.orion.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.techm.orion.connection.ConnectionFactory;
import com.techm.orion.connection.DBUtil;
import com.techm.orion.models.TemplateAttributeJSONModel;

public class AttributeFeatureDao {
	Statement smt = null;
	ResultSet rs1 = null;
	PreparedStatement preparedStmt = null;
	private Connection connection;
	Statement statement;

	public List<String> getParentFeatureList(TemplateAttributeJSONModel templateAttributeJSONModel)
			throws SQLException {
		List<String> list = new ArrayList<String>();

		connection = ConnectionFactory.getConnection();

		try {

			String query2 = "Select attri_name from attribute_feature_details where attri_feature like variable=%?% and attri_feature=?";
			preparedStmt = connection.prepareStatement(query2);
			preparedStmt.setString(1, templateAttributeJSONModel.getAttributename());
			preparedStmt.setString(2, templateAttributeJSONModel.getAttributeFeature());

			rs1 = preparedStmt.executeQuery();
			while (rs1.next()) {
				list.add(rs1.getString("attri_name"));

			}
			preparedStmt.close();
		} finally {
			DBUtil.close(rs1);
			DBUtil.close(preparedStmt);
			DBUtil.close(connection);
		}
		return list;
	}
}
package com.techm.c3p.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.techm.c3p.core.connection.ConnectionFactory;
import com.techm.c3p.core.connection.DBUtil;
import com.techm.c3p.core.models.TemplateAttributeJSONModel;

public class AttributeFeatureDao {

	public List<String> getParentFeatureList(TemplateAttributeJSONModel templateAttributeJSONModel)
			throws SQLException {
		List<String> list = new ArrayList<String>();
		String query = "Select attri_name from attribute_feature_details where attri_feature like variable=%?% and attri_feature=?";
		ResultSet rs = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setString(1, templateAttributeJSONModel.getAttributename());
			preparedStmt.setString(2, templateAttributeJSONModel.getAttributeFeature());
			rs = preparedStmt.executeQuery();
			while (rs.next()) {
				list.add(rs.getString("attri_name"));

			}
		} finally {
			DBUtil.close(rs);
		}
		return list;
	}
}

package com.techm.orion.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.techm.orion.connection.ConnectionFactory;
import com.techm.orion.connection.DBUtil;
import com.techm.orion.pojo.AddNewFeatureTemplateMngmntPojo;
import com.techm.orion.pojo.CommandPojo;
import com.techm.orion.pojo.GetTemplateMngmntActiveDataPojo;

public class TemplateManagementDB {

	private Connection connection;
	Statement statement;

	public int updateFeatureTablesForNewCommand(AddNewFeatureTemplateMngmntPojo addNewFeatureTemplateMngmntPojo) {
		String query1 = null, query = null;
		connection = ConnectionFactory.getConnection();
		PreparedStatement preparedStmt = null;
		Statement smt = null;
		ResultSet rs1 = null;
		int idToSetInCommandTable = 0;
		try {
			// query1 = "Insert into
			// c3p_template_master_feature_list(comand_display_feature,command_parent_feature,command_type,hasParent,is_Save)
			// values(?,?,?,?,?)";
			query1 = "Insert into c3p_template_master_feature_list(comand_display_feature,command_parent_feature,command_type,hasParent,is_Save,isMandate) values(?,?,?,?,?,?)";
			preparedStmt = connection.prepareStatement(query1);
			preparedStmt.setString(1, addNewFeatureTemplateMngmntPojo.getFeatureName());
			if (addNewFeatureTemplateMngmntPojo.getParentName().equalsIgnoreCase("Add New Feature")) {
				preparedStmt.setString(2, addNewFeatureTemplateMngmntPojo.getFeatureName());
			} else {
				preparedStmt.setString(2, addNewFeatureTemplateMngmntPojo.getParentName());

			}
			preparedStmt.setString(3, addNewFeatureTemplateMngmntPojo.getTemplateid());
			preparedStmt.setInt(5, 0);
			preparedStmt.setInt(6, 1);
			if (addNewFeatureTemplateMngmntPojo.getParentName().equalsIgnoreCase("Add New Feature")) {
				preparedStmt.setInt(4, 0);
			} else {
				preparedStmt.setInt(4, 1);

			}
			int updateMasterFeatureTable = preparedStmt.executeUpdate();
			if (updateMasterFeatureTable > 0) {
				int id = 0;
				query = "select * from c3p_template_master_feature_list";
				smt = connection.createStatement();
				rs1 = smt.executeQuery(query);
				while (rs1.next()) {
					if (rs1.getString("comand_display_feature")
							.equalsIgnoreCase(addNewFeatureTemplateMngmntPojo.getFeatureName())) {
						id = rs1.getInt("id");
						idToSetInCommandTable = id;
					}
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(connection);
		}
		return idToSetInCommandTable;
	}

	public List<CommandPojo> updateMasterCommandTableWithNewCommand(
			AddNewFeatureTemplateMngmntPojo addNewFeatureTemplateMngmntPojo, int idToSetInCommandTable) {
		connection = ConnectionFactory.getConnection();
		String query1 = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		int lastCount = 0;
		List<CommandPojo> commandPojoList = new ArrayList<CommandPojo>();
		try {
			CommandPojo commandWithId = null;

			for (int i = 0; i < addNewFeatureTemplateMngmntPojo.getCmdList().size(); i++) {

				CommandPojo commandPojo = addNewFeatureTemplateMngmntPojo.getCmdList().get(i);

				String query0 = "select max(command_sequence_id) from c3p_template_master_command_list";
				preparedStmt = connection.prepareStatement(query0);
				rs = preparedStmt.executeQuery();
				if (rs != null) {
					while (rs.next()) {
						lastCount = rs.getInt("max(command_sequence_id)");
					}
				}

				query1 = "Insert into c3p_template_master_command_list (command_id,command_value,command_sequence_id,command_type,no_form_command) values (?,?,?,?,?)";
				preparedStmt = connection.prepareStatement(query1);
				preparedStmt.setString(1, Integer.toString(idToSetInCommandTable));
				preparedStmt.setString(2, commandPojo.getCommand_value());
				preparedStmt.setInt(3, ++lastCount);
				preparedStmt.setString(4, addNewFeatureTemplateMngmntPojo.getTemplateid());
				if (commandPojo.getNo_command_value() != null
						|| commandPojo.getNo_command_value().equalsIgnoreCase("")) {
					preparedStmt.setString(5, commandPojo.getNo_command_value());
				}

				preparedStmt.executeUpdate();

				commandWithId = new CommandPojo();
				commandWithId.setCommand_id(Integer.toString(idToSetInCommandTable));
				commandWithId.setCommandValue(commandPojo.getCommand_value());
				commandWithId.setCommandSequenceId(lastCount);
				commandWithId.setNew(true);
				commandWithId.setChecked(true);
				commandPojoList.add(commandWithId);

				/*
				 * if (update > 0) { result = true; }
				 */
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(connection);
		}
		return commandPojoList;
	}

	public List<GetTemplateMngmntActiveDataPojo> getDataForRightPanel(String templateId, boolean selectAll)
			throws SQLException {
		TemplateManagementDao templatemanagementDao = new TemplateManagementDao();
		String query1, query2, templateVersion, templateIdAndVersion = null;
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		String tempserieskey = null;

		try {
			json = (JSONObject) parser.parse(templateId);
			if (json.containsKey("templateid")) {
				if (json.get("templateid") != null && !json.get("templateid").equals("")) {
					if (json.get("templateid").toString().contains("_V")) {
						templateId = json.get("templateid").toString().substring(0,
								json.get("templateid").toString().length() - 6);
					} else {
						templateId = json.get("templateid").toString();
					}
				}
			}
			if (json.containsKey("templateVersion")) {
				if (json.get("templateVersion") != null && !json.get("templateVersion").equals("")) {
					templateVersion = json.get("templateVersion").toString();
					templateIdAndVersion = templateId + "_V" + templateVersion;
				} else {
					templateVersion = "1.0";
					templateIdAndVersion = templateId + "_V" + templateVersion;
				}
			}
			if (json.containsKey("vendor")) {
				String vendor = json.get("vendor").toString();
				String devicetype = json.get("deviceType").toString();
				String model = json.get("model").toString();
				tempserieskey = vendor + devicetype + model.substring(0, 2);
				/*if master configuration updated that time series is not null*/
				if (json.containsKey("series")) {
					if (json.get("series") != null && !json.get("series").toString().equals("")) {
						tempserieskey = json.get("series").toString();
					}else {
						/*
						 * Dhanshri Mane 14-1-2020
						 * get the series according to template id*/
						tempserieskey=templatemanagementDao.getSeriesId(templateIdAndVersion, tempserieskey);
						tempserieskey=StringUtils.substringAfter(tempserieskey, "Generic_");
					}
				}
			}

		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		GetTemplateMngmntActiveDataPojo getTemplateMngmntActiveDataPojo = null;
		List<GetTemplateMngmntActiveDataPojo> dataList = new ArrayList<GetTemplateMngmntActiveDataPojo>();
		List<GetTemplateMngmntActiveDataPojo> dataListoftemplatespecificcommand = new ArrayList<GetTemplateMngmntActiveDataPojo>();
		PreparedStatement preparedStmt = null, preparedStmt2 = null;
		connection = ConnectionFactory.getConnection();
		ResultSet rs1 = null, rsl2 = null;
		Map<String, Integer> positionMap = new HashMap<String, Integer>();
		Map<String, Integer> commandSequenceIdforSelectedTemplete = new HashMap<String, Integer>();
		try {
			// query1 = "select
			// cmdlist.command_value,cmdlist.command_sequence_id,flist.check_default,flist.hasParent,flist.id
			// from c3p_template_master_command_list cmdlist
			// ,c3p_template_master_feature_list flist where cmdlist.command_id=flist.id and
			// (flist.command_type = ? or flist.command_type like ?) order by
			// cmdlist.command_sequence_id";

			if (json.get("series") == null || json.get("series").toString().equals("")) {
			query1 = "select cmdlist.command_value,cmdlist.command_sequence_id,flist.check_default,flist.hasParent,flist.id from c3p_template_master_command_list cmdlist ,c3p_template_master_feature_list flist where cmdlist.command_id=flist.id and (flist.command_type = ? or flist.command_type=?)order by cmdlist.command_sequence_id";
			preparedStmt = connection.prepareStatement(query1);
			if (tempserieskey != null) {
				preparedStmt.setString(1, "Generic_" + tempserieskey);
			} else {
				preparedStmt.setString(1, "Generic");
			}
			// preparedStmt.setString(2,templateId );
			preparedStmt.setString(2, templateIdAndVersion);

			query2 = "SELECT * FROM c3p_template_transaction_command_list where c3p_template_transaction_command_list.command_template_id = ?";
			preparedStmt2 = connection.prepareStatement(query2);
			preparedStmt2.setString(1, templateIdAndVersion);
			rsl2 = preparedStmt2.executeQuery();
			while (rsl2.next()) {
				getTemplateMngmntActiveDataPojo = new GetTemplateMngmntActiveDataPojo();
				getTemplateMngmntActiveDataPojo.setCommandSequenceId(
						rsl2.getString("c3p_template_transaction_command_list.command_sequence_id"));

				getTemplateMngmntActiveDataPojo.setActive(true);
				getTemplateMngmntActiveDataPojo.setId(rsl2.getInt("c3p_template_transaction_command_list.command_id"));
				getTemplateMngmntActiveDataPojo
						.setPosition(rsl2.getInt("c3p_template_transaction_command_list.command_position"));
				if (rsl2.getInt("c3p_template_transaction_command_list.is_save") == 1) {
					commandSequenceIdforSelectedTemplete.put(
							rsl2.getString("c3p_template_transaction_command_list.command_sequence_id"),
							rsl2.getInt("c3p_template_transaction_command_list.command_position"));
				}
				positionMap.put(rsl2.getString("c3p_template_transaction_command_list.command_sequence_id"),
						rsl2.getInt("c3p_template_transaction_command_list.command_position"));
				dataListoftemplatespecificcommand.add(getTemplateMngmntActiveDataPojo);
				}
			
			}else {
				query1 = "select cmdlist.command_value,cmdlist.command_sequence_id,flist.check_default,flist.hasParent,flist.id from c3p_template_master_command_list cmdlist ,c3p_template_master_feature_list flist where cmdlist.command_id=flist.id and (flist.command_type = ?)order by cmdlist.command_sequence_id";
				preparedStmt = connection.prepareStatement(query1);
				if (tempserieskey != null) {
					preparedStmt.setString(1, "Generic_" + tempserieskey);
				} else {
					preparedStmt.setString(1, "Generic");
				}
			}

			rs1 = preparedStmt.executeQuery();
			while (rs1.next()) {
				getTemplateMngmntActiveDataPojo = new GetTemplateMngmntActiveDataPojo();
				getTemplateMngmntActiveDataPojo.setCommandValue(rs1.getString("cmdlist.command_value"));
				getTemplateMngmntActiveDataPojo.setCommandSequenceId(rs1.getString("cmdlist.command_sequence_id"));
				getTemplateMngmntActiveDataPojo.setActiveFlag(rs1.getInt("flist.check_default"));
				if (getTemplateMngmntActiveDataPojo.getActiveFlag() == 1 || commandSequenceIdforSelectedTemplete
						.containsKey(rs1.getString("cmdlist.command_sequence_id"))) {
					getTemplateMngmntActiveDataPojo.setActive(true);
				}
				if (positionMap.isEmpty()) {
					getTemplateMngmntActiveDataPojo
							.setPosition(Integer.parseInt(getTemplateMngmntActiveDataPojo.getCommandSequenceId()));
				} else {
					getTemplateMngmntActiveDataPojo
							.setPosition(positionMap.get(rs1.getString("cmdlist.command_sequence_id")));
				}

				getTemplateMngmntActiveDataPojo.setHasParent(rs1.getInt("flist.hasParent"));
				getTemplateMngmntActiveDataPojo.setId(rs1.getInt("flist.id"));
				dataList.add(getTemplateMngmntActiveDataPojo);
			}
		

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs1);
			DBUtil.close(preparedStmt);
			DBUtil.close(rsl2);
			DBUtil.close(preparedStmt2);
			DBUtil.close(connection);
		}
		dataList.sort((o1, o2) -> o1.getPosition() - o2.getPosition());
		return dataList;
	}

	public int updateTransactionCommandForNewTemplate(AddNewFeatureTemplateMngmntPojo addNewFeatureTemplateMngmntPojo) {
		String query1 = null, query2 = null, query = null;
		connection = ConnectionFactory.getConnection();
		PreparedStatement preparedStmt = null;
		PreparedStatement preparedStmt1 = null;
		PreparedStatement preparedStmt2 = null, preparedStmt3 = null, preparedStmt4 = null, preparedStmt5 = null;
		ResultSet rs1 = null;
		int idToSetInCommandTable = 0;
		Set<String> availableCommandIDs = new HashSet<String>();
		try {
			for (int i = 0; i < addNewFeatureTemplateMngmntPojo.getCmdList().size(); i++) {

				CommandPojo commandPojo = addNewFeatureTemplateMngmntPojo.getCmdList().get(i);

				query1 = "Insert into c3p_template_transaction_command_list (command_id,command_sequence_id,command_template_id,command_position,is_save) values (?,?,?,?,?)";
				preparedStmt = connection.prepareStatement(query1);
				preparedStmt.setString(1, commandPojo.getCommand_id());
				availableCommandIDs.add(commandPojo.getCommand_id());
				preparedStmt.setInt(2, commandPojo.getCommand_sequence_id());
				preparedStmt.setString(3, addNewFeatureTemplateMngmntPojo.getTemplateid());

				preparedStmt.setInt(4, commandPojo.getPosition());
				preparedStmt.setInt(5, commandPojo.getIs_save());
				preparedStmt.executeUpdate();

			}

			// query="select * from c3p_template_master_feature_list WHERE command_type like
			// ?";
			query = "select * from c3p_template_master_feature_list WHERE command_type = ?";
			preparedStmt1 = connection.prepareStatement(query);
			// preparedStmt1.setString(1,
			// addNewFeatureTemplateMngmntPojo.getTemplateid().substring(0,
			// addNewFeatureTemplateMngmntPojo.getTemplateid().length()-5)+"%");
			preparedStmt1.setString(1, addNewFeatureTemplateMngmntPojo.getTemplateid());
			rs1 = preparedStmt1.executeQuery();
			List<String> newfeaturesForTemplate = new ArrayList<String>();
			while (rs1.next()) {
				newfeaturesForTemplate.add(String.valueOf(rs1.getInt("id")));
			}
			// query2 = "UPDATE c3p_template_master_feature_list SET is_Save = '1' WHERE
			// command_type like ?";
			query2 = "UPDATE c3p_template_master_feature_list SET is_Save = '1' WHERE command_type = ?";
			preparedStmt2 = connection.prepareStatement(query2);
			// preparedStmt2.setString(1,addNewFeatureTemplateMngmntPojo.getTemplateid().substring(0,
			// addNewFeatureTemplateMngmntPojo.getTemplateid().length()-5)+"%");
			preparedStmt2.setString(1, addNewFeatureTemplateMngmntPojo.getTemplateid());

			if (newfeaturesForTemplate.size() > 0) {
				for (String feture : newfeaturesForTemplate) {
					if (availableCommandIDs.contains(feture)) {
						preparedStmt2.execute("SET SQL_SAFE_UPDATES = 0");
						preparedStmt2.executeUpdate();
						preparedStmt2.execute("SET SQL_SAFE_UPDATES = 1");
					} else {
						/* for deleting t_attrib_m_attribute table data pankaj */
						String query5 = "delete from t_attrib_m_attribute where feature_id =?";
						preparedStmt5 = connection.prepareStatement(query5);
						preparedStmt5.setInt(1, Integer.parseInt(feture));
						preparedStmt5.execute("SET SQL_SAFE_UPDATES = 0");
						preparedStmt5.executeUpdate();
						preparedStmt5.execute("SET SQL_SAFE_UPDATES = 1");

						String query3 = "delete from c3p_template_master_feature_list where id =?";
						preparedStmt3 = connection.prepareStatement(query3);
						preparedStmt3.setInt(1, Integer.parseInt(feture));
						preparedStmt3.execute("SET SQL_SAFE_UPDATES = 0");
						preparedStmt3.executeUpdate();
						preparedStmt3.execute("SET SQL_SAFE_UPDATES = 1");
						String query4 = "delete from c3p_template_master_command_list where command_id =?";
						preparedStmt4 = connection.prepareStatement(query4);
						preparedStmt4.setString(1, feture);
						preparedStmt4.execute("SET SQL_SAFE_UPDATES = 0");
						preparedStmt4.executeUpdate();
						preparedStmt4.execute("SET SQL_SAFE_UPDATES = 1");
					}
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(preparedStmt);
			DBUtil.close(preparedStmt2);
			DBUtil.close(preparedStmt3);
			DBUtil.close(preparedStmt4);
			DBUtil.close(rs1);
			DBUtil.close(connection);
		}
		return idToSetInCommandTable;
	}

	public int updateTransactionFeatureForNewTemplate(AddNewFeatureTemplateMngmntPojo addNewFeatureTemplateMngmntPojo) {
		String query1 = null, query = null;
		connection = ConnectionFactory.getConnection();
		PreparedStatement preparedStmt = null;
		Statement smt = null;
		ResultSet rs1 = null;
		int idToSetInCommandTable = 0;
		try {
			for (int i = 0; i < addNewFeatureTemplateMngmntPojo.getCmdList().size(); i++) {

				CommandPojo commandPojo = addNewFeatureTemplateMngmntPojo.getCmdList().get(i);

				query1 = "Insert into c3p_template_transaction_feature_list (id,command_feature_template_id) values (?,?)";
				preparedStmt = connection.prepareStatement(query1);
				preparedStmt.setString(1, commandPojo.getId());

				preparedStmt.setString(2, addNewFeatureTemplateMngmntPojo.getTemplateid());

				preparedStmt.executeUpdate();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(connection);
		}
		return idToSetInCommandTable;
	}

	public List<GetTemplateMngmntActiveDataPojo> getDataForRightPanelOnEdit(String templateId, boolean selectAll)
			throws SQLException {

		String query1 = null;
		GetTemplateMngmntActiveDataPojo getTemplateMngmntActiveDataPojo = null;
		List<GetTemplateMngmntActiveDataPojo> dataList = new ArrayList<GetTemplateMngmntActiveDataPojo>();
		PreparedStatement preparedStmt = null;
		connection = ConnectionFactory.getConnection();
		ResultSet rs1 = null;
		try {
			query1 = "select cmdlist.command_value,cmdlist.command_sequence_id,flist.check_default,flist.hasParent,flist.id from c3p_template_master_command_list cmdlist ,c3p_template_master_feature_list flist where cmdlist.command_id=flist.id and flist.command_type=? order by cmdlist.position";
			preparedStmt = connection.prepareStatement(query1);
			preparedStmt.setString(1, "Generic");

			rs1 = preparedStmt.executeQuery();
			while (rs1.next()) {
				getTemplateMngmntActiveDataPojo = new GetTemplateMngmntActiveDataPojo();
				getTemplateMngmntActiveDataPojo.setCommandValue(rs1.getString("cmdlist.command_value"));
				getTemplateMngmntActiveDataPojo.setCommandSequenceId(rs1.getString("cmdlist.command_sequence_id"));
				getTemplateMngmntActiveDataPojo.setActiveFlag(rs1.getInt("flist.check_default"));
				if (getTemplateMngmntActiveDataPojo.getActiveFlag() == 1) {
					getTemplateMngmntActiveDataPojo.setActive(true);
				}
				getTemplateMngmntActiveDataPojo.setHasParent(rs1.getInt("flist.hasParent"));
				getTemplateMngmntActiveDataPojo.setId(rs1.getInt("flist.id"));
				dataList.add(getTemplateMngmntActiveDataPojo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs1);
			DBUtil.close(preparedStmt);
			DBUtil.close(connection);
		}
		return dataList;
	}

	public List<GetTemplateMngmntActiveDataPojo> getRightPanelOnEditTemplate(String templateId, boolean selectAll)
			throws SQLException {

		String query1 = null;
		GetTemplateMngmntActiveDataPojo getTemplateMngmntActiveDataPojo = null;
		List<GetTemplateMngmntActiveDataPojo> dataList = new ArrayList<GetTemplateMngmntActiveDataPojo>();
		PreparedStatement preparedStmt = null;
		connection = ConnectionFactory.getConnection();
		ResultSet rs1 = null;
		try {
			query1 = "select txnList.command_id,txnList.command_sequence_id,masterList.command_value from c3p_template_transaction_command_list txnList,c3p_template_master_command_list masterList where txnList.command_id=masterList.command_id and txnList.command_template_id=? order by txnList.command_position";
			preparedStmt = connection.prepareStatement(query1);
			preparedStmt.setString(1, templateId);

			rs1 = preparedStmt.executeQuery();
			while (rs1.next()) {
				getTemplateMngmntActiveDataPojo = new GetTemplateMngmntActiveDataPojo();
				getTemplateMngmntActiveDataPojo.setCommandValue(rs1.getString("masterList.command_value"));
				getTemplateMngmntActiveDataPojo.setCommandSequenceId(rs1.getString("txnList.command_sequence_id"));

				getTemplateMngmntActiveDataPojo.setActive(true);

				getTemplateMngmntActiveDataPojo.setId(rs1.getInt("txnList.id"));
				dataList.add(getTemplateMngmntActiveDataPojo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs1);
			DBUtil.close(preparedStmt);
			DBUtil.close(connection);
		}
		return dataList;
	}

	public boolean checkTemplateVersionAlredyexist(String templateAndVersion) {
		connection = ConnectionFactory.getConnection();
		PreparedStatement preparedStmt = null;
		String query1 = null;
		ResultSet rs1 = null;
		boolean isAlredyPresent = false;
		try {
			query1 = "SELECT * FROM requestinfo.c3p_template_transaction_command_list where command_template_id = ?";
			preparedStmt = connection.prepareStatement(query1);
			preparedStmt.setString(1, templateAndVersion);
			rs1 = preparedStmt.executeQuery();
			if (rs1.next()) {
				isAlredyPresent = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs1);
			DBUtil.close(preparedStmt);
			DBUtil.close(connection);
		}
		return isAlredyPresent;
	}
}

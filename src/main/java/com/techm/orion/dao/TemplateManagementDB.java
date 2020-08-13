
package com.techm.orion.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.techm.orion.connection.ConnectionFactory;
import com.techm.orion.connection.DBUtil;
import com.techm.orion.pojo.AddNewFeatureTemplateMngmntPojo;
import com.techm.orion.pojo.CommandPojo;
import com.techm.orion.pojo.GetTemplateMngmntActiveDataPojo;

public class TemplateManagementDB {

	private static final Logger logger = LogManager.getLogger(TemplateManagementDB.class);
	
	public int updateFeatureTablesForNewCommand(AddNewFeatureTemplateMngmntPojo addNewFeatureTemplateMngmntPojo) {
		ResultSet rs = null;
		int idToSetInCommandTable = 0;
		String query = "Insert into c3p_template_master_feature_list(comand_display_feature,command_parent_feature,command_type,hasParent,is_Save,isMandate) values(?,?,?,?,?,?)";
		String selQuery = "select * from c3p_template_master_feature_list";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);){
			
			preparedStmt.setString(1, addNewFeatureTemplateMngmntPojo.getFeatureName());
			if (("Add New Feature").equalsIgnoreCase(addNewFeatureTemplateMngmntPojo.getParentName())) {
				preparedStmt.setString(2, addNewFeatureTemplateMngmntPojo.getFeatureName());
			} else {
				preparedStmt.setString(2, addNewFeatureTemplateMngmntPojo.getParentName());
			}
			preparedStmt.setString(3, addNewFeatureTemplateMngmntPojo.getTemplateid());
			preparedStmt.setInt(5, 0);
			preparedStmt.setInt(6, 1);
			if (("Add New Feature").equalsIgnoreCase(addNewFeatureTemplateMngmntPojo.getParentName())) {
				preparedStmt.setInt(4, 0);
			} else {
				preparedStmt.setInt(4, 1);
			}
			int updateMasterFeatureTable = preparedStmt.executeUpdate();
			if (updateMasterFeatureTable > 0) {
				int id = 0;	
				try (PreparedStatement smt = connection.prepareStatement(selQuery);){
					rs = smt.executeQuery();
					while (rs.next()) {
						if (rs.getString("comand_display_feature")
								.equalsIgnoreCase(addNewFeatureTemplateMngmntPojo.getFeatureName())) {
							id = rs.getInt("id");
							idToSetInCommandTable = id;
						}
					}
				} catch (SQLException exe) {
					logger.error("SQL Exception in updateFeatureTablesForNewCommand select method "+exe.getMessage());
				} finally {
					DBUtil.close(rs);
				}
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in updateFeatureTablesForNewCommand method "+exe.getMessage());
		}
		return idToSetInCommandTable;
	}

	public List<CommandPojo> updateMasterCommandTableWithNewCommand(
			AddNewFeatureTemplateMngmntPojo addNewFeatureTemplateMngmntPojo, int idToSetInCommandTable) {
		String query1 = "Insert into c3p_template_master_command_list (command_id,command_value,command_sequence_id,command_type,no_form_command) values (?,?,?,?,?)";
		ResultSet rs = null;
		int lastCount = 0;
		List<CommandPojo> commandPojoList = new ArrayList<CommandPojo>();
		String query0 = "select max(command_sequence_id) from c3p_template_master_command_list";
		try(Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query0);) {
			
			for (int i = 0; i < addNewFeatureTemplateMngmntPojo.getCmdList().size(); i++) {

				CommandPojo commandPojo = addNewFeatureTemplateMngmntPojo.getCmdList().get(i);

				rs = preparedStmt.executeQuery();
				if (rs != null) {
					while (rs.next()) {
						lastCount = rs.getInt("max(command_sequence_id)");
					}
				}
				
				try(PreparedStatement preparedStmt1 = connection.prepareStatement(query1);) {
					preparedStmt1.setString(1, Integer.toString(idToSetInCommandTable));
					preparedStmt1.setString(2, commandPojo.getCommand_value());
					preparedStmt1.setInt(3, ++lastCount);
					preparedStmt1.setString(4, addNewFeatureTemplateMngmntPojo.getTemplateid());
					if (commandPojo.getNo_command_value() != null
							|| commandPojo.getNo_command_value().equalsIgnoreCase("")) {
						preparedStmt1.setString(5, commandPojo.getNo_command_value());
					}

					preparedStmt1.executeUpdate();
				} catch (SQLException exe) {
					logger.error("SQL Exception in updateMasterCommandTableWithNewCommand insert method "+exe.getMessage());
				}

				CommandPojo commandWithId = new CommandPojo();
				commandWithId.setCommand_id(Integer.toString(idToSetInCommandTable));
				commandWithId.setCommandValue(commandPojo.getCommand_value());
				commandWithId.setCommandSequenceId(lastCount);
				commandWithId.setNew(true);
				commandWithId.setChecked(true);
				commandPojoList.add(commandWithId);
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in updateMasterCommandTableWithNewCommand method "+exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return commandPojoList;
	}

	public List<GetTemplateMngmntActiveDataPojo> getDataForRightPanel(String templateId, boolean selectAll)
			throws SQLException {
		TemplateManagementDao templatemanagementDao = new TemplateManagementDao();
		String templateVersion, templateIdAndVersion = null;
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		String tempserieskey = null;
		String masterFeatureLisQuery1 = "select cmdlist.command_value,cmdlist.command_sequence_id,flist.check_default,flist.hasParent,flist.id from c3p_template_master_command_list cmdlist ,c3p_template_master_feature_list flist where cmdlist.command_id=flist.id and (flist.command_type = ? or flist.command_type=?) order by cmdlist.command_sequence_id";
		String masterFeatureLisQuery2 = "select cmdlist.command_value,cmdlist.command_sequence_id,flist.check_default,flist.hasParent,flist.id from c3p_template_master_command_list cmdlist ,c3p_template_master_feature_list flist where cmdlist.command_id=flist.id and (flist.command_type = ?) order by cmdlist.command_sequence_id";
		
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
				/* if master configuration updated that time series is not null */
				if (json.containsKey("series")) {
					if (json.get("series") != null && !json.get("series").toString().equals("")) {
						tempserieskey = json.get("series").toString();
					} else {
						/*
						 * Dhanshri Mane 14-1-2020 get the series according to template id
						 */
						tempserieskey = templatemanagementDao.getSeriesId(templateIdAndVersion, tempserieskey);
						tempserieskey = StringUtils.substringAfter(tempserieskey, "Generic_");
					}
				}
			}

		} catch (ParseException exe) {
			logger.error("Parse Exception in getDataForRightPanel method "+exe.getMessage());
		}

		GetTemplateMngmntActiveDataPojo getTemplateMngmntActiveDataPojo = null;
		List<GetTemplateMngmntActiveDataPojo> dataList = new ArrayList<GetTemplateMngmntActiveDataPojo>();
		ResultSet rs1 = null;
		Map<String, Integer> positionMap = new HashMap<String, Integer>();
		Map<String, Integer> commandSequenceIdforSelectedTemplete = new HashMap<String, Integer>();
		try {

			if (json.get("series") == null || json.get("series").toString().equals("")) {
				try (Connection connection = ConnectionFactory.getConnection();
						PreparedStatement preparedStmt = connection.prepareStatement(masterFeatureLisQuery1)) {
					if (tempserieskey != null) {
						preparedStmt.setString(1, "Generic_" + tempserieskey);
					} else {
						preparedStmt.setString(1, "Generic");
					}
					preparedStmt.setString(2, templateIdAndVersion);
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
					
				} catch (SQLException exe) {
					logger.error("SQL Exception in updateTransactionCommandForNewTemplate insert method "+exe.getMessage());
				}				

			} else {				
				try (Connection connection = ConnectionFactory.getConnection();
						PreparedStatement preparedStmt = connection.prepareStatement(masterFeatureLisQuery2)) {
					if (tempserieskey != null) {
						preparedStmt.setString(1, "Generic_" + tempserieskey);
					} else {
						preparedStmt.setString(1, "Generic");
					}
					preparedStmt.setString(2, templateIdAndVersion);
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
					
				} catch (SQLException exe) {
					logger.error("SQL Exception in updateTransactionCommandForNewTemplate insert method "+exe.getMessage());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs1);
		}
		dataList.sort((o1, o2) -> o1.getPosition() - o2.getPosition());
		return dataList;
	}

	public int updateTransactionCommandForNewTemplate(AddNewFeatureTemplateMngmntPojo addNewFeatureTemplateMngmntPojo) {
		String insertQuery = "Insert into c3p_template_transaction_command_list (command_id,command_sequence_id,command_template_id,command_position,is_save) values (?,?,?,?,?)";
		String temMastFeatureQuery = "select * from c3p_template_master_feature_list WHERE command_type = ?";
		String updateQuery = "UPDATE c3p_template_master_feature_list SET is_Save = '1' WHERE command_type = ?";
		String delMAttQuery = "delete from t_attrib_m_attribute where feature_id =?";
		String delMasFeatQuery = "delete from c3p_template_master_feature_list where id =?";
		String delMasComQuery = "delete from c3p_template_master_command_list where command_id =?";
		ResultSet rs1 = null;
		int idToSetInCommandTable = 0;
		Set<String> availableCommandIDs = new HashSet<String>();
		try {
			for (int i = 0; i < addNewFeatureTemplateMngmntPojo.getCmdList().size(); i++) {
				try (Connection connection = ConnectionFactory.getConnection();
						PreparedStatement preparedStmt = connection.prepareStatement(insertQuery)) {
					CommandPojo commandPojo = addNewFeatureTemplateMngmntPojo.getCmdList().get(i);
					availableCommandIDs.add(commandPojo.getCommand_id());
					preparedStmt.setString(1, commandPojo.getCommand_id());
					preparedStmt.setInt(2, commandPojo.getCommand_sequence_id());
					preparedStmt.setString(3, addNewFeatureTemplateMngmntPojo.getTemplateid());
					preparedStmt.setInt(4, commandPojo.getPosition());
					preparedStmt.setInt(5, commandPojo.getIs_save());
					preparedStmt.executeUpdate();
				} catch (SQLException exe) {
					logger.error("SQL Exception in updateTransactionCommandForNewTemplate insert method "+exe.getMessage());
				}
			}
			
			try (Connection connection = ConnectionFactory.getConnection();
					PreparedStatement preparedStmt1 = connection.prepareStatement(temMastFeatureQuery)) {
				preparedStmt1.setString(1, addNewFeatureTemplateMngmntPojo.getTemplateid());
				rs1 = preparedStmt1.executeQuery();
				List<String> newfeaturesForTemplate = new ArrayList<String>();
				while (rs1.next()) {
					newfeaturesForTemplate.add(String.valueOf(rs1.getInt("id")));
				}
				if (newfeaturesForTemplate.size() > 0) {
					for (String feture : newfeaturesForTemplate) {
						if (availableCommandIDs.contains(feture)) {
							try (PreparedStatement uptdPs = connection.prepareStatement(updateQuery)) {
								uptdPs.setString(1, addNewFeatureTemplateMngmntPojo.getTemplateid());
								uptdPs.execute("SET SQL_SAFE_UPDATES = 0");
								uptdPs.executeUpdate();
								uptdPs.execute("SET SQL_SAFE_UPDATES = 1");
							} catch (SQLException exe) {
								logger.error("SQL Exception in updateTransactionCommandForNewTemplate update method "+exe.getMessage());
							}
						}else {
							try (PreparedStatement delMAttPs = connection.prepareStatement(delMAttQuery)) {
								delMAttPs.setInt(1, Integer.parseInt(feture));
								delMAttPs.execute("SET SQL_SAFE_UPDATES = 0");
								delMAttPs.executeUpdate();
								delMAttPs.execute("SET SQL_SAFE_UPDATES = 1");
							} catch (SQLException exe) {
								logger.error("SQL Exception in updateTransactionCommandForNewTemplate delMAttQuery method "+exe.getMessage());
							}
							
							try (PreparedStatement delMAttPs = connection.prepareStatement(delMasFeatQuery)) {
								delMAttPs.setInt(1, Integer.parseInt(feture));
								delMAttPs.execute("SET SQL_SAFE_UPDATES = 0");
								delMAttPs.executeUpdate();
								delMAttPs.execute("SET SQL_SAFE_UPDATES = 1");
							} catch (SQLException exe) {
								logger.error("SQL Exception in updateTransactionCommandForNewTemplate delMasFeatQuery method "+exe.getMessage());
							}
							
							try (PreparedStatement delMAttPs = connection.prepareStatement(delMasComQuery)) {
								delMAttPs.setInt(1, Integer.parseInt(feture));
								delMAttPs.execute("SET SQL_SAFE_UPDATES = 0");
								delMAttPs.executeUpdate();
								delMAttPs.execute("SET SQL_SAFE_UPDATES = 1");
							} catch (SQLException exe) {
								logger.error("SQL Exception in updateTransactionCommandForNewTemplate delMasComQuery method "+exe.getMessage());
							}
						}
					}

				}
			} catch (SQLException exe) {
				logger.error("SQL Exception in updateTransactionCommandForNewTemplate temMastFeatureQuery method "+exe.getMessage());
			}finally {
				DBUtil.close(rs1);
			}
		} catch (Exception exe) {
			logger.error("Exception in updateTransactionCommandForNewTemplate method "+exe.getMessage());		
		}
		return idToSetInCommandTable;
	}

	public int updateTransactionFeatureForNewTemplate(AddNewFeatureTemplateMngmntPojo addNewFeatureTemplateMngmntPojo) {	
		int idToSetInCommandTable = 0;
		String query1 = "Insert into c3p_template_transaction_feature_list (id,command_feature_template_id) values (?,?)";
		for (int i = 0; i < addNewFeatureTemplateMngmntPojo.getCmdList().size(); i++) {
			CommandPojo commandPojo = addNewFeatureTemplateMngmntPojo.getCmdList().get(i);
			try (Connection connection = ConnectionFactory.getConnection();
					PreparedStatement preparedStmt = connection.prepareStatement(query1);){
					preparedStmt.setString(1, commandPojo.getId());
					preparedStmt.setString(2, addNewFeatureTemplateMngmntPojo.getTemplateid());
					preparedStmt.executeUpdate();
			} catch (SQLException exe) {
				logger.error("SQL Exception in updateTransactionFeatureForNewTemplate method "+exe.getMessage());
			}
		}		
		return idToSetInCommandTable;
	}

	public List<GetTemplateMngmntActiveDataPojo> getDataForRightPanelOnEdit(String templateId, boolean selectAll)
			throws SQLException {
		GetTemplateMngmntActiveDataPojo getTemplateMngmntActiveDataPojo = null;
		List<GetTemplateMngmntActiveDataPojo> dataList = new ArrayList<GetTemplateMngmntActiveDataPojo>();
		ResultSet rs1 = null;
		String query1 = "select cmdlist.command_value,cmdlist.command_sequence_id,flist.check_default,flist.hasParent,flist.id from c3p_template_master_command_list cmdlist ,c3p_template_master_feature_list flist where cmdlist.command_id=flist.id and flist.command_type=? order by cmdlist.position";
		try(Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query1);){
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
		} finally {
			DBUtil.close(rs1);
		}
		return dataList;
	}

	public List<GetTemplateMngmntActiveDataPojo> getRightPanelOnEditTemplate(String templateId, boolean selectAll)
			throws SQLException {
		GetTemplateMngmntActiveDataPojo getTemplateMngmntActiveDataPojo = null;
		List<GetTemplateMngmntActiveDataPojo> dataList = new ArrayList<GetTemplateMngmntActiveDataPojo>();
		ResultSet rs1 = null;
		String query1 = "select txnList.command_id,txnList.command_sequence_id,masterList.command_value from c3p_template_transaction_command_list txnList,c3p_template_master_command_list masterList where txnList.command_id=masterList.command_id and txnList.command_template_id=? order by txnList.command_position";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query1);){
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
		} finally {
			DBUtil.close(rs1);			
		}
		return dataList;
	}

	public boolean checkTemplateVersionAlredyexist(String templateAndVersion) {
		ResultSet rs1 = null;
		boolean isAlredyPresent = false;
		String query1 = "SELECT * FROM c3p_template_transaction_command_list where command_template_id = ?";
		try(Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query1);){
			preparedStmt.setString(1, templateAndVersion);
			rs1 = preparedStmt.executeQuery();
			if (rs1.next()) {
				isAlredyPresent = true;
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in checkTemplateVersionAlredyexist method "+exe.getMessage());
		} finally {
			DBUtil.close(rs1);
		}
		return isAlredyPresent;
	}
}
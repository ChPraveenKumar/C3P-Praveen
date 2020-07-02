package com.techm.orion.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.techm.orion.connection.ConnectionFactory;
import com.techm.orion.connection.DBUtil;
import com.techm.orion.entitybeans.BasicConfiguration;
import com.techm.orion.exception.DuplicateDataException;
import com.techm.orion.models.TemplateCommandJSONModel;
import com.techm.orion.models.TemplateLeftPanelJSONModel;
import com.techm.orion.pojo.CommandPojo;
import com.techm.orion.pojo.DeviceDetailsPojo;
import com.techm.orion.pojo.GetTemplateMngmntActiveDataPojo;
import com.techm.orion.pojo.Global;
import com.techm.orion.pojo.RequestInfoSO;
import com.techm.orion.pojo.TemplateBasicConfigurationPojo;

public class TemplateManagementDao {
	private static final Logger logger = LogManager.getLogger(TemplateManagementDao.class);
	private Connection connection;
	Statement statement;

	public boolean updateMasterFeatureAndCommandTable(String series) {
		boolean result = false;
		connection = ConnectionFactory.getConnection();
		String query1 = "select * from c3p_template_master_feature_list WHERE command_type=?";
		String query2 = "Insert into c3p_template_master_feature_list SET comand_display_feature=?, command_parent_feature=?, command_type=?, hasParent=?,isMandate=?,check_default=?,is_Save=?";
		String query3 = "Insert into c3p_template_master_command_list SET command_id=?, command_value=?, command_sequence_id=?, command_type=?";
		String query4 = "select id from t_tpmgmt_m_series where series=?";
		String query5 = "select * from t_tpmgmt_m_basic_configuration where series_id=?";
		String query6 = "delete from c3p_template_master_feature_list where command_type=?";
		String query7 = "Select id from c3p_template_master_feature_list where command_type=?";
		PreparedStatement pst;
		ResultSet res;
		try {
			pst = connection.prepareStatement(query1);
			pst.setString(1, "Generic_" + series);
			res = pst.executeQuery();
			while (res.next()) {
				result = true;
				pst.close();
				break;
			}
			if (!result) {
				// no basic config for this series exists go ahead and add the same
				pst = connection.prepareStatement(query2);
				pst.setString(1, "Basic Config1");
				pst.setString(2, "Basic Configuration");
				pst.setString(3, "Generic_" + series);
				pst.setInt(4, 1);
				pst.setInt(5, 1);
				pst.setInt(6, 1);
				pst.setNull(7, java.sql.Types.INTEGER);

				int res1 = pst.executeUpdate();
				if (res1 == 0) {
					result = false;
					pst.close();

				} else {
					result = true;
					pst.close();

					// now we go and add commands for ths basic config set to master command table

					// find out series if first
					int series_id = 0;
					pst = connection.prepareStatement(query4);
					pst.setString(1, series);
					res = pst.executeQuery();
					while (res.next()) {
						series_id = res.getInt("id");
					}
					pst.close();
					res.close();
					// now fetch and store all the commands from t_tpmgmt_m_basic_configuration
					List<BasicConfiguration> basicConfigSet = new ArrayList<BasicConfiguration>();
					pst = connection.prepareStatement(query5);
					pst.setInt(1, series_id);
					res = pst.executeQuery();
					while (res.next()) {
						BasicConfiguration configObj = new BasicConfiguration();
						configObj.setConfiguration(res.getString("configuration"));
						configObj.setSequence_id(res.getInt("sequence_id"));
						basicConfigSet.add(configObj);
					}
					pst.close();
					res.close();
					// find corresponding command_id from master feature table
					int command_id = 0;
					pst = connection.prepareStatement(query7);
					pst.setString(1, "Generic_" + series);
					res = pst.executeQuery();
					while (res.next()) {
						command_id = res.getInt("id");
					}
					pst.close();
					res.close();
					if (basicConfigSet.size() > 0) {
						for (int i = 0; i < basicConfigSet.size(); i++) {
							pst = connection.prepareStatement(query3);
							pst.setString(1, Integer.toString(command_id));
							pst.setString(2, basicConfigSet.get(i).getConfiguration() + "\n");
							pst.setInt(3, basicConfigSet.get(i).getSequence_id());
							pst.setString(4, "Generic_" + series);
							pst.executeUpdate();

						}
						result = true;
					} else {
						result = false;
						// roll back and delete the basic config feature set entry
						pst = connection.prepareStatement(query6);
						pst.setString(1, "Generic_" + series);
						pst.executeUpdate();

					}
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			DBUtil.close(connection);
		}
		// first check if basicConfig for series is already added or not

		return result;
	}

	public List<String> getParentFeatureList() {
		List<String> list = new ArrayList<String>();
		connection = ConnectionFactory.getConnection();
		String query2 = "Select distinct command_parent_feature from c3p_template_master_feature_list";
		try {
			Statement pst = connection.createStatement();
			ResultSet res = pst.executeQuery(query2);
			while (res.next()) {
				if (!res.getString("command_parent_feature").equalsIgnoreCase("Basic Configuration")) {
					list.add(res.getString("command_parent_feature"));
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			DBUtil.close(connection);
		}
		return list;
	}

	/* pankaj changes For Dynamic display of parent feature */
	public List<String> getParentFeatureList(String templateid) {
		List<String> parentList = new ArrayList<String>();
		parentList.add(0, "Add New Feature");
		connection = ConnectionFactory.getConnection();
		String query2 = "Select distinct command_parent_feature from c3p_template_master_feature_list WHERE command_type LIKE ?";
		try {
			PreparedStatement pst = connection.prepareStatement(query2);
			pst.setString(1, "%" + templateid + "%");
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				parentList.add(rs.getString("command_parent_feature"));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			DBUtil.close(connection);
		}
		return parentList;
	}

	public void updateReadFlagForTemplate(String templateid, String version, String status) {
		connection = ConnectionFactory.getConnection();
		String query2 = null;
		if (Global.loggedInUser.equalsIgnoreCase("Admin")) {
			query2 = "UPDATE templateconfig_basic_details SET readStatusAdmin=? WHERE TempId=? and templateVersion=?";
		} else if (Global.loggedInUser.equalsIgnoreCase("suser")) {
			query2 = "UPDATE templateconfig_basic_details SET readStatusApprover=? WHERE TempId=? and templateVersion=?";

		} else {
			query2 = "UPDATE templateconfig_basic_details SET readStatusAdmin=? WHERE TempId=? and templateVersion=?";

		}
		try {
			PreparedStatement pst = connection.prepareStatement(query2);
			pst.setInt(1, Integer.parseInt(status));
			pst.setString(2, templateid);
			pst.setString(3, version);
			pst.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(connection);

		}

	}

	public boolean getTemplateStatus(String templateid, String version) {
		connection = ConnectionFactory.getConnection();
		boolean result = false;
		String status = null;
		String query2 = "select * from  templateconfig_basic_details where TempId = ? and templateVersion = ?";
		try {
			PreparedStatement pst = connection.prepareStatement(query2);
			pst.setString(1, templateid);
			pst.setString(2, version);
			ResultSet res = pst.executeQuery();
			while (res.next()) {
				status = res.getString("templateStatus");
			}
			if (status.equalsIgnoreCase("Rejected") || status.equalsIgnoreCase("Pending")) {
				result = false;
			} else {
				result = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(connection);

		}
		return result;
	}

	public String getUserTaskIdForTemplate(String templateid, String version) {
		String userTaskid = null;
		connection = ConnectionFactory.getConnection();
		String query2 = "select * from  camundahistory where history_requestId=? and history_versionId=?";
		try {
			PreparedStatement pst = connection.prepareStatement(query2);
			pst.setString(1, templateid);
			pst.setString(2, version);
			ResultSet res = pst.executeQuery();
			while (res.next()) {
				userTaskid = res.getString("history_userTaskId");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(connection);
		}
		return userTaskid;
	}

	public int updateTemplateStatus(String templateid, String version, String status, String approverCommet) {
		int res = 0;
		connection = ConnectionFactory.getConnection();
		String query2 = "update templateConfig_basic_details set templateStatus = ?, comment_section= concat(?, comment_section), updatedDate=? where TempId = ? and templateVersion = ? ";
		try {

			java.util.Date dt = new java.util.Date();

			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String currentTime = sdf.format(dt);

			PreparedStatement pst = connection.prepareStatement(query2);
			pst.setString(1, status);
			if (!approverCommet.isEmpty()) {
				pst.setString(2, approverCommet + ".\n");
			} else {
				pst.setString(2, "");
			}
			pst.setString(3, currentTime);
			pst.setString(4, templateid);
			pst.setString(5, version);
			res = pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(connection);
		}
		return res;
	}

	public List<TemplateBasicConfigurationPojo> getTemplatesForApprovalForLoggedInUser(String username) {
		logger.info("in Approval: get template list");
		List<TemplateBasicConfigurationPojo> list = new ArrayList<TemplateBasicConfigurationPojo>();
		TemplateBasicConfigurationPojo pojo;
		connection = ConnectionFactory.getConnection();
		String query2 = null;
		if (username.equalsIgnoreCase("Admin")) {
			query2 = "SELECT * FROM templateconfig_basic_details where templateStatus=? or templateStatus=?";
		} else if (username.equalsIgnoreCase("suser")) {
			query2 = "SELECT * FROM templateconfig_basic_details where templateStatus=?";
		}
		try {
			PreparedStatement pst = connection.prepareStatement(query2);
			if (username.equalsIgnoreCase("Admin")) {
				pst.setString(1, "Approved");
				pst.setString(2, "Rejected");
			} else if (username.equalsIgnoreCase("suser")) {
				pst.setString(1, "Pending");
			}
			ResultSet rs1 = pst.executeQuery();
			while (rs1.next()) {
				pojo = new TemplateBasicConfigurationPojo();
				pojo.setVendor(rs1.getString("TempVendor"));
				pojo.setModel(rs1.getString("TempModel"));
				pojo.setDeviceType(rs1.getString("TempDeviceType"));
				pojo.setDeviceOs(rs1.getString("TempDeviceOs"));
				pojo.setOsVersion(rs1.getString("TempOsVersion"));
				pojo.setRegion(rs1.getString("TempRegion"));
				pojo.setTemplateId(rs1.getString("TempId"));
				Timestamp d = rs1.getTimestamp("createdDate");
				pojo.setDate(covnertTStoString(d));
				pojo.setVersion(rs1.getString("templateVersion"));
				Timestamp d1 = rs1.getTimestamp("updatedDate");
				pojo.setUpdatedDate(covnertTStoString(d1));
				pojo.setComment(rs1.getString("comment_section"));
				if (pojo.getComment().isEmpty()) {
					pojo.setComment("undefined");
				}
				pojo.setStatus(rs1.getString("templateStatus"));
				pojo.setApprover(rs1.getString("templateApprover"));
				pojo.setCreatedBy(rs1.getString("createdby"));

				if (username.equalsIgnoreCase("Admin")) {
					pojo.setRead(rs1.getInt("readStatusAdmin"));
				} else if (username.equalsIgnoreCase("suser")) {
					pojo.setRead(rs1.getInt("readStatusApprover"));

				}

				if (rs1.getString("templateStatus").equalsIgnoreCase("Approved")
						|| rs1.getString("templateStatus").equalsIgnoreCase("Rejected")) {
					pojo.setEditable(false);
				} else {
					pojo.setEditable(true);
				}
				list.add(pojo);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(connection);
		}
		return list;

	}

	public int getNumberOfTemplatesForApprovalForLoggedInUser(String username) {
		int number = 0;
		connection = ConnectionFactory.getConnection();
		ResultSet rs1 = null;
		String query1 = null;
		logger.info("in Approval-template number");
		if (username.equalsIgnoreCase("suser")) {
			query1 = "select count(TempId) as notifications from templateconfig_basic_details where  templateStatus=?  and readStatusApprover=?";
		} else if (username.equalsIgnoreCase("Admin")) {
			query1 = "select count(TempId) as notifications from templateconfig_basic_details where templateStatus IN (?,?) and readStatusAdmin=?";
		}
		PreparedStatement preparedStmt = null;

		try {
			preparedStmt = connection.prepareStatement(query1);
			if (username.equalsIgnoreCase("suser")) {
				preparedStmt.setString(1, "Pending");
				preparedStmt.setInt(2, 0);
			} else if (username.equalsIgnoreCase("Admin")) {
				preparedStmt.setString(1, "Approved");
				preparedStmt.setString(2, "Rejected");
				preparedStmt.setInt(3, 0);
			}

			rs1 = preparedStmt.executeQuery();
			while (rs1.next()) {
				number = rs1.getInt("notifications");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(connection);
		}
		return number;
	}

	public boolean saveTemperorySequence(String templateId, String versionToSave) {
		boolean res = false;
		connection = ConnectionFactory.getConnection();

		boolean result = false;

		boolean isNewFeature = false;
		// save new command in c3p_template_master_feature_list
		// update the new command in c3p_template_transaction_feature_list
		// save new command in c3p_template_master_command_list along with
		// command sequence id
		// update the same command in c3p_template_transaction_command_list
		boolean updateCommandTables = false;
		int idToSetInCommandTable = 0;
		String commandId = null, commandValue = null;
		int sequenceId = 0;
		boolean updateInMasterCommandTable = false;

		String name = null, parent = null;
		try {
			for (int i = 0; i < Global.globalSessionLeftPanel.size(); i++) {
				if (Global.globalSessionLeftPanel.get(i).getId().equalsIgnoreCase("NewParent")) {
					isNewFeature = true;
					name = Global.globalSessionLeftPanel.get(i).getName();
					parent = Global.globalSessionLeftPanel.get(i).getName();
					idToSetInCommandTable = updateFeatureTablesForNewCommand(name, parent, templateId);

					for (int i1 = 0; i1 < Global.globalSessionRightPanel.size(); i1++) {
						for (int j = 0; j < Global.globalSessionRightPanel.get(i1).getList().size(); j++) {
							if (Global.globalSessionRightPanel.get(i1).getList().get(j).isNew()
									&& Global.globalSessionRightPanel.get(i1).getList().get(j).getCommand_value()
											.equalsIgnoreCase(Global.globalSessionLeftPanel.get(i).getConfText())) {

								commandId = Integer.toString(idToSetInCommandTable);
								sequenceId = Global.globalSessionRightPanel.get(i1).getList().get(j)
										.getCommand_sequence_id();
								commandValue = Global.globalSessionRightPanel.get(i1).getList().get(j)
										.getCommand_value();
								updateInMasterCommandTable = updateMasterCommandTableWithNewCommand(commandId,
										sequenceId, commandValue);

							}
						}
					}

					if (idToSetInCommandTable != 0) {
						updateCommandTables = true;
						name = null;
					}
				}
			}
			if (name == null) {
				for (int i = 0; i < Global.globalSessionLeftPanel.size(); i++) {
					for (int j = 0; j < Global.globalSessionLeftPanel.get(i).getChildList().size(); j++) {
						if (Global.globalSessionLeftPanel.get(i).getChildList().get(j).getId()
								.equalsIgnoreCase("NewChild")) {
							name = Global.globalSessionLeftPanel.get(i).getChildList().get(j).getName();
							parent = Global.globalSessionLeftPanel.get(i).getChildList().get(j).getParent();
							idToSetInCommandTable = updateFeatureTablesForNewCommand(name, parent, templateId);

							for (int i1 = 0; i1 < Global.globalSessionRightPanel.size(); i1++) {
								for (int j1 = 0; j1 < Global.globalSessionRightPanel.get(i1).getList().size(); j1++) {
									if (Global.globalSessionRightPanel.get(i1).getList().get(j1).isNew()
											&& Global.globalSessionRightPanel.get(i1).getList().get(j1)
													.getCommand_value().equalsIgnoreCase(Global.globalSessionLeftPanel
															.get(i).getChildList().get(j).getConfText())) {

										commandId = Integer.toString(idToSetInCommandTable);
										sequenceId = Global.globalSessionRightPanel.get(i1).getList().get(j1)
												.getCommand_sequence_id();
										commandValue = Global.globalSessionRightPanel.get(i1).getList().get(j1)
												.getCommand_value();
										updateInMasterCommandTable = updateMasterCommandTableWithNewCommand(commandId,
												sequenceId, commandValue);

									}
								}
							}

							if (idToSetInCommandTable != 0) {
								updateCommandTables = true;
							}
						}

					}

				}
			}

			/*
			 * if (updateCommandTables) { for (int i = 0; i <
			 * Global.globalSessionRightPanel.size(); i++) { for (int j = 0; j <
			 * Global.globalSessionRightPanel.get(i) .getList().size(); j++) { if
			 * (Global.globalSessionRightPanel.get(i).getList() .get(j).isNew()) {
			 * 
			 * commandId = Integer.toString(idToSetInCommandTable); sequenceId =
			 * Global.globalSessionRightPanel.get(i)
			 * .getList().get(j).getCommand_sequence_id(); commandValue =
			 * Global.globalSessionRightPanel .get(i).getList().get(j).getCommand_value();
			 * updateInMasterCommandTable =updateMasterCommandTableWithNewCommand(commandId,
			 * sequenceId, commandValue); Global.globalSessionRightPanel.get(i).getList()
			 * .get(j).setNew(false); } } }
			 * 
			 * }
			 */
			if (name == null) {
				updateInMasterCommandTable = true;
			}
			/*
			 * if (updateInMasterCommandTable) { result =
			 * updateTransactionCommandTable(templateId, versionToSave); } else {
			 * logger.info("Error in updating master command table"); }
			 */
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(connection);
		}

		return result;
	}

	public boolean updateTransactionCommandTable(String templateId, String version) {
		boolean result = false;
		String query1 = null, query2 = null;
		PreparedStatement preparedStmt = null;
		try {
			query1 = "DELETE FROM c3p_template_transaction_command_list WHERE command_template_id=?";
			preparedStmt = connection.prepareStatement(query1);
			if (version != null) {
				preparedStmt.setString(1,
						Global.templateid.substring(0, Global.templateid.indexOf("V") - 1) + "_V" + version);
			} else {
				preparedStmt.setString(1, Global.templateid);
			}
			preparedStmt.execute("SET FOREIGN_KEY_CHECKS=0");
			preparedStmt.execute("SET SQL_SAFE_UPDATES=0");
			int update = preparedStmt.executeUpdate();

			/*
			 * if (!isPresentCommandList) { for (int i = 0; i < listOfBlocks1.size(); i++) {
			 * String insertBatchQuery =
			 * "INSERT INTO c3p_template_transaction_command_list(command_id,command_sequence_id,command_template_id)"
			 * + "VALUES('" + listOfBlocks1.get(i).getCommandId() + "','" +
			 * listOfBlocks1.get(i).getCommandSequenceId() + "','" + tempID + "')";
			 * ps.addBatch(insertBatchQuery); } recordsAffected2 = ps.executeBatch();
			 */

			query2 = "Insert into c3p_template_transaction_command_list(command_id,command_sequence_id,command_template_id) values (?,?,?)";
			preparedStmt = connection.prepareStatement(query2);

			for (int i = 0; i < Global.globalSessionRightPanel.size(); i++) {
				for (int j = 0; j < Global.globalSessionRightPanel.get(i).getList().size(); j++) {
					preparedStmt.setString(1, Integer.toString(Global.globalSessionRightPanel.get(i).getCommand_id()));
					preparedStmt.setInt(2,
							Global.globalSessionRightPanel.get(i).getList().get(j).getCommand_sequence_id());
					if (version != null) {
						preparedStmt.setString(3,
								Global.templateid.substring(0, Global.templateid.indexOf("V") - 1) + "_V" + version);
					} else {
						preparedStmt.setString(3, Global.templateid);
					}
					int rsres = preparedStmt.executeUpdate();
					if (rsres > 0) {
						result = true;
					}
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(connection);
		}
		return result;
	}

	public boolean updateMasterCommandTableWithNewCommand(String commandId, int sequenceId, String commandValue) {
		boolean result = false;
		String query1 = null, query2 = null, query3 = null, query4 = null;
		PreparedStatement preparedStmt = null;
		Statement smt = null;
		ResultSet rs1 = null, rs2 = null;
		try {
			query1 = "Insert into c3p_template_master_command_list (command_id,command_value,command_sequence_id,command_type) values (?,?,?,?)";
			preparedStmt = connection.prepareStatement(query1);
			preparedStmt.setString(1, commandId);
			preparedStmt.setString(2, commandValue);
			preparedStmt.setInt(3, sequenceId);
			preparedStmt.setString(4, Global.templateid);

			int update = preparedStmt.executeUpdate();
			if (update > 0) {
				result = true;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(connection);
		}
		return result;
	}

	public int updateFeatureTablesForNewCommand(String name, String parent, String templateId) {
		String query1 = null, query2 = null, query3 = null, query4 = null;
		PreparedStatement preparedStmt = null;
		Statement smt = null;
		ResultSet rs1 = null, rs2 = null;
		int idToSetInCommandTable = 0;
		boolean result = false;
		try {
			query1 = "Insert into c3p_template_master_feature_list(comand_display_feature,command_parent_feature,command_type,hasParent) values(?,?,?,?)";
			preparedStmt = connection.prepareStatement(query1);
			preparedStmt.setString(1, name);
			preparedStmt.setString(2, parent);
			preparedStmt.setString(3, Global.templateid);
			if (name.equalsIgnoreCase(parent)) {
				preparedStmt.setInt(4, 0);
			} else {
				preparedStmt.setInt(4, 1);

			}
			int updateMasterFeatureTable = preparedStmt.executeUpdate();
			if (updateMasterFeatureTable > 0) {
				int id = 0;
				query3 = "select * from c3p_template_master_feature_list";
				smt = connection.createStatement();
				rs1 = smt.executeQuery(query3);
				while (rs1.next()) {
					if (rs1.getString("comand_display_feature").equalsIgnoreCase(name)) {
						id = rs1.getInt("id");
						idToSetInCommandTable = id;
					}
				}
				// now update transaction feature table
				query2 = "Insert into c3p_template_transaction_feature_list (id,command_feature_template_id) values (?,?)";
				preparedStmt = connection.prepareStatement(query2);
				preparedStmt.setInt(1, id);
				preparedStmt.setString(2, Global.templateid);

				int updateTransactionFeatureTable = preparedStmt.executeUpdate();
				if (updateTransactionFeatureTable > 0) {
					result = true;
				} else {
					result = false;
					logger.info("Error updating transaction feature table");
				}

			} else {
				result = false;
				logger.info("error updating master feature table");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(connection);
		}
		return idToSetInCommandTable;

	}

	public boolean createTemperorySequence(String templateId, String comand_display_feature, String command_to_add,
			String command_type, int parentId, int topLineNum, int bottomLineNum, boolean dragged, int hasParent,
			String newFeature, String lstCmdId) {
		boolean res = true;
		connection = ConnectionFactory.getConnection();
		ResultSet rs1 = null, rs2 = null;
		String query1 = null, query2 = null, query3 = null;
		PreparedStatement preparedStmt = null;
		Statement smt = null;
		String result = null;
		List<GetTemplateMngmntActiveDataPojo> allFeaturesList = null;
		List<String> sequenceIds = null;
		List<CommandPojo> sequence = new ArrayList<CommandPojo>();
		CommandPojo commandPojo = null;
		List<Integer> inListSequence = new ArrayList<Integer>();
		GetTemplateMngmntActiveDataPojo getTemplateMngmntActivePojo = null;
		int tempCommandId = 0;
		int topLineToUse;
		// Global.sequenceList.clear();
		try {
			if (newFeature.equalsIgnoreCase("true")) {
				topLineToUse = Integer.parseInt(lstCmdId);
			} else {
				topLineToUse = topLineNum;
			}
			if (Global.sequenceList.size() == 0) {
				query1 = "select * from c3p_template_transaction_command_list where command_template_id=?";
				preparedStmt = connection.prepareStatement(query1);
				preparedStmt.setString(1, templateId);
				rs1 = preparedStmt.executeQuery();

				while (rs1.next()) {
					Global.sequenceList.add(rs1.getInt("command_sequence_id"));
				}
				for (int i = 0; i < Global.sequenceList.size(); i++) {
					if (Global.sequenceList.get(i) == topLineToUse) {
						tempCommandId = Global.sequenceList.size() + 1;
						Global.sequenceList.add(i + 1, Global.sequenceList.size() + 1);
						break;
					}
				}

			} else {
				// edit the exsisting sequence
				for (int i = 0; i < Global.sequenceList.size(); i++) {
					if (Global.sequenceList.get(i) == topLineToUse) {
						tempCommandId = Global.sequenceList.size() + 1;
						Global.sequenceList.add(i + 1, Global.sequenceList.size() + 1);
						break;
					}
				}
			}
			List<TemplateLeftPanelJSONModel> tempChildList = null;

			// Add in temperory left panel list
			if (!newFeature.equalsIgnoreCase("true")) {
				if (hasParent == 1) {
					for (int i = 0; i < Global.globalSessionLeftPanel.size(); i++) {
						if (!Global.globalSessionLeftPanel.get(i).getId().equalsIgnoreCase("NewParent")
								&& !Global.globalSessionLeftPanel.get(i).getId().equalsIgnoreCase("NewChild")) {
							if (Integer.parseInt(Global.globalSessionLeftPanel.get(i).getId()) == parentId) {
								tempChildList = new ArrayList<TemplateLeftPanelJSONModel>();
								tempChildList = Global.globalSessionLeftPanel.get(i).getChildList();
								TemplateLeftPanelJSONModel obj = new TemplateLeftPanelJSONModel();
								obj.setId("NewChild");
								obj.setMandatory(false);
								obj.setName(comand_display_feature.substring(10, comand_display_feature.length()));
								obj.setDisabled(false);
								obj.setConfText(command_to_add);
								obj.setParent(Global.globalSessionLeftPanel.get(i).getName());
								tempChildList.add(obj);
								Global.globalSessionLeftPanel.get(i).setChildList(tempChildList);
							}
						}
					}
				} else {
					for (int i = 0; i < Global.globalSessionLeftPanel.size(); i++) {

						TemplateLeftPanelJSONModel obj = new TemplateLeftPanelJSONModel();
						obj.setId("NewParent");
						obj.setMandatory(false);
						obj.setName(comand_display_feature.substring(10, comand_display_feature.length()));
						obj.setDisabled(false);
						obj.setParent(comand_display_feature.substring(10, comand_display_feature.length()));
						obj.setConfText(command_to_add);
						Global.globalSessionLeftPanel.add(obj);
					}
				}
			} else {

				TemplateLeftPanelJSONModel obj = new TemplateLeftPanelJSONModel();
				obj.setId("NewParent");
				obj.setMandatory(false);
				obj.setName(comand_display_feature.substring(10, comand_display_feature.length()));
				obj.setDisabled(false);
				obj.setConfText(command_to_add);
				obj.setParent(comand_display_feature.substring(10, comand_display_feature.length()));
				Global.globalSessionLeftPanel.add(obj);
			}
			// Add in temperory rightpanel list
			/*
			 * if (newFeature.equalsIgnoreCase("true")) { TemplateCommandJSONModel
			 * commandObj = new TemplateCommandJSONModel(); commandObj.setCommand_id(0);
			 * commandObj.setChecked(true); List<CommandPojo> list = new
			 * ArrayList<CommandPojo>();
			 * 
			 * CommandPojo cmdPojo = new CommandPojo(); cmdPojo.setChecked(false);
			 * cmdPojo.setCommand_value(command_to_add);
			 * cmdPojo.setCommand_sequence_id(tempCommandId); cmdPojo.setNew(true);
			 * list.add(cmdPojo); commandObj.setList(list);
			 * 
			 * Global.globalSessionRightPanel.add(commandObj);
			 * 
			 * } else { for (int i = 0; i < Global.globalSessionRightPanel.size(); i++) {
			 * 
			 * if (Global.globalSessionRightPanel.get(i).getCommand_id() == parentId) {
			 * List<CommandPojo> list = new ArrayList<CommandPojo>(); list =
			 * Global.globalSessionRightPanel.get(i).getList();
			 * 
			 * CommandPojo cmdPojo = new CommandPojo(); cmdPojo.setChecked(false);
			 * cmdPojo.setCommand_value(command_to_add);
			 * cmdPojo.setCommand_sequence_id(tempCommandId); cmdPojo.setNew(true);
			 * list.add(cmdPojo);
			 * 
			 * Global.globalSessionRightPanel.get(i).setList(list); } } }
			 */
			for (int i = 0; i < Global.globalSessionRightPanel.size(); i++) {

				TemplateCommandJSONModel commandObj = new TemplateCommandJSONModel();
				commandObj.setCommand_id(0);
				commandObj.setChecked(true);
				List<CommandPojo> list = new ArrayList<CommandPojo>();

				CommandPojo cmdPojo = new CommandPojo();
				cmdPojo.setChecked(false);
				cmdPojo.setCommand_value(command_to_add);
				cmdPojo.setCommand_sequence_id(tempCommandId);
				cmdPojo.setNew(true);
				cmdPojo.setCommand_id(Integer.toString(parentId));
				list.add(cmdPojo);
				commandObj.setList(list);
				boolean flag = false;
				for (int j = 0; j < Global.globalSessionRightPanel.get(i).getList().size(); j++) {
					if (Global.globalSessionRightPanel.get(i).getList().get(j)
							.getCommand_sequence_id() == topLineToUse) {
						Global.globalSessionRightPanel.get(i).getList().add(j, cmdPojo);
						flag = true;
						break;
					}
				}

			}
			String s1 = new Gson().toJson(Global.globalSessionLeftPanel);
			String s2 = new Gson().toJson(Global.globalSessionRightPanel);
			logger.info("SystemOUTTEST");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(connection);
		}
		return res;

	}

	public String selectFeature(String request) {
		boolean res = false;
		connection = ConnectionFactory.getConnection();
		ResultSet rs1 = null;
		String query1 = null, query2 = null, query3 = null;
		PreparedStatement preparedStmt = null, pst = null;
		List<CommandPojo> commandList = new ArrayList<CommandPojo>();
		List<TemplateCommandJSONModel> model = null;
		CommandPojo commandPojo = null;
		String resultString = null;
		String templateIdTouse = null, oldTemplateId = null;
		String templateVersion = null;
		Double oldVersion = 0.0;
		String oldVersionS = null;
		DecimalFormat numberFormat = new DecimalFormat("#.0");

		Statement smt = null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject array = (JSONObject) parser.parse(request);
			JSONArray checkedArray = (JSONArray) array.get("checked");

			if (array.get("templateVersion") != null) {
				oldVersion = Double.parseDouble(array.get("templateVersion").toString()) - 0.1;
				oldVersionS = numberFormat.format(oldVersion);
				oldTemplateId = array.get("templateid").toString() + "_V" + oldVersionS;
				templateVersion = numberFormat.format(Double.parseDouble(array.get("templateVersion").toString()));
				templateIdTouse = array.get("templateid").toString() + "_V" + templateVersion;
			} else {
				templateIdTouse = array.get("templateid").toString().replace("-", "_");
			}
			model = new ArrayList<TemplateCommandJSONModel>();
			for (int k = 0; k < checkedArray.size(); k++) {

				JSONObject object = (JSONObject) checkedArray.get(k);
				TemplateCommandJSONModel modelObj = new TemplateCommandJSONModel();
				commandList = new ArrayList<CommandPojo>();
				if ((Boolean) object.get("checked")) {
					query3 = "select * from c3p_template_transaction_feature_list where command_feature_template_id=?";
					pst = connection.prepareStatement(query3);
					pst.setString(1, templateIdTouse);
					rs1 = pst.executeQuery();
					boolean isPresent = false;
					while (rs1.next()) {
						if (rs1.getInt("id") == Integer.parseInt(object.get("id").toString())) {
							isPresent = true;
							break;
						}
					}

					if (!isPresent) {
						query1 = "INSERT INTO c3p_template_transaction_feature_list(id,command_feature_template_id)"
								+ "VALUES(?,?)";
						preparedStmt = connection.prepareStatement(query1);
						preparedStmt.setInt(1, Integer.parseInt(object.get("id").toString()));
						preparedStmt.setString(2, templateIdTouse);
						// select command value and sequence number from command
						// master table for the selected feature***********
						int i = preparedStmt.executeUpdate();
						if (i == 1) {
							query2 = "Select * from c3p_template_master_command_list where command_id=?";
							pst = connection.prepareStatement(query2);
							pst.setInt(1, Integer.parseInt(object.get("id").toString()));
							// pst.setString(2, "Generic");
							/*
							 * if (array.get("templateVersion") != null) { pst.setString( 3, templateIdTouse
							 * .substring(0, templateIdTouse .indexOf("V") - 1) + "%"); } else {
							 * pst.setString(3, templateIdTouse);
							 * 
							 * }
							 */
							rs1 = pst.executeQuery();

							while (rs1.next()) {
								commandPojo = new CommandPojo();
								commandPojo.setCommand_id(rs1.getString("command_id"));
								commandPojo.setCommand_value(rs1.getString("command_value"));
								commandPojo.setCommand_sequence_id(rs1.getInt("command_sequence_id"));
								commandList.add(commandPojo);

							}
							modelObj.setCommand_id(Integer.parseInt(object.get("id").toString()));
							modelObj.setList(commandList);
						}
						model.add(modelObj);
					}
				} else {
					query3 = "select * from c3p_template_transaction_feature_list where command_feature_template_id=?";
					pst = connection.prepareStatement(query3);
					pst.setString(1, templateIdTouse);
					rs1 = pst.executeQuery();
					boolean isPresent = false;
					while (rs1.next()) {
						if (rs1.getInt("id") == Integer.parseInt(object.get("id").toString())) {
							isPresent = true;
							break;
						}
					}
					if (isPresent) {
						query1 = "DELETE FROM c3p_template_transaction_feature_list WHERE id=? AND command_feature_template_id=?";
						preparedStmt = connection.prepareStatement(query1);
						preparedStmt.setInt(1, Integer.parseInt(object.get("id").toString()));
						preparedStmt.setString(2, templateIdTouse);
						// select command value and sequence number from command
						// master table for the selected feature***********
						preparedStmt.execute("SET FOREIGN_KEY_CHECKS=0");
						preparedStmt.execute("SET SQL_SAFE_UPDATES=0");
						int i = preparedStmt.executeUpdate();
						if (i == 1) {
							/*
							 * query2 = "Select * from c3p_template_master_command_list where command_id=?"
							 * ; pst = connection.prepareStatement(query2); pst.setInt(1,
							 * Integer.parseInt(object.get("id") .toString()));
							 * 
							 * rs1 = pst.executeQuery();
							 * 
							 * while (rs1.next()) { commandPojo = new CommandPojo();
							 * commandPojo.setCommand_id(rs1 .getString("command_id"));
							 * commandPojo.setCommand_value(rs1 .getString("command_value"));
							 * commandPojo.setCommand_sequence_id(rs1 .getInt("command_sequence_id"));
							 * commandList.add(commandPojo);
							 */
							modelObj.setCommand_id(Integer.parseInt(object.get("id").toString()));
							modelObj.setList(commandList);
							model.add(modelObj);
						}

					}

					// }
				}
				JSONObject obj = (JSONObject) checkedArray.get(k);
				for (int i = 0; i < Global.globalSessionRightPanel.size(); i++) {
					for (int j = 0; j < model.size(); j++) {
						if (Global.globalSessionRightPanel.get(i).getCommand_id() == model.get(j).getCommand_id()) {
							if (Boolean.valueOf(obj.get("checked").toString())) {
								Global.globalSessionRightPanel.get(i).setChecked(true);
							} else {
								Global.globalSessionRightPanel.get(i).setChecked(false);

							}
						}
					}
				}
			}

			resultString = new Gson().toJson(model);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		finally {
			DBUtil.close(rs1);
			DBUtil.close(preparedStmt);
			DBUtil.close(connection);
		}
		return resultString;
	}

	public Map<String, String> getDataForRightPanel(String templateId, boolean selectAll) throws SQLException {
		Map<String, String> map = new HashMap<String, String>();
		GetTemplateMngmntActiveDataPojo getTemplateMngmntActivePojo = null;
		connection = ConnectionFactory.getConnection();
		ResultSet rs1 = null, rs2 = null;
		RequestInfoSO request = null;
		List<GetTemplateMngmntActiveDataPojo> allFeaturesList = null;
		List<String> sequenceIds = null;
		Statement statement = null;
		PreparedStatement preparedStmt = null;
		String query1 = null, query2 = null, query3 = null;
		List<CommandPojo> sequence = new ArrayList<CommandPojo>();
		CommandPojo commandPojo = null;
		List<Integer> inListSequence = new ArrayList<Integer>();
		List<TemplateCommandJSONModel> model = null;
		try {
			logger.info("In right panel");

			query1 = "select * from c3p_template_transaction_command_list where command_template_id=?";
			preparedStmt = connection.prepareStatement(query1);
			preparedStmt.setString(1, templateId);

			rs1 = preparedStmt.executeQuery();
			allFeaturesList = new ArrayList<GetTemplateMngmntActiveDataPojo>();
			while (rs1.next()) {
				inListSequence = new ArrayList<Integer>();
				if (sequence.size() == 0) {
					inListSequence.add(rs1.getInt("command_sequence_id"));
					getTemplateMngmntActivePojo = new GetTemplateMngmntActiveDataPojo();
					getTemplateMngmntActivePojo.setCommandId(rs1.getString("command_id"));
					getTemplateMngmntActivePojo.setSequenceIds(inListSequence);
					allFeaturesList.add(getTemplateMngmntActivePojo);
				} else {
					boolean isPresent = false;
					for (int i = 0; i < allFeaturesList.size(); i++) {
						if (allFeaturesList.get(i).getCommandId().equalsIgnoreCase(rs1.getString("command_id"))) {
							isPresent = true;
							break;
						}
					}
					if (isPresent) {
						for (int j = 0; j < allFeaturesList.size(); j++) {
							if (allFeaturesList.get(j).getCommandId().equalsIgnoreCase(rs1.getString("command_id"))) {
								inListSequence = allFeaturesList.get(j).getSequenceIds();
								inListSequence.add(rs1.getInt("command_sequence_id"));
								allFeaturesList.get(j).setSequenceIds(inListSequence);
							}
						}
					} else {
						inListSequence.add(rs1.getInt("command_sequence_id"));
						getTemplateMngmntActivePojo = new GetTemplateMngmntActiveDataPojo();
						getTemplateMngmntActivePojo.setCommandId(rs1.getString("command_id"));
						getTemplateMngmntActivePojo.setSequenceIds(inListSequence);
						allFeaturesList.add(getTemplateMngmntActivePojo);
					}
				}
				commandPojo = new CommandPojo();
				commandPojo.setCommand_sequence_id(rs1.getInt("command_sequence_id"));
				sequence.add(commandPojo);
			}

			for (int i = 0; i < sequence.size(); i++) {
				query2 = "select * from c3p_template_master_command_list where command_sequence_id=?";
				preparedStmt = connection.prepareStatement(query2);
				preparedStmt.setInt(1, sequence.get(i).getCommand_sequence_id());
				rs2 = preparedStmt.executeQuery();
				while (rs2.next()) {
					sequence.get(i).setCommand_id(Integer.toString(rs2.getInt("command_id")));
					sequence.get(i).setCommand_value(rs2.getString("command_value"));
				}
			}
			preparedStmt.close();
			List<String> transactionFeatureId = new ArrayList<String>();
			query3 = "select * from c3p_template_transaction_feature_list where command_feature_template_id=?";
			preparedStmt = connection.prepareStatement(query3);
			preparedStmt.setString(1, templateId);
			rs2 = preparedStmt.executeQuery();
			while (rs2.next()) {
				transactionFeatureId.add(Integer.toString(rs2.getInt("id")));
			}

			model = new ArrayList<TemplateCommandJSONModel>();
			TemplateCommandJSONModel modelPojo = null;
			List<CommandPojo> sequenceCopy = new ArrayList<CommandPojo>();
			for (int i = 0; i < sequence.size(); i++) {
				if (model.size() == 0) {
					modelPojo = new TemplateCommandJSONModel();
					modelPojo.setCommand_id(Integer.parseInt(sequence.get(i).getCommand_id()));
					List<CommandPojo> inList = new ArrayList<CommandPojo>();
					inList.add(sequence.get(i));
					modelPojo.setLastCmdId(sequence.get(i).getCommand_sequence_id());
					if (sequence.get(i).getCommand_id().equalsIgnoreCase("1")) {
						modelPojo.setChecked(true);
					} else if (sequence.get(i).getCommand_id().equalsIgnoreCase("10")) {
						modelPojo.setChecked(true);
					} else if (sequence.get(i).getCommand_id().equalsIgnoreCase("13")) {
						modelPojo.setChecked(true);
					} else if (selectAll) {
						modelPojo.setChecked(true);
					} else {
						for (int k = 0; k < transactionFeatureId.size(); k++) {
							if (transactionFeatureId.get(k).equalsIgnoreCase(sequence.get(i).getCommand_id())) {
								modelPojo.setChecked(true);

							}
						}
					}
					modelPojo.setList(inList);
					model.add(modelPojo);

				} else {
					boolean isPresent = false;
					for (int j = 0; j < model.size(); j++) {
						if (sequence.get(i).getCommand_id()
								.equalsIgnoreCase(Integer.toString(model.get(j).getCommand_id()))) {
							isPresent = true;
							break;
						}
					}
					if (isPresent) {
						for (int k = 0; k < model.size(); k++) {
							if (sequence.get(i).getCommand_id()
									.equalsIgnoreCase(Integer.toString(model.get(k).getCommand_id()))) {

								List<CommandPojo> inList = model.get(k).getList();
								inList.add(sequence.get(i));
								model.get(k).setList(inList);
								int lastCmdId = 0;
								for (int n = 0; n < model.get(k).getList().size(); n++) {
									lastCmdId = model.get(k).getList().get(n).getCommand_sequence_id();
								}
								model.get(k).setLastCmdId(lastCmdId);
							}
						}
					} else {
						modelPojo = new TemplateCommandJSONModel();
						modelPojo.setCommand_id(Integer.parseInt(sequence.get(i).getCommand_id()));
						List<CommandPojo> inList = new ArrayList<CommandPojo>();
						inList.add(sequence.get(i));
						modelPojo.setLastCmdId(sequence.get(i).getCommand_sequence_id());

						modelPojo.setList(inList);
						if (sequence.get(i).getCommand_id().equalsIgnoreCase("1")) {
							modelPojo.setChecked(true);
						} else if (sequence.get(i).getCommand_id().equalsIgnoreCase("10")) {
							modelPojo.setChecked(true);
						} else if (sequence.get(i).getCommand_id().equalsIgnoreCase("13")) {
							modelPojo.setChecked(true);
						} else if (selectAll) {
							modelPojo.setChecked(true);
						} else {
							for (int k = 0; k < transactionFeatureId.size(); k++) {
								if (transactionFeatureId.get(k).equalsIgnoreCase(sequence.get(i).getCommand_id())) {
									modelPojo.setChecked(true);

								}
							}
						}
						model.add(modelPojo);
					}
				}
			}
			if (Global.globalSessionRightPanel.size() > 0) {
				Global.globalSessionRightPanel.clear();
			}
			Global.globalSessionRightPanel = model;
		} finally {
			DBUtil.close(rs1);
			DBUtil.close(preparedStmt);
			DBUtil.close(connection);
		}
		String s = new Gson().toJson(allFeaturesList);
		String s1 = new Gson().toJson(sequence);
		String s2 = new Gson().toJson(model);
		map.put("list", s);
		map.put("sequence", s2);

		return map;
	}

	public List<GetTemplateMngmntActiveDataPojo> getDataForLeftPanel(String templateId, String tempKey,
			String currentTemplateId, boolean editable) throws SQLException {
		GetTemplateMngmntActiveDataPojo getTemplateMngmntActivePojo = null;
		connection = ConnectionFactory.getConnection();
		ResultSet rs1 = null, rs2 = null, rs3 = null;
		RequestInfoSO request = null;
		List<GetTemplateMngmntActiveDataPojo> allFeaturesList = null;
		Statement statement = null;
		PreparedStatement preparedStmt = null, preparedStmt1 = null, preparedStmt2 = null;
		String query1 = null, query2 = null, query3 = null;

		List<GetTemplateMngmntActiveDataPojo> finalFeaturesList = null;

		List<GetTemplateMngmntActiveDataPojo> getFinalList = null;

		try {
			/*
			 * Dhanshri Mane 14-1-2020 if any unsaved feture is assosiated with template and
			 * version id delete it from c3p_template_master_feature_list and
			 * c3p_template_master_command_list
			 */
			if (editable == true) {
				query3 = "select * from c3p_template_master_feature_list WHERE command_type =  ? and is_Save = '0'";
				preparedStmt2 = connection.prepareStatement(query3);
				preparedStmt2.setString(1, templateId);
			} else if (editable == false && currentTemplateId == null) {
				query3 = "select * from c3p_template_master_feature_list WHERE command_type =  ? and is_Save = '0'";
				preparedStmt2 = connection.prepareStatement(query3);
				preparedStmt2.setString(1, templateId);

			} else if (editable == false) {
				query3 = "select * from c3p_template_master_feature_list WHERE (command_type BETWEEN  ? AND ? ) and is_Save = '0'";
				preparedStmt2 = connection.prepareStatement(query3);
				preparedStmt2.setString(1, templateId);
				preparedStmt2.setString(2, currentTemplateId);

			}

			rs3 = preparedStmt2.executeQuery();
			while (rs3.next()) {
				int command_id = rs3.getInt("id");
				/* if feature is save =0 then delete all related table entry */
				String query6 = "delete from t_attrib_m_attribute where feature_id = ?";
				preparedStmt2 = connection.prepareStatement(query6);
				preparedStmt2.setString(1, String.valueOf(command_id));
				preparedStmt2.execute("SET SQL_SAFE_UPDATES = 0");
				preparedStmt2.executeUpdate();
				preparedStmt2.execute("SET SQL_SAFE_UPDATES = 1");

				String query7 = "delete from c3p_template_transaction_feature_list where id = ?";
				preparedStmt2 = connection.prepareStatement(query7);
				preparedStmt2.setString(1, String.valueOf(command_id));
				preparedStmt2.execute("SET SQL_SAFE_UPDATES = 0");
				preparedStmt2.executeUpdate();
				preparedStmt2.execute("SET SQL_SAFE_UPDATES = 1");

				String query5 = "delete from c3p_template_master_command_list where command_id = ?";
				preparedStmt2 = connection.prepareStatement(query5);
				preparedStmt2.setString(1, String.valueOf(command_id));
				preparedStmt2.execute("SET SQL_SAFE_UPDATES = 0");
				preparedStmt2.executeUpdate();
				preparedStmt2.execute("SET SQL_SAFE_UPDATES = 1");

				String query4 = "delete from c3p_template_master_feature_list WHERE id =?";
				preparedStmt2 = connection.prepareStatement(query4);
				preparedStmt2.setInt(1, command_id);
				preparedStmt2.execute("SET SQL_SAFE_UPDATES = 0");
				preparedStmt2.executeUpdate();
				preparedStmt2.execute("SET SQL_SAFE_UPDATES = 1");

			}

			logger.info("In left panel");
			// query1 = "select * from c3p_template_transaction_feature_list where
			// command_feature_template_id like?";
			query1 = "select * from c3p_template_transaction_feature_list where command_feature_template_id=?";
			preparedStmt1 = connection.prepareStatement(query1);
			preparedStmt1.setString(1, templateId);

			rs1 = preparedStmt1.executeQuery();
			allFeaturesList = new ArrayList<GetTemplateMngmntActiveDataPojo>();
			boolean isPresentInTransactionList = false;
			List<Integer> featuresActive = new ArrayList<Integer>();

			GetTemplateMngmntActiveDataPojo pojo = new GetTemplateMngmntActiveDataPojo();

			// query2 = "select * from c3p_template_master_feature_list WHERE command_type
			// LIKE ? or (command_type LIKE ? and is_Save = '1')";
			// query2 = "select * from c3p_template_master_feature_list WHERE command_type
			// LIKE ? or (command_type BETWEEN ? AND ? and is_Save = '1')";

			/* Dhanshri serach by Specific Template id with current template Id */
			query2 = "select * from c3p_template_master_feature_list WHERE command_type =? or (command_type BETWEEN  ? AND ? )";

			// pankaj changes for yes no and edit flow
			preparedStmt = connection.prepareStatement(query2);
			if (tempKey != null) {
				preparedStmt.setString(1, "Generic_" + tempKey);
			} else {
				preparedStmt.setString(1, "Generic");

			}
			/*
			 * preparedStmt.setString( 2, "%" + templateId.substring(0,
			 * templateId.indexOf("_V") - 1) + "%");
			 */
			/* Dhanshri serach by Specific Template id */
			preparedStmt.setString(2, templateId);
			// preparedStmt.setString(2, templateId.substring(0, templateId.indexOf("V") +
			// 1)+ 1.0);

			/* pankaj changes for yes no and edit flow */
			preparedStmt.setString(3, currentTemplateId);
			/* pankaj changes for yes no and edit flow */
			rs2 = preparedStmt.executeQuery();
			while (rs2.next()) {
				pojo = new GetTemplateMngmntActiveDataPojo();
				pojo.setParentKeyValue(rs2.getString("command_parent_feature"));
				pojo.setDisplayKeyValue(rs2.getString("comand_display_feature"));
				pojo.setId(rs2.getInt("id"));
				pojo.setDisabled(false);
				pojo.setHasParent(rs2.getInt("hasParent"));
				pojo.setCommandType(rs2.getString("command_type"));
				pojo.setActive(true);
				allFeaturesList.add(pojo);
			}
			while (rs1.next()) {
				isPresentInTransactionList = true;
				featuresActive.add(rs1.getInt("id"));
			}

			finalFeaturesList = new ArrayList<>();

			for (GetTemplateMngmntActiveDataPojo data : allFeaturesList) {
				if (data.getCommandType().equals(currentTemplateId)) {
					finalFeaturesList.add(data);
				}
			}
			getFinalList = new ArrayList<>(finalFeaturesList);
			for (GetTemplateMngmntActiveDataPojo data : allFeaturesList) {
				boolean flag = false;
				for (GetTemplateMngmntActiveDataPojo data1 : finalFeaturesList) {

					if (data.getDisplayKeyValue().equals(data1.getDisplayKeyValue())) {
						flag = true;
						break;

					}
				}
				if (!flag) {
					getFinalList.add(data);

				}
			}

			/*
			 * if (isPresentInTransactionList) { for (int i = 0; i < allFeaturesList.size();
			 * i++) { for (int j = 0; j < featuresActive.size(); j++) { if
			 * (allFeaturesList.get(i).getId() == featuresActive.get(j)) {
			 * allFeaturesList.get(i).setActive(true); } } } }
			 */

			for (int i = 0; i < getFinalList.size(); i++) {
				if (getFinalList.get(i).getDisplayKeyValue().equalsIgnoreCase("Basic Config1")) {
					getFinalList.get(i).setDisabled(true);
					getFinalList.get(i).setActive(true);
				} else if (getFinalList.get(i).getDisplayKeyValue().equalsIgnoreCase("Basic Config2")) {
					getFinalList.get(i).setDisabled(true);
					getFinalList.get(i).setActive(true);

				} else if (getFinalList.get(i).getDisplayKeyValue().equalsIgnoreCase("Basic Config3")) {
					getFinalList.get(i).setDisabled(true);
					getFinalList.get(i).setActive(true);

				}
			}

		} finally

		{
			DBUtil.close(rs1);
			DBUtil.close(rs2);
			DBUtil.close(preparedStmt);
			DBUtil.close(preparedStmt1);
			DBUtil.close(connection);
		}

		return getFinalList;
	}

	public List<GetTemplateMngmntActiveDataPojo> getDataForActivefeatures(String templateId) throws SQLException {
		GetTemplateMngmntActiveDataPojo getTemplateMngmntActivePojo = null;
		connection = ConnectionFactory.getConnection();
		ResultSet rs = null;
		RequestInfoSO request = null;
		List<GetTemplateMngmntActiveDataPojo> templateMngmntActiveList = null;
		PreparedStatement preparedStmt = null;
		String query = null;
		try {
			query = "select tempCmd.Name,tempCmd.Command_Value,tempactive.active,tempactive.TempId FROM templateconfig_feature_command tempCmd INNER JOIN templateconfig_feature_active tempactive on tempCmd.Name = tempactive.Feature_Selection where tempactive.TempId=?";
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, templateId);

			rs = preparedStmt.executeQuery();
			templateMngmntActiveList = new ArrayList<GetTemplateMngmntActiveDataPojo>();
			int id;
			while (rs.next()) {
				getTemplateMngmntActivePojo = new GetTemplateMngmntActiveDataPojo();
				getTemplateMngmntActivePojo.setChildKeyValue(rs.getString("tempCmd.Name"));
				getTemplateMngmntActivePojo.setCommandValue(rs.getString("tempCmd.Command_Value"));
				getTemplateMngmntActivePojo.setActive(rs.getBoolean("tempactive.active"));
				templateMngmntActiveList.add(getTemplateMngmntActivePojo);
			}

		} finally {
			DBUtil.close(rs);
			DBUtil.close(preparedStmt);
			DBUtil.close(connection);
		}

		return templateMngmntActiveList;
	}

	@SuppressWarnings("resource")
	public List<GetTemplateMngmntActiveDataPojo> getChildCommandValue(JSONArray names, JSONArray checked,
			String templateid) {
		Statement statement = null;
		Connection connection = ConnectionFactory.getConnection();

		String query = "";
		String query1 = "";
		String queryUpdate = "";
		ResultSet rs = null;
		ResultSet rs1 = null;
		PreparedStatement preparedStmt;
		List<GetTemplateMngmntActiveDataPojo> templateMngmntActiveList = null;
		GetTemplateMngmntActiveDataPojo getTemplateMngmntActivePojo = null;
		try {
			if (names.size() == checked.size()) {
				templateMngmntActiveList = new ArrayList<GetTemplateMngmntActiveDataPojo>();
				for (int i = 0; i < names.size(); i++) {
					if (checked.get(i).toString().equalsIgnoreCase("true"))

					{
						query = "select * from  templateconfig_feature_command where Name = ? ";
						preparedStmt = connection.prepareStatement(query);
						preparedStmt.setString(1, names.get(i).toString());
						// preparedStmt.setString(2, parentValue);
						rs = preparedStmt.executeQuery();
						if (rs != null) {
							while (rs.next()) {
								getTemplateMngmntActivePojo = new GetTemplateMngmntActiveDataPojo();
								getTemplateMngmntActivePojo.setChildKeyValue(rs.getString("Name"));
								getTemplateMngmntActivePojo.setCommandValue(rs.getString("Command_Value"));
								templateMngmntActiveList.add(getTemplateMngmntActivePojo);

							}
						}
					}

				}

				for (int j = 0; j < names.size(); j++) {
					if (checked.get(j).toString().equalsIgnoreCase("true")) {
						queryUpdate = "update templateconfig_feature_active set Active = true where TempId = ? and Feature_Selection = ? ";
						try {
							PreparedStatement preparedStatement = connection.prepareStatement(queryUpdate);

							preparedStatement.setString(1, templateid);
							preparedStatement.setString(2, names.get(j).toString());
							preparedStatement.executeUpdate();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							logger.info("Got an exception! ");
							logger.error(e.getMessage());
						}
					} else {
						queryUpdate = "update templateconfig_feature_active set Active = false where TempId = ? and Feature_Selection = ? ";
						try {
							PreparedStatement preparedStatement = connection.prepareStatement(queryUpdate);

							preparedStatement.setString(1, templateid);
							preparedStatement.setString(2, names.get(j).toString());
							preparedStatement.executeUpdate();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							logger.info("Got an exception! ");
							logger.error(e.getMessage());
						}
					}
				}
			}
		}

		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("Got an exception! ");
			logger.error(e.getMessage());
		} finally {
			DBUtil.close(connection);
		}
		return templateMngmntActiveList;
	}

	@SuppressWarnings("resource")
	public List<GetTemplateMngmntActiveDataPojo> getCommandValue(String Value, String templateId) {
		Statement statement = null;
		Connection connection = ConnectionFactory.getConnection();

		String query = "";
		String query1 = "";
		String queryUpdate = "";
		ResultSet rs = null;
		ResultSet rs1 = null;
		PreparedStatement preparedStmt;
		List<GetTemplateMngmntActiveDataPojo> templateMngmntActiveList = null;
		GetTemplateMngmntActiveDataPojo getTemplateMngmntActivePojo = null;
		try {
			/*
			 * if(!Value.equalsIgnoreCase("Routing Basic Configuration")) {
			 */
			query = "select * from  templateconfig_feature_command where Parent_name = ? ";
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, Value);
			// preparedStmt.setString(2, parentValue);
			rs = preparedStmt.executeQuery();
			int i = 0;
			templateMngmntActiveList = new ArrayList<GetTemplateMngmntActiveDataPojo>();
			if (rs != null) {
				while (rs.next()) {
					i++;
					getTemplateMngmntActivePojo = new GetTemplateMngmntActiveDataPojo();
					getTemplateMngmntActivePojo.setChildKeyValue(rs.getString("Name"));
					getTemplateMngmntActivePojo.setCommandValue(rs.getString("Command_Value"));
					templateMngmntActiveList.add(getTemplateMngmntActivePojo);

				}
			}

			if (i == 0) {
				query1 = "select * from  templateconfig_feature_command where Name = ? ";
				preparedStmt = connection.prepareStatement(query1);
				preparedStmt.setString(1, Value);
				// preparedStmt.setString(2, parentValue);
				rs1 = preparedStmt.executeQuery();
				templateMngmntActiveList = new ArrayList<GetTemplateMngmntActiveDataPojo>();
				if (rs1 != null) {
					while (rs1.next()) {
						getTemplateMngmntActivePojo = new GetTemplateMngmntActiveDataPojo();
						getTemplateMngmntActivePojo.setChildKeyValue(rs1.getString("Name"));
						getTemplateMngmntActivePojo.setCommandValue(rs1.getString("Command_Value"));
						templateMngmntActiveList.add(getTemplateMngmntActivePojo);

					}
				}

				queryUpdate = "update templateconfig_feature_active set Active = true where TempId = ? and Feature_Selection = ? ";

				try {
					PreparedStatement preparedStatement = connection.prepareStatement(queryUpdate);

					preparedStatement.setString(1, templateId);
					preparedStatement.setString(2, Value);
					preparedStatement.executeUpdate();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logger.info("Got an exception! ");
					logger.error(e.getMessage());
				}
			} else {
				queryUpdate = "update templateconfig_feature_active set Active = true where TempId = ? and Feature_Selection IN (select name from templateconfig_feature_command where Parent_name=?)";

				try {
					PreparedStatement preparedStatement = connection.prepareStatement(queryUpdate);

					preparedStatement.setString(1, templateId);
					preparedStatement.setString(2, Value);

					preparedStatement.executeUpdate();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logger.info("Got an exception! ");
					logger.error(e.getMessage());
				}
			}
			/*
			 * } else { query =
			 * "select * from  templateconfig_feature_command where Parent_name = ? " ;
			 * preparedStmt = connection.prepareStatement(query); preparedStmt.setString(1,
			 * "Routing Protocol"); //preparedStmt.setString(2, parentValue); rs =
			 * preparedStmt.executeQuery(); int i=0; templateMngmntActiveList = new
			 * ArrayList<GetTemplateMngmntActiveDataPojo>(); if (rs != null) { while
			 * (rs.next()) { i++; getTemplateMngmntActivePojo=new
			 * GetTemplateMngmntActiveDataPojo();
			 * getTemplateMngmntActivePojo.setChildKeyValue (rs.getString("Name"));
			 * getTemplateMngmntActivePojo.setCommandValue (rs.getString("Command_Value"));
			 * templateMngmntActiveList.add(getTemplateMngmntActivePojo);
			 * 
			 * } } }
			 */

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(rs1);
			// DBUtil.close(preparedStatement);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return templateMngmntActiveList;
	}

	@SuppressWarnings("resource")
	public String updateDeactivatedFeature(String Value, String templateId) {
		Statement statement = null;
		Connection connection = ConnectionFactory.getConnection();

		String query = "";
		;
		String query1 = "";
		String queryUpdate = "";
		ResultSet rs = null;
		ResultSet rs1 = null;
		PreparedStatement preparedStmt;
		List<GetTemplateMngmntActiveDataPojo> templateMngmntActiveList = null;
		GetTemplateMngmntActiveDataPojo getTemplateMngmntActivePojo = null;
		try {
			query = "select * from  templateconfig_feature_command where Parent_name = ? ";
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, Value);
			// preparedStmt.setString(2, parentValue);
			rs = preparedStmt.executeQuery();
			if (rs == null) {

				queryUpdate = "update templateconfig_feature_active set Active = false where TempId = ? and Feature_Selection = ? ";

				try {
					PreparedStatement preparedStatement = connection.prepareStatement(queryUpdate);

					preparedStatement.setString(1, templateId);
					preparedStatement.setString(2, Value);
					preparedStatement.executeUpdate();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logger.info("Got an exception! ");
					logger.error(e.getMessage());
				}
			} else {
				queryUpdate = "update templateconfig_feature_active set Active = false where TempId = ? and Feature_Selection IN (select name from templateconfig_feature_command where Parent_name=?)";

				try {
					PreparedStatement preparedStatement = connection.prepareStatement(queryUpdate);

					preparedStatement.setString(1, templateId);
					preparedStatement.setString(2, Value);

					preparedStatement.executeUpdate();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logger.info("Got an exception! ");
					logger.error(e.getMessage());
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(rs1);
			// DBUtil.close(preparedStatement);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return "Success";
	}

	public List<TemplateLeftPanelJSONModel> getDataFeatures(String templateId) throws SQLException {
		GetTemplateMngmntActiveDataPojo getTemplateMngmntActivePojo = new GetTemplateMngmntActiveDataPojo();
		connection = ConnectionFactory.getConnection();
		ResultSet rs = null;
		RequestInfoSO request = null;
		List<GetTemplateMngmntActiveDataPojo> templateMngmntActiveList = null;
		PreparedStatement preparedStmt;
		List<TemplateLeftPanelJSONModel> extractedList = new ArrayList<TemplateLeftPanelJSONModel>();
		TemplateLeftPanelJSONModel jsonModelObj = null;
		String query = null;
		boolean isParent = false;
		List<TemplateLeftPanelJSONModel> mainList = new ArrayList<TemplateLeftPanelJSONModel>();

		try {
			query = "SELECT * FROM templateconfig_feature_command command LEFT OUTER JOIN templateconfig_feature_active active ON (command.Name = active.Feature_Selection AND active.TempId = ?);";

			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, templateId);
			rs = preparedStmt.executeQuery();
			templateMngmntActiveList = new ArrayList<GetTemplateMngmntActiveDataPojo>();
			logger.info("" + rs.getFetchSize());
			int id;
			while (rs.next()) {
				jsonModelObj = new TemplateLeftPanelJSONModel();
				jsonModelObj.setName(rs.getString("Name"));
				jsonModelObj.setIdToCheck(rs.getInt("Feature_Id"));
				jsonModelObj.setChecked(rs.getBoolean("Active"));
				jsonModelObj.setParent(rs.getString("Parent_name"));
				jsonModelObj.setHasParent(rs.getInt("hasParent"));
				jsonModelObj.setId(rs.getString("Name").replace(" ", ""));
				jsonModelObj.setConfText("");
				if (rs.getBoolean("Active") == true) {
					jsonModelObj.setMandatory(true);
				} else {
					jsonModelObj.setMandatory(false);

				}
				jsonModelObj.setDisabled(rs.getBoolean("Disabled"));
				extractedList.add(jsonModelObj);
			}
			List<TemplateLeftPanelJSONModel> childList = null;
			TemplateLeftPanelJSONModel objectToAdd = null;
			boolean alreadyadded = false;
			for (int i = 0; i < extractedList.size(); i++) {
				if (mainList.size() > 0) {
					for (int k = 0; k < mainList.size(); k++) {
						if (extractedList.get(i).getParent().equalsIgnoreCase(mainList.get(k).getParent())) {
							alreadyadded = true;
							break;
						}
					}
					if (!alreadyadded) {
						if (extractedList.get(i).getHasParent() == 1) {
							childList = new ArrayList<TemplateLeftPanelJSONModel>();
							objectToAdd = new TemplateLeftPanelJSONModel();
							for (int j = 0; j < extractedList.size(); j++) {
								if (extractedList.get(j).getParent()
										.equalsIgnoreCase(extractedList.get(i).getParent())) {
									childList.add(extractedList.get(j));
								}
							}
							Collections.reverse(childList);
							objectToAdd.setChildList(childList);
							objectToAdd.setName(childList.get(0).getParent());
							objectToAdd.setParent(childList.get(0).getParent());
							objectToAdd.setId(childList.get(0).getParent().replace(" ", ""));

							mainList.add(objectToAdd);
						} else {
							mainList.add(extractedList.get(i));
						}

					}
					alreadyadded = false;

				} else {
					if (extractedList.get(i).getHasParent() == 1) {
						childList = new ArrayList<TemplateLeftPanelJSONModel>();
						objectToAdd = new TemplateLeftPanelJSONModel();
						for (int j = 0; j < extractedList.size(); j++) {
							if (extractedList.get(j).getParent().equalsIgnoreCase(extractedList.get(i).getParent())) {
								childList.add(extractedList.get(j));
							}
						}
						Collections.reverse(childList);
						objectToAdd.setChildList(childList);
						objectToAdd.setName(childList.get(0).getParent());
						objectToAdd.setParent(childList.get(0).getParent());
						objectToAdd.setId(childList.get(0).getParent().replace(" ", ""));

						mainList.add(objectToAdd);
					} else {
						mainList.add(extractedList.get(i));
					}
				}
			}

		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}

		return mainList;
	}

	@SuppressWarnings("resource")
	public final Map<String, String> createTemplateBasicConfig(String vendor, String deviceType, String model,
			String os, String osVersion, String region, String templateId) {
		Map<String, String> resultmap = new HashMap<String, String>();
		String tempid = null;
		tempid = getTemplateID(vendor, deviceType, model, os, osVersion, region);
		connection = ConnectionFactory.getConnection();
		DuplicateDataException exception = null;
		boolean isPresent = false;
		if (templateId == null) {
			String query2 = "SELECT * FROM templateconfig_basic_details";
			try {
				Statement pst = connection.createStatement();
				ResultSet rs1 = pst.executeQuery(query2);
				while (rs1.next()) {
					if (rs1.getString("TempId").equalsIgnoreCase(tempid)) {
						isPresent = true;
						exception = new DuplicateDataException();
						exception.setError_code("E001");
						query2 = "SELECT * FROM errorcodedata";
						rs1 = pst.executeQuery(query2);
						while (rs1.next()) {
							if (rs1.getString("ErrorId").equalsIgnoreCase(exception.getError_code())) {
								exception.setError_description(rs1.getString("ErrorDescription"));
								exception.setError_type(rs1.getString("ErrorType"));
							}
						}
						break;
					}
				}
				if (!isPresent) {
					resultmap.put("tempid", tempid);
					resultmap.put("status", "success");
					resultmap.put("errorCode", null);
					resultmap.put("errorType", null);
					resultmap.put("errorDescription", null);
					resultmap.put("version", "1.0");
				} else {

					resultmap.put("tempid", tempid);
					resultmap.put("status", "failure");
					resultmap.put("errorCode", exception.getError_code());
					resultmap.put("errorType", exception.getError_type());
					resultmap.put("errorDescription", exception.getError_description());
					resultmap.put("version", "1.0");

				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				DBUtil.close(connection);
			}
		} else {
			resultmap.put("tempid", templateId);
			resultmap.put("status", "success");
			resultmap.put("errorCode", null);
			resultmap.put("errorType", null);
			resultmap.put("errorDescription", null);
			String version = StringUtils.substringAfter(templateId, "_V");
			resultmap.put("version", version);
		}
		return resultmap;
	}

	@SuppressWarnings("resource")
	public final Map<String, String> addTemplate(String vendor, String deviceType, String model, String os,
			String osVersion, String region, String oldTemplateId, String oldVersion, String comment,
			String networkType) {
		connection = ConnectionFactory.getConnection();
		boolean result = false;
		String tempid = null, oldversion = null;
		String query1 = "INSERT INTO templateconfig_basic_details(TempId,TempVendor,TempDeviceType,TempModel,TempDeviceOs,TempOsVersion,TempRegion,createdDate,templateVersion,templateParentVersion,updatedDate,comment_section,createdby,templateApprover,networkType)"
				+ "VALUES(?,?,?,?,?,?,?,now(),?,?,now(),?,?,?,?)";
		String query2 = "SELECT * FROM templateconfig_basic_details";

		tempid = oldTemplateId;
		oldVersion = oldVersion;

		Map<String, String> resultmap = new HashMap<String, String>();
		DuplicateDataException exception = null;
		boolean isPresent = false;
		try {

			Statement pst = connection.createStatement();
			ResultSet rs1 = pst.executeQuery(query2);
			while (rs1.next()) {
				if (rs1.getString("TempId").equalsIgnoreCase(tempid)) {
					isPresent = true;
					vendor = rs1.getString("TempVendor");
					deviceType = rs1.getString("TempDeviceType");
					model = rs1.getString("TempModel");
					os = rs1.getString("TempDeviceOs");
					osVersion = rs1.getString("TempOsVersion");
					region = rs1.getString("TempRegion");
					exception = new DuplicateDataException();
					exception.setError_code("E001");
					query2 = "SELECT * FROM errorcodedata";
					rs1 = pst.executeQuery(query2);
					while (rs1.next()) {
						if (rs1.getString("ErrorId").equalsIgnoreCase(exception.getError_code())) {
							exception.setError_description(rs1.getString("ErrorDescription"));
							exception.setError_type(rs1.getString("ErrorType"));
						}
					}
					break;
				}
			}
			if (!isPresent) {
				PreparedStatement ps = connection.prepareStatement(query1);

				ps.setString(1, tempid.toUpperCase());
				ps.setString(2, vendor.toUpperCase());
				ps.setString(3, deviceType.toUpperCase());
				ps.setString(4, model.toUpperCase());
				ps.setString(5, os.toUpperCase());
				ps.setString(6, osVersion.toUpperCase());
				ps.setString(7, region.toUpperCase());
				ps.setString(8, "1.0");
				ps.setString(9, "1.0");
				ps.setString(10, comment);
				ps.setString(11, Global.loggedInUser);
				ps.setString(12, "suser");
				ps.setString(13, networkType);

				// int i=0;
				int i = ps.executeUpdate();
				if (i == 1) {
					result = true;

				} else {
					result = false;

				}
				resultmap.put("tempid", tempid);
				resultmap.put("status", "success");
				resultmap.put("errorCode", null);
				resultmap.put("errorType", null);
				resultmap.put("errorDescription", null);
				resultmap.put("version", "1.0");
			} else {
				// ADD code to create a new version of the template
				DecimalFormat numberFormat = new DecimalFormat("#.0");
				Double childVersion, parentVersion;
				String parentversion = null, childversion = null;

				parentversion = oldVersion;
				if (null != parentversion) {
					parentVersion = Double.valueOf(parentversion);
					parentVersion = Double.parseDouble(numberFormat.format(parentVersion - 0.1));
				}

				// childVersion=Double.parseDouble(numberFormat.format(parentVersion+0.1));
				childversion = oldVersion;

				PreparedStatement ps1 = connection.prepareStatement(query1);
				ps1.setString(1, tempid.toUpperCase());
				ps1.setString(2, vendor.toUpperCase());
				ps1.setString(3, deviceType.toUpperCase());
				ps1.setString(4, model.toUpperCase());
				ps1.setString(5, os.toUpperCase());
				ps1.setString(6, osVersion.toUpperCase());
				ps1.setString(7, region.toUpperCase());
				ps1.setString(8, childversion);
				ps1.setString(9, parentversion);
				ps1.setString(10, comment);
				ps1.setString(11, Global.loggedInUser);
				ps1.setString(12, "suser");
				ps1.setString(13, networkType);

				// int i=0;
				int i = ps1.executeUpdate();
				if (i == 1) {
					result = true;

				} else {
					result = false;

				}
				resultmap.put("tempid", tempid);
				resultmap.put("status", "success");
				resultmap.put("errorCode", null);
				resultmap.put("errorType", null);
				resultmap.put("errorDescription", null);
				resultmap.put("version", childversion);

				/*
				 * resultmap.put("tempid", null); resultmap.put("status", "failure");
				 * resultmap.put("errorCode", exception.getError_code());
				 * resultmap.put("errorType", exception.getError_type());
				 * resultmap.put("errorDescription", exception.getError_description());
				 */

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(connection);
		}
		return resultmap;
	}

	private String getTemplateID(String vendor, String deviceType, String model, String os, String osVersion,
			String region) {
		String temp = null;
		// will be modified once edit flow is enabled have to check version and
		// accordingliy append the version
		temp = region.toUpperCase().substring(0, 2) + vendor.substring(0, 2).toUpperCase() + model.toUpperCase()
				+ os.substring(0, 2).toUpperCase() + osVersion;
		return temp;
	}

	public final boolean updateTemplateDB(String tempID) throws SQLException {
		connection = ConnectionFactory.getConnection();
		boolean result = false;
		String tempid = null;
		String query1 = "SELECT * FROM c3p_template_master_feature_list WHERE command_type IN (?,?)";
		String query3 = "SELECT * FROM c3p_template_master_command_list where command_id=? AND command_type IN (?,?)";
		String query4 = "SELECT * FROM c3p_template_master_feature_list";

		String query5 = "INSERT INTO c3p_template_transaction_feature_list (id,command_feature_template_id)"
				+ "VALUES(1,?)";
		List<Integer> checkedFeatureIds = new ArrayList<Integer>();
		Statement pst = null;
		ResultSet rs = null;
		GetTemplateMngmntActiveDataPojo pojo = null;
		Statement ps = connection.createStatement();
		PreparedStatement psSupport = null;
		List<GetTemplateMngmntActiveDataPojo> listOfBlocks = null, listOfBlocks1 = null;
		boolean firstUpdate = false;
		try {
			// By Default add basic config to checked list i.e
			// c3p_transaction_feature_table
			PreparedStatement pst1 = connection.prepareStatement(query5);
			pst1.setString(1, tempID);
			int inrs = pst1.executeUpdate();
			if (inrs == 1) {
				firstUpdate = true;
				result = true;
			} else {
				firstUpdate = false;
			}
			/*
			 * if (firstUpdate) { psSupport = connection.prepareStatement(query1);
			 * psSupport.setString(1, tempID); psSupport.setString(2, "Generic"); rs =
			 * psSupport.executeQuery(); // rs = ps.executeQuery(query1); listOfBlocks = new
			 * ArrayList<GetTemplateMngmntActiveDataPojo>(); while (rs.next()) { pojo = new
			 * GetTemplateMngmntActiveDataPojo(); pojo.setParentKeyValue(rs
			 * .getString("command_parent_feature")); pojo.setChildKeyValue(rs
			 * .getString("comand_display_feature"));
			 * pojo.setFeatureType(rs.getString("command_type"));
			 * pojo.setId(rs.getInt("id")); if (rs.getString("comand_display_feature")
			 * .equalsIgnoreCase("basic Config1")) { pojo.setActiveFlag(1);
			 * pojo.setDisabled(true); } else if (rs.getString("comand_display_feature")
			 * .equalsIgnoreCase("basic Config2")) { pojo.setActiveFlag(1);
			 * pojo.setDisabled(true); } else if (rs.getString("comand_display_feature")
			 * .equalsIgnoreCase("basic Config3")) { pojo.setActiveFlag(1);
			 * pojo.setDisabled(true);
			 * 
			 * } else { pojo.setActiveFlag(0); pojo.setDisabled(false);
			 * 
			 * } listOfBlocks.add(pojo); }
			 * 
			 * Statement prest = connection.createStatement();
			 * 
			 * ResultSet rst = prest.executeQuery(query4);
			 * 
			 * while (rst.next()) { checkedFeatureIds.add(rst.getInt("id")); }
			 * 
			 * if (checkedFeatureIds.size() > 0) { listOfBlocks1 = new
			 * ArrayList<GetTemplateMngmntActiveDataPojo>(); for (int i = 0; i <
			 * checkedFeatureIds.size(); i++) { PreparedStatement prest1 = connection
			 * .prepareStatement(query3); prest1.setString(1,
			 * Integer.toString(checkedFeatureIds.get(i))); prest1.setString(2, "Generic");
			 * prest1.setString(3, tempID); rs = prest1.executeQuery(); while (rs.next()) {
			 * pojo = new GetTemplateMngmntActiveDataPojo();
			 * pojo.setCommandValue(rs.getString("command_value"));
			 * pojo.setCommandId(rs.getString("command_id")); pojo.setCommandSequenceId(rs
			 * .getString("command_sequence_id")); pojo.setId(rs.getInt("id"));
			 * 
			 * listOfBlocks1.add(pojo); } } }
			 * 
			 * // check if entries for this record exist String query2 =
			 * "SELECT * FROM c3p_template_transaction_feature_list"; statement =
			 * connection.createStatement(); rs = statement.executeQuery(query2); boolean
			 * isPresent = false; while (rs.next()) { if
			 * (rs.getString("command_feature_template_id") .equalsIgnoreCase(tempID)) {
			 * isPresent = true; break; } }
			 * 
			 * int[] recordsAffected2 = null; boolean isPresentCommandList = false;
			 * 
			 * // check if entries for this record exist in command list table
			 * 
			 * String query4 = "SELECT * FROM c3p_template_transaction_command_list"; pst =
			 * connection.createStatement(); rs = pst.executeQuery(query4); while
			 * (rs.next()) { if(rs.getString("command_transaction_template_id"
			 * ).equalsIgnoreCase(tempID)) { isPresentCommandList=true; break; } }
			 * 
			 * result = true; // insert these commands in active table
			 * 
			 * if(!isPresent) { for(int i=0; i<listOfBlocks.size();i++) { String
			 * insertBatchQuery=
			 * "INSERT INTO c3p_template_transaction_feature_list(id,command_feature_template_id)"
			 * + "VALUES('"+listOfBlocks.get(i).getId()+"','" +tempID+ "')";
			 * ps.addBatch(insertBatchQuery); } recordsAffected1=ps.executeBatch();
			 * 
			 * }
			 * 
			 * if (!isPresentCommandList) { for (int i = 0; i < listOfBlocks1.size(); i++) {
			 * String insertBatchQuery =
			 * "INSERT INTO c3p_template_transaction_command_list(command_id,command_sequence_id,command_template_id)"
			 * + "VALUES('" + listOfBlocks1.get(i).getCommandId() + "','" +
			 * listOfBlocks1.get(i).getCommandSequenceId() + "','" + tempID + "')";
			 * ps.addBatch(insertBatchQuery); } recordsAffected2 = ps.executeBatch(); }
			 * 
			 * // int i=0; if (recordsAffected2.length > 0) { result = true; } else { result
			 * = false; } } else { result = false; }
			 */
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			ps.close();
			DBUtil.close(connection);
		}
		return result;
	}

	public List<TemplateBasicConfigurationPojo> getTemplateList() {
		List<TemplateBasicConfigurationPojo> list = new ArrayList<TemplateBasicConfigurationPojo>();
		TemplateBasicConfigurationPojo pojo;
		connection = ConnectionFactory.getConnection();
		String query2 = "SELECT * FROM templateconfig_basic_details";
		try {
			Statement pst = connection.createStatement();
			ResultSet rs1 = pst.executeQuery(query2);
			while (rs1.next()) {
				pojo = new TemplateBasicConfigurationPojo();
				pojo.setVendor(rs1.getString("TempVendor"));
				pojo.setModel(rs1.getString("TempModel"));
				pojo.setDeviceType(rs1.getString("TempDeviceType"));
				pojo.setDeviceOs(rs1.getString("TempDeviceOs"));
				pojo.setOsVersion(rs1.getString("TempOsVersion"));
				pojo.setRegion(rs1.getString("TempRegion"));
				pojo.setTemplateId(rs1.getString("TempId"));
				Timestamp d = rs1.getTimestamp("createdDate");
				pojo.setDate(covnertTStoString(d));
				pojo.setVersion(rs1.getString("templateVersion"));
				Timestamp d1 = rs1.getTimestamp("updatedDate");
				pojo.setUpdatedDate(covnertTStoString(d1));
				pojo.setComment(rs1.getString("comment_section"));

				if (pojo.getComment().isEmpty()) {
					pojo.setComment("undefined");
				}
				pojo.setStatus(rs1.getString("templateStatus"));
				pojo.setApprover(rs1.getString("templateApprover"));
				pojo.setCreatedBy(rs1.getString("createdby"));
				pojo.setNetworkType(rs1.getString("networkType"));

				if (rs1.getString("templateStatus").equalsIgnoreCase("Pending")) {
					pojo.setEditable(false);
				} else {
					pojo.setEditable(true);
				}
				list.add(pojo);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(connection);
		}
		return list;

	}

	public String covnertTStoString(Timestamp indate) {
		String dateString = null;
		Date date = new Date();
		date.setTime(indate.getTime());
		dateString = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(date);
		;
		return dateString;
	}

	public void updateDBActiveOnSelectAll(String templateId) {
		connection = ConnectionFactory.getConnection();
		String query1 = null, query2 = null, query3 = null;
		query1 = "select * from c3p_template_master_feature_list";
		query2 = "delete from c3p_template_transaction_feature_list WHERE command_feature_template_id=?";
		query3 = "Insert into c3p_template_transaction_feature_list(id,command_feature_template_id)" + "VALUES(?,?)";
		PreparedStatement preparedStmt;
		Statement smt;
		ResultSet rs = null;
		List<Integer> defaultIdList = new ArrayList<Integer>();
		try {
			preparedStmt = connection.prepareStatement(query2);
			smt = connection.createStatement();
			rs = smt.executeQuery(query1);
			while (rs.next()) {
				defaultIdList.add(rs.getInt("id"));
			}
			preparedStmt.setString(1, templateId);
			preparedStmt.execute("SET FOREIGN_KEY_CHECKS=0");
			preparedStmt.execute("SET SQL_SAFE_UPDATES=0");
			int res = preparedStmt.executeUpdate();
			if (res == 1) {
				preparedStmt = connection.prepareStatement(query3);
				for (int i = 0; i < defaultIdList.size(); i++) {
					preparedStmt.setInt(1, defaultIdList.get(i));
					preparedStmt.setString(2, templateId);
					preparedStmt.addBatch();
				}
				preparedStmt.executeBatch();
			}
			for (int i = 0; i < Global.globalSessionRightPanel.size(); i++) {
				Global.globalSessionRightPanel.get(i).setChecked(true);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("Got an exception! ");
			logger.error(e.getMessage());
		} finally {
			DBUtil.close(connection);
		}
	}

	public void updateDBActiveOnDeSelectResetAll(String templateId) {
		connection = ConnectionFactory.getConnection();
		String query = null;
		query = "delete from c3p_template_transaction_feature_list WHERE command_feature_template_id=?";
		PreparedStatement preparedStmt;
		Statement smt;
		ResultSet rs = null;
		try {
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.execute("SET FOREIGN_KEY_CHECKS=0");
			preparedStmt.execute("SET SQL_SAFE_UPDATES=0");
			preparedStmt.setString(1, templateId);
			int res = preparedStmt.executeUpdate();
			if (res == 1) {

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("Got an exception! ");
			logger.error(e.getMessage());
		} finally {
			DBUtil.close(connection);
		}
		for (int i = 0; i < Global.globalSessionRightPanel.size(); i++) {
			if (Global.globalSessionRightPanel.get(i).getCommand_id() > 1) {
				Global.globalSessionRightPanel.get(i).setChecked(false);
			}
		}
	}

	public List<TemplateBasicConfigurationPojo> searchResults(String key, String value) {
		connection = ConnectionFactory.getConnection();
		List<TemplateBasicConfigurationPojo> list = new ArrayList<TemplateBasicConfigurationPojo>();
		String query = null;
		if (key.equalsIgnoreCase("Template ID")) {
			query = "SELECT * FROM requestinfo.templateconfig_basic_details WHERE TempId LIKE ? and templateVersion LIKE ?";
		} else if (key.equalsIgnoreCase("Device Type")) {
			query = "SELECT * FROM requestinfo.templateconfig_basic_details WHERE TempDeviceType LIKE ?";

		} else if (key.equalsIgnoreCase("Vendor")) {
			query = "SELECT * FROM requestinfo.templateconfig_basic_details WHERE TempVendor LIKE ?";

		} else if (key.equalsIgnoreCase("Model")) {
			query = "SELECT * FROM requestinfo.templateconfig_basic_details WHERE TempModel LIKE ?";

		} else if (key.equalsIgnoreCase("OS")) {
			query = "SELECT * FROM requestinfo.templateconfig_basic_details WHERE TempDeviceOs LIKE ?";

		} else if (key.equalsIgnoreCase("OS Version")) {
			query = "SELECT * FROM requestinfo.templateconfig_basic_details WHERE TempOsVersion LIKE ?";

		} else if (key.equalsIgnoreCase("Status")) {
			query = "SELECT * FROM requestinfo.templateconfig_basic_details WHERE templateStatus LIKE ?";

		} else if (key.equalsIgnoreCase("Approver")) {
			query = "SELECT * FROM requestinfo.templateconfig_basic_details WHERE templateApprover LIKE ?";

		}
		ResultSet rs = null;
		TemplateBasicConfigurationPojo template = null;
		List<TemplateBasicConfigurationPojo> templateList = null;
		PreparedStatement pst = null;
		try {

			pst = connection.prepareStatement(query);

			if (key.equalsIgnoreCase("Template ID")) {
				if (value.length() == 14) {
					pst.setString(1, "%" + value.substring(0, 14) + "%");
					pst.setString(2, "%");
				} else if (value.length() < 14) {
					pst.setString(1, "%" + value + "%");
					pst.setString(2, "%");

				} else if (value.length() > 16) {
					pst.setString(1, "%" + value.substring(0, 14) + "%");
					pst.setString(2, value.substring(16, 19) + "%");
				}

			} else {
				pst.setString(1, value + "%");

			}
			rs = pst.executeQuery();
			templateList = new ArrayList<TemplateBasicConfigurationPojo>();
			while (rs.next()) {
				template = new TemplateBasicConfigurationPojo();
				template.setVendor(rs.getString("TempVendor"));
				template.setTemplateId(rs.getString("TempId"));
				template.setDeviceType(rs.getString("TempDeviceType"));
				template.setModel(rs.getString("TempModel"));
				template.setDeviceOs(rs.getString("TempDeviceOs"));
				template.setOsVersion(rs.getString("TempOsVersion"));
				template.setRegion(rs.getString("TempRegion"));
				Timestamp d = rs.getTimestamp("createdDate");
				template.setDate(covnertTStoString(d));
				template.setVersion(rs.getString("templateVersion"));
				Timestamp d1 = rs.getTimestamp("updatedDate");
				template.setUpdatedDate(covnertTStoString(d1));
				template.setStatus(rs.getString("templateStatus"));
				template.setApprover(rs.getString("templateApprover"));
				template.setCreatedBy(rs.getString("createdby"));
				template.setComment(rs.getString("comment_section"));
				if (template.getStatus().equalsIgnoreCase("Approved")
						|| template.getStatus().equalsIgnoreCase("approved")) {
					template.setEditable(true);
				}

				templateList.add(template);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return templateList;
	}

	public List<String> getActiveFeatureListForCurrentTemplate(String templateid) {
		List<String> list = new ArrayList<String>();
		connection = ConnectionFactory.getConnection();
		String query = null;
		query = "select * from templateconfig_feature_active where TempId=?";
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {

			pst = connection.prepareStatement(query);
			pst.setString(1, templateid);
			rs = pst.executeQuery();
			while (rs.next()) {
				if (rs.getInt("Active") == 1) {
					list.add(rs.getString("Feature_Selection"));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return list;
	}

	public final boolean savenewfeatureinCommandList(String parent, String commandName, String commandValue,
			String templateID) {
		boolean res = false;
		connection = ConnectionFactory.getConnection();
		String query = null;
		query = "INSERT INTO templateconfig_feature_command(Name,Command_Value,Parent_name,hasParent,draggable,templateid)"
				+ "VALUES(?,?,?,1,1,?)";
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {

			String commandNme = trimCommandName(commandName);
			if (commandNme.equalsIgnoreCase("Basic Config1")) {
				commandNme = "Basic Configuration";
			} else if (commandNme.equalsIgnoreCase("Basic Config2")) {
				commandNme = "Basic Configuration";

			} else if (commandNme.equalsIgnoreCase("Basic Config3")) {
				commandNme = "Basic Configuration";

			}
			pst = connection.prepareStatement(query);
			pst.setString(1, commandNme);
			pst.setString(2, commandValue);
			pst.setString(3, parent);
			pst.setString(4, templateID);
			int i = pst.executeUpdate();
			if (i == 1) {
				res = true;

			} else {
				res = false;

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return res;
	}

	private String trimCommandName(String value) {
		String val = null;
		val = value.substring(10, value.length());
		return val;
	}

	// this method is called when we generate a config for the template with
	// features that are active

	public String getFinalConfigurationTemplate(String tempId) throws SQLException {
		connection = ConnectionFactory.getConnection();
		ResultSet rs = null;
		PreparedStatement preparedStmt = null;
		String query = null;
		String templateCreated = "";
		try {
			query = "select * FROM templateconfig_feature_command tempCmd INNER JOIN templateconfig_feature_active tempactive on tempCmd.Name = tempactive.Feature_Selection where tempactive.TempId=? and tempactive.active=true";
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, tempId);

			rs = preparedStmt.executeQuery();
			while (rs.next()) {
				if (rs.getString("tempCmd.condition_for_command") != null) {
					templateCreated = templateCreated.concat(rs.getString("tempCmd.condition_for_command"));
				}
				templateCreated = templateCreated.concat(rs.getString("tempCmd.Command_Value"));
				if (rs.getString("tempCmd.condition_for_command") != null) {
					templateCreated = templateCreated.concat("</#if>");
				}
			}

		} finally {
			DBUtil.close(rs);
			DBUtil.close(preparedStmt);
			DBUtil.close(connection);
		}

		return templateCreated;
	}

	/*
	 * this method is called to get all the features in create config which were
	 * selected during temp. Mngmnt
	 */

	public List<String> getListForFeatureSelectTempMngmnt(String tempId) throws SQLException {
		connection = ConnectionFactory.getConnection();
		ResultSet rs = null;
		ResultSet rs1 = null;
		PreparedStatement preparedStmt = null;
		String query = null;
		String query1 = null;
		List<String> featureList = new ArrayList<String>();
		List<String> itemsToAdd = new ArrayList<String>();
		List<Integer> featuresChecked = new ArrayList<Integer>();
		try {
			query = "Select * from c3p_template_transaction_feature_list where command_feature_template_id=?";

			// query =
			// "select distinct tempCmd.Parent_name FROM templateconfig_feature_command
			// tempCmd INNER JOIN templateconfig_feature_active tempactive on tempCmd.Name =
			// tempactive.Feature_Selection where tempactive.TempId=? and
			// tempactive.active=true";
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, tempId);

			rs = preparedStmt.executeQuery();
			while (rs.next()) {
				featuresChecked.add(rs.getInt("id"));
				// itemsToAdd.add(rs.getString("tempCmd.Parent_name"));

			}
			preparedStmt.close();
			query1 = "select * from c3p_template_master_feature_list where id=?";
			for (int i = 0; i < featuresChecked.size(); i++) {

				preparedStmt = connection.prepareStatement(query1);

				preparedStmt.setInt(1, featuresChecked.get(i));
				rs = preparedStmt.executeQuery();
				while (rs.next()) {
					boolean isPresent = false;
					if (rs.getInt("hasParent") == 1) {
						if (featureList.size() > 0) {
							for (int j = 0; j < featureList.size(); j++) {
								if (rs.getString("command_parent_feature").equalsIgnoreCase(featureList.get(j))) {
									isPresent = true;
									break;
								}
							}
							if (!isPresent) {
								featureList.add(rs.getString("command_parent_feature"));

							}
						} else {
							featureList.add(rs.getString("command_parent_feature"));
						}
					} else if (rs.getInt("hasParent") == 0) {
						featureList.add(rs.getString("comand_display_feature"));
					}
				}
			}

		} finally {
			DBUtil.close(rs);
			DBUtil.close(preparedStmt);
			DBUtil.close(connection);
		}

		return featureList;
	}

	public Map<String, String> updateTemplateDBOnModify(String tempID, String OldVersion) {
		connection = ConnectionFactory.getConnection();
		boolean result = false;
		String tempid = null;
		String query1 = "SELECT * FROM templateconfig_feature_active where TempId=?";
		String query2 = null;
		String query3 = "SELECT * FROM TemplateConfig_Feature_Position where TempId=?";
		Map<String, String> resultmap = new HashMap<String, String>();
		Statement pst = null;
		PreparedStatement pst2 = null;
		ResultSet rs = null;
		GetTemplateMngmntActiveDataPojo pojo = null;
		List<GetTemplateMngmntActiveDataPojo> listOfBlocks = null;
		String newTemplateVersion = null;
		try {
			DecimalFormat numberFormat = new DecimalFormat("#.0");

			Double oldTemplateVersion = Double.parseDouble(OldVersion);

			Double newVersion = Double.parseDouble(numberFormat.format(oldTemplateVersion + 0.1));
			newTemplateVersion = newVersion.toString();

			PreparedStatement ps = connection.prepareStatement(query1);
			ps.setString(1, tempID + "_V" + OldVersion);
			// pst = connection.createStatement();
			rs = ps.executeQuery();
			query2 = "INSERT INTO templateconfig_feature_active(TempId,Feature_Selection,Active,Disabled)"
					+ "VALUES(?,?,?,?)";
			pst2 = connection.prepareStatement(query2);
			while (rs.next()) {
				pst2.setString(1, tempID + "_V" + newTemplateVersion);
				pst2.setString(2, rs.getString("Feature_Selection"));
				pst2.setString(3, rs.getString("Active"));
				pst2.setString(4, rs.getString("Disabled"));
				pst2.addBatch();
			}
			pst2.executeBatch();

			PreparedStatement ps2 = connection.prepareStatement(query3);
			ps2.setString(1, tempID + "_V" + OldVersion);
			ResultSet rs1 = null;
			rs1 = ps2.executeQuery();
			query2 = null;
			query2 = "INSERT INTO TemplateConfig_Feature_Position(TempId,Feature_Selection,Block_Position)"
					+ "VALUES(?,?,?)";
			pst2 = connection.prepareStatement(query2);
			while (rs1.next()) {
				pst2.setString(1, tempID + "_V" + newTemplateVersion);
				pst2.setString(2, rs1.getString("Feature_Selection"));
				pst2.setString(3, rs1.getString("Block_Position"));
				pst2.addBatch();
			}
			pst2.executeBatch();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(connection);
		}
		resultmap.put("templateID", tempID);
		resultmap.put("version", newTemplateVersion);

		return resultmap;
	}

	public Map<String, String> backTemplateDBOnModify(String tempID, String OldVersion) {

		connection = ConnectionFactory.getConnection();

		String queryTransactionFeatureList = "Delete from c3p_template_transaction_feature_list where command_feature_template_id IN (?)";

		String queryTransactionCommandList = "Delete from c3p_template_transaction_command_list where command_template_id IN (?)";

		String queryBasicDetails = "Delete from templateconfig_basic_details where TempId=? and templateVersion=?";

		String queryMasterFeatureList = "Delete from c3p_template_master_feature_list where command_type=?";

		String queryMasterCommandList = "Delete from c3p_template_master_command_list where command_type=?";

		Map<String, String> resultmap = new HashMap<String, String>();

		/*
		 * String newTemplateVersion=null; DecimalFormat numberFormat = new
		 * DecimalFormat("#.0"); Double
		 * oldTemplateVersion=Double.parseDouble(OldVersion); Double newVersion=Double
		 * .parseDouble(numberFormat.format(oldTemplateVersion+0.1));
		 * newTemplateVersion=newVersion.toString();
		 */
		if (OldVersion != null) {
			tempID = tempID + "_V" + OldVersion;
		} else {
			tempID = tempID;
		}

		try {
			PreparedStatement activeSmt = connection.prepareStatement(queryTransactionFeatureList);
			activeSmt.execute("SET FOREIGN_KEY_CHECKS=0");
			activeSmt.execute("SET SQL_SAFE_UPDATES=0");
			activeSmt.setString(1, tempID);
			int rs1 = activeSmt.executeUpdate();

			PreparedStatement positionSmt = connection.prepareStatement(queryTransactionCommandList);
			positionSmt.setString(1, tempID);
			positionSmt.execute("SET FOREIGN_KEY_CHECKS=0");
			positionSmt.execute("SET SQL_SAFE_UPDATES=0");
			int rs2 = positionSmt.executeUpdate();

			PreparedStatement basicSmt = connection.prepareStatement(queryBasicDetails);
			basicSmt.setString(1, tempID.substring(0, tempID.indexOf("V") - 1));
			basicSmt.setString(2, tempID.substring(tempID.indexOf("V") + 1, tempID.length()));
			basicSmt.execute("SET FOREIGN_KEY_CHECKS=0");
			basicSmt.execute("SET SQL_SAFE_UPDATES=0");
			int rs3 = basicSmt.executeUpdate();

			PreparedStatement masterFeature = connection.prepareStatement(queryMasterFeatureList);
			masterFeature.setString(1, tempID);
			masterFeature.execute("SET FOREIGN_KEY_CHECKS=0");
			masterFeature.execute("SET SQL_SAFE_UPDATES=0");
			int rs4 = masterFeature.executeUpdate();

			PreparedStatement masterCommand = connection.prepareStatement(queryMasterCommandList);
			masterCommand.setString(1, tempID);
			masterCommand.execute("SET FOREIGN_KEY_CHECKS=0");
			masterCommand.execute("SET SQL_SAFE_UPDATES=0");
			int rs5 = masterCommand.executeUpdate();

			if (rs1 > 0 && rs2 > 0 && rs3 > 0) {
				resultmap.put("output", "success");
				resultmap.put("tempID", tempID);

			} else {
				resultmap.put("output", "failure");
				resultmap.put("tempID", tempID);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(connection);
		}

		return resultmap;
	}

	public final boolean updateTemplateDBEdit(String tempID, String prevVersion, String series) throws SQLException {
		connection = ConnectionFactory.getConnection();
		boolean result = false;
		String tempid = null;
		String oldTemplateId = tempID.substring(0, tempID.indexOf("V") - 1) + "_V" + prevVersion;
		String query1 = "SELECT * FROM c3p_template_master_feature_list WHERE command_type IN (?,?)";
		String query3 = "SELECT * FROM c3p_template_master_command_list where command_id=? AND command_type IN (?,?)";
		String query4 = "SELECT * FROM c3p_template_master_feature_list";

		String query5 = "INSERT INTO c3p_template_transaction_feature_list (id,command_feature_template_id)"
				+ "VALUES(?,?)";
		String query6 = "select * from c3p_template_transaction_feature_list where command_feature_template_id=?";
		List<Integer> checkedFeatureIds = new ArrayList<Integer>();
		List<Integer> toCopyFeatureIds = new ArrayList<Integer>();
		Statement pst = null;
		ResultSet rs = null;
		GetTemplateMngmntActiveDataPojo pojo = null;
		Statement ps = connection.createStatement();
		PreparedStatement psSupport = null;
		List<GetTemplateMngmntActiveDataPojo> listOfBlocks = null, listOfBlocks1 = null;
		boolean firstUpdate = false;
		try {
			// By Default add basic config to checked list i.e
			// c3p_transaction_feature_table
			PreparedStatement prep = connection.prepareStatement(query6);
			prep.setString(1, oldTemplateId);
			ResultSet r = prep.executeQuery();

			while (r.next()) {
				toCopyFeatureIds.add(r.getInt("id"));
			}

			for (int i = 0; i < toCopyFeatureIds.size(); i++) {
				PreparedStatement pst1 = connection.prepareStatement(query5);
				pst1.setInt(1, toCopyFeatureIds.get(i));

				pst1.setString(2, tempID);
				int inrs = pst1.executeUpdate();
				if (inrs == 1) {
					firstUpdate = true;
				} else {
					firstUpdate = false;
				}
			}
			if (firstUpdate) {
				psSupport = connection.prepareStatement(query1);
				psSupport.setString(1, oldTemplateId);
				if (series == null) {
					psSupport.setString(2, "Generic");
				} else {
					psSupport.setString(2, "Generic_" + series);

				}
				rs = psSupport.executeQuery();
				// rs = ps.executeQuery(query1);
				listOfBlocks = new ArrayList<GetTemplateMngmntActiveDataPojo>();
				while (rs.next()) {
					pojo = new GetTemplateMngmntActiveDataPojo();
					pojo.setParentKeyValue(rs.getString("command_parent_feature"));
					pojo.setChildKeyValue(rs.getString("comand_display_feature"));
					pojo.setFeatureType(rs.getString("command_type"));
					pojo.setId(rs.getInt("id"));
					if (rs.getString("comand_display_feature").equalsIgnoreCase("basic Config1")) {
						pojo.setActiveFlag(1);
						pojo.setDisabled(true);
					} else if (rs.getString("comand_display_feature").equalsIgnoreCase("basic Config2")) {
						pojo.setActiveFlag(1);
						pojo.setDisabled(true);
					} else if (rs.getString("comand_display_feature").equalsIgnoreCase("basic Config3")) {
						pojo.setActiveFlag(1);
						pojo.setDisabled(true);

					} else {
						pojo.setActiveFlag(0);
						pojo.setDisabled(false);

					}
					listOfBlocks.add(pojo);
				}

				Statement prest = connection.createStatement();

				ResultSet rst = prest.executeQuery(query4);

				while (rst.next()) {
					checkedFeatureIds.add(rst.getInt("id"));
				}

				if (checkedFeatureIds.size() > 0) {
					listOfBlocks1 = new ArrayList<GetTemplateMngmntActiveDataPojo>();
					for (int i = 0; i < checkedFeatureIds.size(); i++) {
						PreparedStatement prest1 = connection.prepareStatement(query3);
						prest1.setString(1, Integer.toString(checkedFeatureIds.get(i)));
						if (series == null) {
							prest1.setString(2, "Generic");
						} else {
							prest1.setString(2, "Generic_" + series);

						}
						prest1.setString(3, tempID);
						rs = prest1.executeQuery();
						while (rs.next()) {
							pojo = new GetTemplateMngmntActiveDataPojo();
							pojo.setCommandValue(rs.getString("command_value"));
							pojo.setCommandId(rs.getString("command_id"));
							pojo.setCommandSequenceId(rs.getString("command_sequence_id"));
							pojo.setId(rs.getInt("id"));

							listOfBlocks1.add(pojo);
						}
					}
				}

				// check if entries for this record exist
				String query2 = "SELECT * FROM c3p_template_transaction_feature_list";
				statement = connection.createStatement();
				rs = statement.executeQuery(query2);
				boolean isPresent = false;
				while (rs.next()) {
					if (rs.getString("command_feature_template_id").equalsIgnoreCase(tempID)) {
						isPresent = true;
						break;
					}
				}

				int[] recordsAffected2 = null;
				boolean isPresentCommandList = false;

				// check if entries for this record exist in command list table
				/*
				 * String query4 = "SELECT * FROM c3p_template_transaction_command_list"; pst =
				 * connection.createStatement(); rs = pst.executeQuery(query4); while
				 * (rs.next()) { if(rs.getString("command_transaction_template_id"
				 * ).equalsIgnoreCase(tempID)) { isPresentCommandList=true; break; } }
				 */
				result = true;
				// insert these commands in active table
				/*
				 * if(!isPresent) { for(int i=0; i<listOfBlocks.size();i++) { String
				 * insertBatchQuery=
				 * "INSERT INTO c3p_template_transaction_feature_list(id,command_feature_template_id)"
				 * + "VALUES('"+listOfBlocks.get(i).getId()+"','" +tempID+ "')";
				 * ps.addBatch(insertBatchQuery); } recordsAffected1=ps.executeBatch();
				 * 
				 * }
				 */
				String query7 = "select * from c3p_template_transaction_command_list where command_template_id=?";
				PreparedStatement psmt = connection.prepareStatement(query7);
				psmt.setString(1, oldTemplateId);
				ResultSet rs1 = psmt.executeQuery();
				listOfBlocks1.clear();
				listOfBlocks1 = new ArrayList<GetTemplateMngmntActiveDataPojo>();
				while (rs1.next()) {
					GetTemplateMngmntActiveDataPojo pojo1 = new GetTemplateMngmntActiveDataPojo();
					pojo1.setCommandId(rs1.getString("command_id"));
					pojo1.setCommandSequenceId(Integer.toString(rs1.getInt("command_sequence_id")));
					listOfBlocks1.add(pojo1);
				}
				if (!isPresentCommandList) {
					for (int i = 0; i < listOfBlocks1.size(); i++) {
						String insertBatchQuery = "INSERT INTO c3p_template_transaction_command_list(command_id,command_sequence_id,command_template_id)"
								+ "VALUES('" + listOfBlocks1.get(i).getCommandId() + "','"
								+ listOfBlocks1.get(i).getCommandSequenceId() + "','" + tempID + "')";
						ps.addBatch(insertBatchQuery);
					}
					recordsAffected2 = ps.executeBatch();
				}

				// int i=0;
				if (recordsAffected2.length > 0) {
					result = true;
				} else {
					result = false;
				}
			} else {
				result = false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			ps.close();
			DBUtil.close(connection);
		}
		return result;
	}

	/* pankaj yes no flow */
	public List<TemplateBasicConfigurationPojo> getTemplatesListToModify(String templateId) {
		String getTempalteByDateSortedQuery = null;
		List<TemplateBasicConfigurationPojo> templateBscCnfgList = new ArrayList<TemplateBasicConfigurationPojo>();
		TemplateBasicConfigurationPojo templateBscCnfgPojo;
		connection = ConnectionFactory.getConnection();
		getTempalteByDateSortedQuery = "SELECT * FROM templateconfig_basic_details WHERE TempId = ?  ORDER BY updatedDate DESC";
		try {
			PreparedStatement pst = connection.prepareStatement(getTempalteByDateSortedQuery);
			pst.setString(1, templateId);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				templateBscCnfgPojo = new TemplateBasicConfigurationPojo();
				templateBscCnfgPojo.setVendor(rs.getString("TempVendor"));
				templateBscCnfgPojo.setModel(rs.getString("TempModel"));
				templateBscCnfgPojo.setDeviceType(rs.getString("TempDeviceType"));
				templateBscCnfgPojo.setDeviceOs(rs.getString("TempDeviceOs"));
				templateBscCnfgPojo.setOsVersion(rs.getString("TempOsVersion"));
				templateBscCnfgPojo.setRegion(rs.getString("TempRegion"));
				templateBscCnfgPojo.setTemplateId(rs.getString("TempId"));
				Timestamp createdDt = rs.getTimestamp("createdDate");
				templateBscCnfgPojo.setDate(covnertTStoString(createdDt));
				templateBscCnfgPojo.setVersion(rs.getString("templateVersion"));
				Timestamp updatedDt = rs.getTimestamp("updatedDate");
				templateBscCnfgPojo.setUpdatedDate(covnertTStoString(updatedDt));
				templateBscCnfgPojo.setComment(rs.getString("comment_section"));
				if (templateBscCnfgPojo.getComment().isEmpty()) {
					templateBscCnfgPojo.setComment("undefined");
				}
				templateBscCnfgPojo.setStatus(rs.getString("templateStatus"));
				templateBscCnfgPojo.setApprover(rs.getString("templateApprover"));
				templateBscCnfgPojo.setCreatedBy(rs.getString("createdby"));

				templateBscCnfgList.add(templateBscCnfgPojo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			DBUtil.close(connection);
		}
		return templateBscCnfgList;
	}

	/*
	 * Dhanshri Mane 14-1-2020 GetCammands related SeriesId
	 */
	public List<CommandPojo> getCammandsBySeriesId(String seriesId, String templateId) {
		connection = ConnectionFactory.getConnection();
		String query1 = "";
		PreparedStatement pst = null;
		ResultSet res = null;

		List<CommandPojo> cammandPojo = new ArrayList<>();
		try {
			/*
			 * If Template id is null get all commmands Related series into
			 * c3p_template_master_command_list table
			 */
			if (templateId == null) {
				query1 = "select  distinct(c3p_template_master_command_list.command_value),c3p_template_master_command_list.command_id,c3p_template_transaction_command_list.command_position  from c3p_template_master_command_list ,c3p_template_transaction_command_list where  command_type=? and c3p_template_master_command_list.command_id =c3p_template_transaction_command_list.command_id and c3p_template_master_command_list.command_sequence_id =c3p_template_transaction_command_list.command_sequence_id order by c3p_template_transaction_command_list.command_position asc;";
				pst = connection.prepareStatement(query1);
				pst.setString(1, "Generic_" + seriesId);
			} else {
				/*
				 * If Template id is not null get all commmands Related series into
				 * c3p_template_master_command_list,c3p_template_transaction_command_list table
				 */
				query1 = "select c3p_template_master_command_list.command_value,c3p_template_master_command_list.command_id,c3p_template_transaction_command_list.command_position  from c3p_template_master_command_list ,c3p_template_transaction_command_list where command_type=? and c3p_template_master_command_list.command_id =c3p_template_transaction_command_list.command_id and c3p_template_master_command_list.command_sequence_id =c3p_template_transaction_command_list.command_sequence_id and command_template_id=? order by c3p_template_transaction_command_list.command_position asc;";
				pst = connection.prepareStatement(query1);
				pst.setString(1, "Generic_" + seriesId);
				pst.setString(2, templateId);

			}

			res = pst.executeQuery();
			CommandPojo cammand = null;
			if (res.wasNull() == true || !res.isBeforeFirst()) {
				PreparedStatement pst1 = null;
				ResultSet res1 = null;
				try {
					String seriesQuery = "select distinct(c3p_template_master_command_list.command_sequence_id),c3p_template_master_command_list.command_id,c3p_template_master_command_list.command_value from c3p_template_master_command_list  where  command_type=? order by c3p_template_master_command_list.command_sequence_id asc;";
					pst1 = connection.prepareStatement(seriesQuery);
					pst1.setString(1, "Generic_" + seriesId);
					res1 = pst1.executeQuery();
					while (res1.next()) {
						cammand = new CommandPojo();
						cammand.setId(res1.getString("c3p_template_master_command_list.command_id"));
						cammand.setCommandValue(res1.getString("c3p_template_master_command_list.command_value"));
						cammand.setPosition(Integer
								.parseInt((res1.getString("c3p_template_master_command_list.command_sequence_id"))));
						cammandPojo.add(cammand);
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					DBUtil.close(res1);
					DBUtil.close(pst1);
					DBUtil.close(connection);
				}
			} else {
				while (res.next()) {
					cammand = new CommandPojo();
					cammand.setId(res.getString("c3p_template_master_command_list.command_id"));
					cammand.setCommandValue(res.getString("c3p_template_master_command_list.command_value"));
					cammand.setPosition(res.getInt("c3p_template_transaction_command_list.command_position"));
					cammandPojo.add(cammand);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(res);
			DBUtil.close(pst);
			DBUtil.close(connection);
		}

		return cammandPojo;

	}/*
		 * Dhanshri Mane 14-1-2020 GetCammands related Features
		 */

	public List<CommandPojo> getCammandByTemplateAndfeatureId(int featureId, String templateId) {

		connection = ConnectionFactory.getConnection();

		String query1 = "select c3p_template_master_command_list.command_value,c3p_template_master_command_list.command_id,c3p_template_transaction_command_list.command_position,c3p_template_master_command_list.no_form_command  from c3p_template_master_command_list ,c3p_template_transaction_command_list where c3p_template_master_command_list.command_id=? and c3p_template_master_command_list.command_id =c3p_template_transaction_command_list.command_id and c3p_template_master_command_list.command_sequence_id =c3p_template_transaction_command_list.command_sequence_id and c3p_template_transaction_command_list.command_template_id=?;";

		PreparedStatement pst;
		ResultSet res;

		List<CommandPojo> cammandPojo = new ArrayList<>();
		try {
			if (featureId != 0) {
				pst = connection.prepareStatement(query1);
				pst.setInt(1, featureId);
				pst.setString(2, templateId);
				res = pst.executeQuery();
				CommandPojo cammand = null;
				if (res.wasNull() == true || !res.isBeforeFirst()) {
					PreparedStatement pst1 = null;
					ResultSet res1 = null;
					try {
						String seriesQuery = "select distinct(c3p_template_master_command_list.command_sequence_id),c3p_template_master_command_list.command_value,c3p_template_master_command_list.command_id,c3p_template_master_command_list.no_form_command from c3p_template_master_command_list where  command_id=? order by c3p_template_master_command_list.command_sequence_id asc;";
						pst1 = connection.prepareStatement(seriesQuery);
						pst1.setInt(1, featureId);
						res1 = pst1.executeQuery();
						while (res1.next()) {
							cammand = new CommandPojo();
							cammand.setId(res1.getString("c3p_template_master_command_list.command_id"));
							cammand.setCommandValue(res1.getString("c3p_template_master_command_list.command_value"));
							cammand.setNo_command_value(
									res1.getString("c3p_template_master_command_list.no_form_command"));
							cammandPojo.add(cammand);
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						DBUtil.close(res1);
						DBUtil.close(pst1);
						DBUtil.close(connection);
					}
				} else {
					while (res.next()) {
						cammand = new CommandPojo();
						cammand.setId(res.getString("c3p_template_master_command_list.command_id"));
						cammand.setCommandValue(res.getString("c3p_template_master_command_list.command_value"));
						cammand.setNo_command_value(res.getString("c3p_template_master_command_list.no_form_command"));
						cammand.setPosition(res.getInt("c3p_template_transaction_command_list.command_position"));
						cammandPojo.add(cammand);
					}
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(connection);
		}
		return cammandPojo;

	}

	/*
	 * Dhanshri Mane 14-1-2020 get Device Details according to TemplateId
	 */
	public DeviceDetailsPojo getDeviceDetails(String templateId) {

		connection = ConnectionFactory.getConnection();

		String query1 = "select * from templateconfig_basic_details where TempId=?;";

		PreparedStatement pst;
		ResultSet res;
		DeviceDetailsPojo deviceDetails = new DeviceDetailsPojo();
		try {
			if (templateId != null) {
				pst = connection.prepareStatement(query1);
				pst.setString(1, templateId);
				res = pst.executeQuery();

				while (res.next()) {
					deviceDetails.setVendor(res.getString("TempVendor"));
					deviceDetails.setModel(res.getString("TempModel"));
					deviceDetails.setDevicetype(res.getString("TempDeviceType"));
					deviceDetails.setRegion(res.getString("TempRegion"));
					deviceDetails.setOs(res.getString("TempDeviceOs"));
					deviceDetails.setOsVersion(res.getString("TempOsVersion"));
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(connection);
		}
		return deviceDetails;
	}

	/*
	 * Dhanshri Mane 14-1-2020 Get Series According to templateId
	 */
	public String getSeriesId(String templateId, String seriesId) {

		connection = ConnectionFactory.getConnection();

		String query1 = "select * from c3p_template_transaction_feature_list where command_feature_template_id=?;";

		PreparedStatement pst;
		ResultSet res;
		String seriesName = null;
		try {
			if (templateId != null) {
				pst = connection.prepareStatement(query1);
				pst.setString(1, templateId);
				res = pst.executeQuery();

				while (res.next()) {
					int id = res.getInt("id");
					String query2 = "select * from c3p_template_master_feature_list where id=?;";
					PreparedStatement pst1 = null;
					ResultSet res1 = null;
					try {
						pst1 = connection.prepareStatement(query2);
						pst1.setInt(1, id);
						res1 = pst1.executeQuery();
						while (res1.next()) {
							String seriesvalue = res1.getString("command_type");
							if (seriesId != null) {
								if (seriesvalue.contains(seriesId)) {
									seriesName = seriesvalue;
								}
							} else if (seriesId == null && seriesvalue.contains("Generic_")) {
								seriesName = seriesvalue;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						DBUtil.close(pst1);
						DBUtil.close(res1);

					}
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(connection);
		}
		return seriesName;
	}

}
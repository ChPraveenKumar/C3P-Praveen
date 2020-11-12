package com.techm.orion.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techm.orion.connection.ConnectionFactory;
import com.techm.orion.connection.DBUtil;
import com.techm.orion.entitybeans.MasterCharacteristicsEntity;
import com.techm.orion.entitybeans.MasterFeatureEntity;
import com.techm.orion.entitybeans.TemplateConfigBasicDetailsEntity;
import com.techm.orion.entitybeans.TemplateFeatureEntity;
import com.techm.orion.mapper.AttribCreateConfigResponceMapper;
import com.techm.orion.pojo.AttribCreateConfigJson;
import com.techm.orion.pojo.AttribCreateConfigPojo;
import com.techm.orion.pojo.CategoryDropDownPojo;
import com.techm.orion.pojo.TemplateAttribPojo;
import com.techm.orion.pojo.TemplateBasicConfigurationPojo;
import com.techm.orion.repositories.MasterCharacteristicsRepository;
import com.techm.orion.repositories.MasterFeatureRepository;
import com.techm.orion.repositories.TemplateConfigBasicDetailsRepository;
import com.techm.orion.repositories.TemplateFeatureRepo;
import com.techm.orion.service.AttribCreateConfigService;
import com.techm.orion.service.CategoryDropDownService;
import com.techm.orion.service.DcmConfigService;
import com.techm.orion.utility.UtilityMethods;

@Component
public class TemplateSuggestionDao {
	private static final Logger logger = LogManager.getLogger(TemplateSuggestionDao.class);
	@Autowired
	private AttribCreateConfigService service;

	@Autowired
	TemplateFeatureRepo templatefeatureRepo;

	@Autowired
	private MasterCharacteristicsRepository masterCharacteristicsRepository;
	
	@Autowired
	private MasterFeatureRepository masterFeatureRepository;

	@Autowired
	private CategoryDropDownService categoryDropDownservice;
	
	@Autowired
	private DcmConfigService dcmConfigService;
	
	@Autowired
	private TemplateConfigBasicDetailsRepository templateConfigBasicDetailsRepository;

	private Connection connection;
	Statement statement;

	public List<String> getListOfFeaturesForDeviceDetail(String tempId, String networkType) throws SQLException {
		connection = ConnectionFactory.getConnection();
		ResultSet rs = null;
		PreparedStatement preparedStmt = null;
		String query = null;
		String query1 = null;
		List<String> featureList = new ArrayList<String>();

		List<Integer> featuresChecked = new ArrayList<Integer>();
		try {

			query = "Select distinct id from c3p_template_transaction_feature_list where command_feature_template_id in(select CONCAT(temp_id, '_V', temp_version) from templateconfig_basic_details where temp_status='Approved' and temp_id like ?)";
			String query2 = "SELECT * FROM templateconfig_basic_details WHERE temp_id like ?";


			preparedStmt = connection.prepareStatement(query2);
			preparedStmt.setString(1, tempId+"%");

			rs = preparedStmt.executeQuery();
			Set<String> networkTypeList = new HashSet<>();
			while (rs.next()) {
				networkTypeList.add(rs.getString("temp_network_type"));
			}
			preparedStmt.close();
			if (networkTypeList.size() >= 1) {
				for (String data : networkTypeList) {
					if (data.equals(networkType)) {
						preparedStmt = connection.prepareStatement(query);
						preparedStmt.setString(1, tempId+"%");

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
											if (rs.getString("command_parent_feature")
													.equalsIgnoreCase(featureList.get(j))) {
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

	public List<String> getListOfFeatureForSelectedTemplate(String tempId) throws SQLException {
		connection = ConnectionFactory.getConnection();
		ResultSet rs = null;
		PreparedStatement preparedStmt = null;
		String query = null;
		String query1 = null;
		List<String> featureList = new ArrayList<String>();

		List<Integer> featuresChecked = new ArrayList<Integer>();
		try {
			query = "Select * from c3p_template_transaction_feature_list where command_feature_template_id  = ?";
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
			/*
			 * for (String value : featureList) { if(!finalList.contains(value)) {
			 * finalList.add(value); } }
			 */
		} finally {
			DBUtil.close(rs);
			DBUtil.close(preparedStmt);
			DBUtil.close(connection);
		}
		return featureList;
	}

	public List<TemplateBasicConfigurationPojo> getDataGrid(String[] feature, String templateId) {
        String query = createQuery(feature.length);
        // String length=feature.length;
        String query1 = "select * from templateconfig_basic_details  where concat(temp_id,'_V',temp_version) in(Select command_feature_template_id from c3p_template_transaction_feature_list where id in("
                + query
                + ") and command_feature_template_id like ?  group by command_feature_template_id having count(distinct id)=?) and temp_status='Approved'";
        logger.info("Query=" + query1);
        connection = ConnectionFactory.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<TemplateBasicConfigurationPojo> listTemplate = new ArrayList<TemplateBasicConfigurationPojo>();
        TemplateBasicConfigurationPojo templateData;
        List<String> getVersionListForTemplate = new ArrayList<String>();
        try {
            ps = connection.prepareStatement(query1);
            for (int i = 1; i <= feature.length; i++) {
                ps.setString(i, feature[i - 1]);
            }
            ps.setString(feature.length + 1, templateId + '%');
            List<String> list = Arrays.asList(feature);
            if (list.contains("Routing Protocol")) {
                // ps.setString(feature.length+2, String.valueOf(feature.length+2)); /* comment
                // as not getting routing protocol template suggestion*/
                ps.setString(feature.length + 2,
                        String.valueOf(feature.length)); /* added for getting routing protocol template suggestion */
            } else {
                ps.setString(feature.length + 2, String.valueOf(feature.length));
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                templateData = new TemplateBasicConfigurationPojo();
                templateData.setTemplateId(rs.getString("temp_id").concat("_V").concat(rs.getString("temp_version")));
                templateData.setComment(rs.getString("temp_comment_section"));
                templateData.setNetworkType(rs.getString("temp_network_type"));
                templateData.setAlias(rs.getString("temp_alias").concat("_V").concat(rs.getString("temp_version")));
                getVersionListForTemplate.add(rs.getString("temp_version"));

                listTemplate.add(templateData);
            }
            // int versionSize=getVersionListForTemplate.size();

 

            String bestTemplate = getBestTemplate(getVersionListForTemplate, templateId);
            for (Iterator iterator = listTemplate.iterator(); iterator.hasNext();) {
                TemplateBasicConfigurationPojo templateBasicConfigurationPojo = (TemplateBasicConfigurationPojo) iterator
                        .next();

 

                if (templateBasicConfigurationPojo.getTemplateId().equalsIgnoreCase(bestTemplate)) {
                    templateBasicConfigurationPojo.setEnabled(true);
                } else {
                    templateBasicConfigurationPojo.setEnabled(false);
                }

 

            }

 

            try {
                rs.close();
            } catch (SQLException e) {
            }

 

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                ps.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listTemplate;
    }
 

	private String createQuery(int length) {
		String query = "select id from c3p_template_master_feature_list where command_parent_feature in(";
		StringBuilder queryBuilder = new StringBuilder(query);
		for (int i = 0; i < length; i++) {
			queryBuilder.append(" ?");
			if (i != length - 1)
				queryBuilder.append(",");
		}
		queryBuilder.append(")");
		return queryBuilder.toString();
	}

	public void insertTemplateUsageData(String TemplateId) {
		connection = ConnectionFactory.getConnection();

		String templateId = TemplateId.substring(0, 14);
		String version = TemplateId.substring(16, 19);
		ResultSet rs = null;

		try {

			String query1 = "SELECT * FROM template_usage_data WHERE templateId = ? and Version = ?";

			PreparedStatement ps = connection.prepareStatement(query1);

			ps.setString(1, templateId);
			ps.setString(2, version);

			rs = ps.executeQuery();

			if (rs.next()) {

				int totalUsage = Integer.parseInt(rs.getString("Total_Usage"));

				String query = null;
				query = "update template_usage_data set Total_Usage = ?  WHERE templateId = ? and Version = ?";
				PreparedStatement preparedStmt;

				preparedStmt = connection.prepareStatement(query);

				preparedStmt.setInt(1, ++totalUsage);
				preparedStmt.setString(2, templateId);
				preparedStmt.setString(3, version);

				preparedStmt.executeUpdate();

			}

			else {
				String sql = "INSERT INTO template_usage_data(templateId,Version,Total_Usage)" + "VALUES(?,?,?)";

				ps = connection.prepareStatement(sql);

				ps.setString(1, templateId);

				ps.setString(2, version);
				ps.setInt(3, 1);

				ps.executeUpdate();
			}
		} catch (SQLException e) {

		}

	}

	public void updateTemplateUsageData(String TemplateId, String result) {
		connection = ConnectionFactory.getConnection();	
		ResultSet rs = null;
		logger.info("updateTemplateUsageData - TemplateId -"+TemplateId);
		try {
			/*Added the TemplateId not null check.. TemplateId will have some data in case of config test. Other tests TemplateId will be null*/
			if(TemplateId !=null) {
				String templateId = TemplateId.substring(0, 14);
				String version = TemplateId.substring(16, 19);
	
				String query1 = "SELECT * FROM template_usage_data WHERE templateId = ? and Version = ?";
	
				PreparedStatement ps = connection.prepareStatement(query1);
	
				ps.setString(1, templateId);
				ps.setString(2, version);
	
				rs = ps.executeQuery();
	
				if (rs.next()) {
	
					int totalUsage = Integer.parseInt(rs.getString("Total_Usage"));
					int successRatio = Integer.parseInt(rs.getString("Success_Ratio"));
					int failureRatio = Integer.parseInt(rs.getString("Failure_Ratio"));
	
					String query = null;
					// double total_success_ratio=successRatio/totalUsage;
					if (result.equalsIgnoreCase("Success")) {
						query = "update template_usage_data set Success_Ratio = ?,total_success_ratio = ?  WHERE templateId = ? and Version = ?";
						PreparedStatement preparedStmt;
	
						preparedStmt = connection.prepareStatement(query);
	
						preparedStmt.setInt(1, ++successRatio);
						double total_success_ratio = ((double) successRatio / (int) totalUsage);
						preparedStmt.setDouble(2, total_success_ratio);
						;
						preparedStmt.setString(3, templateId);
						preparedStmt.setString(4, version);
	
						preparedStmt.executeUpdate();
					} else {
						query = "update template_usage_data set Failure_Ratio = ?,total_success_ratio = ?  WHERE templateId = ? and Version = ?";
						PreparedStatement preparedStmt;
	
						preparedStmt = connection.prepareStatement(query);
	
						preparedStmt.setInt(1, ++failureRatio);
						double total_success_ratio = ((double) successRatio / (int) totalUsage);
						preparedStmt.setDouble(2, total_success_ratio);
						preparedStmt.setString(3, templateId);
						preparedStmt.setString(4, version);
	
						preparedStmt.executeUpdate();
					}
	
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public String getFailureIssueType(String suggestion) {
		connection = ConnectionFactory.getConnection();

		String category = "";
		ResultSet rs = null;

		try {

			String query1 = "SELECT * FROM errorcodedata WHERE suggestion=?";

			PreparedStatement ps = connection.prepareStatement(query1);

			ps.setString(1, suggestion);

			rs = ps.executeQuery();

			while (rs.next()) {
				category = rs.getString("category");
			}
		} catch (SQLException e) {

		}
		return category;
	}

	public String getBestTemplate(List<String> templateVersionList, String templateId) {
		connection = ConnectionFactory.getConnection();

		String versionWithTemplate = "";
		ResultSet rs = null;
		String query = createQueryForVersion(templateVersionList.size());
		try {

			String query1 = "SELECT * FROM template_usage_data WHERE " + query
					+ " and templateId = ? order by Total_Success_Ratio desc,id desc LIMIT 0,1";

			PreparedStatement ps = connection.prepareStatement(query1);

			ps.setString(1, templateId);
			for (int i = 1; i <= templateVersionList.size(); i++) {
				ps.setString(i, templateVersionList.get(i - 1));
			}
			ps.setString(templateVersionList.size() + 1, templateId);

			rs = ps.executeQuery();
			// List<Double> list=new ArrayList<Double>();

			while (rs.next()) {

				versionWithTemplate = rs.getString("Version");

			}

		} catch (SQLException e) {

		}

		return templateId + "_V" + versionWithTemplate;
	}

	private String createQueryForVersion(int length) {
		String query = "version in(";
		StringBuilder queryBuilder = new StringBuilder(query);
		for (int i = 0; i < length; i++) {
			queryBuilder.append(" ?");
			if (i != length - 1)
				queryBuilder.append(",");
		}
		queryBuilder.append(")");
		return queryBuilder.toString();
	}

	/* Dhanshri Mane */
	/* Return Attribute and related validation,UIcomponent,Category */
	public List<TemplateAttribPojo> getDynamicAttribDataGrid(String[] feature, String templateId) {

		List<TemplateAttribPojo> templateWithAttrib = new ArrayList<>();

		try {

			List<String> featureList = Arrays.asList(feature);
			List<AttribCreateConfigPojo> attribConfig = null;
			for (String features : featureList) {
				attribConfig = new ArrayList<>();
				if (features.contains("Generic_")) {
					String seriesId = StringUtils.substringAfter(features, "Generic_");
					attribConfig.addAll(service.getByAttribSeriesId(seriesId));
				} else {
					attribConfig.addAll(service.getByAttribTemplateAndFeatureName(templateId, features));
				}
				TemplateAttribPojo templateattrib = new TemplateAttribPojo();
				templateattrib.setName(features);

				List<AttribCreateConfigJson> AttribConfigJson = new ArrayList<AttribCreateConfigJson>();
				/* map byAttribSeriesId List to jsonValue List to return Responce */

				for (AttribCreateConfigPojo attribInfo : attribConfig) {
					AttribCreateConfigJson attribJson = new AttribCreateConfigJson();
					attribJson.setId(attribInfo.getId());
					attribJson.setName(attribInfo.getAttribName());
					attribJson.setLabel(attribInfo.getAttribLabel());
					attribJson.setuIComponent(attribInfo.getAttribUIComponent());
					attribJson.setValidations(attribInfo.getAttribValidations());
					attribJson.setType(attribInfo.getAttribType());
					attribJson.setSeriesId(attribInfo.getAttribSeriesId());
					attribJson.setTemplateId(attribInfo.getAttribTemplateId());
					attribJson.setCategotyLabel(attribInfo.getAttribCategoty());
					/* using Category Name find all category Value */
					if (attribInfo.getAttribCategoty() != null) {
						List<CategoryDropDownPojo> allByCategoryName = categoryDropDownservice
								.getAllByCategoryName(attribInfo.getAttribCategoty());
						attribJson.setCategotyLabel(attribInfo.getAttribCategoty());
						attribJson.setCategory(allByCategoryName);
					}
					AttribConfigJson.add(attribJson);

				}

				templateattrib.setAttribConfig(AttribConfigJson);
				templateWithAttrib.add(templateattrib);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return templateWithAttrib;
	}

	/* Dhanshri Mane */
	/* Return Attribute and related validation,UIcomponent,Category */
	public List<TemplateAttribPojo> getDynamicAttribDataGridForUI(String[] feature, String templateId) {

		List<TemplateAttribPojo> templateWithAttrib = new ArrayList<>();

		try {

			List<String> featureList = Arrays.asList(feature);
			List<AttribCreateConfigPojo> attribConfig = null;
			for (String features : featureList) {
				attribConfig = new ArrayList<>();
				TemplateAttribPojo templateattrib = new TemplateAttribPojo();

				if (features.contains("Basic Configuration")) {
					String seriesId = StringUtils.substringAfter(features, "Basic Configuration");
					attribConfig.addAll(service.getByAttribSeriesId(seriesId));
					templateattrib.setName("Basic Configuration");
				} else {
					attribConfig.addAll(service.getByAttribTemplateAndFeatureName(templateId, features));
					templateattrib.setName(features);
				}

				List<AttribCreateConfigJson> AttribConfigJson = new ArrayList<AttribCreateConfigJson>();
				/* map byAttribSeriesId List to jsonValue List to return Responce */

				for (AttribCreateConfigPojo attribInfo : attribConfig) {
					AttribCreateConfigJson attribJson = new AttribCreateConfigJson();
					attribJson.setId(attribInfo.getId());
					attribJson.setName(attribInfo.getAttribName());
					attribJson.setLabel(attribInfo.getAttribLabel());
					attribJson.setuIComponent(attribInfo.getAttribUIComponent());
					attribJson.setValidations(attribInfo.getAttribValidations());
					attribJson.setType(attribInfo.getAttribType());
					attribJson.setSeriesId(attribInfo.getAttribSeriesId());
					attribJson.setTemplateId(attribInfo.getAttribTemplateId());
					attribJson.setCategotyLabel(attribInfo.getAttribCategoty());
					/* using Category Name find all category Value */
					if (attribInfo.getAttribCategoty() != null) {
						List<CategoryDropDownPojo> allByCategoryName = categoryDropDownservice
								.getAllByCategoryName(attribInfo.getAttribCategoty());
						attribJson.setCategotyLabel(attribInfo.getAttribCategoty());
						attribJson.setCategory(allByCategoryName);
					}
					AttribConfigJson.add(attribJson);

				}

				templateattrib.setAttribConfig(AttribConfigJson);
				templateWithAttrib.add(templateattrib);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return templateWithAttrib;
	}

	public JSONObject getBasicDeatilsOfTemplate(String templateId, String version) {
		connection = ConnectionFactory.getConnection();
		JSONObject basicDetails = new JSONObject();
		ResultSet rs = null;
		List<String> versionList = new ArrayList<>();
		try {
			String query1 = "SELECT * FROM templateconfig_basic_details WHERE temp_id=? And temp_version=?";
			String query2 = "SELECT * FROM templateconfig_basic_details WHERE temp_id=? order by temp_version asc;";

			PreparedStatement ps = connection.prepareStatement(query1);
			ps.setString(1, templateId);
			ps.setString(2, version);
			rs = ps.executeQuery();

			PreparedStatement ps2 = connection.prepareStatement(query2);
			ps2.setString(1, templateId);

			String status = null;
			while (rs.next()) {
				basicDetails.put("vendor", rs.getString("temp_vendor"));
				basicDetails.put("deviceFamily", rs.getString("temp_device_family"));
				basicDetails.put("model", rs.getString("temp_model"));
				basicDetails.put("region", rs.getString("temp_region"));
				basicDetails.put("os", rs.getString("temp_device_os"));
				basicDetails.put("osVersion", rs.getString("temp_os_version"));
				basicDetails.put("creationDate", rs.getString("temp_created_date"));
				basicDetails.put("raisedBy", rs.getString("temp_created_by"));
				basicDetails.put("networkType", rs.getString("temp_network_type"));
				String comment = rs.getString("temp_comment_section");
				basicDetails.put("comment", comment);
				status = rs.getString("temp_status");
			}
			rs = ps2.executeQuery();
			while (rs.next()) {
				versionList.add(rs.getString("temp_version"));
			}
			int size = versionList.size();
			String dbVersion = versionList.get(size - 1);

			if (version.equals(dbVersion)) {
				if (status.equals("Approved")) {
					basicDetails.put("isEditable", true);
				} else {
					basicDetails.put("isEditable", false);
				}
			} else {
				basicDetails.put("isEditable", false);
			}
			basicDetails.put("verionsList", versionList);

		} catch (SQLException e) {

		}
		return basicDetails;
	}

	public List<String> getFeatureList(String tempId) throws SQLException {
		connection = ConnectionFactory.getConnection();
		ResultSet rs = null;
		PreparedStatement preparedStmt = null;
		String query = null;
		List<String> featureList = new ArrayList<String>();

		try {
			query = "SELECT * FROM c3p_template_master_feature_list where is_Save=\"1\" and command_type=?";
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, tempId);

			rs = preparedStmt.executeQuery();
			while (rs.next()) {
				featureList.add(rs.getString("comand_display_feature"));
			}
			preparedStmt.close();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(preparedStmt);
			DBUtil.close(connection);
		}
		return featureList;
	}
	
	public List<String> getListOfFeaturesForDeviceDetail(String tempId, String networkType, String requestType) throws SQLException {
		connection = ConnectionFactory.getConnection();
		ResultSet rs = null;
		PreparedStatement preparedStmt = null;
		String query = null;
		String query1 = null;
		List<String> featureList = new ArrayList<String>();

		List<Integer> featuresChecked = new ArrayList<Integer>();
		try {

			query = "Select distinct id from c3p_template_transaction_feature_list where command_feature_template_id in(select CONCAT(temp_id, '_V', temp_version) from templateconfig_basic_details where temp_status='Approved' and temp_id LIKE ?)";
			String query2 = "SELECT * FROM templateconfig_basic_details WHERE temp_id LIKE ?";

			preparedStmt = connection.prepareStatement(query2);
			preparedStmt.setString(1, tempId+"%");

			rs = preparedStmt.executeQuery();
			Set<String> networkTypeList = new HashSet<>();
			while (rs.next()) {
				networkTypeList.add(rs.getString("temp_network_type"));
			}
			preparedStmt.close();
			if (networkTypeList.size() == 1) {
				for (String data : networkTypeList) {
					if (data.equals(networkType)) {
						preparedStmt = connection.prepareStatement(query);
						preparedStmt.setString(1, tempId+"%");

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
											if (rs.getString("command_parent_feature")
													.equalsIgnoreCase(featureList.get(j))) {
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
	
	@SuppressWarnings("unchecked")
	public JSONObject getFeaturesForDevice(String request) throws ParseException {
		String deviceFamily = null, os = null, osVersion = null, networkType = null, region = null, vendor = null;
		JSONObject json = new JSONObject();
		JSONParser parser = new JSONParser();
		json = (JSONObject) parser.parse(request);
		if (json.containsKey("deviceFamily")) {
			deviceFamily = json.get("deviceFamily").toString();
		}
		if (json.containsKey("vendor")) {
			vendor = json.get("vendor").toString();
		}
		if (json.containsKey("os")) {
			os = json.get("os").toString();
		}
		if (json.containsKey("osVersion")) {
			osVersion = json.get("osVersion").toString();
		}
		if (json.containsKey("region")) {
			region = json.get("region").toString();
		}
		if (json.containsKey("networkType")) {
			networkType = json.get("networkType").toString();
		}
		if ("All".equals(region)) {
			region = "%";
		} else {
			region = "%" + region + "%";
		}
		if ("All".equals(osVersion)) {
			osVersion = "%";
		} else {
			osVersion = "%" + osVersion + "%";
		}
		if ("All".equals(os)) {
			os = "%";
		} else {
			os = "%" + os + "%";
		}
		if ("All".equals(deviceFamily)) {
			deviceFamily = "%";
		} else {
			deviceFamily = "%" + deviceFamily + "%";
		}
		if ("All".equals(networkType)) {
			networkType = "%";
		} else {
			networkType = "%" + networkType + "%";
		}
		JSONObject features = new JSONObject();
		JSONArray outputArray = new JSONArray();
		List<MasterFeatureEntity> masterList = masterFeatureRepository.getData(deviceFamily, os, region, osVersion,
				vendor, networkType);
		masterList.forEach(list -> {
			JSONObject object = new JSONObject();
			JSONObject featureDetails = new JSONObject();
			featureDetails.put("fId", list.getfId());
			featureDetails.put("fName", list.getfName());
			featureDetails.put("fReplicationFlag", list.getfReplicationind());
			object.put("featureDetails", featureDetails);
			object.put("vendor", list.getfVendor());
			object.put("deviceFamily", list.getfFamily());
			object.put("os", list.getfOs());
			object.put("osVersion", list.getfOsversion());
			object.put("region", list.getfRegion());
			object.put("networkType", list.getfNetworkfun());
			outputArray.add(object);
		});
		features.put("output", outputArray);
		return features;
	}

	@SuppressWarnings({ "unchecked", "unchecked" })
	public JSONObject getTemplateDetailsForSelectedFeatures(String request) throws ParseException {
		String region = null, vendor = null, deviceFamily = null, os = null, osVersion = null;
		JSONObject json = new JSONObject();
		JSONParser parser = new JSONParser();
		JSONObject templatelist = new JSONObject();
		json = (JSONObject) parser.parse(request);
		region = json.get("region").toString();
		vendor = json.get("vendor").toString();
		deviceFamily = json.get("deviceFamily").toString();
		os = json.get("os").toString();
		osVersion = json.get("osVersion").toString();
		JSONArray jsonArray = null;
		jsonArray = (JSONArray) json.get("features");
		MasterFeatureEntity masterFeatureEntity = new MasterFeatureEntity();
		TemplateFeatureEntity templateFeatureEntity = new TemplateFeatureEntity();
		String templateId = "";
		String commands = "";
		List<TemplateFeatureEntity> commandTypes = new ArrayList<>();
		List<TemplateConfigBasicDetailsEntity> tempConfigBasic = new ArrayList<>();
		templateId = dcmConfigService.getTemplateName(region, vendor, os, osVersion, deviceFamily);
		commandTypes.addAll(templatefeatureRepo.findByCommandId(templateId));
		List<String> featureList = new ArrayList<>();
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject obj1 = (JSONObject) jsonArray.get(i);
			if (obj1.get("fId") != null) {
				templateFeatureEntity.setMasterFId(obj1.get("fId").toString());
				featureList.add(obj1.get("fId").toString());
			}
			if (obj1.get("fName") != null) {
				masterFeatureEntity.setfName(obj1.get("fName").toString());
			}
			if (obj1.get("fReplicationFlag") != null) {
				masterFeatureEntity.setfReplicationind((boolean) obj1.get("fReplicationFlag"));
			}
		}
		List<TemplateFeatureEntity> TemplateList = new ArrayList<>();
		commandTypes = commandTypes.stream().filter(UtilityMethods.distinctByKeys(TemplateFeatureEntity::getCommand))
				.collect(Collectors.toList());
		commandTypes.forEach(template -> {
			List<String> featureIds = templatefeatureRepo.findByMasterfeatureIdByTemplateId(template.getCommand());
			Collections.sort(featureList);
			Collections.sort(featureIds);
			boolean flag = false;
			if (featureList.size() < featureIds.size()) {
				flag = featureIds.containsAll(featureList);
			} else if (featureIds.size() == featureList.size()) {
				flag = featureList.equals(featureIds);
			}
			if (flag) {
				TemplateList.add(template);
			}
		});
		JSONObject obj = new JSONObject();
		JSONArray arr = new JSONArray();
		for (TemplateFeatureEntity featureEntity : TemplateList) {
			commands = featureEntity.getCommand();
			String temp = StringUtils.substringBefore(commands, "_");
			tempConfigBasic.addAll(templateConfigBasicDetailsRepository.tempIdList(temp));
		}
		// Check unique Template with Id and Version

		List<TemplateConfigBasicDetailsEntity> templateList = tempConfigBasic.stream()
				.filter(UtilityMethods.distinctByKeys(TemplateConfigBasicDetailsEntity::getTempAlias,
						TemplateConfigBasicDetailsEntity::getTempId, TemplateConfigBasicDetailsEntity::getTempVersion))
				.collect(Collectors.toList());
		templateList.forEach(list -> {
			JSONObject templateDetails = new JSONObject();
			templateDetails.put("templateId", list.getTempId() + "_V" + list.getTempVersion());
			templateDetails.put("alias", list.getTempAlias());
			arr.add(templateDetails);
		});
		obj.put("templateDetails", arr);
		if (!arr.isEmpty()) {
			obj.put("Message", "Success");
		} else {
			obj.put("Message", "Templates are not available");
		}
		templatelist.put("entity", obj);
		return templatelist;
	}

	public List<TemplateAttribPojo> getDynamicAttribData(String request) throws ParseException {
		List<TemplateAttribPojo> templateWithAttrib = new ArrayList<>();
		String templateId = null;
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		json = (JSONObject) parser.parse(request);
		jsonArray = (JSONArray) json.get("features");
		templateId = json.get("templateId").toString();
		try {
			if (!templateId.isEmpty()) {
				for (int i = 0; i < jsonArray.size(); i++) {
					List<AttribCreateConfigJson> attribConfigJson = new ArrayList<AttribCreateConfigJson>();
					TemplateAttribPojo templateattrib = new TemplateAttribPojo();
					JSONObject obj1 = (JSONObject) jsonArray.get(i);
					if (obj1.get("fId") != null) {
						templateattrib.setfId(obj1.get("fId").toString());
					}
					if (obj1.get("fName") != null) {
						templateattrib.setfName(obj1.get("fName").toString());
					}
					if (obj1.get("fReplicationFlag") != null) {
						templateattrib.setfReplicationFlag((boolean) obj1.get("fReplicationFlag"));
					}
					List<AttribCreateConfigPojo> AttribData = service.getByFId(templateattrib.getfId(), templateId);

					/* map byAttribSeriesId List to jsonValue List to return Response */
					for (AttribCreateConfigPojo attribInfo : AttribData) {
						AttribCreateConfigJson attribJson = new AttribCreateConfigJson();
						attribJson.setId(attribInfo.getId());
						attribJson.setName(attribInfo.getAttribName());
						attribJson.setLabel(attribInfo.getAttribLabel());
						attribJson.setuIComponent(attribInfo.getAttribUIComponent());
						attribJson.setValidations(attribInfo.getAttribValidations());
						attribJson.setType(attribInfo.getAttribType());
						attribJson.setSeriesId(attribInfo.getAttribSeriesId());
						attribJson.setTemplateId(attribInfo.getAttribTemplateId());
						attribJson.setCategotyLabel(attribInfo.getAttribCategoty());
						/* using Category Name find all category Value */
						if (attribInfo.getAttribCategoty() != null) {
							List<CategoryDropDownPojo> allByCategoryName = categoryDropDownservice
									.getAllByCategoryName(attribInfo.getAttribCategoty());
							attribJson.setCategotyLabel(attribInfo.getAttribCategoty());
							attribJson.setCategory(allByCategoryName);
						}
						attribConfigJson.add(attribJson);
					}
					templateattrib.setAttribConfig(attribConfigJson);
					templateWithAttrib.add(templateattrib);
				}
			} else {
				for (int i = 0; i < jsonArray.size(); i++) {
					MasterFeatureEntity entity = new MasterFeatureEntity();
					JSONObject obj1 = (JSONObject) jsonArray.get(i);
					if (obj1.get("fId") != null) {
						entity.setfId(obj1.get("fId").toString());
					}
					if (obj1.get("fName") != null) {
						entity.setfName(obj1.get("fName").toString());
					}
					if (obj1.get("fReplicationFlag") != null) {
						entity.setfReplicationind((Boolean) obj1.get("fReplicationFlag"));
					}
					AttribCreateConfigResponceMapper attribCreateConfigResponceMapper = new AttribCreateConfigResponceMapper();
					TemplateAttribPojo templateAttrib = new TemplateAttribPojo();
					List<AttribCreateConfigJson> attribCreateConfigJson = new ArrayList<AttribCreateConfigJson>();
					List<MasterFeatureEntity> masterfeature = masterFeatureRepository.findByFeatureId(entity.getfId());
					masterfeature.forEach(list -> {
						templateAttrib.setfId(list.getfId());
						templateAttrib.setfName(list.getfName());
						templateAttrib.setfReplicationFlag(list.getfReplicationind());
					});
					List<MasterCharacteristicsEntity> masterChar = masterCharacteristicsRepository
							.findAllByCFId(templateAttrib.getfId());
					attribCreateConfigJson = attribCreateConfigResponceMapper
							.convertCharacteristicsAttribPojoToJson(masterChar);
					templateAttrib.setAttribConfig(attribCreateConfigJson);
					templateWithAttrib.add(templateAttrib);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return templateWithAttrib;
	}

}
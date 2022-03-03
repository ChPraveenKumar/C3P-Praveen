package com.techm.c3p.core.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.techm.c3p.core.dao.RequestInfoDetailsDao;
import com.techm.c3p.core.entitybeans.AuditDashboardResultEntity;
import com.techm.c3p.core.entitybeans.RequestInfoEntity;
import com.techm.c3p.core.entitybeans.TemplateFeatureEntity;
import com.techm.c3p.core.pojo.CommandPojo;
import com.techm.c3p.core.pojo.RequestInfoPojo;
import com.techm.c3p.core.repositories.AuditDashboardResultRepository;
import com.techm.c3p.core.repositories.MasterCommandsRepository;
import com.techm.c3p.core.repositories.RequestInfoDetailsRepositories;
import com.techm.c3p.core.repositories.TemplateFeatureRepo;
import com.techm.c3p.core.utility.C3PCoreAppLabels;
import com.techm.c3p.core.utility.UtilityMethods;

@Service
public class GoldenTemplateConfigurationService {
	private static final Logger logger = LogManager.getLogger(GoldenTemplateConfigurationService.class);

	@Autowired
	private RequestInfoDetailsRepositories requestInfoDetailsRepositories;

	@Autowired
	private RequestInfoDetailsDao requestInfoDetailsDao;

	@Autowired
	private MasterCommandsRepository masterCommandsRepository;

	@Autowired
	private TemplateFeatureRepo templateFeatureRepo;

	@Autowired
	private AuditDashboardResultRepository auditDashboardResultRepository;

	@Value("${python.service.uri}")
	private String pythonServiceUri;

	@SuppressWarnings("unchecked")
	public JSONObject createRequest(JSONObject requestJson) {
		String dataMode = null, requestIdData = null, requestDataVersion = null;
		JSONObject comparisionLogic = new JSONObject();
		if (requestJson.containsKey("requestId") && requestJson.get("requestId") != null) {
			requestIdData = requestJson.get("requestId").toString();
		}
		if (requestJson.containsKey("version") && requestJson.get("version") != null) {
			requestDataVersion = requestJson.get("version").toString();
		}
		if (requestJson.containsKey("requestData") && requestJson.get("requestData") != null) {
			dataMode = requestJson.get("requestData").toString();
		}

		if (requestIdData != null && requestDataVersion != null) {

			RequestInfoPojo requestValueData = requestInfoDetailsDao
					.getRequestDetailTRequestInfoDBForVersion(requestIdData, requestDataVersion);

			if (requestValueData != null) {
				List<TemplateFeatureEntity> commandFeatureId = templateFeatureRepo
						.findByCommandType(requestValueData.getTemplateID());
				String requestId = null;
				Double requestVersion = null;
				String backupData = "";
				if (requestValueData.getConfigurationGenerationMethods().equals("lastBackup")) {
					List<RequestInfoEntity> requestData = requestInfoDetailsRepositories
							.findByHostNameAndManagmentIPAndAlphanumericReqIdContainsAndStatus(
									requestValueData.getHostname(), requestValueData.getManagementIp(), "SLGB",
									"Success");

					if (requestData != null && !requestData.isEmpty()) {
						Collections.reverse(requestData);
						requestId = requestData.get(0).getAlphanumericReqId();
						requestVersion = requestData.get(0).getRequestVersion();
					} else {
						requestId = requestValueData.getAlphanumericReqId();
						requestVersion = requestValueData.getRequestVersion();
					}
				}
				if ("config".equals(requestValueData.getConfigurationGenerationMethods())) {
					requestId = requestValueData.getAlphanumericReqId();
					requestVersion = requestValueData.getRequestVersion();
				}
				String filepath = C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V" + requestVersion
						+ "_PreviousConfig.txt";
				try {
					backupData = UtilityMethods.readFirstLineFromFile(filepath);
				} catch (IOException e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
				Map<String, Object> dataMap = new HashMap<>();
				dataMap.put("backup", backupData);
				dataMap.put("additionalData", "");
				dataMap.put("addition", "0");
				dataMap.put("missing", "0");
				dataMap.put("missingData", "");
				List<CommandPojo> convertFileToCommand = convertFileToCommand(backupData);
				dataMap.put("configuration",convertFileToCommand);
				List<CommandPojo> cmdData = new ArrayList<>();
				for (TemplateFeatureEntity feature : commandFeatureId) {
					List<CommandPojo> commandTemplateData = masterCommandsRepository.findByCommandId(feature.getId());
					if (commandTemplateData != null && !commandTemplateData.isEmpty()) {
						String commandFileData = "";
						for (CommandPojo data : commandTemplateData) {
							commandFileData = commandFileData + data.getCommand_value();
						}
						dataMap.put("commandFileData", commandFileData);
						dataMap = comparisionLogic(dataMap, dataMode, feature.getComandDisplayFeature(),
								requestValueData, commandTemplateData);						
						List<CommandPojo> commandFileDataValue = (List<CommandPojo>) dataMap.get("commandFileData");
						
						if (commandFileDataValue != null) {
							cmdData.addAll(commandFileDataValue);
						}

					}
				}
				dataMap.put("commandFileData",cmdData);
//				dataMap = reassignData(dataMap);
//				comparisionLogic.put("features", dataMap.get("additionalData"));
				comparisionLogic.put("added", dataMap.get("addition"));
				comparisionLogic.put("deleted", dataMap.get("missing"));
//				comparisionLogic.put("missingData", dataMap.get("missingData"));
				comparisionLogic.put("configurations",createResponseJson(updatesequesnceId((List<CommandPojo>)dataMap.get("configuration"))));
				comparisionLogic.put("feature", createResponseJson(updatesequesnceId((List<CommandPojo>)dataMap.get("commandFileData"))));
				

			}
		}

		return comparisionLogic;
	}

	/*@SuppressWarnings("unchecked")
	private Map<String, Object> reassignData(Map<String, Object> dataMap) {
		List<CommandPojo> commandData = (List<CommandPojo>) dataMap.get("commandFileData");
		List<CommandPojo> fileData = (List<CommandPojo>) dataMap.get("configuration");
		
		int seqId = 1;
		for(CommandPojo cmd : fileData) {
		
			if(cmd.getTempId().equals("1")) {
				commandData = updatesequesnceId(commandData,seqId);
				
			}
			seqId++;
		}
		
		int cmdSeqId = 1;
		for(CommandPojo cmd : commandData) {
		
			if(cmd.getTempId().equals("1")) {
				fileData = updatesequesnceId(fileData,cmdSeqId);
			}
			cmdSeqId++;
		}
		
		dataMap.put("commandFileData", createResponseJson(commandData));		
		dataMap.put("configuration", createResponseJson(fileData));
		return dataMap;	
		
	}
*/
	private JSONArray createResponseJson(List<CommandPojo> commandData) {
		JSONArray arr = new JSONArray();
		commandData.forEach(cmd->{
			JSONObject jsonData = new JSONObject();
			jsonData.put("comand", cmd.getCommand_value());			
			jsonData.put("sequenceId", cmd.getCommandSequenceId());
			jsonData.put("colorFlag", cmd.getTempId());
			arr.add(jsonData);
		});
		return arr;
		
	}

	private List<CommandPojo> updatesequesnceId(List<CommandPojo> fileData) {
		int count =1;
		List<CommandPojo> dataList =  new ArrayList<>();
		for(CommandPojo cmd : fileData) {
			/*if(count == seqId) {
				CommandPojo cmdValue = new CommandPojo();
				cmdValue.setCommand_value("");
				cmdValue.setCommandSequenceId(count);
				dataList.add(cmdValue);
				count ++ ;	
			}*/
			cmd.setCommandSequenceId(count);
			dataList.add(cmd);			
			count++;
		}
		return dataList;
	}

	private List<CommandPojo> convertFileToCommand(String backupData) {
		List<String> fileString = Arrays.asList(backupData.split("\n", -1));
		List<CommandPojo> dataComand =  new ArrayList<>();
		int count=1;
		for(String fileData : fileString) {
			CommandPojo comand =  new CommandPojo();
			comand.setCommand_value(fileData);
			comand.setCommandSequenceId(count);
			count++;
			dataComand.add(comand);
		}	
		return dataComand;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> comparisionLogic(Map<String, Object> dataMap, String dataMode, String featureName,
			RequestInfoPojo requestValueData, List<CommandPojo> commandTemplateData) {
		List<String> fileString = Arrays.asList(String.valueOf(dataMap.get("backup")).split("\n", -1));
		List<String> commandData = Arrays.asList(String.valueOf(dataMap.get("commandFileData")).split("\n", -1));
		List<String> bracketCommands = removeBracket(commandData);

		List<String> backupDataString = new ArrayList<>();
		int start = 0;
		int end = 0;
		for (String fileData : fileString) {
			for (String cmdData : bracketCommands) {
				if (!fileData.equals("!") && !cmdData.equals("!")) {
					String data = cmdData;
					data = data.replaceFirst("^\\s*", "");
					List<String> newData = Arrays.asList(data.split(" ", -1));
					int count = 0;
					if (newData.size() > 2 || !newData.get(0).equals("")) {
						for (String d : newData) {
							if (fileData.contains(d)) {
								count++;
							}
						}
					}
					if (count == newData.size()) {
						if (start == 0) {
							start = fileString.indexOf(fileData);
						}
						end = fileString.indexOf(fileData);
						fileData = cmdData;
						break;
					}
				}
			}
			backupDataString.add(fileData);
		}
		List<String> subList = new ArrayList<>();

		if (start == 0 && end == 0) {
			saveAuditResultData(requestValueData, featureName, "Missing", null, null);
		} else {
			subList = backupDataString.subList(start, end + 1);
		}
		List<String> dataString = Arrays.asList(String.valueOf(dataMap.get("additionalData")).split("\n", -1));

		List<String> missingString = Arrays.asList(String.valueOf(dataMap.get("missingData")).split("\n", -1));
		List<String> missingDataList = new ArrayList<>();
		missingDataList.addAll(missingString);
		List<String> extraDataList = new ArrayList<>();
		extraDataList.addAll(dataString);
		int missingCount = Integer.valueOf(String.valueOf(dataMap.get("missing")));
		int additionalCount = Integer.parseInt(String.valueOf(dataMap.get("addition")));
		for (String cmdData : bracketCommands) {
			if (!cmdData.equals("!") && !cmdData.isEmpty()) {
				boolean dataFound = false;
				if (!subList.isEmpty()) {
					for (String fileData : subList) {
						if (cmdData.equals(fileData)) {
							dataFound = true;
							break;
						}
					}

					if (!dataFound) {
						missingCount++;
						missingDataList.add(cmdData);
						if ("Network Audit".equals(dataMode)) {
							saveAuditResultData(requestValueData, featureName, "Additional", null, cmdData);
						}

					}
				}
			}
		}
		if (!subList.isEmpty()) {
			for (String fileData : subList) {
				if (!fileData.isEmpty()) {
					boolean dataFound = false;
					for (String cmdData : bracketCommands) {
						if (cmdData.equals(fileData)) {
							dataFound = true;
							break;
						}
					}
					if (!dataFound) {
						additionalCount++;
						extraDataList.add(fileData);
						if ("Network Audit".equals(dataMode)) {
							saveAuditResultData(requestValueData, featureName, "Deleted", fileData, null);
						}
					}
				}
			}
		}
		for (CommandPojo cmd : commandTemplateData) {
			if (missingDataList != null && !missingDataList.isEmpty()) {
				missingDataList.forEach(data -> {
					if (!data.isEmpty()) {
						if( cmd.getTempId()==null || cmd.getTempId().equals("0") || cmd.getTempId().equals("2")) {
						if (data.equals(cmd.getCommand_value())) {
							cmd.setTempId("2");
						}
						}
					}
				});
			}
		}
		
		List<CommandPojo> convertFileToCommand = (List<CommandPojo>) dataMap.get("configuration");
		
			if (extraDataList != null && !extraDataList.isEmpty()) {
				extraDataList.forEach(data -> {				
					if (!data.isEmpty()) {
						for (CommandPojo cmd : convertFileToCommand) {						
						if (data.equals(cmd.getCommand_value())) {
							if((cmd.getTempId() == null  ||cmd.getTempId().equals("0") || cmd.getTempId().equals("1"))) {
							cmd.setTempId("1");
							break;
							}
						} 
					}
				}
			});
		}
		String additionalData = StringUtils.join(extraDataList, "\n");
		String missingData = StringUtils.join(missingDataList, "\n");
		dataMap.put("additionalData", additionalData);
		dataMap.put("addition", String.valueOf(additionalCount));
		dataMap.put("missing", String.valueOf(missingCount));
		dataMap.put("missingData", missingData);			
		dataMap.put("commandFileData", commandTemplateData);		
		dataMap.put("configuration",convertFileToCommand);
		return dataMap;
	}


	private void saveAuditResultData(RequestInfoPojo requestValueData, String featureName, String status,
			String templateValue, String configurationValue) {
		AuditDashboardResultEntity auditDashboardResultEntity = new AuditDashboardResultEntity();
		String batchId = requestValueData.getBatchId();
		if (batchId != null) {
			auditDashboardResultEntity.setAdrAuditId(batchId);
		}
		if (configurationValue != null) {
			auditDashboardResultEntity.setAdrConfigurationValue(configurationValue);
		}
		if (templateValue != null) {
			auditDashboardResultEntity.setAdrTemplateValue(templateValue);
		}
		auditDashboardResultEntity.setAdRequestId(requestValueData.getAlphanumericReqId());
		auditDashboardResultEntity.setAdRequestVersion(requestValueData.getRequestVersion());
		auditDashboardResultEntity.setAdrFeatureName(featureName);
		auditDashboardResultEntity.setAdrResult(status);
		auditDashboardResultEntity.setAdrTemplateId(requestValueData.getTemplateID());
		auditDashboardResultEntity.setCreatedBy(requestValueData.getRequestCreatedOn());
		auditDashboardResultEntity.setCreatedBy(requestValueData.getRequestCreatorName());
		auditDashboardResultRepository.save(auditDashboardResultEntity);
	}

	private List<String> removeBracket(List<String> commandData) {
		List<String> commandSet = new ArrayList<>();
		for (String newCommandValue : commandData) {
			if (newCommandValue.contains("[")) {
				if (newCommandValue.contains("[")) {
					String dataValue = StringUtils.substringAfter(newCommandValue, "[");
					dataValue = StringUtils.substringBefore(dataValue, "]");
					newCommandValue = StringUtils.remove(newCommandValue, "[" + dataValue + "]");
				}
				if (newCommandValue.contains("\n")) {
					newCommandValue = newCommandValue.replace("\n", "");
				}

			}
			commandSet.add(newCommandValue);
			if (newCommandValue.contains("[")) {
				commandSet = removeBracket(commandSet);
			}
		}

		return commandSet;

	}
}

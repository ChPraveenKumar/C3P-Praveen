package com.techm.orion.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.techm.orion.entitybeans.DiscoveryDashboardEntity;
import com.techm.orion.entitybeans.ImportMasterStagingEntity;
import com.techm.orion.entitybeans.MasterFeatureEntity;
import com.techm.orion.entitybeans.RequestInfoEntity;
import com.techm.orion.entitybeans.TemplateConfigBasicDetailsEntity;
import com.techm.orion.entitybeans.TestDetail;
import com.techm.orion.pojo.RecentActivityPojo;
import com.techm.orion.repositories.DiscoveryDashboardRepository;
import com.techm.orion.repositories.ImportMasterStagingRepo;
import com.techm.orion.repositories.MasterFeatureRepository;
import com.techm.orion.repositories.RequestInfoDetailsRepositories;
import com.techm.orion.repositories.TemplateConfigBasicDetailsRepository;
import com.techm.orion.repositories.TestDetailsRepository;
import com.techm.orion.utility.WAFADateUtil;

@Service
public class RecentActivityService {
	private static final Logger logger = LogManager.getLogger(RecentActivityService.class);

	@Autowired
	private RequestInfoDetailsRepositories requestInfoDetailsRepositories;

	@Autowired
	private TemplateConfigBasicDetailsRepository templateConfigBasicDetailsRepository;

	@Autowired
	private TestDetailsRepository testDetailsRepository;

	@Autowired
	private MasterFeatureRepository masterFeatureRepository;

	@Autowired
	private DiscoveryDashboardRepository discoveryDashboardRepository;

	@Autowired
	private ImportMasterStagingRepo importMasterStagingRepo;

	@Autowired
	private WAFADateUtil dateUtil;

	@SuppressWarnings("unchecked")
	public JSONObject getRecentActivityDetails(String request) {
		logger.info("Inside- getRecentActivityDetails");
		JSONParser recentActivityParser = new JSONParser();
		JSONObject recentActivityObj = new JSONObject();
		JSONObject recentActivityJson = null;
		String userName = null;
		try {
			int startIndex = 0, endIndex = 10;
			recentActivityJson = (JSONObject) recentActivityParser.parse(request);
			if (recentActivityJson.get("endIndex") != null)
				endIndex = Integer.parseInt(recentActivityJson.get("endIndex").toString());
			if (recentActivityJson.get("userName") != null)
				userName = recentActivityJson.get("userName").toString();
			Pageable pageable = new PageRequest(startIndex, endIndex);
			List<RequestInfoEntity> requestInfoList = requestInfoDetailsRepositories
					.findByRequestCreatorNameOrderByDateofProcessingDesc(userName, pageable);
			List<TemplateConfigBasicDetailsEntity> templateInfoList = templateConfigBasicDetailsRepository
					.findByTempCreatedByOrderByTempCreatedDateDesc(userName, pageable);
			List<TestDetail> testInfoList = testDetailsRepository.findByCreatedByOrderByCreatedOnDesc(userName,
					pageable);
			List<MasterFeatureEntity> masterFeatureList = masterFeatureRepository
					.findByFCreatedByOrderByFCreatedDateDesc(userName, pageable);
			List<DiscoveryDashboardEntity> discoveryInfoList = discoveryDashboardRepository
					.findByDisCreatedByOrderByDisCreatedDateDesc(userName, pageable);
			List<ImportMasterStagingEntity> onboardingInfoList = importMasterStagingRepo
					.findByCreatedByOrderByExecutionDateDesc(userName, pageable);
			List<RecentActivityPojo> recentActivityList = new ArrayList<RecentActivityPojo>();

			for (RequestInfoEntity entity : requestInfoList) {
				recentActivityList.addAll(recentActivityRequestDetails(entity));
			}
			for (TemplateConfigBasicDetailsEntity tempEntity : templateInfoList) {
				recentActivityList.addAll(recentActivityTemplateDetails(tempEntity));
			}
			for (TestDetail testEntity : testInfoList) {
				recentActivityList.addAll(recentActivityTestDetails(testEntity));
			}
			for (MasterFeatureEntity mfEntity : masterFeatureList) {
				recentActivityFeatureDetails(mfEntity);
			}
			for (DiscoveryDashboardEntity ddEentity : discoveryInfoList) {
				recentActivityList.addAll(recentActivityDiscoveryDetails(ddEentity));
			}
			for (ImportMasterStagingEntity onboardingEntity : onboardingInfoList) {
				recentActivityList.addAll(recentActivityOnBoardDetails(onboardingEntity));
			}
			recentActivityList.sort((RecentActivityPojo s1, RecentActivityPojo s2) -> s2.getCreatedDate()
					.compareTo(s1.getCreatedDate()));
			recentActivityObj.put(new String("output"),
					recentActivityList.stream().sorted().limit(endIndex).collect(Collectors.toList()));
		} catch (Exception e) {
			logger.error("Error in getRecentActivityDetails " + e);
		}
		return recentActivityObj;
	}

	private List<RecentActivityPojo> recentActivityRequestDetails(RequestInfoEntity entity) {
		List<RecentActivityPojo> recentActivityList = new ArrayList<RecentActivityPojo>();
		String msg = null;
		RecentActivityPojo recentActivity = new RecentActivityPojo();
		String requestId = entity.getAlphanumericReqId();
		recentActivity.setCreatedDate(dateUtil.convertTimeStampInSDFFormat(entity.getDateofProcessing()));
		String type = entity.getAlphanumericReqId().substring(0, Math.min(entity.getAlphanumericReqId().length(), 4));
		String bitSelection = entity.getCertificationSelectionBit();
		String hostName = entity.getHostName();
		String batchId = entity.getBatchId();
		double version = entity.getRequestVersion();

		if ("SLGF".equalsIgnoreCase(type)) {
			if (bitSelection != null && "0000000".equals(bitSelection)) {
				msg = "Firmware request created for " + hostName;
			} else {
				msg = " Firmware upgrade request initiated for " + hostName;
			}
		} else if ("SLGC".equalsIgnoreCase(type)) {
			if (batchId != null) {
				msg = "Bulk configuration request created for " + hostName;
			} else if (version == 1) {
				msg = " Configuration request created " + requestId;
			} else
				msg = " Configuration request modified " + requestId;
		}
		if ("SLGT".equalsIgnoreCase(type)) {
			if (batchId != null) {
				msg = "Bulk Test request created for " + hostName;
			} else {
				msg = " Test request created " + requestId;
			}
		}
		if ("SLGB".equalsIgnoreCase(type)) {
			if (batchId != null) {
				msg = "Bulk backup request created for " + hostName;
			} else {
				msg = " Backup request created " + requestId;
			}
		}
		if ("SLGA".equalsIgnoreCase(type)) {
			if (batchId != null) {
				msg = "Bulk network audit request created for " + hostName;
			} else {
				msg = " Network audit request created for " + requestId;
			}
		}
		if ("SLGM".equalsIgnoreCase(type)) {
				msg = " MACD request created for " + requestId;
			}
		recentActivity.setMessage(msg);
		recentActivityList.add(recentActivity);
		return recentActivityList;

	}

	private List<RecentActivityPojo> recentActivityTemplateDetails(TemplateConfigBasicDetailsEntity tempEntity) {
		List<RecentActivityPojo> recentActivityList = new ArrayList<RecentActivityPojo>();
		String msg = null;
		RecentActivityPojo recentActivity = new RecentActivityPojo();
		recentActivity = new RecentActivityPojo();
		String tempId = null;
		tempId = tempEntity.getTempId();
		double tempVersion = 0.0;
		String version = tempEntity.getTempVersion();
		tempVersion = Double.parseDouble(version);
		if (tempVersion == 1)
			msg = tempId + " - template created";
		else
			msg = tempId + " - template upgraded to version " + version;
		recentActivity.setCreatedDate(dateUtil.convertTimeStampInSDFFormat(tempEntity.getTempCreatedDate()));
		recentActivity.setMessage(msg);
		recentActivityList.add(recentActivity);
		return recentActivityList;
	}

	private List<RecentActivityPojo> recentActivityTestDetails(TestDetail testEntity) {
		List<RecentActivityPojo> recentActivityList = new ArrayList<RecentActivityPojo>();
		String msg = null;
		RecentActivityPojo recentActivity = new RecentActivityPojo();
		String testId = null;
		testId = testEntity.getTestId();
		double testVersion = 0.0;
		String version = testEntity.getVersion();
		testVersion = Double.parseDouble(version);
		if (testVersion == 1)
			msg = testId + " - test created";
		else
			msg = testId + " - test upgraded to version " + version;
		recentActivity
				.setCreatedDate(dateUtil.convertStringToTimestampInSTDFormat(testEntity.getCreatedOn().toString()));
		recentActivity.setMessage(msg);
		recentActivityList.add(recentActivity);
		return recentActivityList;
	}

	private List<RecentActivityPojo> recentActivityFeatureDetails(MasterFeatureEntity featureEntity) {
		List<RecentActivityPojo> recentActivityList = new ArrayList<RecentActivityPojo>();
		String msg = null;
		RecentActivityPojo recentActivity = new RecentActivityPojo();
		String featureId = null;
		featureId = featureEntity.getfId();
		double fVersion = 0.0;
		String version = featureEntity.getfVersion();
		fVersion = Double.parseDouble(version);
		if (fVersion == 1)
			msg = featureId + " - feature created";
		else
			msg = featureId + " - feature upgraded to version " + version;
		recentActivity.setCreatedDate(dateUtil.convertTimeStampInSDFFormat(featureEntity.getfCreatedDate()));
		recentActivity.setMessage(msg);
		recentActivityList.add(recentActivity);
		return recentActivityList;
	}

	private List<RecentActivityPojo> recentActivityDiscoveryDetails(DiscoveryDashboardEntity ddEentity) {
		List<RecentActivityPojo> recentActivityList = new ArrayList<RecentActivityPojo>();
		String msg = null;
		RecentActivityPojo recentActivity = new RecentActivityPojo();
		String discoveryId = null;
		discoveryId = ddEentity.getDisDashId();
		msg = "Device discovery performed for device " + discoveryId;
		recentActivity
				.setCreatedDate(ddEentity.getDisCreatedDate().toString());
		recentActivity.setMessage(msg);
		recentActivityList.add(recentActivity);
		return recentActivityList;
	}

	private List<RecentActivityPojo> recentActivityOnBoardDetails(ImportMasterStagingEntity onboardingEntity) {
		List<RecentActivityPojo> recentActivityList = new ArrayList<RecentActivityPojo>();
		String msg = null;
		RecentActivityPojo recentActivity = new RecentActivityPojo();
		recentActivity = new RecentActivityPojo();
		String importId = null;
		importId = onboardingEntity.getImportId();
		msg = "Devices onboarded using import with import id " + importId;
		recentActivity.setCreatedDate(dateUtil.convertTimeStampInSDFFormat(onboardingEntity.getExecutionDate()));
		recentActivity.setMessage(msg);
		recentActivityList.add(recentActivity);
		return recentActivityList;
	}
}
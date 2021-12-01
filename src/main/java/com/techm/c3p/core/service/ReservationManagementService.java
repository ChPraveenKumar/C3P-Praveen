package com.techm.c3p.core.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.c3p.core.entitybeans.DeviceDiscoveryEntity;
import com.techm.c3p.core.entitybeans.PortEntity;
import com.techm.c3p.core.entitybeans.PortUsageEntity;
import com.techm.c3p.core.entitybeans.ReservationPortStatusEntity;
import com.techm.c3p.core.entitybeans.WorkGroup;
import com.techm.c3p.core.repositories.DeviceDiscoveryRepository;
import com.techm.c3p.core.repositories.PortEntityRepository;
import com.techm.c3p.core.repositories.PortUsageRepository;
import com.techm.c3p.core.repositories.ReservationPortStatusRepository;
import com.techm.c3p.core.repositories.WorkGroupRepository;
import com.techm.c3p.core.rest.DeviceDiscoveryController;
import com.techm.c3p.core.utility.WAFADateUtil;

@Service
public class ReservationManagementService {
	private static final Logger logger = LogManager.getLogger(ReservationManagementService.class);

	@Autowired
	private ReservationPortStatusRepository reservationPortStatusRepository;


	@Autowired
	private PortUsageRepository portUsageRepository;

	@Autowired
	private WorkGroupRepository workGroupRepository;

	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepository;

	@Autowired
	private PortEntityRepository portEntityRepository;

	@Autowired
	private DeviceDiscoveryController deviceDiscoveryController;
	
	@Autowired
	private WAFADateUtil dateUtil;

	@SuppressWarnings("unchecked")
	public JSONObject getDahsboardData() {
		List<ReservationPortStatusEntity> reservationInformation = reservationPortStatusRepository.findAll();
		int count = 0;
		Set<String> projectIds = new HashSet<>();
		reservationInformation.forEach(data -> {
			if (data != null) {
				projectIds.add(data.getRpProjectId());
			}
		});
		JSONArray reservationDashboardData = new JSONArray();
		for (String projectId : projectIds) {
			JSONObject reservationData = new JSONObject();
			WorkGroup projectData = workGroupRepository.findAllByWorkGroupIdAndWorkGroupType(projectId, "Project");
			reservationData.put("project", projectData.getWorkGroupName());
			List<ReservationPortStatusEntity> reservationPortData = reservationPortStatusRepository.findAllByRpProjectId(projectId);
			reservationData.put("projectStatus", projectData.getWorkGroupStatus());
			if (projectData.getEndDate() != null) {				
				reservationData.put("projectEndDate", dateUtil
						.dateTimeInAppFormat(projectData.getEndDate().toString()));
			} else {
				reservationData.put("projectEndDate", "");
			}
			JSONArray dataValue = new JSONArray();
			for (ReservationPortStatusEntity dashboarddata : reservationPortData) {
				DeviceDiscoveryEntity deviceData = deviceDiscoveryRepository
						.findAllByDId(dashboarddata.getRpDeviceId());
				if ("Reserved".equals(dashboarddata.getRpReservationStatus())) {
					count = count + 1;
				}
				dataValue.add(setReservationData(deviceData, dashboarddata, projectData));

			}
			reservationData.put("childList", dataValue);
			reservationDashboardData.add(reservationData);
		}
		JSONObject outputObject = new JSONObject();
		outputObject.put("output", reservationDashboardData.toString());
		outputObject.put("count", count);
		JSONObject finalObject = new JSONObject();
		finalObject.put("entity", outputObject);
		return finalObject;
	}

	@SuppressWarnings("unchecked")
	private JSONObject setReservationData(DeviceDiscoveryEntity deviceData, ReservationPortStatusEntity dashboarddata,
			WorkGroup projectData) {
		JSONObject resrvationValue = new JSONObject();
		resrvationValue.put("device", deviceData.getdHostName());
		if (deviceData.getCustSiteId() != null) {
			resrvationValue.put("localtion",
					deviceDiscoveryController.setSiteDetails(deviceData.getCustSiteId(), deviceData));
		}
		PortEntity portData = portEntityRepository.findByPortId(dashboarddata.getRpPortId());
		resrvationValue.put("port", portData.getPortName());
		resrvationValue.put("reservationStatus", dashboarddata.getRpReservationStatus());
		if (projectData.getCreatedDate() != null) {			
			resrvationValue.put("startDate",dateUtil
					.dateTimeInAppFormat(dashboarddata.getRpTo().toString()));
		} else {
			resrvationValue.put("startDate", "");
		}
		if (projectData.getEndDate() != null) {			
			resrvationValue.put("endDate", dateUtil
					.dateTimeInAppFormat(dashboarddata.getRpFrom().toString()));
		} else {
			resrvationValue.put("endDate", "");
		}
		PortUsageEntity portUsageData = portUsageRepository.findByPuPortId(dashboarddata.getRpPortId());
		resrvationValue.put("usage", portUsageData.getPuUsage());
		return resrvationValue;
	}

	@SuppressWarnings("unchecked")
	public JSONObject serachData(JSONObject jsonRequest) {
		String key = null;
		String value = null;
		if (jsonRequest.containsKey("key") && jsonRequest.get("key") != null) {
			key = jsonRequest.get("key").toString();
		}
		if (jsonRequest.containsKey("value") && jsonRequest.get("value") != null) {
			value = jsonRequest.get("value").toString();
		}
		JSONArray resrvationDashboardData = new JSONArray();

		int count = 0;
		if (key != null && value != null) {
			if ("projectName".equals(key)) {				
				List<WorkGroup> projecValues = workGroupRepository.findByWorkGroupNameContains(value);
				if (projecValues != null && !projecValues.isEmpty()) {					
					for (WorkGroup projecValue : projecValues) {
						JSONObject reservationData = new JSONObject();
						WorkGroup projectData = workGroupRepository
								.findAllByWorkGroupIdAndWorkGroupType(projecValue.getWorkGroupId(), "Project");
						reservationData.put("project", projectData.getWorkGroupName());
						List<ReservationPortStatusEntity> reservationPortData = reservationPortStatusRepository
								.findAllByRpProjectId(projecValue.getWorkGroupId());
						reservationData.put("projectStatus", projectData.getWorkGroupStatus());
						if (projectData.getEndDate() != null) {									
							reservationData.put("projectEndDate", dateUtil.dateTimeInAppFormat(projectData.getEndDate().toString()));
						} else {
							reservationData.put("projectEndDate", "");
						}
						JSONArray dataValue = new JSONArray();
						for (ReservationPortStatusEntity dashboarddata : reservationPortData) {
							DeviceDiscoveryEntity deviceData = deviceDiscoveryRepository
									.findAllByDId(dashboarddata.getRpDeviceId());
							dataValue.add(setReservationData(deviceData, dashboarddata, projectData));
							if ("Reserved".equals(dashboarddata.getRpReservationStatus())) {
								count = count + 1;
							}
						}
						reservationData.put("childList", dataValue);
						resrvationDashboardData.add(reservationData);
					}
				}
			}
		}
		if ("deviceName".equals(key)) {
			List<DeviceDiscoveryEntity> deviceDatas = deviceDiscoveryRepository.findBydHostNameContaining(value);
			if (deviceDatas != null && !deviceDatas.isEmpty()) {
				for (DeviceDiscoveryEntity deviceData : deviceDatas) {
					List<ReservationPortStatusEntity> resrvationDeviceData = reservationPortStatusRepository
							.findAllByRpDeviceId(deviceData.getdId());

					for (ReservationPortStatusEntity data : resrvationDeviceData) {
						JSONObject reservationData = new JSONObject();
						WorkGroup projectData = workGroupRepository
								.findAllByWorkGroupIdAndWorkGroupType(data.getRpProjectId(), "Project");
						reservationData.put("project", projectData.getWorkGroupName());
						reservationData.put("projectStatus", projectData.getWorkGroupStatus());
						if (projectData.getEndDate() != null) {
							String endDate = StringUtils.substringBefore((projectData.getEndDate().toString()), " ")
									.replace("-", "/");
							reservationData.put("projectEndDate", endDate);
						} else {
							reservationData.put("projectEndDate", "");
						}
						if ("Reserved".equals(data.getRpReservationStatus())) {
							count = count + 1;
						}
						JSONArray dataValue = new JSONArray();
						dataValue.add(setReservationData(deviceData, data, projectData));
						reservationData.put("childList", dataValue);
						resrvationDashboardData.add(reservationData);
					}
				}
			}
		}
		Collections.reverse(resrvationDashboardData);
		JSONObject outputObject = new JSONObject();
		outputObject.put("output", resrvationDashboardData.toString());
		outputObject.put("count", count);
		JSONObject finalObject = new JSONObject();
		finalObject.put("entity", outputObject);

		return finalObject;
	}

}

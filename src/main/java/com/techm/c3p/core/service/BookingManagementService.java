package com.techm.c3p.core.service;

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

import com.techm.c3p.core.entitybeans.BookingPortStatusEntity;
import com.techm.c3p.core.entitybeans.DeviceDiscoveryEntity;
import com.techm.c3p.core.entitybeans.PortEntity;
import com.techm.c3p.core.entitybeans.PortUsageEntity;
import com.techm.c3p.core.entitybeans.WorkGroup;
import com.techm.c3p.core.repositories.BookingInformationRepository;
import com.techm.c3p.core.repositories.BookingPortStatusRepository;
import com.techm.c3p.core.repositories.DeviceDiscoveryRepository;
import com.techm.c3p.core.repositories.PortEntityRepository;
import com.techm.c3p.core.repositories.PortUsageRepository;
import com.techm.c3p.core.repositories.WorkGroupRepository;
import com.techm.c3p.core.rest.DeviceDiscoveryController;

@Service
public class BookingManagementService {
	private static final Logger logger = LogManager.getLogger(BookingManagementService.class);

	@Autowired
	private BookingPortStatusRepository bookingPortStatusRepository;

	@Autowired
	private BookingInformationRepository bookingInformationRepository;

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

	@SuppressWarnings("unchecked")
	public JSONObject getDahsboardData() {
		List<BookingPortStatusEntity> bookingInformation = bookingPortStatusRepository.findAll();
		int count = 0;
		Set<String> projectIds = new HashSet<>();
		bookingInformation.forEach(data -> {
			if (data != null) {
				projectIds.add(data.getBpProjectId());
			}
		});
		JSONArray bookingDashboardData = new JSONArray();
		for (String projectId : projectIds) {
			JSONObject bookingData = new JSONObject();
			WorkGroup projectData = workGroupRepository.findAllByWorkGroupIdAndWorkGroupType(projectId, "Project");
			bookingData.put("project", projectData.getWorkGroupName());
			List<BookingPortStatusEntity> bookingPortData = bookingPortStatusRepository.findAllByBpProjectId(projectId);
			bookingData.put("projectStatus", projectData.getWorkGroupStatus());
			if (projectData.getEndDate() != null) {
				String endDate = StringUtils.substringBefore((projectData.getEndDate().toString()), " ").replace("-",
						"/");
				bookingData.put("projectEndDate", endDate);
			} else {
				bookingData.put("projectEndDate", "");
			}
			JSONArray dataValue = new JSONArray();
			for (BookingPortStatusEntity dashboarddata : bookingPortData) {
				DeviceDiscoveryEntity deviceData = deviceDiscoveryRepository
						.findAllByDId(dashboarddata.getBpDeviceId());
				if ("Booked".equals(dashboarddata.getBpBookingStatus())) {
					count = count + 1;
				}
				dataValue.add(setBookingData(deviceData, dashboarddata, projectData));

			}
			bookingData.put("childList", dataValue);
			bookingDashboardData.add(bookingData);
		}
		JSONObject outputObject = new JSONObject();
		outputObject.put("output", bookingDashboardData.toString());
		outputObject.put("count", count);
		JSONObject finalObject = new JSONObject();
		finalObject.put("entity", outputObject);
		return finalObject;
	}

	@SuppressWarnings("unchecked")
	private JSONObject setBookingData(DeviceDiscoveryEntity deviceData, BookingPortStatusEntity dashboarddata,
			WorkGroup projectData) {
		JSONObject bookingValue = new JSONObject();
		bookingValue.put("device", deviceData.getdHostName());
		if (deviceData.getCustSiteId() != null) {
			bookingValue.put("localtion",
					deviceDiscoveryController.setSiteDetails(deviceData.getCustSiteId(), deviceData));
		}
		PortEntity portData = portEntityRepository.findByPortId(dashboarddata.getBpPortId());
		bookingValue.put("port", portData.getPortName());
		bookingValue.put("bookingStatus", dashboarddata.getBpBookingStatus());
		if (projectData.getCreatedDate() != null) {
			String startDate = StringUtils.substringBefore((dashboarddata.getBpTo().toString()), " ").replace("-", "/");
			bookingValue.put("startDate", startDate);
		} else {
			bookingValue.put("startDate", "");
		}
		if (projectData.getEndDate() != null) {
			String endDate = StringUtils.substringBefore((dashboarddata.getBpFrom().toString()), " ").replace("-", "/");
			bookingValue.put("endDate", endDate);
		} else {
			bookingValue.put("endDate", "");
		}
		PortUsageEntity portUsageData = portUsageRepository.findByPuPortId(dashboarddata.getBpPortId());
		bookingValue.put("usage", portUsageData.getPuUsage());
		return bookingValue;
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
		JSONArray bookingDashboardData = new JSONArray();

		int count = 0;
		if (key != null && value != null) {
			if ("projectName".equals(key)) {
				JSONObject bookingData = new JSONObject();
				WorkGroup projecValue = workGroupRepository.findByWorkGroupName(value);
				if (projecValue != null) {
					WorkGroup projectData = workGroupRepository
							.findAllByWorkGroupIdAndWorkGroupType(projecValue.getWorkGroupId(), "Project");
					bookingData.put("project", projectData.getWorkGroupName());
					List<BookingPortStatusEntity> bookingPortData = bookingPortStatusRepository
							.findAllByBpProjectId(projecValue.getWorkGroupId());
					bookingData.put("projectStatus", projectData.getWorkGroupStatus());
					if (projectData.getEndDate() != null) {
						String endDate = StringUtils.substringBefore((projectData.getEndDate().toString()), " ")
								.replace("-", "/");
						bookingData.put("projectEndDate", endDate);
					} else {
						bookingData.put("projectEndDate", "");
					}
					JSONArray dataValue = new JSONArray();
					for (BookingPortStatusEntity dashboarddata : bookingPortData) {
						DeviceDiscoveryEntity deviceData = deviceDiscoveryRepository
								.findAllByDId(dashboarddata.getBpDeviceId());
						dataValue.add(setBookingData(deviceData, dashboarddata, projectData));
						if ("Booked".equals(dashboarddata.getBpBookingStatus())) {
							count = count + 1;
						}
					}
					bookingData.put("childList", dataValue);
					bookingDashboardData.add(bookingData);
				}
			}
		}
		if ("deviceName".equals(key)) {
			DeviceDiscoveryEntity deviceData = deviceDiscoveryRepository.findByDHostName(value);
			if (deviceData != null) {
				List<BookingPortStatusEntity> bookingDeviceData = bookingPortStatusRepository
						.findAllByBpDeviceId(deviceData.getdId());

				for (BookingPortStatusEntity data : bookingDeviceData) {
					JSONObject bookingData = new JSONObject();
					WorkGroup projectData = workGroupRepository
							.findAllByWorkGroupIdAndWorkGroupType(data.getBpProjectId(), "Project");
					bookingData.put("project", projectData.getWorkGroupName());
					bookingData.put("projectStatus", projectData.getWorkGroupStatus());
					if (projectData.getEndDate() != null) {
						String endDate = StringUtils.substringBefore((projectData.getEndDate().toString()), " ")
								.replace("-", "/");
						bookingData.put("projectEndDate", endDate);
					} else {
						bookingData.put("projectEndDate", "");
					}
					if ("Booked".equals(data.getBpBookingStatus())) {
						count = count + 1;
					}
					JSONArray dataValue = new JSONArray();
					dataValue.add(setBookingData(deviceData, data, projectData));
					bookingData.put("childList", dataValue);
					bookingDashboardData.add(bookingData);
				}
			}
		}

		JSONObject outputObject = new JSONObject();
		outputObject.put("output", bookingDashboardData.toString());
		outputObject.put("count", count);
		JSONObject finalObject = new JSONObject();
		finalObject.put("entity", outputObject);

		return finalObject;
	}

}

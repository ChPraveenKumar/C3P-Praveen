package com.techm.c3p.core.mapper;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.techm.c3p.core.entitybeans.RequestInfoEntity;
import com.techm.c3p.core.pojo.RequestInfoCreateConfig;

public class RequestInfoMappper {

	public RequestInfoCreateConfig setEntityToPojo(RequestInfoEntity entity) {
		RequestInfoCreateConfig request = null;
		if (entity != null) {
			request = new RequestInfoCreateConfig();
			request.setInfoId(entity.getInfoId());
			request.setOs(entity.getOs());
			request.setAlphanumericReqId(entity.getAlphanumericReqId());
			request.setOsVersion(entity.getOsVersion());
			request.setManagmentIP(entity.getManagmentIP());
			request.setVendor(entity.getVendor());
			request.setCustomer(entity.getCustomer());
			request.setRegion(entity.getRegion());
			request.setSiteName(entity.getSiteName());
			request.setSiteid(entity.getSiteId());
			request.setRequestVersion(entity.getRequestVersion());
			request.setRequestCreatorName(entity.getRequestCreatorName());
			request.setHostname(entity.getHostName());
			request.setNetworkType(entity.getNetworkType());
			if (entity.getRequestOwnerName() != null) {
				request.setRequestOwner(entity.getRequestOwnerName());
			}
			request.setFamily(entity.getFamily());
			request.setTemplateID(entity.getTemplateUsed());
			request.setRequestCreatedOn(entity.getDateofProcessing().toString());
			if (entity.getEndDateOfProcessing() != null) {
				request.setEndDateOfProcessing(entity.getEndDateOfProcessing().toString());
			}
			request.setReadFE(entity.getReadFE());
			request.setReadSE(entity.getReadSE());
//			request.setDeviceType(entity.getDeviceType());
			request.setFamily(entity.getFamily());
			request.setModel(entity.getModel());
			request.setRequestType(entity.getRequestType());
			request.setStatus(entity.getStatus());
			if (entity.getStartUp() != null) {
				request.setStartUp(entity.getStartUp());
			}
			if (entity.getRequestElapsedTime() != null) {
				request.setRequestElapsedTime(entity.getRequestElapsedTime());
			}

			request.setCloudPlatform(entity.getrCloudName());
			request.setCluster(entity.getrClusterName());
			request.setProject(entity.getrProjecName());
			if (entity.getStatus().equalsIgnoreCase("Success") || entity.getStatus().equalsIgnoreCase("success")) {
				/*
				 * SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				 * Timestamp endDateOfProcessing = entity.getEndDateOfProcessing(); Date d1_d =
				 * null; Date d2_d = null;
				 * 
				 * try { d1_d = format.parse((covnertTStoString(dateOfProcession))); d2_d =
				 * format.parse((covnertTStoString(endDateOfProcessing))); } catch
				 * (ParseException e) { // TODO Auto-generated catch block e.printStackTrace();
				 * } String elapsedtime = null; // in milliseconds long diff = d2_d.getTime() -
				 * d1_d.getTime();
				 * 
				 * long diffSeconds = diff / 1000 % 60; long diffMinutes = diff / (60 * 1000) %
				 * 60; long diffHours = diff / (60 * 60 * 1000) % 24; long diffDays = diff / (24
				 * * 60 * 60 * 1000);
				 * 
				 * long dayTohours = diffDays * 24;
				 * 
				 * DecimalFormat formatter = new DecimalFormat("00"); String sec =
				 * formatter.format(diffSeconds); String min = formatter.format(diffMinutes);
				 * String hrs = formatter.format(diffHours + dayTohours);
				 * 
				 * elapsedtime = hrs + ":" + min + ":" + sec;
				 * 
				 */
				request.setRequestElapsedTime(entity.getRequestElapsedTime());
			} else if (entity.getStatus().equalsIgnoreCase("Scheduled")) {
				Timestamp scheduledTime = entity.getSceheduledTime();

				if (scheduledTime != null) {
					request.setSceheduledTime(covnertTStoString(scheduledTime));
				}
				{
					request.setRequestElapsedTime("00:00:00");
				}

			}
		}
		return request;
	}

	public String covnertTStoString(Timestamp indate) {
		String dateString = null;
		Date date = new Date();
		date.setTime(indate.getTime());
		dateString = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(date);
		return dateString;
	}
}

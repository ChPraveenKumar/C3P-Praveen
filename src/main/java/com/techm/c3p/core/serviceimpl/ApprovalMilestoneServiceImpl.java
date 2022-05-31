package com.techm.c3p.core.serviceimpl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.c3p.core.entitybeans.ReservationPortStatusEntity;
import com.techm.c3p.core.pojo.ApprovalMilestoneRequestPojo;
import com.techm.c3p.core.pojo.ApprovalMilestoneResponse;
import com.techm.c3p.core.repositories.ReservationPortStatusRepository;
import com.techm.c3p.core.service.ApprovalMilestoneService;

@Service
public class ApprovalMilestoneServiceImpl implements ApprovalMilestoneService {
	private static final Logger logger = LogManager.getLogger(ApprovalMilestoneServiceImpl.class);

	@Autowired
	private ReservationPortStatusRepository reservationPortStatusRepository;

	public ApprovalMilestoneResponse getApprovalStatus(ApprovalMilestoneRequestPojo request) {
		ApprovalMilestoneResponse response = new ApprovalMilestoneResponse();
		String reservationId = request.getRequestID();
		List<ReservationPortStatusEntity> entity = reservationPortStatusRepository.findByRpReservationId(reservationId);

		if (entity != null) {
			String status="";
			long reserveCount = entity.stream().filter(e -> e.getRpReservationStatus().equalsIgnoreCase("reserved")).count();
			if(reserveCount>0) {
				status = "reserved";
			}
			long pendingCount = entity.stream().filter(e -> e.getRpReservationStatus().equalsIgnoreCase("pending")).count();
			if(pendingCount>0) {
				status = "pending";
			}
			
			if(status=="") {
				status="NOT FOUND";
			}
			
			response.setApprovalStatus(status);
			response.setRequestId(request.getRequestID());
			response.setVersion(request.getVersion());

		}
		return response;
	}
}
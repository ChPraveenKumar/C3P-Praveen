package com.techm.c3p.core.service;

import com.techm.c3p.core.pojo.ApprovalMilestoneRequestPojo;
import com.techm.c3p.core.pojo.ApprovalMilestoneResponse;


public interface ApprovalMilestoneService {
	
		public ApprovalMilestoneResponse getApprovalStatus(ApprovalMilestoneRequestPojo request);	
}
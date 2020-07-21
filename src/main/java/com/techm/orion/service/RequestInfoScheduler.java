
package com.techm.orion.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.entitybeans.BatchIdEntity;
import com.techm.orion.entitybeans.RequestInfoEntity;
import com.techm.orion.entitybeans.WebServiceEntity;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.repositories.BatchInfoRepo;
import com.techm.orion.repositories.RequestInfoDetailsRepositories;
import com.techm.orion.repositories.WebServiceRepo;

/*  @Controller
  
  @RequestMapping("/searchdeviceinventoryNew")*/

@Controller
public class RequestInfoScheduler {

	@Autowired
	public RequestInfoDetailsRepositories requestInfoDetailsRepositories;

	@Autowired
	public BatchInfoRepo batchInfoRepo;

	@Autowired
	RequestInfoDao dao;

	@Autowired
	public WebServiceRepo webServiceRepo;

	private static final Logger logger = LogManager.getLogger(RequestInfoScheduler.class);


	/*
	 * @GET
	 * 
	 * @RequestMapping(value = "/getAllBatchRequest", method = RequestMethod.GET,
	 * produces = "application/json")
	 */
	
	@Scheduled(cron = "0/5 * * * * *")	
	public void fetchDBJob() {
		CreateConfigRequestDCM configRequest = new CreateConfigRequestDCM();
		String tempBatchId = null, tempId = null;
		List<BatchIdEntity> entity = batchInfoRepo.findAll();

		List<RequestInfoEntity> detailsList = new ArrayList<RequestInfoEntity>();
		WebServiceEntity obj = new WebServiceEntity();

		for (int i = 0; i < entity.size(); i++) {

			if (entity.get(i).getBatchStatus().equals("In Progress")) {
				tempBatchId = entity.get(i).getBatchId();

				detailsList = requestInfoDetailsRepositories.findByBatchId(tempBatchId);
				/*Loop addition for parallel execution
				 * Owner: Ruchita Salvi
				 * Algo as below
				 */
				//Get execution status for all requests in batch
				//If execution status for all is true then check status of request
				//if anyone request is in progress then batch is in progress
				//If anyone no request is in progress then batch is completed

				List<Boolean>exec_status=new ArrayList<Boolean>();
				detailsList.forEach(item -> {
					exec_status.add(item.getExecutionStatus());

				});
				if(exec_status.contains(false))
				{
				
				for (int j = 0; j < detailsList.size(); j++) {
					/*if (detailsList.get(j).getStatus().equals("In Progress")
							|| detailsList.get(j).getStatus().equals("Awaiting"))*/

					//{
						configRequest.setRequestId(detailsList.get(j).getAlphanumericReqId());

						LocalDateTime nowDate = LocalDateTime.now();
						Timestamp timestamp = Timestamp.valueOf(nowDate);
						configRequest.setRequestCreatedOn(timestamp.toString());

						configRequest.setRequest_version(detailsList.get(j).getRequestVersion());
						configRequest.setRequestType(detailsList.get(j).getRequestType());

						if ((detailsList.get(j).getRequestType().equals("SLGB"))) {

							if ((detailsList.get(j).getExecutionStatus() == false)) {

								try {
									TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
											configRequest);
									telnetCommunicationSSH.setDaemon(true);
									telnetCommunicationSSH.start();
									System.out.println(timestamp.toString());
								} catch (Exception e) {

									logger.error(e);
								}
								
								obj = webServiceRepo.findByAlphanumericReqId(detailsList.get(j).getAlphanumericReqId());
								dao.updateRequestExecutionStatus(detailsList.get(j).getAlphanumericReqId());
								
								/*
								try {

									Thread.sleep(27000);
								} catch (Exception e) {
									logger.error(e);
								}
								obj = webServiceRepo.findByAlphanumericReqId(detailsList.get(j).getAlphanumericReqId());

								int applicationTest = obj.getApplication_test();

								if (applicationTest == 2) {

									dao.updateBatchRequestStatus(detailsList.get(j).getAlphanumericReqId());
								}

								dao.updateRequestExecutionStatus(detailsList.get(j).getAlphanumericReqId());

								try {

									Thread.sleep(35000);
								} catch (Exception e) {
									logger.error(e);
								}
								*/
							}
						} else if (detailsList.get(j).getRequestType().equals("Config MACD")) {
							if ((detailsList.get(j).getExecutionStatus() == false)) {

								try {
									TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
											configRequest);
									telnetCommunicationSSH.setDaemon(true);
									telnetCommunicationSSH.start();
								} catch (Exception e) {

									logger.error(e);
								}
								
								obj = webServiceRepo.findByAlphanumericReqId(detailsList.get(j).getAlphanumericReqId());
								dao.updateRequestExecutionStatus(detailsList.get(j).getAlphanumericReqId());
								
								/*try {

									Thread.sleep(30000);
								} catch (Exception e) {
									logger.error(e);
								}
								obj = webServiceRepo.findByAlphanumericReqId(detailsList.get(j).getAlphanumericReqId());

								int applicationTest = obj.getApplication_test();

								if (applicationTest == 2) {
									dao.updateBatchRequestStatus(detailsList.get(j).getAlphanumericReqId());
								}

								dao.updateRequestExecutionStatus(detailsList.get(j).getAlphanumericReqId());
								try {

									Thread.sleep(45000);
								} catch (Exception e) {
									logger.error(e);
								}*/
							}
						} else if (detailsList.get(j).getRequestType().equals("Test")
								|| detailsList.get(j).getRequestType().equals("Audit")) {

							if ((detailsList.get(j).getExecutionStatus() == false)) {

								try {

									TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
											configRequest);
									telnetCommunicationSSH.setDaemon(true);
									telnetCommunicationSSH.start();
								} catch (Exception e) {

									logger.error(e);
								}
								obj = webServiceRepo.findByAlphanumericReqId(detailsList.get(j).getAlphanumericReqId());
								dao.updateRequestExecutionStatus(detailsList.get(j).getAlphanumericReqId());
								/*try {

									Thread.sleep(80000);
								} catch (Exception e) {
									logger.error(e);
								}*/
								/*obj = webServiceRepo.findByAlphanumericReqId(detailsList.get(j).getAlphanumericReqId());

								int applicationTest = obj.getApplication_test();

								if (applicationTest == 2) {
									dao.updateBatchRequestStatus(detailsList.get(j).getAlphanumericReqId());
								}

								dao.updateRequestExecutionStatus(detailsList.get(j).getAlphanumericReqId());
								try {

									Thread.sleep(100000);
								} catch (Exception e) {
									logger.error(e);
								}*/
							}

						}

					//}
				}
				}
				else
				{
					List<String>req_status=new ArrayList<String>();
					detailsList.forEach(item -> {
						req_status.add(item.getStatus());

					});
					
					if(req_status.contains("In Progress"))
					{
						//Set batch status in progress
						

					}
					else
					{
						//set batch status Completed
						dao.updateBatchStatus(tempBatchId);
					}
					
				}
			}

		}

		/*for (int i = 0; i < entity.size(); i++) {

			if (entity.get(i).getBatchStatus().equals("In Progress")) {
				tempBatchId = entity.get(i).getBatchId();

				detailsList = requestInfoDetailsRepositories.findByBatchId(tempBatchId);
				for (int j = 0; j < detailsList.size(); j++) {

					if(detailsList.get(j).getExecutionStatus()==true)
					{

					boolean tempStatus = dao.updateBatchStatus(tempBatchId);
					 }

				}

			}
		}*/

	}

}

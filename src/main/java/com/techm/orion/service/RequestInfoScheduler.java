package com.techm.orion.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.entitybeans.BatchIdEntity;
import com.techm.orion.entitybeans.RequestInfoEntity;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.repositories.BatchInfoRepo;
import com.techm.orion.repositories.RequestInfoDetailsRepositories;

//@RequestMapping("/Test")
@Controller
public class RequestInfoScheduler {
	private static final Logger logger = LogManager.getLogger(NetworkTestSSH.class);
	@Autowired
	public RequestInfoDetailsRepositories requestInfoDetailsRepositories;

	@Autowired
	public BatchInfoRepo batchInfoRepo;

	@Autowired
	RequestInfoDao dao;

	/*
	 * @GET
	 * 
	 * @RequestMapping(value = "/getAllRequest", method = RequestMethod.GET,
	 * produces = "application/json")
	 */
	
	@Scheduled(cron = "0/5 * * * * *")
	public void fetchDBJob() {
		CreateConfigRequestDCM configRequest = new CreateConfigRequestDCM();
		String tempBatchId = null;
		List<BatchIdEntity> entity = batchInfoRepo.findAll();

		List<RequestInfoEntity> detailsList = new ArrayList<RequestInfoEntity>();

		for (int i = 0; i < entity.size(); i++) {

			if (entity.get(i).getBatchStatus().equals("In Progress")) {
				tempBatchId = entity.get(i).getBatchId();

				detailsList = requestInfoDetailsRepositories.findByBatchId(tempBatchId);

				for (int j = 0; j < detailsList.size(); j++) {

					if (detailsList.get(j).getStatus().equals("In Progress")
							|| detailsList.get(j).getStatus().equals("Awaiting"))

					{
						configRequest.setRequestId(detailsList.get(j).getAlphanumericReqId());

						LocalDateTime nowDate = LocalDateTime.now();
						Timestamp timestamp = Timestamp.valueOf(nowDate);
						configRequest.setRequestCreatedOn(timestamp.toString());

						configRequest.setRequest_version(detailsList.get(j).getRequestVersion());
						configRequest.setRequestType(detailsList.get(j).getRequestType());

						logger.info(detailsList.get(j).getAlphanumericReqId());

						if (!(detailsList.get(j).getRequestType().equals("Config MACD"))) {

							TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(configRequest);
							telnetCommunicationSSH.setDaemon(true);
							telnetCommunicationSSH.start();

							try {

								Thread.sleep(100000);
							} catch (Exception e) {
								logger.error(e);
							}
						} else {

							try {

								Thread.sleep(5000);
							} catch (Exception e) {
								logger.error(e);
							}

							TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(configRequest);
							telnetCommunicationSSH.setDaemon(true);
							telnetCommunicationSSH.start();

							try {

								Thread.sleep(100000);
							} catch (Exception e) {
								logger.error(e);
							}
						}
					}
				}
			}

		}

		for (int i = 0; i < entity.size(); i++) {

			if (entity.get(i).getBatchStatus().equals("In Progress")) {
				tempBatchId = entity.get(i).getBatchId();

				detailsList = requestInfoDetailsRepositories.findByBatchId(tempBatchId);
				for (int j = 0; j < detailsList.size(); j++) {
					if (detailsList.get(j).getStatus().equals("Success")
							|| detailsList.get(j).getStatus().equals("Failure")) {
						boolean tempStatus = dao.updateBatchStatus(tempBatchId);

					}
				}
			}
		}

	}

}

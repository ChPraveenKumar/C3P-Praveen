package com.techm.orion.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.entitybeans.BatchIdEntity;
import com.techm.orion.entitybeans.RequestInfoEntity;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.repositories.BatchInfoRepo;
import com.techm.orion.repositories.RequestInfoDetailsRepositories;

//@RequestMapping("/Test")
@Controller
public class RequestInfoScheduler {

	@Autowired
	public RequestInfoDetailsRepositories requestInfoDetailsRepositories;

	@Autowired
	public BatchInfoRepo batchInfoRepo;

	@Autowired
	RequestInfoDao dao;

	Logger log = LoggerFactory.getLogger(RequestInfoScheduler.class);

	
/*	 * @GET
	 * 
	 * @RequestMapping(value = "/getAllRequest", method = RequestMethod.GET,
	 * produces = "application/json")*/
	 
	@Scheduled(cron = "0/5 * * * * *")
	public void fetchDBJob() {
		CreateConfigRequestDCM configRequest = new CreateConfigRequestDCM();
		String tempBatchId = null, tempId = null;
		List<BatchIdEntity> entity = batchInfoRepo.findAll();

		List<RequestInfoEntity> detailsList = new ArrayList<RequestInfoEntity>();

		for (int i = 0; i < entity.size(); i++) {

			if (entity.get(i).getBatchStatus().equals("In Progress")) {
				tempBatchId = entity.get(i).getBatchId();

				detailsList = requestInfoDetailsRepositories
						.findByBatchId(tempBatchId);

				for (int j = 0; j < detailsList.size(); j++) {

					if (detailsList.get(j).getStatus().equals("In Progress")
							|| detailsList.get(j).getStatus()
									.equals("Awaiting"))

					{
						configRequest.setRequestId(detailsList.get(j)
								.getAlphanumericReqId());

						LocalDateTime nowDate = LocalDateTime.now();
						Timestamp timestamp = Timestamp.valueOf(nowDate);
						configRequest.setRequestCreatedOn(timestamp.toString());

						configRequest.setRequest_version(detailsList.get(j)
								.getRequestVersion());
						configRequest.setRequestType(detailsList.get(j)
								.getRequestType());

						System.out.println(detailsList.get(j)
								.getAlphanumericReqId());

						if (!(detailsList.get(j).getRequestType()
								.equals("Config MACD"))) {
							
							TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
									configRequest);
							telnetCommunicationSSH.setDaemon(true);
							telnetCommunicationSSH.start();

							try {

								Thread.sleep(100000);
							} catch (Exception e) {
								System.out.println(e);
							}
						} else {
							
							try {

								Thread.sleep(5000);
							} catch (Exception e) {
								System.out.println(e);
							}

							TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
									configRequest);
							telnetCommunicationSSH.setDaemon(true);
							telnetCommunicationSSH.start();

							try {

								Thread.sleep(100000);
							} catch (Exception e) {
								System.out.println(e);
							}
						}
					}
				}
			}

		}

		for (int i = 0; i < entity.size(); i++) {

			if (entity.get(i).getBatchStatus().equals("In Progress")) {
				tempBatchId = entity.get(i).getBatchId();

				detailsList = requestInfoDetailsRepositories
						.findByBatchId(tempBatchId);
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

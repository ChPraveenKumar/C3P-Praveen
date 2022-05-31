package com.techm.c3p.core.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techm.c3p.core.entitybeans.ReservationInformationEntity;
import com.techm.c3p.core.repositories.ReservationInformationRepository;

@Component
public class ReservationInfoDao {

	private static final Logger logger = LogManager.getLogger(ReservationInfoDao.class);
	
	public ReservationInformationEntity reservationInformationEntity = null;
	
	@Autowired
	private ReservationInformationRepository iReservationInformationRepo;
	
	public ReservationInfoDao() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @param riData
	 */
	public void saveReservationInfoData(ReservationInformationEntity riData) {
		iReservationInformationRepo.save(riData);		
	}

}

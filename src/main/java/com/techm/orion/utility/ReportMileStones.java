package com.techm.orion.utility;

import org.springframework.stereotype.Component;

import com.techm.orion.pojo.MileStones;

@Component
public class ReportMileStones {

	public MileStones getMileStones(String requestType) {
		MileStones mileStone = null;

		if ("SLGC".equals(requestType)) {
			mileStone = new MileStones(true, true, true, false, true, true, false, true, true, true, true);
		} else if ("SNNC".equals(requestType)) {
			mileStone = new MileStones(true, true, true, false, true, true, false, true, true, true, true);
		} else if ("SNRC".equals(requestType)) {
			mileStone = new MileStones(true, true, true, false, true, true, false, true, true, true, true);
		} else if ("SLGM".equals(requestType)) {
			mileStone = new MileStones(true, true, true, false, true, true, false, true, true, true, true);
		} else if ("SLGT".equals(requestType)) {
			mileStone = new MileStones(true, false, true, false, false, true, false, true, false, true, true);
		} else if ("SLGA".equals(requestType)) {
			mileStone = new MileStones(true, false, true, false, false, false, false, false, true, false, true);
		} else if ("SLGB".equals(requestType)) {
			mileStone = new MileStones(true, false, true, true, false, false, false, false, false, false, true);
		} else if ("SLGF".equals(requestType)) {
			mileStone = new MileStones(true, true, false, false, true, false, true, true, false, false, true);
		}
		return mileStone;
	}
}
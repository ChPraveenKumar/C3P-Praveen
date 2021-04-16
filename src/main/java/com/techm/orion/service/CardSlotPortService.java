package com.techm.orion.service;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.orion.entitybeans.CardEntity;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.PortEntity;
import com.techm.orion.entitybeans.SlotEntity;
import com.techm.orion.entitybeans.SubSlotEntity;
import com.techm.orion.repositories.CardEntityRepository;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.PortEntityRepository;
import com.techm.orion.repositories.SlotEntityRepository;
import com.techm.orion.repositories.SubSlotEntityRepository;

@Service
public class CardSlotPortService {

	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepository;

	@Autowired
	private SlotEntityRepository slotEntityRepository;

	@Autowired
	private SubSlotEntityRepository subSlotEntityRepository;

	@Autowired
	private CardEntityRepository cardEntityRepository;

	@Autowired
	private PortEntityRepository portEntityRepository;

	@SuppressWarnings("unchecked")
	public JSONObject getCardSlots(String hostName) {
		DeviceDiscoveryEntity entity = deviceDiscoveryRepository.findByDHostName(hostName);
		JSONObject slotJson = new JSONObject();
		JSONObject responseJson = new JSONObject();
		List<SlotEntity> slotEntity = slotEntityRepository.findByDeviceId(entity.getdId());
		slotEntity.forEach(slotList -> {
			List<CardEntity> card = cardEntityRepository.findByslotEntitySlotId(slotList.getSlotId());
			List<SubSlotEntity> subSlotEntity = subSlotEntityRepository.findByslotEntitySlotId(slotList.getSlotId());
			JSONArray slotArray = new JSONArray();
			JSONArray cardArray = new JSONArray();
			card.forEach(cardList -> {
				List<PortEntity> portEntityList = portEntityRepository.findBycardEntityCardId(cardList.getCardId());
				slotJson.put("name", slotList.getSlotName());
				JSONObject cardJson = new JSONObject();
				cardJson.put("name", cardList.getCardName());
				JSONArray interfaceCardArray = new JSONArray();
				if (portEntityList != null && !portEntityList.isEmpty()) {
					portEntityList.forEach(ports -> {
						JSONObject interfaceJson = new JSONObject();
						interfaceJson.put("interfaceName", ports.getPortName());
						interfaceJson.put("interfaceStatus", ports.getPortStatus());
						interfaceCardArray.add(interfaceJson);
					});
					cardJson.put("interfaces", interfaceCardArray);
					cardArray.add(cardJson);
				}
			});
			JSONArray subSlotCardArray = new JSONArray();
			JSONObject subSlotCardObject = new JSONObject();
			JSONObject subSlotCard = new JSONObject();
			JSONArray subSlotObjArray = new JSONArray();
			subSlotEntity.forEach(slot -> {
				if (subSlotEntity != null) {
					List<CardEntity> cardSubSlot = cardEntityRepository
							.findBysubSlotEntitySubSlotId(slot.getSubSlotId());
					cardSubSlot.forEach(cardSubList -> {
						List<PortEntity> subSlotPortEntityList = portEntityRepository
								.findBycardEntityCardId(cardSubList.getCardId());
						subSlotCardObject.put("subSlotName", slot.getSubSlotName());
						subSlotCard.put("name", cardSubList.getCardName());
						JSONArray interfaceSubSlotArray = new JSONArray();
						subSlotPortEntityList.forEach(subSlotList -> {
							JSONObject interfaceSubSlotJson = new JSONObject();
							interfaceSubSlotJson.put("interfaceName", subSlotList.getPortName());
							interfaceSubSlotJson.put("interfaceStatus", subSlotList.getPortStatus());
							interfaceSubSlotArray.add(interfaceSubSlotJson);
						});
						subSlotCard.put("interfaces", interfaceSubSlotArray);
						subSlotCardArray.add(subSlotCard);
						subSlotCardObject.put("cards", subSlotCardArray);
						subSlotObjArray.add(subSlotCardObject);
					});
				}
			});
			slotJson.put("name", slotList.getSlotName());
			slotJson.put("subSlots", subSlotObjArray);
			slotJson.put("cards", cardArray);
			slotArray.add(slotJson);
			responseJson.put("slots", slotArray);

			float occupiedCount = 0, utilizationCount = 0, reservedCount = 0;
			float occupiedPercentage = 0, utilizationPercentage = 0, reservedPercentage = 0;
			float nameCount = portEntityRepository.portNameCount();
			occupiedCount = portEntityRepository.statusPortNameCount("Occupied");
			utilizationCount = portEntityRepository.statusPortNameCount("Utilization");
			reservedCount = portEntityRepository.statusPortNameCount("Reserved");
			occupiedPercentage = (occupiedCount / nameCount) * 100;
			utilizationPercentage = (utilizationCount / nameCount) * 100;
			reservedPercentage = (reservedCount / nameCount) * 100;
			responseJson.put("hostName", hostName);
			responseJson.put("utilization", utilizationPercentage + "%");
			responseJson.put("occupied", occupiedPercentage + "%");
			responseJson.put("reserved", reservedPercentage + "%");
		});
		return responseJson;
	}
}
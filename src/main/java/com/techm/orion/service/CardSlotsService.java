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
public class CardSlotsService {

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
		JSONObject slotEntities = new JSONObject();
		JSONObject responseJson = new JSONObject();
		List<SlotEntity> slotEntity = slotEntityRepository.findByDeviceId(entity.getdId());
		slotEntity.forEach(slots -> {
			List<CardEntity> card = cardEntityRepository.findByslotEntitySlotId(slots.getSlotId());
			List<SubSlotEntity> subSlotEntity = subSlotEntityRepository.findByslotEntitySlotId(slots.getSlotId());
			JSONArray slotArray = new JSONArray();
			JSONArray cardArray = new JSONArray();
			card.forEach(cards -> {
				List<PortEntity> portEntityList = portEntityRepository.findBycardEntityCardId(cards.getCardId());
				slotEntities.put("name", slots.getSlotName());
				JSONObject cardEntities = new JSONObject();
				cardEntities.put("name", cards.getCardName());
				JSONArray interfaceCardArray = new JSONArray();
				if (portEntityList != null && !portEntityList.isEmpty()) {
					portEntityList.forEach(ports -> {
						JSONObject interfaceJson = new JSONObject();
						interfaceJson.put("interfaceName", ports.getPortName());
						interfaceJson.put("interfaceStatus", ports.getPortStatus());
						interfaceCardArray.add(interfaceJson);
					});
					cardEntities.put("interfaces", interfaceCardArray);
					cardArray.add(cardEntities);
				}
			});
			JSONArray subSlotCardArray = new JSONArray();
			JSONObject subSlotCardObject = new JSONObject();
			JSONObject subSlotEntities = new JSONObject();
			JSONArray subSlotObjArray = new JSONArray();
			subSlotEntity.forEach(slot -> {
				if (subSlotEntity != null) {
					List<CardEntity> cardSubSlot = cardEntityRepository
							.findBysubSlotEntitySubSlotId(slot.getSubSlotId());
					cardSubSlot.forEach(cardSubList -> {
						List<PortEntity> subSlotPortEntityList = portEntityRepository
								.findBycardEntityCardId(cardSubList.getCardId());
						subSlotCardObject.put("subSlotName", slot.getSubSlotName());
						subSlotEntities.put("name", cardSubList.getCardName());
						JSONArray interfaceSubSlotArray = new JSONArray();
						subSlotPortEntityList.forEach(subSlotList -> {
							JSONObject interfaceSubSlotJson = new JSONObject();
							interfaceSubSlotJson.put("interfaceName", subSlotList.getPortName());
							interfaceSubSlotJson.put("interfaceStatus", subSlotList.getPortStatus());
							interfaceSubSlotArray.add(interfaceSubSlotJson);
						});
						subSlotEntities.put("interfaces", interfaceSubSlotArray);
						subSlotCardArray.add(subSlotEntities);
						subSlotCardObject.put("cards", subSlotCardArray);
						subSlotObjArray.add(subSlotCardObject);
					});
				}
			});
			slotEntities.put("name", slots.getSlotName());
			slotEntities.put("subSlots", subSlotObjArray);
			slotEntities.put("cards", cardArray);
			slotArray.add(slotEntities);
			responseJson.put("slots", slotArray);

			float occupiedCount = 0, utilizationCount = 0, reservedCount = 0;
			float occupiedPercentage = 0, utilizationPercentage = 0, reservedPercentage = 0, occupied = 0;
			float nameCount = portEntityRepository.portNameCount();
			occupiedCount = portEntityRepository.statusPortNameCount("Occupied");
			utilizationCount = portEntityRepository.statusPortNameCount("Utilization");
			reservedCount = portEntityRepository.statusPortNameCount("Reserved");
			occupied = occupiedCount;
			occupiedPercentage = (occupied / nameCount) * 100;
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

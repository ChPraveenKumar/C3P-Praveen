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
		List<SlotEntity> slotEntity = slotEntityRepository.findByDeviceId(entity.getdId());
		JSONObject responseJson = new JSONObject();
		JSONArray slotArray = new JSONArray();
		slotEntity.forEach(slots -> {
			JSONObject slotEntities = new JSONObject();
			slotEntities.put("name", slots.getSlotName());
			// cards
			JSONArray cardArray = new JSONArray();
			List<CardEntity> card = cardEntityRepository.findByslotEntitySlotId(slots.getSlotId());
			card.forEach(cards -> {
				JSONObject cardEntities = new JSONObject();
				cardEntities.put("name", cards.getCardName());
				List<PortEntity> portEntityList = portEntityRepository.findBycardEntityCardId(cards.getCardId());
				JSONArray interfaceCardArray = new JSONArray();
				portEntityList.forEach(ports -> {
					JSONObject interfaceJson = new JSONObject();
					interfaceJson.put("interfaceName", ports.getPortName());
					interfaceJson.put("interfaceStatus", ports.getPortStatus());
					interfaceCardArray.add(interfaceJson);
				});
				cardEntities.put("interfaces", interfaceCardArray);
				cardArray.add(cardEntities);
			});
			// subslots
			JSONArray subSlotObjArray = new JSONArray();
			List<SubSlotEntity> subSlotEntity = subSlotEntityRepository.findByslotEntitySlotId(slots.getSlotId());
			subSlotEntity.forEach(subSlots -> {
				JSONObject subSlotCardObject = new JSONObject();
				subSlotCardObject.put("subSlotName", subSlots.getSubSlotName());
				JSONArray subSlotCardArray = new JSONArray();
				List<CardEntity> cardSubSlot = cardEntityRepository
						.findBysubSlotEntitySubSlotId(subSlots.getSubSlotId());
				cardSubSlot.forEach(cardSubSlots -> {
					JSONObject subSlotEntities = new JSONObject();
					subSlotEntities.put("name", cardSubSlots.getCardName());
					JSONArray interfaceSubSlotArray = new JSONArray();
					List<PortEntity> subSlotPortEntityList = portEntityRepository
							.findBycardEntityCardId(cardSubSlots.getCardId());
					subSlotPortEntityList.forEach(subPorts -> {
						JSONObject interfaceSubSlotJson = new JSONObject();
						interfaceSubSlotJson.put("interfaceName", subPorts.getPortName());
						interfaceSubSlotJson.put("interfaceStatus", subPorts.getPortStatus());
						interfaceSubSlotArray.add(interfaceSubSlotJson);
					});
					subSlotEntities.put("interfaces", interfaceSubSlotArray);
					subSlotCardArray.add(subSlotEntities);
				});
				subSlotCardObject.put("cards", subSlotCardArray);
				subSlotObjArray.add(subSlotCardObject);
			});

			slotEntities.put("name", slots.getSlotName());
			slotEntities.put("subSlots", subSlotObjArray);
			slotEntities.put("cards", cardArray);
			slotArray.add(slotEntities);
		});
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

		return responseJson;

	}

}

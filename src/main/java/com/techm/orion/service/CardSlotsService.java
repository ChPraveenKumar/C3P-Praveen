package com.techm.orion.service;

import java.text.DecimalFormat;
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
		float occupiedCount = 0, freeCount = 0, reservedCount = 0;
		float occupiedPercentage = 0, freePercentage = 0, reservedPercentage = 0;
		JSONObject responseJson = new JSONObject();
		if (entity != null) {
			List<SlotEntity> slotEntity = slotEntityRepository.findByDeviceId(entity.getdId());

			JSONArray slotArray = new JSONArray();
			// slots
			slotEntity.forEach(slots -> {
				JSONObject slotEntities = new JSONObject();
				slotEntities.put("name", slots.getSlotName());
				JSONArray cardArray = new JSONArray();
				List<CardEntity> card = cardEntityRepository.findByCardSlots(slots.getSlotId());
				// cards
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
					List<CardEntity> cardSubSlot = cardEntityRepository.findBySubSlots(subSlots.getSubSlotId());
					// cards
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

			float nameCount = portEntityRepository.portNameCount(entity.getdId());
			occupiedCount = portEntityRepository.statusPortNameCount(entity.getdId(), "Occupied");
			freeCount = portEntityRepository.statusPortNameCount(entity.getdId(), "Free");
			reservedCount = portEntityRepository.statusPortNameCount(entity.getdId(), "Reserved");

			occupiedPercentage = (occupiedCount / nameCount) * 100;
			freePercentage = (freeCount / nameCount) * 100;
			reservedPercentage = (reservedCount / nameCount) * 100;
			DecimalFormat df = new DecimalFormat("###.##");
			responseJson.put("hostName", hostName);
			if (freeCount != 0 || freePercentage == 0) {
				responseJson.put("free", df.format(freePercentage) + " %");
			} else {
				responseJson.put("free", "");
			}
			if (occupiedCount != 0 || occupiedPercentage == 0) {
				responseJson.put("occupied", df.format(occupiedPercentage) + " %");
			} else {
				responseJson.put("occupied", "");
			}
			if (reservedCount != 0 || reservedPercentage == 0) {
				responseJson.put("reserved", df.format(reservedPercentage) + " %");
			} else {
				responseJson.put("reserved", "");
			}
		}
		return responseJson;
	}

}

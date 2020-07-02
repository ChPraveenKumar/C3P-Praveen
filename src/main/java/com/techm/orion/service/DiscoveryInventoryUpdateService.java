package com.techm.orion.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import com.techm.orion.entitybeans.DiscoveryResultDeviceDetailsEntity;

@Component
public class DiscoveryInventoryUpdateService implements DisposableBean, Runnable {
	private static final Logger logger = LogManager.getLogger(DiscoveryInventoryUpdateService.class);
	/*
	 * @Autowired public DiscoveryResultDeviceDetailsRepository
	 * discoveryResultDeviceDetailsRepository;
	 * 
	 * @Autowired public DiscoveryResultDeviceInterfaceEntity
	 * discoveryResultDeviceInterfaceRepository;
	 */

	private Thread thread;

	public DiscoveryInventoryUpdateService() {

		this.thread = new Thread(this);
		this.thread.start();
	}

	public void load() {
		// 1. Load data from dump to main inventory

		loadDataInMainDB();

	}

	private void loadDataInMainDB() {
		try {
			List<DiscoveryResultDeviceDetailsEntity> discoveryResultList = new ArrayList<DiscoveryResultDeviceDetailsEntity>();
			// discoveryResultList=discoveryResultDeviceDetailsRepository.findAll();

			/*
			 * List<DeviceDiscoveryEntity>deviceDiscoveryList=new
			 * ArrayList<DeviceDiscoveryEntity>();
			 * deviceDiscoveryList=deviceDiscoveryRepository.findAll();
			 * 
			 * for(int i=0; i<discoveryResultList.size();i++) { DeviceDiscoveryEntity
			 * discoveryDeviceDetail=new DeviceDiscoveryEntity();
			 * if(discoveryResultList.get(i).getdCpu()!=null) {
			 * discoveryDeviceDetail.setdCPU(discoveryResultList.get(i).getdCpu()); }
			 * if(discoveryResultList.get(i).getdCpuRevision()!=null) {
			 * discoveryDeviceDetail.setdCPURevision(discoveryResultList.get(i).
			 * getdCpuRevision()); } if(discoveryResultList.get(i).getdDrmSize()!=null) {
			 * discoveryDeviceDetail.setdDRAMSize(discoveryResultList.get(i).getdDrmSize());
			 * } if(discoveryResultList.get(i).getdFlashSize()!=null) {
			 * discoveryDeviceDetail.setdFlashSize(discoveryResultList.get(i).getdFlashSize(
			 * )); } if(discoveryResultList.get(i).getdHostname()!=null) {
			 * discoveryDeviceDetail.setdHostName(discoveryResultList.get(i).getdHostname())
			 * ; } if(discoveryResultList.get(i).getdIpAddrsSix()!=null) {
			 * discoveryDeviceDetail.setdIPAddrSix(discoveryResultList.get(i).getdIpAddrsSix
			 * ()); } if(discoveryResultList.get(i).getdImageFile()!=null) {
			 * discoveryDeviceDetail.setdImageFileName(discoveryResultList.get(i).
			 * getdImageFile()); } if(discoveryResultList.get(i).getdMacaddress()!=null) {
			 * discoveryDeviceDetail.setdMACAddress(discoveryResultList.get(i).
			 * getdMacaddress()); } if(discoveryResultList.get(i).getdMgmtip()!=null) {
			 * discoveryDeviceDetail.setdMgmtIp(discoveryResultList.get(i).getdMgmtip()); }
			 * if(discoveryResultList.get(i).getdModel()!=null) {
			 * discoveryDeviceDetail.setdModel(discoveryResultList.get(i).getdModel()); }
			 * if(discoveryResultList.get(i).getdNvramSize()!=null) {
			 * discoveryDeviceDetail.setdNVRAMSize(discoveryResultList.get(i).getdNvramSize(
			 * )); } if(discoveryResultList.get(i).getdOs()!=null) {
			 * discoveryDeviceDetail.setdOs(discoveryResultList.get(i).getdOs()); }
			 * if(discoveryResultList.get(i).getdOsVersion()!=null) {
			 * discoveryDeviceDetail.setdOsVersion(discoveryResultList.get(i).getdOsVersion(
			 * )); } if(discoveryResultList.get(i).getdReleasever()!=null) {
			 * discoveryDeviceDetail.setdReleaseVer(discoveryResultList.get(i).
			 * getdReleasever()); } if(discoveryResultList.get(i).getdSerialNumber()!=null)
			 * { discoveryDeviceDetail.setdSerialNumber(discoveryResultList.get(i).
			 * getdSerialNumber()); } if(discoveryResultList.get(i).getdSries()!=null) {
			 * discoveryDeviceDetail.setdSeries(discoveryResultList.get(i).getdSries()); }
			 * if(discoveryResultList.get(i).getdUpsince()!=null) {
			 * discoveryDeviceDetail.setdUpSince(discoveryResultList.get(i).getdUpsince());
			 * } if(discoveryResultList.get(i).getdVendor()!=null) {
			 * discoveryDeviceDetail.setdVendor(discoveryResultList.get(i).getdVendor()); }
			 * Date date = Calendar.getInstance().getTime(); DateFormat dateFormat = new
			 * SimpleDateFormat("mm/dd/yyyy hh:mm:ss"); String strDate =
			 * dateFormat.format(date);
			 * 
			 * discoveryDeviceDetail.setdDatePolled(strDate);
			 * 
			 * 
			 * Set<DiscoveryResultDeviceInterfaceEntity>setInterfaceInDiscoveryResult=
			 * discoveryResultList.get(i).getInterfaces();
			 * List<DiscoveryResultDeviceInterfaceEntity>listInterfaceInDiscoveryResult=new
			 * ArrayList<DiscoveryResultDeviceInterfaceEntity>(setInterfaceInDiscoveryResult
			 * ); List<DeviceDiscoveryInterfaceEntity>listToAdd=new
			 * ArrayList<DeviceDiscoveryInterfaceEntity>(); for(int
			 * j=0;j<listInterfaceInDiscoveryResult.size();j++) {
			 * DeviceDiscoveryInterfaceEntity interfaceDetail=new
			 * DeviceDiscoveryInterfaceEntity();
			 * if(listInterfaceInDiscoveryResult.get(j).getiIntName()!=null) {
			 * interfaceDetail.setiIntName(listInterfaceInDiscoveryResult.get(j).getiIntName
			 * ()); } if(listInterfaceInDiscoveryResult.get(j).getiIntDescription()!=null) {
			 * interfaceDetail.setiIntDescription(listInterfaceInDiscoveryResult.get(j).
			 * getiIntDescription()); }
			 * if(listInterfaceInDiscoveryResult.get(j).getiIntIpaddr()!=null) {
			 * interfaceDetail.setiIntIpaddr(listInterfaceInDiscoveryResult.get(j).
			 * getiIntIpaddr()); }
			 * if(listInterfaceInDiscoveryResult.get(j).getiIntSubnet()!=null) {
			 * interfaceDetail.setiIntSubnet(listInterfaceInDiscoveryResult.get(j).
			 * getiIntSubnet()); }
			 * if(listInterfaceInDiscoveryResult.get(j).getiIntIpv6addr()!=null) {
			 * interfaceDetail.setiIntIpv6addr(listInterfaceInDiscoveryResult.get(j).
			 * getiIntIpv6addr()); }
			 * if(listInterfaceInDiscoveryResult.get(j).getiIntPrefix()!=null) {
			 * interfaceDetail.setiIntPrefix(listInterfaceInDiscoveryResult.get(j).
			 * getiIntPrefix());
			 * 
			 * } if(listInterfaceInDiscoveryResult.get(j).getiIntAdminStat()!=null) {
			 * interfaceDetail.setiIntAdminStat(listInterfaceInDiscoveryResult.get(j).
			 * getiIntAdminStat()); }
			 * if(listInterfaceInDiscoveryResult.get(j).getiIntOperStat()!=null) {
			 * interfaceDetail.setiIntOperStat(listInterfaceInDiscoveryResult.get(j).
			 * getiIntOperStat()); }
			 * if(listInterfaceInDiscoveryResult.get(j).getiIntPhyAddr()!=null) {
			 * interfaceDetail.setiIntPhyAddr(listInterfaceInDiscoveryResult.get(j).
			 * getiIntPhyAddr()); } interfaceDetail.setDevice(discoveryDeviceDetail);
			 * listToAdd.add(interfaceDetail);
			 */

			// }

			// Set<DeviceDiscoveryInterfaceEntity> tSet = new
			// HashSet<DeviceDiscoveryInterfaceEntity>(listToAdd);

			// discoveryDeviceDetail.setInterfaces(tSet);

			// }
		} catch (Exception e) {
			logger.info("Exception " + e);
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		loadDataInMainDB();
	}

	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub

	}

}
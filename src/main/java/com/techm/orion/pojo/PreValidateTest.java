package com.techm.orion.pojo;

import java.io.Serializable;

public class PreValidateTest implements Serializable{
	
    	/**
     * 
     */
    private static final long serialVersionUID = -4172068664361517925L;
	private String deviceReachableStatus="Failed";
    	private String vendorGUIValue;
    	private String modelGUIValue;
    	private String osVersionGUIValue;
    	private String vendorActualValue;
    	private String modelActualValue;
    	private String osVersionActualValue;
    	
    	private String vendorTestStatus="Failed";
    	private String modelTestStatus="Failed";
    	private String osVersionTestStatus="Failed";
    	private String deviceMountingStatus="Failed";
		public String getDeviceMountingStatus() {
			return deviceMountingStatus;
		}
		public void setDeviceMountingStatus(String deviceMountingStatus) {
			this.deviceMountingStatus = deviceMountingStatus;
		}
		public String getDeviceReachableStatus() {
			return deviceReachableStatus;
		}
		public void setDeviceReachableStatus(String deviceReachableStatus) {
			this.deviceReachableStatus = deviceReachableStatus;
		}
		public String getVendorGUIValue() {
			return vendorGUIValue;
		}
		public void setVendorGUIValue(String vendorGUIValue) {
			this.vendorGUIValue = vendorGUIValue;
		}
		public String getModelGUIValue() {
			return modelGUIValue;
		}
		public void setModelGUIValue(String modelGUIValue) {
			this.modelGUIValue = modelGUIValue;
		}
		public String getOsVersionGUIValue() {
			return osVersionGUIValue;
		}
		public void setOsVersionGUIValue(String osVersionGUIValue) {
			this.osVersionGUIValue = osVersionGUIValue;
		}
		public String getVendorActualValue() {
			return vendorActualValue;
		}
		public void setVendorActualValue(String vendorActualValue) {
			this.vendorActualValue = vendorActualValue;
		}
		public String getModelActualValue() {
			return modelActualValue;
		}
		public void setModelActualValue(String modelActualValue) {
			this.modelActualValue = modelActualValue;
		}
		public String getOsVersionActualValue() {
			return osVersionActualValue;
		}
		public void setOsVersionActualValue(String osVersionActualValue) {
			this.osVersionActualValue = osVersionActualValue;
		}
		public String getVendorTestStatus() {
			return vendorTestStatus;
		}
		public void setVendorTestStatus(String vendorTestStatus) {
			this.vendorTestStatus = vendorTestStatus;
		}
		public String getModelTestStatus() {
			return modelTestStatus;
		}
		public void setModelTestStatus(String modelTestStatus) {
			this.modelTestStatus = modelTestStatus;
		}
		public String getOsVersionTestStatus() {
			return osVersionTestStatus;
		}
		public void setOsVersionTestStatus(String osVersionTestStatus) {
			this.osVersionTestStatus = osVersionTestStatus;
		}
    	
    	
    	
	
	
   
	
}

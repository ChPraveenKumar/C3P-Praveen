package com.techm.c3p.core.pojo;

public class CloudPojo {

	private String cloudPlatform;
	private String cloudProject;
	private String clusterName;
	private String clusterNetworkName;
	private String clusterNodePoolName;
	private String diskSize;
	private String numberOfNodes;
	private String clusterLocation;
	private String machineType;
	
	private String podName;
	private String podClusterName;
	private String numberOfPods;
	private String podImagename;
	private String podNamespace;
	
	public String getMachineType() {
		return machineType;
	}
	public void setMachineType(String machineType) {
		this.machineType = machineType;
	}
	
	public String getCloudPlatform() {
		return cloudPlatform;
	}
	public void setCloudPlatform(String cloudPlatform) {
		this.cloudPlatform = cloudPlatform;
	}
	public String getCloudProject() {
		return cloudProject;
	}
	public void setCloudProject(String cloudProject) {
		this.cloudProject = cloudProject;
	}
	public String getClusterName() {
		return clusterName;
	}
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
	public String getClusterNetworkName() {
		return clusterNetworkName;
	}
	public void setClusterNetworkName(String clusterNetworkName) {
		this.clusterNetworkName = clusterNetworkName;
	}
	public String getClusterNodePoolName() {
		return clusterNodePoolName;
	}
	public void setClusterNodePoolName(String clusterNodePoolName) {
		this.clusterNodePoolName = clusterNodePoolName;
	}
	public String getDiskSize() {
		return diskSize;
	}
	public void setDiskSize(String diskSize) {
		this.diskSize = diskSize;
	}
	public String getNumberOfNodes() {
		return numberOfNodes;
	}
	public void setNumberOfNodes(String numberOfNodes) {
		this.numberOfNodes = numberOfNodes;
	}
	public String getClusterLocation() {
		return clusterLocation;
	}
	public void setClusterLocation(String clusterLocation) {
		this.clusterLocation = clusterLocation;
	}
	public String getPodName() {
		return podName;
	}
	public void setPodName(String podName) {
		this.podName = podName;
	}
	public String getPodClusterName() {
		return podClusterName;
	}
	public void setPodClusterName(String podClusterName) {
		this.podClusterName = podClusterName;
	}
	public String getNumberOfPods() {
		return numberOfPods;
	}
	public void setNumberOfPods(String numberOfPods) {
		this.numberOfPods = numberOfPods;
	}
	public String getPodImagename() {
		return podImagename;
	}
	public void setPodImagename(String podImagename) {
		this.podImagename = podImagename;
	}
	public String getPodNamespace() {
		return podNamespace;
	}
	public void setPodNamespace(String podNamespace) {
		this.podNamespace = podNamespace;
	}

}

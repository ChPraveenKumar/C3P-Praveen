package com.techm.c3p.core.pojo;

public class MileStones {
	private boolean start;
	private boolean generate;
	private boolean preProcess;
	private boolean instantiate;
	private boolean preValidate;
	private boolean backup;
	private boolean backupDelivery;
	private boolean networkTest;
	private boolean preHealthCheck;
	private boolean healthCheck;
	private boolean networkAudit;
	private boolean approval;
	private boolean others;
	private boolean report;
	
	public MileStones(boolean start, boolean generate,boolean preProcess,boolean instantiate, boolean preValidate, boolean backup, boolean backupDelivery,
			boolean networkTest, boolean preHealthCheck, boolean healthCheck, boolean networkAudit, boolean approval, boolean others,
			boolean report) {
		super();
		this.start = start;
		this.generate = generate;
		this.preProcess = preProcess;
		this.instantiate=instantiate;
		this.preValidate = preValidate;
		this.backup = backup;
		this.backupDelivery = backupDelivery;
		this.networkTest = networkTest;
		this.preHealthCheck = preHealthCheck;
		this.healthCheck = healthCheck;
		this.networkAudit = networkAudit;
		this.approval = approval;
		this.others = others;
		this.report = report;
	}
	public boolean isInstantiate() {
		return instantiate;
	}
	public void setInstantiate(boolean instantiate) {
		this.instantiate = instantiate;
	}
	public boolean isStart() {
		return start;
	}
	public void setStart(boolean start) {
		this.start = start;
	}
	public boolean isGenerate() {
		return generate;
	}
	public void setGenerate(boolean generate) {
		this.generate = generate;
	}
	public boolean isPreValidate() {
		return preValidate;
	}
	public void setPreValidate(boolean preValidate) {
		this.preValidate = preValidate;
	}
	public boolean isBackup() {
		return backup;
	}
	public void setBackup(boolean backup) {
		this.backup = backup;
	}
	public boolean isBackupDelivery() {
		return backupDelivery;
	}
	public void setBackupDelivery(boolean backupDelivery) {
		this.backupDelivery = backupDelivery;
	}
	public boolean isNetworkTest() {
		return networkTest;
	}
	public void setNetworkTest(boolean networkTest) {
		this.networkTest = networkTest;
	}
	public boolean isPreHealthCheck() {
		return preHealthCheck;
	}
	public void setPreHealthCheck(boolean preHealthCheck) {
		this.preHealthCheck = preHealthCheck;
	}
	public boolean isHealthCheck() {
		return healthCheck;
	}
	public void setHealthCheck(boolean healthCheck) {
		this.healthCheck = healthCheck;
	}
	public boolean isNetworkAudit() {
		return networkAudit;
	}
	public void setNetworkAudit(boolean networkAudit) {
		this.networkAudit = networkAudit;
	}
	public boolean isOthers() {
		return others;
	}
	public void setOthers(boolean others) {
		this.others = others;
	}
	public boolean isReport() {
		return report;
	}
	public void setReport(boolean report) {
		this.report = report;
	}
	public boolean isPreProcess() {
		return preProcess;
	}
	public void setPreProcess(boolean preProcess) {
		this.preProcess = preProcess;
	}
	
	public boolean isApproval() {
		return approval;
	}
	public void setApproval(boolean approval) {
		this.approval = approval;
	}
}

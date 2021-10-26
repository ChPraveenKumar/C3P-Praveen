package com.techm.c3p.core.pojo;

public class EIPAMPojo {
    private String site = null;
    private String customer = null;
    private String ip=null;
    private String mask=null;
    private String region=null;

    private boolean isIpUsed=false;
    public boolean isIpUsed() {
		return isIpUsed;
	}
	public void setIpUsed(boolean isIpUsed) {
		this.isIpUsed = isIpUsed;
	}
	private String service=null;
    private int status=0;

    public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	/**
     * @return the region
     */
    public String getRegion() {
        return region;
    }
    /**
     * @param region the region to set
     */
    public void setRegion(String region) {
        this.region = region;
    }
    /**
     * @return the service
     */
    public String getService() {
        return service;
    }
    /**
     * @param service the service to set
     */
    public void setService(String service) {
        this.service = service;
    }
    /**
     * @return the mask
     */
    public String getMask() {
        return mask;
    }
    /**
     * @param mask the mask to set
     */
    public void setMask(String mask) {
        this.mask = mask;
    }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getSite() {
        return site;
    }
    public void setSite(String site) {
        this.site = site;
    }
    public String getCustomer() {
        return customer;
    }
    public void setCustomer(String customer) {
        this.customer = customer;
    }

    
}

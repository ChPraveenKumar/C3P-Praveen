package com.techm.c3p.core.beans;

public class DeviceInterfaceSO {

	private String name;
	private String description;
	private String ip;
	private String mask;
	private String speed;
	private String encapsulation;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getEncapsulation() {
		return encapsulation;
	}

	public void setEncapsulation(String encapsulation) {
		this.encapsulation = encapsulation;
	}

	@Override
	public String toString() {
		return "DeviceInterfaceSO [name=" + name + ", description=" + description + ", ip=" + ip + ", mask=" + mask
				+ ", speed=" + speed + ", encapsulation=" + encapsulation + "]";
	}

}

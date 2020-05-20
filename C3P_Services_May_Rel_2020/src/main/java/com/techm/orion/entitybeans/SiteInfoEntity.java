package com.techm.orion.entitybeans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "c3p_custSiteinfo")
public class SiteInfoEntity {

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	@Column(name = "id")
	private int id;
	
	@Column(name = "c_site_name")
	private String cSiteName;
	
	@Column(name = "c_site_id")
	private String cSiteId;
	
	@Column(name = "c_cust_id")
	private String cCustId;
	
	@Column(name = "c_cust_name")
	private String cCustName;
	
	@Column(name = "c_site_region")
	private String cSiteRegion;
	
	@Column(name = "c_site_subregion")
	private String cSiteSubRegion;
	
	@Column(name = "c_site_market")
	private String cSiteMarket;
	
	@Column(name = "c_site_contact")
	private String cSiteContact;
	
	@Column(name = "c_site_contactemail")
	private String cSiteContactEmail;
	
	@Column(name = "c_site_contactphone")
	private String cSiteContactPhone;
	
	@Column(name = "c_site_addressline1")
	private String cSiteAddressLine1;
	
	@Column(name = "c_site_addressline2")
	private String cSIteAddressLine2;
	
	@Column(name = "c_site_city")
	private String cSiteCity;
	
	@Column(name = "c_site_zip")
	private String cSiteZip;
	
	@Column(name = "c_site_state")
	private String cSiteState;
	
	@Column(name = "c_site_country")
	private String cSiteCountry;
	
	@Column(name = "c_site_status")
	private String cSiteStatus;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getcSiteName() {
		return cSiteName;
	}

	public void setcSiteName(String cSiteName) {
		this.cSiteName = cSiteName;
	}

	public String getcCustId() {
		return cCustId;
	}

	public void setcCustId(String cCustId) {
		this.cCustId = cCustId;
	}

	public String getcCustName() {
		return cCustName;
	}

	public void setcCustName(String cCustName) {
		this.cCustName = cCustName;
	}

	public String getcSiteRegion() {
		return cSiteRegion;
	}

	public void setcSiteRegion(String cSiteRegion) {
		this.cSiteRegion = cSiteRegion;
	}

	public String getcSiteSubRegion() {
		return cSiteSubRegion;
	}

	public void setcSiteSubRegion(String cSiteSubRegion) {
		this.cSiteSubRegion = cSiteSubRegion;
	}

	public String getcSiteMarket() {
		return cSiteMarket;
	}

	public void setcSiteMarket(String cSiteMarket) {
		this.cSiteMarket = cSiteMarket;
	}

	public String getcSiteContact() {
		return cSiteContact;
	}

	public void setcSiteContact(String cSiteContact) {
		this.cSiteContact = cSiteContact;
	}

	public String getcSiteContactEmail() {
		return cSiteContactEmail;
	}

	public void setcSiteContactEmail(String cSiteContactEmail) {
		this.cSiteContactEmail = cSiteContactEmail;
	}

	public String getcSiteContactPhone() {
		return cSiteContactPhone;
	}

	public void setcSiteContactPhone(String cSiteContactPhone) {
		this.cSiteContactPhone = cSiteContactPhone;
	}

	public String getcSiteAddressLine1() {
		return cSiteAddressLine1;
	}

	public void setcSiteAddressLine1(String cSiteAddressLine1) {
		this.cSiteAddressLine1 = cSiteAddressLine1;
	}

	public String getcSIteAddressLine2() {
		return cSIteAddressLine2;
	}

	public void setcSIteAddressLine2(String cSIteAddressLine2) {
		this.cSIteAddressLine2 = cSIteAddressLine2;
	}

	public String getcSiteCity() {
		return cSiteCity;
	}

	public void setcSiteCity(String cSiteCity) {
		this.cSiteCity = cSiteCity;
	}

	public String getcSiteZip() {
		return cSiteZip;
	}

	public void setcSiteZip(String cSiteZip) {
		this.cSiteZip = cSiteZip;
	}

	public String getcSiteState() {
		return cSiteState;
	}

	public void setcSiteState(String cSiteState) {
		this.cSiteState = cSiteState;
	}

	public String getcSiteCountry() {
		return cSiteCountry;
	}

	public void setcSiteCountry(String cSiteCountry) {
		this.cSiteCountry = cSiteCountry;
	}

	public String getcSiteStatus() {
		return cSiteStatus;
	}

	public void setcSiteStatus(String cSiteStatus) {
		this.cSiteStatus = cSiteStatus;
	}

	public String getcSiteId() {
		return cSiteId;
	}

	public void setcSiteId(String cSiteId) {
		this.cSiteId = cSiteId;
	}
	
	
}

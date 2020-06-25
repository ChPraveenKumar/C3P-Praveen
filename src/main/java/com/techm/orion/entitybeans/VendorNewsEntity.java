package com.techm.orion.entitybeans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "c3p_t_vendornews")
@JsonIgnoreProperties(ignoreUnknown = false)
public class VendorNewsEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4108868890119754082L;

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "vendor")
	private String vendor;

	@Column(name = "news", length = 150)
	private String news;

	@Column(name = "date")
	private String date;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getNews() {
		return news;
	}

	public void setNews(String news) {
		this.news = news;
	}

}

package com.techm.orion.entitybeans;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "t_attrib_funct_m_dropdown")
public class CategoryDropDownEntity implements Serializable {

	private static final long serialVersionUID = -7332414379037083305L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "attrib_value")
	private String attribValue;

	@Column(name = "attrib_parent_value")
	private int attribParentValue;

	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	private CategoryMasterEntity category;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAttribValue() {
		return attribValue;
	}

	public void setAttribValue(String attribValue) {
		this.attribValue = attribValue;
	}

	public int getAttribParentValue() {
		return attribParentValue;
	}

	public void setAttribParentValue(int attribParentValue) {
		this.attribParentValue = attribParentValue;
	}

	public CategoryMasterEntity getCategory() {
		return category;
	}

	public void setCategory(CategoryMasterEntity category) {
		this.category = category;
	}

	public CategoryDropDownEntity(int id, String attribValue,
			int attribParentValue, CategoryMasterEntity category) {
		super();
		this.id = id;
		this.attribValue = attribValue;
		this.attribParentValue = attribParentValue;
		this.category = category;
	}

	public CategoryDropDownEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "CategoryDropDownEntity [id=" + id + ", attribValue="
				+ attribValue + ", attribParentValue=" + attribParentValue
				+ ", category=" + category + "]";
	}
	
	

}

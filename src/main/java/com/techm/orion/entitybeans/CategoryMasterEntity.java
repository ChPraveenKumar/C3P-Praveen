package com.techm.orion.entitybeans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_attrib_funct_m_category")
public class CategoryMasterEntity implements Serializable {

	private static final long serialVersionUID = -440516813731775575L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "category_name")
	private String categoryName;

	/*
	 * @OneToMany(mappedBy = "categoryMaster") private
	 * List<PredefinedGenericAttribEntity> predefinedGenericAttribEntity;
	 */

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	/*
	 * public List<PredefinedGenericAttribEntity> getPredefinedGenericAttribEntity()
	 * { return predefinedGenericAttribEntity; }
	 * 
	 * public void setPredefinedGenericAttribEntity(
	 * List<PredefinedGenericAttribEntity> predefinedGenericAttribEntity) {
	 * this.predefinedGenericAttribEntity = predefinedGenericAttribEntity; }
	 */
}

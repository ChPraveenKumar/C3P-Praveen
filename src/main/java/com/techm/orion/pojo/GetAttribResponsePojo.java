package com.techm.orion.pojo;

import java.util.List;

public class GetAttribResponsePojo {
	
	private List<PredefinedAttribResponsePojo> predefinedAttribResponse;
	
	private List <GenericAttribResponsePojo> genericAttribResponse;
	
	private List<AttribValidationPojo> attribValidationList;
	
	private List<AttribUIComponentPojo> AttribUIComponentList;

	public List<PredefinedAttribResponsePojo> getPredefinedAttribResponse() {
		return predefinedAttribResponse;
	}

	public void setPredefinedAttribResponse(
			List<PredefinedAttribResponsePojo> predefinedAttribResponse) {
		this.predefinedAttribResponse = predefinedAttribResponse;
	}

	public List<GenericAttribResponsePojo> getGenericAttribResponse() {
		return genericAttribResponse;
	}

	public void setGenericAttribResponse(
			List<GenericAttribResponsePojo> genericAttribResponse) {
		this.genericAttribResponse = genericAttribResponse;
	}

	public List<AttribValidationPojo> getAttribValidationList() {
		return attribValidationList;
	}

	public void setAttribValidationList(
			List<AttribValidationPojo> attribValidationList) {
		this.attribValidationList = attribValidationList;
	}

	public List<AttribUIComponentPojo> getAttribUIComponentList() {
		return AttribUIComponentList;
	}

	public void setAttribUIComponentList(
			List<AttribUIComponentPojo> attribUIComponentList) {
		AttribUIComponentList = attribUIComponentList;
	}
	
}

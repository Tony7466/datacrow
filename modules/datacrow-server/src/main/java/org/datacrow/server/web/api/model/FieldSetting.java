package org.datacrow.server.web.api.model;

import org.datacrow.core.utilities.definitions.WebFieldDefinition;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FieldSetting {
	
	@JsonProperty("id")
	private int id;
	@JsonProperty("fieldIdx")
	private int fieldIdx;
	@JsonProperty("labelKey")
	private String labelKey;
	@JsonProperty("order")
	private int order;
	
	public FieldSetting() {}
	
	public FieldSetting(WebFieldDefinition wf, int order) {
		this.id = wf.getFieldIdx();
		this.fieldIdx = wf.getFieldIdx();
		this.labelKey = wf.getLabelKey();
		this.order = order;
	}

	public int getId() {
		return id;
	}
	
	public int getFieldIdx() {
		return fieldIdx;
	}
	
	public String getLabelKey() {
		return labelKey;
	}
	
	public int getOrder() {
		return order;
	}
}

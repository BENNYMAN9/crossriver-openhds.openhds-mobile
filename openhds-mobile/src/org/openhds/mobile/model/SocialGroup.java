package org.openhds.mobile.model;

import java.io.Serializable;

public class SocialGroup implements Serializable {

	private static final long serialVersionUID = 571090333555561853L;
	
	private String uuid;
	private String extId;
	private String groupName;
	private String groupHead;
	private String status;
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getExtId() {
		return extId;
	}
	
	public void setExtId(String extId) {
		this.extId = extId;
	}
	
	public String getGroupName() {
		return groupName;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public String getGroupHead() {
		return groupHead;
	}
	
	public void setGroupHead(String groupHead) {
		this.groupHead = groupHead;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
}

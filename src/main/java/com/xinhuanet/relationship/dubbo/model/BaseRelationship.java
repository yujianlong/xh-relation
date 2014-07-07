package com.xinhuanet.relationship.dubbo.model;

import java.util.Date;

public class BaseRelationship {
	private long operUserId;
	private long targetUserId;
	private Date createdAt;

	public long getOperUserId() {
		return operUserId;
	}

	public void setOperUserId(long operUserId) {
		this.operUserId = operUserId;
	}

	public long getTargetUserId() {
		return targetUserId;
	}

	public void setTargetUserId(long targetUserId) {
		this.targetUserId = targetUserId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

}

package com.xinhuanet.relationship.dubbo.model;

import java.util.List;

public class FriendRelationship extends BaseRelationship {
	private String remark;
	private boolean privateFlag;
	private List<String> friendGroups;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public boolean isPrivateFlag() {
		return privateFlag;
	}

	public void setPrivateFlag(boolean privateFlag) {
		this.privateFlag = privateFlag;
	}

	public List<String> getFriendGroups() {
		return friendGroups;
	}

	public void setFriendGroups(List<String> friendGroups) {
		this.friendGroups = friendGroups;
	}

}

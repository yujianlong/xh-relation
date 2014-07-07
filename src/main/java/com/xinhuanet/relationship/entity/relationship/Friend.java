package com.xinhuanet.relationship.entity.relationship;

import java.util.List;

import org.springframework.data.neo4j.annotation.RelationshipEntity;

import com.xinhuanet.relationship.common.constant.RelTypeName;

@RelationshipEntity(type = RelTypeName.FRIEND)
public class Friend extends BaseRelationship {
	private String remark;
	private String remarkName;
	private boolean privateFlag;
	private List<String> friendGroups;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRemarkName() {
		return remarkName;
	}

	public void setRemarkName(String remarkName) {
		this.remarkName = remarkName;
	}

	public boolean getPrivateFlag() {
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

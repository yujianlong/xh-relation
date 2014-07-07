package com.xinhuanet.relationship.entity.node;

import java.util.List;

import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
public class UserNode extends BaseNode {

	@Indexed
	private Integer userId;

	private List<String> customFriendGroups;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public List<String> getCustomFriendGroups() {
		return customFriendGroups;
	}

	public void setCustomFriendGroups(List<String> customFriendGroups) {
		this.customFriendGroups = customFriendGroups;
	}
}
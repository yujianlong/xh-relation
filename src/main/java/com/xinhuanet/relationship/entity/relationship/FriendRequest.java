package com.xinhuanet.relationship.entity.relationship;

import org.springframework.data.neo4j.annotation.RelationshipEntity;

import com.xinhuanet.relationship.common.constant.RelTypeName;

@RelationshipEntity(type = RelTypeName.FRIEND_REQUEST)
public class FriendRequest extends BaseRelationship {
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}

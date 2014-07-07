package com.xinhuanet.relationship.dubbo.model;

/**
 * @param message 好友请求说明信心
 */
public class FriendRequestRelationship extends BaseRelationship {
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}

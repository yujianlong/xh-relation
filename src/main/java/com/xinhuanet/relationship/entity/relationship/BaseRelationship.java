package com.xinhuanet.relationship.entity.relationship;

import java.util.Date;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.StartNode;

import com.xinhuanet.relationship.entity.node.UserNode;

public class BaseRelationship {
	@GraphId
	private Long relId;
	@EndNode
	@Fetch
	private UserNode endNode;
	@StartNode
	@Fetch
	private UserNode startNode;
	private Date createdAt;
	private Date updatedAt;

	public Long getRelId() {
		return relId;
	}

	public void setRelId(Long relId) {
		this.relId = relId;
	}

	public UserNode getEndNode() {
		return endNode;
	}

	public void setEndNode(UserNode endNode) {
		this.endNode = endNode;
	}

	public UserNode getStartNode() {
		return startNode;
	}

	public void setStartNode(UserNode startNode) {
		this.startNode = startNode;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

}

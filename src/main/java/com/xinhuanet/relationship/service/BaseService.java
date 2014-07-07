package com.xinhuanet.relationship.service;

import org.springframework.data.neo4j.template.Neo4jOperations;

import com.xinhuanet.relationship.common.exception.XhrcException;
import com.xinhuanet.relationship.entity.node.UserNode;
import com.xinhuanet.relationship.repository.UserRepository;

public interface BaseService {
	Neo4jOperations template();

	UserRepository userRepository();

	/**
	 * 获取指定userIde 的用户节点属性，如果用户节点不存在，则创建用户节点
	 * 
	 * @param userId
	 * @return
	 */
	UserNode getUserNode(Integer userId) throws XhrcException;

	/**
	 * 保存用户节点，如果用户节点存在，则更新用户节点属性
	 * 
	 * @param userNode 要保存的用户节点
	 */
	UserNode saveUserNode(UserNode userNode) throws XhrcException;

	/**
	 * 创建用户节点
	 * 
	 * @param userId
	 */
	UserNode createUserNode(Integer userId) throws XhrcException;
}

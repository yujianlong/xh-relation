package com.xinhuanet.relationship.service.impl;

import java.util.Calendar;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.xinhuanet.relationship.common.exception.XhrcException;
import com.xinhuanet.relationship.common.exception.XhrcRuntimeException;
import com.xinhuanet.relationship.config.RelationshipConfig;
import com.xinhuanet.relationship.entity.node.UserNode;
import com.xinhuanet.relationship.repository.UserRepository;
import com.xinhuanet.relationship.service.BaseService;

@Service("baseService")
public class BaseServiceImpl implements BaseService {
	private static Logger logger = LoggerFactory.getLogger(BaseServiceImpl.class);

	@Resource
	private Neo4jOperations template;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RelationshipConfig relationshipConfig;

	@Override
	public UserNode saveUserNode(UserNode userNode) throws XhrcException {
		try {
			return userRepository().save(userNode);
		} catch (Exception e) {
			logger.error("保存用户节点失败：数据库操作异常。userNode=" + JSON.toJSONString(userNode), e);
			throw new XhrcException("20021", "保存用户节点失败：数据库操作异常。userNode=" + JSON.toJSONString(userNode), e);
		}
	}

	@Override
	public UserNode createUserNode(Integer userId) throws XhrcException {
		UserNode uNode = new UserNode();
		uNode.setUserId(userId);
		uNode.setCreatedAt(Calendar.getInstance().getTime());
		uNode = saveUserNode(uNode);
		if (uNode == null || uNode.getNodeId() == null || uNode.getNodeId() <= 0 || uNode.getUserId() == null
				|| uNode.getUserId() <= 0) {
			logger.error("创建用户节点失败：字段缺失。userNode=" + JSON.toJSONString(uNode));
			throw new XhrcRuntimeException("20041", "创建用户节点失败：字段缺失。userNode=" + JSON.toJSONString(uNode));
		}
		return uNode;
	}

	@Override
	public UserNode getUserNode(Integer userId) throws XhrcException {
		if (userId == null || userId.intValue() == 0) {
			logger.error("获取用户节点失败：userId为null或0。userId=" + userId);
			throw new XhrcRuntimeException("20041", "获取用户节点失败：userId为null或0。userId=" + userId);
		}
		UserNode uNode = null;
		try {
			uNode = userRepository().findByPropertyValue("userId", userId);
		} catch (Exception e) {
			logger.error("获取用户节点失败：数据库操作异常。userId：" + userId, e);
			throw new XhrcRuntimeException("20021", "获取用户节点失败：数据库操作异常。userId=" + userId, e);
		}
		if (uNode == null) {
			logger.info("用户节点不存在，将会创建用户节点。userId=" + userId);
			return createUserNode(userId);
		} else {
			return uNode;
		}
	}

	@Override
	public Neo4jOperations template() {
		return template;
	}

	public void setTemplate(Neo4jOperations template) {
		this.template = template;
	}

	@Override
	public UserRepository userRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public RelationshipConfig getRelationshipConfig() {
		return relationshipConfig;
	}

	public void setRelationshipConfig(RelationshipConfig relationshipConfig) {
		this.relationshipConfig = relationshipConfig;
	}
}

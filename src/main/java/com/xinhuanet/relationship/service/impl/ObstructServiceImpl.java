package com.xinhuanet.relationship.service.impl;

import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.xinhuanet.relationship.common.constant.RelTypeName;
import com.xinhuanet.relationship.common.exception.XhrcException;
import com.xinhuanet.relationship.common.exception.XhrcRuntimeException;
import com.xinhuanet.relationship.entity.node.UserNode;
import com.xinhuanet.relationship.entity.relationship.Obstructs;
import com.xinhuanet.relationship.service.ObstructService;

@Service("obstructService")
public class ObstructServiceImpl extends BaseServiceImpl implements ObstructService {
	private static Logger logger = LoggerFactory.getLogger(ObstructServiceImpl.class);

	@Override
	public boolean createObstruct(Integer startUserId, Integer endUserId) throws XhrcException {
		UserNode startNode = getUserNode(startUserId);
		UserNode endNode = getUserNode(endUserId);
		try {
			Obstructs rel = userRepository().createRelationshipBetween(startNode, endNode, Obstructs.class,
					RelTypeName.OBSTRUCTS);
			rel.setCreatedAt(Calendar.getInstance().getTime());
			template().save(rel);
		} catch (Exception e) {
			logger.error("创建阻止关系失败：数据库操作异常。startUserId=" + startUserId + "， endUserId=" + endUserId, e);
			throw new XhrcException("20021", "创建阻止关系失败：数据库操作异常。startUserId=" + startUserId + " ， endUserId="
					+ endUserId, e);
		}
		return true;
	}

	@Override
	public boolean deleteObstruct(Integer startUserId, Integer endUserId) throws XhrcException {
		boolean bool = false;
		UserNode startNode = getUserNode(startUserId);
		UserNode endNode = getUserNode(endUserId);
		try {
			userRepository().deleteRelationshipBetween(startNode, endNode, RelTypeName.OBSTRUCTS);
			bool = true;
		} catch (Exception e) {
			logger.error("删除阻止关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId, e);
			throw new XhrcException("20021", "删除阻止关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId,
					e);
		}
		return bool;
	}

	@Override
	public boolean existObstruct(Integer startUserId, Integer endUserId) throws XhrcException {
		boolean bool = false;
		UserNode startNode = getUserNode(startUserId);
		UserNode endNode = getUserNode(endUserId);
		try {
			Obstructs block = userRepository().getRelationshipBetween(startNode, endNode, Obstructs.class,
					RelTypeName.OBSTRUCTS);
			if (block != null) {
				bool = true;
			}
		} catch (Exception e) {
			logger.error("判断是否存在阻止关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId, e);
			throw new XhrcRuntimeException("20021", "判断是否存在阻止关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId="
					+ endUserId, e);
		}

		return bool;
	}

	@Override
	public List<Integer> getUserObstructs(Integer userId) throws XhrcException {
		UserNode node = getUserNode(userId);
		List<Integer> obstructId = null;
		try {
			obstructId = userRepository().getUserObstructs(node.getNodeId());
		} catch (Exception e) {
			logger.error("获得用户阻止的用户id列表失败：数据库操作异常。userId=" + userId, e);
			throw new XhrcRuntimeException("20021", "获得用户阻止的用户id列表失败：数据库操作异常。userId=" + userId, e);
		}
		return obstructId;
	}

	@Override
	public long countUserObstructs(Integer userId) throws XhrcException {

		long count = 0;
		UserNode node = getUserNode(userId);
		try {
			count = userRepository().countUserObstructs(node.getNodeId());
		} catch (Exception e) {
			logger.error("获取用户阻止数量出错：数据库操作异常。userId=" + userId, e);
			throw new XhrcRuntimeException("20021", "获取用户阻止数量出错：数据库操作异常。userId=" + userId, e);
		}
		return count;
	}

}

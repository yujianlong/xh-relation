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
import com.xinhuanet.relationship.entity.relationship.Blacklists;
import com.xinhuanet.relationship.service.BlacklistService;

@Service("blacklistService")
public class BlacklistServiceImpl extends BaseServiceImpl implements BlacklistService {
	private static Logger logger = LoggerFactory.getLogger(BlacklistServiceImpl.class);

	@Override
	public boolean deleteBlacklist(Integer startUserId, Integer endUserId) throws XhrcException {
		boolean bool = false;
		UserNode startNode = getUserNode(startUserId);
		UserNode endNode = getUserNode(endUserId);
		try {
			userRepository().deleteRelationshipBetween(startNode, endNode, RelTypeName.BLACKLISTS);
			bool = true;
		} catch (Exception e) {
			logger.error("移出黑名单失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId, e);
			throw new XhrcException("20021", "移出黑名单失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId,
					e);
		}
		return bool;
	}

	@Override
	public boolean existBlacklist(Integer startUserId, Integer endUserId) throws XhrcException {
		boolean bool = false;
		UserNode startNode = getUserNode(startUserId);
		UserNode endNode = getUserNode(endUserId);
		try {
			Blacklists black = userRepository().getRelationshipBetween(startNode, endNode, Blacklists.class,
					RelTypeName.BLACKLISTS);
			if (black != null) {
				bool = true;
			}
		} catch (Exception e) {
			logger.error("判断是否在黑名单失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId, e);
			throw new XhrcRuntimeException("20021", "判断是否在黑名单失败：数据库操作异常。startUserId=" + startUserId + "；endUserId="
					+ endUserId, e);
		}

		return bool;
	}

	@Override
	public List<Integer> getUserBlacklists(Integer userId) throws XhrcException {
		UserNode uNode = getUserNode(userId);
		Long nodeId = uNode.getNodeId();
		List<Integer> blacksId = null;
		try {
			blacksId = userRepository().getUserBlacks(nodeId);
		} catch (Exception e) {
			logger.error("获取用户黑名单的id列表失败：数据库操作异常。userId=" + userId, e);
			throw new XhrcRuntimeException("20021", "获取用户黑名单的id列表失败：数据库操作异常。userId=" + userId, e);
		}
		return blacksId;
	}

	@Override
	public long countUserBlacklists(Integer userId) throws XhrcException {
		long count = 0;
		UserNode node = getUserNode(userId);
		try {
			count = userRepository().countUserBlacks(node.getNodeId());
		} catch (Exception e) {
			logger.error("获取用户黑名单数量出错：数据库操作异常。userId=" + userId, e);
			throw new XhrcRuntimeException("20021", "获取用户黑名单数量出错：数据库操作异常。userId=" + userId, e);
		}
		return count;
	}

	@Override
	public boolean createBlackList(Integer startUserId, Integer endUserId) throws XhrcException {
		UserNode startNode = getUserNode(startUserId);
		UserNode endNode = getUserNode(endUserId);
		try {
			Blacklists rel = userRepository().createRelationshipBetween(startNode, endNode, Blacklists.class,
					RelTypeName.BLACKLISTS);
			rel.setCreatedAt(Calendar.getInstance().getTime());
			template().save(rel);
		} catch (Exception e) {
			logger.error("创建黑名单关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId, e);
			throw new XhrcException("20021",
					"创建黑名单关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId, e);
		}
		return true;
	}

}

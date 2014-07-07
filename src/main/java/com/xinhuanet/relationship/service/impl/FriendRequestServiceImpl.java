package com.xinhuanet.relationship.service.impl;

import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.xinhuanet.relationship.common.constant.RelTypeName;
import com.xinhuanet.relationship.common.exception.XhrcException;
import com.xinhuanet.relationship.common.exception.XhrcRuntimeException;
import com.xinhuanet.relationship.entity.node.UserNode;
import com.xinhuanet.relationship.entity.relationship.FriendRequest;
import com.xinhuanet.relationship.service.FriendRequestService;

@Service("friendRequestService")
public class FriendRequestServiceImpl extends BaseServiceImpl implements FriendRequestService {
	private static Logger logger = LoggerFactory.getLogger(FriendRequestServiceImpl.class);
	private static final int FRIEND_REQUEST_LENGTH = 1000;

	@Override
	public boolean createFriendRequest(Integer startUserId, Integer endUserId, String message) throws XhrcException {
		if (!StringUtils.isEmpty(message) && message.length() > FRIEND_REQUEST_LENGTH) {
			logger.warn("创建好友请求附言失败，message长度不合法。");
			throw new XhrcRuntimeException("20042", "创建好友请求附言失败，message长度不合法。");
		}
		UserNode startNode = getUserNode(startUserId);
		UserNode endNode = getUserNode(endUserId);

		try {
			FriendRequest rel = userRepository().createRelationshipBetween(startNode, endNode, FriendRequest.class,
					RelTypeName.FRIEND_REQUEST);
			if (rel == null) {
				logger.error("创建好友请求失败。startUserId=" + startUserId + "， endUserId=" + endUserId);
				throw new XhrcException("20021", "创建好友请求失败。startUserId=" + startUserId + "， endUserId=" + endUserId);
			}
			if (!StringUtils.isEmpty(message)) {
				rel.setMessage(message);
			}

			rel.setCreatedAt(Calendar.getInstance().getTime());
			template().save(rel);
		} catch (Exception e) {
			logger.error("创建好友请求失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId, e);
			throw new XhrcException("20021", "创建好友请求失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId,
					e);
		}
		return true;
	}

	@Override
	public boolean deleteFriendRequest(Integer startUserId, Integer endUserId) throws XhrcException {
		boolean bool = false;
		UserNode startNode = getUserNode(startUserId);
		UserNode endNode = getUserNode(endUserId);
		try {
			userRepository().deleteRelationshipBetween(startNode, endNode, RelTypeName.FRIEND_REQUEST);
			bool = true;
		} catch (Exception e) {
			logger.error("删除好友请求失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId, e);
			throw new XhrcException("20021", "删除好友请求失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId,
					e);
		}
		return bool;
	}

	@Override
	public boolean existFriendRequest(Integer startUserId, Integer endUserId) throws XhrcException {
		boolean bool = false;
		UserNode startNode = getUserNode(startUserId);
		UserNode endNode = getUserNode(endUserId);
		try {
			FriendRequest fq = userRepository().getRelationshipBetween(startNode, endNode, FriendRequest.class,
					RelTypeName.FRIEND_REQUEST);
			if (fq != null) {
				bool = true;
			}
		} catch (Exception e) {
			logger.error("判断是否存在好友请求关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId, e);
			throw new XhrcRuntimeException("20021", "判断是否存在好友请求关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId="
					+ endUserId, e);
		}

		return bool;
	}

	@Override
	public List<FriendRequest> getFriendRequestToUser(Integer userId) throws XhrcException {
		UserNode uNode = getUserNode(userId);
		List<FriendRequest> friendRequest = null;
		try {
			friendRequest = userRepository().getFriendRequestToUser(uNode.getNodeId());
		} catch (Exception e) {
			logger.error("获得用户好友请求信息列表失败：数据库操作异常。userId=" + userId, e);
			throw new XhrcRuntimeException("20021", "获得用户好友请求信息列表失败：数据库操作异常。userId=" + userId, e);
		}
		return friendRequest;
	}

}

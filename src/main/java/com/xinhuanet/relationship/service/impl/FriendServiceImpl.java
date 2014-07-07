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
import com.xinhuanet.relationship.entity.relationship.Friend;
import com.xinhuanet.relationship.lang.Lang;
import com.xinhuanet.relationship.repository.UserRepository;
import com.xinhuanet.relationship.service.FriendService;

@Service("friendService")
public class FriendServiceImpl extends BaseServiceImpl implements FriendService {
	private static Logger logger = LoggerFactory.getLogger(FriendServiceImpl.class);
	private static final int FRIEND_GROUP_MAX_LENGTH = 100; // 好友分组名称最大长度
	private static final int REMARK_MAX_LENGTH = 500; // 好友备注最大长度
	private static final int REMARK_NAME_LENGTH = 20; // 备注最大长度
	private static final int SKIP_DEFAULT = 0;
	private static final int LIMIT_DEFAULT = 20; // 分页大小默认值20
	private static final int LIMIT_MAX = 1000; // 最大允许分页大小

	@Override
	public boolean deleteFriendBetween(Integer userId1, Integer userId2) throws XhrcException {
		UserRepository userRepository = userRepository();
		boolean bool = false;
		UserNode node1 = getUserNode(userId1);
		UserNode node2 = getUserNode(userId2);
		try {
			userRepository.deleteRelationshipBetween(node1, node2, RelTypeName.FRIEND);
			userRepository.deleteRelationshipBetween(node2, node1, RelTypeName.FRIEND);
			bool = true;
		} catch (Exception e) {
			logger.debug("删除好友关系失败：数据库操作异常。userId1=" + userId1 + "；userId2=" + userId2, e);
			throw new XhrcRuntimeException("20021", "删除好友关系失败：数据库操作异常。userId1=" + userId1 + "；userId2=" + userId2, e);
		}

		return bool;
	}

	@Override
	public boolean existFriendBetween(Integer userId1, Integer userId2) throws XhrcException {
		UserRepository userRepository = userRepository();
		boolean bool = false;
		UserNode node1 = getUserNode(userId1);
		UserNode node2 = getUserNode(userId2);

		try {
			Friend ifd1 = userRepository.getRelationshipBetween(node1, node2, Friend.class, RelTypeName.FRIEND);
			Friend ifd2 = userRepository.getRelationshipBetween(node2, node1, Friend.class, RelTypeName.FRIEND);
			if (ifd1 != null && ifd2 != null) {
				bool = true;
			}
		} catch (Exception e) {
			logger.debug("判断是否存在好友关系失败：数据库操作异常。userId1=" + userId1 + "；userId2=" + userId2, e);
			throw new XhrcRuntimeException("20021", "判断是否存在好友关系失败：数据库操作异常。userId1=" + userId1 + "；userId2=" + userId2,
					e);
		}

		return bool;
	}

	@Override
	public long countUserFriend(Integer userId) throws XhrcException {
		long count = 0;
		UserNode node = getUserNode(userId);
		try {
			count = userRepository().countUserFriend(node.getNodeId());
		} catch (Exception e) {
			logger.error("统计好友数量失败：数据库操作异常。userId=" + userId, e);
			throw new XhrcException("20021", "统计好友数量失败：数据库操作异常。userId=" + userId, e);
		}
		return count;

	}

	@Override
	public boolean createFriend(Integer startUserId, Integer endUserId, String remarkName, String remark,
			boolean privateFlag, List<String> friendGroups) throws XhrcException {
		if (!StringUtils.isEmpty(remarkName) && remarkName.length() > REMARK_NAME_LENGTH) {
			logger.warn("设置好友备注名称失败，remarkName长度不合法。");
			throw new XhrcRuntimeException("20042", "设置好友备注姓名失败，remarkName长度不合法。");
		}
		if (!StringUtils.isEmpty(remark) && remark.length() > REMARK_MAX_LENGTH) {
			logger.warn("设置好友备注失败，remark长度不合法。");
			throw new XhrcRuntimeException("20042", "设置好友备注失败，remark长度不合法。");
		}
		if (!Lang.isEmpty(friendGroups)) {
			for (String friendGroup : friendGroups) {
				if (friendGroup.length() > FRIEND_GROUP_MAX_LENGTH) {
					logger.warn("设置好友分组失败，friendGroup长度不合法。");
					throw new XhrcRuntimeException("20042", "设置好友分组失败，friendGroup长度不合法。");
				}
			}
		}

		UserNode startNode = getUserNode(startUserId);
		UserNode endNode = getUserNode(endUserId);
		try {
			Friend friRel = userRepository().createRelationshipBetween(startNode, endNode, Friend.class,
					RelTypeName.FRIEND);
			if (friRel == null) {
				logger.error("创建好友失败。startUserId=" + startUserId + "；endUserId=" + endUserId);
				throw new XhrcException("20021", "创建好友失败。startUserId=" + startUserId + "；endUserId=" + endUserId);

			}
			if (!StringUtils.isEmpty(remarkName)) {
				friRel.setRemarkName(remarkName);
			}
			if (!StringUtils.isEmpty(remark)) {
				friRel.setRemark(remark);
			}
			friRel.setPrivateFlag(privateFlag);
			if (!Lang.isEmpty(friendGroups)) {
				friRel.setFriendGroups(friendGroups);
			} else {
				friRel.setFriendGroups(Lang.list("默认分组"));
			}
			friRel.setCreatedAt(Calendar.getInstance().getTime());
			template().save(friRel);
		} catch (Exception e) {
			logger.error("创建好友关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId, e);
			throw new XhrcException("20021", "创建好友关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId,
					e);
		}
		return true;
	}

	@Override
	public boolean updateFriend(Integer startUserId, Integer endUserId, String remarkName, String remark,
			Boolean privateFlag, List<String> friendGroups) throws XhrcException {
		if (!StringUtils.isEmpty(remarkName) && remarkName.length() > REMARK_NAME_LENGTH) {
			logger.warn("设置好友备注名称失败，remarkName长度不合法。");
			throw new XhrcRuntimeException("20042", "设置好友备注名称失败，remarkName长度不合法。");
		}
		if (!StringUtils.isEmpty(remark) && remark.length() > REMARK_MAX_LENGTH) {
			logger.warn("设置好友备注失败，remark长度不合法。");
			throw new XhrcRuntimeException("20042", "设置好友备注失败，remark长度不合法。");
		}
		if (!Lang.isEmpty(friendGroups)) {
			for (String friendGroup : friendGroups) {
				if (friendGroup.length() > FRIEND_GROUP_MAX_LENGTH) {
					logger.warn("设置好友分组失败，friendGroup长度不合法。");
					throw new XhrcRuntimeException("20042", "设置好友分组失败，friendGroup长度不合法。");
				}
			}
		}
		UserNode startNode = getUserNode(startUserId);
		UserNode endNode = getUserNode(endUserId);
		Friend friRel = userRepository().getRelationshipBetween(startNode, endNode, Friend.class, RelTypeName.FRIEND);
		if (friRel == null) {
			logger.error("更新好友关系失败。startUserId=" + startUserId + "；endUserId=" + endUserId + " 不存在好友关系");
			throw new XhrcException("20021", "更新好友关系失败。startUserId=" + startUserId + "；endUserId=" + endUserId
					+ " 不存在好友关系");
		}
		try {
			if (!StringUtils.isEmpty(remarkName)) {
				friRel.setRemarkName(remarkName);
			}
			if (!StringUtils.isEmpty(remark)) {
				friRel.setRemark(remark);
			}
			if (!StringUtils.isEmpty(privateFlag)) {
				friRel.setPrivateFlag(privateFlag);
			}
			if (!Lang.isEmpty(friendGroups)) {
				friRel.setFriendGroups(friendGroups);
			}
			friRel.setUpdatedAt(Calendar.getInstance().getTime());
			template().save(friRel);

		} catch (Exception e) {
			logger.error("更新好友关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId, e);
			throw new XhrcException("20021", "更新好友关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId,
					e);
		}
		return true;
	}

	@Override
	public long countUserFriendByGroup(Integer userId, String group) throws XhrcException {
		long count = 0;
		UserNode node = getUserNode(userId);
		try {
			count = userRepository().countUserFriendByGroup(node.getNodeId(), group);
		} catch (Exception e) {
			logger.error("按好友分组查询用户好友出错：数据库操作异常。userId=" + userId + "；group=" + group, e);
			throw new XhrcException("20021", "按好友分组查询用户好友出错：数据库操作异常。userId=" + userId + "；group=" + group, e);
		}
		return count;
	}

	@Override
	public List<Friend> getUserFriends(Integer userId, String group, Long skip, Integer limit) throws XhrcException {
		long skipOrDefault = skip == null || skip < 0 ? SKIP_DEFAULT : skip;
		int limitOrDefault = limit == null || limit <= 0 ? LIMIT_DEFAULT : limit;
		if (limit != null && limit > LIMIT_MAX) {
			logger.warn("获取好友关系信息失败，limit（每页信息数目，最大为1000）不合法。limit = " + limit);
			throw new XhrcRuntimeException("20042", "获取好友关系信息失败，limit（每页信息数目，最大为1000）不合法.limit = " + limit);
		}
		UserNode uNode = getUserNode(userId);
		Long nodeId = uNode.getNodeId();
		List<Friend> friendsId = null;
		try {
			if (StringUtils.isEmpty(group)) {
				friendsId = userRepository().getUserFriends(nodeId, skipOrDefault, limitOrDefault);
			} else {
				friendsId = userRepository().getUserFriendsByGroup(nodeId, group, skipOrDefault, limitOrDefault);
			}
		} catch (Exception e) {
			logger.error("获取指定分组好友id列表失败：数据库操作异常。userId=" + userId + "；group=" + group, e);
			throw new XhrcRuntimeException("20021", "获取指定分组好友id列表失败：数据库操作异常。userId=" + userId + "；group=" + group, e);
		}
		return friendsId;
	}

	@Override
	public List<Integer> getUserFriendsAtDeepTwo(Integer userId, Long skip, Integer limit) throws XhrcException {
		long skipOrDefault = skip == null || skip < 0 ? SKIP_DEFAULT : skip;
		int limitOrDefault = limit == null || limit <= 0 ? LIMIT_DEFAULT : limit;
		if (limit != null && limit > LIMIT_MAX) {
			logger.warn("获取深度为2的好友id列表失败，limit（每页信息数目，最大为1000）不合法。limit = " + limit);
			throw new XhrcRuntimeException("20042", "获取深度为2的好友id列表失败，limit（每页信息数目，最大为1000）不合法。limit = " + limit);
		}
		UserNode uNode = getUserNode(userId);
		Long nodeId = uNode.getNodeId();
		List<Integer> fofid = null;
		try {
			fofid = userRepository().getUserFriendsOfFriends(nodeId, skipOrDefault, limitOrDefault);
		} catch (Exception e) {
			logger.error("获取朋友的朋友id列表失败：数据库操作异常。userId=" + userId, e);
			throw new XhrcRuntimeException("20021", "获取朋友的朋友id列表失败：数据库操作异常。userId=" + userId, e);
		}
		return fofid;
	}
}

package com.xinhuanet.relationship.dubbo.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.xinhuanet.relationship.common.exception.XhrcException;
import com.xinhuanet.relationship.dubbo.model.FollowsRelationship;
import com.xinhuanet.relationship.dubbo.model.FriendRelationship;
import com.xinhuanet.relationship.dubbo.model.FriendRequestRelationship;
import com.xinhuanet.relationship.dubbo.service.UserRelationshipService;
import com.xinhuanet.relationship.entity.relationship.Follows;
import com.xinhuanet.relationship.entity.relationship.Friend;
import com.xinhuanet.relationship.entity.relationship.FriendRequest;
import com.xinhuanet.relationship.lang.Lang;
import com.xinhuanet.relationship.service.BlacklistService;
import com.xinhuanet.relationship.service.BlockService;
import com.xinhuanet.relationship.service.FollowService;
import com.xinhuanet.relationship.service.FriendRequestService;
import com.xinhuanet.relationship.service.FriendService;
import com.xinhuanet.relationship.service.ObstructService;
import com.xinhuanet.relationship.service.UserNodeService;

public class UserRelationshipServiceImpl implements UserRelationshipService {
	private static Logger logger = LoggerFactory.getLogger(UserRelationshipServiceImpl.class);
	@Autowired
	private UserNodeService userNodeService;
	@Autowired
	private FriendService friendService;
	@Autowired
	private FollowService followService;
	@Autowired
	private BlockService blockService;
	@Autowired
	private BlacklistService blacklistService;
	@Autowired
	private ObstructService obstructService;
	@Autowired
	private FriendRequestService friendRequestService;

	@Override
	public List<String> getUserFriendGroups(Integer userId) throws XhrcException {
		return userNodeService.getUserFriendGroups(userId);
	}

	@Override
	public boolean createUserCustomFriendGroup(Integer userId, String friendGroup) throws XhrcException {
		userNodeService.createUserCustomFriendGroup(userId, friendGroup);
		return true;
	}

	@Override
	public boolean deleteUserCustomFriendGroup(Integer userId, String friendGroup) throws XhrcException {
		userNodeService.deleteUserCustomFriendGroup(userId, friendGroup);
		return true;
	}

	@Override
	public boolean updateFriend(Integer operUserId, Integer targetUserId, String remarkName, String remark,
			Boolean privateFlag, List<String> friendGroups) throws XhrcException {
		return friendService.updateFriend(operUserId, targetUserId, remarkName, remark, privateFlag, friendGroups);
	}

	@Override
	public boolean deleteFriendBetween(Integer userId1, Integer userId2) throws XhrcException {
		// 判断是否存在关注关系，如不存在关注关系，但存在阻止或屏蔽关系，则删除该阻止及屏蔽关系。
		if (!followService.existFollow(userId1, userId2)) {
			blockService.deleteBlock(userId1, userId2);
		}
		if (!followService.existFollow(userId2, userId1)) {
			obstructService.deleteObstruct(userId1, userId2);
		}
		return friendService.deleteFriendBetween(userId1, userId2);
	}

	@Override
	public boolean existFriendBetween(Integer userId1, Integer userId2) throws XhrcException {
		return friendService.existFriendBetween(userId1, userId2);
	}

	@Override
	public boolean createFollow(Integer operUserId, Integer targetUserId, String remark) throws XhrcException {
		// 如果我在对方的黑名单里，或对方在我的黑名单，均无法添加关注关系。
		if (existBlacklist(operUserId, targetUserId) || existBlacklist(targetUserId, operUserId)) {
			logger.debug("操作用户和目标用户存在黑名单关系，无法关注");
			throw new XhrcException("20051", "操作用户和目标用户存在黑名单关系，无法关注");
		}
		return followService.createFollow(operUserId, targetUserId, remark);
	}

	@Override
	public boolean updateFollow(Integer operUserId, Integer targetUserId, String remark) throws XhrcException {
		return followService.updateFollow(operUserId, targetUserId, remark);
	}

	@Override
	public boolean deleteFollow(Integer operUserId, Integer targetUserId) throws XhrcException {
		// 判断是否存在好友关系，如不存在好友关系，但存在阻止或屏蔽关系，则删除该阻止及屏蔽关系。

		if (!friendService.existFriendBetween(operUserId, targetUserId)) {
			blockService.deleteBlock(operUserId, targetUserId);
			obstructService.deleteObstruct(operUserId, targetUserId);
		}

		return followService.deleteFollow(operUserId, targetUserId);
	}

	@Override
	public boolean existFollow(Integer operUserId, Integer targetUserId) throws XhrcException {
		return followService.existFollow(operUserId, targetUserId);
	}

	@Override
	public long countUserFollows(Integer operUserId) throws XhrcException {
		return followService.countFollow(operUserId);
	}

	@Override
	public long countUserFriends(Integer userId, String friendGroup) throws XhrcException {
		long num = 0;
		if (StringUtils.isEmpty(friendGroup)) {
			num = friendService.countUserFriend(userId);
		} else {
			num = friendService.countUserFriendByGroup(userId, friendGroup);
		}

		return num;
	}

	@Override
	public List<FollowsRelationship> getUserFollows(Integer userId, Long skip, Integer limit) throws XhrcException {
		List<FollowsRelationship> followsRelationship = new ArrayList<FollowsRelationship>();
		List<Follows> follows = followService.getUserFollows(userId, skip, limit);
		if (follows != null) {
			FollowsRelationship fr = new FollowsRelationship();
			for (Follows f : follows) {
				fr.setOperUserId(f.getStartNode().getUserId());
				fr.setTargetUserId(f.getEndNode().getUserId());
				fr.setRemark(f.getRemark());
				fr.setCreatedAt(f.getCreatedAt());
				followsRelationship.add(fr);
			}
		}
		return followsRelationship;
	}

	@Override
	public Map<String, Long> countUserFriendsByEachGroup(Integer userId) throws XhrcException {
		List<String> grouplist = getUserFriendGroups(userId);
		Map<String, Long> groupnum = new HashMap<String, Long>();
		for (String group : grouplist) {
			groupnum.put(group, friendService.countUserFriendByGroup(userId, group));
		}
		return groupnum;
	}

	@Override
	public List<FriendRelationship> getUserFriends(Integer userId, String friendGroup, Long skip, Integer limit)
			throws XhrcException {
		List<FriendRelationship> ifd = new ArrayList<FriendRelationship>();
		List<Friend> ifs = friendService.getUserFriends(userId, friendGroup, skip, limit);
		if (ifs != null) {
			FriendRelationship ifr = new FriendRelationship();
			for (Friend friend : ifs) {
				ifr.setOperUserId(friend.getStartNode().getUserId());
				ifr.setTargetUserId(friend.getEndNode().getUserId());
				ifr.setRemark(friend.getRemark());
				ifr.setPrivateFlag(friend.getPrivateFlag());
				ifr.setCreatedAt(friend.getCreatedAt());
				ifr.setFriendGroups(friend.getFriendGroups());

				ifd.add(ifr);
			}
		}
		return ifd;
	}

	@Override
	public List<Integer> getUserBlacklists(Integer userId) throws XhrcException {
		List<Integer> blacksIdList = blacklistService.getUserBlacklists(userId);
		if (blacksIdList == null) {
			blacksIdList = new ArrayList<Integer>();
		}
		return blacksIdList;
	}

	@Override
	public List<Integer> getUserBlocks(Integer userId) throws XhrcException {
		List<Integer> blocksIdList = blockService.getUserBlocks(userId);
		if (blocksIdList == null) {
			blocksIdList = new ArrayList<Integer>();
		}
		return blocksIdList;
	}

	@Override
	public long countUserBlocks(Integer userId) throws XhrcException {
		return blockService.countUserBlocks(userId);
	}

	@Override
	public long countUserBlacklists(Integer userId) throws XhrcException {
		return blacklistService.countUserBlacklists(userId);
	}

	@Override
	public boolean deleteBlacklist(Integer operUserId, Integer targetUserId) throws XhrcException {
		return blacklistService.deleteBlacklist(operUserId, targetUserId);
	}

	@Override
	public boolean existBlacklist(Integer operUserId, Integer targetUserId) throws XhrcException {
		return blacklistService.existBlacklist(operUserId, targetUserId);
	}

	@Override
	public boolean deleteBlock(Integer operUserId, Integer targetUserId) throws XhrcException {
		return blockService.deleteBlock(operUserId, targetUserId);
	}

	@Override
	public boolean existBlock(Integer operUserId, Integer targetUserId) throws XhrcException {
		return blockService.existBlock(operUserId, targetUserId);
	}

	@Override
	public boolean createBlacklist(Integer operUserId, Integer targetUserId) throws XhrcException {
		// 判断是否存在好友关系，如存在，删除
		if (!existFriendBetween(operUserId, targetUserId)) {
			logger.debug("用户" + operUserId + "和" + targetUserId + "不存在好友关系，无法加入黑名单");
			throw new XhrcException("20052", "用户" + operUserId + "和" + targetUserId + "不存在好友关系，无法加入黑名单");
		} else {
			deleteFriendBetween(operUserId, targetUserId);
		}
		// 判断是否存在关注关系，如存在，删除
		deleteFollow(operUserId, targetUserId);
		deleteFollow(targetUserId, operUserId);
		// 屏蔽
		deleteBlock(operUserId, targetUserId);
		deleteBlock(targetUserId, operUserId);
		// 阻止
		deleteObstruct(targetUserId, operUserId);
		deleteObstruct(operUserId, targetUserId);
		return blacklistService.createBlackList(operUserId, targetUserId);
	}

	@Override
	public boolean createBlock(Integer operUserId, Integer targetUserId) throws XhrcException {
		if (!(existFriendBetween(operUserId, targetUserId) || existFollow(operUserId, targetUserId))) {
			logger.debug("不存在好友或者关注关系，无法屏蔽");
			throw new XhrcException("20052", "不存在好友或者关注关系，无法屏蔽");
		}
		return blockService.createBlock(operUserId, targetUserId);
	}

	@Override
	public boolean deleteObstruct(Integer operUserId, Integer targetUserId) throws XhrcException {
		return obstructService.deleteObstruct(operUserId, targetUserId);
	}

	@Override
	public boolean existObstruct(Integer operUserId, Integer targetUserId) throws XhrcException {
		return obstructService.existObstruct(operUserId, targetUserId);
	}

	@Override
	public boolean createFriendRequest(Integer operUserId, Integer targetUserId, String message) throws XhrcException {
		// 如果我在对方的黑名单里，对方在我的黑名单，两者已经是好友，均无法发送好友请求。
		if (existBlacklist(operUserId, targetUserId)) {
			logger.debug("目标用户在操作用户黑名单里，不能发送好友请求");
			throw new XhrcException("20051", "目标用户在操作用户黑名单里，不能发送好友请求");
		}
		if (existBlacklist(targetUserId, operUserId)) {
			logger.debug("操作用户在目标用户黑名单里，不能发送好友请求");
			throw new XhrcException("20051", "操作用户在目标用户黑名单里，不能发送好友请求");
		}
		if (existFriendBetween(operUserId, targetUserId)) {
			logger.debug("操作用户和目标用户已经是好友，不能发送好友请求");
			throw new XhrcException("20051", "操作用户和目标用户已经是好友，不能发送好友请求");
		}
		friendRequestService.createFriendRequest(operUserId, targetUserId, message);
		return true;
	}

	@Override
	public boolean createObstruct(Integer operUserId, Integer targetUserId) throws XhrcException {
		// 判断是否存在好友关系或者关注（目标用户关注了操作用户）关系，如果二者都不存在，则无法添加阻止。
		if (!existFollow(targetUserId, operUserId) && !existFriendBetween(operUserId, targetUserId)) {
			logger.debug("不存在好友关系或关注关系，不能创建");
			throw new XhrcException("20052", "不存在好友关系或关注关系，不能创建");
		}
		return obstructService.createObstruct(operUserId, targetUserId);
	}

	@Override
	public List<Integer> getUserObstructs(Integer userId) throws XhrcException {
		List<Integer> obstructIdList = obstructService.getUserObstructs(userId);
		if (obstructIdList == null) {
			obstructIdList = new ArrayList<Integer>();
		}
		return obstructIdList;
	}

	@Override
	public long countUserObstructs(Integer userId) throws XhrcException {
		return obstructService.countUserObstructs(userId);
	}

	@Override
	public boolean existFriendRequest(Integer operUserId, Integer targetUserId) throws XhrcException {
		return friendRequestService.existFriendRequest(operUserId, targetUserId);
	}

	@Override
	public boolean acceptFriendRequest(Integer operUserId, Integer targetUserId, String remarkName, String remark,
			boolean privateFlag, List<String> friendGroups) throws XhrcException {
		// 删除对方向我的好友请求；删除我向对方的好友请求（若存在）。
		friendRequestService.deleteFriendRequest(targetUserId, operUserId);
		friendRequestService.deleteFriendRequest(operUserId, targetUserId);
		// 校验好友分组是否存在
		if (!userNodeService.existUserFriendGroups(operUserId, friendGroups)) {
			logger.error("指定的好友分组不存在于该用户的好友分组列表中，接受好友请求失败。");
			throw new XhrcException("20052", "指定的好友分组不存在于该用户的好友分组列表中，接受好友请求失败。");
		}
		friendService.createFriend(operUserId, targetUserId, remarkName, remark, privateFlag, friendGroups);
		friendService.createFriend(targetUserId, operUserId, null, remark, false, Lang.list("默认分组"));
		return true;
	}

	@Override
	public boolean refuseFriendRequest(Integer operUserId, Integer targetUserId) throws XhrcException {
		// 删除操作用户指向目标用户的的好友请求
		friendRequestService.deleteFriendRequest(operUserId, targetUserId);
		// 删除目标用户指向操作用户的好友请求（若存在删除，不存在不做任何操作）。
		friendRequestService.deleteFriendRequest(targetUserId, operUserId);
		return true;
	}

	@Override
	public List<FriendRequestRelationship> getFriendRequestToUser(Integer userId) throws XhrcException {
		List<FriendRequestRelationship> friendRequestD = new ArrayList<FriendRequestRelationship>();
		FriendRequestRelationship frd = new FriendRequestRelationship();
		List<FriendRequest> friendRequestS = friendRequestService.getFriendRequestToUser(userId);
		if (friendRequestS != null) {
			for (FriendRequest frs : friendRequestS) {
				frd.setOperUserId(frs.getStartNode().getUserId());
				frd.setTargetUserId(frs.getEndNode().getUserId());
				frd.setMessage(frs.getMessage());
				frd.setCreatedAt(frs.getCreatedAt());
				friendRequestD.add(frd);
			}
		}
		return friendRequestD;
	}

	@Override
	public List<FollowsRelationship> getFollowsToUser(Integer userId, Long skip, Integer limit) throws XhrcException {
		List<FollowsRelationship> followsRelationship = new ArrayList<FollowsRelationship>();
		List<Follows> follows = followService.getFollowsToUser(userId, skip, limit);
		if (follows != null) {
			FollowsRelationship fr = new FollowsRelationship();
			for (Follows f : follows) {
				fr.setOperUserId(f.getStartNode().getUserId());
				fr.setTargetUserId(f.getEndNode().getUserId());
				fr.setRemark(f.getRemark());
				fr.setCreatedAt(f.getCreatedAt());
				followsRelationship.add(fr);
			}
		}
		return followsRelationship;
	}

	@Override
	public List<Integer> getUserFriendsAtDeepTwo(Integer userId, Long skip, Integer limit) throws XhrcException {
		List<Integer> fofid = friendService.getUserFriendsAtDeepTwo(userId, skip, limit);
		if (fofid == null) {
			fofid = new ArrayList<Integer>();
		}
		return fofid;
	}

	@Override
	public long countFollowsToUser(Integer userId) throws XhrcException {
		return followService.countFollowsToUser(userId);
	}

}

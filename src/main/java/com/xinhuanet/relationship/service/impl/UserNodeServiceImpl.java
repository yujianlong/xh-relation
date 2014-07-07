package com.xinhuanet.relationship.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.xinhuanet.relationship.common.exception.XhrcException;
import com.xinhuanet.relationship.common.exception.XhrcRuntimeException;
import com.xinhuanet.relationship.entity.node.UserNode;
import com.xinhuanet.relationship.lang.Lang;
import com.xinhuanet.relationship.service.UserNodeService;

@Service("userNodeService")
public class UserNodeServiceImpl extends BaseServiceImpl implements UserNodeService {

	private static Logger logger = LoggerFactory.getLogger(UserNodeServiceImpl.class);
	private static final int FRIEND_GROUP_LENGTH = 100; // 好友分组名称最大长度

	@Override
	public List<String> getUserCustomFriendGroups(Integer userId) throws XhrcException {
		UserNode uNode = getUserNode(userId);
		return uNode.getCustomFriendGroups();
	}

	@Override
	public List<String> getUserFriendGroups(Integer userId) throws XhrcException {
		// 取系统好友分组
		List<String> groups = getRelationshipConfig().getSystemFriendGroups();
		if (Lang.isEmpty(groups)) {
			logger.error("获取系统定义好友分组失败：列表为空。");
			throw new XhrcRuntimeException("20021", "获取系统定义分组失败：列表为空。");
		}
		// 联合用户自定义好友分组列表
		List<String> customGroups = getUserCustomFriendGroups(userId);
		if (!Lang.isEmpty(customGroups)) {
			groups.addAll(customGroups);
		}
		return groups;
	}

	@Override
	public boolean createUserCustomFriendGroup(Integer userId, String friendGroup) throws XhrcException {
		UserNode uNode = getUserNode(userId);
		// 校验参数
		if (StringUtils.isEmpty(friendGroup)) {
			logger.warn("创建用户自定义好友分组失败：friendGroup为空。friendGroup=" + friendGroup);
			throw new XhrcRuntimeException("20041", "创建用户自定义好友分组失败：friendGroup为空。friendGroup=" + friendGroup);
		}
		if (friendGroup.length() > FRIEND_GROUP_LENGTH) {
			logger.warn("创建好友自定义分组失败，friendGroup长度不合法。");
			throw new XhrcRuntimeException("20042", "创建好友自定义分组失败，friendGroup长度不合法。");
		}
		// 获取用户自定义好友分组
		List<String> friendGroups = uNode.getCustomFriendGroups();
		if (friendGroups == null) {
			friendGroups = new ArrayList<String>();
		}
		// 取系统好友分组
		List<String> systemFriendGroups = getRelationshipConfig().getSystemFriendGroups();
		// 校验好友分组是否重复
		if (friendGroups.contains(friendGroup) || systemFriendGroups.contains(friendGroup)) {
			logger.warn("创建好友自定义分组失败：分组名已存在。friendGroup=" + friendGroup);
			throw new XhrcRuntimeException("20043", "创建好友自定义分组失败：分组名已存在。friendGroup=" + friendGroup);
		}
		// 添加好友分组并保存
		friendGroups.add(friendGroup);
		uNode.setCustomFriendGroups(friendGroups);
		uNode.setUpdatedAt(Calendar.getInstance().getTime());
		saveUserNode(uNode);
		return true;
	}

	@Override
	public boolean deleteUserCustomFriendGroup(Integer userId, String friendGroup) throws XhrcException {
		UserNode uNode = getUserNode(userId);
		// 校验参数
		if (StringUtils.isEmpty(friendGroup)) {
			logger.warn("删除用户好友自定义分组失败：friendGroup为空。friendGroup=" + friendGroup);
			throw new XhrcRuntimeException("20041", "删除用户好友自定义分组失败：friendGroup为空。friendGroup=" + friendGroup);
		}
		// 校验好友分组是否存在
		List<String> friendGroups = uNode.getCustomFriendGroups();
		if (!friendGroups.contains(friendGroup)) {
			logger.warn("删除用户好友自定义分组失败：分组不存在。userId=" + userId + "；friendGroup=" + friendGroup);
			throw new XhrcRuntimeException("20044", "删除用户好友自定义分组失败：分组不存在。userId=" + userId + "；friendGroup="
					+ friendGroup);
		}
		// 移除分组并保存
		friendGroups.remove(friendGroup);
		uNode.setCustomFriendGroups(friendGroups);
		uNode.setUpdatedAt(Calendar.getInstance().getTime());
		saveUserNode(uNode);
		// 删除用户所有的好友关系中，好友分组属性——列表中值为friendGroup的元素 TODO 关系的updateAt字段没更新
		userRepository().removeFriendGroupForUserFriendRelationship(uNode.getNodeId(), friendGroup);
		// 用户所有的好友关系中，好友分组属性为空列表的，全部设置为["默认分组"]
		userRepository().addDefaultFriendGroupForEmptyFriendGroupRelationship(uNode.getNodeId());
		return true;
	}

	@Override
	public boolean existUserFriendGroup(Integer userId, String friendGroup) throws XhrcException {
		// 此时应该是“默认分组”
		if (StringUtils.isEmpty(friendGroup)) {
			return true;
		}
		List<String> groups = getUserFriendGroups(userId);
		return groups.contains(friendGroup);
	}

	@Override
	public boolean existUserFriendGroups(Integer userId, List<String> friendGroups) throws XhrcException {
		// 此时应该是“默认分组”
		if (Lang.isEmpty(friendGroups)) {
			return true;
		}
		List<String> groups = getUserFriendGroups(userId);
		return groups.containsAll(friendGroups);
	}

}

package com.xinhuanet.relationship.service;

import java.util.List;

import com.xinhuanet.relationship.common.exception.XhrcException;

public interface UserNodeService extends BaseService {

	/**
	 * 获取用户自定义好友分组列表
	 * 
	 * @param userId
	 * @return 用户分组列表
	 */
	List<String> getUserCustomFriendGroups(Integer userId) throws XhrcException;

	/**
	 * 获取用户好友分组列表
	 * 
	 * @param userId
	 * @return 用户分组列表
	 */
	List<String> getUserFriendGroups(Integer userId) throws XhrcException;

	/**
	 * 创建用户自定义好友分组
	 * 
	 * @param userId
	 * @param friendGroup 好友分组名
	 */
	boolean createUserCustomFriendGroup(Integer userId, String friendGroup) throws XhrcException;

	/**
	 * 删除用户自定义好友分组
	 * 
	 * @param userId
	 * @param friendGroup 好友分组名
	 */
	boolean deleteUserCustomFriendGroup(Integer userId, String friendGroup) throws XhrcException;

	/**
	 * 判断friendGroup是否存在于指定用户的好友分组列表中
	 * 
	 * @param userId
	 * @return
	 * @throws XhrcException
	 */
	boolean existUserFriendGroup(Integer userId, String friendGroup) throws XhrcException;

	/**
	 * 判断列表friendGroups是否存在于指定用户的好友分组列表中
	 * 
	 * @param userId
	 * @return
	 * @throws XhrcException
	 */
	boolean existUserFriendGroups(Integer userId, List<String> friendGroups) throws XhrcException;
}

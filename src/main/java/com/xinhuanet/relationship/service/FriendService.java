package com.xinhuanet.relationship.service;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.query.Param;

import com.xinhuanet.relationship.common.exception.XhrcException;
import com.xinhuanet.relationship.entity.relationship.Friend;

public interface FriendService extends BaseService {

	/**
	 * <p>
	 * 删除双向好友关系.
	 * </p>
	 * 
	 * @param Integer userId1
	 *            删除好友关系的用户ID1
	 * @param Integer userId2
	 *            删除好友关系的用户ID2
	 * @return boolean
	 *         删除操作成功返回true 失败返回false
	 * @throws XhrcException
	 * @author chenwc
	 */
	boolean deleteFriendBetween(Integer userId1, Integer userId2) throws XhrcException;

	/**
	 * <p>
	 * 判断是否存在好友关系.
	 * </p>
	 * 
	 * @param Integer userId1
	 *            删除好友关系的用户ID1
	 * @param Integer userId2
	 *            删除好友关系的用户ID2
	 * @return boolean
	 *         存在好友关系返回true 不存在好友关系返回false
	 * @throws XhrcException
	 * @author chenwc
	 */
	boolean existFriendBetween(Integer userId1, Integer userId2) throws XhrcException;

	/**
	 * <p>
	 * 按分组 计算用户好友数量.
	 * </p>
	 * 
	 * @param Integer userIdId
	 *            用户节点ID
	 * @param String group
	 *            用户群组
	 * @return long
	 *         用户好友数量
	 * @throws XhrcException
	 * @author chenwc
	 */
	@Query("START n=Node:UserNode(userId={userId}) MATCH n-[r:IS_FRIEND]->t -[:IS_FRIEND]->n WHERE r.targetUserGroup! ={targetUserGroup} RETURN COUNT(t)")
	long countUserFriendByGroup(@Param("userId") Integer userId, @Param("targetUserGroup") String group)
			throws XhrcException;

	/**
	 * <p>
	 * 计算用户所有好友数量.
	 * </p>
	 * 
	 * @param Integer userIdId
	 *            用户节点ID
	 * @return long
	 *         用户好友数量
	 * @throws XhrcException
	 * @author chenwc
	 */
	long countUserFriend(Integer userId) throws XhrcException;

	/**
	 * <p>
	 * 创建好友关系.
	 * </p>
	 * 
	 * @param Integer startUserId
	 *            创建好友关系操作用户ID
	 * @param Integer endUserId
	 *            创建好友关系目标用户ID
	 * @param String remarkName
	 *            备注名称
	 * @param String remark
	 *            指定备注
	 * @param String friendDesc
	 *            描述信息
	 * @param boolean privateFlag
	 *        公开隐私
	 * @param List<String> friendGroups
	 *            用户分组
	 * @return boolean
	 * @throws XhrcException
	 * @author yjl
	 */
	boolean createFriend(Integer startUserId, Integer endUserId, String remarkName, String remark, boolean privateFlag,
			List<String> friendGroups) throws XhrcException;

	/**
	 * <p>
	 * 更新好友关系.
	 * </p>
	 * 
	 * @param Integer startUserId
	 *            更新好友关系操作用户ID
	 * @param Integer endUserId
	 *            更新好友关系目标用户ID
	 * @param String remarkName
	 *            备注名称
	 * @param String remark
	 *            备注
	 * @param String friendDesc
	 *            描述信息
	 * @param boolean privateFlag
	 *        公开隐私
	 * @param List<String> friendGroups
	 *            用户分组
	 * @return boolean
	 * @throws XhrcException
	 * @author yjl
	 */
	boolean updateFriend(Integer startUserId, Integer endUserId, String remarkName, String remark, Boolean privateFlag,
			List<String> friendGroups) throws XhrcException;

	/**
	 * <p>
	 * 获取指定用户在指定好友分组的好友关系信息列表。
	 * </p>
	 * 
	 * @param Integer userId
	 *            操作用户id
	 * @param String friendGroup
	 *            指定的好友分组 ，如果为空则返回所有的好友。
	 * @param skip
	 *            获取id列表时跳过的好友关系信息数目，当skip为null或小于0时，默认设置为0
	 * @param limit
	 *            获取的好友关系信息数目，当limit为null，小于0、等于0是默认设为20，limit最大为1000.
	 * @return List<IsFriend>
	 *         用户指定好友分组的好友关系信息列表，可能为空列表
	 *         好友关系信息包括用户（startUser）、好友（endUser）、好友关系Id（relId）
	 *         好友备注（remark）、用户好友分组列表（friendGroups）、是否为私密好友（privateFlag）、
	 *         好友关系创建时间（createAt）、好友关系更新时间（updatedAt）
	 * @throws XhrcException
	 * @author zhaodm
	 */

	List<Friend> getUserFriends(Integer userId, String group, Long skip, Integer limit) throws XhrcException;

	/**
	 * <p>
	 * 获得指定用户深度为2的好友id列表、 不包括用户的直接好友,跳过前skip条记录，取limit条记录
	 * </p>
	 * 
	 * @param userId
	 *            操作用户id
	 * @param skip
	 *            获取id列表时跳过的id数目，当skip为null或小于0时，默认设置为0
	 * @param limit
	 *            获取的id数目，当limit为null，小于0、等于0是默认设为20，limit最大为1000.
	 * @return List
	 *         用户深度为2的朋友id指定位置指定数目的列表（skip+1至skip+limit），可能为空列表
	 * @throws XhrcException
	 * @author zhaodm
	 */
	List<Integer> getUserFriendsAtDeepTwo(Integer userId, Long skip, Integer limit) throws XhrcException;

}

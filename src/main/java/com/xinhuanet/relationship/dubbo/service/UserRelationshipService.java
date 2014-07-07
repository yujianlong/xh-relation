package com.xinhuanet.relationship.dubbo.service;

import java.util.List;
import java.util.Map;

import com.xinhuanet.relationship.common.exception.XhrcException;
import com.xinhuanet.relationship.dubbo.model.FollowsRelationship;
import com.xinhuanet.relationship.dubbo.model.FriendRelationship;
import com.xinhuanet.relationship.dubbo.model.FriendRequestRelationship;

/**
 * 
 * 用户关系接口
 * 
 */
public interface UserRelationshipService {

	/**
	 * 创建用户自定义好友分组
	 * 
	 * @param userId 指定用户id
	 * @param friendGroup 指定分组名称（长度100以内）
	 * @return 是否成功
	 * @author gongchengdong
	 */
	boolean createUserCustomFriendGroup(Integer userId, String friendGroup) throws XhrcException;

	/**
	 * <p>
	 * 获取用户好友分组
	 * </p>
	 * <p>
	 * 获取指定用户的好友分组列表，包括系统好友分组和自定义好友分组。
	 * </p>
	 * 
	 * @param userId 指定用户id
	 * @return 好友分组列表
	 * @author gongchengdong
	 */
	List<String> getUserFriendGroups(Integer userId) throws XhrcException;

	/**
	 * 删除用户自定义好友分组
	 * 
	 * @param userId 指定用户id
	 * @param friendGroup 分组名称
	 * @return 是否成功
	 * @author gongchengdong
	 */
	boolean deleteUserCustomFriendGroup(Integer userId, String friendGroup) throws XhrcException;

	/**
	 * <p>
	 * 操作用户向目标用户发送好友请求
	 * </p>
	 * <p>
	 * 1. 如果我在对方的黑名单里，对方在我的黑名单，两者已经是好友，均无法发送好友请求。
	 * </P>
	 * <p>
	 * 2. 建立好友请求关系。
	 * </P>
	 * 
	 * @param operUserId
	 *            发出请求的用户id
	 * @param targetUserId
	 *            接收请求的用户id
	 * @param message (长度1000以内)
	 *            附言
	 * @return boolean
	 * @throws XhrcException
	 * @author yjl
	 */
	boolean createFriendRequest(Integer operUserId, Integer targetUserId, String message) throws XhrcException;

	/**
	 * <p>
	 * 获取对指定用户发送的好友请求信息列表
	 * </p>
	 * 
	 * @param userId 指定用户id
	 * @return List<FriendRequest>
	 *         对用户发送的好友请求信息列表，可能为空列表
	 *         请求信息 FriendRequest
	 *         包括发送请求用户userId（operUserId）、被请求用户userId（targetUserId）、
	 *         请求发送时间（createAt）和附言（message）
	 * @throws XhrcException
	 * @author zhaodm
	 */

	List<FriendRequestRelationship> getFriendRequestToUser(Integer userId) throws XhrcException;

	/**
	 * <p>
	 * 判断是否操作用户向目标用户发送过好友请求
	 * </p>
	 * 
	 * @param operUserId 发出请求的操作用户id
	 * @param targetUserId 接收请求的目标用户id
	 * @return boolean
	 * @throws XhrcException
	 * @author chenwc
	 */
	boolean existFriendRequest(Integer operUserId, Integer targetUserId) throws XhrcException;

	/**
	 * <p>
	 * 操作用户同意目标用户发送的好友请求
	 * </p>
	 * <p>
	 * 1. 删除目标用户向操作用户发送的好友请求；删除操作用户向目标用户发送的好友请求（若存在）。
	 * </p>
	 * <p>
	 * 2. 建立好友关系。（目标用户会在操作用户的指定的好友分组中，且可指定备注及是否私密好友；操作用户会在目标用户的名为“默认分组”的好友分组里， 且为非私密好友。）
	 * </p>
	 * 
	 * @param operUserId 接受请求的操作用户id
	 * @param targetUserId 发送请求的目标用户id
	 * @param remarkName 备注姓名 (长度20以内)
	 * @param remark 指定备注 (长度500以内)
	 * @param privateFlag 指定是否私密好友
	 * @param friendGroups 指定好友分组 (长度100以内)
	 * @return boolean
	 * @throws XhrcException
	 * @author yjl
	 */
	boolean acceptFriendRequest(Integer operUserId, Integer targetUserId, String remarkName, String remark,
			boolean privateFlag, List<String> friendGroups) throws XhrcException;

	/**
	 * <p>
	 * 操作用户拒绝目标用户发送的好友请求
	 * </p>
	 * <p>
	 * 1. 删除目标用户向操作用户发送的好友请求。
	 * </p>
	 * <p>
	 * 2. 删除操作用户向目标用户发送的好友请求（若存在）。
	 * </p>
	 * 
	 * @param operUserId 拒绝的操作用户id
	 * @param targetUserId 被拒绝目标用户id
	 * @return boolean
	 * @throws XhrcException
	 * @author chenwc
	 */
	boolean refuseFriendRequest(Integer operUserId, Integer targetUserId) throws XhrcException;

	/**
	 * <p>
	 * 获取指定用户的好友关系信息列表，可指定好友分组。 跳过前skip条记录，取limit条记录
	 * </p>
	 * <p>
	 * 根据好友分组参数是否为null，返回不同结果。 若非null则返回指定好友分组内的好友关系信息列表； 若为null则返回用户的所有好友关系信息列表
	 * </p>
	 * 
	 * @param Integer userId 指定用户id
	 * @param String friendGroup
	 *            指定的好友分组 可以为null，
	 *            若非null则返回用户在指定好友分组内的好友关系信息列表，
	 *            若为null则返回用户的所有好友关系信息列表
	 * @param Long skip
	 *            获取时跳过的好友关系信息数目，当skip为null或小于0时，默认设置为0
	 * @param Integer limit
	 *            获取的好友关系信息数目，当limit为null，小于0，等于0，默认设置为20,limit最大为1000.
	 * @return List<FriendRelationship>
	 *         用户的好友关系信息的指定位置指定数目的列表（skip+1至skip+limit），可能为空列表
	 *         好友关系信息包括用户userId（operUserId）、好友userId（targetUserId）、
	 *         好友备注（remark）、好友分组列表（friendGroups）、是否为私密好友（privateFlag）、
	 *         好友关系创建时间（createAt）
	 * @throws XhrcException
	 * @author zhaodm
	 */

	List<FriendRelationship> getUserFriends(Integer userId, String friendGroup, Long skip, Integer limit)
			throws XhrcException;

	/**
	 * <p>
	 * 获得指定用户深度为2的好友id列表、 不包括用户的直接好友,跳过前skip条记录，再取limit条记录
	 * </p>
	 * 
	 * @param userId
	 *            操作用户id
	 * @param skip
	 *            获取id列表时跳过的id数目，当skip为null或小于0时，默认设置为0
	 * @param limit
	 *            获取的id数目,当limit为null，小于0，等于0，默认设置为20，limit最大为1000.
	 * @return List
	 *         用户深度为2的好友id指定位置指定数目的列表（skip+1至skip+limit），可能为空列表
	 * @throws XhrcException
	 * @author zhaodm
	 */
	List<Integer> getUserFriendsAtDeepTwo(Integer userId, Long skip, Integer limit) throws XhrcException;

	/**
	 * <p>
	 * 统计指定用户指定好友分组的好友数量
	 * </p>
	 * <p>
	 * 好友分组参数为空串或null，则统计指定用户所有好友数量。
	 * </p>
	 * 
	 * @param Integer userId 指定用户id
	 * @param String friendGroup 指定好友分组
	 * @return long 用户好友数量
	 * @author chenwc
	 */
	long countUserFriends(Integer userId, String friendGroup) throws XhrcException;

	/**
	 * <p>
	 * 统计指定用户每个好友分组的好友数量
	 * </p>
	 * 
	 * @param Integer userId 指定用户id
	 * @return Map 用户每个好友分组的好友数量
	 * @author zhaodm
	 */
	Map<String, Long> countUserFriendsByEachGroup(Integer userId) throws XhrcException;

	/**
	 * <p>
	 * 判断两个用户是否是好友
	 * </p>
	 * 
	 * @param Integer userId1 用户1的用户id
	 * @param Integer userId2 用户2的用户id
	 * @return boolean
	 *         存在好友关系返回true 不存在好友关系返回false
	 * @throws XhrcException
	 * @author chenwc
	 */
	boolean existFriendBetween(Integer userId1, Integer userId2) throws XhrcException;

	/**
	 * 更新好友关系的属性（单向）
	 * 如果某项属性参数为空或null则不更新该属性
	 * 
	 * @param operUserId 操作用户ID
	 * @param targetUserId 目标用户ID
	 * @param remarkName 备注姓名 (长度20以内)
	 * @param remark 指定备注 (长度500以内)
	 * @param privateFlag 指定是否私密好友
	 * @param friendGroups 指定用户分组 (长度100以内)
	 * @return boolean
	 * @throws XhrcException
	 * @author yjl
	 */
	boolean updateFriend(Integer operUserId, Integer targetUserId, String remarkName, String remark,
			Boolean privateFlag, List<String> friendGroups) throws XhrcException;

	/**
	 * <p>
	 * 删除好友
	 * </p>
	 * <p>
	 * 1. 若不存在关注（目标用户关注了操作用户）关系，但存在阻止（操作用户阻止了目标用户）关系，则删除该阻止关系。
	 * </p>
	 * <p>
	 * 2. 若不存在关注（操作用户关注了目标用户）关系，但存在屏蔽（操作用户屏蔽了目标用户）关系，则删除该屏蔽关系。
	 * </p>
	 * <p>
	 * 3. 删除好友关系（双向）。
	 * </p>
	 * 
	 * @param Integer userId1 用户1的用户id
	 * @param Integer userId2 用户2的用户id
	 * @return boolean 删除操作成功返回true 失败返回false
	 * @throws XhrcException
	 * @author chenwc
	 */
	boolean deleteFriendBetween(Integer userId1, Integer userId2) throws XhrcException;

	/**
	 * <p>
	 * 关注目标用户
	 * </p>
	 * <p>
	 * 1. 若操作用户拉黑了目标用户，或目标用户拉黑了操作用户，均无法添加关注关系。
	 * </p>
	 * <p>
	 * 2. 添加关注关系，可指定备注。
	 * </p>
	 * 
	 * @param operUserId 操作用户id
	 * @param targetUserId 目标用户id
	 * @param remark 指定备注 (长度500以内)
	 * @return boolean
	 * @throws XhrcException
	 * @author yjl
	 */
	boolean createFollow(Integer operUserId, Integer targetUserId, String remark) throws XhrcException;

	/**
	 * <p>
	 * 统计指定用户关注的用户数量
	 * </p>
	 * 
	 * @param Integer userId 指定用户id
	 * @return long 关注数量
	 * @throws XhrcException
	 * @author chenwc
	 */
	long countUserFollows(Integer userId) throws XhrcException;

	/**
	 * <p>
	 * 统计指定用户被关注的用户数量
	 * </p>
	 * 
	 * @param Integer userId 指定用户id
	 * @return long 关注数量
	 * @throws XhrcException
	 * @author zhaodm
	 */
	long countFollowsToUser(Integer userId) throws XhrcException;

	/**
	 * <p>
	 * 判断操作用户是否关注了目标用户
	 * </p>
	 * 
	 * @param Integer operUserId 操作用户id
	 * @param Integer targetUserId 目标用户id
	 * @return boolean
	 * @throws XhrcException
	 * @author chenwc
	 */
	boolean existFollow(Integer operUserId, Integer targetUserId) throws XhrcException;

	/**
	 * 更新关注关系
	 * 
	 * @param operUserId 更新关注的操作用户id
	 * @param targetUserId 更新关注的目标用户id
	 * @param remark 指定备注 (长度500以内)
	 * @return boolean
	 * @throws XhrcException
	 * @author yjl
	 */
	boolean updateFollow(Integer operUserId, Integer targetUserId, String remark) throws XhrcException;

	/**
	 * <p>
	 * 取消对目标用户的关注
	 * </p>
	 * <p>
	 * 1. 判断是否存在好友关系，如不存在好友关系，但存在阻止（目标用户阻止了操作用户）或屏蔽（操作用户屏蔽了目标用户）关系，则删除该阻止及屏蔽关系。
	 * </p>
	 * <p>
	 * 2. 删除关注关系。
	 * </p>
	 * 
	 * @param Integer operUserId 操作用户id
	 * @param Integer targetUserId 目标用户id
	 * @return boolean
	 * @throws XhrcException
	 * @author chenwc
	 */
	boolean deleteFollow(Integer operUserId, Integer targetUserId) throws XhrcException;

	/**
	 * <p>
	 * 拉黑目标用户
	 * </p>
	 * <p>
	 * 1. 判断两者是否存在好友关系，不存在则无法添加黑名单。
	 * </p>
	 * <p>
	 * 2. 删除两者之间一切关系（关注、阻止、好友）。
	 * </p>
	 * <p>
	 * 3. 建立操作用户对目标用户的黑名单关系。
	 * </P>
	 * 
	 * @param operUserId 操作用户id
	 * @param targetUserId 拉到黑名单中的目标用户id
	 * @return boolean
	 * @throws XhrcException
	 * @author yjl
	 */
	boolean createBlacklist(Integer operUserId, Integer targetUserId) throws XhrcException;

	/**
	 * <p>
	 * 获取指定用户黑名单中的用户id列表
	 * </p>
	 * 
	 * @param Integer userId 指定用户id
	 * @return List<Integer> 用户黑名单的用户id列表，可能为空列表
	 * @throws XhrcException
	 * @author zhaodm
	 */

	List<Integer> getUserBlacklists(Integer userId) throws XhrcException;

	/**
	 * <p>
	 * 统计指定用户拉黑的用户数目
	 * </p>
	 * 
	 * @param Integer userId 指定用户id
	 * @return long 黑名单中用户数量
	 * @throws XhrcException
	 * @author zhaodm
	 */

	long countUserBlacklists(Integer userId) throws XhrcException;

	/**
	 * <p>
	 * 判断操作用户是否拉黑了目标用户
	 * </p>
	 * 
	 * @param Integer operUserId 操作用户id
	 * @param Integer targetUserId 是否在黑名单中的目标用户id
	 * @return boolean
	 * @throws XhrcException
	 * @author chenwc
	 */
	boolean existBlacklist(Integer operUserId, Integer targetUserId) throws XhrcException;

	/**
	 * <p>
	 * 取消对目标用户的拉黑
	 * </p>
	 * 
	 * @param Integer operUserId 操作用户id
	 * @param Integer targetUserId 被移出黑名单的目标用户id
	 * @return boolean
	 * @throws XhrcException
	 * @author chenwc
	 */
	boolean deleteBlacklist(Integer operUserId, Integer targetUserId) throws XhrcException;

	/**
	 * <p>
	 * 屏蔽目标用户
	 * </p>
	 * <p>
	 * 1. 判断是否存在好友关系或者关注（操作用户关注了目标用户）关系，如果二者都不存在，则无法添加屏蔽。
	 * </p>
	 * <p>
	 * 2. 添加屏蔽关系。
	 * </p>
	 * 
	 * @param operUserId 操作用户id
	 * @param targetUserId 被屏蔽的目标用户id
	 * @return boolean
	 * @throws XhrcException
	 * @author yjl
	 */
	boolean createBlock(Integer operUserId, Integer targetUserId) throws XhrcException;

	/**
	 * <p>
	 * 获取指定用户屏蔽的用户id列表
	 * </p>
	 * 
	 * @param Integer userId 操作用户id
	 * @return List<Integer> 用户屏蔽的用户id列表，可能为空列表
	 * @throws XhrcException
	 * @author zhaodm
	 */
	List<Integer> getUserBlocks(Integer userId) throws XhrcException;

	/**
	 * <p>
	 * 统计指定用户屏蔽的用户数量
	 * </p>
	 * 
	 * @param Integer userId 指定用户的id
	 * @return long 屏蔽的用户数量
	 * @throws XhrcException
	 * @author zhaodm
	 */
	long countUserBlocks(Integer userId) throws XhrcException;

	/**
	 * <p>
	 * 判断操作用户是否屏蔽了目标用户
	 * </p>
	 * 
	 * @param Integer operUserId 操作用户id
	 * @param Integer targetUserId 目标用户id
	 * @return boolean
	 * @throws XhrcException
	 * @author chenwc
	 */
	boolean existBlock(Integer operUserId, Integer targetUserId) throws XhrcException;

	/**
	 * <p>
	 * 取消对目标用户的屏蔽
	 * </p>
	 * 
	 * @param Integer operUserId 操作用户id
	 * @param Integer targetUserId 被屏蔽的目标用户id
	 * @return boolean
	 * @throws XhrcException
	 * @author chenwc
	 */
	boolean deleteBlock(Integer operUserId, Integer targetUserId) throws XhrcException;

	/**
	 * <p>
	 * 阻止目标用户
	 * </p>
	 * <p>
	 * 1. 判断是否存在好友关系或者关注（目标用户关注了操作用户）关系，如果二者都不存在，则无法添加阻止。
	 * </p>
	 * <p>
	 * 2. 添加阻止关系。
	 * </p>
	 * 
	 * @param Integer operUserId 操作用户id
	 * @param Integer targetUserId 目标用户id
	 * @return 成功返回true, 失败返回false
	 * @throws XhrcException
	 * @author zhangyanchao
	 */
	boolean createObstruct(Integer operUserId, Integer targetUserId) throws XhrcException;

	/**
	 * <p>
	 * 获取指定用户阻止的用户信息列表
	 * </p>
	 * 
	 * @param Integer userId 指定用户id
	 * @return List<Integer>
	 *         返回用户阻止id列表，可能为空列表
	 * @throws XhrcException
	 * @author zhaodm
	 */

	List<Integer> getUserObstructs(Integer userId) throws XhrcException;

	/**
	 * <p>
	 * 统计指定用户阻止的用户数量
	 * </p>
	 * 
	 * @param Integer userId 指定用户id
	 * @return long
	 *         用户阻止的用户数量
	 * @throws XhrcException
	 * @author zhaodm
	 */

	long countUserObstructs(Integer userId) throws XhrcException;

	/**
	 * <p>
	 * 判断操作用户是否阻止了目标用户
	 * </p>
	 * 
	 * @param operUserId 操作用户id
	 * @param targetUserId 目标用户id
	 * @throws XhrcException
	 * @return boolean
	 * @author chenwc
	 */
	boolean existObstruct(Integer operUserId, Integer targetUserId) throws XhrcException;

	/**
	 * <p>
	 * 取消对目标用户的阻止
	 * </p>
	 * 
	 * @param operUserId 操作用户id
	 * @param targetUserId 目标用户id
	 * @return boolean
	 * @throws XhrcException
	 * @author chenwc
	 */
	boolean deleteObstruct(Integer operUserId, Integer targetUserId) throws XhrcException;

	/**
	 * <p>
	 * 获取指定用户关注信息列表。 跳过前skip条记录，取limit条记录
	 * </p>
	 * 
	 * @param Integer userId
	 *            操作用户id
	 * @param Long skip
	 *            获取时跳过的记录数目，当skip为null或小于0时，默认设置为0
	 * @param Integer limit
	 *            获取的关注信息数量，当limit为null，小于0，等于0，默认设置为20，limit最大为1000.
	 * @return List<Follows>
	 *         返回用户关注信息的指定位置指定数目的列表（skip+1至skip+limit），可能为空列表。
	 *         关注信息包括发起关注的用户userId（operUserId）、被关注用户的userId（targetUserId）、
	 *         关注的时间（createAt）和备注信息（remark）
	 * @throws XhrcException
	 * @author zhaodm
	 */
	List<FollowsRelationship> getUserFollows(Integer userId, Long skip, Integer limit) throws XhrcException;

	/**
	 * <p>
	 * 获取对指定用户的关注信息列表。 跳过前skip条记录，取limit条记录
	 * </p>
	 * 
	 * @param Integer userId
	 *            操作用户id
	 * @param Long skip
	 *            获取时跳过的关注信息数目，当skip为null或小于0时，默认设置为0
	 * @param Integer limit
	 *            获取的关注信息数目，当limit为null，小于0，等于0，默认设置为20，limit最大为1000.
	 * @return List<Follows>
	 *         返回对指定用户的关注信息的指定位置指定数目的列表（skip+1至skip+limit），可能为空列表。
	 *         关注信息包括发起关注的用户userId（operUserId）、被关注用户的userId（targetUserId）、
	 *         关注的时间（createAt）和备注信息（remark）
	 * @throws XhrcException
	 * @author zhaodm
	 */
	List<FollowsRelationship> getFollowsToUser(Integer userId, Long skip, Integer limit) throws XhrcException;

}

package com.xinhuanet.relationship.service;

import java.util.List;

import com.xinhuanet.relationship.common.exception.XhrcException;
import com.xinhuanet.relationship.entity.relationship.Follows;

public interface FollowService extends BaseService {
	/**
	 * <p>
	 * 删除关注关系.
	 * </p>
	 * 
	 * @param Integer startUserId
	 *            发起关注用户id
	 * @param Integer endUserId
	 *            被关注用户id
	 * @return boolean
	 * @author chenwc
	 */
	boolean deleteFollow(Integer startUserId, Integer endUserId) throws XhrcException;

	/**
	 * <p>
	 * 判断是否存在关注关系.
	 * </p>
	 * 
	 * @param Integer startUserId
	 *            发起关注用户id
	 * @param Integer endUserId
	 *            被关注用户id
	 * @return boolean
	 * @author chenwc
	 */
	boolean existFollow(Integer startUserId, Integer endUserId) throws XhrcException;

	/**
	 * <p>
	 * 计算关注人数.
	 * </p>
	 * 
	 * @param Integer startUserId
	 *            发起关注用户id
	 * @return long
	 *         关注数量
	 * @author chenwc
	 */
	long countFollow(Integer startUserId) throws XhrcException;

	/**
	 * <p>
	 * 创建关注关系.
	 * </p>
	 * 
	 * @param Integer startUserId
	 *            创建关注关系操作用户ID
	 * @param Integer endUserId
	 *            创建关注关系目标用户ID
	 * @param String remark
	 *            备注
	 * @return boolean
	 * @throws XhrcException
	 * @author yjl
	 */
	boolean createFollow(Integer startUserId, Integer endUserId, String remark) throws XhrcException;

	/**
	 * <p>
	 * 更新关注关系.
	 * </p>
	 * 
	 * @param Integer startUserId
	 *            更新关注关系操作用户ID
	 * @param Integer endUserId
	 *            更新关注关系目标用户ID
	 * @param String remark
	 *            备注
	 * @return boolean
	 * @throws XhrcException
	 * @author yjl
	 */
	boolean updateFollow(Integer startUserId, Integer endUserId, String remark) throws XhrcException;

	/**
	 * <p>
	 * 获取指定用户关注信息列表。 跳过前offset个关注信息后，再取limit个关注信息
	 * </p>
	 * 
	 * @param Integer userId
	 *            操作用户id
	 * @param Long offset
	 *            获取时跳过的关注信息数
	 * @param Integer limit
	 *            获取的关注信息数量
	 * @return List<Follows>
	 *         返回用户关注信息的指定位置指定数目的列表（offset+1至offset+limit），可能为空列表。
	 * @throws XhrcException
	 * @author zhaodm
	 */
	List<Follows> getUserFollows(Integer userId, Long offset, Integer limit) throws XhrcException;

	/**
	 * <p>
	 * 获取对指定用户的关注信息列表。 跳过前offset个关注信息后，再取limit个关注信息
	 * </p>
	 * 
	 * @param Integer userId
	 *            操作用户id
	 * @param Long offset
	 *            获取时跳过的关注信息数
	 * @param Integer limit
	 *            获取的关注信息数量
	 * @return List<Follows>
	 *         返回对指定用户的关注信息的指定位置指定数目的列表（offset+1至offset+limit），可能为空列表。
	 * @throws XhrcException
	 * @author zhaodm
	 */
	List<Follows> getFollowsToUser(Integer userId, Long offset, Integer limit) throws XhrcException;

	/**
	 * <p>
	 * 统计关注了指定用户的用户数量
	 * </p>
	 * 
	 * @param Integer userId 指定用户id
	 * @return long 关注数量
	 * @throws XhrcException
	 * @author zhaodm
	 */
	long countFollowsToUser(Integer userId) throws XhrcException;
}

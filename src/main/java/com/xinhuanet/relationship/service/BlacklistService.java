package com.xinhuanet.relationship.service;

import java.util.List;

import com.xinhuanet.relationship.common.exception.XhrcException;

public interface BlacklistService extends BaseService {
	/**
	 * <p>
	 * 从startUser的黑名单中移除endUser。
	 * </p>
	 * 
	 * @param Integer startUserId
	 *            操作用户id
	 * @param Integer endUserId
	 *            被移出黑名单用户id
	 * @return boolean
	 * @throws XhrcException
	 * @author chenwc
	 */
	boolean deleteBlacklist(Integer startUserId, Integer endUserId) throws XhrcException;

	/**
	 * <p>
	 * 判断endUser是否在startUser的黑名单中。
	 * </p>
	 * 
	 * @param Integer startUserId
	 *            操作用户id
	 * @param Integer endUserId
	 *            是否在黑名单中的用户id
	 * @return boolean
	 * @throws XhrcException
	 * @author chenwc
	 */
	boolean existBlacklist(Integer startUserId, Integer endUserId) throws XhrcException;

	/**
	 * <p>
	 * 获得指定用户黑名单id列表
	 * </p>
	 * 
	 * @param Integer userId
	 *            操作用户id
	 * @return List<Integer>
	 *         返回指定用户黑名单id列表，可能为空列表
	 * @throws XhrcException
	 * @author zhaodm
	 */

	List<Integer> getUserBlacklists(Integer userId) throws XhrcException;

	/**
	 * <p>
	 * 获得指定用户黑名单中用户数量
	 * </p>
	 * 
	 * @param Integer userId
	 *            操作用户id
	 * @return List<Integer>
	 *         指定用户黑名单中用户数量，可能为空列表
	 * @throws XhrcException
	 * @author zhaodm
	 */

	long countUserBlacklists(Integer userId) throws XhrcException;

	/**
	 * 创建黑名单关系
	 * 
	 * @param startUserId
	 *            操作用户id
	 * @param endUserId
	 *            黑名单用户id
	 * @return boolean
	 * @throws XhrcException
	 * @author yjl
	 */
	boolean createBlackList(Integer startUserId, Integer endUserId) throws XhrcException;

}

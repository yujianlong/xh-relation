package com.xinhuanet.relationship.service;

import java.util.List;

import com.xinhuanet.relationship.common.exception.XhrcException;

public interface BlockService extends BaseService {
	/**
	 * <p>
	 * 删除屏蔽关系。
	 * </p>
	 * 
	 * @param Integer startUserId
	 *            当前用户id
	 * @param Integer endUserId
	 *            被屏蔽用户id
	 * @return boolean
	 * @author chenwc
	 */
	boolean deleteBlock(Integer startUserId, Integer endUserId) throws XhrcException;

	/**
	 * <p>
	 * 判断是否存在屏蔽关系。
	 * </p>
	 * 
	 * @param Integer startUserId
	 *            当前用户id
	 * @param Integer endUserId
	 *            被屏蔽用户id
	 * @return boolean
	 * @author chenwc
	 */
	boolean existBlock(Integer startUserId, Integer endUserId) throws XhrcException;

	/**
	 * <p>
	 * 获得指定用户屏蔽id列表
	 * </p>
	 * 
	 * @param Integer userId
	 *            操作用户id
	 * @return List<Integer>
	 *         返回指定用户屏蔽id列表，可能为空列表
	 * @throws XhrcException
	 * @author zhaodm
	 */

	List<Integer> getUserBlocks(Integer userId) throws XhrcException;

	/**
	 * <p>
	 * 获得指定用户屏蔽数量
	 * </p>
	 * 
	 * @param Integer userId
	 *            操作用户id
	 * @return long
	 *         指定用户屏蔽数量
	 * @throws XhrcException
	 * @author zhaodm
	 */

	long countUserBlocks(Integer userId) throws XhrcException;

	/**
	 * 创建屏蔽关系
	 * 
	 * @param startUserId
	 *            操作用户id
	 * @param endUserId
	 *            被屏蔽用户id
	 * @return boolean
	 * @throws XhrcException
	 * @author yjl
	 */
	boolean createBlock(Integer startUserId, Integer endUserId) throws XhrcException;

}

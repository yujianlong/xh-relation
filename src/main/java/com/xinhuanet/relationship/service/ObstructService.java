package com.xinhuanet.relationship.service;

import java.util.List;

import com.xinhuanet.relationship.common.exception.XhrcException;

public interface ObstructService extends BaseService {
	/**
	 * <p>
	 * 操作用户阻止了目标用户
	 * </p>
	 * <p>
	 * 说明：目标用户将看不到操作用户的动态
	 * </p>
	 * 
	 * @param Integer startUserId
	 *            操作用户id
	 * @param Integer endUserId
	 *            目标用户id
	 * @return 成功返回true, 失败返回false
	 * @throws XhrcException
	 * @author zhangyanchao
	 */
	boolean createObstruct(Integer startUserId, Integer endUserId) throws XhrcException;

	/**
	 * <p>
	 * 删除阻止关系。
	 * </p>
	 * 
	 * @param Integer startUserId
	 *            当前用户id
	 * @param Integer endUserId
	 *            被阻止用户id
	 * @return boolean
	 * @author chenwc
	 */
	boolean deleteObstruct(Integer startUserId, Integer endUserId) throws XhrcException;

	/**
	 * <p>
	 * 判断是否存在阻止关系。
	 * </p>
	 * 
	 * @param Integer startUserId
	 *            当前用户id
	 * @param Integer endUserId
	 *            被阻止用户id
	 * @return boolean
	 * @author chenwc
	 */
	boolean existObstruct(Integer startUserId, Integer endUserId) throws XhrcException;

	/**
	 * <p>
	 * 获得指定用户阻止id列表
	 * </p>
	 * 
	 * @param Integer userId
	 *            操作用户id
	 * @return List<Integer>
	 *         返回指定用户阻止id列表，可能为空列表
	 * @throws XhrcException
	 * @author zhaodm
	 */

	List<Integer> getUserObstructs(Integer userId) throws XhrcException;

	/**
	 * <p>
	 * 获得指定用户阻止数量
	 * </p>
	 * 
	 * @param Integer userId
	 *            操作用户id
	 * @return long
	 *         指定用户阻止数量
	 * @throws XhrcException
	 * @author zhaodm
	 */

	long countUserObstructs(Integer userId) throws XhrcException;

}

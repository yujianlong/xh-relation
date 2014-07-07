package com.xinhuanet.relationship.service;

import java.util.List;

import com.xinhuanet.relationship.common.exception.XhrcException;
import com.xinhuanet.relationship.entity.relationship.FriendRequest;

public interface FriendRequestService extends BaseService {

	/**
	 * <p>
	 * 创建好友请求关系.
	 * </p>
	 * 
	 * @param startUserId
	 *            发出请求的用户id
	 * @param endUserId
	 *            接收请求的用户id
	 * @param message
	 *            附言
	 * @return boolean
	 * @throws XhrcException
	 * @author yjl
	 */
	boolean createFriendRequest(Integer startUserId, Integer endUserId, String message) throws XhrcException;

	/**
	 * <p>
	 * 删除好友请求。
	 * </p>
	 * 
	 * @param startUserId
	 *            发出请求的用户id
	 * @param endUserId
	 *            接收请求的用户id
	 * @return boolean
	 * @throws XhrcException
	 * @author chenwc
	 */
	boolean deleteFriendRequest(Integer startUserId, Integer endUserId) throws XhrcException;

	/**
	 * <p>
	 * 判断是否存在好友请求（startUser对 endUser发出）。
	 * </p>
	 * 
	 * @param startUserId
	 *            发出请求的用户id
	 * @param endUserId
	 *            接收请求的用户id
	 * @return boolean
	 * @throws XhrcException
	 * @author chenwc
	 */
	boolean existFriendRequest(Integer startUserId, Integer endUserId) throws XhrcException;

	/**
	 * <p>
	 * 获得对指定用户发出的好友请求信息列表
	 * </p>
	 * 
	 * @param Integer userId
	 *            操作用户id
	 * @return List<FriendRequest>
	 *         对指定用户发出的好友请求信息列表，可能为空列表
	 * @throws XhrcException
	 * @author zhaodm
	 */

	List<FriendRequest> getFriendRequestToUser(Integer userId) throws XhrcException;

}

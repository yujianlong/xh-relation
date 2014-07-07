package com.xinhuanet.relationship.service.impl;

import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.xinhuanet.relationship.common.constant.RelTypeName;
import com.xinhuanet.relationship.common.exception.XhrcException;
import com.xinhuanet.relationship.common.exception.XhrcRuntimeException;
import com.xinhuanet.relationship.entity.node.UserNode;
import com.xinhuanet.relationship.entity.relationship.Follows;
import com.xinhuanet.relationship.repository.UserRepository;
import com.xinhuanet.relationship.service.FollowService;

@Service("followService")
public class FollowServiceImpl extends BaseServiceImpl implements FollowService {
	private static Logger logger = LoggerFactory.getLogger(FollowServiceImpl.class);
	private static final int REMARK_MAX_LENGTH = 500; // 关注备注最大长度
	private static final Integer LIMIT_DEFAULT = 20; // 分页默认值20
	private static final Integer SKIP_DEFAULT = 0;
	private static final int LIMIT_MAX = 1000; // 最大允许分页大小

	@Override
	public boolean deleteFollow(Integer startUserId, Integer endUserId) throws XhrcException {
		UserRepository userRepository = userRepository();
		boolean bool = false;

		UserNode startUser = getUserNode(startUserId);
		UserNode endUser = getUserNode(endUserId);

		try {
			userRepository.deleteRelationshipBetween(startUser, endUser, RelTypeName.FOLLOWS);
			bool = true;
		} catch (Exception e) {
			logger.error("删除关注关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId, e);
			throw new XhrcException("20021", "删除关注关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId,
					e);
		}
		return bool;
	}

	@Override
	public boolean existFollow(Integer startUserId, Integer endUserId) throws XhrcException {
		UserRepository userRepository = userRepository();
		boolean bool = false;

		UserNode startUser = getUserNode(startUserId);
		UserNode endUser = getUserNode(endUserId);
		try {
			Follows ifow = userRepository
					.getRelationshipBetween(startUser, endUser, Follows.class, RelTypeName.FOLLOWS);
			if (ifow != null) {
				bool = true;
			}
		} catch (Exception e) {
			logger.error("判断关注关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId, e);
			throw new XhrcRuntimeException("20021", "判断关注关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId="
					+ endUserId, e);
		}
		return bool;
	}

	@Override
	public long countFollow(Integer startUserId) throws XhrcException {
		long count = 0;
		UserNode node = getUserNode(startUserId);
		try {
			count = userRepository().countFollow(node.getNodeId());
		} catch (Exception e) {
			logger.error("统计关注数量失败：数据库操作异常。startUserId=" + startUserId, e);
			throw new XhrcException("20021", "统计关注数量失败：数据库操作异常。startUserId=" + startUserId, e);
		}
		return count;

	}

	@Override
	public boolean createFollow(Integer startUserId, Integer endUserId, String remark) throws XhrcException {

		if (!StringUtils.isEmpty(remark) && remark.length() > REMARK_MAX_LENGTH) {
			logger.warn("创建备注失败，remark长度不合法。");
			throw new XhrcRuntimeException("20042", "创建备注失败，remark长度不合法。");
		}

		UserNode startNode = getUserNode(startUserId);
		UserNode endNode = getUserNode(endUserId);
		try {
			Follows folRel = userRepository().createRelationshipBetween(startNode, endNode, Follows.class,
					RelTypeName.FOLLOWS);
			if (folRel == null) {
				logger.error("创建关注关系失败。startUserId=" + startUserId + "， endUserId=" + endUserId);
				throw new XhrcException("20021", "创建关注关系失败。startUserId=" + startUserId + "， endUserId=" + endUserId);
			}
			if (!StringUtils.isEmpty(remark)) {
				folRel.setRemark(remark);
			}

			folRel.setCreatedAt(Calendar.getInstance().getTime());
			template().save(folRel);
		} catch (Exception e) {
			logger.error("创建关注关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId, e);
			throw new XhrcException("20021", "创建关注关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId,
					e);
		}

		return true;
	}

	@Override
	public boolean updateFollow(Integer startUserId, Integer endUserId, String remark) throws XhrcException {
		if (!StringUtils.isEmpty(remark) && remark.length() > REMARK_MAX_LENGTH) {
			logger.warn("创建备注失败，remark长度不合法。");
			throw new XhrcRuntimeException("20042", "创建备注失败，remark长度不合法。");
		}
		UserNode startNode = getUserNode(startUserId);
		UserNode endNode = getUserNode(endUserId);
		Follows folRel = userRepository()
				.getRelationshipBetween(startNode, endNode, Follows.class, RelTypeName.FOLLOWS);
		if (folRel == null) {
			logger.error("更新关注关系失败。startUserId=" + startUserId + "， endUserId=" + endUserId + " 关注关系不存在");
			throw new XhrcException("20021", "更新关注关系失败。startUserId=" + startUserId + "， endUserId=" + endUserId
					+ " 关注关系不存在");

		}
		try {
			if (!StringUtils.isEmpty(remark)) {
				folRel.setRemark(remark);
			}
			folRel.setUpdatedAt(Calendar.getInstance().getTime());
			template().save(folRel);

		} catch (Exception e) {
			logger.error("更新关注关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId, e);
			throw new XhrcException("20021", "更新关注关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId,
					e);
		}

		return true;
	}

	@Override
	public List<Follows> getUserFollows(Integer userId, Long skip, Integer limit) throws XhrcException {
		long skipOrDefault = skip == null || skip < 0 ? SKIP_DEFAULT : skip;
		int limitOrDefault = limit == null || limit <= 0 ? LIMIT_DEFAULT : limit;
		if (limit != null && limit > LIMIT_MAX) {
			logger.warn("获取用户关注信息列表失败，limit（每页信息数目，最大为1000）不合法。limit = " + limit);
			throw new XhrcRuntimeException("20042", "获取用户关注信息列表失败，limit（每页信息数目，最大为1000）不合法。limit = " + limit);
		}
		UserNode uNode = getUserNode(userId);
		List<Follows> follows = null;
		try {
			follows = userRepository().getUserFollows(uNode.getNodeId(), skipOrDefault, limitOrDefault);
		} catch (Exception e) {
			logger.error("获取用户关注信息列表失败：数据库操作异常。userId=" + userId, e);
			throw new XhrcRuntimeException("20021", "获取用户关注信息列表失败：数据库操作异常。userId=" + userId, e);
		}
		return follows;
	}

	@Override
	public List<Follows> getFollowsToUser(Integer userId, Long skip, Integer limit) throws XhrcException {
		long skipOrDefault = skip == null || skip < 0 ? SKIP_DEFAULT : skip;
		int limitOrDefault = limit == null || limit <= 0 ? LIMIT_DEFAULT : limit;
		if (limit != null && limit > LIMIT_MAX) {
			logger.warn("获取对用户的关注信息列表失败，limit（每页信息数目，最大为1000）不合法。limit = " + limit);
			throw new XhrcRuntimeException("20042", "获取对用户的关注信息列表失败，limit（每页信息数目，最大为1000）不合法。limit = " + limit);
		}
		UserNode uNode = getUserNode(userId);
		List<Follows> follows = null;
		try {
			follows = userRepository().getFollowsToUser(uNode.getNodeId(), skipOrDefault, limitOrDefault);
		} catch (Exception e) {
			logger.error("获取对用户的关注信息列表失败：数据库操作异常。userId=" + userId, e);
			throw new XhrcRuntimeException("20021", "获取对用户的关注信息列表失败：数据库操作异常。userId=" + userId, e);
		}
		return follows;
	}

	@Override
	public long countFollowsToUser(Integer userId) throws XhrcException {
		long count = 0;
		UserNode node = getUserNode(userId);
		try {
			count = userRepository().countFollowsToUser(node.getNodeId());
		} catch (Exception e) {
			logger.error("统计关注数量失败：数据库操作异常。userId=" + userId, e);
			throw new XhrcException("20021", "统计关注数量失败：数据库操作异常。userId=" + userId, e);
		}
		return count;
	}
}

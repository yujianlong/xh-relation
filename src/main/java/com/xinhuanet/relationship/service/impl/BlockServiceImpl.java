package com.xinhuanet.relationship.service.impl;

import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.xinhuanet.relationship.common.constant.RelTypeName;
import com.xinhuanet.relationship.common.exception.XhrcException;
import com.xinhuanet.relationship.common.exception.XhrcRuntimeException;
import com.xinhuanet.relationship.entity.node.UserNode;
import com.xinhuanet.relationship.entity.relationship.Blocks;
import com.xinhuanet.relationship.service.BlockService;

@Service("blockService")
public class BlockServiceImpl extends BaseServiceImpl implements BlockService {
	private static Logger logger = LoggerFactory.getLogger(BlockServiceImpl.class);

	@Override
	public boolean deleteBlock(Integer startUserId, Integer endUserId) throws XhrcException {
		boolean bool = false;
		UserNode startNode = getUserNode(startUserId);
		UserNode endNode = getUserNode(endUserId);
		try {
			userRepository().deleteRelationshipBetween(startNode, endNode, RelTypeName.BLOCKS);
			bool = true;
		} catch (Exception e) {
			logger.error("删除屏蔽关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId, e);
			throw new XhrcException("20021", "删除屏蔽关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId,
					e);
		}
		return bool;
	}

	@Override
	public boolean existBlock(Integer startUserId, Integer endUserId) throws XhrcException {
		boolean bool = false;
		UserNode startNode = getUserNode(startUserId);
		UserNode endNode = getUserNode(endUserId);
		try {
			Blocks block = userRepository()
					.getRelationshipBetween(startNode, endNode, Blocks.class, RelTypeName.BLOCKS);
			if (block != null) {
				bool = true;
			}
		} catch (Exception e) {
			logger.error("判断是否存在屏蔽关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId, e);
			throw new XhrcRuntimeException("20021", "判断是否存在屏蔽关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId="
					+ endUserId, e);
		}

		return bool;
	}

	@Override
	public List<Integer> getUserBlocks(Integer userId) throws XhrcException {
		UserNode uNode = getUserNode(userId);
		Long nodeId = uNode.getNodeId();
		List<Integer> blocksId = null;
		try {
			blocksId = userRepository().getUserBlocks(nodeId);
		} catch (Exception e) {
			logger.error("获取用户屏蔽的用户id列表失败：数据库操作异常。userId=" + userId, e);
			throw new XhrcRuntimeException("20021", "获取用户屏蔽的用户id列表失败：数据库操作异常。userId=" + userId, e);
		}
		return blocksId;
	}

	@Override
	public long countUserBlocks(Integer userId) throws XhrcException {
		long count = 0;
		UserNode node = getUserNode(userId);
		try {
			count = userRepository().countUserBlocks(node.getNodeId());
		} catch (Exception e) {
			logger.error("获取用户屏蔽数量失败：数据库操作异常。userId=" + userId, e);
			throw new XhrcRuntimeException("20021", "获取用户屏蔽数量失败：数据库操作异常。userId=" + userId, e);
		}
		return count;
	}

	@Override
	public boolean createBlock(Integer startUserId, Integer endUserId) throws XhrcException {
		UserNode startNode = getUserNode(startUserId);
		UserNode endNode = getUserNode(endUserId);
		try {
			Blocks rel = userRepository().createRelationshipBetween(startNode, endNode, Blocks.class,
					RelTypeName.BLOCKS);
			rel.setCreatedAt(Calendar.getInstance().getTime());
			template().save(rel);
		} catch (Exception e) {
			logger.error("创建屏蔽关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId, e);
			throw new XhrcException("20021", "创建屏蔽关系失败：数据库操作异常。startUserId=" + startUserId + "；endUserId=" + endUserId,
					e);
		}
		return true;
	}
}

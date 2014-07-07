package com.xinhuanet.relationship.service.impl;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSON;
import com.xinhuanet.relationship.TestUtils;
import com.xinhuanet.relationship.common.constant.RelTypeName;
import com.xinhuanet.relationship.common.exception.XhrcException;
import com.xinhuanet.relationship.entity.node.UserNode;
import com.xinhuanet.relationship.entity.relationship.Blocks;
import com.xinhuanet.relationship.lang.Lang;
import com.xinhuanet.relationship.repository.UserRepository;

public class BlockServiceImplTest {

	private static Logger logger = LoggerFactory.getLogger(BlockServiceImplTest.class);

	private static BlockServiceImpl blockService;
	private static ClassPathXmlApplicationContext context;
	private static UserRepository userRepository;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// 获取baseService
		context = new ClassPathXmlApplicationContext("context-main.xml");
		blockService = context.getBean("blockService", BlockServiceImpl.class);
		userRepository = blockService.userRepository();

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		context.close();
	}

	@Test
	public void testDeleteBlockImpl() throws XhrcException {
		// 创建测试数据
		UserNode node1 = blockService.getUserNode(256);
		UserNode node2 = blockService.getUserNode(257);
		UserNode node3 = blockService.getUserNode(258);
		Blocks bk1 = userRepository.getRelationshipBetween(node1, node2, Blocks.class, RelTypeName.BLOCKS);
		Blocks bk2 = userRepository.getRelationshipBetween(node1, node3, Blocks.class, RelTypeName.BLOCKS);
		if (bk1 == null) {
			bk1 = userRepository.createRelationshipBetween(node1, node2, Blocks.class, RelTypeName.BLOCKS);
		}
		if (bk2 != null) {
			userRepository.deleteRelationshipBetween(node1, node3, RelTypeName.BLOCKS);
		}

		// 测试开始 1.存在屏蔽关系 请求删除 2.不存在屏蔽关系 请求删除
		boolean bool = false;
		bool = blockService.deleteBlock(node1.getUserId(), node2.getUserId());
		// 成功返回true
		assertTrue(bool);
		Blocks blk1 = userRepository.getRelationshipBetween(node1, node2, Blocks.class, RelTypeName.BLOCKS);
		// 不存在屏蔽关系
		assertNull(blk1);

		bool = blockService.deleteBlock(node1.getUserId(), node3.getUserId());
		// 成功返回true
		assertTrue(bool);
		Blocks blk2 = userRepository.getRelationshipBetween(node1, node3, Blocks.class, RelTypeName.BLOCKS);
		// 不存在屏蔽关系
		assertNull(blk2);
	}

	@Test
	public void testExistBlockImpl() throws XhrcException {
		// 创建测试数据
		UserNode node1 = blockService.getUserNode(256);
		UserNode node2 = blockService.getUserNode(257);
		UserNode node3 = blockService.getUserNode(258);
		Blocks bk1 = userRepository.getRelationshipBetween(node1, node2, Blocks.class, RelTypeName.BLOCKS);
		Blocks bk2 = userRepository.getRelationshipBetween(node1, node3, Blocks.class, RelTypeName.BLOCKS);
		if (bk1 == null) {
			bk1 = userRepository.createRelationshipBetween(node1, node2, Blocks.class, RelTypeName.BLOCKS);
		}
		if (bk2 != null) {
			userRepository.deleteRelationshipBetween(node1, node3, RelTypeName.BLOCKS);
		}

		// 测试开始 1.存在屏蔽关系 2.不存在屏蔽关系
		boolean bool = false;
		bool = blockService.existBlock(node1.getUserId(), node2.getUserId());
		// 成功返回true
		assertTrue(bool);
		Blocks blk1 = userRepository.getRelationshipBetween(node1, node2, Blocks.class, RelTypeName.BLOCKS);
		// 存在屏蔽关系
		assertNotNull(blk1);

		bool = blockService.existBlock(node1.getUserId(), node3.getUserId());
		// 不存在屏蔽 返回false
		assertFalse(bool);
		Blocks blk2 = userRepository.getRelationshipBetween(node1, node3, Blocks.class, RelTypeName.BLOCKS);
		// 不存在屏蔽关系
		assertNull(blk2);
	}

	@Test
	public void testGetUserBlocks() throws XhrcException {
		TestUtils.initUserNode(userRepository, 680, 681, 682, 683);
		UserNode node1 = userRepository.findByPropertyValue("userId", 680);
		UserNode node2 = userRepository.findByPropertyValue("userId", 681);

		UserNode node3 = userRepository.findByPropertyValue("userId", 682);
		UserNode node4 = userRepository.findByPropertyValue("userId", 683);
		// 判断若不存在关系，则创建关系，node1屏蔽node2和node4，node4 屏蔽node1，node2屏蔽node3
		Blocks block1 = userRepository.getRelationshipBetween(node1, node2, Blocks.class, RelTypeName.BLOCKS);
		if (block1 == null) {
			userRepository.createRelationshipBetween(node1, node2, Blocks.class, RelTypeName.BLOCKS);
		}
		Blocks block2 = userRepository.getRelationshipBetween(node1, node4, Blocks.class, RelTypeName.BLOCKS);
		if (block2 == null) {
			userRepository.createRelationshipBetween(node1, node4, Blocks.class, RelTypeName.BLOCKS);
			userRepository.createRelationshipBetween(node4, node1, Blocks.class, RelTypeName.BLOCKS);
		}
		Blocks block3 = userRepository.getRelationshipBetween(node2, node3, Blocks.class, RelTypeName.BLOCKS);
		if (block3 == null) {
			userRepository.createRelationshipBetween(node2, node3, Blocks.class, RelTypeName.BLOCKS);
		}
		boolean bool = false;
		List<Integer> resultList = null;
		resultList = blockService.getUserBlocks(680);
		assertEquals("获取屏蔽数量错误", resultList.size(), 2);
		bool = resultList.get(0) == 681 || resultList.get(0) == 683;
		assertTrue(bool);
		resultList = blockService.getUserBlocks(681);
		assertEquals("获取用户屏蔽错误", resultList.get(0).intValue(), 682);
	}

	@Test
	public void testCountUserBlocks() throws XhrcException {
		TestUtils.initUserNode(userRepository, 680, 681, 682, 683);
		UserNode node1 = userRepository.findByPropertyValue("userId", 680);
		UserNode node2 = userRepository.findByPropertyValue("userId", 681);

		UserNode node3 = userRepository.findByPropertyValue("userId", 682);
		UserNode node4 = userRepository.findByPropertyValue("userId", 683);
		// 判断若不存在关系，则创建关系，node1屏蔽node2和node4，node4 屏蔽node1，node2屏蔽node3
		Blocks block1 = userRepository.getRelationshipBetween(node1, node2, Blocks.class, RelTypeName.BLOCKS);
		if (block1 == null) {
			userRepository.createRelationshipBetween(node1, node2, Blocks.class, RelTypeName.BLOCKS);
		}
		Blocks block2 = userRepository.getRelationshipBetween(node1, node4, Blocks.class, RelTypeName.BLOCKS);
		if (block2 == null) {
			userRepository.createRelationshipBetween(node1, node4, Blocks.class, RelTypeName.BLOCKS);
			userRepository.createRelationshipBetween(node4, node1, Blocks.class, RelTypeName.BLOCKS);
		}
		Blocks block3 = userRepository.getRelationshipBetween(node2, node3, Blocks.class, RelTypeName.BLOCKS);
		if (block3 == null) {
			userRepository.createRelationshipBetween(node2, node3, Blocks.class, RelTypeName.BLOCKS);
		}
		assertEquals("获取屏蔽数量错误", blockService.countUserBlocks(680), 2);
		assertEquals("获取屏蔽数量错误", blockService.countUserBlocks(681), 1);
	}

	@Test
	public void testCreateBlock() throws XhrcException {
		// 验证是否存该节点
		UserNode node1 = blockService.userRepository().findByPropertyValue("userId", 147);
		UserNode node2 = blockService.userRepository().findByPropertyValue("userId", 148);
		// 若存在删除它们,存在关系的时候要删除关系
		if (node1 != null && node2 != null) {
			Blocks blocklist = userRepository.getRelationshipBetween(node1, node2, Blocks.class, RelTypeName.BLOCKS);
			if (blocklist != null) {
				blockService.template().delete(blocklist);
			}

		}
		if (node1 != null) {
			blockService.template().delete(node1);
		}
		if (node2 != null) {
			blockService.template().delete(node2);
		}
		// 创建两个用户节点 147.148
		UserNode yjl1 = new UserNode();
		yjl1.setUserId(147);
		yjl1.setCustomFriendGroups(Lang.list("奇闻趣事"));
		blockService.userRepository().save(yjl1);
		UserNode yjl2 = new UserNode();
		yjl2.setUserId(148);
		yjl2.setCustomFriendGroups(Lang.list("喜闻乐见"));
		blockService.userRepository().save(yjl2);
		// 校验是否成功创建
		logger.debug("~~~~~~~~~~UserNode:yjl1:" + JSON.toJSONString(yjl1));
		logger.debug("~~~~~~~~~~UserNode:yjl2:" + JSON.toJSONString(yjl2));

		// 从neo4j获取节点
		UserNode startNode = blockService.userRepository().findByPropertyValue("userId", 147);
		UserNode endNode = blockService.userRepository().findByPropertyValue("userId", 148);
		boolean sucess = blockService.createBlock(147, 148);
		assertTrue(sucess);
		Blocks blocklst = blockService.template().getRelationshipBetween(startNode, endNode, Blocks.class,
				RelTypeName.BLOCKS);
		assertNotNull(blocklst);
		logger.debug("~~~~~~~~~~Blocklist:" + JSON.toJSONString(blocklst));

	}

}

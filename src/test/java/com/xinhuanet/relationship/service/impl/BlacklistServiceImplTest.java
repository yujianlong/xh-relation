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
import com.xinhuanet.relationship.entity.relationship.Blacklists;
import com.xinhuanet.relationship.lang.Lang;
import com.xinhuanet.relationship.repository.UserRepository;

public class BlacklistServiceImplTest {

	private static Logger logger = LoggerFactory.getLogger(BlacklistServiceImplTest.class);

	private static BlacklistServiceImpl blacklistService;
	private static ClassPathXmlApplicationContext context;
	private static UserRepository userRepository;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// 获取baseService
		context = new ClassPathXmlApplicationContext("context-main.xml");
		blacklistService = context.getBean("blacklistService", BlacklistServiceImpl.class);
		userRepository = blacklistService.userRepository();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		context.close();
	}

	@Test
	public void testDeleteBlackImpl() throws XhrcException {
		// 创建测试数据
		UserNode node1 = blacklistService.getUserNode(256);
		UserNode node2 = blacklistService.getUserNode(257);
		UserNode node3 = blacklistService.getUserNode(258);
		Blacklists bk1 = userRepository.getRelationshipBetween(node1, node2, Blacklists.class, RelTypeName.BLACKLISTS);
		Blacklists bk2 = userRepository.getRelationshipBetween(node1, node3, Blacklists.class, RelTypeName.BLACKLISTS);
		if (bk1 == null) {
			bk1 = userRepository.createRelationshipBetween(node1, node2, Blacklists.class, RelTypeName.BLACKLISTS);
		}
		if (bk2 != null) {
			userRepository.deleteRelationshipBetween(node1, node3, RelTypeName.BLACKLISTS);
		}

		// 测试开始 1.存在黑名单中 请求删除 2.不在黑名单 请求删除
		boolean bool = false;
		bool = blacklistService.deleteBlacklist(node1.getUserId(), node2.getUserId());
		// 成功返回true
		assertTrue(bool);
		Blacklists blk1 = userRepository.getRelationshipBetween(node1, node2, Blacklists.class, RelTypeName.BLACKLISTS);
		// 不在黑名单中
		assertNull(blk1);

		bool = blacklistService.deleteBlacklist(node1.getUserId(), node3.getUserId());
		// 成功返回true
		assertTrue(bool);
		Blacklists blk2 = userRepository.getRelationshipBetween(node1, node3, Blacklists.class, RelTypeName.BLACKLISTS);
		// 不在黑名单中
		assertNull(blk2);
	}

	@Test
	public void testExistBlackImpl() throws XhrcException {
		// 创建测试数据
		UserNode node1 = blacklistService.getUserNode(256);
		UserNode node2 = blacklistService.getUserNode(257);
		UserNode node3 = blacklistService.getUserNode(258);
		Blacklists bk1 = userRepository.getRelationshipBetween(node1, node2, Blacklists.class, RelTypeName.BLACKLISTS);
		Blacklists bk2 = userRepository.getRelationshipBetween(node1, node3, Blacklists.class, RelTypeName.BLACKLISTS);
		if (bk1 == null) {
			bk1 = userRepository.createRelationshipBetween(node1, node2, Blacklists.class, RelTypeName.BLACKLISTS);
		}
		if (bk2 != null) {
			userRepository.deleteRelationshipBetween(node1, node3, RelTypeName.BLACKLISTS);
		}

		// 测试开始 1.存在黑名单中 2.不在黑名单中
		boolean bool = false;
		bool = blacklistService.existBlacklist(node1.getUserId(), node2.getUserId());
		// 在黑名单中返回true
		assertTrue(bool);
		Blacklists blk1 = userRepository.getRelationshipBetween(node1, node2, Blacklists.class, RelTypeName.BLACKLISTS);
		// 在黑名单中
		assertNotNull(blk1);

		bool = blacklistService.existBlacklist(node1.getUserId(), node3.getUserId());
		// 不在黑名单 返回false
		assertFalse(bool);
		Blacklists blk2 = userRepository.getRelationshipBetween(node1, node3, Blacklists.class, RelTypeName.BLACKLISTS);
		// 不在黑名单中
		assertNull(blk2);
	}

	@Test
	public void testGetUserBlacklists() throws XhrcException {
		TestUtils.initUserNode(userRepository, 685, 686, 687, 688);
		UserNode node1 = userRepository.findByPropertyValue("userId", 685);
		UserNode node2 = userRepository.findByPropertyValue("userId", 686);

		UserNode node3 = userRepository.findByPropertyValue("userId", 687);
		UserNode node4 = userRepository.findByPropertyValue("userId", 688);
		// 判断若不存在关系，则创建关系，node1阻止node2和node4，node4 阻止node1，node2阻止node3
		Blacklists inBlacklist1 = userRepository.getRelationshipBetween(node1, node2, Blacklists.class,
				RelTypeName.BLACKLISTS);
		if (inBlacklist1 == null) {
			userRepository.createRelationshipBetween(node1, node2, Blacklists.class, RelTypeName.BLACKLISTS);
		}
		Blacklists inBlacklist2 = userRepository.getRelationshipBetween(node1, node4, Blacklists.class,
				RelTypeName.BLACKLISTS);
		if (inBlacklist2 == null) {
			userRepository.createRelationshipBetween(node1, node4, Blacklists.class, RelTypeName.BLACKLISTS);
			userRepository.createRelationshipBetween(node4, node1, Blacklists.class, RelTypeName.BLACKLISTS);
		}
		Blacklists inBlacklist3 = userRepository.getRelationshipBetween(node2, node3, Blacklists.class,
				RelTypeName.BLACKLISTS);
		if (inBlacklist3 == null) {
			userRepository.createRelationshipBetween(node2, node3, Blacklists.class, RelTypeName.BLACKLISTS);
		}
		boolean bool = false;
		List<Integer> resultList = null;
		resultList = blacklistService.getUserBlacklists(685);
		assertEquals("获取黑名单数量错误", resultList.size(), 2);
		bool = resultList.get(0) == 686 || resultList.get(0) == 688;
		assertTrue(bool);
		resultList = blacklistService.getUserBlacklists(686);
		assertEquals("获取黑名单用户错误", resultList.get(0).intValue(), 687);
	}

	@Test
	public void testCountUserBlacklists() throws XhrcException {
		TestUtils.initUserNode(userRepository, 685, 686, 687, 688);
		UserNode node1 = userRepository.findByPropertyValue("userId", 685);
		UserNode node2 = userRepository.findByPropertyValue("userId", 686);

		UserNode node3 = userRepository.findByPropertyValue("userId", 687);
		UserNode node4 = userRepository.findByPropertyValue("userId", 688);
		// 判断若不存在关系，则创建关系，node1阻止node2和node4，node4 阻止node1，node2阻止node3
		Blacklists inBlacklist1 = userRepository.getRelationshipBetween(node1, node2, Blacklists.class,
				RelTypeName.BLACKLISTS);
		if (inBlacklist1 == null) {
			userRepository.createRelationshipBetween(node1, node2, Blacklists.class, RelTypeName.BLACKLISTS);
		}
		Blacklists inBlacklist2 = userRepository.getRelationshipBetween(node1, node4, Blacklists.class,
				RelTypeName.BLACKLISTS);
		if (inBlacklist2 == null) {
			userRepository.createRelationshipBetween(node1, node4, Blacklists.class, RelTypeName.BLACKLISTS);
			userRepository.createRelationshipBetween(node4, node1, Blacklists.class, RelTypeName.BLACKLISTS);
		}
		Blacklists inBlacklist3 = userRepository.getRelationshipBetween(node2, node3, Blacklists.class,
				RelTypeName.BLACKLISTS);
		if (inBlacklist3 == null) {
			userRepository.createRelationshipBetween(node2, node3, Blacklists.class, RelTypeName.BLACKLISTS);
		}

		assertEquals("获取黑名单数量错误", blacklistService.countUserBlacklists(685), 2);
		assertEquals("获取黑名单数量错误", blacklistService.countUserBlacklists(686), 1);

	}

	@Test
	public void testCreateBlack() throws XhrcException {
		// 验证是否存该节点
		UserNode node1 = blacklistService.userRepository().findByPropertyValue("userId", 145);
		UserNode node2 = blacklistService.userRepository().findByPropertyValue("userId", 146);
		// 若存在删除它们,存在关系的时候要删除关系
		if (node1 != null && node2 != null) {
			Blacklists inBlacklist = userRepository.getRelationshipBetween(node1, node2, Blacklists.class,
					RelTypeName.BLACKLISTS);
			if (inBlacklist != null) {
				blacklistService.template().delete(inBlacklist);
			}

		}
		if (node1 != null) {
			blacklistService.template().delete(node1);
		}
		if (node2 != null) {
			blacklistService.template().delete(node2);
		}
		// 创建两个用户节点 145.146
		UserNode yjl1 = new UserNode();
		yjl1.setUserId(145);
		yjl1.setCustomFriendGroups(Lang.list("奇闻趣事"));
		blacklistService.userRepository().save(yjl1);
		UserNode yjl2 = new UserNode();
		yjl2.setUserId(146);
		yjl2.setCustomFriendGroups(Lang.list("喜闻乐见"));
		blacklistService.userRepository().save(yjl2);
		// 校验是否成功创建
		logger.debug("~~~~~~~~~~UserNode:yjl1:" + JSON.toJSONString(yjl1));
		logger.debug("~~~~~~~~~~UserNode:yjl2:" + JSON.toJSONString(yjl2));
		// 从neo4j获取节点
		UserNode startNode = blacklistService.userRepository().findByPropertyValue("userId", 145);
		UserNode endNode = blacklistService.userRepository().findByPropertyValue("userId", 146);
		boolean sucess = blacklistService.createBlackList(145, 146);
		assertTrue(sucess);
		Blacklists iblst = blacklistService.template().getRelationshipBetween(startNode, endNode, Blacklists.class,
				RelTypeName.BLACKLISTS);
		assertNotNull(iblst);
		logger.debug("~~~~~~~~~~InBlacklist:" + JSON.toJSONString(iblst));

	}

}

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
import com.xinhuanet.relationship.entity.relationship.Obstructs;
import com.xinhuanet.relationship.lang.Lang;
import com.xinhuanet.relationship.repository.UserRepository;

public class ObstructServiceImplTest {

	private static Logger logger = LoggerFactory.getLogger(ObstructServiceImpl.class);

	private static ObstructServiceImpl obstructService;
	private static ClassPathXmlApplicationContext context;
	private static UserRepository userRepository;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// 获取baseService
		context = new ClassPathXmlApplicationContext("context-main.xml");
		obstructService = context.getBean("obstructService", ObstructServiceImpl.class);
		userRepository = obstructService.userRepository();

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		context.close();
	}

	@Test
	public void testCreateObstruct() throws XhrcException {
		// 验证是否存该节点
		UserNode node1 = obstructService.userRepository().findByPropertyValue("userId", 201);
		UserNode node2 = obstructService.userRepository().findByPropertyValue("userId", 202);
		// 若存在删除它们,存在关系的时候要删除关系
		if (node1 != null && node2 != null) {
			Obstructs ob = userRepository.getRelationshipBetween(node1, node2, Obstructs.class, RelTypeName.OBSTRUCTS);
			if (ob != null) {
				obstructService.template().delete(ob);
			}

		}
		if (node1 != null) {
			obstructService.template().delete(node1);
		}
		if (node2 != null) {
			obstructService.template().delete(node2);
		}
		// 创建两个用户节点 201 202
		UserNode yjl1 = new UserNode();
		yjl1.setUserId(201);
		yjl1.setCustomFriendGroups(Lang.list("奇闻趣事"));
		obstructService.userRepository().save(yjl1);
		UserNode yjl2 = new UserNode();
		yjl2.setUserId(202);
		yjl2.setCustomFriendGroups(Lang.list("喜闻乐见"));
		obstructService.userRepository().save(yjl2);
		// 校验是否成功创建
		logger.debug("~~~~~~~~~~UserNode:yjl1:" + JSON.toJSONString(yjl1));
		logger.debug("~~~~~~~~~~UserNode:yjl2:" + JSON.toJSONString(yjl2));

		// 从neo4j获取节点
		UserNode startNode = obstructService.userRepository().findByPropertyValue("userId", 201);
		UserNode endNode = obstructService.userRepository().findByPropertyValue("userId", 202);
		boolean sucess = obstructService.createObstruct(201, 202);
		assertTrue(sucess);
		Obstructs obs = obstructService.template().getRelationshipBetween(startNode, endNode, Obstructs.class,
				RelTypeName.OBSTRUCTS);
		assertNotNull(obs);
		logger.debug("~~~~~~~~~~Blocklist:" + JSON.toJSONString(obs));

	}

	@Test
	public void testDeleteObstructImpl() throws XhrcException {
		// 创建测试数据
		UserNode node1 = obstructService.getUserNode(256);
		UserNode node2 = obstructService.getUserNode(257);
		UserNode node3 = obstructService.getUserNode(258);
		Obstructs bk1 = userRepository.getRelationshipBetween(node1, node2, Obstructs.class, RelTypeName.OBSTRUCTS);
		Obstructs bk2 = userRepository.getRelationshipBetween(node1, node3, Obstructs.class, RelTypeName.OBSTRUCTS);
		if (bk1 == null) {
			bk1 = userRepository.createRelationshipBetween(node1, node2, Obstructs.class, RelTypeName.OBSTRUCTS);
		}
		if (bk2 != null) {
			userRepository.deleteRelationshipBetween(node1, node3, RelTypeName.OBSTRUCTS);
		}

		// 测试开始 1.存在阻止 请求删除 2.不存在阻止 请求删除
		boolean bool = false;
		bool = obstructService.deleteObstruct(node1.getUserId(), node2.getUserId());
		// 成功返回true
		assertTrue(bool);
		Obstructs blk1 = userRepository.getRelationshipBetween(node1, node2, Obstructs.class, RelTypeName.OBSTRUCTS);
		// 不存在阻止
		assertNull(blk1);

		bool = obstructService.deleteObstruct(node1.getUserId(), node3.getUserId());
		// 成功返回true
		assertTrue(bool);
		Obstructs blk2 = userRepository.getRelationshipBetween(node1, node3, Obstructs.class, RelTypeName.OBSTRUCTS);
		// 不存在阻止
		assertNull(blk2);
	}

	@Test
	public void testExistObstructImpl() throws XhrcException {
		// 创建测试数据
		UserNode node1 = obstructService.getUserNode(256);
		UserNode node2 = obstructService.getUserNode(257);
		UserNode node3 = obstructService.getUserNode(258);
		Obstructs bk1 = userRepository.getRelationshipBetween(node1, node2, Obstructs.class, RelTypeName.OBSTRUCTS);
		Obstructs bk2 = userRepository.getRelationshipBetween(node1, node3, Obstructs.class, RelTypeName.OBSTRUCTS);
		if (bk1 == null) {
			bk1 = userRepository.createRelationshipBetween(node1, node2, Obstructs.class, RelTypeName.OBSTRUCTS);
		}
		if (bk2 != null) {
			userRepository.deleteRelationshipBetween(node1, node3, RelTypeName.OBSTRUCTS);
		}

		// 测试开始 1.存在阻止 2.不存在阻止
		boolean bool = false;
		bool = obstructService.existObstruct(node1.getUserId(), node2.getUserId());
		// 存在阻止 返回true
		assertTrue(bool);
		Obstructs blk1 = userRepository.getRelationshipBetween(node1, node2, Obstructs.class, RelTypeName.OBSTRUCTS);
		// 存在阻止
		assertNotNull(blk1);

		bool = obstructService.existObstruct(node1.getUserId(), node3.getUserId());
		// 不存在阻止 返回false
		assertFalse(bool);
		Obstructs blk2 = userRepository.getRelationshipBetween(node1, node3, Obstructs.class, RelTypeName.OBSTRUCTS);
		// 不存在阻止
		assertNull(blk2);
	}

	@Test
	public void testGetUserObstructs() throws XhrcException {
		UserNode node1 = userRepository.findByPropertyValue("userId", 600);
		UserNode node2 = userRepository.findByPropertyValue("userId", 601);

		UserNode node3 = userRepository.findByPropertyValue("userId", 602);
		UserNode node4 = userRepository.findByPropertyValue("userId", 603);
		if (node1 != null) {
			obstructService.template().delete(node1);
		}
		if (node2 != null) {
			obstructService.template().delete(node2);
		}
		if (node3 != null) {
			obstructService.template().delete(node3);
		}
		if (node4 != null) {
			obstructService.template().delete(node4);
		}
		TestUtils.initUserNode(userRepository, 600, 601, 602, 603);
		node1 = userRepository.findByPropertyValue("userId", 600);
		node2 = userRepository.findByPropertyValue("userId", 601);

		node3 = userRepository.findByPropertyValue("userId", 602);
		node4 = userRepository.findByPropertyValue("userId", 603);
		// 判断若不存在关系，则创建关系，node1阻止node2和node4，node3 阻止node1，node2阻止node3
		Obstructs obstruct1 = userRepository.getRelationshipBetween(node1, node2, Obstructs.class,
				RelTypeName.OBSTRUCTS);
		if (obstruct1 == null) {
			userRepository.createRelationshipBetween(node1, node2, Obstructs.class, RelTypeName.OBSTRUCTS);
		}
		Obstructs obstruct2 = userRepository.getRelationshipBetween(node1, node4, Obstructs.class,
				RelTypeName.OBSTRUCTS);
		if (obstruct2 == null) {
			userRepository.createRelationshipBetween(node1, node4, Obstructs.class, RelTypeName.OBSTRUCTS);
		}
		Obstructs obstruct3 = userRepository.getRelationshipBetween(node3, node1, Obstructs.class,
				RelTypeName.OBSTRUCTS);
		if (obstruct3 == null) {
			userRepository.createRelationshipBetween(node3, node1, Obstructs.class, RelTypeName.OBSTRUCTS);
		}
		Obstructs obstruct4 = userRepository.getRelationshipBetween(node2, node3, Obstructs.class,
				RelTypeName.OBSTRUCTS);
		if (obstruct4 == null) {
			userRepository.createRelationshipBetween(node2, node3, Obstructs.class, RelTypeName.OBSTRUCTS);
		}
		boolean bool = false;
		List<Integer> resultList = null;
		resultList = obstructService.getUserObstructs(600);
		assertEquals("获取阻止数量错误", resultList.size(), 2);
		bool = resultList.get(0) == 601 || resultList.get(0) == 603;
		assertTrue(bool);
		resultList = obstructService.getUserObstructs(601);
		assertEquals("获取用户阻止错误", resultList.get(0).intValue(), 602);
	}

	@Test
	public void testCountUserObstructs() throws XhrcException {
		TestUtils.initUserNode(userRepository, 605, 606, 607, 608);
		UserNode node1 = userRepository.findByPropertyValue("userId", 605);
		UserNode node2 = userRepository.findByPropertyValue("userId", 606);

		UserNode node3 = userRepository.findByPropertyValue("userId", 607);
		UserNode node4 = userRepository.findByPropertyValue("userId", 608);
		// 判断若不存在关系，则创建关系，node1阻止node2和node4，node3 阻止node1，node2阻止node3
		Obstructs obstruct1 = userRepository.getRelationshipBetween(node1, node2, Obstructs.class,
				RelTypeName.OBSTRUCTS);
		if (obstruct1 == null) {
			userRepository.createRelationshipBetween(node1, node2, Obstructs.class, RelTypeName.OBSTRUCTS);
		}
		Obstructs obstruct2 = userRepository.getRelationshipBetween(node1, node4, Obstructs.class,
				RelTypeName.OBSTRUCTS);
		if (obstruct2 == null) {
			userRepository.createRelationshipBetween(node1, node4, Obstructs.class, RelTypeName.OBSTRUCTS);
		}
		Obstructs obstruct3 = userRepository.getRelationshipBetween(node3, node1, Obstructs.class,
				RelTypeName.OBSTRUCTS);
		if (obstruct3 == null) {
			userRepository.createRelationshipBetween(node3, node1, Obstructs.class, RelTypeName.OBSTRUCTS);
		}
		Obstructs obstruct4 = userRepository.getRelationshipBetween(node2, node3, Obstructs.class,
				RelTypeName.OBSTRUCTS);
		if (obstruct4 == null) {
			userRepository.createRelationshipBetween(node2, node3, Obstructs.class, RelTypeName.OBSTRUCTS);
		}
		assertEquals("获取阻止数量错误", obstructService.countUserObstructs(605), 2);
		assertEquals("获取阻止数量错误", obstructService.countUserObstructs(606), 1);
	}
}

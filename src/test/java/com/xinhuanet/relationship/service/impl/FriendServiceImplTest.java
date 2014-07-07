package com.xinhuanet.relationship.service.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.neo4j.template.Neo4jOperations;

import com.alibaba.fastjson.JSON;
import com.xinhuanet.relationship.TestUtils;
import com.xinhuanet.relationship.common.constant.RelTypeName;
import com.xinhuanet.relationship.common.exception.XhrcException;
import com.xinhuanet.relationship.common.exception.XhrcRuntimeException;
import com.xinhuanet.relationship.entity.node.UserNode;
import com.xinhuanet.relationship.entity.relationship.Friend;
import com.xinhuanet.relationship.lang.Lang;
import com.xinhuanet.relationship.repository.UserRepository;

public class FriendServiceImplTest {

	private static Logger logger = LoggerFactory.getLogger(FriendServiceImplTest.class);

	private static FriendServiceImpl friendService;
	private static ClassPathXmlApplicationContext context;
	private static UserRepository userRepository;
	private static Neo4jOperations template;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// 获取baseService
		context = new ClassPathXmlApplicationContext("context-main.xml");
		friendService = context.getBean("friendService", FriendServiceImpl.class);
		userRepository = friendService.userRepository();
		template = friendService.template();
		// 准备数据
		TestUtils.initUserNode(userRepository, 105, 106, 255, 256, 257, 258);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		context.close();
	}

	@Test
	public void testDeleteFriendBetween() throws XhrcException {
		// 查找4个节点 255，256 ，257 ， 258 不存在则创建
		UserNode node1 = friendService.getUserNode(255);
		UserNode node2 = friendService.getUserNode(256);
		UserNode node3 = friendService.getUserNode(257);
		UserNode node4 = friendService.getUserNode(258);

		// 若存在关系的时候要删除关系
		if (node1 != null && node2 != null) {
			Friend friend1 = friendService.template().getRelationshipBetween(node1, node2, Friend.class,
					RelTypeName.FRIEND);
			if (friend1 != null) {
				friendService.template().delete(friend1);
			}
			Friend friend2 = friendService.template().getRelationshipBetween(node2, node1, Friend.class,
					RelTypeName.FRIEND);
			if (friend2 != null) {
				friendService.template().delete(friend2);
			}
		}
		if (node1 == null) {
			UserNode user1 = new UserNode();
			user1.setUserId(255);
			user1.setCustomFriendGroups(Lang.list("煮酒论史"));
			node1 = friendService.userRepository().save(user1);
		}
		if (node2 == null) {
			UserNode user2 = new UserNode();
			user2.setUserId(256);
			user2.setCustomFriendGroups(Lang.list("煮酒论史"));
			node2 = friendService.userRepository().save(user2);
		}

		// 若存在删除它们,存在关系的时候要删除关系
		if (node3 != null && node4 != null) {
			Friend friend = friendService.template().getRelationshipBetween(node3, node4, Friend.class,
					RelTypeName.FRIEND);
			if (friend != null) {
				friendService.template().delete(friend);
			}
		}
		if (node3 == null) {
			UserNode user3 = new UserNode();
			user3.setUserId(257);
			user3.setCustomFriendGroups(Lang.list("闲闲书话"));
			node3 = friendService.userRepository().save(user3);
		}
		if (node4 == null) {
			UserNode user4 = new UserNode();
			user4.setUserId(258);
			user4.setCustomFriendGroups(Lang.list("闲闲书话"));
			node4 = friendService.userRepository().save(user4);
		}

		// 成功为 true 不成功为false
		boolean bool = false;
		// 创建好友关系
		userRepository.createRelationshipBetween(node1, node2, Friend.class, RelTypeName.FRIEND);
		userRepository.createRelationshipBetween(node2, node1, Friend.class, RelTypeName.FRIEND);
		bool = friendService.deleteFriendBetween(node1.getUserId().intValue(), node2.getUserId().intValue());
		// 验证是否删除成功 (存在关系)
		assertTrue(bool);
		Friend friend1 = friendService.template()
				.getRelationshipBetween(node1, node2, Friend.class, RelTypeName.FRIEND);
		Friend friend2 = friendService.template()
				.getRelationshipBetween(node2, node1, Friend.class, RelTypeName.FRIEND);
		// 验证好友关系删除是否成功
		assertNull(friend1);
		assertNull(friend2);

		bool = friendService.deleteFriendBetween(node3.getUserId().intValue(), node4.getUserId().intValue());
		// 验证是否删除成功 （不存在关系）
		assertTrue(bool);
		Friend friend3 = friendService.template()
				.getRelationshipBetween(node3, node4, Friend.class, RelTypeName.FRIEND);
		Friend friend4 = friendService.template()
				.getRelationshipBetween(node4, node3, Friend.class, RelTypeName.FRIEND);
		assertNull(friend3);
		assertNull(friend4);
	}

	@Test
	public void testExistFriendBetween() throws XhrcException {
		// 查找4个节点 255，256 ，257 ， 258 不存在则创建
		UserNode node1 = friendService.getUserNode(255);
		UserNode node2 = friendService.getUserNode(256);
		UserNode node3 = friendService.getUserNode(257);
		UserNode node4 = friendService.getUserNode(258);

		// 若存在关系的时候要删除关系
		if (node1 != null && node2 != null) {
			Friend friend1 = friendService.template().getRelationshipBetween(node1, node2, Friend.class,
					RelTypeName.FRIEND);
			if (friend1 != null) {
				friendService.template().delete(friend1);
			}
			Friend friend2 = friendService.template().getRelationshipBetween(node2, node1, Friend.class,
					RelTypeName.FRIEND);
			if (friend2 != null) {
				friendService.template().delete(friend2);
			}
		}
		if (node1 == null) {
			UserNode user1 = new UserNode();
			user1.setUserId(255);
			user1.setCustomFriendGroups(Lang.list("煮酒论史"));
			node1 = friendService.userRepository().save(user1);
		}
		if (node2 == null) {
			UserNode user2 = new UserNode();
			user2.setUserId(256);
			user2.setCustomFriendGroups(Lang.list("煮酒论史"));
			node2 = friendService.userRepository().save(user2);
		}

		// 若存在删除它们,存在关系的时候要删除关系
		if (node3 != null && node4 != null) {
			Friend friend = friendService.template().getRelationshipBetween(node3, node4, Friend.class,
					RelTypeName.FRIEND);
			if (friend != null) {
				friendService.template().delete(friend);
			}
		}
		if (node3 == null) {
			UserNode user3 = new UserNode();
			user3.setUserId(257);
			user3.setCustomFriendGroups(Lang.list("闲闲书话"));
			node3 = friendService.userRepository().save(user3);
		}
		if (node4 == null) {
			UserNode user4 = new UserNode();
			user4.setUserId(258);
			user4.setCustomFriendGroups(Lang.list("闲闲书话"));
			node4 = friendService.userRepository().save(user4);
		}

		boolean bool = false;

		bool = friendService.existFriendBetween(node3.getUserId().intValue(), node4.getUserId().intValue());
		// 不存在为false
		assertFalse(bool);

		userRepository.createRelationshipBetween(node1, node3, Friend.class, RelTypeName.FRIEND);
		bool = friendService.existFriendBetween(node1.getUserId().intValue(), node3.getUserId().intValue());
		// 存在单向关系返回false
		assertFalse(bool);

		userRepository.createRelationshipBetween(node1, node2, Friend.class, RelTypeName.FRIEND);
		userRepository.createRelationshipBetween(node2, node1, Friend.class, RelTypeName.FRIEND);
		bool = friendService.existFriendBetween(node1.getUserId().intValue(), node2.getUserId().intValue());
		// 存在为true
		assertTrue(bool);
	}

	@Test
	public void testExistFriend() throws XhrcException {
		// 验证是否存该节点
		UserNode node1 = friendService.userRepository().findByPropertyValue("userId", 105);
		UserNode node2 = friendService.userRepository().findByPropertyValue("userId", 106);
		// 若存在删除它们,存在关系的时候要删除关系
		if (node1 != null && node2 != null) {
			Friend friend = friendService.template().getRelationshipBetween(node1, node2, Friend.class,
					RelTypeName.FRIEND);
			if (friend != null) {
				friendService.template().delete(friend);
			}
		}
		if (node1 != null) {
			friendService.template().delete(node1);
		}
		if (node2 != null) {
			friendService.template().delete(node2);
		}
		// 创建两个用户节点 105.106
		UserNode yjl1 = new UserNode();
		yjl1.setUserId(105);
		yjl1.setCustomFriendGroups(Lang.list("奇闻趣事"));
		friendService.userRepository().save(yjl1);
		UserNode yjl2 = new UserNode();
		yjl2.setUserId(106);
		yjl2.setCustomFriendGroups(Lang.list("喜闻乐见"));
		friendService.userRepository().save(yjl2);
		// 校验是否成功创建
		logger.debug("~~~~~~~~~~UserNode:yjl1:" + JSON.toJSONString(yjl1));
		logger.debug("~~~~~~~~~~UserNode:yjl2:" + JSON.toJSONString(yjl2));
		// 从neo4j获取节点
		UserNode startNode = friendService.userRepository().findByPropertyValue("userId", 105);
		UserNode endNode = friendService.userRepository().findByPropertyValue("userId", 106);
		// 创建两个节点之间的好友关系
		boolean sucess = friendService.createFriend(105, 106, "备注姓名", "备注1", false, Lang.list("分组1"))
				&& friendService.createFriend(106, 105, "备注姓名", "备注1", false, Lang.list("分组1"));
		// 验证是否创建成功
		assertTrue(sucess);
		// 获取两个节点之间的好友关系
		Friend ifrnd = friendService.template().getRelationshipBetween(startNode, endNode, Friend.class,
				RelTypeName.FRIEND);
		// 验证不空
		assertNotNull(ifrnd);
		// 打印json
		logger.debug("~~~~~~~~~~Isfriend:" + JSON.toJSONString(ifrnd));
		assertTrue(friendService.existFriendBetween(105, 106));
	}

	@Test
	public void testCountUserFriend() throws XhrcException {
		long count = 0;

		UserNode startUser = userRepository.findByPropertyValue("userId", 290);
		if (startUser == null) {
			// 创建用户 加好友30个
			startUser = new UserNode();
			startUser.setUserId(290);
			// startUser.setFriendGroups("好友");
			startUser = friendService.userRepository().save(startUser);

			for (int i = 291; i < 321; i++) {
				UserNode user = new UserNode();
				user.setUserId(i);
				// user.setFriendGroups("好友");
				user = friendService.userRepository().save(user);
				userRepository.createRelationshipBetween(startUser, user, Friend.class, RelTypeName.FRIEND);
				userRepository.createRelationshipBetween(user, startUser, Friend.class, RelTypeName.FRIEND);
			}
		}
		count = friendService.countUserFriend(startUser.getUserId());
		assertEquals("好友数量计算错误", 30, count);
		logger.debug("############用户  -- 好友数量:" + startUser.getUserId() + " -- " + count);
	}

	@Test
	public void testCreateFriend() throws XhrcException {
		// 验证是否存该节点
		UserNode node1 = friendService.userRepository().findByPropertyValue("userId", 105);
		UserNode node2 = friendService.userRepository().findByPropertyValue("userId", 106);
		// 若存在删除它们,存在关系的时候要删除关系
		if (node1 != null && node2 != null) {
			Friend friend = friendService.template().getRelationshipBetween(node1, node2, Friend.class,
					RelTypeName.FRIEND);
			if (friend != null) {
				friendService.template().delete(friend);
			}
		}
		if (node1 != null) {
			friendService.template().delete(node1);
		}
		if (node2 != null) {
			friendService.template().delete(node2);
		}
		// 创建两个用户节点 105.106
		UserNode yjl1 = new UserNode();
		yjl1.setUserId(105);
		yjl1.setCustomFriendGroups(Lang.list("奇闻趣事"));
		friendService.userRepository().save(yjl1);
		UserNode yjl2 = new UserNode();
		yjl2.setUserId(106);
		yjl2.setCustomFriendGroups(Lang.list("喜闻乐见"));
		friendService.userRepository().save(yjl2);
		// 校验是否成功创建
		logger.debug("~~~~~~~~~~UserNode:yjl1:" + JSON.toJSONString(yjl1));
		logger.debug("~~~~~~~~~~UserNode:yjl2:" + JSON.toJSONString(yjl2));
		// 从neo4j获取节点
		UserNode startNode = friendService.userRepository().findByPropertyValue("userId", 105);
		UserNode endNode = friendService.userRepository().findByPropertyValue("userId", 106);
		// 创建两个节点之间的好友关系
		boolean sucess = friendService.createFriend(105, 106, "备注姓名", "备注1", false, Lang.list("分组1"));
		// 验证是否创建成功
		assertTrue(sucess);
		// 获取两个节点之间的好友关系
		Friend ifrnd = friendService.template().getRelationshipBetween(startNode, endNode, Friend.class,
				RelTypeName.FRIEND);
		// 验证不空
		assertNotNull(ifrnd);
		// 打印json
		logger.debug("~~~~~~~~~~Isfriend:" + JSON.toJSONString(ifrnd));
	}

	@Test
	public void testCreateFriendValidateLength() throws XhrcException {
		// 验证是否存该节点
		UserNode node1 = friendService.userRepository().findByPropertyValue("userId", 105);
		UserNode node2 = friendService.userRepository().findByPropertyValue("userId", 106);

		if (node1 != null) {
			friendService.template().delete(node1);
		}
		if (node2 != null) {
			friendService.template().delete(node2);
		}
		// 创建两个用户节点 105.106
		UserNode yjl1 = new UserNode();
		yjl1.setUserId(105);
		yjl1.setCustomFriendGroups(Lang.list("奇闻趣事"));
		friendService.userRepository().save(yjl1);
		UserNode yjl2 = new UserNode();
		yjl2.setUserId(106);
		yjl2.setCustomFriendGroups(Lang.list("喜闻乐见"));
		friendService.userRepository().save(yjl2);
		// 校验是否成功创建
		logger.debug("~~~~~~~~~~UserNode:yjl1:" + JSON.toJSONString(yjl1));
		logger.debug("~~~~~~~~~~UserNode:yjl2:" + JSON.toJSONString(yjl2));

		// 从neo4j获取节点
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 502; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		Throwable t = null;
		try {
			friendService.createFriend(105, 106, "备注姓名", sb.toString(), false, Lang.list("分组1"));
		} catch (Exception ex) {
			t = ex;
		} finally {
			assertNotNull(t);
			assertTrue(t instanceof XhrcRuntimeException);
			logger.debug("~~~~~~~~~~" + t.getMessage());
			assertTrue(t.getMessage().contains("设置好友备注失败，remark长度不合法。"));
		}

	}

	@Test
	public void testCreateFriendValidateGroupLength() throws XhrcException {
		// 验证是否存该节点
		UserNode node1 = friendService.userRepository().findByPropertyValue("userId", 105);
		UserNode node2 = friendService.userRepository().findByPropertyValue("userId", 106);

		if (node1 != null) {
			friendService.template().delete(node1);
		}
		if (node2 != null) {
			friendService.template().delete(node2);
		}
		// 创建两个用户节点 105.106
		UserNode yjl1 = new UserNode();
		yjl1.setUserId(105);
		yjl1.setCustomFriendGroups(Lang.list("奇闻趣事"));
		friendService.userRepository().save(yjl1);
		UserNode yjl2 = new UserNode();
		yjl2.setUserId(106);
		yjl2.setCustomFriendGroups(Lang.list("喜闻乐见"));
		friendService.userRepository().save(yjl2);
		// 校验是否成功创建
		logger.debug("~~~~~~~~~~UserNode:yjl1:" + JSON.toJSONString(yjl1));
		logger.debug("~~~~~~~~~~UserNode:yjl2:" + JSON.toJSONString(yjl2));

		// 从neo4j获取节点
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 102; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		List<String> fgroup = new ArrayList<String>();
		fgroup.add(sb.toString());
		Throwable t = null;
		try {
			friendService.createFriend(105, 106, "备注姓名", "beizhu", false, fgroup);
		} catch (Exception ex) {
			t = ex;
		} finally {
			assertNotNull(t);
			assertTrue(t instanceof XhrcRuntimeException);
			logger.debug("~~~~~~~~~~" + t.getMessage());
			assertTrue(t.getMessage().contains("设置好友分组失败，friendGroup长度不合法。"));
		}

	}

	@Test
	public void testCreateFriendValidateRemarNameLength() throws XhrcException {

		// 从neo4j获取节点
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 22; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		Throwable t = null;
		try {
			friendService.createFriend(105, 106, sb.toString(), "备注", false, Lang.list("分组1"));
		} catch (Exception ex) {
			t = ex;
		} finally {
			assertNotNull(t);
			assertTrue(t instanceof XhrcRuntimeException);
			logger.debug("~~~~~~~~~~" + t.getMessage());
			assertTrue(t.getMessage().contains("设置好友备注姓名失败，remarkName长度不合法。"));
		}

	}

	@Test
	public void testUpdateFriend() throws XhrcException {
		// 验证是否存该节点
		UserNode node1 = friendService.userRepository().findByPropertyValue("userId", 107);
		UserNode node2 = friendService.userRepository().findByPropertyValue("userId", 108);
		// 若存在删除它们,存在关系的时候要删除关系
		if (node1 != null && node2 != null) {
			Friend friend = friendService.template().getRelationshipBetween(node1, node2, Friend.class,
					RelTypeName.FRIEND);
			if (friend != null) {
				friendService.template().delete(friend);
			}
		}
		if (node1 != null) {
			friendService.template().delete(node1);
		}
		if (node2 != null) {
			friendService.template().delete(node2);
		}
		// 创建两个用户节点 107.108
		UserNode yjl1 = new UserNode();
		yjl1.setUserId(107);
		yjl1.setCustomFriendGroups(Lang.list("天下杂谈"));
		friendService.userRepository().save(yjl1);
		UserNode yjl2 = new UserNode();
		yjl2.setUserId(108);
		yjl2.setCustomFriendGroups(Lang.list("商业论坛"));
		friendService.userRepository().save(yjl2);
		// 校验是否成功创建
		logger.debug("~~~~~~~~~~UserNode:yjl1:" + JSON.toJSONString(yjl1));
		logger.debug("~~~~~~~~~~UserNode:yjl2:" + JSON.toJSONString(yjl2));
		// 从neo4j获取节点
		UserNode startNode = friendService.userRepository().findByPropertyValue("userId", 107);
		UserNode endNode = friendService.userRepository().findByPropertyValue("userId", 108);

		// 创建两个节点之间的好友关系
		Friend ifrnd = friendService.template().createRelationshipBetween(startNode, endNode, Friend.class,
				RelTypeName.FRIEND, false);
		// 验证不空
		assertNotNull(ifrnd);
		// 打印json
		logger.debug("~~~~~~~~~~Isfriend:" + JSON.toJSONString(ifrnd));
		// 更新好友关系
		boolean sucess = friendService.updateFriend(107, 108, "备注名称2", "备注2", false, Lang.list("分组1"));
		// 验证是否更新成功
		assertTrue(sucess);
		// 获取两个节点之间的好友关系
		ifrnd = friendService.template().getRelationshipBetween(startNode, endNode, Friend.class, RelTypeName.FRIEND);
		// 验证不空
		assertNotNull(ifrnd);
		// 打印json
		logger.debug("~~~~~~~~~~Isfriend:" + JSON.toJSONString(ifrnd));

	}

	@Test
	public void testUpdateFriendValidateRemarkNameLength() throws XhrcException {
		// 验证是否存该节点，两个用户节点 107.108
		// 从neo4j获取节点
		UserNode startNode = friendService.userRepository().findByPropertyValue("userId", 107);
		UserNode endNode = friendService.userRepository().findByPropertyValue("userId", 108);

		// 创建两个节点之间的好友关系
		Friend ifrnd = friendService.template().createRelationshipBetween(startNode, endNode, Friend.class,
				RelTypeName.FRIEND, false);
		// 验证不空
		assertNotNull(ifrnd);
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 22; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		Throwable t = null;
		try {
			friendService.updateFriend(107, 108, "一二三四五一二三四五一二三四五一二三四五3", "备注", false, Lang.list("分组1"));
		} catch (Exception ex) {
			t = ex;
		} finally {
			assertNotNull(t);
			assertTrue(t instanceof XhrcRuntimeException);
			logger.debug("~~~~~~~~~~" + t.getMessage());
			assertTrue(t.getMessage().contains("设置好友备注名称失败，remarkName长度不合法。"));
		}

	}

	@Test
	public void testUpdateFriendValidateLength() throws XhrcException {
		// 验证是否存该节点
		UserNode node1 = friendService.userRepository().findByPropertyValue("userId", 107);
		UserNode node2 = friendService.userRepository().findByPropertyValue("userId", 108);
		// 若存在删除它们,存在关系的时候要删除关系
		if (node1 != null && node2 != null) {
			Friend friend = friendService.template().getRelationshipBetween(node1, node2, Friend.class,
					RelTypeName.FRIEND);
			if (friend != null) {
				friendService.template().delete(friend);
			}
		}
		if (node1 != null) {
			friendService.template().delete(node1);
		}
		if (node2 != null) {
			friendService.template().delete(node2);
		}
		// 创建两个用户节点 107.108
		UserNode yjl1 = new UserNode();
		yjl1.setUserId(107);
		yjl1.setCustomFriendGroups(Lang.list("天下杂谈"));
		friendService.userRepository().save(yjl1);
		UserNode yjl2 = new UserNode();
		yjl2.setUserId(108);
		yjl2.setCustomFriendGroups(Lang.list("商业论坛"));
		friendService.userRepository().save(yjl2);
		// 校验是否成功创建
		logger.debug("~~~~~~~~~~UserNode:yjl1:" + JSON.toJSONString(yjl1));
		logger.debug("~~~~~~~~~~UserNode:yjl2:" + JSON.toJSONString(yjl2));
		// 从neo4j获取节点
		UserNode startNode = friendService.userRepository().findByPropertyValue("userId", 107);
		UserNode endNode = friendService.userRepository().findByPropertyValue("userId", 108);

		// 创建两个节点之间的好友关系
		Friend ifrnd = friendService.template().createRelationshipBetween(startNode, endNode, Friend.class,
				RelTypeName.FRIEND, false);
		// 验证不空
		assertNotNull(ifrnd);
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 502; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		Throwable t = null;
		try {
			friendService.updateFriend(107, 108, "", sb.toString(), false, Lang.list("分组1"));
		} catch (Exception ex) {
			t = ex;
		} finally {
			assertNotNull(t);
			assertTrue(t instanceof XhrcRuntimeException);
			logger.debug("~~~~~~~~~~" + t.getMessage());
			assertTrue(t.getMessage().contains("设置好友备注失败，remark长度不合法。"));
		}

	}

	@Test
	public void testCountUserFriendByGroup() throws XhrcException {
		long count = 0;

		UserNode startUser = userRepository.findByPropertyValue("userId", 290);
		if (startUser == null) {
			// 创建用户 加好友30个
			startUser = new UserNode();
			startUser.setUserId(290);
			// startUser.setFriendGroups("好友");
			startUser = friendService.userRepository().save(startUser);

			for (int i = 291; i < 321; i++) {
				UserNode user = new UserNode();
				user.setUserId(i);
				// user.setFriendGroups("好友");
				user = userRepository.save(user);
				userRepository.createRelationshipBetween(startUser, user, Friend.class, RelTypeName.FRIEND);
				userRepository.createRelationshipBetween(user, startUser, Friend.class, RelTypeName.FRIEND);
			}
			for (int i = 291; i < 301; i++) {
				UserNode targetUser = userRepository.findByPropertyValue("userId", i);
				Friend friRel = friendService.template().getRelationshipBetween(startUser, targetUser, Friend.class,
						RelTypeName.FRIEND);
				friRel.setFriendGroups(Lang.list("分组1"));
				// friRel.setIssueDate(DateUtils.formatIssueDate(new Date()));
				friendService.template().save(friRel);
			}
			for (int i = 301; i < 321; i++) {
				UserNode targetUser = userRepository.findByPropertyValue("userId", i);
				Friend friRel = friendService.template().getRelationshipBetween(startUser, targetUser, Friend.class,
						RelTypeName.FRIEND);
				friRel.setFriendGroups(Lang.list("分组2"));
				// friRel.setIssueDate(DateUtils.formatIssueDate(new Date()));
				friendService.template().save(friRel);
			}
		}

		count = friendService.countUserFriendByGroup(startUser.getUserId(), "分组1");
		assertEquals("好友数量计算错误", 10, count);
		logger.debug("############用户  -- 分组 -- 好友数量:" + startUser.getUserId() + "--分组1" + " -- " + count);
		count = friendService.countUserFriendByGroup(startUser.getUserId(), "分组2");
		assertEquals("好友数量计算错误", 20, count);
		logger.debug("############用户  -- 分组 -- 好友数量:" + startUser.getUserId() + "--分组2" + " -- " + count);
	}

	@Test
	public void testGetUserFriendsWithoutGroup() throws XhrcException {
		TestUtils.initUserNode(userRepository, 665, 666, 667, 668);
		UserNode node1 = userRepository.findByPropertyValue("userId", 665);
		UserNode node2 = userRepository.findByPropertyValue("userId", 666);

		UserNode node3 = userRepository.findByPropertyValue("userId", 667);
		UserNode node4 = userRepository.findByPropertyValue("userId", 668);
		// 判断若不存在关系，则创建关系，node1与node2，node4是朋友，node2和node3是朋友
		Friend friend1 = friendService.template()
				.getRelationshipBetween(node1, node2, Friend.class, RelTypeName.FRIEND);
		if (friend1 == null) {
			userRepository.createRelationshipBetween(node1, node2, Friend.class, RelTypeName.FRIEND);
			userRepository.createRelationshipBetween(node2, node1, Friend.class, RelTypeName.FRIEND);
		}
		Friend friend2 = friendService.template()
				.getRelationshipBetween(node3, node2, Friend.class, RelTypeName.FRIEND);
		Friend friend21 = friendService.template().getRelationshipBetween(node2, node3, Friend.class,
				RelTypeName.FRIEND);
		if (friend2 == null || friend21 == null) {
			userRepository.createRelationshipBetween(node3, node2, Friend.class, RelTypeName.FRIEND);
			userRepository.createRelationshipBetween(node2, node3, Friend.class, RelTypeName.FRIEND);
		}
		Friend friend3 = friendService.template()
				.getRelationshipBetween(node1, node4, Friend.class, RelTypeName.FRIEND);
		if (friend3 == null) {
			userRepository.createRelationshipBetween(node1, node4, Friend.class, RelTypeName.FRIEND);
			userRepository.createRelationshipBetween(node4, node1, Friend.class, RelTypeName.FRIEND);
		}
		boolean bool = false;
		List<Friend> resultList1 = null;
		resultList1 = friendService.getUserFriends(665, null, null, null);
		assertEquals("返回好友id数量错误", resultList1.size(), 2);
		bool = resultList1.get(0).getEndNode().getUserId() == 666 || resultList1.get(0).getEndNode().getUserId() == 668;
		assertTrue(bool);
		bool = resultList1.get(1).getEndNode().getUserId() == 666 || resultList1.get(1).getEndNode().getUserId() == 668;
		assertTrue(bool);
		bool = resultList1.get(0) == resultList1.get(1);
		assertFalse(bool);

	}

	@Test
	public void testGetUserFriendsWithGroup() throws XhrcException {
		TestUtils.initUserNode(userRepository, 661, 662, 663, 664, 660);
		UserNode node1 = userRepository.findByPropertyValue("userId", 661);
		UserNode node2 = userRepository.findByPropertyValue("userId", 662);

		UserNode node3 = userRepository.findByPropertyValue("userId", 663);
		UserNode node4 = userRepository.findByPropertyValue("userId", 664);
		UserNode node5 = userRepository.findByPropertyValue("userId", 660);
		// node1与node2互为好友，互为知己
		Friend friend10 = template.getRelationshipBetween(node1, node2, Friend.class, RelTypeName.FRIEND);
		Friend friend11 = template.getRelationshipBetween(node2, node1, Friend.class, RelTypeName.FRIEND);
		if (friend10 == null) {
			friend10 = template.createRelationshipBetween(node1, node2, Friend.class, RelTypeName.FRIEND, false);
			List<String> friendGroup = new ArrayList<String>();
			friendGroup.add("知己");
			friend10.setFriendGroups(friendGroup);
			template.save(friend10);
		} else {
			if (friend10.getFriendGroups() == null || !friend10.getFriendGroups().contains("知己")) {
				List<String> friendGroup = new ArrayList<String>();
				friendGroup.add("知己");
				friend10.setFriendGroups(friendGroup);
				template.save(friend10);
			}
		}
		if (friend11 == null) {
			friend11 = template.createRelationshipBetween(node2, node1, Friend.class, RelTypeName.FRIEND, false);
			List<String> friendGroup = new ArrayList<String>();
			friendGroup.add("知己");
			friend11.setFriendGroups(friendGroup);
			template.save(friend11);
		} else {
			if (friend11.getFriendGroups() == null || !friend11.getFriendGroups().contains("知己")) {
				List<String> friendGroup = new ArrayList<String>();
				friendGroup.add("知己");
				friend11.setFriendGroups(friendGroup);
				template.save(friend11);
			}
		}
		// node1与node4互为好友，node1是node4但node4不是node1的知己
		Friend friend20 = template.getRelationshipBetween(node1, node4, Friend.class, RelTypeName.FRIEND);
		Friend friend21 = template.getRelationshipBetween(node4, node1, Friend.class, RelTypeName.FRIEND);
		if (friend20 == null) {
			friend20 = template.createRelationshipBetween(node1, node4, Friend.class, RelTypeName.FRIEND, false);
			template.save(friend20);
		} else {
			if (friend20.getFriendGroups() != null && friend20.getFriendGroups().contains("知己")) {
				List<String> friendGroup = new ArrayList<String>();
				friendGroup = friend20.getFriendGroups();
				friendGroup.remove("知己");
				friend20.setFriendGroups(friendGroup);
				template.save(friend20);
			}
		}
		if (friend21 == null) {
			friend21 = template.createRelationshipBetween(node4, node1, Friend.class, RelTypeName.FRIEND, false);
			List<String> friendGroup = new ArrayList<String>();
			friendGroup.add("知己");
			friend21.setFriendGroups(friendGroup);
			template.save(friend21);
		} else {
			if (friend21.getFriendGroups() == null || !friend21.getFriendGroups().contains("知己")) {
				List<String> friendGroup = new ArrayList<String>();
				friendGroup.add("知己");
				friend21.setFriendGroups(friendGroup);
				template.save(friend21);
			}
		}

		// node1与node5互为好友，node5是node1的知己，但node1不是node5的知己
		Friend friend40 = template.getRelationshipBetween(node1, node5, Friend.class, RelTypeName.FRIEND);
		Friend friend41 = template.getRelationshipBetween(node5, node1, Friend.class, RelTypeName.FRIEND);
		if (friend40 == null) {
			friend40 = template.createRelationshipBetween(node1, node5, Friend.class, RelTypeName.FRIEND, false);
			List<String> friendGroup = new ArrayList<String>();
			friendGroup.add("知己");
			friend40.setFriendGroups(friendGroup);
			template.save(friend40);
		} else {
			if (friend40.getFriendGroups() == null || !friend40.getFriendGroups().contains("知己")) {
				List<String> friendGroup = new ArrayList<String>();
				friendGroup.add("知己");
				friend40.setFriendGroups(friendGroup);
				template.save(friend40);
			}
		}
		if (friend41 == null) {
			friend41 = template.createRelationshipBetween(node5, node1, Friend.class, RelTypeName.FRIEND, false);
			template.save(friend41);
		} else {
			if (friend41.getFriendGroups() != null && friend41.getFriendGroups().contains("知己")) {
				List<String> friendGroup = new ArrayList<String>();
				friendGroup = friend41.getFriendGroups();
				friendGroup.remove("知己");
				friend41.setFriendGroups(friendGroup);
				template.save(friend41);
			}
		}
		// node3和node1互为好友，互为发小，
		Friend friend30 = template.getRelationshipBetween(node1, node3, Friend.class, RelTypeName.FRIEND);
		Friend friend31 = template.getRelationshipBetween(node3, node1, Friend.class, RelTypeName.FRIEND);
		if (friend30 == null) {
			friend30 = template.createRelationshipBetween(node1, node3, Friend.class, RelTypeName.FRIEND, false);
			List<String> friendGroup = new ArrayList<String>();
			friendGroup.add("发小");
			friend30.setFriendGroups(friendGroup);
			template.save(friend30);
		} else {
			if (friend30.getFriendGroups() == null || !friend30.getFriendGroups().contains("发小")) {
				List<String> friendGroup = new ArrayList<String>();
				friendGroup.add("发小");
				friend30.setFriendGroups(friendGroup);
				template.save(friend30);
			}
		}
		if (friend31 == null) {
			friend31 = template.createRelationshipBetween(node3, node1, Friend.class, RelTypeName.FRIEND, false);
			List<String> friendGroup = new ArrayList<String>();
			friendGroup.add("发小");
			friend31.setFriendGroups(friendGroup);
			template.save(friend31);
		} else {
			if (friend31.getFriendGroups() == null || !friend31.getFriendGroups().contains("发小")) {
				List<String> friendGroup = new ArrayList<String>();
				friendGroup.add("发小");
				friend31.setFriendGroups(friendGroup);
				template.save(friend31);
			}
		}

		boolean bool = false;
		List<Friend> resultList = null;
		resultList = friendService.getUserFriends(661, "知己", null, null);
		assertEquals("获取好友id数量错误", resultList.size(), 2);
		bool = resultList.get(0).getEndNode().getUserId() == 662 || resultList.get(0).getEndNode().getUserId() == 660;
		assertTrue(bool);
		bool = resultList.get(1).getEndNode().getUserId() == 662 || resultList.get(1).getEndNode().getUserId() == 660;
		assertTrue(bool);
		assertEquals(friendService.getUserFriends(663, "发小", null, null).size(), 1);

	}

	@Test
	public void testGetUserFriendsOfFriends() throws XhrcException {
		TestUtils.initUserNode(userRepository, 661, 662, 663, 664, 665);
		UserNode node1 = userRepository.findByPropertyValue("userId", 661);
		UserNode node2 = userRepository.findByPropertyValue("userId", 662);

		UserNode node3 = userRepository.findByPropertyValue("userId", 663);
		UserNode node4 = userRepository.findByPropertyValue("userId", 664);
		UserNode node5 = userRepository.findByPropertyValue("userId", 665);
		// node1与node2是好友
		userRepository.createRelationshipBetween(node1, node2, Friend.class, RelTypeName.FRIEND);
		userRepository.createRelationshipBetween(node2, node1, Friend.class, RelTypeName.FRIEND);
		// node2与node3是好友
		userRepository.createRelationshipBetween(node2, node3, Friend.class, RelTypeName.FRIEND);
		userRepository.createRelationshipBetween(node3, node2, Friend.class, RelTypeName.FRIEND);
		// node1与node4是好友
		userRepository.createRelationshipBetween(node1, node4, Friend.class, RelTypeName.FRIEND);
		userRepository.createRelationshipBetween(node4, node1, Friend.class, RelTypeName.FRIEND);
		// node2与node4是好友
		userRepository.createRelationshipBetween(node2, node4, Friend.class, RelTypeName.FRIEND);
		userRepository.createRelationshipBetween(node4, node2, Friend.class, RelTypeName.FRIEND);
		// node3与node4是好友
		userRepository.createRelationshipBetween(node3, node4, Friend.class, RelTypeName.FRIEND);
		userRepository.createRelationshipBetween(node4, node3, Friend.class, RelTypeName.FRIEND);
		// node4与node5是好友
		userRepository.createRelationshipBetween(node4, node5, Friend.class, RelTypeName.FRIEND);
		userRepository.createRelationshipBetween(node5, node4, Friend.class, RelTypeName.FRIEND);
		List<Integer> fofid = friendService.getUserFriendsAtDeepTwo(661, (long) 0, 5);
		assertEquals("获取朋友的朋友数量错误", 2, fofid.size());
		boolean bool = false;
		bool = fofid.get(0) == 663 || fofid.get(0) == 665;
		assertTrue(bool);
	}
}

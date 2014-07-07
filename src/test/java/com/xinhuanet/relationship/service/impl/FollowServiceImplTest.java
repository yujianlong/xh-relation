package com.xinhuanet.relationship.service.impl;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Random;

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
import com.xinhuanet.relationship.common.exception.XhrcRuntimeException;
import com.xinhuanet.relationship.entity.node.UserNode;
import com.xinhuanet.relationship.entity.relationship.Follows;
import com.xinhuanet.relationship.lang.Lang;
import com.xinhuanet.relationship.repository.UserRepository;

public class FollowServiceImplTest {

	private static Logger logger = LoggerFactory.getLogger(FollowServiceImplTest.class);

	private static FollowServiceImpl followService;
	private static ClassPathXmlApplicationContext context;
	private static UserRepository userRepository;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// 获取baseService
		context = new ClassPathXmlApplicationContext("context-main.xml");
		followService = context.getBean("followService", FollowServiceImpl.class);
		userRepository = followService.userRepository();
		// 准备数据
		TestUtils.initUserNode(userRepository, 251, 252, 253, 254);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		context.close();
	}

	@Test
	public void testDeleteFollow() throws XhrcException {
		// 查找4个节点 251，252，253， 254 不存在则创建
		UserNode node1 = followService.getUserNode(251);
		UserNode node2 = followService.getUserNode(252);
		UserNode node3 = followService.getUserNode(253);
		UserNode node4 = followService.getUserNode(254);

		// 若存在关系的时候要删除关系
		if (node1 != null && node2 != null) {
			Follows follow = followService.template().getRelationshipBetween(node1, node2, Follows.class,
					RelTypeName.FOLLOWS);
			if (follow != null) {
				followService.template().delete(follow);
			}
		}
		// 若存在删除它们,存在关系的时候要删除关系
		if (node3 != null && node4 != null) {
			Follows follow = followService.userRepository().getRelationshipBetween(node3, node4, Follows.class,
					RelTypeName.FOLLOWS);
			if (follow != null) {
				followService.template().delete(follow);
			}
		}

		// 成功为 true 不成功为false
		userRepository.createRelationshipBetween(node1, node2, Follows.class, RelTypeName.FOLLOWS);
		boolean bool = false;
		bool = followService.deleteFollow(node1.getUserId(), node2.getUserId());
		// 验证是否删除成功
		assertTrue(bool);
		Follows flw1 = followService.template()
				.getRelationshipBetween(node1, node2, Follows.class, RelTypeName.FOLLOWS);
		Follows flw2 = followService.template()
				.getRelationshipBetween(node2, node1, Follows.class, RelTypeName.FOLLOWS);
		// 验证关注关系删除是否成功
		assertNull(flw1);
		assertNull(flw2);

		bool = followService.deleteFollow(node3.getUserId(), node4.getUserId());
		// 验证是否删除成功
		assertTrue(bool);
		Follows flw3 = followService.template()
				.getRelationshipBetween(node3, node4, Follows.class, RelTypeName.FOLLOWS);
		Follows flw4 = followService.template()
				.getRelationshipBetween(node4, node3, Follows.class, RelTypeName.FOLLOWS);
		// 验证关注关系删除是否成功
		assertNull(flw3);
		assertNull(flw4);
	}

	@Test
	public void testExistFollow() throws XhrcException {
		// 查找4个节点 251，252，253， 254 不存在则创建
		UserNode node1 = followService.getUserNode(251);
		UserNode node2 = followService.getUserNode(252);
		UserNode node3 = followService.getUserNode(253);
		UserNode node4 = followService.getUserNode(254);

		// 若存在关系的时候要删除关系
		if (node1 != null && node2 != null) {
			Follows follow = followService.template().getRelationshipBetween(node1, node2, Follows.class,
					RelTypeName.FOLLOWS);
			if (follow != null) {
				followService.template().delete(follow);
			}
		}

		// 若存在删除它们,存在关系的时候要删除关系
		if (node3 != null && node4 != null) {
			Follows follow = followService.template().getRelationshipBetween(node3, node4, Follows.class,
					RelTypeName.FOLLOWS);
			if (follow != null) {
				followService.template().delete(follow);
			}
		}

		userRepository.createRelationshipBetween(node1, node2, Follows.class, RelTypeName.FOLLOWS);
		boolean bool = false;
		bool = followService.existFollow(node1.getUserId(), node2.getUserId());
		// 存在返回 true
		assertTrue(bool);

		bool = followService.existFollow(node3.getUserId(), node4.getUserId());
		// 不存在返回false
		assertFalse(bool);
	}

	@Test
	public void testCountFollow() throws XhrcException {
		long count = 0;

		UserNode startUser = userRepository.findByPropertyValue("userId", 259);
		if (startUser == null) {
			// 创建用户 关注30个
			startUser = new UserNode();
			startUser.setUserId(259);
			// startUser.setFriendGroups("好友家园");
			startUser = followService.userRepository().save(startUser);

			for (int i = 260; i < 290; i++) {
				UserNode user = new UserNode();
				user.setUserId(i);
				// user.setFriendGroups("好友家园");
				user = followService.userRepository().save(user);
				userRepository.createRelationshipBetween(startUser, user, Follows.class, RelTypeName.FOLLOWS);
			}
		}
		count = followService.countFollow(startUser.getUserId());
		assertEquals("关注数量计算错误", 30, count);
		logger.debug("############用户  -- 关注数量:" + startUser.getUserId() + " -- " + count);
	}

	@Test
	public void testCreateFollow() throws XhrcException {
		// 验证是否存该节点
		UserNode node1 = followService.userRepository().findByPropertyValue("userId", 109);
		UserNode node2 = followService.userRepository().findByPropertyValue("userId", 110);
		// 若存在删除它们,存在关系的时候要删除关系
		if (node1 != null && node2 != null) {
			Follows follow = followService.template().getRelationshipBetween(node1, node2, Follows.class,
					RelTypeName.FOLLOWS);
			if (follow != null) {
				followService.template().delete(follow);
			}
		}
		if (node1 != null) {
			followService.template().delete(node1);
		}
		if (node2 != null) {
			followService.template().delete(node2);
		}
		// 创建两个用户节点 109.110
		UserNode yjl1 = new UserNode();
		yjl1.setUserId(109);
		yjl1.setCustomFriendGroups(Lang.list("奇闻趣事1"));
		followService.userRepository().save(yjl1);
		UserNode yjl2 = new UserNode();
		yjl2.setUserId(110);
		yjl2.setCustomFriendGroups(Lang.list("喜闻乐见1"));
		followService.userRepository().save(yjl2);
		// 校验是否成功创建
		logger.debug("~~~~~~~~~~UserNode:yjl1:" + JSON.toJSONString(yjl1));
		logger.debug("~~~~~~~~~~UserNode:yjl2:" + JSON.toJSONString(yjl2));
		// 从neo4j获取节点
		UserNode startNode = followService.userRepository().findByPropertyValue("userId", 109);
		UserNode endNode = followService.userRepository().findByPropertyValue("userId", 110);
		// 创建两个节点之间的好友关系
		boolean sucess = followService.createFollow(109, 110, "备注0");
		// 验证是否创建成功
		assertTrue(sucess);
		// 获取两个节点之间的好友关系
		Follows ifrnd = followService.template().getRelationshipBetween(startNode, endNode, Follows.class,
				RelTypeName.FOLLOWS);
		// 验证不空
		assertNotNull(ifrnd);
		// 打印json
		logger.debug("~~~~~~~~~~Follow:" + JSON.toJSONString(ifrnd));

	}

	@Test
	public void testCreateFollowValidateLength() throws XhrcException {
		// 验证是否存该节点
		UserNode node1 = followService.userRepository().findByPropertyValue("userId", 109);
		UserNode node2 = followService.userRepository().findByPropertyValue("userId", 110);
		if (node1 != null) {
			followService.template().delete(node1);
		}
		if (node2 != null) {
			followService.template().delete(node2);
		}
		// 创建两个用户节点 109.110
		UserNode yjl1 = new UserNode();
		yjl1.setUserId(109);
		yjl1.setCustomFriendGroups(Lang.list("奇闻趣事1"));
		followService.userRepository().save(yjl1);
		UserNode yjl2 = new UserNode();
		yjl2.setUserId(110);
		yjl2.setCustomFriendGroups(Lang.list("喜闻乐见1"));
		followService.userRepository().save(yjl2);
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
			followService.createFollow(109, 110, sb.toString());
		} catch (Exception ex) {
			t = ex;
		} finally {
			assertNotNull(t);
			assertTrue(t instanceof XhrcRuntimeException);
			logger.debug("~~~~~~~~~~" + t.getMessage());
			assertTrue(t.getMessage().contains("创建备注失败，remark长度不合法。"));
		}

	}

	@Test
	public void testUpdateFollow() throws XhrcException {
		// 验证是否存该节点
		UserNode node1 = followService.userRepository().findByPropertyValue("userId", 111);
		UserNode node2 = followService.userRepository().findByPropertyValue("userId", 112);
		// 若存在删除它们,存在关系的时候要删除关系
		if (node1 != null && node2 != null) {
			Follows follow = followService.template().getRelationshipBetween(node1, node2, Follows.class,
					RelTypeName.FOLLOWS);
			if (follow != null) {
				followService.template().delete(follow);
			}
		}
		if (node1 != null) {
			followService.template().delete(node1);
		}
		if (node2 != null) {
			followService.template().delete(node2);
		}
		// 创建两个用户节点 111.112
		UserNode yjl1 = new UserNode();
		yjl1.setUserId(111);
		yjl1.setCustomFriendGroups(Lang.list("天下杂谈1"));
		followService.userRepository().save(yjl1);
		UserNode yjl2 = new UserNode();
		yjl2.setUserId(112);
		yjl2.setCustomFriendGroups(Lang.list("商业论坛1"));
		followService.userRepository().save(yjl2);
		// 校验是否成功创建
		logger.debug("~~~~~~~~~~UserNode:yjl1:" + JSON.toJSONString(yjl1));
		logger.debug("~~~~~~~~~~UserNode:yjl2:" + JSON.toJSONString(yjl2));
		// 从neo4j获取节点
		UserNode startNode = followService.userRepository().findByPropertyValue("userId", 111);
		UserNode endNode = followService.userRepository().findByPropertyValue("userId", 112);
		// 创建两个节点之间的关注关系
		Follows ifrnd = followService.template().createRelationshipBetween(startNode, endNode, Follows.class,
				RelTypeName.FOLLOWS, false);
		// 验证不空
		assertNotNull(ifrnd);
		// 打印json
		logger.debug("~~~~~~~~~~Follow:" + JSON.toJSONString(ifrnd));
		// 更新关注关系
		boolean sucess = followService.updateFollow(111, 112, "备注follow1");
		// 验证是否更新成功
		assertTrue(sucess);
		// 获取两个节点之间的关注关系
		ifrnd = followService.template().getRelationshipBetween(startNode, endNode, Follows.class, RelTypeName.FOLLOWS);
		// 验证不空
		assertNotNull(ifrnd);
		// 打印json
		logger.debug("~~~~~~~~~~Follow:" + JSON.toJSONString(ifrnd));

	}

	@Test
	public void testUpdateFollowValidateLength() throws XhrcException {
		// 验证是否存该节点
		UserNode node1 = followService.userRepository().findByPropertyValue("userId", 111);
		UserNode node2 = followService.userRepository().findByPropertyValue("userId", 112);
		if (node1 != null) {
			followService.template().delete(node1);
		}
		if (node2 != null) {
			followService.template().delete(node2);
		}
		// 创建两个用户节点 111.112
		UserNode yjl1 = new UserNode();
		yjl1.setUserId(111);
		yjl1.setCustomFriendGroups(Lang.list("天下杂谈1"));
		followService.userRepository().save(yjl1);
		UserNode yjl2 = new UserNode();
		yjl2.setUserId(112);
		yjl2.setCustomFriendGroups(Lang.list("商业论坛1"));
		followService.userRepository().save(yjl2);
		// 校验是否成功创建
		logger.debug("~~~~~~~~~~UserNode:yjl1:" + JSON.toJSONString(yjl1));
		logger.debug("~~~~~~~~~~UserNode:yjl2:" + JSON.toJSONString(yjl2));
		// 从neo4j获取节点
		UserNode startNode = followService.userRepository().findByPropertyValue("userId", 111);
		UserNode endNode = followService.userRepository().findByPropertyValue("userId", 112);
		// 创建两个节点之间的关注关系
		Follows ifrnd = followService.template().createRelationshipBetween(startNode, endNode, Follows.class,
				RelTypeName.FOLLOWS, false);
		// 从neo4j获取节点
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
			followService.updateFollow(111, 112, sb.toString());
		} catch (Exception ex) {
			t = ex;
		} finally {
			assertNotNull(t);
			assertTrue(t instanceof XhrcRuntimeException);
			logger.debug("~~~~~~~~~~" + t.getMessage());
			assertTrue(t.getMessage().contains("创建备注失败，remark长度不合法。"));
		}

	}

	@Test
	public void testGetUserFollows() throws XhrcException {
		TestUtils.initUserNode(userRepository, 661, 662, 663, 664);
		UserNode node1 = userRepository.findByPropertyValue("userId", 661);
		UserNode node2 = userRepository.findByPropertyValue("userId", 662);

		UserNode node3 = userRepository.findByPropertyValue("userId", 663);
		UserNode node4 = userRepository.findByPropertyValue("userId", 664);
		// 判断若不存在关系，则创建关系，node1关注node2和node4，node4 关注node1，node2关注node3
		Follows follow1 = followService.template().getRelationshipBetween(node1, node2, Follows.class,
				RelTypeName.FOLLOWS);
		if (follow1 == null) {
			userRepository.createRelationshipBetween(node1, node2, Follows.class, RelTypeName.FOLLOWS);
		}
		Follows follow2 = followService.template().getRelationshipBetween(node1, node4, Follows.class,
				RelTypeName.FOLLOWS);
		if (follow2 == null) {
			userRepository.createRelationshipBetween(node1, node4, Follows.class, RelTypeName.FOLLOWS);
			userRepository.createRelationshipBetween(node4, node1, Follows.class, RelTypeName.FOLLOWS);
		}
		Follows follow3 = followService.template().getRelationshipBetween(node2, node3, Follows.class,
				RelTypeName.FRIEND);
		if (follow3 == null) {
			userRepository.createRelationshipBetween(node2, node3, Follows.class, RelTypeName.FOLLOWS);
		}
		boolean bool = false;
		List<Follows> resultList = null;
		resultList = followService.getUserFollows(661, null, null);
		assertEquals("获取关注数量错误", resultList.size(), 2);
		bool = resultList.get(0).getEndNode().getUserId() == 662 || resultList.get(0).getEndNode().getUserId() == 664;
		assertTrue(bool);
	}

	@Test
	public void testGetFollowsToUser() throws XhrcException {
		TestUtils.initUserNode(userRepository, 641, 642, 643, 644);
		UserNode node1 = userRepository.findByPropertyValue("userId", 641);
		UserNode node2 = userRepository.findByPropertyValue("userId", 642);
		UserNode node3 = userRepository.findByPropertyValue("userId", 643);
		UserNode node4 = userRepository.findByPropertyValue("userId", 644);
		// 判断若不存在关系，则创建关系，node1关注node2和node4，node4 关注node1，node2关注node3
		Follows follow1 = followService.template().getRelationshipBetween(node1, node2, Follows.class,
				RelTypeName.FOLLOWS);
		if (follow1 == null) {
			userRepository.createRelationshipBetween(node1, node2, Follows.class, RelTypeName.FOLLOWS);
		}
		Follows follow2 = followService.template().getRelationshipBetween(node1, node4, Follows.class,
				RelTypeName.FOLLOWS);
		Follows follow21 = followService.template().getRelationshipBetween(node4, node1, Follows.class,
				RelTypeName.FOLLOWS);
		if (follow2 == null) {
			userRepository.createRelationshipBetween(node1, node4, Follows.class, RelTypeName.FOLLOWS);
		}
		if (follow21 == null) {
			userRepository.createRelationshipBetween(node4, node1, Follows.class, RelTypeName.FOLLOWS);
		}
		Follows follow3 = followService.template().getRelationshipBetween(node2, node3, Follows.class,
				RelTypeName.FRIEND);
		if (follow3 == null) {
			userRepository.createRelationshipBetween(node2, node3, Follows.class, RelTypeName.FOLLOWS);
		}
		boolean bool = false;
		List<Follows> resultList = null;
		resultList = followService.getFollowsToUser(641, null, null);
		assertEquals("获取关注数量错误", resultList.size(), 1);
		bool = resultList.get(0).getStartNode().getUserId() == 644;
		assertTrue(bool);
		resultList = followService.getFollowsToUser(642, null, null);
		assertEquals("获取关注数量错误", resultList.size(), 1);
		bool = resultList.get(0).getStartNode().getUserId() == 641;
		assertTrue(bool);
	}

	@Test
	public void testCountFollowsToUser() throws XhrcException {
		TestUtils.initUserNode(userRepository, 641, 642, 643, 644);
		UserNode node1 = userRepository.findByPropertyValue("userId", 641);
		UserNode node2 = userRepository.findByPropertyValue("userId", 642);
		UserNode node3 = userRepository.findByPropertyValue("userId", 643);
		UserNode node4 = userRepository.findByPropertyValue("userId", 644);
		// 判断若不存在关系，则创建关系，node1关注node2和node4，node4 关注node1，node2关注node3
		Follows follow1 = followService.template().getRelationshipBetween(node1, node2, Follows.class,
				RelTypeName.FOLLOWS);
		if (follow1 == null) {
			userRepository.createRelationshipBetween(node1, node2, Follows.class, RelTypeName.FOLLOWS);
		}
		Follows follow2 = followService.template().getRelationshipBetween(node1, node4, Follows.class,
				RelTypeName.FOLLOWS);
		Follows follow21 = followService.template().getRelationshipBetween(node4, node1, Follows.class,
				RelTypeName.FOLLOWS);
		if (follow2 == null) {
			userRepository.createRelationshipBetween(node1, node4, Follows.class, RelTypeName.FOLLOWS);
		}
		if (follow21 == null) {
			userRepository.createRelationshipBetween(node4, node1, Follows.class, RelTypeName.FOLLOWS);
		}
		Follows follow3 = followService.template().getRelationshipBetween(node2, node3, Follows.class,
				RelTypeName.FRIEND);
		if (follow3 == null) {
			userRepository.createRelationshipBetween(node2, node3, Follows.class, RelTypeName.FOLLOWS);
		}
		assertEquals("获取关注数量错误", followService.countFollowsToUser(641), 1);
		assertEquals("获取关注数量错误", followService.countFollowsToUser(643), 1);

	}
}

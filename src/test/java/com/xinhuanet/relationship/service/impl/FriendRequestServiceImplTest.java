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
import com.xinhuanet.relationship.entity.relationship.FriendRequest;
import com.xinhuanet.relationship.lang.Lang;
import com.xinhuanet.relationship.repository.UserRepository;

public class FriendRequestServiceImplTest {

	private static Logger logger = LoggerFactory.getLogger(FriendRequestServiceImpl.class);

	private static FriendRequestServiceImpl friendRequestService;
	private static ClassPathXmlApplicationContext context;
	private static UserRepository userRepository;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// 获取baseService
		context = new ClassPathXmlApplicationContext("context-main.xml");
		friendRequestService = context.getBean("friendRequestService", FriendRequestServiceImpl.class);
		userRepository = friendRequestService.userRepository();

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		context.close();
	}

	@Test
	public void testCreateFriendRequest() throws XhrcException {
		// 验证是否存该节点
		UserNode node1 = friendRequestService.userRepository().findByPropertyValue("userId", 195);
		UserNode node2 = friendRequestService.userRepository().findByPropertyValue("userId", 196);
		// 若存在删除它们,存在关系的时候要删除关系
		if (node1 != null && node2 != null) {
			FriendRequest fr = friendRequestService.template().getRelationshipBetween(node1, node2,
					FriendRequest.class, RelTypeName.FRIEND_REQUEST);
			if (fr != null) {
				friendRequestService.template().delete(fr);
			}
		}
		if (node1 != null) {
			friendRequestService.template().delete(node1);
		}
		if (node2 != null) {
			friendRequestService.template().delete(node2);
		}
		// 创建两个用户节点 195.196
		UserNode yjl1 = new UserNode();
		yjl1.setUserId(195);
		yjl1.setCustomFriendGroups(Lang.list("奇闻趣事"));
		friendRequestService.userRepository().save(yjl1);
		UserNode yjl2 = new UserNode();
		yjl2.setUserId(196);
		yjl2.setCustomFriendGroups(Lang.list("喜闻乐见"));
		friendRequestService.userRepository().save(yjl2);
		// 校验是否成功创建
		logger.debug("~~~~~~~~~~UserNode:yjl1:" + JSON.toJSONString(yjl1));
		logger.debug("~~~~~~~~~~UserNode:yjl2:" + JSON.toJSONString(yjl2));
		// 从neo4j获取节点
		UserNode startNode = friendRequestService.userRepository().findByPropertyValue("userId", 195);
		UserNode endNode = friendRequestService.userRepository().findByPropertyValue("userId", 196);
		// 创建两个节点之间的好友关系
		boolean sucess = friendRequestService.createFriendRequest(195, 196, "校验信息1");
		// 验证是否创建成功
		assertTrue(sucess);
		// 获取两个节点之间的好友关系
		FriendRequest ifrnd = friendRequestService.template().getRelationshipBetween(startNode, endNode,
				FriendRequest.class, RelTypeName.FRIEND_REQUEST);
		// 验证不空
		assertNotNull(ifrnd);
		// 打印json
		logger.debug("~~~~~~~~~~FRIEND_REQUEST:" + JSON.toJSONString(ifrnd));
	}

	@Test
	public void testCreateFriendRequestValidateLength() throws XhrcException {
		// 验证是否存该节点
		UserNode node1 = friendRequestService.userRepository().findByPropertyValue("userId", 195);
		UserNode node2 = friendRequestService.userRepository().findByPropertyValue("userId", 196);
		if (node1 != null) {
			friendRequestService.template().delete(node1);
		}
		if (node2 != null) {
			friendRequestService.template().delete(node2);
		}
		// 创建两个用户节点 195.196
		UserNode yjl1 = new UserNode();
		yjl1.setUserId(195);
		yjl1.setCustomFriendGroups(Lang.list("奇闻趣事"));
		friendRequestService.userRepository().save(yjl1);
		UserNode yjl2 = new UserNode();
		yjl2.setUserId(196);
		yjl2.setCustomFriendGroups(Lang.list("喜闻乐见"));
		friendRequestService.userRepository().save(yjl2);
		// 校验是否成功创建
		logger.debug("~~~~~~~~~~UserNode:yjl1:" + JSON.toJSONString(yjl1));
		logger.debug("~~~~~~~~~~UserNode:yjl2:" + JSON.toJSONString(yjl2));
		// 从neo4j获取节点
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 1002; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		Throwable t = null;
		try {
			friendRequestService.createFriendRequest(195, 196, sb.toString());
		} catch (Exception ex) {
			t = ex;
		} finally {
			assertNotNull(t);
			assertTrue(t instanceof XhrcRuntimeException);
			logger.debug("~~~~~~~~~~" + t.getMessage());
			assertTrue(t.getMessage().contains("创建好友请求附言失败，message长度不合法。"));
		}

	}

	@Test
	public void testDeleteFriendRequest() throws XhrcException {
		// 创建测试数据
		UserNode node1 = friendRequestService.getUserNode(256);
		UserNode node2 = friendRequestService.getUserNode(257);
		UserNode node3 = friendRequestService.getUserNode(258);
		FriendRequest bk1 = userRepository.getRelationshipBetween(node1, node2, FriendRequest.class,
				RelTypeName.FRIEND_REQUEST);
		FriendRequest bk2 = userRepository.getRelationshipBetween(node1, node3, FriendRequest.class,
				RelTypeName.FRIEND_REQUEST);
		if (bk1 == null) {
			bk1 = userRepository.createRelationshipBetween(node1, node2, FriendRequest.class,
					RelTypeName.FRIEND_REQUEST);
		}
		if (bk2 != null) {
			userRepository.deleteRelationshipBetween(node1, node3, RelTypeName.FRIEND_REQUEST);
		}

		// 测试开始 1.存在好友请求 请求删除 2.不存在好友请求 请求删除
		boolean bool = false;
		bool = friendRequestService.deleteFriendRequest(node1.getUserId(), node2.getUserId());
		// 成功返回true
		assertTrue(bool);
		FriendRequest blk1 = userRepository.getRelationshipBetween(node1, node2, FriendRequest.class,
				RelTypeName.FRIEND_REQUEST);
		// 不存在好友请求
		assertNull(blk1);

		bool = friendRequestService.deleteFriendRequest(node1.getUserId(), node3.getUserId());
		// 成功返回true
		assertTrue(bool);
		FriendRequest blk2 = userRepository.getRelationshipBetween(node1, node3, FriendRequest.class,
				RelTypeName.FRIEND_REQUEST);
		// 不存在好友请求
		assertNull(blk2);
	}

	@Test
	public void testExistFriendRequest() throws XhrcException {
		// 创建测试数据
		UserNode node1 = friendRequestService.getUserNode(256);
		UserNode node2 = friendRequestService.getUserNode(257);
		UserNode node3 = friendRequestService.getUserNode(258);
		FriendRequest bk1 = userRepository.getRelationshipBetween(node1, node2, FriendRequest.class,
				RelTypeName.FRIEND_REQUEST);
		FriendRequest bk2 = userRepository.getRelationshipBetween(node1, node3, FriendRequest.class,
				RelTypeName.FRIEND_REQUEST);
		if (bk1 == null) {
			bk1 = userRepository.createRelationshipBetween(node1, node2, FriendRequest.class,
					RelTypeName.FRIEND_REQUEST);
		}
		if (bk2 != null) {
			userRepository.deleteRelationshipBetween(node1, node3, RelTypeName.FRIEND_REQUEST);
		}

		// 测试开始 1.存在好友请求 2.不存在好友请求
		boolean bool = false;
		bool = friendRequestService.existFriendRequest(node1.getUserId(), node2.getUserId());
		// 存在返回true
		assertTrue(bool);
		FriendRequest blk1 = userRepository.getRelationshipBetween(node1, node2, FriendRequest.class,
				RelTypeName.FRIEND_REQUEST);
		// 存在好友请求
		assertNotNull(blk1);

		bool = friendRequestService.existFriendRequest(node1.getUserId(), node3.getUserId());
		// 不存在好友请求
		assertFalse(bool);
		FriendRequest blk2 = userRepository.getRelationshipBetween(node1, node3, FriendRequest.class,
				RelTypeName.FRIEND_REQUEST);
		// 不存在好友请求
		assertNull(blk2);
	}

	@Test
	public void testGetFriendRequestToUser() throws XhrcException {
		TestUtils.initUserNode(userRepository, 610, 611, 612, 613);
		UserNode node1 = userRepository.findByPropertyValue("userId", 610);
		UserNode node2 = userRepository.findByPropertyValue("userId", 611);

		UserNode node3 = userRepository.findByPropertyValue("userId", 612);
		UserNode node4 = userRepository.findByPropertyValue("userId", 613);
		// 判断若不存在关系，则创建关系，node2和node4请求node1，node1请求node3
		FriendRequest friendRequest1 = friendRequestService.template().getRelationshipBetween(node2, node1,
				FriendRequest.class, RelTypeName.FRIEND_REQUEST);
		if (friendRequest1 == null) {
			userRepository.createRelationshipBetween(node2, node1, FriendRequest.class, RelTypeName.FRIEND_REQUEST);
		}
		FriendRequest friendRequest2 = friendRequestService.template().getRelationshipBetween(node4, node1,
				FriendRequest.class, RelTypeName.FRIEND_REQUEST);
		if (friendRequest2 == null) {
			userRepository.createRelationshipBetween(node4, node1, FriendRequest.class, RelTypeName.FRIEND_REQUEST);
		}
		FriendRequest friendRequest3 = friendRequestService.template().getRelationshipBetween(node1, node3,
				FriendRequest.class, RelTypeName.FRIEND_REQUEST);
		if (friendRequest3 == null) {
			userRepository.createRelationshipBetween(node1, node3, FriendRequest.class, RelTypeName.FRIEND_REQUEST);
		}
		boolean bool = false;
		List<FriendRequest> resultList = null;
		resultList = friendRequestService.getFriendRequestToUser(610);
		assertEquals("获取用户关注信息列表失败", resultList.size(), 2);
		bool = resultList.get(0).getStartNode().getUserId() == 611
				|| resultList.get(0).getStartNode().getUserId() == 613;
		assertTrue(bool);
	}
}

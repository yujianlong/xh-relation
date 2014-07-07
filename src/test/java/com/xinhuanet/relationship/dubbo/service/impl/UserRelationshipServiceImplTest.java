package com.xinhuanet.relationship.dubbo.service.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.xinhuanet.relationship.dubbo.model.FollowsRelationship;
import com.xinhuanet.relationship.dubbo.model.FriendRelationship;
import com.xinhuanet.relationship.dubbo.model.FriendRequestRelationship;
import com.xinhuanet.relationship.entity.node.UserNode;
import com.xinhuanet.relationship.entity.relationship.Blacklists;
import com.xinhuanet.relationship.entity.relationship.Blocks;
import com.xinhuanet.relationship.entity.relationship.Follows;
import com.xinhuanet.relationship.entity.relationship.Friend;
import com.xinhuanet.relationship.entity.relationship.FriendRequest;
import com.xinhuanet.relationship.entity.relationship.Obstructs;
import com.xinhuanet.relationship.lang.Lang;
import com.xinhuanet.relationship.repository.UserRepository;
import com.xinhuanet.relationship.service.impl.BlacklistServiceImpl;
import com.xinhuanet.relationship.service.impl.BlockServiceImpl;
import com.xinhuanet.relationship.service.impl.FollowServiceImpl;
import com.xinhuanet.relationship.service.impl.FriendRequestServiceImpl;
import com.xinhuanet.relationship.service.impl.FriendServiceImpl;
import com.xinhuanet.relationship.service.impl.ObstructServiceImpl;

public class UserRelationshipServiceImplTest {
	private static Logger logger = LoggerFactory.getLogger(UserRelationshipServiceImplTest.class);
	private static UserRelationshipServiceImpl userRelationshipService;
	private static ClassPathXmlApplicationContext context;
	private static FriendServiceImpl friendService;
	private static FollowServiceImpl followService;
	private static BlacklistServiceImpl blackService;
	private static BlockServiceImpl blockService;
	private static ObstructServiceImpl obstructService;
	private static FriendRequestServiceImpl friendRequestService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// 获取baseService
		context = new ClassPathXmlApplicationContext(new String[] { "context-main.xml", "context-provider.xml" });
		// context.start();
		userRelationshipService = context.getBean("userRelationshipService", UserRelationshipServiceImpl.class);// new
																												// UserRelationshipServiceImpl();
		friendService = context.getBean("friendService", FriendServiceImpl.class);
		followService = context.getBean("followService", FollowServiceImpl.class);
		blackService = context.getBean("blacklistService", BlacklistServiceImpl.class);
		blockService = context.getBean("blockService", BlockServiceImpl.class);
		obstructService = context.getBean("obstructService", ObstructServiceImpl.class);
		friendRequestService = context.getBean("friendRequestService", FriendRequestServiceImpl.class);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		context.close();
	}

	@Test
	public void testCreateFriendRequest() throws XhrcException {
		// 验证是否存该节点
		UserNode node1 = friendRequestService.userRepository().findByPropertyValue("userId", 204);
		UserNode node2 = friendRequestService.userRepository().findByPropertyValue("userId", 205);
		// 若存在删除它们,存在关系的时候要删除关系
		if (node1 != null && node2 != null) {
			FriendRequest friend = friendRequestService.template().getRelationshipBetween(node1, node2,
					FriendRequest.class, RelTypeName.FRIEND_REQUEST);
			if (friend != null) {
				friendRequestService.template().delete(friend);
			}
		}
		if (node1 != null) {
			friendRequestService.template().delete(node1);
		}
		if (node2 != null) {
			friendRequestService.template().delete(node2);
		}
		// 创建两个用户节点 204.205
		UserNode yjl1 = new UserNode();
		yjl1.setUserId(204);
		yjl1.setCustomFriendGroups(Lang.list("奇闻趣事"));
		friendRequestService.userRepository().save(yjl1);
		UserNode yjl2 = new UserNode();
		yjl2.setUserId(205);
		yjl2.setCustomFriendGroups(Lang.list("喜闻乐见"));
		friendRequestService.userRepository().save(yjl2);
		// 校验是否成功创建
		logger.debug("~~~~~~~~~~UserNode:yjl1:" + JSON.toJSONString(yjl1));
		logger.debug("~~~~~~~~~~UserNode:yjl2:" + JSON.toJSONString(yjl2));
		// 从neo4j获取节点
		UserNode startNode = friendRequestService.userRepository().findByPropertyValue("userId", 204);
		UserNode endNode = friendRequestService.userRepository().findByPropertyValue("userId", 205);
		// 创建两个节点之间的好友关系
		boolean sucess = userRelationshipService.createFriendRequest(204, 205, "dfd");
		// 验证是否创建成功
		assertTrue(sucess);
		// 获取两个节点之间的好友关系
		FriendRequest ifrnd = friendRequestService.userRepository().getRelationshipBetween(startNode, endNode,
				FriendRequest.class, RelTypeName.FRIEND_REQUEST);
		// 验证不空
		assertNotNull(ifrnd);
		logger.debug("~~~~~~~~~~friendrequest:" + JSON.toJSONString(ifrnd));

	}

	@Test
	public void testAcceptFriendRequest() throws XhrcException {
		// 发送好友请求
		UserNode node1 = friendRequestService.userRepository().findByPropertyValue("userId", 202);
		UserNode node2 = friendRequestService.userRepository().findByPropertyValue("userId", 203);
		// 若存在删除它们,存在关系的时候要删除关系
		if (node1 != null && node2 != null) {
			Friend friend = friendRequestService.template().getRelationshipBetween(node1, node2, Friend.class,
					RelTypeName.FRIEND);
			if (friend != null) {
				friendService.template().delete(friend);
			}
		}
		if (node1 != null) {
			friendRequestService.template().delete(node1);
		}
		if (node2 != null) {
			friendRequestService.template().delete(node2);
		}
		// 创建两个用户节点 202.203
		UserNode yjl1 = new UserNode();
		yjl1.setUserId(202);
		yjl1.setCustomFriendGroups(Lang.list("奇闻趣事"));
		friendRequestService.userRepository().save(yjl1);
		UserNode yjl2 = new UserNode();
		yjl2.setUserId(203);
		yjl2.setCustomFriendGroups(Lang.list("喜闻乐见"));
		friendRequestService.userRepository().save(yjl2);
		// 校验是否成功创建
		logger.debug("~~~~~~~~~~UserNode:yjl1:" + JSON.toJSONString(yjl1));
		logger.debug("~~~~~~~~~~UserNode:yjl2:" + JSON.toJSONString(yjl2));
		// 从neo4j获取节点
		UserNode startNode = friendRequestService.userRepository().findByPropertyValue("userId", 202);
		UserNode endNode = friendRequestService.userRepository().findByPropertyValue("userId", 203);
		// 创建两个节点之间的好友关系
		boolean sucess = friendRequestService.createFriendRequest(202, 203, "dfd");
		// 验证是否创建成功
		assertTrue(sucess);
		// 获取两个节点之间的好友关系
		FriendRequest ifrnd = friendRequestService.userRepository().getRelationshipBetween(startNode, endNode,
				FriendRequest.class, RelTypeName.FRIEND_REQUEST);
		// 验证不空
		assertNotNull(ifrnd);
		// 同意
		userRelationshipService.acceptFriendRequest(202, 203, "remarksName", "remarks", false, null);
		// 验证好友关系是否建立
		Friend ifr = friendRequestService.userRepository().getRelationshipBetween(startNode, endNode, Friend.class,
				RelTypeName.FRIEND);
		assertNotNull(ifr);
		logger.debug("~~~~~~~~~~isfriend:" + JSON.toJSONString(ifr));

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
			Friend friend2 = friendService.template().getRelationshipBetween(node2, node1, Friend.class,
					RelTypeName.FRIEND);
			if (friend2 != null) {
				friendService.template().delete(friend2);
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
		Friend ifrnd2 = friendService.template().createRelationshipBetween(endNode, startNode, Friend.class,
				RelTypeName.FRIEND, false);
		// 验证不空
		assertNotNull(ifrnd);
		// 验证不空
		assertNotNull(ifrnd2);
		// 打印json
		logger.debug("~~~~~~~~~~Isfriend:" + JSON.toJSONString(ifrnd));
		// 更新好友关系
		boolean sucess = userRelationshipService.updateFriend(107, 108, "备注名称2", "备注2", false, Lang.list("分组1"));
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
	public void testUpdateFriendNotExist() throws XhrcException {
		// 验证是否存该节点
		UserNode node1 = friendService.userRepository().findByPropertyValue("userId", 119);
		UserNode node2 = friendService.userRepository().findByPropertyValue("userId", 144);
		if (node1 != null) {
			friendService.template().delete(node1);
		}
		if (node2 != null) {
			friendService.template().delete(node2);
		}
		// 创建两个用户节点 119.144
		UserNode yjl1 = new UserNode();
		yjl1.setUserId(119);
		yjl1.setCustomFriendGroups(Lang.list("天下杂谈"));
		friendService.userRepository().save(yjl1);
		UserNode yjl2 = new UserNode();
		yjl2.setUserId(144);
		yjl2.setCustomFriendGroups(Lang.list("商业论坛"));
		friendService.userRepository().save(yjl2);
		// 校验是否成功创建
		logger.debug("~~~~~~~~~~UserNode:yjl1:" + JSON.toJSONString(yjl1));
		logger.debug("~~~~~~~~~~UserNode:yjl2:" + JSON.toJSONString(yjl2));
		// 从neo4j获取节点
		UserNode startNode = friendService.userRepository().findByPropertyValue("userId", 119);
		UserNode endNode = friendService.userRepository().findByPropertyValue("userId", 144);

		// 创建两个节点之间的好友关系
		Friend ifrnd = friendService.template().createRelationshipBetween(startNode, endNode, Friend.class,
				RelTypeName.FRIEND, false);
		Friend ifrnd2 = friendService.template().createRelationshipBetween(endNode, startNode, Friend.class,
				RelTypeName.FRIEND, false);
		// 验证不空
		assertNotNull(ifrnd);
		// 验证不空
		assertNotNull(ifrnd2);
		// 打印json
		logger.debug("~~~~~~~~~~Isfriend:" + JSON.toJSONString(ifrnd));
		// 更新好友关系
		boolean sucess = friendService.updateFriend(119, 144, "备注名称2", "备注2", false, Lang.list("分组1"));
		// 验证是否更新成功
		assertTrue(sucess);
		// 获取两个节点之间的好友关系
		ifrnd = friendService.template().getRelationshipBetween(startNode, endNode, Friend.class, RelTypeName.FRIEND);
		// 验证不空
		assertNotNull(ifrnd);
		// 打印json
		logger.debug("~~~~~~~~~~Isfriend:" + JSON.toJSONString(ifrnd));
		// 删除关系
		friendService.userRepository().deleteRelationshipBetween(startNode, endNode, RelTypeName.FRIEND);
		friendService.userRepository().deleteRelationshipBetween(endNode, startNode, RelTypeName.FRIEND);
		// friendService.template().delete(ifrnd);
		// 再次更新
		Throwable t = null;
		try {
			// 你的方法调用代码
			// 下句仅为示例
			sucess = userRelationshipService.updateFriend(119, 144, "备注名称2", "备注2", false, Lang.list("分组1"));
		} catch (Exception ex) {
			t = ex;
		} finally {
			assertNotNull(t);
			assertTrue(t instanceof XhrcException);
			assertTrue(t.getMessage().contains("更新好友关系失败。startUserId=119；endUserId=144 不存在好友关系"));
		}
	}

	@Test
	public void testCreateFollow() throws XhrcException {
		TestUtils.initUserNode(followService.userRepository(), 660, 661);
		UserNode node3 = followService.userRepository().findByPropertyValue("userId", 109);
		UserNode node4 = followService.userRepository().findByPropertyValue("userId", 110);
		blackService.userRepository().createRelationshipBetween(node3, node4, Blacklists.class, RelTypeName.BLACKLISTS);
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
		boolean sucess = userRelationshipService.createFollow(109, 110, "备注0");
		// 验证是否创建成功
		assertTrue(sucess);
		// 获取两个节点之间的好友关系
		Follows ifrnd = followService.template().getRelationshipBetween(startNode, endNode, Follows.class,
				RelTypeName.FOLLOWS);
		// 验证不空
		assertNotNull(ifrnd);
		// 打印json
		logger.debug("~~~~~~~~~~Follow:" + JSON.toJSONString(ifrnd));
		XhrcException t = null;
		try {
			userRelationshipService.createFollow(660, 661, "Sun of a beach");
		} catch (XhrcException e) {
			t = e;
		} finally {
			assertNotNull(t);
			assertEquals(t.getCause(), "" + 20051);
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
		boolean sucess = userRelationshipService.updateFollow(111, 112, "备注follow1");
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
	public void testUpdateFollowNotExist() throws XhrcException {
		// 验证是否存该节点
		UserNode node1 = followService.userRepository().findByPropertyValue("userId", 115);
		UserNode node2 = followService.userRepository().findByPropertyValue("userId", 116);
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
		// 创建两个用户节点 115.116
		UserNode yjl1 = new UserNode();
		yjl1.setUserId(115);
		yjl1.setCustomFriendGroups(Lang.list("天下杂谈3"));
		followService.userRepository().save(yjl1);
		UserNode yjl2 = new UserNode();
		yjl2.setUserId(116);
		yjl2.setCustomFriendGroups(Lang.list("商业论坛3"));
		followService.userRepository().save(yjl2);
		// 校验是否成功创建
		logger.debug("~~~~~~~~~~UserNode:yjl1:" + JSON.toJSONString(yjl1));
		logger.debug("~~~~~~~~~~UserNode:yjl2:" + JSON.toJSONString(yjl2));
		// 从neo4j获取节点
		UserNode startNode = followService.userRepository().findByPropertyValue("userId", 115);
		UserNode endNode = followService.userRepository().findByPropertyValue("userId", 116);
		// 创建两个节点之间的关注关系
		Follows ifrnd = followService.template().createRelationshipBetween(startNode, endNode, Follows.class,
				RelTypeName.FOLLOWS, false);
		// 验证不空
		assertNotNull(ifrnd);
		// 打印json
		logger.debug("~~~~~~~~~~Follow:" + JSON.toJSONString(ifrnd));
		// 更新关注关系
		boolean sucess = userRelationshipService.updateFollow(115, 116, "备注follow3");
		// 验证是否更新成功
		assertTrue(sucess);
		// 获取两个节点之间的关注关系
		ifrnd = followService.template().getRelationshipBetween(startNode, endNode, Follows.class, RelTypeName.FOLLOWS);
		// 验证不空
		assertNotNull(ifrnd);
		// 打印json
		logger.debug("~~~~~~~~~~Follow:" + JSON.toJSONString(ifrnd));
		// 删除关系
		followService.template().delete(ifrnd);
		// 再次更新
		Throwable t = null;
		try {
			// 你的方法调用代码
			// 下句仅为示例
			sucess = userRelationshipService.updateFollow(115, 116, "备注1");
		} catch (Exception ex) {
			t = ex;
		} finally {
			assertNotNull(t);
			assertTrue(t instanceof XhrcException);
			assertTrue(t.getMessage().contains("更新关注关系失败。startUserId=115， endUserId=116 关注关系不存在"));
		}

	}

	@Test
	public void testCreateBlackList() throws XhrcException {
		TestUtils.initUserNode(blackService.userRepository(), 660, 661);
		// 验证是否存该节点
		UserNode node1 = blackService.userRepository().findByPropertyValue("userId", 145);
		UserNode node2 = blackService.userRepository().findByPropertyValue("userId", 146);
		// 若存在删除它们,存在关系的时候要删除关系
		if (node1 != null && node2 != null) {
			Blacklists inBlacklist = blackService.userRepository().getRelationshipBetween(node1, node2,
					Blacklists.class, RelTypeName.BLACKLISTS);
			if (inBlacklist != null) {
				blackService.template().delete(inBlacklist);
			}

		}
		if (node1 != null) {
			blackService.template().delete(node1);
		}
		if (node2 != null) {
			blackService.template().delete(node2);
		}
		// 创建两个用户节点 145.146
		UserNode yjl1 = new UserNode();
		yjl1.setUserId(145);
		yjl1.setCustomFriendGroups(Lang.list("奇闻趣事"));
		blackService.userRepository().save(yjl1);
		UserNode yjl2 = new UserNode();
		yjl2.setUserId(146);
		yjl2.setCustomFriendGroups(Lang.list("喜闻乐见"));
		blackService.userRepository().save(yjl2);
		// 校验是否成功创建
		logger.debug("~~~~~~~~~~UserNode:yjl1:" + JSON.toJSONString(yjl1));
		logger.debug("~~~~~~~~~~UserNode:yjl2:" + JSON.toJSONString(yjl2));
		// 从neo4j获取节点
		UserNode startNode = blackService.userRepository().findByPropertyValue("userId", 145);
		UserNode endNode = blackService.userRepository().findByPropertyValue("userId", 146);
		// 先建立好友关系
		boolean su = friendService.createFriend(145, 146, "", "", false, null)
				&& friendService.createFriend(146, 145, "", "", false, null);
		// 测试关注
		followService.createFollow(145, 146, "follow");
		// 屏蔽
		blockService.createBlock(145, 146);
		// 阻止
		obstructService.createObstruct(145, 146);
		assertTrue(su);
		boolean sucess = userRelationshipService.createBlacklist(145, 146);
		assertTrue(sucess);
		Blacklists iblst = blackService.template().getRelationshipBetween(startNode, endNode, Blacklists.class,
				RelTypeName.BLACKLISTS);
		assertNotNull(iblst);
		logger.debug("~~~~~~~~~~InBlacklist:" + JSON.toJSONString(iblst));
		XhrcException t = null;
		try {
			userRelationshipService.createBlacklist(661, 660);
		} catch (XhrcException e) {
			t = e;
			assertEquals(e.getErrorCode(), 20052 + "");
		} finally {
			assertNotNull(t);
			assertEquals(t.getErrorCode(), "" + 20052);
		}

	}

	@Test
	public void testCreateBlock() throws XhrcException {
		// 验证是否存该节点
		UserNode node1 = blockService.userRepository().findByPropertyValue("userId", 147);
		UserNode node2 = blockService.userRepository().findByPropertyValue("userId", 148);
		// 若存在删除它们,存在关系的时候要删除关系
		if (node1 != null && node2 != null) {
			Blocks blocklist = blockService.userRepository().getRelationshipBetween(node1, node2, Blocks.class,
					RelTypeName.BLOCKS);
			if (blocklist != null) {
				blockService.template().delete(blocklist);
			}
			Follows follow = followService.template().getRelationshipBetween(node1, node2, Follows.class,
					RelTypeName.FOLLOWS);
			if (follow != null) {
				followService.template().delete(follow);
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
		// 先创建好友或者关注关系
		boolean su = followService.createFollow(147, 148, "关注1");
		assertTrue(su);
		// 阻止
		boolean sucess = userRelationshipService.createBlock(147, 148);
		assertTrue(sucess);
		Blocks blocklst = blockService.template().getRelationshipBetween(startNode, endNode, Blocks.class,
				RelTypeName.BLOCKS);
		assertNotNull(blocklst);
		logger.debug("~~~~~~~~~~Blocklist:" + JSON.toJSONString(blocklst));

	}

	@Test
	public void testCreateBlockExist() throws XhrcException {
		// 验证是否存该节点
		UserNode node1 = blockService.userRepository().findByPropertyValue("userId", 147);
		UserNode node2 = blockService.userRepository().findByPropertyValue("userId", 148);
		// 若存在删除它们,存在关系的时候要删除关系
		if (node1 != null && node2 != null) {
			Blocks blocklist = blockService.userRepository().getRelationshipBetween(node1, node2, Blocks.class,
					RelTypeName.BLOCKS);
			if (blocklist != null) {
				blockService.template().delete(blocklist);
			}
			Follows follow = followService.template().getRelationshipBetween(node1, node2, Follows.class,
					RelTypeName.FOLLOWS);
			if (follow != null) {
				followService.template().delete(follow);
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
		boolean su = followService.createFollow(147, 148, "关注1");
		assertTrue(su);
		// 阻止
		boolean sucess = userRelationshipService.createBlock(147, 148);
		assertTrue(sucess);

		// 异常测试，不存在关系 直接加阻止。
		TestUtils.initUserNode(blockService.userRepository(), 149, 150);
		Throwable t = null;
		try {
			userRelationshipService.createBlock(149, 150);
		} catch (Exception e) {
			t = e;
		} finally {
			assertNotNull(t);
			assertTrue(t instanceof XhrcException);
			assertTrue(t.getMessage().contains("不存在好友或者关注关系，无法屏蔽"));
		}
	}

	@Test
	public void testDeleteBlock() throws XhrcException {
		// 创建测试数据
		UserNode node1 = blockService.getUserNode(256);
		UserNode node2 = blockService.getUserNode(257);
		UserNode node3 = blockService.getUserNode(258);
		Blocks bk1 = blockService.template().getRelationshipBetween(node1, node2, Blocks.class, RelTypeName.BLOCKS);
		Blocks bk2 = blockService.template().getRelationshipBetween(node1, node3, Blocks.class, RelTypeName.BLOCKS);
		if (bk1 == null) {
			bk1 = blockService.userRepository().createRelationshipBetween(node1, node2, Blocks.class,
					RelTypeName.BLOCKS);
		}
		if (bk2 != null) {
			blockService.userRepository().deleteRelationshipBetween(node1, node3, RelTypeName.BLOCKS);
		}

		// 测试开始 1.存在阻止关系 请求删除 2.不存在阻止关系 请求删除
		boolean bool = false;
		bool = userRelationshipService.deleteBlock(node1.getUserId(), node2.getUserId());
		// 成功返回true
		assertTrue(bool);
		Blocks blk1 = blockService.userRepository().getRelationshipBetween(node1, node2, Blocks.class,
				RelTypeName.BLOCKS);
		// 不存在阻止关系
		assertNull(blk1);

		bool = userRelationshipService.deleteBlock(node1.getUserId(), node3.getUserId());
		// 成功返回true
		assertTrue(bool);
		Blocks blk2 = blockService.userRepository().getRelationshipBetween(node1, node3, Blocks.class,
				RelTypeName.BLOCKS);
		// 不存在阻止关系
		assertNull(blk2);
	}

	@Test
	public void testDeleteObstruct() throws XhrcException {
		// 创建测试数据
		UserNode node1 = obstructService.getUserNode(256);
		UserNode node2 = obstructService.getUserNode(257);
		UserNode node3 = obstructService.getUserNode(258);
		UserRepository userRepository = obstructService.userRepository();
		Obstructs os1 = userRepository.getRelationshipBetween(node1, node2, Obstructs.class, RelTypeName.OBSTRUCTS);
		Obstructs os2 = userRepository.getRelationshipBetween(node1, node3, Obstructs.class, RelTypeName.OBSTRUCTS);
		if (os1 == null) {
			os1 = userRepository.createRelationshipBetween(node1, node2, Obstructs.class, RelTypeName.OBSTRUCTS);
		}
		if (os2 != null) {
			userRepository.deleteRelationshipBetween(node1, node3, RelTypeName.OBSTRUCTS);
		}

		// 测试开始 1.存在阻止 请求删除 2.不存在阻止 请求删除
		boolean bool = false;
		bool = userRelationshipService.deleteObstruct(node1.getUserId(), node2.getUserId());
		// 成功返回true
		assertTrue(bool);
		Obstructs obs1 = userRepository.getRelationshipBetween(node1, node2, Obstructs.class, RelTypeName.OBSTRUCTS);
		// 不存在阻止
		assertNull(obs1);

		bool = userRelationshipService.deleteObstruct(node1.getUserId(), node3.getUserId());
		// 成功返回true
		assertTrue(bool);
		Obstructs obs2 = userRepository.getRelationshipBetween(node1, node3, Obstructs.class, RelTypeName.OBSTRUCTS);
		// 不存在阻止
		assertNull(obs2);
	}

	@Test
	public void testDeleteFollow() throws XhrcException {
		// 创建测试关系数据
		prepareData4testDeleteFollow();

		UserRepository userRepository = followService.userRepository();
		UserNode node1 = followService.getUserNode(251);
		UserNode node2 = followService.getUserNode(252);
		UserNode node3 = followService.getUserNode(253);
		UserNode node4 = followService.getUserNode(254);
		// 成功为 true 不成功为false
		boolean bool = false;
		bool = userRelationshipService.deleteFollow(node1.getUserId(), node2.getUserId());
		// 验证是否删除成功
		assertTrue(bool);
		Follows flw1 = userRepository.getRelationshipBetween(node1, node2, Follows.class, RelTypeName.FOLLOWS);
		Blocks blk = userRepository.getRelationshipBetween(node1, node2, Blocks.class, RelTypeName.BLOCKS);
		// 验证关注关系删除是否成功
		assertNull(flw1);
		// 有好友关系时 屏蔽关系不删除
		assertNotNull(blk);

		bool = userRelationshipService.deleteFollow(node3.getUserId(), node4.getUserId());
		// 验证是否删除成功
		assertTrue(bool);
		Follows flw2 = userRepository.getRelationshipBetween(node3, node4, Follows.class, RelTypeName.FOLLOWS);
		Obstructs obs = userRepository.getRelationshipBetween(node3, node4, Obstructs.class, RelTypeName.OBSTRUCTS);
		// 验证关注关系删除是否成功
		assertNull(flw2);
		// 无好友关系时 阻止关系删除
		assertNull(obs);
	}

	/**************** 创建测试删除关注关系数据 *********************/
	public void prepareData4testDeleteFollow() throws XhrcException {
		// 查找4个节点 251，252，253， 254 不存在则创建
		UserRepository userRepository = followService.userRepository();
		UserNode node1 = followService.getUserNode(251);
		UserNode node2 = followService.getUserNode(252);
		UserNode node3 = followService.getUserNode(253);
		UserNode node4 = followService.getUserNode(254);

		// 创建两对关注关系 一对有好友关系一对没有
		Follows follow1 = userRepository.getRelationshipBetween(node1, node2, Follows.class, RelTypeName.FOLLOWS);
		if (follow1 == null) {
			userRepository.createRelationshipBetween(node1, node2, Follows.class, RelTypeName.FOLLOWS);
		}
		Follows follow2 = userRepository.getRelationshipBetween(node3, node4, Follows.class, RelTypeName.FOLLOWS);
		if (follow2 == null) {
			userRepository.createRelationshipBetween(node3, node4, Follows.class, RelTypeName.FOLLOWS);
		}

		// node1 node2创建好友关系
		Friend isf1 = userRepository.getRelationshipBetween(node1, node2, Friend.class, RelTypeName.FRIEND);
		if (isf1 == null) {
			userRepository.createRelationshipBetween(node1, node2, Friend.class, RelTypeName.FRIEND);
		}
		Friend isf2 = userRepository.getRelationshipBetween(node2, node1, Friend.class, RelTypeName.FRIEND);
		if (isf2 == null) {
			userRepository.createRelationshipBetween(node2, node1, Friend.class, RelTypeName.FRIEND);
		}
		// node3 node4删除好友关系
		Friend isf3 = userRepository.getRelationshipBetween(node3, node4, Friend.class, RelTypeName.FRIEND);
		if (isf3 != null) {
			userRepository.deleteRelationshipBetween(node3, node4, RelTypeName.FRIEND);
		}
		Friend isf4 = userRepository.getRelationshipBetween(node4, node3, Friend.class, RelTypeName.FRIEND);
		if (isf4 != null) {
			userRepository.deleteRelationshipBetween(node4, node3, RelTypeName.FRIEND);
		}

		// node1 nond2创建屏蔽 node3 node4创建阻止
		Blocks blk = userRepository.getRelationshipBetween(node1, node2, Blocks.class, RelTypeName.BLOCKS);
		if (blk == null) {
			userRepository.createRelationshipBetween(node1, node2, Blocks.class, RelTypeName.BLOCKS);
		}
		Obstructs obs = userRepository.getRelationshipBetween(node3, node4, Obstructs.class, RelTypeName.OBSTRUCTS);
		if (obs == null) {
			userRepository.createRelationshipBetween(node3, node4, Obstructs.class, RelTypeName.OBSTRUCTS);
		}

	}

	@Test
	public void testDeleteFriendBetween() throws XhrcException {
		// 创建测试关系数据
		prepareData4testDeleteFriendBetween();

		UserRepository userRepository = followService.userRepository();
		UserNode node1 = followService.getUserNode(251);
		UserNode node2 = followService.getUserNode(252);
		UserNode node3 = followService.getUserNode(253);
		UserNode node4 = followService.getUserNode(254);

		// 成功为 true 不成功为false
		boolean bool = false;
		bool = userRelationshipService.deleteFriendBetween(node1.getUserId(), node2.getUserId());
		// 验证是否删除成功
		assertTrue(bool);
		Friend isf1 = userRepository.getRelationshipBetween(node1, node2, Friend.class, RelTypeName.FRIEND);
		Blocks blk = userRepository.getRelationshipBetween(node1, node2, Blocks.class, RelTypeName.BLOCKS);
		// 验证关注关系删除是否成功
		assertNull(isf1);
		// 有好友关系时 屏蔽关系不删除
		assertNotNull(blk);

		bool = userRelationshipService.deleteFriendBetween(node3.getUserId(), node4.getUserId());
		// 验证是否删除成功
		assertTrue(bool);
		Friend isf2 = userRepository.getRelationshipBetween(node3, node4, Friend.class, RelTypeName.FRIEND);
		Obstructs obs = userRepository.getRelationshipBetween(node3, node4, Obstructs.class, RelTypeName.OBSTRUCTS);
		// 验证关注关系删除是否成功
		assertNull(isf2);
		// 无好友关系时 阻止关系删除
		assertNull(obs);
	}

	/************* 创建测试删除好友关系数据 ****************/
	public void prepareData4testDeleteFriendBetween() throws XhrcException {
		// 查找4个节点 251，252，253， 254 不存在则创建
		UserRepository userRepository = followService.userRepository();
		UserNode node1 = followService.getUserNode(251);
		UserNode node2 = followService.getUserNode(252);
		UserNode node3 = followService.getUserNode(253);
		UserNode node4 = followService.getUserNode(254);

		// 创建两对好友关系 一对有关注关系一对没有
		Friend isf1 = userRepository.getRelationshipBetween(node1, node2, Friend.class, RelTypeName.FRIEND);
		if (isf1 == null) {
			userRepository.createRelationshipBetween(node1, node2, Friend.class, RelTypeName.FRIEND);
		}
		Friend isf2 = userRepository.getRelationshipBetween(node2, node1, Friend.class, RelTypeName.FRIEND);
		if (isf2 == null) {
			userRepository.createRelationshipBetween(node2, node1, Friend.class, RelTypeName.FRIEND);
		}
		Friend isf3 = userRepository.getRelationshipBetween(node3, node4, Friend.class, RelTypeName.FRIEND);
		if (isf3 == null) {
			userRepository.createRelationshipBetween(node3, node4, Friend.class, RelTypeName.FRIEND);
		}
		Friend isf4 = userRepository.getRelationshipBetween(node4, node3, Friend.class, RelTypeName.FRIEND);
		if (isf4 == null) {
			userRepository.createRelationshipBetween(node4, node3, Friend.class, RelTypeName.FRIEND);
		}

		// node1 node2创建关注关系
		Follows follow1 = userRepository.getRelationshipBetween(node1, node2, Follows.class, RelTypeName.FOLLOWS);
		if (follow1 == null) {
			userRepository.createRelationshipBetween(node1, node2, Follows.class, RelTypeName.FOLLOWS);
		}
		// 如果node3 node4 间有关注关系 删除
		Follows follow2 = userRepository.getRelationshipBetween(node3, node4, Follows.class, RelTypeName.FOLLOWS);
		if (follow2 != null) {
			userRepository.deleteRelationshipBetween(node3, node4, RelTypeName.FOLLOWS);
		}

		// node1 nond2创建屏蔽 node3 node4创建阻止
		Blocks blk = userRepository.getRelationshipBetween(node1, node2, Blocks.class, RelTypeName.BLOCKS);
		if (blk == null) {
			userRepository.createRelationshipBetween(node1, node2, Blocks.class, RelTypeName.BLOCKS);
		}
		Obstructs obs = userRepository.getRelationshipBetween(node3, node4, Obstructs.class, RelTypeName.OBSTRUCTS);
		if (obs == null) {
			userRepository.createRelationshipBetween(node3, node4, Obstructs.class, RelTypeName.OBSTRUCTS);
		}

	}

	@Test
	public void testCreateObstruct() throws XhrcException {
		TestUtils.initUserNode(obstructService.userRepository(), 660, 661);
		// 验证是否存该节点
		UserNode node1 = obstructService.userRepository().findByPropertyValue("userId", 201);
		UserNode node2 = obstructService.userRepository().findByPropertyValue("userId", 202);
		UserRepository userRepository = obstructService.userRepository();
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
		// 创建两个节点之间的关注关系
		Follows ifrnd = followService.template().createRelationshipBetween(endNode, startNode, Follows.class,
				RelTypeName.FOLLOWS, false);
		// 验证不空
		assertNotNull(ifrnd);

		boolean sucess = userRelationshipService.createObstruct(201, 202);
		assertTrue(sucess);
		Obstructs obs = obstructService.template().getRelationshipBetween(startNode, endNode, Obstructs.class,
				RelTypeName.OBSTRUCTS);
		assertNotNull(obs);
		logger.debug("~~~~~~~~~~Blocklist:" + JSON.toJSONString(obs));
		XhrcException t = null;
		try {
			userRelationshipService.createObstruct(660, 661);
		} catch (XhrcException e) {
			t = e;
		} finally {
			assertNotNull(t);
			assertEquals(t.getErrorCode(), "" + 20052);
		}

	}

	@Test
	public void testGetUserFriends() throws XhrcException {
		TestUtils.initUserNode(friendService.userRepository(), 621, 622, 624, 620);
		UserNode node1 = friendService.userRepository().findByPropertyValue("userId", 621);
		UserNode node2 = friendService.userRepository().findByPropertyValue("userId", 622);

		UserNode node4 = friendService.userRepository().findByPropertyValue("userId", 624);
		UserNode node5 = friendService.userRepository().findByPropertyValue("userId", 620);
		// node1与node2互为好友，互为同事
		Friend friend10 = friendService.template().getRelationshipBetween(node1, node2, Friend.class,
				RelTypeName.FRIEND);
		Friend friend11 = friendService.template().getRelationshipBetween(node2, node1, Friend.class,
				RelTypeName.FRIEND);
		if (friend10 == null) {
			friend10 = friendService.template().createRelationshipBetween(node1, node2, Friend.class,
					RelTypeName.FRIEND, false);
			List<String> friendGroup = new ArrayList<String>();
			friendGroup.add("同事");
			friend10.setFriendGroups(friendGroup);
			friendService.template().save(friend10);
		} else {
			if (friend10.getFriendGroups() == null || !friend10.getFriendGroups().contains("同事")) {
				List<String> friendGroup = new ArrayList<String>();
				friendGroup.add("同事");
				friend10.setFriendGroups(friendGroup);
				friendService.template().save(friend10);
			}
		}
		if (friend11 == null) {
			friend11 = friendService.template().createRelationshipBetween(node2, node1, Friend.class,
					RelTypeName.FRIEND, false);
			List<String> friendGroup = new ArrayList<String>();
			friendGroup.add("同事");
			friend11.setFriendGroups(friendGroup);
			friendService.template().save(friend11);
		} else {
			if (friend11.getFriendGroups() == null || !friend11.getFriendGroups().contains("同事")) {
				List<String> friendGroup = new ArrayList<String>();
				friendGroup.add("同事");
				friend11.setFriendGroups(friendGroup);
				friendService.template().save(friend11);
			}
		}
		// node1与node4互为好友，node1是node4但node4不是node1的同事
		Friend friend20 = friendService.template().getRelationshipBetween(node1, node4, Friend.class,
				RelTypeName.FRIEND);
		Friend friend21 = friendService.template().getRelationshipBetween(node4, node1, Friend.class,
				RelTypeName.FRIEND);
		if (friend20 == null) {
			friend20 = friendService.template().createRelationshipBetween(node1, node4, Friend.class,
					RelTypeName.FRIEND, false);
			friendService.template().save(friend20);
		} else {
			if (friend20.getFriendGroups() != null && friend20.getFriendGroups().contains("同事")) {
				List<String> friendGroup = new ArrayList<String>();
				friendGroup = friend20.getFriendGroups();
				friendGroup.remove("同事");
				friend20.setFriendGroups(friendGroup);
				friendService.template().save(friend20);
			}
		}
		if (friend21 == null) {
			friend21 = friendService.template().createRelationshipBetween(node4, node1, Friend.class,
					RelTypeName.FRIEND, false);
			List<String> friendGroup = new ArrayList<String>();
			friendGroup.add("同事");
			friend21.setFriendGroups(friendGroup);
			friendService.template().save(friend21);
		} else {
			if (friend21.getFriendGroups() == null || !friend21.getFriendGroups().contains("同事")) {
				List<String> friendGroup = new ArrayList<String>();
				friendGroup.add("同事");
				friend21.setFriendGroups(friendGroup);
				friendService.template().save(friend21);
			}
		}

		// node1与node5互为好友，node5是node1的同事，但node1不是node5的同事
		Friend friend40 = friendService.template().getRelationshipBetween(node1, node5, Friend.class,
				RelTypeName.FRIEND);
		Friend friend41 = friendService.template().getRelationshipBetween(node5, node1, Friend.class,
				RelTypeName.FRIEND);
		if (friend40 == null) {
			friend40 = friendService.template().createRelationshipBetween(node1, node5, Friend.class,
					RelTypeName.FRIEND, false);
			List<String> friendGroup = new ArrayList<String>();
			friendGroup.add("同事");
			friend40.setFriendGroups(friendGroup);
			friendService.template().save(friend40);
		} else {
			if (friend40.getFriendGroups() == null || !friend40.getFriendGroups().contains("同事")) {
				List<String> friendGroup = new ArrayList<String>();
				friendGroup.add("同事");
				friend40.setFriendGroups(friendGroup);
				friendService.template().save(friend40);
			}
		}
		if (friend41 == null) {
			friend41 = friendService.template().createRelationshipBetween(node5, node1, Friend.class,
					RelTypeName.FRIEND, false);
			friendService.template().save(friend41);
		} else {
			if (friend41.getFriendGroups() != null && friend41.getFriendGroups().contains("同事")) {
				List<String> friendGroup = new ArrayList<String>();
				friendGroup = friend41.getFriendGroups();
				friendGroup.remove("同事");
				friend41.setFriendGroups(friendGroup);
				friendService.template().save(friend41);
			}
		}
		boolean bool = false;
		List<FriendRelationship> resultList = null;
		resultList = userRelationshipService.getUserFriends(621, "同事", null, null);
		assertEquals("获取好友id数量错误", resultList.size(), 2);
		bool = resultList.get(0).getTargetUserId() == 622 || resultList.get(0).getTargetUserId() == 620;
		assertTrue(bool);
		bool = resultList.get(1).getTargetUserId() == 622 || resultList.get(1).getTargetUserId() == 620;
		assertTrue(bool);
		resultList = userRelationshipService.getUserFriends(621, null, null, null);
		assertEquals("获取用户好友id数目有误", resultList.size(), 3);

	}

	@Test
	public void testGetUserFollows() throws XhrcException {
		TestUtils.initUserNode(followService.userRepository(), 631, 632, 633, 634);
		UserNode node1 = followService.userRepository().findByPropertyValue("userId", 631);
		UserNode node2 = followService.userRepository().findByPropertyValue("userId", 632);
		UserNode node3 = followService.userRepository().findByPropertyValue("userId", 633);
		UserNode node4 = followService.userRepository().findByPropertyValue("userId", 634);
		// 判断若不存在关系，则创建关系，node1关注node2和node4，node4 关注node1，node2关注node3
		Friend follow1 = followService.template().getRelationshipBetween(node1, node2, Friend.class,
				RelTypeName.FOLLOWS);
		if (follow1 == null) {
			followService.userRepository().createRelationshipBetween(node1, node2, Follows.class, RelTypeName.FOLLOWS);
		}
		Friend follow2 = followService.template().getRelationshipBetween(node1, node4, Friend.class,
				RelTypeName.FOLLOWS);
		if (follow2 == null) {
			followService.userRepository().createRelationshipBetween(node1, node4, Follows.class, RelTypeName.FOLLOWS);
			followService.userRepository().createRelationshipBetween(node4, node1, Follows.class, RelTypeName.FOLLOWS);
		}
		Friend follow3 = followService.template()
				.getRelationshipBetween(node2, node3, Friend.class, RelTypeName.FRIEND);
		if (follow3 == null) {
			followService.userRepository().createRelationshipBetween(node2, node3, Follows.class, RelTypeName.FOLLOWS);
		}
		boolean bool = false;
		List<FollowsRelationship> resultList = null;
		resultList = userRelationshipService.getUserFollows(631, null, null);
		assertEquals("获取关注数量错误", resultList.size(), 2);
		bool = resultList.get(0).getTargetUserId() == 632 || resultList.get(0).getTargetUserId() == 634;
		assertTrue(bool);
	}

	@Test
	public void testGetUserBlocks() throws XhrcException {
		TestUtils.initUserNode(blockService.userRepository(), 680, 681, 682, 683);
		UserNode node1 = blockService.userRepository().findByPropertyValue("userId", 680);
		UserNode node2 = blockService.userRepository().findByPropertyValue("userId", 681);

		UserNode node3 = blockService.userRepository().findByPropertyValue("userId", 682);
		UserNode node4 = blockService.userRepository().findByPropertyValue("userId", 683);
		// 判断若不存在关系，则创建关系，node1屏蔽node2和node4，node4 屏蔽node1，node2屏蔽node3
		Blocks block1 = blockService.userRepository().getRelationshipBetween(node1, node2, Blocks.class,
				RelTypeName.BLOCKS);
		if (block1 == null) {
			blockService.userRepository().createRelationshipBetween(node1, node2, Blocks.class, RelTypeName.BLOCKS);
		}
		Friend block2 = blockService.userRepository().getRelationshipBetween(node1, node4, Friend.class,
				RelTypeName.BLOCKS);
		if (block2 == null) {
			blockService.userRepository().createRelationshipBetween(node1, node4, Blocks.class, RelTypeName.BLOCKS);
			blockService.userRepository().createRelationshipBetween(node4, node1, Blocks.class, RelTypeName.BLOCKS);
		}
		Blocks block3 = blockService.userRepository().getRelationshipBetween(node2, node3, Blocks.class,
				RelTypeName.BLOCKS);
		if (block3 == null) {
			blockService.userRepository().createRelationshipBetween(node2, node3, Blocks.class, RelTypeName.BLOCKS);
		}
		boolean bool = false;
		List<Integer> resultList = null;
		resultList = userRelationshipService.getUserBlocks(680);
		assertEquals("获取屏蔽数量错误", resultList.size(), 2);
		bool = resultList.get(0) == 681 || resultList.get(0) == 683;
		assertTrue(bool);
		resultList = userRelationshipService.getUserBlocks(681);
		assertEquals("获取用户屏蔽错误", resultList.get(0).intValue(), 682);
	}

	@Test
	public void testCountUserBlocks() throws XhrcException {
		TestUtils.initUserNode(blockService.userRepository(), 680, 681, 682, 683);
		UserNode node1 = blockService.userRepository().findByPropertyValue("userId", 680);
		UserNode node2 = blockService.userRepository().findByPropertyValue("userId", 681);

		UserNode node3 = blockService.userRepository().findByPropertyValue("userId", 682);
		UserNode node4 = blockService.userRepository().findByPropertyValue("userId", 683);
		// 判断若不存在关系，则创建关系，node1屏蔽node2和node4，node4 屏蔽node1，node2屏蔽node3
		Blocks block1 = blockService.userRepository().getRelationshipBetween(node1, node2, Blocks.class,
				RelTypeName.BLOCKS);
		if (block1 == null) {
			blockService.userRepository().createRelationshipBetween(node1, node2, Blocks.class, RelTypeName.BLOCKS);
		}
		Friend block2 = blockService.userRepository().getRelationshipBetween(node1, node4, Friend.class,
				RelTypeName.BLOCKS);
		if (block2 == null) {
			blockService.userRepository().createRelationshipBetween(node1, node4, Blocks.class, RelTypeName.BLOCKS);
			blockService.userRepository().createRelationshipBetween(node4, node1, Blocks.class, RelTypeName.BLOCKS);
		}
		Blocks block3 = blockService.userRepository().getRelationshipBetween(node2, node3, Blocks.class,
				RelTypeName.BLOCKS);
		if (block3 == null) {
			blockService.userRepository().createRelationshipBetween(node2, node3, Blocks.class, RelTypeName.BLOCKS);
		}
		assertEquals("获取屏蔽数量错误", userRelationshipService.countUserBlocks(680), 2);
		assertEquals("获取屏蔽数量错误", userRelationshipService.countUserBlocks(681), 1);
	}

	@Test
	public void testGetUserBlacklists() throws XhrcException {
		TestUtils.initUserNode(blackService.userRepository(), 685, 686, 687, 688);
		UserNode node1 = blackService.userRepository().findByPropertyValue("userId", 685);
		UserNode node2 = blackService.userRepository().findByPropertyValue("userId", 686);

		UserNode node3 = blackService.userRepository().findByPropertyValue("userId", 687);
		UserNode node4 = blackService.userRepository().findByPropertyValue("userId", 688);
		// 判断若不存在关系，则创建关系，node1阻止node2和node4，node4 阻止node1，node2阻止node3
		Blacklists inBlacklist1 = blackService.userRepository().getRelationshipBetween(node1, node2, Blacklists.class,
				RelTypeName.BLACKLISTS);
		if (inBlacklist1 == null) {
			blackService.userRepository().createRelationshipBetween(node1, node2, Blacklists.class,
					RelTypeName.BLACKLISTS);
		}
		Friend inBlacklist2 = blackService.userRepository().getRelationshipBetween(node1, node4, Friend.class,
				RelTypeName.BLACKLISTS);
		if (inBlacklist2 == null) {
			blackService.userRepository().createRelationshipBetween(node1, node4, Blacklists.class,
					RelTypeName.BLACKLISTS);
			blackService.userRepository().createRelationshipBetween(node4, node1, Blacklists.class,
					RelTypeName.BLACKLISTS);
		}
		Blacklists inBlacklist3 = blackService.userRepository().getRelationshipBetween(node2, node3, Blacklists.class,
				RelTypeName.BLACKLISTS);
		if (inBlacklist3 == null) {
			blackService.userRepository().createRelationshipBetween(node2, node3, Blacklists.class,
					RelTypeName.BLACKLISTS);
		}
		boolean bool = false;
		List<Integer> resultList = null;
		resultList = userRelationshipService.getUserBlacklists(685);
		assertEquals("获取黑名单数量错误", resultList.size(), 2);
		bool = resultList.get(0) == 686 || resultList.get(0) == 688;
		assertTrue(bool);
		resultList = userRelationshipService.getUserBlacklists(686);
		assertEquals("获取黑名单用户错误", resultList.get(0).intValue(), 687);
	}

	@Test
	public void testCountUserBlacklists() throws XhrcException {
		TestUtils.initUserNode(blackService.userRepository(), 685, 686, 687, 688);
		UserNode node1 = blackService.userRepository().findByPropertyValue("userId", 685);
		UserNode node2 = blackService.userRepository().findByPropertyValue("userId", 686);

		UserNode node3 = blackService.userRepository().findByPropertyValue("userId", 687);
		UserNode node4 = blackService.userRepository().findByPropertyValue("userId", 688);
		// 判断若不存在关系，则创建关系，node1阻止node2和node4，node4 阻止node1，node2阻止node3
		Blacklists inBlacklist1 = blackService.userRepository().getRelationshipBetween(node1, node2, Blacklists.class,
				RelTypeName.BLACKLISTS);
		if (inBlacklist1 == null) {
			blackService.userRepository().createRelationshipBetween(node1, node2, Blacklists.class,
					RelTypeName.BLACKLISTS);
		}
		Friend inBlacklist2 = blackService.userRepository().getRelationshipBetween(node1, node4, Friend.class,
				RelTypeName.BLACKLISTS);
		if (inBlacklist2 == null) {
			blackService.userRepository().createRelationshipBetween(node1, node4, Blacklists.class,
					RelTypeName.BLACKLISTS);
			blackService.userRepository().createRelationshipBetween(node4, node1, Blacklists.class,
					RelTypeName.BLACKLISTS);
		}
		Blacklists inBlacklist3 = blackService.userRepository().getRelationshipBetween(node2, node3, Blacklists.class,
				RelTypeName.BLACKLISTS);
		if (inBlacklist3 == null) {
			blackService.userRepository().createRelationshipBetween(node2, node3, Blacklists.class,
					RelTypeName.BLACKLISTS);
		}

		assertEquals("获取黑名单数量错误", userRelationshipService.countUserBlacklists(685), 2);
		assertEquals("获取黑名单数量错误", userRelationshipService.countUserBlacklists(686), 1);
	}

	@Test
	public void testGetUserObstructs() throws XhrcException {
		TestUtils.initUserNode(obstructService.userRepository(), 600, 601, 602, 603);
		UserNode node1 = obstructService.userRepository().findByPropertyValue("userId", 600);
		UserNode node2 = obstructService.userRepository().findByPropertyValue("userId", 601);

		UserNode node3 = obstructService.userRepository().findByPropertyValue("userId", 602);
		UserNode node4 = obstructService.userRepository().findByPropertyValue("userId", 603);
		// 判断若不存在关系，则创建关系，node1阻止node2和node4，node3 阻止node1，node2阻止node3
		Obstructs obstruct1 = obstructService.userRepository().getRelationshipBetween(node1, node2, Obstructs.class,
				RelTypeName.OBSTRUCTS);
		if (obstruct1 == null) {
			obstructService.userRepository().createRelationshipBetween(node1, node2, Obstructs.class,
					RelTypeName.OBSTRUCTS);
		}
		Friend obstruct2 = obstructService.userRepository().getRelationshipBetween(node1, node4, Friend.class,
				RelTypeName.OBSTRUCTS);
		if (obstruct2 == null) {
			obstructService.userRepository().createRelationshipBetween(node1, node4, Obstructs.class,
					RelTypeName.OBSTRUCTS);
		}
		Obstructs obstruct3 = obstructService.userRepository().getRelationshipBetween(node3, node1, Obstructs.class,
				RelTypeName.OBSTRUCTS);
		if (obstruct3 == null) {
			obstructService.userRepository().createRelationshipBetween(node3, node1, Obstructs.class,
					RelTypeName.OBSTRUCTS);
		}
		Obstructs obstruct4 = obstructService.userRepository().getRelationshipBetween(node2, node3, Obstructs.class,
				RelTypeName.OBSTRUCTS);
		if (obstruct4 == null) {
			obstructService.userRepository().createRelationshipBetween(node2, node3, Obstructs.class,
					RelTypeName.OBSTRUCTS);
		}
		boolean bool = false;
		List<Integer> resultList = null;
		resultList = userRelationshipService.getUserObstructs(600);
		assertEquals("获取阻止数量错误", resultList.size(), 2);
		bool = resultList.get(0) == 601 || resultList.get(0) == 603;
		assertTrue(bool);
		resultList = userRelationshipService.getUserObstructs(601);
		assertEquals("获取用户阻止错误", resultList.get(0).intValue(), 602);
	}

	@Test
	public void testCountUserObstructs() throws XhrcException {
		TestUtils.initUserNode(obstructService.userRepository(), 605, 606, 607, 608);
		UserNode node1 = obstructService.userRepository().findByPropertyValue("userId", 605);
		UserNode node2 = obstructService.userRepository().findByPropertyValue("userId", 606);

		UserNode node3 = obstructService.userRepository().findByPropertyValue("userId", 607);
		UserNode node4 = obstructService.userRepository().findByPropertyValue("userId", 608);
		// 判断若不存在关系，则创建关系，node1阻止node2和node4，node3 阻止node1，node2阻止node3
		Obstructs obstruct1 = obstructService.userRepository().getRelationshipBetween(node1, node2, Obstructs.class,
				RelTypeName.OBSTRUCTS);
		if (obstruct1 == null) {
			obstructService.userRepository().createRelationshipBetween(node1, node2, Obstructs.class,
					RelTypeName.OBSTRUCTS);
		}
		Friend obstruct2 = obstructService.userRepository().getRelationshipBetween(node1, node4, Friend.class,
				RelTypeName.OBSTRUCTS);
		if (obstruct2 == null) {
			obstructService.userRepository().createRelationshipBetween(node1, node4, Obstructs.class,
					RelTypeName.OBSTRUCTS);
		}
		Obstructs obstruct3 = obstructService.userRepository().getRelationshipBetween(node3, node1, Obstructs.class,
				RelTypeName.OBSTRUCTS);
		if (obstruct3 == null) {
			obstructService.userRepository().createRelationshipBetween(node3, node1, Obstructs.class,
					RelTypeName.OBSTRUCTS);
		}
		Obstructs obstruct4 = obstructService.userRepository().getRelationshipBetween(node2, node3, Obstructs.class,
				RelTypeName.OBSTRUCTS);
		if (obstruct4 == null) {
			obstructService.userRepository().createRelationshipBetween(node2, node3, Obstructs.class,
					RelTypeName.OBSTRUCTS);
		}
		assertEquals("获取阻止数量错误", userRelationshipService.countUserObstructs(605), 2);
		assertEquals("获取阻止数量错误", userRelationshipService.countUserObstructs(606), 1);
	}

	@Test
	public void testGetFollowsToUser() throws XhrcException {
		TestUtils.initUserNode(followService.userRepository(), 641, 642, 643, 644);
		UserNode node1 = followService.userRepository().findByPropertyValue("userId", 641);
		UserNode node2 = followService.userRepository().findByPropertyValue("userId", 642);
		UserNode node3 = followService.userRepository().findByPropertyValue("userId", 643);
		UserNode node4 = followService.userRepository().findByPropertyValue("userId", 644);
		// 判断若不存在关系，则创建关系，node1关注node2和node4，node4 关注node1，node2关注node3
		Friend follow1 = followService.template().getRelationshipBetween(node1, node2, Friend.class,
				RelTypeName.FOLLOWS);
		if (follow1 == null) {
			followService.userRepository().createRelationshipBetween(node1, node2, Follows.class, RelTypeName.FOLLOWS);
		}
		Friend follow2 = followService.template().getRelationshipBetween(node1, node4, Friend.class,
				RelTypeName.FOLLOWS);
		Friend follow21 = followService.template().getRelationshipBetween(node4, node1, Friend.class,
				RelTypeName.FOLLOWS);
		if (follow2 == null) {
			followService.userRepository().createRelationshipBetween(node1, node4, Follows.class, RelTypeName.FOLLOWS);
		}
		if (follow21 == null) {
			followService.userRepository().createRelationshipBetween(node4, node1, Follows.class, RelTypeName.FOLLOWS);
		}
		Friend follow3 = followService.template()
				.getRelationshipBetween(node2, node3, Friend.class, RelTypeName.FRIEND);
		if (follow3 == null) {
			followService.userRepository().createRelationshipBetween(node2, node3, Follows.class, RelTypeName.FOLLOWS);
		}
		boolean bool = false;
		List<FollowsRelationship> resultList = null;
		resultList = userRelationshipService.getFollowsToUser(641, null, null);
		assertEquals("获取关注数量错误", resultList.size(), 1);
		bool = resultList.get(0).getOperUserId() == 644;
		assertTrue(bool);
		resultList = userRelationshipService.getFollowsToUser(642, null, null);
		assertEquals("获取关注数量错误", resultList.size(), 1);
		bool = resultList.get(0).getOperUserId() == 641;
		assertTrue(bool);
	}

	@Test
	public void testRefuseFriendRequest() throws XhrcException {
		UserRepository userRepository = friendRequestService.userRepository();
		// 创建测试数据
		UserNode node1 = friendRequestService.getUserNode(256);
		UserNode node2 = friendRequestService.getUserNode(257);
		UserNode node3 = friendRequestService.getUserNode(258);

		FriendRequest bk1 = userRepository.getRelationshipBetween(node1, node2, FriendRequest.class,
				RelTypeName.FRIEND_REQUEST);
		FriendRequest bk2 = userRepository.getRelationshipBetween(node1, node3, FriendRequest.class,
				RelTypeName.FRIEND_REQUEST);
		FriendRequest bk3 = userRepository.getRelationshipBetween(node3, node1, FriendRequest.class,
				RelTypeName.FRIEND_REQUEST);
		if (bk1 == null) {
			bk1 = userRepository.createRelationshipBetween(node1, node2, FriendRequest.class,
					RelTypeName.FRIEND_REQUEST);
		}
		if (bk2 == null) {
			bk2 = userRepository.createRelationshipBetween(node1, node3, FriendRequest.class,
					RelTypeName.FRIEND_REQUEST);
		}
		if (bk3 == null) {
			bk3 = userRepository.createRelationshipBetween(node3, node1, FriendRequest.class,
					RelTypeName.FRIEND_REQUEST);
		}

		// 测试开始 1.拒绝指向当前操作用户的好友请求 2.双向都有好友请求
		boolean bool = false;
		bool = userRelationshipService.refuseFriendRequest(node1.getUserId(), node2.getUserId());
		// 成功返回true
		assertTrue(bool);
		FriendRequest blk1 = userRepository.getRelationshipBetween(node1, node2, FriendRequest.class,
				RelTypeName.FRIEND_REQUEST);
		// 不存在好友请求
		assertNull(blk1);

		bool = userRelationshipService.refuseFriendRequest(node1.getUserId(), node3.getUserId());
		// 成功返回true
		assertTrue(bool);
		FriendRequest blk2 = userRepository.getRelationshipBetween(node1, node3, FriendRequest.class,
				RelTypeName.FRIEND_REQUEST);
		FriendRequest blk3 = userRepository.getRelationshipBetween(node3, node1, FriendRequest.class,
				RelTypeName.FRIEND_REQUEST);
		// 不存在好友请求
		assertNull(blk3);

	}

	@Test
	public void testCountUserFriendsByEachGroup() throws XhrcException {
		TestUtils.initUserNode(friendService.userRepository(), 645);
		UserNode node1 = friendService.userRepository().findByPropertyValue("userId", 645);
		node1 = friendService.userRepository().save(node1);
		TestUtils.initUserNode(friendService.userRepository(), 646, 647, 648, 649);
		for (int i = 646; i < 650; i++) {
			UserNode unode = friendService.userRepository().findByPropertyValue("userId", i);
			friendService.userRepository().createRelationshipBetween(node1, unode, Friend.class, RelTypeName.FRIEND);
			friendService.userRepository().createRelationshipBetween(unode, node1, Friend.class, RelTypeName.FRIEND);
		}
		for (int i = 646; i < 648; i++) {
			UserNode targetUser = friendService.userRepository().findByPropertyValue("userId", i);
			Friend friRel = friendService.template().getRelationshipBetween(node1, targetUser, Friend.class,
					RelTypeName.FRIEND);
			friRel.setFriendGroups(Lang.list("知己"));
			friendService.template().save(friRel);
		}
		for (int i = 648; i < 650; i++) {
			UserNode targetUser = friendService.userRepository().findByPropertyValue("userId", i);
			Friend friRel = friendService.template().getRelationshipBetween(node1, targetUser, Friend.class,
					RelTypeName.FRIEND);
			friRel.setFriendGroups(Lang.list("发小"));
			friendService.template().save(friRel);
		}
		Map<String, Long> map = new HashMap<String, Long>();
		map = userRelationshipService.countUserFriendsByEachGroup(645);
		assertEquals("好友数量计算错误", 2, map.get("知己").intValue());
		assertEquals("好友数量计算错误", 2, map.get("发小").intValue());
	}

	@Test
	public void testCreateFriendRequestValidate() throws XhrcException {
		TestUtils.initUserNode(friendRequestService.userRepository(), 201, 202, 203, 204, 205, 206);
		// 验证是否存该节点
		UserNode node1 = friendRequestService.getUserNode(201);
		UserNode node2 = friendRequestService.getUserNode(202);
		UserNode node3 = friendRequestService.getUserNode(203);
		UserNode node4 = friendRequestService.getUserNode(204);
		UserNode node5 = friendRequestService.getUserNode(205);
		UserNode node6 = friendRequestService.getUserNode(206);
		UserRepository userRepository = friendRequestService.userRepository();

		// 黑名单
		userRepository.createRelationshipBetween(node1, node2, Blacklists.class, RelTypeName.BLACKLISTS);
		userRepository.createRelationshipBetween(node4, node3, Blacklists.class, RelTypeName.BLACKLISTS);
		// 双向好友
		userRepository.createRelationshipBetween(node5, node6, Friend.class, RelTypeName.FRIEND);
		userRepository.createRelationshipBetween(node6, node5, Friend.class, RelTypeName.FRIEND);

		// 测试开始
		boolean bool = false;
		// 1.我将对方拉到黑名单
		try {
			bool = userRelationshipService.createFriendRequest(node1.getUserId(), node2.getUserId(), "好友请求");
		} catch (Exception e) {
			e.getMessage();
		}
		assertFalse(bool);
		// 验证为空
		FriendRequest fq1 = userRepository.getRelationshipBetween(node1, node2, FriendRequest.class,
				RelTypeName.FRIEND_REQUEST);
		assertNull(fq1);

		// 2对方将我拉到黑名单
		try {
			bool = userRelationshipService.createFriendRequest(node3.getUserId(), node4.getUserId(), "好友请求");
		} catch (Exception e) {
			e.getMessage();
		}
		assertFalse(bool);
		// 验证为空
		FriendRequest fq2 = userRepository.getRelationshipBetween(node3, node4, FriendRequest.class,
				RelTypeName.FRIEND_REQUEST);
		assertNull(fq2);

		// 3.我和对方已经是好友
		try {
			bool = userRelationshipService.createFriendRequest(node3.getUserId(), node4.getUserId(), "好友请求");
		} catch (Exception e) {
			e.getMessage();
		}
		assertFalse(bool);
		// 验证为空
		FriendRequest fq3 = userRepository.getRelationshipBetween(node5, node6, FriendRequest.class,
				RelTypeName.FRIEND_REQUEST);
		assertNull(fq3);
	}

	@Test
	public void testGetUserFriendsAtDeepTwo() throws XhrcException {
		TestUtils.initUserNode(friendService.userRepository(), 661, 662, 663, 664, 665);
		UserNode node1 = friendService.userRepository().findByPropertyValue("userId", 661);
		UserNode node2 = friendService.userRepository().findByPropertyValue("userId", 662);

		UserNode node3 = friendService.userRepository().findByPropertyValue("userId", 663);
		UserNode node4 = friendService.userRepository().findByPropertyValue("userId", 664);
		UserNode node5 = friendService.userRepository().findByPropertyValue("userId", 665);
		// node1与node2是好友
		friendService.userRepository().createRelationshipBetween(node1, node2, Friend.class, RelTypeName.FRIEND);
		friendService.userRepository().createRelationshipBetween(node2, node1, Friend.class, RelTypeName.FRIEND);
		// node2与node3是好友
		friendService.userRepository().createRelationshipBetween(node2, node3, Friend.class, RelTypeName.FRIEND);
		friendService.userRepository().createRelationshipBetween(node3, node2, Friend.class, RelTypeName.FRIEND);
		// node1与node4是好友
		friendService.userRepository().createRelationshipBetween(node1, node4, Friend.class, RelTypeName.FRIEND);
		friendService.userRepository().createRelationshipBetween(node4, node1, Friend.class, RelTypeName.FRIEND);
		// node2与node4是好友
		friendService.userRepository().createRelationshipBetween(node2, node4, Friend.class, RelTypeName.FRIEND);
		friendService.userRepository().createRelationshipBetween(node4, node2, Friend.class, RelTypeName.FRIEND);
		// node3与node4是好友
		friendService.userRepository().createRelationshipBetween(node3, node4, Friend.class, RelTypeName.FRIEND);
		friendService.userRepository().createRelationshipBetween(node4, node3, Friend.class, RelTypeName.FRIEND);
		// node4与node5是好友
		friendService.userRepository().createRelationshipBetween(node4, node5, Friend.class, RelTypeName.FRIEND);
		friendService.userRepository().createRelationshipBetween(node5, node4, Friend.class, RelTypeName.FRIEND);
		List<Integer> fofid = userRelationshipService.getUserFriendsAtDeepTwo(661, (long) 0, 5);
		assertEquals("获取朋友的朋友数量错误", 2, fofid.size());
		boolean bool = false;
		bool = fofid.get(0) == 663 || fofid.get(0) == 665;
		assertTrue(bool);
	}

	@Test
	public void testGetFriendRequestToUser() throws XhrcException {
		TestUtils.initUserNode(friendRequestService.userRepository(), 610, 611, 612, 613);
		UserNode node1 = friendRequestService.userRepository().findByPropertyValue("userId", 610);
		UserNode node2 = friendRequestService.userRepository().findByPropertyValue("userId", 611);

		UserNode node3 = friendRequestService.userRepository().findByPropertyValue("userId", 612);
		UserNode node4 = friendRequestService.userRepository().findByPropertyValue("userId", 613);
		// 判断若不存在关系，则创建关系，node2和node4请求node1，node1请求node3
		FriendRequest friendRequest1 = friendRequestService.template().getRelationshipBetween(node2, node1,
				FriendRequest.class, RelTypeName.FRIEND_REQUEST);
		if (friendRequest1 == null) {
			friendRequestService.userRepository().createRelationshipBetween(node2, node1, FriendRequest.class,
					RelTypeName.FRIEND_REQUEST);
		}
		FriendRequest friendRequest2 = friendRequestService.template().getRelationshipBetween(node4, node1,
				FriendRequest.class, RelTypeName.FRIEND_REQUEST);
		if (friendRequest2 == null) {
			friendRequestService.userRepository().createRelationshipBetween(node4, node1, FriendRequest.class,
					RelTypeName.FRIEND_REQUEST);
		}
		FriendRequest friendRequest3 = friendRequestService.template().getRelationshipBetween(node1, node3,
				FriendRequest.class, RelTypeName.FRIEND_REQUEST);
		if (friendRequest3 == null) {
			friendRequestService.userRepository().createRelationshipBetween(node1, node3, FriendRequest.class,
					RelTypeName.FRIEND_REQUEST);
		}
		boolean bool = false;
		List<FriendRequestRelationship> resultList = null;
		resultList = userRelationshipService.getFriendRequestToUser(610);
		assertEquals("获取用户关注信息列表失败", resultList.size(), 2);
		bool = resultList.get(0).getOperUserId() == 611 || resultList.get(0).getOperUserId() == 613;
		assertTrue(bool);
	}

	@Test
	public void testCountFollowsToUser() throws XhrcException {
		TestUtils.initUserNode(followService.userRepository(), 641, 642, 643, 644);
		UserNode node1 = followService.userRepository().findByPropertyValue("userId", 641);
		UserNode node2 = followService.userRepository().findByPropertyValue("userId", 642);
		UserNode node3 = followService.userRepository().findByPropertyValue("userId", 643);
		UserNode node4 = followService.userRepository().findByPropertyValue("userId", 644);
		// 判断若不存在关系，则创建关系，node1关注node2和node4，node4 关注node1，node2关注node3
		Follows follow1 = followService.template().getRelationshipBetween(node1, node2, Follows.class,
				RelTypeName.FOLLOWS);
		if (follow1 == null) {
			followService.userRepository().createRelationshipBetween(node1, node2, Follows.class, RelTypeName.FOLLOWS);
		}
		Follows follow2 = followService.template().getRelationshipBetween(node1, node4, Follows.class,
				RelTypeName.FOLLOWS);
		Follows follow21 = followService.template().getRelationshipBetween(node4, node1, Follows.class,
				RelTypeName.FOLLOWS);
		if (follow2 == null) {
			followService.userRepository().createRelationshipBetween(node1, node4, Follows.class, RelTypeName.FOLLOWS);
		}
		if (follow21 == null) {
			followService.userRepository().createRelationshipBetween(node4, node1, Follows.class, RelTypeName.FOLLOWS);
		}
		Follows follow3 = followService.template().getRelationshipBetween(node2, node3, Follows.class,
				RelTypeName.FRIEND);
		if (follow3 == null) {
			followService.userRepository().createRelationshipBetween(node2, node3, Follows.class, RelTypeName.FOLLOWS);
		}
		assertEquals("获取关注数量错误", userRelationshipService.countFollowsToUser(641), 1);
		assertEquals("获取关注数量错误", userRelationshipService.countFollowsToUser(643), 1);

	}
}
package com.xinhuanet.relationship.service.impl;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.xinhuanet.relationship.TestUtils;
import com.xinhuanet.relationship.common.exception.XhrcException;
import com.xinhuanet.relationship.entity.node.UserNode;
import com.xinhuanet.relationship.lang.Lang;
import com.xinhuanet.relationship.repository.UserRepository;
import com.xinhuanet.relationship.service.UserNodeService;

public class UserNodeServiceImplTest {

	private static ClassPathXmlApplicationContext context;
	private static UserNodeService userNodeService;
	private static UserRepository userRepository;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// 获取userNodeService userRepository
		context = new ClassPathXmlApplicationContext("context-main.xml");
		userNodeService = context.getBean("userNodeService", UserNodeServiceImpl.class);
		userRepository = userNodeService.userRepository();
		// 准备数据
		TestUtils.initUserNode(userRepository, 511, 512, 513);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		context.close();
	}

	@Test
	public void testGetUserCustomFriendGroups() throws XhrcException {
		UserNode actual = userNodeService.getUserNode(511);
		List<String> list = actual.getCustomFriendGroups();
		assertEquals(Lang.list("小弟", "知己", "发小"), list);
	}

	@Test
	public void testGetUserFriendGroup() throws XhrcException {
		List<String> list = userNodeService.getUserFriendGroups(511);
		assertEquals(Lang.list("默认分组", "贵宾", "朋友", "家人", "亲属", "同事", "同学", "同乡", "同城", "密友", "其他", "小弟", "知己", "发小"),
				list);
	}

	@Test
	public void testCreateUserCustomFriendGroup() throws XhrcException, InterruptedException {
		userNodeService.createUserCustomFriendGroup(512, "老大");
		UserNode node = userRepository.findByPropertyValue("userId", 512);
		assertEquals(Lang.list("小弟", "知己", "发小", "老大"), node.getCustomFriendGroups());
	}

	@Test
	public void testCreateUserCustomFriendGroupForEmptyCustomFriendGroupUser() throws XhrcException,
			InterruptedException {
		// 准备数据
		userRepository.delete(userRepository.findAllByPropertyValue("userId", 515));
		UserNode node = new UserNode();
		node.setUserId(515);
		// 不设置其好友分组字段
		userRepository.save(node);

		userNodeService.createUserCustomFriendGroup(515, "千斤顶");

		// 验证
		UserNode expected = userRepository.findByPropertyValue("userId", 515);
		assertEquals(Lang.list("千斤顶"), expected.getCustomFriendGroups());
	}

	@Test
	public void testDeleteUserCustomFriendGroup() throws XhrcException, InterruptedException {
		userNodeService.deleteUserCustomFriendGroup(513, "小弟");
		UserNode node = userRepository.findByPropertyValue("userId", 513);
		assertEquals(Lang.list("知己", "发小"), node.getCustomFriendGroups());
	}

}

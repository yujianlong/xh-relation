package com.xinhuanet.relationship.service.impl;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.xinhuanet.relationship.common.exception.XhrcException;
import com.xinhuanet.relationship.entity.node.UserNode;
import com.xinhuanet.relationship.lang.Lang;
import com.xinhuanet.relationship.repository.UserRepository;

public class BaseServiceImplTest {

	private static Logger logger = LoggerFactory.getLogger(BaseServiceImplTest.class);

	private static ClassPathXmlApplicationContext context;
	private static BaseServiceImpl baseService;
	private static UserRepository userRepository;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// 获取baseService
		context = new ClassPathXmlApplicationContext("context-main.xml");
		baseService = context.getBean("baseService", BaseServiceImpl.class);
		userRepository = baseService.userRepository();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		context.close();
	}

	@Test
	public void testTemplate() {
		assertNotNull(baseService.template());
	}

	@Test
	public void testUserRepository() {
		assertNotNull(baseService.userRepository());
	}

	@Test
	public void testDbData() {
		logger.debug("~~~~~~~~~~~~~count:" + baseService.template().count(UserNode.class));
	}

	@Test
	public void testSaveUserNode() throws XhrcException, InterruptedException {
		// 准备数据
		userRepository.delete(userRepository.findAllByPropertyValue("userId", 502));
		// 测试开始
		UserNode node = new UserNode();
		node.setUserId(502);
		node.setCustomFriendGroups(Lang.list("师兄", "师弟", "哥们"));
		baseService.saveUserNode(node);
		UserNode nodeNew = userRepository.findByPropertyValue("userId", 502);
		assertNotNull(nodeNew);
		assertEquals(Lang.list("师兄", "师弟", "哥们"), nodeNew.getCustomFriendGroups());
	}

	@Test
	public void testCreateUserNode() throws XhrcException, InterruptedException {
		// 准备数据
		userRepository.delete(userRepository.findAllByPropertyValue("userId", 503));
		// 测试开始
		baseService.createUserNode(503);
		UserNode node = userRepository.findByPropertyValue("userId", 503);
		assertNotNull(node);
	}

	@Test
	public void testGetUseNode() throws XhrcException {
		UserNode actual = baseService.getUserNode(501);
		UserNode expected = userRepository.findByPropertyValue("userId", 501);
		assertEquals(expected.getNodeId(), actual.getNodeId());
		assertEquals(expected.getUserId(), actual.getUserId());
		assertEquals(expected.getCustomFriendGroups(), actual.getCustomFriendGroups());
	}

	@Test
	public void testGetUseNodeNotExisit() throws XhrcException {
		// 准备数据
		userRepository.delete(userRepository.findAllByPropertyValue("userId", 520));
		UserNode actual = baseService.getUserNode(520);
		UserNode expected = userRepository.findByPropertyValue("userId", 520);
		assertEquals(expected.getUserId(), actual.getUserId());
	}
}

package com.xinhuanet.relationship.service.impl;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import com.xinhuanet.relationship.common.exception.XhrcException;
import com.xinhuanet.relationship.service.BlockService;

public class TransactionTest {

	private static Logger logger = LoggerFactory.getLogger(BlockServiceImplTest.class);

	private static BlockService blockService;
	private static ClassPathXmlApplicationContext context;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// 获取baseService
		context = new ClassPathXmlApplicationContext("context-main.xml");
		blockService = context.getBean("blockService", BlockService.class);
		// baseService = context.getBean("baseService", BaseService.class);
		// userRepository = blockService.userRepository();

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// context.close();
	}

	@Test
	@Transactional
	public void testTransactionTest() throws XhrcException {
		blockService.createBlock(3300, 3301);
		// throw new RuntimeException("抛异常回滚测试!");
	}

}

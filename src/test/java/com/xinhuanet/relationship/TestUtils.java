package com.xinhuanet.relationship;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.xinhuanet.relationship.common.exception.XhrcException;
import com.xinhuanet.relationship.entity.node.UserNode;
import com.xinhuanet.relationship.lang.Lang;
import com.xinhuanet.relationship.repository.UserRepository;

public class TestUtils {
	private static Logger logger = LoggerFactory.getLogger(TestUtils.class);

	/**
	 * 删除所有指定userId的节点及其相关关系，重新创建这些节点。
	 * 各用户节点都设置了自定义用户分组："小弟", "知己", "发小"。
	 * 
	 * @param service
	 * @param userId
	 * @throws XhrcException
	 */
	public static void initUserNode(UserRepository userRepository, int... userId) throws XhrcException {
		for (int id : userId) {
			userRepository.delete(userRepository.findAllByPropertyValue("userId", id));
			UserNode userNode = new UserNode();
			userNode.setUserId(id);
			userNode.setCustomFriendGroups(Lang.list("小弟", "知己", "发小"));
			try {
				userRepository.save(userNode);
			} catch (Exception e) {
				logger.error("保存用户节点异常 :", e);
				throw new XhrcException("保存用户节点异常 :" + JSON.toJSONString(userNode), e);
			}
		}
	}
}

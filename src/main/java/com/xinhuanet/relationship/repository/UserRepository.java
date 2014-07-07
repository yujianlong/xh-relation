package com.xinhuanet.relationship.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;
import org.springframework.data.repository.query.Param;

import com.xinhuanet.relationship.entity.node.UserNode;
import com.xinhuanet.relationship.entity.relationship.Follows;
import com.xinhuanet.relationship.entity.relationship.Friend;
import com.xinhuanet.relationship.entity.relationship.FriendRequest;

/**
 * @author Administrator
 * 
 */
public interface UserRepository extends GraphRepository<UserNode>, RelationshipOperationsRepository<UserNode> {
	/**
	 * 
	 * <p>
	 * 按好友分组查询好友数量.
	 * </p>
	 * 
	 * @param nodeId 节点id
	 * @param friendGroup 好友分组
	 * @return long 好友数量
	 * @author chenwc
	 */
	@Query("START n=node({nodeId}) MATCH n-[r:FRIEND]->t-[:FRIEND]->n WHERE  (has(r.friendGroups)) AND ({friendGroup} IN r.friendGroups)  RETURN COUNT(t)")
	long countUserFriendByGroup(@Param("nodeId") long nodeId, @Param("friendGroup") String friendGroup);

	/**
	 * 
	 * <p>
	 * 查询好友数量.
	 * </p>
	 * 
	 * @param nodeId 节点id
	 * @return long 好友数量
	 * @author chenwc
	 */
	@Query("START n=node({nodeId}) MATCH n-[:FRIEND]->t-[:FRIEND]->n RETURN COUNT(t)")
	long countUserFriend(@Param("nodeId") long nodeId);

	/**
	 * 
	 * <p>
	 * 查询关注数量.
	 * </p>
	 * 
	 * @param nodeId 节点id
	 * @return long 关注数量
	 * @author chenwc
	 */
	@Query("START n=node({nodeId}) MATCH n-[:FOLLOWS]->t  RETURN COUNT(t)")
	long countFollow(@Param("nodeId") long nodeId);

	/**
	 * 
	 * <p>
	 * 查询关注了指定用户的用户数目.
	 * </p>
	 * 
	 * @param nodeId 节点id
	 * @return long 关注数量
	 * @author zhaodm
	 */
	@Query("START n=node({nodeId}) MATCH t-[:FOLLOWS]->n  RETURN COUNT(t)")
	long countFollowsToUser(@Param("nodeId") long nodeId);

	/**
	 * <p>
	 * 获得指定用户在指定群组内的好友关系信息列表。跳过前skip条记录，取limit条记录
	 * </p>
	 * 
	 * @param long nodeId
	 *        节点id
	 * @param String friendGroup
	 *            指定的好友分组
	 * @param skip 跳过的好友关系数目
	 * @param limit 每页的好友关系数目
	 * @return List<Integer>
	 *         用户在指定群组内的好友id列表，可能为空列表
	 *         好友关系信息包括用户（startUser）、好友（endUser）、好友关系Id（relId）
	 *         好友备注（remark）、用户好友分组列表（friendGroups）、是否为私密好友（privateFlag）、
	 *         好友关系创建时间（createAt）、好友关系更新时间（updatedAt）
	 * @author zhaodm
	 */

	@Query("START n=node({nodeId}) MATCH n-[r:FRIEND]->t-[:FRIEND]->n WHERE  (has(r.friendGroups)) AND ({friendGroup} IN r.friendGroups)  RETURN r SKIP {skip1} LIMIT {limit1}")
	List<Friend> getUserFriendsByGroup(@Param("nodeId") long nodeId, @Param("friendGroup") String friendGroup,
			@Param("skip1") long offset, @Param("limit1") int limit);

	/**
	 * <p>
	 * 获取指定用户所有好友关系信息列表。 跳过前skip条记录，取limit条记录
	 * </p>
	 * 
	 * @param long nodeId
	 *        节点id
	 * @param skip 跳过的好友关系数目
	 * @param limit 每页的好友关系数目
	 * @return List<IsFriend>
	 *         用户所有好友关系信息列表，可能为空列表
	 *         好友关系信息包括用户（startUser）、好友（endUser）、好友关系Id（relId）
	 *         好友备注（remark）、用户好友分组列表（friendGroups）、是否为私密好友（privateFlag）、
	 *         好友关系创建时间（createAt）、好友关系更新时间（updatedAt）
	 * @author zhaodm
	 */
	@Query("START n=node({nodeId}) MATCH n-[r:FRIEND]->t-[:FRIEND]->n RETURN r SKIP {offset1} LIMIT {limit1}")
	List<Friend> getUserFriends(@Param("nodeId") long nodeId, @Param("offset1") long offset, @Param("limit1") int limit);

	/**
	 * <p>
	 * 获取用户关注信息列表。 跳过前skip条记录，取limit条记录
	 * </p>
	 * 
	 * @param long nodeId
	 *        节点id
	 * @param skip 跳过的用户关注信息数目
	 * @param limit 每页关注信息数目
	 * @return List<Follows>
	 *         用户关注信息列表，可能为空列表
	 * @author zhaodm
	 */

	@Query("START n=node({nodeId}) MATCH n-[r:FOLLOWS]->t RETURN r SKIP {offset1} LIMIT {limit1}")
	List<Follows> getUserFollows(@Param("nodeId") long nodeId, @Param("offset1") long offset, @Param("limit1") int limit);

	/**
	 * <p>
	 * 获取对指定用户发出的好友请求信息列表
	 * </p>
	 * 
	 * @param long nodeId
	 *        节点id
	 * @return List<FriendRequest>
	 *         对指定用户发出的好友请求信息列表，可能为空列表
	 *         请求信息包括发送请求用户userId（startUserId）、被请求用户userId（endUserId）、
	 *         请求发送时间（createAt）和请求消息（message）
	 * @author zhaodm
	 */

	@Query("START n=node({nodeId}) MATCH t-[r:FRIEND_REQUEST]->n RETURN r")
	List<FriendRequest> getFriendRequestToUser(@Param("nodeId") long nodeId);

	/**
	 * <p>
	 * 获获取指定用户拉黑的用户id列表
	 * </p>
	 * 
	 * @param long nodeId
	 *        节点id
	 * @return List<Integer>
	 *         用户黑名单中的用户id列表，可能为空列表
	 * @author zhaodm
	 */

	@Query("START n=node({nodeId}) MATCH n-[:BLACKLISTS]->t  RETURN t.userId")
	List<Integer> getUserBlacks(@Param("nodeId") long nodeId);

	/**
	 * <p>
	 * 获取指定用户屏蔽的用户id列表
	 * </p>
	 * 
	 * @param long nodeId
	 *        节点id
	 * @return List<Integer>
	 *         用户屏蔽的id列表，可能为空列表
	 * @author zhaodm
	 */

	@Query("START n=node({nodeId}) MATCH n-[:BLOCKS]->t  RETURN t.userId")
	List<Integer> getUserBlocks(@Param("nodeId") long nodeId);

	/**
	 * <p>
	 * 统计指定用户黑名单中用户数量
	 * </p>
	 * 
	 * @param long nodeId
	 *        节点id
	 * @return long
	 *         用户黑名单中用户数量
	 * @author zhaodm
	 */
	@Query("START n=node({nodeId}) MATCH n-[:BLACKLISTS]->t  RETURN count(t)")
	long countUserBlacks(@Param("nodeId") long nodeId);

	/**
	 * <p>
	 * 统计指定用户屏蔽的用户数量
	 * </p>
	 * 
	 * @param long nodeId
	 *        节点id
	 * @return long
	 *         指定用户屏蔽的用户数量
	 * @author zhaodm
	 */

	@Query("START n=node({nodeId}) MATCH n-[:BLOCKS]->t  RETURN count(t)")
	long countUserBlocks(@Param("nodeId") long nodeId);

	/**
	 * <p>
	 * 获得指定用户阻止id列表
	 * </p>
	 * 
	 * @param long nodeId
	 *        节点id
	 * @return List<Integer>
	 *         指定用户阻止的用户id列表，可能为空列表
	 * @author zhaodm
	 */

	@Query("START n=node({nodeId}) MATCH n-[:OBSTRUCTS]->t  RETURN t.userId")
	List<Integer> getUserObstructs(@Param("nodeId") long nodeId);

	/**
	 * <p>
	 * 统计指定用户阻止的用户数量
	 * </p>
	 * 
	 * @param long nodeId
	 *        节点id
	 * @return long
	 *         指定用户阻止的用户数量
	 * @author zhaodm
	 */

	@Query("START n=node({nodeId}) MATCH n-[:OBSTRUCTS]->t  RETURN count(t)")
	long countUserObstructs(@Param("nodeId") long nodeId);

	/**
	 * <p>
	 * 获取对用户关的注信息列表。 跳过前skip条记录，取limit条记录
	 * </p>
	 * 
	 * @param long nodeId
	 *        节点id
	 * @param skip 跳过的对用户关注信息数目
	 * @param limit 每页关注信息数目
	 * @return List<Follows>
	 *         对指定用户的关注信息列表，可能为空列表
	 * @author zhaodm
	 */

	@Query("START n=node({nodeId}) MATCH t-[r:FOLLOWS]->n  RETURN r SKIP {offset1} LIMIT {limit1}")
	List<Follows> getFollowsToUser(@Param("nodeId") long nodeId, @Param("offset1") long offset,
			@Param("limit1") int limit);

	/**
	 * 删除用户所有的好友关系中，好友分组属性——列表中值为friendGroup的元素
	 * 
	 * @param nodeId
	 * @param friendGroup
	 * @return
	 */
	@Query("START n=node({nodeId})  MATCH (n)-[r:FRIEND]->()  WHERE (has(r.friendGroups)) AND ({friendGroup} IN r.friendGroups)  SET r.friendGroups=filter(x in r.friendGroups WHERE not(x={friendGroup})) RETURN count(r)")
	long removeFriendGroupForUserFriendRelationship(@Param("nodeId") long nodeId,
			@Param("friendGroup") String friendGroup);

	/**
	 * 用户所有的好友关系中，好友分组属性为空列表的，全部设置为["默认分组"]
	 * 
	 * @param nodeId
	 * @return
	 */
	@Query("START n=node({nodeId})  MATCH (n)-[r:FRIEND]->()  WHERE (has(r.friendGroups)) AND (length(r.friendGroups)=0)  SET r.friendGroups=[\"默认分组\"] RETURN count(r)")
	long addDefaultFriendGroupForEmptyFriendGroupRelationship(@Param("nodeId") long nodeId);

	/**
	 * 获取用户深度为2的好友id列表，跳过前skip条记录，取limit条记录
	 * 
	 * @param nodeId 操作用户id
	 * @param offset 跳过的记录数
	 * @param limit 每页的id数目
	 * @return List 用户深度为2的好友id列表
	 */
	@Query("START n=node({nodeId}) MATCH t-[:FRIEND]->()-[:FRIEND]->n-[:FRIEND]->()-[:FRIEND]->t WHERE NOT(n-[:FRIEND]->t) AND NOT(n = t) RETURN distinct t.userId SKIP {offset1} LIMIT {limit1}")
	List<Integer> getUserFriendsOfFriends(@Param("nodeId") long nodeId, @Param("offset1") long offset,
			@Param("limit1") int limit);
}

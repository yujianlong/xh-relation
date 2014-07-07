package com.xinhuanet.relationship;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.xinhuanet.relationship.dubbo.service.impl.UserRelationshipServiceImplTest;
import com.xinhuanet.relationship.service.impl.BaseServiceImplTest;
import com.xinhuanet.relationship.service.impl.BlacklistServiceImplTest;
import com.xinhuanet.relationship.service.impl.BlockServiceImplTest;
import com.xinhuanet.relationship.service.impl.FollowServiceImplTest;
import com.xinhuanet.relationship.service.impl.FriendRequestServiceImplTest;
import com.xinhuanet.relationship.service.impl.FriendServiceImplTest;
import com.xinhuanet.relationship.service.impl.ObstructServiceImplTest;
import com.xinhuanet.relationship.service.impl.UserNodeServiceImplTest;

@RunWith(Suite.class)
@SuiteClasses({ BaseServiceImplTest.class, UserNodeServiceImplTest.class, FollowServiceImplTest.class,
		FriendServiceImplTest.class, FriendRequestServiceImplTest.class, BlacklistServiceImplTest.class,
		BlockServiceImplTest.class, ObstructServiceImplTest.class, UserRelationshipServiceImplTest.class })
public class AllTests {

}

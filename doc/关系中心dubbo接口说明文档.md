用户关系中心 dubbo 接口说明文档
===================


#1.获取用户好友分组

```java
List<String> getUserFriendGroups(Integer userId) throws XhucException
```
**获取用户好友分组，包括系统好友分组和自定义好友分组。**

###参数:
userId - 用户Id

###返回:
好友分组列表

###抛出:
XhucException

#2.获取指定用户的关注信息列表

```java
List<FollowsRelationship> getUserFollows(Integer userId, Long offset, Integer limit) throws XhrcException;
```
 > **获取指定用户关注信息列表。 跳过前offset个关注信息后，再取limit个关注信息，**

###参数：
userId - 操作用户id  
offset - 获取时跳过的关注信息数，当offset为null或小于0时，默认设置为0  
limit - 获取的关注信息数量，当offset为null，小于0，等于0，默认设置为20  

###返回：
list - 返回用户关注信息的指定位置指定数目的列表（offset+1至offset+limit），可能为空列表。
> 关注信息包括发起关注的用户userId（operUserId）、被关注用户的userId（targetUserId）、
> 关注的时间（createAt）和备注信息（remark）

###抛出：
XhuaException

#3.获取指定用户的好友关系信息列表

```java
	List<Integer> getUserFriends(Integer userId, String friendGroup) throws XhucException;
```
> **获得指定用户的好友id列表。根据指定群组参数是否为空(包括null和"")返回不同结果， 跳过前offset个好友关系信息后，再取limit个好友关系信息**

###参数：
userId - 操作用户id  
friendGroup - 指定的群组，可以为空  
> 若非空返回指定用户在指定群组内的好友关系信息列表，
> 若为空则返回指定用户的所有好友关系信息列表
offset - 获取时跳过的好友信息信息数，当offset为null或小于0时，默认设置为0
limit - 获取的好友信息数量，当offset为null，小于0，等于0，默认设置为20

###返回：
list - 指定用户的好友关系信息列表。 结果可能为空
 > 好友关系信息包括用户userId（operUserId）、好友userId（targetUserId）、
 > 好友备注（remark）、用户好友分组列表（friendGroups）、是否为私密好友（privateFlag）、
 > 好友关系创建时间（createAt）

###抛出：
XhuaException

#4.获取向指定用户发出的好友请求信息列表

```java
List<FriendRequestRelationship> getFriendRequestToUser(Integer userId) throws XhucException;
```
> **获得对指定用户发出的好友请求信息列表。好友请求为单向关系**

###参数：
userId - 操作用户id

###返回：
list - 对指定用户发出的好友请求信息列表，可能为空列表      
 > 请求信息包括发送请求用户userId（operUserId）、被请求用户userId（targetUserId）、
 > 请求发送时间（createAt）和附言（message）

###抛出：
XhuaException

#5.获取指定用户屏蔽的用户id列表

```java
List<Integer> getUserBlocks(Integer userId) throws XhucException
```
> **获取指定用户屏蔽的用户id列表。屏蔽为单向关系**

###参数：
userId - 操作用户id

###返回：
list - 指定用户屏蔽的用户id列表。可能为空列表

###抛出：
XhuaException

#6.获取指定用户黑名单中用户id列表

```java
List<Integer> getUserBlacklists(Integer userId) throws XhucException
```
> **获取指定用户黑名单中用户id列表。**

###参数：
userId - 操作用户id

###返回：
list - 指定用户黑名单中用户id列表，可能为空列表

###抛出：
XhuaException

#7.统计指定用户黑名单中用户的数目

```java
long countUserBlacklists(Integer userId) throws XhucException
```
> **统计指定用户黑名单中的用户数目。**

###参数：
userId - 操作用户id

###返回：
long - 指定用户黑名单中的用户数目

###抛出：
XhuaException

#8.统计指定用户屏蔽的用户数目

```java
long countUserBlocks(Integer userId) throws XhucException
```
> **获得指定用户屏蔽的用户数目。**

###参数：
userId - 操作用户id

###返回：
long - 指定用户屏蔽的用户数目

###抛出：
XhuaException

#9.删除好友关系。
```java
boolean deleteFriendBetween(Integer userId1, Integer userId2) throws XhucException
```
>**删除好友关系,删除的是双向关系。  判断是否存在关注关系，如不存在关注关系，但存在阻止或屏蔽关系，则删除该阻止及屏蔽关系。**
###参数:
userId1 - 用户1Id  
userId2 - 用户2Id

###返回:
删除操作成功返回true 失败返回false。

###抛出:
XhucException



#10.操作用户取消对目标用户的关注。
```java
boolean deleteFollow(Integer startUserId, Integer endUserId) throws XhucException;
```
>**判断是否存在好友关系，如不存在好友关系，但存在阻止或屏蔽关系，则删除该阻止及屏蔽关系。然后删除关注关系。**
###参数:
startUserId - 操作用户ID  
endUserId - 被关注用户ID

###返回:
删除操作成功返回true 失败返回false。

###抛出:
XhucException

#11.删除两个用户间的黑名单关系。
```java
boolean deleteBlacklist(Integer startUserId, Integer endUserId) throws XhucException;
```
>**从startUser的黑名单中移除endUser。**
###参数:
startUserId -  操作用户id	  
endUserId -  被移出黑名单用户id

###返回:
删除操作成功返回true 失败返回false。

###抛出:
XhucException

#12.操作用户取消对目标用户的屏蔽关系。
```java
boolean deleteBlock(Integer startUserId, Integer endUserId) throws XhucException;
```
>**删除startUser对endUser的屏蔽关系。**
###参数:
 startUserId -  操作用户id  
 endUserId -   被屏蔽的目标用户id

###返回:
删除操作成功返回true 失败返回false。

###抛出:
XhucException


#13.操作用户取消对目标用户的阻止。
```java
boolean deleteObstruct(Integer startUserId, Integer endUserId) throws XhucException;
```
>**删除startUser对endUser的阻止关系。**
###参数:
 startUserId -  操作用户id  
 endUserId -   被阻止用户id

###返回:
删除操作成功返回true 失败返回false。

###抛出:
XhucException

#14.判断是否存在好友关系。
```java
boolean existFriendBetween(Integer userId1, Integer userId2) throws XhucException;
```
>**判断是否存在好友关系。好友关系是双向关系。**
###参数:
userId1 - 用户1Id  
userId2 - 用户2Id

###返回:
存在返回true， 不存在返回false。

###抛出:
XhucException

#15.判断是否存在关注关系。
```java
boolean existFollow(Integer startUserId, Integer endUserId) throws XhucException;
```
>**判断是否存在关注关系。startUser 关注 endUser**
###参数:
startUserId - 操作用户ID   
endUserId - 被关注用户ID

###返回:
存在关注返回true 不存在返回false。

###抛出:
XhucException


#16.判断两个用户间是不是有屏蔽关系
```java
boolean existBlock(Integer startUserId, Integer endUserId) throws XhucException;
```
>**判断startUser是否屏蔽endUser。**
###参数:
startUserId - 操作用户ID   
endUserId - 被屏蔽用户ID

###返回:
存在阻止返回true 不存在返回false。

###抛出:
XhucException

#17.判断endUser是否在startUser的黑名单中。
```java
boolean existBlacklist(Integer startUserId, Integer endUserId) throws XhucException;
```
>**判断endUser是否在startUser的黑名单中。**
###参数:
startUserId - 操作用户ID      
endUserId - 判断是否在黑名单中的用户id

###返回:
存在黑名单中返回true 不存在返回false。

###抛出:
XhucException
#18.判断两个用户间是不是有阻止关系
```java
boolean existObstruct(Integer startUserId, Integer endUserId) throws XhucException;
```
>**判断startUser是否阻止endUser对自己的访问。**
###参数:
startUserId - 操作用户ID      
endUserId - 判断是否在阻止名单中的用户id

###返回:
存在阻止名单中返回true 不存在返回false。

###抛出:
XhucException

#19.判断两个用户间是不是有好友请求
```java
boolean existFriendRequest(Integer startUserId, Integer endUserId) throws XhucException;
```
>**判断是否存在startUser对endUser的好友请求。**
###参数:
startUserId - 发出请求用户ID      
endUserId -   被请求加好友的用户id

###返回:
存在好友请求返回true 不存在返回false。

###抛出:
XhucException


#20.统计指定用户指定好友分组的好友数目。
```java
long countUserFriends(Integer userId, String friendGroup) throws XhucException;
```
>**统计指定用户指定好友分组的好友数目。好友分组参数为空或null 则统计指定用户所有好友数量**
###参数:
userId - 用户ID   
friendGroup- 用户好友分组  

###返回:
好友数量

###抛出:
XhucException

#21. 统计指定用户关注的用户数量。
```java
long countUserFollows(Integer userId) throws XhucException;
```
>** 统计指定用户关注的用户数量。**
###参数:
userId - 用户ID 


###返回:
关注数量

###抛出:
XhucException

#23.更新好友关系的属性（单向）。
```java
boolean updateFriend(Integer operUserId, Integer targetUserId, String remark, boolean privateFlag,
			List<String> friendGroups) throws XhucException;
```
**更新好友关系的属性（单向）**
###参数:
operUserId - 操作用户ID  
targetUserId - 目标用户ID  
remark- 指定备注 
privateFlag- 指定是否私密好友 
friendGroups- 指定用户分组 


###返回:
更新成功返回true，否则返回false

###抛出:
XhucException
#24.创建关注关系。
```java
boolean createFollow(Integer operUserId, Integer targetUserId, String remark) throws XhucException;
```
**创建关注关系**
###参数:
operUserId  - 操作用户id 
targetUserId - 目标用户id 
remark - 指定备注 


###返回:
创建成功返回true，否则返回false

###抛出:
XhucException
#25.更新关注关系。
```java
boolean updateFollow(Integer operUserId, Integer targetUserId, String remark) throws XhucException;
```
**更新关注关系。**
###参数:
operUserId  - 更新关注的操作用户id 
targetUserId - 更新关注的目标用户id  
remark - 指定备注 


###返回:
更新成功返回true，否则返回false

###抛出:
XhucException
#26.建立黑名单关系。
```java
boolean createBlacklist(Integer operUserId, Integer targetUserId) throws XhucException;
```
**建立黑名单关系。**
###参数:
operUserId - 操作用户id  
targetUserId - 黑名单用户id  

###返回:
创建成功返回true，否则返回false

###抛出:
XhucException
#27.创建屏蔽关系。
```java
boolean createBlock(Integer operUserId, Integer targetUserId) throws XhucException;
```
**创建屏蔽关系。**
###参数:

operUserId - 操作用户id  
targetUserId - 被屏蔽用户id  

###返回:
创建成功返回true，否则返回false

###抛出:
XhucException

#28.拒绝好友请求。
```java
boolean refuseFriendRequest(Integer startUserId, Integer endUserId) throws XhucException;
```
>**拒绝startUser 对endUser 的好友请求。**
###参数:

startUserId - 操作用户id  
endUserId  - 目标用户id  

###返回:
创建成功返回true，否则返回false

###抛出:
XhucException
#29.获取指定用户阻止的用户id列表

```java
List<Integer> getUserObstructs(Integer userId) throws XhucException
```
> **获取指定用户阻止的用户id列表。阻止关系为单向**

###参数：
userId - 操作用户id

###返回：
list - 用户阻止的用户id列表。可能为空列表

###抛出：
XhuaException
#30.统计指定用户阻止的用户数目

```java
long countUserObstructs(Integer userId) throws XhucException
```
**统计指定用户阻止的用户数目。**

###参数：
userId - 操作用户id

###返回：
long - 用户阻止的用户数目

###抛出：
XhuaException

#31.创建阻止关系。
```java
boolean createObstruct(Integer startUserId, Integer endUserId) throws XhucException;  
```
**操作用户阻止了目标用户。**
###参数:

startUserId - 操作用户id  
endUserId  - 目标用户id  

###返回:
创建成功返回true，否则返回false

###抛出:
XhucException
#32.建立好友请求关系。
```java
boolean createFriendRequest(Integer operUserId, Integer targetUserId, String message) throws XhucException;
```
**建立好友请求关系**
###参数:

operUserId - 发出请求的用户id 
targetUserId - 接收请求的用户id  
message - 附言  


###返回:
创建成功返回true，否则返回false

###抛出:
XhucException
#33.同意好友请求。
```java
boolean acceptFriendRequest(Integer operUserId, Integer targetUserId, String remark, boolean privateFlag,
			List<String> friendGroups) throws XhucException;
```
**同意好友请求**
###参数:

operUserId - 接受请求的操作用户id 
targetUserId - 发送请求的目标用户id 
remark - 指定备注 
privateFlag - 指定是否私密好友 
friendGroups - 指定好友分组 

###返回:
创建成功返回true，否则返回false

###抛出:
XhucException
#34.获取对指定用户的关注信息列表

```java
List<FollowsRelationship> getFollowsToUser(Integer userId, Long offset, Integer limit) throws XhrcException;
```
 > **获取对指定用户的关注信息列表。 跳过前offset个关注信息后，再取limit个关注信息。**

###参数：
userId - 操作用户id
offset - 获取时跳过的关注信息数，当offset为null或小于0时，默认设置为0
limit - 获取的关注信息数量，当offset为null，小于0，等于0，默认设置为20

###返回：
list - 返回对指定用户的关注信息的指定位置指定数目的列表（offset+1至offset+limit），可能为空列表。
        > 关注信息包括发起关注的用户userId（operUserId）、被关注用户的userId（targetUserId）、
        > 关注的时间（createAt）和备注信息（remark）

###抛出：
XhuaException
#35.统计用户每个好友分组的好友数量

```java
Map<String, Long> countUserFriendsByEachGroup(Integer userId) throws XhucException
```
 >**统计用户每个分组列表的好友数量**
    
###参数：
userId - 用户id

###返回：
Map - 用户每个好友分组的好友数量

###抛出：
XhuaException

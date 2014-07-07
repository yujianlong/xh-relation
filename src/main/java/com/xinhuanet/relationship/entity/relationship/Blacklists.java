package com.xinhuanet.relationship.entity.relationship;

import org.springframework.data.neo4j.annotation.RelationshipEntity;

import com.xinhuanet.relationship.common.constant.RelTypeName;

@RelationshipEntity(type = RelTypeName.BLACKLISTS)
public class Blacklists extends BaseRelationship {

}

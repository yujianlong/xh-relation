package com.xinhuanet.relationship.entity.relationship;

import org.springframework.data.neo4j.annotation.RelationshipEntity;

import com.xinhuanet.relationship.common.constant.RelTypeName;

@RelationshipEntity(type = RelTypeName.BLOCKS)
public class Blocks extends BaseRelationship {

}

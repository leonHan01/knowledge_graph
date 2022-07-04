package com.smart.neo4j.constant;

import org.neo4j.graphdb.RelationshipType;

public enum RelTypes implements RelationshipType {
    DEFAULT_RELATION,
    /**
     * 同个文件里的关系
     */
    FILE_INNER_RELATION,

    /**
     * 两个文件之间的关系
     */
    FILE_OUTER_RELATION;
}

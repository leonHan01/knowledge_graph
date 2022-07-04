package com.smart.domain.knowlage;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 知识点
 */
@Data
public class KnowledgeNode {

    /**
     * 知识点名称
     */
    private String name;

    /**
     * 知识点别名
     */
    private String alias1;

    /**
     * 知识点关联的url链接
     */
    private String url = "null";

    /**
     * 知识点level
     */
    private String level = "Lv4";

    /**
     * tag
     */
    private String tag = "null";
}

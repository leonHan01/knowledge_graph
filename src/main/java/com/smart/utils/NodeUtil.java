package com.smart.utils;

import com.smart.constant.KnowledgeNoodeConstant;
import com.smart.domain.knowlage.KnowledgeNode;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import java.util.HashMap;
import java.util.Map;

public class NodeUtil {

    private static Map<String, Node> nodeMap = new HashMap<>();

    private static Label LABE_L1 = Label.label("Lv1");
    private static Label LABE_L2 = Label.label("Lv2");
    private static Label LABE_L3 = Label.label("Lv3");
    private static Label LABE_L4 = Label.label("Lv4");

    public static Node genNeo4JNodeNodeName(String name, Map<String, String> metaMap, Transaction tx) {
        KnowledgeNode knowledgeNode = getKnowledgeNode(name, metaMap);
        Node node = getNeo4JNodeFromKnowledgeNode(knowledgeNode, tx);
        return node;
    }

    public static KnowledgeNode getKnowledgeNode(String name, Map<String, String> meta) {
        KnowledgeNode knowledgeNode = new KnowledgeNode();
        knowledgeNode.setName(name);
        knowledgeNode.setLevel(meta.get(KnowledgeNoodeConstant.KEY_LEVEL));
        knowledgeNode.setAlias1(meta.get(KnowledgeNoodeConstant.KEY_ALIAS1));
        //TODO 以后从元数据中解析更多有用信息
        return knowledgeNode;
    }

    public static Node getNeo4JNodeFromKnowledgeNode(KnowledgeNode knowledgeNode, Transaction tx) {
        Node node = nodeMap.get(knowledgeNode.getName());
        if (node == null) {
            Label label = LABE_L4;
            if (knowledgeNode.getLevel().equals("LV1")) {
                label = LABE_L1;
            } else if (knowledgeNode.getLevel().equals("LV2")) {
                label = LABE_L2;
            } else if (knowledgeNode.getLevel().equals("LV3")) {
                label = LABE_L3;
            }
            node = tx.createNode(label);
            if (StringUtils.isNotEmpty(knowledgeNode.getAlias1())) {
                node.setProperty(KnowledgeNoodeConstant.KEY_ALIAS1, knowledgeNode.getAlias1());
            } else {
                node.setProperty(KnowledgeNoodeConstant.KEY_ALIAS1, knowledgeNode.getName());
            }

            node.setProperty("name", knowledgeNode.getName());
            node.setProperty("Level", knowledgeNode.getLevel());
            node.setProperty("url", knowledgeNode.getUrl());
            node.setProperty("tag", knowledgeNode.getTag());
            nodeMap.put(knowledgeNode.getName(), node);
        }

        return node;
    }
}

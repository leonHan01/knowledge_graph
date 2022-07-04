package com.smart.neo4j;

import com.smart.domain.knowlage.KnowledgeNode;
import com.smart.neo4j.constant.RelTypes;
import com.smart.neo4j.db.DbmsHolder;
import com.smart.utils.FileUtil;
import com.smart.utils.NodeUtil;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

@Service
public class GraphService {

    private Map<String, Node> nodeMap = new HashMap<>();

    @Autowired
    private ObsidianNodeParse obsidianNodeParse;

    @PostConstruct
    public void initGraphService() throws IOException {
        DbmsHolder.initNeo4j();
        obsidianNodeParse.parseObsidianNode();
        //connectNodes();
    }

    public void connectNodes() {
        Set<String> list = FileUtil.readTxt("C:\\D\\pic\\old\\hanpic\\src\\main\\resources\\static\\graph.txt");
        try (Transaction tx = DbmsHolder.getGraphDb().beginTx()) {
            for (String knowledge : list) {
                String[] knowledgeArray = knowledge.split("#");

                Node fromNode = getNeo4JNode(knowledgeArray[0], tx);
                if (knowledgeArray.length == 1) {
                    continue;
                }

                String secondNodeKey = null;
                String relation = "default";
                // length 为3，则中间这个为relation
                if (knowledgeArray.length == 3) {
                    secondNodeKey = knowledgeArray[2];
                    relation = knowledgeArray[1];
                } else if (knowledgeArray.length == 2) {
                    secondNodeKey = knowledgeArray[1];
                }

                List<Node> toNodeList = getToNodes(secondNodeKey, tx);

                for (Node toNode : toNodeList) {
                    Relationship relationship = fromNode.createRelationshipTo(toNode, RelTypes.DEFAULT_RELATION);
                    relationship.setProperty("关系", relation);
                }
            }
            tx.commit();
        }
    }

    private KnowledgeNode parseKnowlageNode(String nodeInfo) {
        String[] nodeInfoArray = nodeInfo.split("_");
        KnowledgeNode knowledgeNode = new KnowledgeNode();
        for (String nodeInfoItem : nodeInfoArray) {
            if (nodeInfoItem.startsWith("lv") || nodeInfoItem.startsWith("LV") || nodeInfoItem.startsWith("Lv")) {
                knowledgeNode.setLevel(nodeInfoItem.toUpperCase());
            } else if (nodeInfoItem.contains(".")) {
                knowledgeNode.setUrl(nodeInfoItem);
            } else if (nodeInfoItem.startsWith("tag")) {
                knowledgeNode.setTag(nodeInfoItem.toUpperCase());
            }
            else {
                knowledgeNode.setName(nodeInfoItem.toUpperCase());
            }
        }
        return knowledgeNode;
    }

    private List<Node> getToNodes(String nodeKey, Transaction tx) {
        List<Node> list = new ArrayList<>();
        if (nodeKey.contains(",") && nodeKey.startsWith("[") && nodeKey.endsWith("]")) {
            nodeKey = nodeKey.substring(1, nodeKey.length() - 1);
            String[] nodeKeyArray = nodeKey.split(",");
            for (String key : nodeKeyArray) {
                list.add(getNeo4JNode(key, tx));
            }
        } else {
            list.add(getNeo4JNode(nodeKey, tx));
        }
        return list;
    }

    private Node getNeo4JNode(String nodeKey, Transaction tx) {
        KnowledgeNode knowledgeNode = parseKnowlageNode(nodeKey);
        Node node = NodeUtil.getNeo4JNodeFromKnowledgeNode(knowledgeNode, tx);
        return node;
    }

}
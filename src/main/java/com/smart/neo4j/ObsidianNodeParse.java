package com.smart.neo4j;

import com.smart.constant.KnowledgeNoodeConstant;
import com.smart.neo4j.constant.RelTypes;
import com.smart.neo4j.db.DbmsHolder;
import com.smart.utils.NodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ObsidianNodeParse {

    /**
     * key：文章标题 + 段落标题
     */
    public Map<String, Node> segmentNode = new HashMap<>();

    public void parseObsidianNode() throws IOException {
        File file = new File("C:\\Users\\41451\\Desktop\\个人知识库\\个人知识库");
        List<String> fileNameList = getAllMdFilesPath(file);

        try (Transaction tx = DbmsHolder.getGraphDb().beginTx()) {
            for (String fileName : fileNameList) {
                parseMdNode(fileName, tx);
            }

            for (String fileName : fileNameList) {
                parseNodeRelation(fileName, tx);
            }

            tx.commit();
        }
    }

    private void parseNodeRelation(String filePath, Transaction tx) throws IOException {
        String[] fileNameArray = filePath.split("\\\\");
        String fileName = fileNameArray[fileNameArray.length - 1].split("\\.")[0];
        Node currentNode = segmentNode.get(fileName);
        String currentSegmentKey = fileName;
        String targetSegmentKey = "";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (StringUtils.isBlank(line)) {
                    continue;
                }

                if (line.startsWith("# ")) {
                    currentSegmentKey = fileName + "#" + line.split("# ")[1];
                    currentNode = segmentNode.get(currentSegmentKey);
                }

                if (line.startsWith("## ")) {
                    currentSegmentKey = fileName + "#" + line.split("## ")[1];
                    currentNode = segmentNode.get(currentSegmentKey);
                }

                if (line.startsWith("### ")) {
                    currentSegmentKey = fileName + "#" + line.split("### ")[1];
                    currentNode = segmentNode.get(currentSegmentKey);
                }

                line = line.trim();
                if (line.startsWith("[[")) {
                    targetSegmentKey = line.split("]]")[0].substring(2);
                    Node relationNode = segmentNode.get(targetSegmentKey);
                    String relationName = "default";
                    if (line.split("]]").length >= 2) {
                        relationName = line.split("]]")[1];
                    }

                    System.out.println(currentSegmentKey + "-->" + targetSegmentKey);
                    Relationship relationship = currentNode.createRelationshipTo(relationNode, RelTypes.FILE_OUTER_RELATION);
                    relationship.setProperty("relation", relationName);
                }
            }
        }
    }

    private void parseMdNode(String filePath, Transaction tx) throws IOException {
        // 先为file创建Node
        String[] fileNameArray = filePath.split("\\\\");
        String fileName = fileNameArray[fileNameArray.length - 1].split("\\.")[0];
        Map<String, String > metaMap = new HashMap<>();
        metaMap.put(KnowledgeNoodeConstant.KEY_ALIAS1, fileName);
        metaMap.put(KnowledgeNoodeConstant.KEY_LEVEL, "LV2");
        Node fileNode = NodeUtil.genNeo4JNodeNodeName(fileName, metaMap, tx);
        segmentNode.put(fileName, fileNode);

        String nodeLv1 = fileName;
        String nodeLv2 = fileName;
        Node lv1Node = null;
        Node lv2Node = null;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (StringUtils.isBlank(line)) {
                    continue;
                }

                if (!line.startsWith("#")) {
                    continue;
                }
                String segmentName = null;
                if (line.startsWith("# ")) {
                    segmentName = line.split("# ")[1];
                    metaMap.put(KnowledgeNoodeConstant.KEY_ALIAS1, segmentName);
                    metaMap.put(KnowledgeNoodeConstant.KEY_LEVEL, "LV3");
                    nodeLv1 = fileName + "-" + segmentName;

                    lv1Node = NodeUtil.genNeo4JNodeNodeName(nodeLv1, metaMap, tx);
                    segmentNode.put(fileName + "#" + segmentName, lv1Node);
                    Relationship relationship = fileNode.createRelationshipTo(lv1Node, RelTypes.FILE_INNER_RELATION);
                    relationship.setProperty("relation", "子结构");
                    nodeLv2 = null;
                } else if (line.startsWith("## ")) {
                    segmentName = line.split("## ")[1];
                    metaMap.put(KnowledgeNoodeConstant.KEY_ALIAS1, segmentName);
                    metaMap.put(KnowledgeNoodeConstant.KEY_LEVEL, "LV4");
                    nodeLv2 = nodeLv1 + "-" + segmentName;
                    lv2Node = NodeUtil.genNeo4JNodeNodeName(nodeLv2, metaMap, tx);
                    if (lv1Node != null) {
                        Relationship relationship = lv1Node.createRelationshipTo(lv2Node, RelTypes.FILE_INNER_RELATION);
                        relationship.setProperty("relation", "子结构");
                    } else {
                        Relationship relationship = fileNode.createRelationshipTo(lv2Node, RelTypes.FILE_INNER_RELATION);
                        relationship.setProperty("relation", "子结构");
                    }

                    String segmentKey = fileName + "#" + segmentName;
                    segmentNode.put(segmentKey, lv2Node);
                } else if (line.startsWith("### ")) {
                    segmentName = line.split("### ")[1];
                    String nodeLv3 = nodeLv2 + "-" + segmentName;
                    metaMap.put(KnowledgeNoodeConstant.KEY_ALIAS1, segmentName);
                    metaMap.put(KnowledgeNoodeConstant.KEY_LEVEL, "LV4");
                    Node lv3Node = NodeUtil.genNeo4JNodeNodeName(nodeLv3, metaMap, tx);
                    if (lv2Node != null) {
                        Relationship relationship = lv2Node.createRelationshipTo(lv3Node, RelTypes.FILE_INNER_RELATION);
                        relationship.setProperty("relation", "子结构");
                    } else if (lv1Node != null) {
                        Relationship relationship = lv1Node.createRelationshipTo(lv3Node, RelTypes.FILE_INNER_RELATION);
                        relationship.setProperty("relation", "子结构");
                    } else {
                        Relationship relationship = fileNode.createRelationshipTo(lv3Node, RelTypes.FILE_INNER_RELATION);
                        relationship.setProperty("relation", "子结构");
                    }

                    String segmentKey = fileName + "#" + segmentName;
                    segmentNode.put(segmentKey, lv3Node);
                }

                System.out.println(line);
            }
        }
    }


    private List<String> getAllMdFilesPath(File file) {
        List<String> result = new ArrayList<>();
        File[] listFiles = file.listFiles();
        for (File file1 : listFiles) {
            if (file1.isDirectory()) {
                File file2 = new File(file1.getAbsolutePath());
                result.addAll(getAllMdFilesPath(file2));
            } else if (file1.isFile()) {
                if (file1.getName().endsWith("md")) {
                    result.add(file1.getPath());
                    System.out.println(file1.getPath());
                }
            }
        }
        return result;
    }



}

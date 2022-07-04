package com.smart.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@RestController
public class FileUtil {

    public static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

    static int deleteFileSize = 0;


    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName 要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            LOG.info("删除文件失败:" + fileName + "不存在！");
            return false;
        } else {
            return deleteFile(fileName);
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            deleteFileSize += getFileSize(file);
            if (file.delete()) {
                LOG.info("删除单个文件" + fileName + "成功！");
                LOG.info("总删除文件大小：" + deleteFileSize + "MB");
                return true;
            } else {
                LOG.info("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            LOG.info("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 获取文件长度
     *
     * @param file
     */
    public static double getFileSize(File file) {
        if (file.exists() && file.isFile()) {
            double fileSize = file.length() / (1024D * 1024D);
            return fileSize;
        }
        return 0;
    }

    public static Set<String> readTxt(String filePath) {
        Set<String> list = new HashSet<>();
        try {
            String encoding = "UTF-8";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);// 考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    list.add(lineTxt);
                }
                bufferedReader.close();
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void main(String[] args) {
        readTxt("C:\\D\\pic\\old\\hanpic\\src\\main\\resources\\static\\mtl.txt");
    }
}

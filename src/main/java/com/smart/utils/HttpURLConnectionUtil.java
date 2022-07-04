package com.smart.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class HttpURLConnectionUtil {

    public static final Logger LOG = LoggerFactory.getLogger(HttpURLConnectionUtil.class);


    public static String doGet(String url, String cookie) {
        try {
            InputStream inputStream = getInputStream(url, cookie);
            // 封装输入流is，并指定字符集
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            // 存放数据
            StringBuffer sbf = new StringBuffer();
            String temp = null;
            while ((temp = br.readLine()) != null) {
                sbf.append(temp);
                sbf.append("\r\n");
            }
            return sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    // 通过get请求得到读取器响应数据的数据流
    public static InputStream getInputStream(String url, String cookie) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            conn.setReadTimeout(25000);
            conn.setConnectTimeout(25000);
            conn.setRequestMethod("GET");
            if (StringUtils.isNotBlank(cookie)) {
                conn.setRequestProperty("Cookie", cookie);
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:36.0) Gecko/20100101 Firefox/36.0");
                conn.setInstanceFollowRedirects(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);
            }
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = conn.getInputStream();
                return inputStream;
            }

        } catch (IOException e) {
            LOG.error("下载图片失败:{}", url, e);
        }
        return null;
    }


    // 通过get请求得到读取器响应数据的数据流
    public static InputStream getInputStreamByGet(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(25000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = conn.getInputStream();
            return inputStream;
        }
        return null;
    }

    // 通过get请求得到读取器响应数据的数据流
    public static InputStream getInputStreamByGet(String url, String referer) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            conn.setReadTimeout(60000);
            conn.setConnectTimeout(60000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Referer", referer);

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = conn.getInputStream();
                return inputStream;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 将服务器响应的数据流存到本地文件
    public static void saveData(InputStream is, File file) {
        try {
            saveDataThrowExp(is, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 将服务器响应的数据流存到本地文件
    public static void saveDataThrowExp(InputStream is, File file) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(is);
             BufferedOutputStream bos = new BufferedOutputStream(
                     new FileOutputStream(file))) {
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
                bos.flush();
            }
        }
    }
}
/*
 * @(#)HttpUtil.java 2018年3月28日 下午1:30:01 com.thunisoft.a6 Copyright 2018
 * Thuisoft, Inc. All rights reserved. THUNISOFT PROPRIETARY/CONFIDENTIAL. Use
 * is subject to license terms.
 */
package com.yuanzhy.tools.template.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * HttpUtil
 * 
 * @author yuanzhy
 * @date 2018-3-29
 */
public final class HttpUtil {

    private static final int CONNECT_TIME_OUT = 5000;

    private static final int READ_TIME_OUT = 5000;

    /**
     * get请求，url中已拼接好参数
     * @param httpUrl
     * @return
     */
    public static InputStream get(String httpUrl) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(httpUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(CONNECT_TIME_OUT);
            conn.setReadTimeout(READ_TIME_OUT);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            return conn.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            //            if (conn != null) {
            //                conn.disconnect();
            //            }
        }
    }

    /**
     * post传递参数方式
     * @param httpUrl httpUrl
     * @param params params
     * @return
     */
    public static InputStream post(String httpUrl, Map<String, String> params) {
        HttpURLConnection conn = null;
        PrintWriter pw = null;
        try {
            URL url = new URL(httpUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(CONNECT_TIME_OUT);
            conn.setReadTimeout(READ_TIME_OUT);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            if (params != null && !params.isEmpty()) {
                pw = new PrintWriter(conn.getOutputStream());
                StringBuilder sb = new StringBuilder();
                params.entrySet().forEach(entry -> {
                    sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                });
                sb.deleteCharAt(sb.length() - 1);
                pw.write(sb.toString());
                pw.flush();
            }
            return conn.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(pw);
            //            if (conn != null) {
            //                conn.disconnect();
            //            }
        }
    }

    public static InputStream postJson(String httpUrl, String json) {
        HttpURLConnection conn = null;
        PrintWriter pw = null;
        try {
            URL url = new URL(httpUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(CONNECT_TIME_OUT);
            conn.setReadTimeout(READ_TIME_OUT);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            pw = new PrintWriter(conn.getOutputStream());
            pw.write(json);
            pw.flush();
            return conn.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(pw);
            //            if (conn != null) {
            //                conn.disconnect();
            //            }
        }
    }
}

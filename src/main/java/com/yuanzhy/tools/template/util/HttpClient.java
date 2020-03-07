package com.yuanzhy.tools.template.util;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * @author yuanzhy
 * @date 2020-01-17
 */
public class HttpClient {

    private static final int CONNECT_TIME_OUT = 5000;

    private static final int READ_TIME_OUT = 5000;

    private String cookie;

    /**
     * get请求，url中已拼接好参数
     *
     * @param httpUrl
     * @return
     */
    public String get(String httpUrl) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(httpUrl);
            conn = (HttpURLConnection) url.openConnection();
            checkAndSetCert(conn);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(CONNECT_TIME_OUT);
            conn.setReadTimeout(READ_TIME_OUT);
            conn.setUseCaches(false);
            return string(conn.getInputStream());
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
     *
     * @param httpUrl httpUrl
     * @param params  params
     * @return
     */
    public String post(String httpUrl, Map<String, String> params) {
        HttpURLConnection conn = null;
        PrintWriter pw = null;
        try {
            URL url = new URL(httpUrl);
            conn = (HttpURLConnection) url.openConnection();
            checkAndSetCert(conn);
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(CONNECT_TIME_OUT);
            conn.setReadTimeout(READ_TIME_OUT);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
            conn.setRequestProperty("Origin", "http://xxx");
            conn.setRequestProperty("Host", "xxx");
            conn.setRequestProperty("Referer", "");
            conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
            if (cookie != null) {
//                conn.setRequestProperty("Cookie", "lsn="+ConfigUtil.getProperty("tools.autosign.username") + "; " + cookie);
                conn.setRequestProperty("Cookie", cookie);
            }
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
            String key = null;
            for (int i = 1; (key = conn.getHeaderFieldKey(i)) != null; i++) {
                if (key.equalsIgnoreCase("set-cookie")) {
                    String cookieVal = conn.getHeaderField(i);
                    if (cookieVal.contains("JSESSIONID")) {
                        cookie = cookieVal + ";";
                        break;
                    }
                    //                    cookieVal = cookieVal.substring(0, cookieVal.indexOf(";"));
//                    sessionId = sessionId + cookieVal + ";";
                }
            }

            return string(conn.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (pw != null) {
                pw.close();
            }
            //            if (conn != null) {
            //                conn.disconnect();
            //            }
        }
    }

    private void checkAndSetCert(URLConnection conn) {
        if (conn instanceof HttpsURLConnection) {
            try {
                SSLContext sc = createSslContext();
                ((HttpsURLConnection) conn).setSSLSocketFactory(sc.getSocketFactory());
                ((HttpsURLConnection) conn).setHostnameVerifier((s, sslSession) -> true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String string(InputStream is) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(is))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static SSLContext createSslContext() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSL");

        sc.init(null, new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }}, new java.security.SecureRandom());

        return sc;
    }

    public String postJson(String httpUrl,Map<String, String> headers, String json) {
        PrintWriter pw = null;
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(CONNECT_TIME_OUT);
            conn.setReadTimeout(READ_TIME_OUT);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            headers.forEach((key, value) -> conn.setRequestProperty(key, value));
//            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            pw = new PrintWriter(conn.getOutputStream());
            pw.write(json);
            pw.flush();
            return string(conn.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (pw != null) {
                pw.close();
            }
            //            if (conn != null) {
            //                conn.disconnect();
            //            }
        }
    }
}

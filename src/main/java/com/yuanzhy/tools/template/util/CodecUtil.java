package com.yuanzhy.tools.template.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @Author yuanzhy
 * @Date 2018/8/17
 */
public class CodecUtil {


    public static String encode(String source) {
        try {
            return URLEncoder.encode(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return source;
        }
    }

    public static String decode(String source) {
        try {
            return URLDecoder.decode(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return source;
        }
    }
}

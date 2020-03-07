package com.yuanzhy.tools.template.util;

import java.security.MessageDigest;

/**
 *
 * @author yuanzhy
 * @date 2020-01-17
 */
public class MD5 {

    public static String digest(String source) {
        try {
            return bytesToHexString(MessageDigest.getInstance("MD5").digest(source.getBytes("UTF-8")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String bytesToHexString(byte[] bArr) {
        StringBuffer sb = new StringBuffer(bArr.length);
        String sTmp;

        for (int i = 0; i < bArr.length; i++) {
            sTmp = Integer.toHexString(0xFF & bArr[i]);
            if (sTmp.length() < 2)
                sb.append(0);
            sb.append(sTmp.toUpperCase());
        }

        return sb.toString();
    }
}

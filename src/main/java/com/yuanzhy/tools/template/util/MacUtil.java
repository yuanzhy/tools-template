package com.yuanzhy.tools.template.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 *
 * @author yuanzhy
 * @date 2018-12-20
 */
public class MacUtil {
    /**
     * 获取unix网卡的mac地址. 非windows的系统默认调用本方法获取. 如果有特殊系统请继续扩充新的取mac地址方法.
     *
     * @return mac地址
     */
    public static String getUnixMACAddress() {
        String mac = null;
        try {
            // linux下的命令，一般取eth0作为本地主网卡
            Process process = Runtime.getRuntime().exec("ifconfig");
            // 显示信息中包含有mac地址信息
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(),"utf-8"))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    line = line.toLowerCase();
                    if (line.contains("ether")) {
                        mac = StringUtil.substringAfter(line, "ether").trim().split(" ")[0].trim();
                        break;
                    }
                    if (line.contains("hwaddr")) {
                        mac = StringUtil.substringAfter(line, "hwaddr").trim().split(" ")[0].trim();
                        break;
                    }
                }
            }
        }
        catch (Exception e) {
            // ignore
//            System.out.println("unix/linux方式未获取到网卡地址");
        }
        return mac;
    }

    /**
     * 获取widnows网卡的mac地址.
     *
     * @return mac地址
     */
    public static String getWindowsMACAddress() {
        String mac = null;
        try {
            // windows下的命令，显示信息中包含有mac地址信息
            Process process = Runtime.getRuntime().exec("ipconfig /all");
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "gbk"))) {
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    // 寻找标示字符串[physical
                    line = line.toLowerCase();
                    if (line.contains("physical address")) {
                        mac = StringUtil.substringAfter(line, ":").trim();
                        break;
                    }
                    if (line.contains("物理地址")) {
                        mac = StringUtil.substringAfter(line, ":").trim();
                        break;
                    }
                }
            }
        }
        catch (Exception e) {
            // ignore
//            System.out.println("widnows方式未获取到网卡地址");
        }
        return mac;
    }

    /**
     * windows 7 专用 获取MAC地址
     *
     * @return
     * @throws Exception
     */
    public static String getWindows7MACAddress() {
        StringBuffer sb = new StringBuffer();
        try {
            // 获取本地IP对象
            InetAddress ia = InetAddress.getLocalHost();
            // 获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
            byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
            // 下面代码是把mac地址拼装成String
            for (int i = 0; i < mac.length; i++) {
                // mac[i] & 0xFF 是为了把byte转化为正整数
                String s = Integer.toHexString(mac[i] & 0xFF);
                sb.append(s.length() == 1 ? 0 + s : s);
            }
        }
        catch (Exception ex) {
            // ignore
//            System.out.println("windows 7方式未获取到网卡地址");
        }
        return sb.toString();
    }

    /**
     * 获取MAC地址
     *
     */
    public static String getMACAddress() {
        String mac = "";
        if (OSUtil.isWindows()) {
            mac = getWindowsMACAddress();
            if (StringUtil.isEmpty(mac)) {
                mac = getWindows7MACAddress();
            }
        } else {
            mac = getUnixMACAddress();
        }
        if (StringUtil.isBlank(mac)) {
            return "";
        } else {
            return mac.toUpperCase();
        }
    }

//    public static void main(String[] a) {
//        System.out.println(getMACAddress());
//    }
}

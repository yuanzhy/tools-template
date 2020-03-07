package com.yuanzhy.tools.template.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @author yuanzhy
 * @date 2018/6/13
 */
public class ConfigUtil {

    /**
     *
     */
    private static Properties props = new Properties();

    static {
        // 先从平级目录找config.properties
        InputStreamReader in = null;
        try {
            File configFile = new File(getJarPath().concat("/config.properties"));
            if (configFile.exists()) {
                System.out.println("auto-sign.jar同级目录下找到config.properties，读取此配置");
                in = new InputStreamReader(new FileInputStream(configFile), "UTF-8");
            } else {
                System.out.println("auto-sign.jar同级目录下没有config.properties，默认读取jar包中的配置");
                in = new InputStreamReader(ConfigUtil.class.getClassLoader().getResourceAsStream("config.properties"), "UTF-8");
            }
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getProperty(String key) {
        String p = props.getProperty(key);
//        if (p.startsWith("EN:")) {
//            Base64.getDecoder().decode(p.substring(3));
//        }
        return p;
    }

    public static boolean getBoolProperty(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }

    public static String getJarPath() {
        String path = ConfigUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String result = new File(path).getParentFile().getAbsolutePath();
        return CodecUtil.decode(result).replace("\\", "/");
    }
}

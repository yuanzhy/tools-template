package com.yuanzhy.tools.template.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yuanzhy
 * @date 2018/8/11
 */
public final class ArgumentUtil {

    private static Logger log = LoggerFactory.getLogger(ArgumentUtil.class);

    private static Map<String, Object> argsMap = new HashMap<String, Object>();

//    private static String[] args;

    public static String getString(String key) {
        Object value = argsMap.get(key);
        return value == null ? null : value.toString();
    }

    public static Date getDate(String key) {
        String dateStr = getString(key);
        if (dateStr == null) {
            return null;
        }
        String dateKey = key.concat("-date");
        Date date = (Date)argsMap.get(dateKey);
        if (date == null) {
            date = DateUtil.parseDate(dateStr);
            argsMap.put(dateKey, date); // 缓存起来，这样只解析一次
        }
        return date;
    }

    /**
     * 用于判断boolean类型参数
     * @param key key
     * @return
     */
    public static boolean getBoolean(String key) {
        return argsMap.containsKey(key);
    }

    public static void parseArgs(String[] args) {
        if (args == null || args.length == 0) {
            argsMap.put("path", getPath(null));
            return;
        }
//        ArgumentUtil.args = args;
        argsMap.put("path", getPath(args[0])); // 兼容之前的直接传递path
        for (String arg : args) {
            if (arg.startsWith("--")) {
                if (arg.contains("=")) {
                    String key = arg.substring(2, arg.indexOf("="));
                    String value = arg.substring(arg.indexOf("=") + 1);
                    if ("path".equals(key)) {
                        value = getPath(value);
                    }
                    argsMap.put(key, value);
                } else { // boolean 类型的参数 如 --console，使用命令行方式运行
                    String key = arg.substring(2);
                    argsMap.put(key, true);
                }
            }
        }
    }

    /**
     * @param param param
     * @return
     */
    private static String getPath(String param) {
        String jarPath = ConfigUtil.getJarPath();
        if (param == null || param.startsWith("--")) {
            return jarPath;
        }
        if (isAbsolutePath(param)) {
            return param;
        } else if (param.startsWith("./")) {
            return jarPath + param.substring(1);
        } else if (param.startsWith("../")) {
            int count = StringUtil.countMatches(param, "../");
            File path = new File(jarPath);
            for (int i = 0; i < count; i++) {
                path = path.getParentFile();
            }
            return path.getAbsolutePath();
        } else {
            return jarPath + "/" + param;
        }
    }

    private static boolean isAbsolutePath(String path) {
        if (path.startsWith("/")) {
            return true;
        }
        if (isWindows()) {// windows
            if (path.contains(":") || path.startsWith("\\")) {
                return true;
            }
        } else {// not windows, just unix compatible
            if (path.startsWith("~")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否windows系统
     */
    private static boolean isWindows() {
        boolean isWindows = false;
        try {
            String osName = System.getProperty("os.name").toLowerCase();
            String sharpOsName = osName.replaceAll("windows", "{windows}")
                    .replaceAll("^win([^a-z])", "{windows}$1").replaceAll("([^a-z])win([^a-z])", "$1{windows}$2");
            isWindows = sharpOsName.contains("{windows}");
        } catch (Exception e) {
            log.warn("获取操作系统类型出错", e);
        }
        return isWindows;
    }
}

package com.yuanzhy.tools.template.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.AutoCloseInputStream;

import java.io.File;

public class GitUtil {

    public static String readProjectServerUrl(String path) {
        File _path = new File(path);
        String postPath = "";
        while (true) {
            File configFile = new File(new File(_path, ".git"), "config");
            if (configFile.exists() && configFile.isFile()) {
                try {
                    IniReader reader = new IniReader(configFile.getAbsolutePath());
                    String url = reader.getValue("remote \"origin\"", "url");
                    if (!StringUtil.isEmpty(postPath))
                        url = url + "/" + postPath;
                    return url;
                } catch (Exception e) {
                    return null;
                }
            }

            if (!StringUtil.isEmpty(postPath)) {
                postPath = _path.getName() + "/" + postPath;
            } else {
                postPath = _path.getName();
            }
            _path = _path.getParentFile();
            if (_path == null)
                break;
        }
        return null;
    }

    /**
     * 读取用户名
     * @return
     */
    public static String readUsername() {
        // 先尝试使用命令行解决 git config user.name
        String username = "";
        try {
            Process p = Runtime.getRuntime().exec("git config user.name");
            username = IOUtils.toString(new AutoCloseInputStream(p.getInputStream()), "UTF-8").trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtil.isEmpty(username)) {
            try {
                Process p = Runtime.getRuntime().exec("git config --global user.name");
                username = IOUtils.toString(new AutoCloseInputStream(p.getInputStream()), "UTF-8").trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (StringUtil.isEmpty(username)) {
            // 再尝试读取.gitconfig文件
            String homePath = System.getProperties().getProperty("user.home");
            File configFile = new File(homePath, ".gitconfig");
            if (configFile.exists() && configFile.isFile()) {
                try {
                    IniReader reader = new IniReader(configFile.getAbsolutePath());
                    return reader.getValue("user", "name");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return username;
    }

    public static void main(String[] args) {
        System.out.println(readProjectServerUrl("e:\\work\\artery-release\\src"));
        System.out.println(readProjectServerUrl("e:\\work\\artery-release"));
        System.out.println(readProjectServerUrl("E:\\work\\Artery\\artery-api"));
    }
}

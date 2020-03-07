package com.yuanzhy.tools.template.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class IniReader {

    protected Map<String, Properties> sections = new HashMap<>();

    private transient String currentSection;

    private transient Properties current;

    /**
     * 构造函数 
     * @param filename
     * @throws IOException
     */
    public IniReader(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            read(reader);
        }
    }

    /**
     * 读取文件 
     * @param reader reader
     * @throws IOException
     */
    protected void read(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            parseLine(line);
        }
    }

    /**
     * 解析配置文件行 
     * @param line line
     */
    protected void parseLine(String line) {
        line = line.trim();
        if (line.matches("\\[.*\\]")) {
            currentSection = line.replaceFirst("\\[(.*)\\]", "$1");
            current = new Properties();
            sections.put(currentSection, current);
        } else if (line.matches(".*=.*")) {
            if (current != null) {
                int i = line.indexOf('=');
                String name = line.substring(0, i).trim();
                String value = line.substring(i + 1).trim();
                current.setProperty(name, value);
            }
        }
    }

    /**
     * 获取值 
     * @param section section
     * @param name name
     * @return
     */
    public String getValue(String section, String name) {
        Properties p = sections.get(section);
        if (p == null) {
            return null;
        }
        return p.getProperty(name);
    }

    /**
     * 是否包含key 
     * @param section section
     * @param key key
     * @return
     */
    public boolean containsKey(String section, String key) {
        Properties p = sections.get(section);
        return p.contains(key);
    }
}  
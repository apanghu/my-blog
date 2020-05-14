package com.my.blog.website.utils;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Jesse-liu
 * @description: 配置文件读取工具类
 * @date 2020/5/13 11:24
 */
public class PropertiesUtil {
    //配置文件的路径
    private String configPath = null;

    /**
     * 配置文件对象
     */
    private Properties props = null;

    public PropertiesUtil() throws IOException {
        if (props == null) {
            InputStreamReader in = new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream("ip-black.properties"), "utf-8");
            props = new Properties();
            props.load(in);
            //关闭资源
            in.close();
        }
    }

    /**
     * @param path 配置文件路径
     * @return : null
     * @author Jesse-liu
     * @date 2020/5/13
     * @description: 有参构造函数，用于sh运行，自动找到classpath下的path路径。
     **/
    public PropertiesUtil(String path) throws IOException {
        if (props == null) {
            InputStreamReader in = new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(path), "utf-8");
            configPath = path;
            props = new Properties();
            props.load(in);
            //关闭资源
            in.close();
        }
    }

    /**
     * 根据key值读取配置的值
     * Jun 26, 2010 9:15:43 PM
     *
     * @param key key值
     * @return key 键对应的值
     * @throws IOException
     * @author 朱志杰
     */
    public String readValue(String key) throws IOException {
        return props.getProperty(key);
    }

    /**
     * 读取properties的全部信息
     *
     * @throws FileNotFoundException 配置文件没有找到
     * @throws IOException           关闭资源文件，或者加载配置文件错误
     */
    public Map<String, String> readAllProperties() throws FileNotFoundException, IOException {
        //保存所有的键值
        Map<String, String> map = new HashMap<String, String>();
        Enumeration en = props.propertyNames();
        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();
            String value = props.getProperty(key);
            map.put(key, value);
        }
        return map;
    }

    private static String longToIp(Long number) {
        String ip = "";
        for (int i = 3; i >= 0; i--) {
            long tmp = number >> (8 * i);
            ip += String.valueOf((tmp & 0xff));
            if (i != 0) {
                ip += ".";
            }
        }
        return ip;
    }

    /**
     * 设置某个key的值,并保存至文件。
     * by https://blog.csdn.net/qq_40147276/article/details/91976753
     *
     * @param key key值
     * @return key 键对应的值
     * @throws IOException
     */
    public void setValue(String key, String value) throws IOException {
        Properties prop = new Properties();
        InputStreamReader fis = new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(this.configPath), "utf-8");
        // 从输入流中读取属性列表（键和元素对）
        prop.load(fis);
        // 调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。
        // 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("ip-black.properties"), "utf-8");
        prop.setProperty(key, value);
        // 以适合使用 load 方法加载到 Properties 表中的格式，
        // 将此 Properties 表中的属性列表（键和元素对）写入输出流
        prop.store(writer, "last update");
        //关闭文件
        fis.close();
        writer.close();
    }

    /**
     * @return the props
     */
    public Properties getProps() {
        return props;
    }
}

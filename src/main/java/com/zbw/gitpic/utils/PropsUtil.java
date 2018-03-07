package com.zbw.gitpic.utils;

import java.io.*;
import java.util.Properties;

/**
 * 属性文件助手
 *
 * @author zbw
 * @create 2017/11/20 11:23
 */
public final class PropsUtil {

    synchronized static public Properties loadProps(String propsName) {
        String jarUrl = JarUtil.getJarDir();
        String fileUrl = jarUrl + File.separator + propsName;
        File propFile = new File(fileUrl);
        if (!propFile.exists()) {
            try {
                propFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Properties props = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(propFile);
            props.load(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return props;
    }

    public static void saveString(Properties props, String propsName, String key, String value) {
        props.setProperty(key, value);
        try {
            String jarUrl = JarUtil.getJarDir();
            String fileUrl = jarUrl + File.separator + propsName;
            FileOutputStream fos = new FileOutputStream(new File(fileUrl));
            props.store(fos, "This is the properties for gitPic");
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取字符型属性
     */
    public static String getString(Properties props, String key) {
        String value = "";
        if (props.containsKey(key)) {
            value = props.getProperty(key);
        }
        return value;
    }

    /**
     * 获取字符型属性（带有默认值）
     */
    public static String getString(Properties props, String key, String defalutValue) {
        String value = defalutValue;
        if (props.containsKey(key)) {
            value = props.getProperty(key);
        }
        return value;
    }

    /**
     * 获取数值型属性
     */
    public static int getNumber(Properties props, String key) {
        int value = 0;
        if (props.containsKey(key)) {
            value = CastUtil.castInt(props.getProperty(key));
        }
        return value;
    }

    /**
     * 获取数值型属性（带有默认值）
     *
     * @param props
     * @param key
     * @param defaultValue
     * @return
     */
    public static int getNumber(Properties props, String key, int defaultValue) {
        int value = defaultValue;
        if (props.containsKey(key)) {
            value = CastUtil.castInt(props.getProperty(key));
        }
        return value;
    }

    /**
     * 获取布尔型属性
     */
    public static boolean getBoolean(Properties props, String key) {
        return getBoolean(props, key, false);
    }

    /**
     * 获取布尔型属性（带有默认值）
     */
    public static boolean getBoolean(Properties props, String key, boolean defalutValue) {
        boolean value = defalutValue;
        if (props.containsKey(key)) {
            value = CastUtil.castBoolean(props.getProperty(key));
        }
        return value;
    }

}

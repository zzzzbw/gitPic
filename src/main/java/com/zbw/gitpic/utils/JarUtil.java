package com.zbw.gitpic.utils;

import java.io.File;

/**
 * @author zbw
 * @create 2018/3/7 15:03
 */
public class JarUtil {

    /**
     * 获取jar绝对路径
     *
     * @return
     */
    public static String getJarPath() {
        File file = getFile();
        if (file == null) {
            return null;
        }
        return file.getAbsolutePath();
    }

    /**
     * 获取jar目录
     *
     * @return
     */
    public static String getJarDir() {
        File file = getFile();
        if (file == null) {
            return null;
        }
        return getFile().getParent();
    }

    /**
     * 获取jar包名
     *
     * @return
     */
    public static String getJarName() {
        File file = getFile();
        if (file == null) {
            return null;
        }
        return getFile().getName();
    }

    /**
     * 获取当前Jar文件
     *
     * @return
     */
    private static File getFile() {
        // 关键是这行...
        String path = JarUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        try {
            path = java.net.URLDecoder.decode(path, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return new File(path);
    }
}

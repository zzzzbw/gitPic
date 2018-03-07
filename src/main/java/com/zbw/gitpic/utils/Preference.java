package com.zbw.gitpic.utils;

import java.util.Properties;

/**
 * @author zbw
 * @create 2018/2/27 22:51
 */
public class Preference {

    private static final Preference INSTANCE = new Preference();

    private Properties properties;

    private String projectPath;

    private String picPath;

    private String gitUsername;

    private String gitPassword;

    private Preference() {
        init();
    }

    public static Preference getInstance() {
        return INSTANCE;
    }

    private void init() {
        if (null == properties) {
            properties = PropsUtil.loadProps(Constants.SETTING_FILE);
        }
        if (null != properties) {
            projectPath = PropsUtil.getString(properties, Constants.SETTING_PROJECT_PATH);
            picPath = PropsUtil.getString(properties, Constants.SETTING_PIC_PATH);
            gitUsername = PropsUtil.getString(properties, Constants.SETTING_GIT_USERNAME);
            gitPassword = PropsUtil.getString(properties, Constants.SETTING_GIT_PASSWORD);
        }
    }

    public void saveProjectPath(String projectPath) {
        PropsUtil.saveString(properties, Constants.SETTING_FILE, Constants.SETTING_PROJECT_PATH, projectPath);
        this.projectPath = projectPath;
    }

    public void savePicPath(String picPath) {
        PropsUtil.saveString(properties, Constants.SETTING_FILE, Constants.SETTING_PIC_PATH, picPath);
        this.picPath = picPath;
    }

    public void saveGitUsername(String username) {
        PropsUtil.saveString(properties, Constants.SETTING_FILE, Constants.SETTING_GIT_USERNAME, username);
        this.gitUsername = username;
    }

    public void saveGitPassword(String password) {
        PropsUtil.saveString(properties, Constants.SETTING_FILE, Constants.SETTING_GIT_PASSWORD, password);
        this.gitPassword = password;
    }


    public String getProjectPath() {
        return projectPath;
    }

    public String getPicPath() {
        return picPath;
    }

    public String getGitUsername() {
        return gitUsername;
    }

    public String getGitPassword() {
        return gitPassword;
    }
}

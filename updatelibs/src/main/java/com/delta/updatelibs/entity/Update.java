package com.delta.updatelibs.entity;

/**
 * 更新json配置文件对应JavaBean类
 * Created by Shufeng.Wu on 2016/12/26.
 */

public class Update {

    /**
     * version : 2.0
     * versionCode : 2
     * url : http://172.22.35.177:8081/app-debug.apk
     * description : 检测到最新版本，请及时更新！
     */

    private String version;
    private String versionCode;
    private String url;
    private String description;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

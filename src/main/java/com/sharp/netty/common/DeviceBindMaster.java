package com.sharp.netty.common;

import java.io.Serializable;

/***
 * @author Duo.Cui
 * 主用户信息数据类
 */
public class DeviceBindMaster implements Serializable {
    private static final long serialVersionUID = -7050565178325326535L;


    private String code;
    private String info;
    private String data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }


}

package com.jiedu.project.lovefamily.mode;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/2.
 */
public class User implements Serializable{
    //用户昵称
    private String userName;
    //用户状态标示文字
    private String userStatue;
    //用户头像地址
    private String photoUrl;
    //用户唯一标示
    private String monitoredId;
    //用户的监控人标示
    private String monitorId;
    //添加安全范围时用到的id
    private String id;
    //用户状态标示数字
    private String statueNum;
    //住址
    private String address;
    //生日
    private String birthday;
//性别
    private String sex;
    //手机号码
    private String phone;

    //上传时间
    public  String uploadTime;

    //上传频率
    public String frequency;

    //是否上传
    public int isUploadLocation;


    public int login;//1表示自动登录

    public String code;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserStatue() {
        return userStatue;
    }

    public String getId() {
        return id;
    }

    public String getMonitoredId() {
        return monitoredId;
    }

    public String getMonitorId() {
        return monitorId;
    }

    public String getStatueNum() {
        return statueNum;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserStatue(String userStatue) {
        this.userStatue = userStatue;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMonitoredId(String monitoredId) {
        this.monitoredId = monitoredId;
    }

    public void setMonitorId(String monitorId) {
        this.monitorId = monitorId;
    }

    public void setStatueNum(String statueNum) {
        this.statueNum = statueNum;
    }
}

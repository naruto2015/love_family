package com.jiedu.project.lovefamily.adapter;

/**
 * Created by Administrator on 2016/10/18.
 */
public class Child {
    private String dindwei;
    private String shipin;
    private String dianhua ;
    private String xinxi;
    private String shezhi;

    public Child(){
        dindwei = "定位";
        shipin = "视频";
        dianhua = "电话";
        xinxi="信息";
        shezhi = "设置";
    }

    public String getDindwei() {
        return dindwei;
    }

    public void setDindwei(String dindwei) {
        this.dindwei = dindwei;
    }

    public String getShezhi() {
        return shezhi;
    }

    public void setShezhi(String shezhi) {
        this.shezhi = shezhi;
    }

    public String getShipin() {
        return shipin;
    }

    public void setShipin(String shipin) {
        this.shipin = shipin;
    }

    public String getDianhua() {
        return dianhua;
    }

    public void setDianhua(String dianhua) {
        this.dianhua = dianhua;
    }

    public String getXinxi() {
        return xinxi;
    }

    public void setXinxi(String xinxi) {
        this.xinxi = xinxi;
    }
}

package com.jiedu.project.lovefamily.mode;

/**
 * Created by Administrator on 2016/4/6.
 */
public class ServiceMode {
    private String code;
    private String name;
    private String remark;
    private String busiExpense;
    private String maxPersonNum;
    private String creditsExpense;
    private String id;
    private boolean isCheck;

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }
    public boolean getIsCheck( ){
        return isCheck;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBusiExpense(String busiExpense) {
        this.busiExpense = busiExpense;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setCreditsExpense(String creditsExpense) {
        this.creditsExpense = creditsExpense;
    }

    public void setMaxPersonNum(String maxPersonNum) {
        this.maxPersonNum = maxPersonNum;
    }

    public String getRemark() {
        return remark;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getBusiExpense() {
        return busiExpense;
    }

    public String getCode() {
        return code;
    }

    public String getCreditsExpense() {
        return creditsExpense;
    }

    public String getMaxPersonNum() {
        return maxPersonNum;
    }
}

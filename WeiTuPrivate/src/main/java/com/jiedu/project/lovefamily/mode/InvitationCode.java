package com.jiedu.project.lovefamily.mode;

/**
 * Created by Administrator on 2016/3/24.
 */
public class InvitationCode {
  public  String customerId;
  public  String phone;
  public String type;
  public String inviteCode;
  public String needUploadPostion;
  public String frequency;
  public InvitationCode(String customerId,String phone,String type,String inviteCode, String needUploadPostion,String frequency){
this.customerId=customerId;
    this.phone=phone;
    this.type=type;
    this.inviteCode=inviteCode;
    this.needUploadPostion=needUploadPostion;
    this.frequency=frequency;
  }


}

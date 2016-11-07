package com.jiedu.project.lovefamily.mode;

/**
 * Created by naruto on 2016/5/18.
 */
public class MessageModel {
    /**
     * 标识是否可以删除
     */
    private boolean canRemove = true;
    public String msgTitle;
    public String msgContent;
//    获取消息时间
    public String msgTime;

    public String messageTitle;
    public String messageContent;
    public String isReaded;//0表示已读
    public String messageId;
    public boolean isCanRemove() {
        return canRemove;
    }
    public void setCanRemove(boolean canRemove) {
        this.canRemove = canRemove;
    }
    public MessageModel(String title, boolean canRemove) {
        this.messageTitle = title;
        this.canRemove = canRemove;
    }
    public MessageModel() {
    }


}

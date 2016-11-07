package com.jiedu.project.lovefamily.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.activity.MessageContentActivity;
import com.jiedu.project.lovefamily.mode.MessageModel;
import com.jiedu.project.lovefamily.net.RequestHelp;
import com.jude.swipbackhelper.SwipeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by naruto on 2016/5/18.
 */
public class MessageAdapter extends BaseAdapter {

    public List<MessageModel> list;
    public  Context context;
    MessageViewHolder viewHolder=null;
    private PullToRefreshListView listView;
    String currentMessageId = "";
    private RequestHelp help = new RequestHelp();
    private int Item;
    private boolean ischecked=false;

    private String edittype;
//    打开的条目
    private ArrayList<SwipeLayout> opendItems;

    /**
     * CheckBox 是否选择的存储集合,key 是 position , value 是该position是否选中
     */
    private Map<Integer, Boolean> isCheckMap = new HashMap<Integer, Boolean>();

    private static HashMap<Integer,Boolean> isSelected;

    public static HashMap<Integer,Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        MessageAdapter.isSelected = isSelected;
    }



    public MessageAdapter(Context context, List<MessageModel> list, PullToRefreshListView listView,String edittype){
        this.list=list;
        this.context=context;
        this.listView=listView;
        this.edittype=edittype;
        opendItems = new ArrayList<SwipeLayout>();
        isSelected = new HashMap<Integer, Boolean>();
        for(int i=0; i<list.size();i++) {
            getIsSelected().put(i,false);
        }
        configCheckMap(false);

    }

    /**
     * 首先,默认情况下,所有项目都是没有选中的.这里进行初始化
     */
    public void configCheckMap(boolean bool) {
        for (int i = 0; i < list.size(); i++) {
            isCheckMap.put(i, bool);
        }

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {

        View view = convertView;
        if (convertView == null) {
            view = View.inflate(context,R.layout.message_listitem, null);
            new MessageViewHolder(view);
        }
        MessageModel messageModel = list.get(i);

        boolean canRemove = messageModel.isCanRemove(); //获得该item 是否允许删除

        viewHolder = (MessageViewHolder) view.getTag();
        viewHolder.msgTitle.setText(list.get(i).messageTitle);
        viewHolder.msgContent.setText(list.get(i).messageContent);
        //设置为本地获取时间
        viewHolder.msgTime.setText(list.get(i).msgTime);
        //获取当前item
        final int currentItem = i;
        Item=currentItem;
        viewHolder.msgcheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isCheckMap.put(i, b);
            }
        });

        if (isCheckMap.get(i) == null) {
            isCheckMap.put(i, false);
        }
        viewHolder.msgcheck.setChecked(isCheckMap.get(i));

        viewHolder.msgDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setTitle("是否删除")//设置对话框标题
               .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       list.remove(currentItem);
                       notifyDataSetChanged();
                       currentMessageId = list.get(currentItem - 1).messageId;
                       Log.e("1102", "------------>" + currentMessageId);
                       RefreshUpData(currentMessageId);
                       Log.e("1102", "------------>" + "删除成功");
                   }
               }).setNegativeButton("取消",new DialogInterface.OnClickListener() {//添加返回按钮
                     @Override
                  public void onClick(DialogInterface dialog, int which) {//响应事件
                      // TODO Auto-generated method stub
//                         finish();
                        }
                   }).show();//在按键响应事件中显示此对话框

            }
        });
        viewHolder.msgOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageContentActivity.class);
                list.get(currentItem).isReaded = 0 + "";
                notifyDataSetChanged();
                intent.putExtra("content", list.get(currentItem).messageContent);
                intent.putExtra("messageId", list.get(currentItem).messageId);
                context.startActivity(intent);
            }
        });
        if (Integer.valueOf(list.get(i).isReaded)==0){
//            viewHolder.isRead.setVisibility(View.INVISIBLE);
//            viewHolder.isReaded.setVisibility(View.VISIBLE);
//            viewHolder.msgTime.setTextColor(Color.GRAY);
            viewHolder.hongdian.setVisibility(View.GONE);
            viewHolder.msgTime.setTextColor(Color.GRAY);
            viewHolder.msgTitle.setTextColor(Color.BLACK);
        }else{
//            viewHolder.isRead.setVisibility(View.VISIBLE);
//            viewHolder.isRead.setBackgroundColor(Color.GREEN);
//            viewHolder.isReaded.setVisibility(View.INVISIBLE);
//            viewHolder.isRead.setVisibility(View.VISIBLE);
            viewHolder.hongdian.setVisibility(View.VISIBLE);
            viewHolder.msgTime.setTextColor(context.getResources().getColor(R.color.online));
            viewHolder.msgTitle.setTextColor(context.getResources().getColor(R.color.online));
        }
        return view;
    }



    public List<MessageModel> getDatas() {
        return list;
    }


    class MessageViewHolder{
        ImageView hongdian;
        TextView msgTitle;
        TextView msgContent;
        //添加消息时间
        TextView msgTime;
        //消息删除按钮
        TextView msgDel;
        //消息打开按钮
        TextView msgOpen;
        //消息显示数量
        TextView msgfriendcount;
       final CheckBox msgcheck;
//        CircleImageView isRead;
//        CircleImageView isReaded;

        public MessageViewHolder(View view) {
            hongdian= (ImageView) view.findViewById(R.id.hongdian);
            msgTitle= (TextView) view.findViewById(R.id.msg_title);
            msgContent= (TextView) view.findViewById(R.id.msg_content);
            msgDel = (TextView) view.findViewById(R.id.tv_del);
            msgOpen = (TextView) view.findViewById(R.id.tv_open);
            msgcheck = (CheckBox) view.findViewById(R.id.msg_check);
            msgTime= (TextView) view.findViewById(R.id.msg_time);
            msgfriendcount= (TextView)view.findViewById(R.id.msg_friend_count);
            if (edittype.equals("edit")){
                msgcheck.setVisibility(View.VISIBLE);
            }else if (edittype.equals("notedit")){
                msgcheck.setVisibility(View.GONE);
            }else if(edittype.equals("setisreaded")){

                notifyDataSetChanged();
            }else if (edittype.equals("chooseallcheckbox")){
                msgcheck.setChecked(true);
            }else if (edittype.equals("unchooseallcheckbox")){
                if (msgcheck.isChecked()){
                    msgcheck.setChecked(false);
                }
            }
            view.setTag(this);
        }
        public void MessageshowCheckBoxViewHolder(boolean ischeckall) {
            if (ischeckall){
//                viewHolder.isReaded.setVisibility(View.INVISIBLE);
//                viewHolder.isRead.setVisibility(View.INVISIBLE);
                viewHolder.msgcheck.setVisibility(View.VISIBLE);
                boolean is= viewHolder.msgcheck.isChecked();
                Log.e("0014","viewHolder.msgcheck.isChecked()"+is);
            }else {
                viewHolder.msgcheck.setVisibility(View.GONE);
                if (Integer.valueOf(list.get(Item).isReaded)==0){
//                    viewHolder.isRead.setVisibility(View.INVISIBLE);
//                    viewHolder.isReaded.setVisibility(View.VISIBLE);
                }else{
//                    viewHolder.isReaded.setVisibility(View.INVISIBLE);
//                    viewHolder.isRead.setVisibility(View.VISIBLE);
                }
            }

        }

    }

    private void RefreshUpData(String currentItem) {
        final String item = currentItem;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap<String, String>();
                String url = RequestHelp.Del_MESSAGE + item;
                Log.e("1102", "------------->" + url);
                String json = help.requestPost(url, map);
                Log.e("1102", "------------->" + json);
            }
        }).start();
    }

    public void showCheckBox(boolean ischeckall){
        viewHolder.MessageshowCheckBoxViewHolder(ischeckall);
    }
    // 移除一个项目的时候
    public void remove(int position) {
        this.list.remove(position);
    }

    //去除存放勾选状态的hashmap
    public Map<Integer, Boolean> getCheckMap() {
        return this.isCheckMap;
    }
    public void SetIsreaded(int position){
        list.get(position).isReaded=0+"";
        notifyDataSetChanged();
    }

}

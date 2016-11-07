package com.jiedu.project.lovefamily.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jiedu.project.lovefamily.R;

/**
 * Created by Administrator on 2016/3/3.
 */
class EditItem extends RelativeLayout{

    private ImageView imageView;
    private EditText editText;
    public EditItem(Context context) {
        super(context);
        initView(context);
    }

    public EditItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public EditItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    void initView(Context context){
        View.inflate(context, R.layout.edit_item,null );
        imageView= (ImageView) findViewById(R.id.myview_edit_icon);
        editText=(EditText)findViewById(R.id.myview_edit);

    }
    public void initImageAndEdit(int id,String hint){
        imageView.setBackgroundResource(id);
        editText.setHint(hint);
    }
    public String getEdit(){

        return editText.getText().toString();
    }

}

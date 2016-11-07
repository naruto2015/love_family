package com.jiedu.project.lovefamily.myview;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jiedu.project.lovefamily.R;

public class SafeView extends View {

    public SafeView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public SafeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public SafeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
    }


    //两个圆点进度条线长度
    int lineWidth;
    //线宽
    int lineHight=10;
    //圆的半径
    int circleR=18;
    //控件宽度
    int width;
    //控件高度
    int hight;
    //范围list
    ArrayList<String>list=new ArrayList<String>();
    //进度条背景画笔
    Paint paint;
    //进度条进度画笔
    Paint progressPaint;
    //文字画笔
    Paint textPaint;
    //设置默认进度
    int progress=2;
    //设置默认文字大小
    int textSize= (int) getResources().getDimension(R.dimen.x8);
    //设置文字与控件顶部或者底部的空间
    int space=(int) getResources().getDimension(R.dimen.x2);
    //进度条颜色
    int progressColor=Color.BLUE;
    //进度背景颜色
    int backgroundColor=Color.RED;
    //文字颜色
    int textColor=Color.BLACK;
    OnProgressChangeListener listener;
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        width=getWidth();
        hight=getHeight();
        lineWidth=(width-(list.size()*2*circleR)-2*textSize)/(list.size()-1);
        initPaint();

        canvas.drawLine(circleR+textSize, hight/2, ((list.size()-1)*2+1)*circleR+lineWidth*(list.size()-1)+textSize, hight/2, paint);
        canvas.drawLine(circleR+textSize, hight/2, (progress*2*circleR+progress*lineWidth)+textSize, hight/2, progressPaint);
        for (int i = 0; i < list.size(); i++) {
            if(i<=progress){
                canvas.drawCircle((i*2+1)*circleR+lineWidth*i+textSize, hight/2, circleR, progressPaint);
            }else{
                canvas.drawCircle((i*2+1)*circleR+lineWidth*i+textSize, hight/2, circleR, paint);
            }
            if(i%2==0){
                canvas.drawText(list.get(i), (i*2)*circleR+lineWidth*i+space, hight-space, textPaint);
            }else{
                canvas.drawText(list.get(i), (i*2)*circleR+lineWidth*i+space, textSize+space, textPaint);
            }


        }

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {


        setProgress(getInt(event.getX() / (2 * circleR + lineWidth)));
        listener.onChange(progress);
        return true;
    }
    //设置进度条颜色和背景颜色
    public void setProgressBackground(int progressColor,int backgroundColor){
        this.progressColor=progressColor;
        this.backgroundColor=backgroundColor;
    }
    //初始化画笔
    void initPaint(){
        paint=new Paint();
        progressPaint=new Paint();
        textPaint=new Paint();
        paint.setColor(backgroundColor);
        paint.setStrokeWidth(lineHight);
        paint.setAntiAlias(true);
        progressPaint.setColor(progressColor);
        progressPaint.setStrokeWidth(lineHight);
        progressPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
    }
    //设置进度
    public void setProgress(int i){
        if(i<list.size()){
            progress=i;
        }
        invalidate();
    }
    //获得进度条当前进度
    public int getProgress(){
        return progress;
    }
    //设置线的宽度和圆的半径
    public void setLineAndCircle(int lineHight,int circleR){
        this.lineHight=lineHight;
        this.circleR=circleR;
        invalidate();
    }
    //设置刻度list
    public void setList(ArrayList<String>list){
        this.list=list;
        invalidate();
    }
    //四舍五入
    int getInt(double d){
        return Integer.valueOf(new DecimalFormat("0").format(d));

    }
    //设置文字与顶部或者底部空隙
    public void setSpace(int space){
        this.space=space;
        invalidate();
    }
    //设置文字颜色
    public void setTextColor(int color){
        textColor=color;
        invalidate();
    }
    //设置文字大小
    public void setTextSize(int textSize){
        this.textSize=textSize;
        invalidate();
    }
    public void setOnProgressChangeListener(OnProgressChangeListener listener){
        this.listener=listener;
    }

    public interface OnProgressChangeListener{
        void onChange(int progress);
    }
}

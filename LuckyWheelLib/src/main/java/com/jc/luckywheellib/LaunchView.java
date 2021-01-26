package com.jc.luckywheellib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

public class LaunchView extends View {

    private Context mContext;

   //progress_MAX=POWER_MAX-POWER_MIN(500-100)
    public static final int progress_MAX = 400;
    private int mProgress = 0;

    private TimerTask timerTask;
    private Timer timer;
    private int mWidth, mHeight;
    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint textPaint;
    private int backgroundColor = 0xff9CC8EB;
    private int progressColor =0xffFF5722;
    private int textColor =0xffffffff;
    private RectF mBackRectF;//背景总的矩形
    private int progressPadding;
    private int progressWidth;//进度条总长度
    private int progressHeight;//进度条总高度

    private boolean isWheelRolling=false;

    private LaunchListener launchListener;

    public LaunchView(Context context) {
        this(context,null);
    }

    public LaunchView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LaunchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        initPaint();
    }

    private void initPaint() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setAntiAlias(true);

        progressPaint = new Paint();
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.FILL);
        progressPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(dip2px(mContext,18));
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setAntiAlias(true);

    }
    //设置当前进度
    public void setProgress(int progress) {


        mProgress = progress;
        if (mProgress < 0) {
            mProgress = 0;
        } else if (mProgress > progress_MAX) {
            mProgress = progress_MAX;
        }

        postInvalidate();
    }

    public void setLaunchListener(LaunchListener launchListener){
        this.launchListener=launchListener;
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mBackRectF = new RectF(0, 0, mWidth, mHeight);
        progressPadding=dip2px(mContext,3);
        progressWidth=mWidth-2*progressPadding;
        progressHeight=mHeight-2*progressPadding;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawProgress(canvas);
        drawText(canvas);
    }

    private void drawBackground(Canvas canvas){
        canvas.drawRoundRect(mBackRectF, 0, 0, backgroundPaint);

    }
    private void drawProgress(Canvas canvas){
        //画完半圆再根据进度画一段矩形
        int right = progressWidth * mProgress / progress_MAX + progressPadding;
        canvas.drawRect(progressPadding , progressPadding, right, mHeight-progressPadding, progressPaint);

    }
    private void drawText(Canvas canvas){
        canvas.drawText("按住蓄力", progressWidth/2-textPaint.measureText("按住蓄力")/2, (float) mHeight/2+(textPaint.getTextSize()/2), textPaint);

    }
    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }



    private boolean isPowerIncrease=true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case ACTION_DOWN:
                if(!isWheelRolling){
                    mProgress=0;
                    postInvalidate();

                    timerTask=new TimerTask() {
                        @Override
                        public void run() {

                            if(mProgress>=progress_MAX){
                                isPowerIncrease=false;
                            }else if(mProgress<=0){
                                isPowerIncrease=true;
                            }

                            if(isPowerIncrease){
                               mProgress=mProgress+3;
                            }else{
                                mProgress=mProgress-3;

                            }
                            postInvalidate();
                        }
                    };
                    timer=new Timer();
                    timer.schedule(timerTask,15,15);

                    return true;
                }
                break;
            case ACTION_UP:
                timerTask.cancel();
                timer.cancel();
                timerTask=null;

                if(launchListener!=null){
                    launchListener.onLaunch(mProgress+100);
                }
                break;
        }



        return super.onTouchEvent(event);

    }
}

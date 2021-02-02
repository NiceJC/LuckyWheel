package com.jc.luckywheellib;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 仿真抽奖（抽奖人数与奖品数量是一致的）
 * 按照设置的奖品数量 对应设置扇形的面积大小
 * 每抽中一次奖品，则消除对应的扇形，重新分配扇形角度
 * <p>
 * 抽奖的结果 其实取决于传入的抽奖参数power
 */
public class LuckyWheelView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private List<GiftPie> pieList = new ArrayList<>();//抽奖扇形
    public static final int POWER_MAX = 500;
    public static final int POWER_MIN = 100;


    private SurfaceHolder surfaceHolder;
    private Thread thread;
    private Canvas canvas;
    private boolean isRunning;


    private int currentSpeed;

    //描述字体大小（px）
    private int textSize;
    private int colors[] = {
            getResources().getColor(R.color.color1),
            getResources().getColor(R.color.color2),
            getResources().getColor(R.color.color3),
            getResources().getColor(R.color.color4),
            getResources().getColor(R.color.color5),
            getResources().getColor(R.color.color6)
    };

    private Context mContext;
    //mUseWidth 宽高中较小的值（取最大正方形）
    private int mWidth, mHeight, mUseWidth;
    private boolean isRolling = false; //是否在转动中
    //    //是否平均显示 true：所有奖项在转盘上显示一样面积 false：按照实际剩余比例显示
//    private boolean isAverage = false;
    //当前的旋转角度
    private float currentRotateAngle = 0;
    //画扇形时的起始角度
    private float currentStartAngle = 0;
    private RectF pieReact;

    private Bitmap backgroundPic;//背景图
    private Bitmap pointerPic;//指针图
    private Paint piePaint;
    private Paint textPaint;
    private WheelListener wheelListener;

    public void setWheelListener(WheelListener wheelListener) {
        this.wheelListener = wheelListener;
    }

    public LuckyWheelView(Context context) {
        this(context, null);

    }

    public LuckyWheelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LuckyWheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initParam();
        initPaint();


    }

    //总共有多少分奖品（或者有多少人参与抽奖）
    int sumCount;
    //每一份对应的角度
    float pieceAngle;

    public void setTypeList(List<GiftType> gifts) {

        if (gifts == null || gifts.size() == 0) {
            return;
        }
        pieList.clear();

        for (int i = 0; i < gifts.size(); i++) {
            gifts.get(i).setColor(colors[i % 6]);
        }


        //逐一设置颜色  角度
        for (GiftType type : gifts
        ) {

            for (int i = 0; i < type.getCount(); i++) {
                if (type.isWorst()) {
                    //阳光普照奖
                    pieList.add(new GiftPie(getResources().getColor(R.color.color0)
                            , type.getName(), true));
                } else {
                    //其他奖
                    pieList.add(new GiftPie(type.getColor(), type.getName(), false));
                }
            }
        }
        updatePieAngle();
        //打乱顺序
        Collections.shuffle(pieList);
        draw();

    }

    /**
     * 开始转盘抽奖
     * power的值即为初始角速度
     * <p>
     * <p>
     * 方便起见 加速度为每一帧(15ms)减少1
     * 估算  300来算 大概5秒停下
     */
    public void startRoll(int power) {
        if (pieList.size() == 0) {
            Toast.makeText(mContext, "请先设置奖品", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isRolling) {
            return;
        }
        if (power < POWER_MIN || power > POWER_MAX) {
            Toast.makeText(mContext, "力度不行啊", Toast.LENGTH_SHORT).show();
            return;
        }
        isRolling = true;

        currentRotateAngle = 0;
        currentSpeed = power;


    }

    public void updatePieAngle() {
        sumCount = 0;
        for (GiftPie pie : pieList
        ) {
            if (!pie.isCatch()) {
                sumCount++;
            }
        }
        pieceAngle = (float) 360 / sumCount;
        draw();
    }

    public void resetPiePool() {
        for (GiftPie pie : pieList
        ) {

            pie.setCatch(false);
        }
        updatePieAngle();
    }


    private void catchPie() {

        double targetAngle = (270 - currentRotateAngle) % 360;
        if (targetAngle < 0) {
            targetAngle = targetAngle + 360;
        }
        int catchPosition = (int) Math.floor(targetAngle / pieceAngle);

        List<GiftPie> unCatchPieList = new ArrayList<>();
        for (GiftPie pie : pieList
        ) {
            if (!pie.isCatch()) {
                unCatchPieList.add(pie);
            }
        }


        GiftPie targetPie = unCatchPieList.get(catchPosition);
        wheelListener.onCatch(targetPie.getName(), targetPie);
    }

    private void initParam() {

        backgroundPic = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.back);
        pointerPic = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.node);
//设置可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);


    }

    public boolean getIsRolling() {
        return isRolling;
    }


    private void initPaint() {

        piePaint = new Paint();
        piePaint.setAntiAlias(true);
        piePaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(0xffffffff);
        textSize = dip2px(mContext, 12);
        textPaint.setTextSize(textSize);

    }

    float pieRadium;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mUseWidth = Math.min(mWidth, mHeight);
        pieRadium = (float) mUseWidth * 2 / 5;
        pieReact = new RectF(-pieRadium, -pieRadium, pieRadium, pieRadium);
    }

    private void draw() {
        try {
            canvas = surfaceHolder.lockCanvas();
            drawBackground(canvas);
            drawPies(canvas);
            drawPointer(canvas);
        } catch (Exception e) {
        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    //绘制背景
    private void drawBackground(Canvas canvas) {


        canvas.drawColor(Color.WHITE);

        //将原点挪到 最大正方形左上角
        //将原点挪到
        //最大正方形 左上角
        canvas.translate((float) (mWidth - mUseWidth) / 2, (float) (mHeight - mUseWidth) / 2);

        //背景图铺满整个最大正方形
        Matrix matrix = new Matrix();
        matrix.postScale(mUseWidth / (float) backgroundPic.getWidth(), mUseWidth / (float) backgroundPic.getHeight());
        canvas.drawBitmap(backgroundPic, matrix, null);

    }

    //画扇形
    private void drawPies(Canvas canvas) {

        //坐标移到正中心
        canvas.translate((float) mUseWidth / 2, (float) mUseWidth / 2);

        currentStartAngle = 0;
        for (GiftPie bean : pieList
        ) {
            if (bean.isCatch()) {
                //抽中过的就不画了
                continue;
            }
            piePaint.setColor(bean.getColor());
            canvas.drawArc(pieReact, currentStartAngle + currentRotateAngle, pieceAngle, true, piePaint);

            if (!bean.isOrdinary()) {
                Path path = new Path();
                path.lineTo(pieRadium * (float) Math.cos(Math.PI * (currentStartAngle + currentRotateAngle + (float) pieceAngle / 2) / 180), pieRadium * (float) Math.sin(Math.PI * (currentStartAngle + currentRotateAngle + (float) pieceAngle / 2) / 180));
                canvas.drawTextOnPath(bean.getName(), path, pieRadium * 2 / 3, (float) textSize / 2, textPaint);
            }
            currentStartAngle = currentStartAngle + pieceAngle;

        }
    }

    private void drawPointer(Canvas canvas) {
        Matrix matrix = new Matrix();
        matrix.postTranslate(-(float) pointerPic.getWidth() / 2, -(float) pointerPic.getHeight() / 2);
        canvas.drawBitmap(pointerPic, matrix, null);

    }

    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        isRunning = true;
        thread = new Thread(this);
        thread.start();
        draw();

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

        isRunning = false;

    }

    @Override
    public void run() {
        while (isRunning) {


            long start = System.currentTimeMillis();

            if (isRolling) {
                if (currentSpeed <= 0) {
                    isRolling = false;
                    catchPie();

                } else {
                    currentRotateAngle = currentRotateAngle + (float) currentSpeed / 1000 * 15;
                    currentSpeed--;

                    draw();
                }
            }

            long end = System.currentTimeMillis();

            try {
                if (end - start < 15) {
                    Thread.sleep(15 - (end - start));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}

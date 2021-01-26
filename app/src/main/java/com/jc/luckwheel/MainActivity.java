package com.jc.luckwheel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jc.luckywheellib.GiftPie;
import com.jc.luckywheellib.GiftType;
import com.jc.luckywheellib.LaunchListener;
import com.jc.luckywheellib.LaunchView;
import com.jc.luckywheellib.LuckyWheelView;
import com.jc.luckywheellib.WheelListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    LuckyWheelView luckyWheelView;
    LaunchView launchView;
    Button btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        luckyWheelView=findViewById(R.id.luck_wheel_view);
        launchView=findViewById(R.id.launch_view);
        btnReset=findViewById(R.id.btn_reset);
        initData();
        initEvent();
    }

    private void initData(){
        List<GiftType> typeList = new ArrayList<>();
        typeList.add(new GiftType("一等奖",1));
        typeList.add(new GiftType("二等奖",2));
        typeList.add(new GiftType("三等奖",3));
        typeList.add(new GiftType("四等奖",4));
        typeList.add(new GiftType("阳光普照",5,true));
        luckyWheelView.setTypeList(typeList);
    }

    private void initEvent(){

        luckyWheelView.setWheelListener(new WheelListener() {
            @Override
            public void onStartRolling() {

            }

            @Override
            public void onCatch(final String name, final GiftPie pie) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("恭喜恭喜：");
                        builder.setMessage("您抽中的是"+name);
                        builder.setIcon(com.jc.luckywheellib.R.mipmap.ic_launcher);
                        builder.setCancelable(true);
                        AlertDialog dialog= builder.create();
                        dialog.show();

                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                pie.setCatch(true);
                                luckyWheelView.updatePieAngle();
                            }
                        });
                    }
                });



            }
        });

        launchView.setLaunchListener(new LaunchListener() {
            @Override
            public void onLaunch(int power) {
                luckyWheelView.startRoll(power);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                luckyWheelView.resetPiePool();
            }
        });
    }

}
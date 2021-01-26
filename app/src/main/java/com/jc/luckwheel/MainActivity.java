package com.jc.luckwheel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jc.luckywheellib.GiftType;
import com.jc.luckywheellib.LaunchListener;
import com.jc.luckywheellib.LaunchView;
import com.jc.luckywheellib.LuckyWheelView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    LuckyWheelView luckyWheelView;
    LaunchView launchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        luckyWheelView=findViewById(R.id.luck_wheel_view);
        launchView=findViewById(R.id.launch_view);

        initData();
        initEvent();
    }

    private void initData(){
        List<GiftType> typeList = new ArrayList<>();
        typeList.add(new GiftType("一等奖",1));
        typeList.add(new GiftType("二等奖",2));
        typeList.add(new GiftType("三等奖",3));
        typeList.add(new GiftType("四等奖",4));
        typeList.add(new GiftType("阳光普照",20,true));
        luckyWheelView.setTypeList(typeList);
    }

    private void initEvent(){

        launchView.setLaunchListener(new LaunchListener() {
            @Override
            public void onLaunch(int power) {
                luckyWheelView.startRoll(power);
            }
        });
    }

}
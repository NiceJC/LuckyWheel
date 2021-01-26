package com.jc.luckwheel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jc.luckywheellib.GiftType;
import com.jc.luckywheellib.LuckyWheelView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    LuckyWheelView luckyWheelView;
    Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        luckyWheelView=findViewById(R.id.luck_wheel_view);
        btnStart=findViewById(R.id.btn_start);

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
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                luckyWheelView.startRoll(500);
            }
        });
    }

}
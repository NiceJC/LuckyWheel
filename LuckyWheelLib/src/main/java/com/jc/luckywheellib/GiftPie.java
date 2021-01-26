package com.jc.luckywheellib;

//每一小片扇形（最小的抽奖单位）
public class GiftPie {

    //绘制参数 颜色
    private int color;

    //绘制参数 角度
    private int startAngle;

    private String name;
    private int ownerId;
    private String ownerName;
    private boolean isOrdinary =false;

    public GiftPie(int color, String name, boolean isOrdinary) {
        this.color = color;
        this.name = name;
        this.isOrdinary = isOrdinary;
    }

    public boolean isOrdinary() {
        return isOrdinary;
    }

    public void setOrdinary(boolean ordinary) {
        isOrdinary = ordinary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}

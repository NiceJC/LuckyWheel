package com.jc.luckywheellib;

//奖品类型
public class GiftType {
    //奖品名称
    private String name;
    //奖品的数量（相当于权  影响扇形角度 抽中几率）
    private int count;
    //奖品的图片
    private String imagePath;

    //阳光普照奖（底色固定白色 饼上不写文字标注 也不画背景图）
    private boolean isWorst=false;
    private int color;


    public GiftType(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public GiftType(String name, int count, String imagePath) {
        this.name = name;
        this.count = count;
        this.imagePath = imagePath;
    }

    public GiftType(String name, int count, boolean isWorst) {
        this.name = name;
        this.count = count;
        this.isWorst = isWorst;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isWorst() {
        return isWorst;
    }

    public void setWorst(boolean worst) {
        isWorst = worst;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}

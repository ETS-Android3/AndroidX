package com.androidx.dialog;

public class SelectorBody {

    /**
     * 名称
     */
    private String name;
    /**
     * 选中状态
     */
    private boolean check;
    /**
     * 位置
     */
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
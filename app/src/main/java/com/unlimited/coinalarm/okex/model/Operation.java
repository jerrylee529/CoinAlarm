package com.unlimited.coinalarm.okex.model;

import java.util.ArrayList;

public class Operation {
    private String mOp; //1. subscribe 订阅； 2. unsubscribe 取消订阅 ；3. login 登录
    private ArrayList mArgs; // 取值为频道名，可以定义一个或者多个频道

    public Operation() {
        mOp = "subscribe";
        mArgs = new ArrayList<String>();
    }

    public String getOp() {
        return  mOp;
    }

    public void setOp(String op) {
        mOp = op;
    }

    public void addArg(String arg) {
        mArgs.add(arg);
    }

    public ArrayList getArgs() {
        return mArgs;
    }
}

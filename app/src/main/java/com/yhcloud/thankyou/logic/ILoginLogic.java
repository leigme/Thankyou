package com.yhcloud.thankyou.logic;

import com.yhcloud.thankyou.mInterface.MyCallListener;

/**
 * Created by Administrator on 2016/11/14.
 */

public interface ILoginLogic {
    public void login(String username, String password, MyCallListener myCallListener);
}

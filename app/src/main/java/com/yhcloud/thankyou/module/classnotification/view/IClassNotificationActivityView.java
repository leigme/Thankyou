package com.yhcloud.thankyou.module.classnotification.view;

import com.yhcloud.thankyou.mInterface.IBaseActivityView;
import com.yhcloud.thankyou.module.classnotification.bean.ClassNotificationBean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/1/6.
 */

public interface IClassNotificationActivityView extends IBaseActivityView {
    void showRightMenu();
    void showList(ArrayList<ClassNotificationBean> list);
    void completeRefreshList();
}
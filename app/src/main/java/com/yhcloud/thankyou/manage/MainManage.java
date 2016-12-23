package com.yhcloud.thankyou.manage;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;

import com.yhcloud.thankyou.R;
import com.yhcloud.thankyou.bean.ClassInfoBean;
import com.yhcloud.thankyou.bean.PopupMenuBean;
import com.yhcloud.thankyou.bean.UserInfo;
import com.yhcloud.thankyou.logic.IMainLogic;
import com.yhcloud.thankyou.module.curriculum.view.CurriculumActivity;
import com.yhcloud.thankyou.service.LogicService;
import com.yhcloud.thankyou.module.classcadre.view.ClassCadreActivity;
import com.yhcloud.thankyou.view.ClassFragment;
import com.yhcloud.thankyou.module.dutystudent.view.DutyStudentActivity;
import com.yhcloud.thankyou.view.HomeFragment;
import com.yhcloud.thankyou.view.IClassView;
import com.yhcloud.thankyou.view.IMainView;
import com.yhcloud.thankyou.view.MineFragment;

import java.util.ArrayList;

/**
 * Created by leig on 2016/11/19.
 */

public class MainManage {

    private String TAG = getClass().getSimpleName();
    private IMainLogic mIMainLogic;
    private IMainView mIMainView;
    private Activity mActivity;
    private LogicService mService;
    private UserInfo mUserInfo;
    private ArrayList<Fragment> mFragments;
    private ArrayList<ClassInfoBean> mClassInfoBeen;
    private ArrayList<PopupMenuBean> mMenuBeen;

    public MainManage(IMainView mainView) {
        this.mIMainView = mainView;
        this.mActivity = (Activity) mainView;
        Intent intent = new Intent(mActivity, LogicService.class);
        mActivity.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                mService = ((LogicService.MyBinder)binder).getService();
                mUserInfo = mService.getUserInfo();
                mIMainView.initView();
                mIMainView.initData();
                mIMainView.initEvent();
                initViewPages();
                mIMainView.showFragment(0);
                mIMainView.setHeaderLeftImage(mUserInfo.getUserInfoBean().getHeadImageURL());
                mIMainView.setTitle(mUserInfo.getUserInfoBean().getSchoolName());
                if (null != mActivity.getIntent()) {
                    Bundle bundle = mActivity.getIntent().getExtras();
                    mClassInfoBeen = (ArrayList<ClassInfoBean>) bundle.getSerializable("ClassInfos");
                    for (ClassInfoBean classInfoBean: mClassInfoBeen) {
                        if (classInfoBean.getClassId().equals(mUserInfo.getUserInfoBean().getDefaultClassId())) {
                            classInfoBean.setSelected(true);
                        }
                    }
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Service.BIND_AUTO_CREATE);
    }

    public void initViewPages() {
        mFragments = new ArrayList<>();
        HomeFragment homeFragment = HomeFragment.newInstance(mService);
        mFragments.add(homeFragment);
        ClassFragment classFragment = ClassFragment.newInstance(mService);
        mFragments.add(classFragment);
        MineFragment mineFragment = MineFragment.newInstance(mService);
        mFragments.add(mineFragment);
        mIMainView.initFragments(mFragments);
    }

    public void setTitle(int i) {
        switch (i) {
            case 0:
                mIMainView.setTitle(mUserInfo.getUserInfoBean().getSchoolName());
                break;
            case 1:
                if (null != mClassInfoBeen) {
                    for (ClassInfoBean classInfoBean: mClassInfoBeen) {
                        if (classInfoBean.getClassId().equals(mUserInfo.getUserInfoBean().getDefaultClassId())) {
                            mIMainView.setTitle(classInfoBean.getClassName());
                        }
                    }
                }
                break;
            case 2:
                mIMainView.setTitle("我的");
                break;
        }
    }

    public void showDrawer() {
        if (null != mClassInfoBeen) {
            mIMainView.showDrawer(mClassInfoBeen);
        }
    }

    public void setDefaultClassId(String classId) {
        mUserInfo.getUserInfoBean().setDefaultClassId(classId);
    }

    public void setRightButton(boolean showed) {
        mIMainView.showHeaderRightButton(showed);
        if (showed) {
            if (null == mMenuBeen || 0 == mMenuBeen.size()) {
                mMenuBeen = new ArrayList<>();
                PopupMenuBean popupMenuBean1 = new PopupMenuBean(R.mipmap.icon_class_cadre, "班干部", new Intent(mActivity, ClassCadreActivity.class));
                PopupMenuBean popupMenuBean2 = new PopupMenuBean(R.mipmap.icon_class_duty, "值日生", new Intent(mActivity, DutyStudentActivity.class));
                PopupMenuBean popupMenuBean3 = new PopupMenuBean(R.mipmap.icon_class_curriculum, "课表", new Intent(mActivity, CurriculumActivity.class));
                mMenuBeen.add(popupMenuBean1);
                mMenuBeen.add(popupMenuBean2);
                mMenuBeen.add(popupMenuBean3);
                mIMainView.initPopupMenu(mMenuBeen);
            }
        }
    }

    public void setClassPeopleList(String classId) {
        IClassView iClassView = (IClassView) mFragments.get(1);
        iClassView.getClassManage().getClassPeopleList(classId);
    }
}

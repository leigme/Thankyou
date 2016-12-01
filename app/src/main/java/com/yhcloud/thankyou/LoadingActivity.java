package com.yhcloud.thankyou;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yhcloud.thankyou.bean.ClassInfoBean;
import com.yhcloud.thankyou.bean.UserInfo;
import com.yhcloud.thankyou.bean.UserInfoBean;
import com.yhcloud.thankyou.mInterface.ICallListener;
import com.yhcloud.thankyou.service.LogicService;
import com.yhcloud.thankyou.utils.Constant;
import com.yhcloud.thankyou.view.LoginActivity;
import com.yhcloud.thankyou.view.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class LoadingActivity extends AppCompatActivity {

    private String TAG = getClass().getSimpleName();

    private LogicService mService;
    private UserInfo mUserInfo;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        //隐藏系统状态栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //自动升级判断

        mUserInfo = new UserInfo();
        Intent intent = new Intent(this, LogicService.class);
        this.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                mService = ((LogicService.MyBinder)binder).getService();
                SharedPreferences preferences = LoadingActivity.this.getSharedPreferences(Constant.USER_INFO, MODE_PRIVATE);
                String username = preferences.getString(Constant.USER_NAME, "");
                String password = preferences.getString(Constant.USER_PWD, "");
                if (null != username && !"".equals(username) && null != password && !"".equals(password)) {
                    mService.login(username, password, new ICallListener() {
                        @Override
                        public void callSuccess(Object o) {
                            Log.e(TAG, "回调成功:" + o);
                            try {
                                JSONObject jsonObject = new JSONObject((String) o);
                                if (!jsonObject.getBoolean("errorFlag")) {
                                    String key = jsonObject.getString("key");
                                    if (null != key && !"".equals(key)) {
                                        mUserInfo.setKey(key);
                                    }
                                    String jsonUserInfo = jsonObject.getString("userinfo");
                                    String jsonClassInfos = jsonObject.getString("classlist");
                                    if (null != jsonUserInfo && !"".equals(jsonUserInfo) && null != jsonClassInfos && !"".equals(jsonClassInfos)) {
                                        Gson gson = new Gson();
                                        UserInfoBean userInfoBean = gson.fromJson(jsonUserInfo, UserInfoBean.class);
                                        if (null != userInfoBean) {
                                            mUserInfo.setUserInfoBean(userInfoBean);
                                        }
                                        ArrayList<ClassInfoBean> classInfoBeen = gson.fromJson(jsonClassInfos, new TypeToken<ArrayList<ClassInfoBean>>(){}.getType());
                                        if (null != classInfoBeen) {
                                            mUserInfo.setClassInfoBeen(classInfoBeen);
                                        }
                                        saveUserInfo(mUserInfo);
                                        mService.setUserInfo(mUserInfo);
                                        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("ClassInfos", classInfoBeen);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            goLoginActivity();
                        }
                        @Override
                        public void callFailed() {
                            Log.e(TAG, "回调失败");
                            goLoginActivity();
                        }
                    });
                } else {
                    goLoginActivity();
                }

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Service.BIND_AUTO_CREATE);
    }

    //延迟3秒进入登录界面
    private void goLoginActivity() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 3000);
    }

    public void saveUserInfo(UserInfo userInfo) {
        mPreferences = this.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
        mPreferences.edit().putString(Constant.USER_NAME, userInfo.getUsername());
        mPreferences.edit().putString(Constant.USER_PWD, userInfo.getPassword());
        mPreferences.edit().putInt(Constant.USER_FLAG, userInfo.getUserInfoBean().getUserRoleId());
        mPreferences.edit().putString(Constant.USER_HXNAME, userInfo.getUserInfoBean().getHXUserName());
        mPreferences.edit().putString(Constant.USER_HXPWD, userInfo.getUserInfoBean().getHXPwd());
        mPreferences.edit().commit();
    }
}

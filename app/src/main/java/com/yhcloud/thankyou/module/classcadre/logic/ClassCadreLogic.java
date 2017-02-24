package com.yhcloud.thankyou.module.classcadre.logic;

import com.yhcloud.thankyou.minterface.ICallListener;
import com.yhcloud.thankyou.utils.Constant;
import com.yhcloud.thankyou.utils.Tools;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/23.
 */

public class ClassCadreLogic {

    private String TAG = getClass().getSimpleName();

    public void getClassCadreList(String classId, final ICallListener<String> iCallListener) {
        OkHttpUtils.post()
                .url(Constant.GETCLASSCADRE)
                .addParams("classId", classId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Tools.print(TAG, "getClassCadreList-请求失败：" + e);
                        iCallListener.callFailed();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Tools.print(TAG, "getClassCadreList-请求成功：" + response);
                        iCallListener.callSuccess(response);
                    }
                });
    }
}

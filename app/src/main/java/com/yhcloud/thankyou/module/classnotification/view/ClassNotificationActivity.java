package com.yhcloud.thankyou.module.classnotification.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yhcloud.thankyou.R;
import com.yhcloud.thankyou.mInterface.IOnClickListener;
import com.yhcloud.thankyou.module.classnotification.adapter.ClassNotificationListAdapter;
import com.yhcloud.thankyou.module.classnotification.bean.ClassNotificationBean;
import com.yhcloud.thankyou.module.classnotification.manage.ClassNotificationManage;
import com.yhcloud.thankyou.utils.DividerItemDecoration;
import com.yhcloud.thankyou.utils.Tools;
import com.yhcloud.thankyou.utils.myview.MyToast;

import java.util.ArrayList;

public class ClassNotificationActivity extends AppCompatActivity implements IClassNotificationView {

    private String TAG = getClass().getSimpleName();

    //视图控件
    private LinearLayout llBack, llMenu;
    private TextView tvTitle, tvMenu;
    private RecyclerView rvList;
    private ProgressDialog mProgressDialog;
    //适配器
    private ClassNotificationListAdapter cnla;
    //管理器
    private ClassNotificationManage mManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_notification);
        mManage = new ClassNotificationManage(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void initView() {
        llBack = (LinearLayout) findViewById(R.id.ll_header_left);
//        llMenu = (LinearLayout) findViewById(R.id.ll_header_right);
        tvTitle = (TextView) findViewById(R.id.tv_header_title);
        rvList = (RecyclerView) findViewById(R.id.rv_classnotification_list);
    }

    @Override
    public void initEvent() {
        View.OnClickListener myOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ll_header_left:
                        finish();
                        break;
                }
            }
        };
        llBack.setOnClickListener(myOnClickListener);
    }

    @Override
    public void showDefault(boolean showed) {

    }

    @Override
    public void showLoading(int msgId) {
        mProgressDialog = ProgressDialog.show(this, null, getString(msgId));
    }

    @Override
    public void hiddenLoading() {
        if (null != mProgressDialog) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    @Override
    public void setRightTitle(String title) {
        tvMenu.setText(title);
    }

    @Override
    public void showToastMsg(int msgId) {
        MyToast.showToast(this, msgId);
    }

    @Override
    public void showToastMsg(String msg) {
        MyToast.showToast(this, msg);
    }

    @Override
    public void showRightMenu() {
        llMenu = (LinearLayout) findViewById(R.id.ll_header_right);
        llMenu.setVisibility(View.VISIBLE);
        tvMenu = (TextView) findViewById(R.id.tv_header_right);
        llMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mManage.goAddClassNotification();
            }
        });
    }

    @Override
    public void showList(final ArrayList<ClassNotificationBean> list) {
        if (null == cnla) {
            cnla = new ClassNotificationListAdapter(this, list);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            rvList.setLayoutManager(layoutManager);
            rvList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
            rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                        if (lastVisiblePosition >= layoutManager.getItemCount() - 1) {
                            mManage.getMoreData();
                        }
                    }
                }
            });
            cnla.setIOnClickListener(new IOnClickListener() {
                @Override
                public void OnItemClickListener(View view, int position) {
                    mManage.goClassNotificationDetail(position);
                }

                @Override
                public void OnItemLongClickListener(View view, int position) {

                }
            });
            rvList.setAdapter(cnla);
        } else {
            cnla.refreshData(list);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<ClassNotificationBean> list = (ArrayList<ClassNotificationBean>) data.getSerializableExtra("list");
        mManage.refreshData(list);
    }
}

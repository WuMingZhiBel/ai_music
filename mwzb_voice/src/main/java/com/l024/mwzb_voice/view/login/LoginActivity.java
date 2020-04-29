package com.l024.mwzb_voice.view.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.l024.lib_commin_ui.base.BaseActivity;
import com.l024.lib_network.okhttp.listener.DisposeDataListener;
import com.l024.mwzb_voice.R;
import com.l024.mwzb_voice.api.RequestCenter;
import com.l024.mwzb_voice.events.LoginEvent;
import com.l024.mwzb_voice.model.user.User;
import com.l024.mwzb_voice.utils.UserManager;

import org.greenrobot.eventbus.EventBus;

/**
 * 登录相关
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 跳转到登录页面
     * @param context
     */
    public static void start(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tvLogin = findViewById(R.id.login_view);
        tvLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_view:{
                RequestCenter.login(new DisposeDataListener() {
                    @Override
                    public void onSuccess(Object responseObj) {
                        //登录成功
                        User user = (User) responseObj;
                        //保存本地
                        if(user!=null){
                            UserManager.getInstance().savaUser(user);
                            EventBus.getDefault().post(new LoginEvent());
                            finish();
                        }else{
                            Toast.makeText(LoginActivity.this, "登录异常", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Object reasonObj) {
                        //登录失败
                    }
                });
                break;
            }
        }
    }
}

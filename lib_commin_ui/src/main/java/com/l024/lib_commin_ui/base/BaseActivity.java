package com.l024.lib_commin_ui.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.l024.lib_commin_ui.utils.StatusBarUtil;

/**
 * @author wu_ming_zhi_bei
 * @date 2020/4/26 11:15
 * @Notes 基础activity
 */
public class BaseActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //实现沉浸式
        StatusBarUtil.statusBarLightMode(this);
    }
}

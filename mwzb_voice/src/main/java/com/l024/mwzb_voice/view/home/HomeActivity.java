package com.l024.mwzb_voice.view.home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.l024.lib_commin_ui.base.BaseActivity;
import com.l024.lib_image_loader.app.ImageLoaderManager;
import com.l024.mwzb_voice.R;
import com.l024.mwzb_voice.events.LoginEvent;
import com.l024.mwzb_voice.model.CHANNEL;
import com.l024.mwzb_voice.utils.UserManager;
import com.l024.mwzb_voice.view.home.adapter.HomePagerAdapter;
import com.l024.mwzb_voice.view.login.LoginActivity;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 首页
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener{
    //首页要出现的卡片标签
    private static final CHANNEL[] CHANNELS = new CHANNEL[]{CHANNEL.MY, CHANNEL.DISCORY, CHANNEL.FRIEND};
    /**
     * view
     */
    private DrawerLayout mDrawerLayout;//总布局
    private TextView mToggleView;//左边菜单栏
    private TextView mSearchView;//搜索
    private MagicIndicator mMagicIndicator;//指示器
    private ViewPager mViewPager;//页面
    private HomePagerAdapter mAdapter;//ViewPage适配器
    private LinearLayout unlogginLayout;//未登录显示ui
    private ImageView ivAvatr;//头像


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //注册
        EventBus.getDefault().register(this);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggleView = (TextView) findViewById(R.id.toggle_view);
        mToggleView.setOnClickListener(this);
        mSearchView = (TextView) findViewById(R.id.search_view);
        mSearchView.setOnClickListener(this);
        mMagicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        //初始化适配器
        mAdapter = new HomePagerAdapter(getSupportFragmentManager(),CHANNELS);
        mViewPager.setAdapter(mAdapter);
        //初始化指示器
        initMagicIndicator();
        //未登录显示
        unlogginLayout = findViewById(R.id.unloggin_layout);
        unlogginLayout.setOnClickListener(this);
        ivAvatr = findViewById(R.id.avatr_view);
    }

    /**
     * 初始化数据
     */
    private void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.unloggin_layout:{
                //未登录
                if(!UserManager.getInstance().hasLogin()){
                    LoginActivity.start(this);
                }else{
                    //关闭左边
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                }
                break;
            }
        }
    }

    /**
     * 初始化指示器
     */
    private void initMagicIndicator() {
        mMagicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return CHANNELS==null?0:CHANNELS.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(HomeActivity.this);
                simplePagerTitleView.setText(CHANNELS[index].getKey());//设置文字
                simplePagerTitleView.setTextSize(19);//文字大小
                simplePagerTitleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//字体加粗
                simplePagerTitleView.setSelectedColor(Color.parseColor("#333333"));//选中颜色
                simplePagerTitleView.setNormalColor(Color.parseColor("#999999"));//默认颜色颜色
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //点击后设置viewPager当前页面
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null;
            }
        });
        //配置指示器，并和ViewPager产生绑定
        mMagicIndicator.setNavigator(commonNavigator);
        //进行绑定
        ViewPagerHelper.bind(mMagicIndicator,mViewPager);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(LoginEvent event){
        //隐藏未登录
        unlogginLayout.setVisibility(View.GONE);
        ivAvatr.setVisibility(View.VISIBLE);
        //加载头像 头像从user中获取
        ImageLoaderManager.getInstance().displayImageForCircie(ivAvatr,UserManager.getInstance().getUser().data.photoUrl);
    }

}

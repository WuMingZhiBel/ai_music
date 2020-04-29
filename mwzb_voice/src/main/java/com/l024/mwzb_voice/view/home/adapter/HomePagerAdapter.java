package com.l024.mwzb_voice.view.home.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.l024.mwzb_voice.model.CHANNEL;
import com.l024.mwzb_voice.view.discory.DiscoryFragment;
import com.l024.mwzb_voice.view.friend.FriendFragment;
import com.l024.mwzb_voice.view.mine.MineFragment;

/**
 * @author wu_ming_zhi_bei
 * @date 2020/4/26 10:58
 * @Notes 首页ViewPageAdapter适配器
 */
public class HomePagerAdapter extends FragmentPagerAdapter{
    private CHANNEL[] mList;
    public HomePagerAdapter(@NonNull FragmentManager fm, CHANNEL[] datas) {
        super(fm);
        mList = datas;
    }

    /**
     * 使用这种方式比较优势。当点击到这个项的时候才会去创建对用的Fragment 而不是直接在首页创建好把List<Fragment>传递进来</Fragment></>
     * @param position
     * @return
     */
    @NonNull
    @Override
    public Fragment getItem(int position) {
        int type = mList[position].getValue();
        switch (type){
            case CHANNEL.MINE_ID:{
                //我的
                return MineFragment.newInstance();
            }
            case CHANNEL.FRIEND_ID:{
                //朋友
                return FriendFragment.newInstance();
            }
            case CHANNEL.DISCORY_ID:{
                //发现
                return DiscoryFragment.newInstance();
            }
            case CHANNEL.VIDED_ID:{
                //视频
                //return VidedFragment.newInstance();
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return mList.length;
    }
}

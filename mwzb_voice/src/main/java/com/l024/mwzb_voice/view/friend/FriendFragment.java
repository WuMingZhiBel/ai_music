package com.l024.mwzb_voice.view.friend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.l024.mwzb_voice.R;

/**
 * @author wu_ming_zhi_bei
 * @date 2020/4/26 11:32
 * @Notes
 */
public class FriendFragment extends Fragment {

    private static FriendFragment friendFragment;

    private FriendFragment(){

    }
    public static Fragment newInstance() {
        if(friendFragment==null){
            synchronized (FriendFragment.class){
                if(friendFragment==null){
                    friendFragment = new FriendFragment();
                }
            }
        }
        return friendFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_layout, null);
        return view;

    }
}

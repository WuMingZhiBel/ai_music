package com.l024.mwzb_voice.view.discory;

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
 * @date 2020/4/26 11:34
 * @Notes 发现页面
 */
public class DiscoryFragment extends Fragment {
    private static DiscoryFragment discoryFragment;

    private DiscoryFragment(){

    }
    public static Fragment newInstance() {
        if(discoryFragment==null){
            synchronized (DiscoryFragment.class){
                if(discoryFragment==null){
                    discoryFragment = new DiscoryFragment();
                }
            }
        }
        return discoryFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discory_layout, null);
        return view;

    }
}

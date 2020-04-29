package com.l024.mwzb_voice.model;

/**
 * @author wu_ming_zhi_bei
 * @date 2020/4/26 10:21
 * @Notes 头部指示器标签
 */
public enum CHANNEL {
    MY("我的",0x01),
    DISCORY("发现",0x02),
    FRIEND("朋友",0x03),
    VIDED("视频",0x04);

    //所有类型 标识
    public static final int MINE_ID = 0x01;
    public static final int DISCORY_ID = 0x02;
    public static final int FRIEND_ID = 0x03;
    public static final int VIDED_ID = 0x04;

    private final String key;
    private final int value;

    CHANNEL(String key, int value) {
        this.key = key;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

}

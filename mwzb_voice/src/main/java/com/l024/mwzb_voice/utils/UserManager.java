package com.l024.mwzb_voice.utils;

import com.l024.mwzb_voice.model.user.User;

/**
 * @author wu_ming_zhi_bei
 * @date 2020/4/27 10:44
 * @Notes 用户相关管理类
 */
public class UserManager {
    private static UserManager userManager;
    private User mUser;
    private UserManager(){

    }

    public static UserManager getInstance(){
        if(userManager==null){
            synchronized (UserManager.class){
                if(userManager==null){
                    userManager = new UserManager();
                }
            }
        }
        return userManager;
    }

    /**
     * 用户保存到内存
     */
    public void savaUser(User user){
        mUser = user;
        //保存进数据库
        saveLocal(user);
    }

    /**
     * 保存进数据库
     * @param user
     */
    private void saveLocal(User user) {
    }

    /**
     * 获取用户
     */
    public User getUser(){
        if(mUser==null){
            //查询数据库
            return mUser;
        }else{
            return mUser;
        }
    }

    /**
     * 从本地获取用户
     */
    private User getLocal(){
        return null;
    }

    /**
     * 判断用户是否登录
     * @return
     */
    public boolean hasLogin() {
        return getUser()==null?false:true;
    }

    /**
     * 清除内存用户
     */
    public void removeUser(){
        mUser = null;
        removeLocal();
    }

    /**
     * 清除数据库用户
     */
    private void removeLocal(){

    }
}

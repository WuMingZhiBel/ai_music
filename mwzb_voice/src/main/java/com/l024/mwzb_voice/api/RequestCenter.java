package com.l024.mwzb_voice.api;

/**
 * @author wu_ming_zhi_bei
 * @date 2020/4/26 16:28
 * @Notes 请求地址
 */

import com.l024.lib_network.okhttp.CommonOkHttpClient;
import com.l024.lib_network.okhttp.listener.DisposeDataHandle;
import com.l024.lib_network.okhttp.listener.DisposeDataListener;
import com.l024.lib_network.okhttp.request.CommonRequest;
import com.l024.lib_network.okhttp.request.RequestParams;
import com.l024.mwzb_voice.model.user.User;

/**
 * 请求中心
 */
public class RequestCenter {

    static class HttpConstants {
        //private static final String ROOT_URL = "http://imooc.com/api";
        private static final String ROOT_URL = "http://39.97.122.129";

        /**
         * 首页请求接口
         */
        private static String HOME_RECOMMAND = ROOT_URL + "/module_voice/home_recommand";

        private static String HOME_RECOMMAND_MORE = ROOT_URL + "/module_voice/home_recommand_more";

        private static String HOME_FRIEND = ROOT_URL + "/module_voice/home_friend";

        /**
         * 登陆接口
         */
        public static String LOGIN = ROOT_URL + "/module_voice/login_phone";
    }

    //根据参数发送所有post请求
    public static void getRequest(String url, RequestParams params, DisposeDataListener listener,
                                  Class<?> clazz) {
        CommonOkHttpClient.get(CommonRequest.
                createGetRequest(url, params), new DisposeDataHandle(listener, clazz));
    }

    //public static void requestRecommandData(DisposeDataListener listener) {
    //    RequestCenter.getRequest(HttpConstants.HOME_RECOMMAND, null, listener,
    //            BaseRecommandModel.class);
    //}
    //
    //public static void requestRecommandMore(DisposeDataListener listener) {
    //    RequestCenter.getRequest(HttpConstants.HOME_RECOMMAND_MORE, null, listener,
    //            BaseRecommandMoreModel.class);
    //}

    //public static void requestFriendData(DisposeDataListener listener) {
    //    RequestCenter.getRequest(HttpConstants.HOME_FRIEND, null, listener, BaseFriendModel.class);
    //}

    /**
     * 用户登陆请求
     */
    public static void login(DisposeDataListener listener) {

        RequestParams params = new RequestParams();
        params.put("mb", "18734924592");
        params.put("pwd", "999999q");
        RequestCenter.getRequest(HttpConstants.LOGIN, params, listener, User.class);
    }
}

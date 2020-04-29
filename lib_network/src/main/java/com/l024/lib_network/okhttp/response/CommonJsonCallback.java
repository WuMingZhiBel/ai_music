package com.l024.lib_network.okhttp.response;

import android.os.Handler;
import android.os.Looper;

import com.l024.lib_network.okhttp.exception.OkHttpException;
import com.l024.lib_network.okhttp.listener.DisposeDataHandle;
import com.l024.lib_network.okhttp.listener.DisposeDataListener;
import com.l024.lib_network.okhttp.utils.ResponseEntityToModule;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @author wu_ming_zhi_bei
 * @date 2020/4/26 15:28
 * @Notes 网络请求返回类
 */
public class CommonJsonCallback implements Callback {
    /**
     * the logic layer exception, may alter in different app
     */
    // 有返回则对于http请求来说是成功的，但还有可能是业务逻辑上的错误
    protected final String RESULT_CODE = "ecode";
    protected final int RESULT_CODE_VALUE = 0;
    protected final String ERROR_MSG = "emsg";
    protected final String EMPTY_MSG = "";

    /**
     * the java layer exception, do not same to the logic error
     */
    protected final int NETWORK_ERROR = -1; // the network relative error
    protected final int JSON_ERROR = -2; // the JSON relative error
    protected final int OTHER_ERROR = -3; // the unknow error

    /**
     * 将其它线程的数据转发到UI线程
     */
    private Handler mDeliveryHandler;
    private DisposeDataListener mListener;
    private Class<?> mClass;

    public CommonJsonCallback(DisposeDataHandle handle) {
        this.mListener = handle.mListener;
        this.mClass = handle.mClass;
        this.mDeliveryHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onFailure(Call call, final IOException e) {
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onFailure(new OkHttpException(NETWORK_ERROR,e));
            }
        });
    }

    @Override
    public void onResponse(Call call, final Response response) throws IOException {
        //获取返回数据
        final String result = response.body().string();
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                handleResponse(result);
            }
        });
    }

    private void handleResponse(String result) {
        if(result==null || result.trim().equals("")){
            mListener.onFailure(new OkHttpException(NETWORK_ERROR,EMPTY_MSG));
            return;
        }
        try {
            //如果没有传递需要转换的实体类。则直接把结果返回
            if(mClass == null){
                mListener.onSuccess(result);
            }else{
                //JSON转实体类
                Object obj = ResponseEntityToModule.parseJsonToModule(result,mClass);
                if(obj!=null){
                    mListener.onSuccess(obj);
                }else{
                    //解析失败。返回
                    mListener.onFailure(new OkHttpException(JSON_ERROR,EMPTY_MSG));
                }
            }
        }catch (Exception e){
            mListener.onFailure(new OkHttpException(JSON_ERROR,EMPTY_MSG));
        }
    }
}

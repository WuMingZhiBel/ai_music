package com.l024.lib_network.okhttp.response;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.l024.lib_network.okhttp.exception.OkHttpException;
import com.l024.lib_network.okhttp.listener.DisposeDataHandle;
import com.l024.lib_network.okhttp.listener.DisposeDownloadListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @author wu_ming_zhi_bei
 * @date 2020/4/26 15:51
 * @Notes 处理文件类型的响应类
 */
public class CommonFileCallback implements Callback {

    /**
     * the java layer exception, do not same to the logic error
     */
    protected final int NETWORK_ERROR = -1; // the network relative error
    protected final int IO_ERROR = -2; // the JSON relative error
    protected final String EMPTY_MSG = "";
    /**
     * 将其它线程的数据转发到UI线程
     */
    private static final int PROGRESS_MESSAGE = 0x01;
    private Handler mDeliveryHandler;
    private DisposeDownloadListener mListener;
    private String mFilePath; //文件路径
    private int mProgress; //当前进度

    public CommonFileCallback(DisposeDataHandle handle) {
        this.mListener = (DisposeDownloadListener) handle.mListener;
        this.mFilePath = handle.mSource;
        this.mDeliveryHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    //进度条
                    case PROGRESS_MESSAGE:
                        mListener.onProgress((int) msg.obj);
                        break;
                }
            }
        };
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
    public void onResponse(Call call, Response response) throws IOException {
        //获取到数据
        final File file = handleResponse(response);
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                if(file!=null){
                    mListener.onSuccess(file);
                }else{
                    mListener.onFailure(new OkHttpException(IO_ERROR,EMPTY_MSG));
                }
            }
        });
    }

    /**
     * 获取文件在写入文件 并发送一个进度事件
     * @param response
     * @return
     */
    private File handleResponse(Response response) {
        if(response==null){
            return null;
        }
        InputStream inputStream = null;
        File file = null;
        FileOutputStream fos = null;
        byte[] buffter = new byte[1024];
        int length = 0;
        double sunLength = 0;
        double currentLength = 0;
        try {
            checkLocalFilePath(mFilePath);
            file = new File(mFilePath);
            fos = new FileOutputStream(file);
            inputStream = response.body().byteStream();
            sunLength = response.body().contentLength();
            while ((length = inputStream.read(buffter))!=-1){
                fos.write(buffter,0,buffter.length);
                currentLength += length;
                //计算进度
                mProgress = (int)(currentLength/sunLength*100);
                //进度发送出去
                mDeliveryHandler.obtainMessage(PROGRESS_MESSAGE,mProgress).sendToTarget();
            }
            fos.flush();
        }catch (Exception e){
            file = null;
        }finally {
            try{
                if(fos!=null){
                    fos.close();
                }
                if(inputStream!=null){
                    inputStream.close();
                }
            }catch (Exception e){
                file = null;
            }
        }
        return file;
    }

    //检查本地文件路径 创建目录以及创建文件
    private void checkLocalFilePath(String localFilePath) {
        File path = new File(localFilePath.substring(0,
                localFilePath.lastIndexOf("/") + 1));
        File file = new File(localFilePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

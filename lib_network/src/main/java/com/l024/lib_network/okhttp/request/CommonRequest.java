package com.l024.lib_network.okhttp.request;

import java.io.File;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author wu_ming_zhi_bei
 * @date 2020/4/26 14:27
 * @Notes 对外提供get/post请求
 */
public class CommonRequest {

    /**
     * 创建Post没有header头的Request对象
     * @param url
     * @param params
     * @return
     */
    public static Request createPostRequest(String url,RequestParams params){
        return createPostRequest(url,params,null);
    }

    /**
     * 创建Get没有header头的Request对象
     * @param url
     * @param params
     * @return
     */
    public static Request createGetRequest(String url,RequestParams params){
        return createGetRequest(url,params,null);
    }
    /**
     * 创建post的Request请求对象
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public static Request createPostRequest(String url,RequestParams params,RequestParams headers){
        FormBody.Builder mFormBodyBuildet = new FormBody.Builder();
        if(params!=null){
            for(Map.Entry<String,String> entry:params.urlParams.entrySet()){
                //参数遍历
                mFormBodyBuildet.add(entry.getKey(),entry.getValue());
            }
        }
        Headers.Builder mHeaderBuilder = new Headers.Builder();
        if(headers != null){
            for(Map.Entry<String,String> entry:headers.urlParams.entrySet()){
                //添加请求头
                mHeaderBuilder.add(entry.getKey(),entry.getValue());
            }
        }
        //创建request
        Request request = new Request
                .Builder()
                .url(url)
                .headers(mHeaderBuilder.build())
                .post(mFormBodyBuildet.build())
                .build();
        return request;
    }

    /**
     * 创建Get请求的Request对象
     */
    public static Request createGetRequest(String url,RequestParams params,RequestParams headers){
        StringBuilder sb = new StringBuilder(url).append("?");
        if(params!=null){
            for(Map.Entry<String,String> entry:params.urlParams.entrySet()){
                //参数遍历
                sb.append(entry.getKey());
                sb.append("=");
                sb.append(entry.getValue()+"&");
            }
        }
        Headers.Builder mHeaderBuilder = new Headers.Builder();
        if(headers != null){
            for(Map.Entry<String,String> entry:headers.urlParams.entrySet()){
                //添加请求头
                mHeaderBuilder.add(entry.getKey(),entry.getValue());
            }
        }
        return new Request.Builder()
                .url(url)
                .headers(mHeaderBuilder.build())
                .get()
                .build();
    }

    /**
     * 文件上传的Request请求
     */
    public static final MediaType FILE_TYPE = MediaType.parse("application/octet-stream");
    public static Request createMultiPostRequest(String url,RequestParams params,RequestParams headers) {
        MultipartBody.Builder requestBody = new MultipartBody.Builder();
        //文件上传指定表单提交
        requestBody.setType(MultipartBody.FORM);
        if(params!=null){
            for(Map.Entry<String,Object> entry:params.fileParams.entrySet()){
                //参数遍历
                if(entry.getValue() instanceof File){
                    requestBody.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                            RequestBody.create(FILE_TYPE, (File) entry.getValue()));
                }else if (entry.getValue() instanceof String) {
                    requestBody.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                            RequestBody.create(null, (String) entry.getValue()));
                }
            }
        }
        return new Request.Builder().url(url).post(requestBody.build()).build();
    }

}

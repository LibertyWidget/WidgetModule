package com.util.http;

import android.content.Context;
import android.os.Build;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.util.ThreadUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;


public class HttpManager {

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");//mdiatype 这个需要和服务端保持一致
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static volatile HttpManager mInstance;//单利引用
    private OkHttpClient mOkHttpClient;//okHttpClient 实例

    public enum RequestType {
        GET,//get请求
        POST_JSON,//post请求参数为json
        POST_FORM//post请求参数为表单
    }


    /**
     * 初始化RequestManager
     */
    private HttpManager(Context context) {
        //初始化OkHttpClient
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);//设置超时时间
        mOkHttpClient.setReadTimeout(10, TimeUnit.SECONDS);//设置读取超时时间
        mOkHttpClient.setReadTimeout(10, TimeUnit.SECONDS);//设置写入超时时间
    }

    /**
     * 获取单例引用
     */
    public static HttpManager $(Context context) {
        HttpManager inst = mInstance;
        if (inst == null) {
            synchronized (HttpManager.class) {
                inst = mInstance;
                if (inst == null) {
                    inst = new HttpManager(context.getApplicationContext());
                    mInstance = inst;
                }
            }
        }
        return inst;
    }


    /**
     * okHttp同步请求统一入口
     *
     * @param actionUrl   接口地址
     * @param requestType 请求类型
     * @param paramsMap   请求参数
     */
    public Response requestSync(String actionUrl, RequestType requestType, HashMap<String, String> paramsMap) {
        Response response = null;
        switch (requestType) {
            case GET:
                response = requestGetBySync(actionUrl, paramsMap);
                break;
            case POST_JSON:
                response = requestPostBySync(actionUrl, paramsMap);
                break;
            case POST_FORM:
                response = requestPostBySyncWithForm(actionUrl, paramsMap);
                break;
        }
        return response;
    }


    /**
     * okHttp get同步请求
     *
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     */
    private Response requestGetBySync(String actionUrl, HashMap<String, String> paramsMap) {
        StringBuilder tempParams = new StringBuilder();
        try {
            //处理参数
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                //对参数进行URLEncoder
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            //补全请求地址
            String requestUrl = String.format("%s?%s", actionUrl, tempParams.toString());
            //创建一个请求
            Request request = addHeaders().url(requestUrl).build();
            //创建一个Call
            final Call call = mOkHttpClient.newCall(request);
            //执行请求
            return call.execute();
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * okHttp post同步请求
     *
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     */
    private Response requestPostBySync(String actionUrl, HashMap<String, String> paramsMap) {
        try {
            //处理参数
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            //补全请求地址
            String requestUrl = String.format("%s", actionUrl);

            //生成参数
            String params = tempParams.toString();
            //创建一个请求实体对象 RequestBody
            RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, params);
            //创建一个请求
            final Request request = addHeaders().url(requestUrl).post(body).build();
            //创建一个Call
            final Call call = mOkHttpClient.newCall(request);
            //执行请求
            return call.execute();
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * okHttp post同步请求表单提交
     *
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     */
    private Response requestPostBySyncWithForm(String actionUrl, HashMap<String, String> paramsMap) {
        try {
            //创建一个FormBody.Builder
            JSONObject jsonArray = new JSONObject();
            for (String key : paramsMap.keySet()) {
                //追加表单信息
                jsonArray.put(key, paramsMap.get(key));
            }
            RequestBody body = RequestBody.create(JSON, jsonArray.toString());
            //补全请求地址
            String requestUrl = String.format("%s", actionUrl);
            //创建一个请求
            final Request request = addHeaders().url(requestUrl).post(body).build();
            //创建一个Call
            final Call call = mOkHttpClient.newCall(request);
            //执行请求
            return call.execute();
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * okHttp异步请求统一入口
     *
     * @param actionUrl   接口地址
     * @param requestType 请求类型
     * @param paramsMap   请求参数
     * @param callBack    请求返回数据回调
     **/
    public Call requestAsync(String actionUrl, RequestType requestType, HashMap<String, String> paramsMap, ReqCallBack callBack) {
        Call call = null;
        switch (requestType) {
            case GET:
                call = requestGetByAsync(actionUrl, paramsMap, callBack);
                break;
            case POST_JSON:
                call = requestPostByAsync(actionUrl, paramsMap, callBack);
                break;
            case POST_FORM:
                call = requestPostByAsyncWithForm(actionUrl, paramsMap, callBack);
                break;
        }
        return call;
    }

    /**
     * okHttp get异步请求
     *
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     * @param callBack  请求返回数据回调
     */
    private Call requestGetByAsync(String actionUrl, HashMap<String, String> paramsMap, final ReqCallBack callBack) {

        try {
            Request request = null;
            if (null != paramsMap) {
                StringBuilder tempParams = new StringBuilder();
                int pos = 0;
                for (String key : paramsMap.keySet()) {
                    if (pos > 0) {
                        tempParams.append("&");
                    }
                    tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                    pos++;
                }
                String requestUrl = String.format("%s?%s", actionUrl, tempParams.toString());
                request = addHeaders().url(requestUrl).build();
            } else {
                request = addHeaders().url(actionUrl).build();
            }


            final Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    failedCallBack(e.getMessage(), callBack);
                }

                @Override
                public void onResponse(Response response) {
                    if (response.isSuccessful()) {
                        successCallBack(response, callBack);
                    } else {
                        failedCallBack("Server Error", callBack);
                    }
                }
            });
            return call;
        } catch (Exception ignored) {
        }
        return null;
    }


    /**
     * okHttp post异步请求
     *
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     * @param callBack  请求返回数据回调
     */
    private Call requestPostByAsync(String actionUrl, HashMap<String, String> paramsMap, final ReqCallBack callBack) {
        try {
            Request request = null;
            String requestUrl = String.format("%s", actionUrl);
            if (null != paramsMap) {
                StringBuilder tempParams = new StringBuilder();
                int pos = 0;
                for (String key : paramsMap.keySet()) {
                    if (pos > 0) {
                        tempParams.append("&");
                    }
                    tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                    pos++;
                }
                String params = tempParams.toString();
                RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, params);
                request = addHeaders().url(requestUrl).post(body).build();
            } else {
                request = addHeaders().url(requestUrl).build();
            }
            final Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    failedCallBack(e.getMessage(), callBack);
                }

                @Override
                public void onResponse(Response response) {
                    if (response.isSuccessful()) {
                        successCallBack(response, callBack);
                    } else {
                        failedCallBack("Server Error", callBack);
                    }
                }

            });
            return call;
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * okHttp post异步请求表单提交
     *
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     * @param callBack  请求返回数据回调
     */
    private Call requestPostByAsyncWithForm(String actionUrl, HashMap<String, String> paramsMap, final ReqCallBack callBack) {
        String requestUrl = String.format("%s", actionUrl);
        try {
            Request request = null;
            if (null != paramsMap) {
                //创建一个FormBody.Builder
                JSONObject jsonArray = new JSONObject();
                for (String key : paramsMap.keySet()) {
                    //追加表单信息
                    jsonArray.put(key, paramsMap.get(key));
                }
                RequestBody body = RequestBody.create(JSON, jsonArray.toString());
                request = addHeaders().url(requestUrl).post(body).build();
            } else {
                request = addHeaders().url(requestUrl).build();
            }

            final Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    failedCallBack(e.getMessage(), callBack);
                }

                @Override
                public void onResponse(Response response) {
                    if (response.isSuccessful()) {
                        successCallBack(response, callBack);
                    } else {
                        failedCallBack("Server Error", callBack);
                    }
                }
            });
            return call;
        } catch (Exception ignored) {
        }
        return null;
    }

    public interface ReqCallBack {
        /**
         * 响应成功
         */
        void onReqSuccess(Response result);

        /**
         * 响应失败
         */
        void onReqFailed(String errorMsg);
    }

    /**
     * 统一为请求添加头信息
     */
    private Request.Builder addHeaders() {
        Request.Builder builder = new Request.Builder()
                .addHeader("Connection", "keep-alive")
                .addHeader("platform", "2")
                .addHeader("phoneModel", Build.MODEL)
                .addHeader("systemVersion", Build.VERSION.RELEASE)
                .addHeader("appVersion", "3.2.0");
        return builder;
    }

    /**
     * 统一同意处理成功信息
     */
    private void successCallBack(final Response result, final ReqCallBack callBack) {
        ThreadUtil.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onReqSuccess(result);
                }
            }
        });
    }

    /**
     * 统一处理失败信息
     */
    private void failedCallBack(final String errorMsg, final ReqCallBack callBack) {
        ThreadUtil.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onReqFailed(errorMsg);
                }
            }
        });
    }

    /**
     * 创建带进度的RequestBody
     *
     * @param file     准备上传的文件
     * @param callBack 回调
     */
    public RequestBody createProgressRequestBody(final MediaType contentType, final File file, final ReqProgressCallBack callBack) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) {
                Source source;
                try {
                    source = Okio.source(file);
                    Buffer buf = new Buffer();
                    long remaining = contentLength();
                    long current = 0;
                    for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                        sink.write(buf, readCount);
                        current += readCount;
                        progressCallBack(remaining, current, callBack);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public interface ReqProgressCallBack extends ReqCallBack {
        /**
         * 响应进度更新
         */
        void onProgress(long total, long current);
    }

    /**
     * 统一处理进度信息
     *
     * @param total   总计大小
     * @param current 当前进度
     */
    private void progressCallBack(final long total, final long current, final ReqProgressCallBack callBack) {
        ThreadUtil.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onProgress(total, current);
                }
            }
        });
    }
}

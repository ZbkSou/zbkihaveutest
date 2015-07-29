package com.bkzhou.models;

import android.content.Context;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bkzhou.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bkzhou on 15-7-27.
 */
public class UtilVolley {
    private final static String TAG = "Model";

    private Context mContext ;

    /**
     * 请求队列 Volley
     */
    private RequestQueue mQueue;
    /**
     * 图片加载
     *
     * @param context
     */
    private ImageLoader mImageLoader;
    private BitmapLruCache bitmapLruCache;
    /**
     * 图片缓存大小 20MB
     */
    private static final int MAX_CACHE_SIZE = (int) (Runtime.getRuntime().maxMemory() / (8 * 1024));
    private static Toast mErrorToast = null;
    /**
     * Cookie
     */
    private String mCookies;
    private String mUserAgent;
    /**
     * 网络出错时是否显示错误Toast 默认不显示
     */
    private static boolean needShowToast = false;
    /**
     * 请求重试时间Timeout
     */
    private static final int MY_SOCKET_TIMEOUT_MS = 60000 * 5;
    public UtilVolley (){

    }
    public UtilVolley(Context context){
        mContext = context;
        if(context == null){
            Log.e(TAG,"init error: context is null");
        }
        if (mQueue == null){
            mQueue = Volley.newRequestQueue(context);
        }
        if(mImageLoader == null){
            bitmapLruCache = new BitmapLruCache(MAX_CACHE_SIZE);
            mImageLoader = new ImageLoader(mQueue, bitmapLruCache);
        }
        mErrorToast = Toast.makeText(mContext, "网络错误", Toast.LENGTH_SHORT);
        WebView webView = new WebView(mContext);
        WebSettings settings = webView.getSettings();
        mUserAgent = settings.getUserAgentString();
        webView = null;
        settings = null;
    }

    /**
     * 清空请求
     */
    public void cncelAllRequest(){
        mQueue.cancelAll("all");
    }

    /**
     * 清空图片缓存
     */
    public void releaseCache(){
        if (bitmapLruCache != null){
            Log.d(TAG, "releaseCache");
            bitmapLruCache.release();
        }
    }
    /**
     * 设置Cookie 保持登录状态
     */
    public void setmCookies(String cookies){
        mCookies = cookies;
    }
    public String getCookie() {
        return mCookies;
    }

    public void clearCookie() {
        mCookies = null;
    }
    /**
     * 取消或显示错误Toast
     *
     * @param need
     */
    public void setShowErrorToast(boolean need) {
        needShowToast = need;
    }
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
//    返回处理接口
    /**
     * Json 返回值
     *
     */
    public interface JsonResponse {
        public void onSuccess(JSONObject response ,JSONArray responseArray);

        public void onError(VolleyError error);
    }

    /**
     * String 返回值
     *
     */
    public interface StringResponse {
        public void onSuccess(String response);

        public void onError(VolleyError error);
    }
//    网络请求方法

    /**
     * get
     * url 附件参数
     * @param url
     * @param jsonResponse
     */
    public  void get (String url,final JsonResponse jsonResponse   ){
        Log.d(TAG,"get from :"+url);
        request(Request.Method.GET, url, null, null, jsonResponse);
    }

    /**
     * Get
     * 自带参数
     *
     * @param url
     * @param params
     * @param jsonResponse
     */
    public void get(String url, Map<String, String> params, final JsonResponse jsonResponse) {
        StringBuilder newUrl = new StringBuilder();
        newUrl.append(url);
        if (params != null) {
            if (url.indexOf("?") < 0) {
                newUrl.append("?");
            }
            for (String key : params.keySet()) {
                newUrl.append(key);
                newUrl.append("=");
                newUrl.append(params.get(key));
                newUrl.append("&");
            }
            newUrl.deleteCharAt(newUrl.length() - 1);
        }
        request(Request.Method.GET, newUrl.toString(), params, jsonResponse);
    }
    /**
     * PUT
     *
     * @param url
     * @param jsonResponse
     */
    public void put(String url, Map<String, String> request, final JsonResponse jsonResponse) {
        request(Request.Method.PUT, url, request, jsonResponse);
    }

    /**
     * Post
     *
     * @param url
     * @param jsonResponse
     */
    public void post(String url, Map<String, String> request, final JsonResponse jsonResponse) {
        request(Request.Method.POST, url, request, jsonResponse);
    }

    /**
     * Delete
     *
     * @param url
     * @param request
     * @param jsonResponse
     */
    public void delete(String url, VolleyParams request, final JsonResponse jsonResponse) {
        request(Request.Method.DELETE, url, request, jsonResponse);
    }
    /**
     * 请求
     *
     * @param request
     */
//    public void request(Request request) {
//        request.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        mQueue.add(request);
//    }

    /**
     * 请求数据
     * 参数不可重复
     *
     * @param method
     * @param url
     * @param params       Map
     * @param jsonResponse
     */
    private void request(int method, String url, final Map<String, String> params, final JsonResponse jsonResponse) {
        request(method, url, params, null, jsonResponse);
    }

    /**
     * 请求数据
     * 参数可重复
     *
     * @param method
     * @param url
     * @param params
     * @param jsonResponse
     */
    private void request(int method, String url, final VolleyParams params, final JsonResponse jsonResponse) {
        request(method, url, null, params, jsonResponse);
    }

    /**
     * 请求数据用来统一处理请求
     * @param method
     * @param url
     * @param params
     * @param vParams
     * @param jsonResponse
     */
    private void request (int method , String url ,final Map<String,String>params, final VolleyParams vParams, final JsonResponse jsonResponse){
        if(mQueue == null){
            Log.e(TAG,"queue not init,please init model first");
            return ;
        }
        StringRequest strRequest = new StringRequest(method , url, new Response.Listener<String>() {

            @Override
            public void onResponse(String s) {
                JSONArray jsonArr = new JSONArray();
                JSONObject jsonObj = new JSONObject();

                    try {
                        if (s.trim().startsWith("[")) {
                            jsonArr = new JSONArray(s);
                        }else if (s.trim().startsWith("{")){
                            jsonObj = new JSONObject(s);
                        }
                        jsonResponse.onSuccess(jsonObj,jsonArr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        }, new Response.ErrorListener()   {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                jsonResponse.onError(volleyError);
                if (needShowToast && mErrorToast != null) {
                    mErrorToast.show();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String ,String > heahers  = new HashMap<String,String>();
                if(mCookies != null &&  mCookies.length()>0){

                }

                return headers;
            }
        };

    }
}

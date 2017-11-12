package com.gyf.wyf;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.appcompat.BuildConfig;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.HashMap;

import io.github.wangeason.multiphotopicker.PhotoPickerActivity;
import io.github.wangeason.multiphotopicker.utils.PhotoPickerIntent;

public class MainActivity extends AppCompatActivity implements JavaScriptObject.OnCallAppListener {

    public static final String TAG = "YP";
    public static final String KEY_PhotoCmd = "KEY_PhotoCmd";
    public static final String KEY_PhotoCmd_Last = "KEY_PhotoCmd_Last";
    //    public static final String MAIN_URL = "http://wyf.cngyf.com/mpcctp/login.jsp";
    // 掌中付
    public static String MAIN_URL;// = "http://www.4007112233.com:12001/mpcctp/login.jsp?_oemId=2015000000";
    private static final int REQUEST_CODE = 10000;
    //    public static final String MAIN_URL = "http://xiejinz.6655.la/mpcctp/login.jsp";
    HashMap<String, PhotoCmd> photoCmdMap = new HashMap<>();

    JavaScriptObject javaScriptObject;

    Handler handler = new Handler();

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MAIN_URL = getString(R.string.server_url);
        //MAIN_URL += ("?app=" + getString(R.string.app) + "&TID=" + Utils.getCombinedDeviceID(this));

        String deviceID = Utils.getDeviceId(this);
        String md5deviceID = Utils.getCombinedDeviceID(this);
        Log.d(TAG, "deviceID=" + deviceID);
        Log.d(TAG, "md5deviceID=" + md5deviceID);
        javaScriptObject = new JavaScriptObject(this);
        webView = (WebView) findViewById(R.id.webView);

        if (savedInstanceState != null) {
            photoCmdMap = (HashMap<String, PhotoCmd>) savedInstanceState.getSerializable(KEY_PhotoCmd);

            PhotoCmd photoCmd = savedInstanceState.getParcelable(KEY_PhotoCmd_Last);
            javaScriptObject.photoCmd = photoCmd;

            Log.w(TAG, photoCmd.toString());

            initView();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.w(TAG, "onCreate----appCallJsFunction");
                    for (PhotoCmd cmd : photoCmdMap.values()) {
                        Log.w(TAG, "onCreate----cmd---");
                        appCallJsFunction(cmd);
                    }
                }
            }, 1000);

        } else {
            initView();
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.w(TAG, photoCmdMap.toString());
        if (photoCmdMap != null) {
            outState.putSerializable(KEY_PhotoCmd, photoCmdMap);
            outState.putParcelable(KEY_PhotoCmd_Last, javaScriptObject.photoCmd);
        }

    }

    private void initView() {

        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setBackgroundColor(Color.argb(0, 0, 0, 0));

        javaScriptObject.setOnCallAppListener(this);
        webView.addJavascriptInterface(javaScriptObject, "myApp");

        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webView.getSettings().setBlockNetworkImage(false);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        webView.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webView.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        webView.getSettings().setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        webView.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        webView.getSettings().setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        //webView.getSettings().setAppCacheEnabled(true);//是否使用缓存
        webView.getSettings().setDomStorageEnabled(true);//DOM Storage
        // 支持数据库
        webView.getSettings().setDatabaseEnabled(true);

        // 支持缓存
        webView.getSettings().setAppCacheEnabled(true);
        String appCaceDir = this.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        webView.getSettings().setAppCachePath(appCaceDir);

        // displayWebview.getSettings().setUserAgentString("User-Agent:Android");//设置用户代理，一般不用

        webView.loadUrl(MAIN_URL);
        Log.d(TAG, "MAIN_URL=" + MAIN_URL);
        if (javaScriptObject.photoCmd != null && javaScriptObject.photoCmd.getUrl() != null) {
            webView.loadUrl(javaScriptObject.photoCmd.getUrl());
        }
    }

    @Override
    public void callAppFunction(String cmd, String data) {

        if ("2001".equals(cmd) && data != null) {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(data));
            startActivity(intent);

            return;
        }

        if ("3001".equals(cmd)) {
            String deviceID = Utils.getCombinedDeviceID(this);
            Log.d(TAG, "deviceID=" + deviceID);
            appCallJsFunction2(javaScriptObject.photoCmd, deviceID);
            return;
        }

        if ("3002".equals(cmd)) {
            appCallJsFunction2(javaScriptObject.photoCmd, BuildConfig.VERSION_NAME);
            return;
        }

        PhotoPickerIntent intent = new PhotoPickerIntent(MainActivity.this);
        intent.setPhotoCount(1);
        if ("1001".equals(cmd) || "1002".equals(cmd)) {
            intent.setShowCamera(true);
        } else {
            intent.setShowCamera(false);
        }

        intent.setShowGif(false);
        intent.setMultiChoose(false);
        startActivityForResult(intent, REQUEST_CODE);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                javaScriptObject.photoCmd.setUrl(webView.getUrl());
                photoCmdMap.put(javaScriptObject.photoCmd.getRealData(), javaScriptObject.photoCmd);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.w(TAG, "onActivityResult....");
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                for (String photo : photos) {
                    Log.w(TAG, photo);

                    PhotoCmd photoCmd = photoCmdMap.get(javaScriptObject.photoCmd.getRealData());
                    photoCmd.imageBase64 = Utils.fileToBase64(photo);

                    Log.w(TAG, photoCmd.toString());

                    appCallJsFunction(photoCmd);
                }
            }
        }
    }

    void appCallJsFunction(PhotoCmd photoCmd) {
        String img = "javascript:appCallJsFunction('1', '"
                + photoCmd.cmd + "', '"
                + photoCmd.getRealData() + "',"
                + "'data:image/png;base64,"
                + photoCmd.imageBase64 + "')";

//        String format = String.format("javascript:appCallJsFunction('1', '%s', '%s', '%s')", javaScriptObject.cmd, javaScriptObject.data, img);
        Log.w(TAG, img);
        webView.loadUrl(img);
    }

    void appCallJsFunction2(PhotoCmd photoCmd, String data) {
        String img = "javascript:appCallJsFunction('1', '"
                + photoCmd.cmd + "', '"
                + photoCmd.getRealData() + "',"
                + data + "')";

        Log.w(TAG, img);
        webView.loadUrl(img);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            Log.w(TAG, url);
            return true;
        }

      /*  @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Log.w(TAG, request.getUrl().getPath());
            }
            return true;
        }*/

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.w(TAG, "onPageFinished----" + url);
            if (javaScriptObject.photoCmd != null && url.equals(javaScriptObject.photoCmd.getUrl())) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.w(TAG, "onPageFinished----onPageFinished");
                        for (PhotoCmd cmd : photoCmdMap.values()) {
                            Log.w(TAG, "onPageFinished----cmd---");
                            appCallJsFunction(cmd);
                        }
                    }
                });

            }

        }
    }
}

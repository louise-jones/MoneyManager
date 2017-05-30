package com.minidroid.moneymanager.ui.invest;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.permission.PermissionManagerActivity;
import com.minidroid.moneymanager.view.swipemenulistview.custom.CustomProgressDialog;

/**
 * 理财界面
 * Created by minidroid on 2017/4/23.
 * Email:460821714@qq.com
 */
public class InvestActivity extends PermissionManagerActivity {
    private WebView mWebView;
    private static final String URL = "https://jr.yatang.cn/";
    private LinearLayout mLayout;
    private CustomProgressDialog mProgressDialog;
    private WebChromeClient mWebChromeClient;
    private WebViewClient mWebViewClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest);
        init();
    }

    private void init() {
        mLayout = (LinearLayout) findViewById(R.id.web_layout);
        //添加webview
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView = new WebView(getApplicationContext());
        mWebView.setLayoutParams(params);
        mLayout.addView(mWebView);
        //webview设置
        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setSupportZoom(true);                      //支持缩放，默认为true。是下面那个的前提。
        mWebSettings.setBuiltInZoomControls(true);              //设置内置的缩放控件。
        //设置自适应屏幕，两者合用
        mWebSettings.setLoadWithOverviewMode(true);             // 缩放至屏幕的大小
        mWebSettings.setUseWideViewPort(true);                  //将图片调整到适合webview的大小
        mWebSettings.setDefaultTextEncodingName("utf-8");       //设置编码格式
        mWebSettings.setLoadsImagesAutomatically(true);         //支持自动加载图片

        mProgressDialog = CustomProgressDialog.createDialog(this);
        mProgressDialog.setMessage("加载中...");
        mProgressDialog.show();

        //WebChromeClient是辅助WebView处理Javascript的对话框，网站图标，网站title，加载进度等 :
        mWebChromeClient = new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress >= 100) {
                    mProgressDialog.dismiss();
                }
            }
        };

        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        mWebViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
            //处理超链接
            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                view.loadUrl(URL);
                return true;
            }
        };
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.setWebViewClient(mWebViewClient);
        //WebView加载web资源
        mWebView.loadUrl(URL);
    }

    //在 Activity 销毁的时候，可以先让 WebView 加载null内容，然后移除 WebView，再销毁 WebView，最后置空。
    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    //改写物理按键——返回的逻辑
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();//返回上一页面
                return true;
            } else {
               // System.exit(0);//退出程序
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}

package com.coffesoft.cmd.eleitorconectado;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

/**
 * Created by GabrielPaulino on 24/05/2016.
 */

public class Tela_browser extends FragmentActivity {

    private WebView browser;
    private String url;
    private EditText link;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_browser);

        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("url"))
            url = ((bundle.getString("url")));

        browser = (WebView) findViewById(R.id.WV_tb_browser);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.setWebChromeClient(new WebChromeClient());
        browser.getSettings().setPluginState(WebSettings.PluginState.ON);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        browser.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        browser.setWebViewClient(new WebViewClient());


        browser.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        browser.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (browser.canGoBack()) {
            browser.goBack();
        } else {
            finish();
        }
    }

    /**
     * Created by GabrielPaulino on 20/08/2016.
     */

}
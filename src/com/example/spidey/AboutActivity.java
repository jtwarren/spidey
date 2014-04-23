package com.example.spidey;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class AboutActivity extends Activity {  
	
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);  
        WebView wv;  
        wv = (WebView) findViewById(R.id.webview);  
        wv.loadUrl("file:///android_asset/spidey.html");   // now it will not fail here
    }  
}
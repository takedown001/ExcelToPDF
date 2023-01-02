package com.trainig.excelviewer;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class webView extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_web);
        WebView webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=" +"https://blanched-jurisdicti.000webhostapp.com/pdf/doc.pdf");
    }
}

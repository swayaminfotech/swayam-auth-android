package com.swayamauth.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.swayamauth.R;
import com.swayamauth.databinding.ActivityWeburlBinding;

public class WebUrlViewActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityWeburlBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeburlBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getSupportActionBar().hide();
        binding.header.tvTitle.setText(this.getString(R.string.terms_use));
        binding.flLoader.flLoader.setVisibility(View.VISIBLE);
        WebSettings webSettings = binding.wbView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        binding.wbView.setWebChromeClient(new WebChromeClient());
        binding.wbView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    if (binding.flLoader != null) {
                        binding.flLoader.flLoader.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if(getIntent().getExtras() != null) {
            binding.wbView.loadUrl(getIntent().getExtras().getString("url"));
            binding.header.tvTitle.setText(getIntent().getExtras().getString("title"));
        }
        setClickListener();
    }

    // Set click listener
    private void setClickListener() {
        binding.header.ivBack.setOnClickListener(this);
    }

    // Handle click event
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                finish();
                break;
        }
    }
}
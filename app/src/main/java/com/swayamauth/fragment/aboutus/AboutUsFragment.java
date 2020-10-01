package com.swayamauth.fragment.aboutus;

/**
 * Created by swayam infotech
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.swayamauth.databinding.FragmentAboutUsBinding;

public class AboutUsFragment extends Fragment {

    FragmentAboutUsBinding binding;
    private AboutUsViewModel aboutUsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAboutUsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        aboutUsViewModel =
                new ViewModelProvider(this).get(AboutUsViewModel.class);

        setData();

        return root;
    }

    // Set url in webview
    private void setData() {
        binding.prgLoader.flLoader.setVisibility(View.VISIBLE);
        aboutUsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                WebSettings webSettings = binding.wbView.getSettings();

                webSettings.setJavaScriptEnabled(true);
                webSettings.setUseWideViewPort(true);
                webSettings.setLoadWithOverviewMode(true);
                binding.wbView.setWebChromeClient(new WebChromeClient());
                binding.wbView.setWebViewClient(new WebViewClient() {

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        try {
                            if (binding.prgLoader != null) {
                                binding.prgLoader.flLoader.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                binding.wbView.loadUrl(s);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
package com.swayamauth.fragment.aboutus;

/**
 * Created by swayam infotech
 */

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.swayamauth.helper.Constants;

public class AboutUsViewModel extends ViewModel {

    private MutableLiveData<String> mUrl;

    public AboutUsViewModel() {
        mUrl = new MutableLiveData<>();
        mUrl.setValue(Constants.ABOUT_US_URL);
    }

    public LiveData<String> getText() {
        return mUrl;
    }
}

package com.swayamauth.fragment.home;

/**
 * Created by swayam infotech
 */

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.swayamauth.helper.Constants;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mUrl;

    public HomeViewModel() {
        mUrl = new MutableLiveData<>();
        mUrl.setValue(Constants.ABOUT_US_URL);
    }

    public LiveData<String> getText() {
        return mUrl;
    }
}

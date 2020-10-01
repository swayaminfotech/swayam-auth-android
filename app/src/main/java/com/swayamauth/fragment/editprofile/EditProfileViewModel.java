package com.swayamauth.fragment.editprofile;

/**
 * Created by swayam infotech
 */

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.swayamauth.Utils.Prefs;
import com.swayamauth.Utils.Utils;
import com.swayamauth.helper.Constants;
import com.swayamauth.model.UserProfileResponse;
import com.swayamauth.network.ApiClient;
import com.swayamauth.network.ApiInterfaceService;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileViewModel extends ViewModel {

    private MutableLiveData<UserProfileResponse> mProfileView;
    private Context context;

    public EditProfileViewModel() {
        mProfileView = new MutableLiveData<>();
        getUserDetail();
    }

    public void setContext(Context context) {
        this.context = context;
        getUserDetail();
    }

    // Get User details API Call
    private void getUserDetail() {

        if(context != null && Utils.isNetworkAvailable(context)) {

            ApiInterfaceService mApiService = ApiClient.getClient().create(ApiInterfaceService.class);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("user_id", Prefs.getString(context, Constants.KEY_USER_ID, ""));
            hashMap.put("logged_id", Prefs.getString(context, Constants.KEY_USER_ID, ""));
            Call<UserProfileResponse> call = mApiService.getUserDetails(hashMap);

            call.enqueue(new Callback<UserProfileResponse>() {
                @Override
                public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                    try {
                        mProfileView.setValue(response.body());
                    } catch (NullPointerException e) {
                        mProfileView.setValue(new UserProfileResponse("no", ""));
                    }
                }

                @Override
                public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                    mProfileView.setValue(new UserProfileResponse("no", ""));
                }
            });
        }
    }

    public LiveData<UserProfileResponse> getText() {
        return mProfileView;
    }
}

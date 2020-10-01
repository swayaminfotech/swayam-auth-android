package com.swayamauth.fragment.changepassword;

/**
 * Created by swayam infotech
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.swayamauth.R;
import com.swayamauth.Utils.Utils;
import com.swayamauth.databinding.FragmentChangePasswordBinding;
import com.swayamauth.fragment.BaseFragment;
import com.swayamauth.helper.DialogAlertHelper;
import com.swayamauth.listener.DialogClickListener;
import com.swayamauth.model.AlertSuccessResponse;
import com.swayamauth.network.ApiClient;
import com.swayamauth.network.ApiInterfaceService;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordFragment extends BaseFragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    FragmentChangePasswordBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentChangePasswordBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setClickListener();

        return root;
    }

    // set click listener
    private void setClickListener() {
        binding.btnChangePassword.setOnClickListener(this);
    }

    // Handle Click event
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnChangePassword:
                if(isValid()) {
                    if(Utils.isNetworkAvailable(getActivity())) {
                        changePasswordAPI();
                    }
                }
                break;
        }
    }

    // Change Password API call
    private void changePasswordAPI() {
        if(binding.prgLoader != null) {
            binding.btnChangePassword.setVisibility(View.GONE);
            binding.prgLoader.setVisibility(View.VISIBLE);
        }
        ApiInterfaceService mApiService = ApiClient.getClient().create(ApiInterfaceService.class);

        String mOldPassword = binding.etOldPassword.getText().toString();
        String mPassword = binding.etPassword.getText().toString();

        HashMap<String, Object> payloadLogin = new HashMap<String, Object>();

        payloadLogin.put("oldPassword", Utils.get_SHA_512_SecurePassword("", mOldPassword));
        payloadLogin.put("password", Utils.get_SHA_512_SecurePassword("", mPassword));

        Call<AlertSuccessResponse> call = mApiService.ChangePassword(payloadLogin);

        call.enqueue(new Callback<AlertSuccessResponse>() {
            @Override
            public void onResponse(Call<AlertSuccessResponse> call, Response<AlertSuccessResponse> response) {

                if (binding.prgLoader != null) {
                    binding.btnChangePassword.setVisibility(View.VISIBLE);
                    binding.prgLoader.setVisibility(View.GONE);
                }
                try {
                    if (response.body().getSuccess().equals("yes")) {

                        DialogAlertHelper.show_dialog_OK_response(getActivity(), response.body().getMessage(), new DialogClickListener() {
                            @Override
                            public void onOkPress() {

                            }

                            @Override
                            public void onCancelPress() {

                            }
                        });

                    } else if (response.body().getSuccess().equals("no")) {
                        showErrorDialog(getActivity().getString(R.string.error), response.body().getMessage());
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<AlertSuccessResponse> call, Throwable t) {
                if (binding.prgLoader != null) {
                    binding.btnChangePassword.setVisibility(View.VISIBLE);
                    binding.prgLoader.setVisibility(View.GONE);
                }
                showErrorDialog(getActivity().getString(R.string.error),  getResources().getString(R.string.some_thing_went_wrong));
            }
        });
    }

    // Check fileds validations
    boolean isValid() {
        boolean isValid = true;
        String message = "";
        String title = "";

        String mOldPassword = binding.etOldPassword.getText().toString();
        String mPassword = binding.etPassword.getText().toString();
        String mConfirmNewPassword = binding.etConfirmNewPassword.getText().toString();

        if(Utils.isStringNull(mOldPassword)){
            isValid = false;
            message = getString(R.string.error_blank_validation_old_password);
            title = getString(R.string.error_required_password);
            binding.etOldPassword.requestFocus();
        } else if(Utils.isStringNull(mPassword)){
            isValid = false;
            message = getString(R.string.error_blank_validation_new_password);
            title = getString(R.string.error_required_password);
            binding.etPassword.requestFocus();
        } else if(mPassword.length() < 8){
            isValid = false;
            message = getString(R.string.error_validation_length_password);
            title = getString(R.string.error_required_password);
            binding.etPassword.requestFocus();
        } else if(Utils.isStringNull(mConfirmNewPassword)){
            isValid = false;
            message = getString(R.string.error_blank_validation_new_confirm_password);
            title = getString(R.string.error_required_password);
            binding.etConfirmNewPassword.requestFocus();
        } else if(!mPassword.equals(mConfirmNewPassword)){
            isValid = false;
            message = getString(R.string.error_mismatch_password);
            title = getString(R.string.error_required_password);
            binding.etConfirmNewPassword.requestFocus();
        }
        if(!isValid) {
            showErrorDialog(title, message);
        }
        return isValid;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
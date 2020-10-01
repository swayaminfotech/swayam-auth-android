package com.swayamauth.activity;

/**
 * Created by swayam infotech
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.swayamauth.BaseActivity;
import com.swayamauth.R;
import com.swayamauth.Utils.Utils;
import com.swayamauth.databinding.ActivityForgotPasswordBinding;
import com.swayamauth.helper.DialogAlertHelper;
import com.swayamauth.listener.DialogClickListener;
import com.swayamauth.model.AlertSuccessResponse;
import com.swayamauth.network.ApiClient;
import com.swayamauth.network.ApiInterfaceService;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener {

    ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setClickListener();

        getSupportActionBar().hide();

        binding.header.tvTitle.setText(this.getString(R.string.forgot_password));

        binding.llTextField.setVisibility(View.VISIBLE);

    }

    // Set click listener
    private void setClickListener() {
        binding.btnForgotPassword.setOnClickListener(this);
        binding.header.ivBack.setOnClickListener(this);
    }

    // Handle click listener
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnForgotPassword:
                if(isValid()) {
                    if(Utils.isNetworkAvailable(ForgotPasswordActivity.this)) {
                        forgotAPI();
                    }
                }
                break;

            case R.id.ivBack:
                finish();
                break;

        }
    }

    // Call Forgot API
    private void forgotAPI() {
        if(binding.prgLoader != null) {
            binding.btnForgotPassword.setVisibility(View.GONE);
            binding.prgLoader.setVisibility(View.VISIBLE);
        }
        ApiInterfaceService mApiService = ApiClient.getClient().create(ApiInterfaceService.class);

        String mEmail = binding.etEmailAddress.getText().toString();

        HashMap<String, Object> payloadLogin = new HashMap<String, Object>();

        payloadLogin.put("email", mEmail);

        Call<AlertSuccessResponse> call = mApiService.ForgotPassword(payloadLogin);

        call.enqueue(new Callback<AlertSuccessResponse>() {
            @Override
            public void onResponse(Call<AlertSuccessResponse> call, Response<AlertSuccessResponse> response) {

                if (binding.prgLoader != null) {
                    binding.btnForgotPassword.setVisibility(View.VISIBLE);
                    binding.prgLoader.setVisibility(View.GONE);
                }
                try {
                    if (response.body().getSuccess().equals("yes")) {

                        DialogAlertHelper.show_dialog_OK_response(ForgotPasswordActivity.this, response.body().getMessage(), new DialogClickListener() {
                            @Override
                            public void onOkPress() {

                                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                        Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelPress() {

                            }
                        });

                    } else if (response.body().getSuccess().equals("no")) {
                        showErrorDialog(ForgotPasswordActivity.this.getString(R.string.error) , response.body().getMessage());
                    }
                } catch (NullPointerException e) {
                   e.printStackTrace();
                    showErrorDialog(ForgotPasswordActivity.this.getString(R.string.error) ,  getResources().getString(R.string.some_thing_went_wrong));
                }
            }

            @Override
            public void onFailure(Call<AlertSuccessResponse> call, Throwable t) {
                if (binding.prgLoader != null) {
                    binding.btnForgotPassword.setVisibility(View.VISIBLE);
                    binding.prgLoader.setVisibility(View.GONE);
                }
                showErrorDialog(ForgotPasswordActivity.this.getString(R.string.error) , getResources().getString(R.string.some_thing_went_wrong));
            }
        });
    }

    // Check field validation
    boolean isValid() {
        boolean isValid = true;
        String message = "";
        String title = "";

        String email = binding.etEmailAddress.getText().toString();

        if(Utils.isStringNull(email)){
            isValid = false;
            message = getString(R.string.error_blank_validation_email_address);
            title = getString(R.string.error_required_email_address);
            binding.etEmailAddress.requestFocus();
        } else if(!Utils.isValidEmail(email)){
            isValid = false;
            message = getString(R.string.error_validation_valid_email_address);
            title = getString(R.string.error_required_valid_email_address);
            binding.etEmailAddress.requestFocus();
        }
        if(!isValid) {
            showErrorDialog(title, message);
        }
        return isValid;
    }

}
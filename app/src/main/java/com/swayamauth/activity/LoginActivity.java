package com.swayamauth.activity;

/**
 * Created by swayam infotech
 */

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.swayamauth.BaseActivity;
import com.swayamauth.MainActivity;
import com.swayamauth.R;
import com.swayamauth.Utils.Prefs;
import com.swayamauth.Utils.Utils;
import com.swayamauth.databinding.ActivityLoginBinding;
import com.swayamauth.helper.Constants;
import com.swayamauth.helper.DialogAlertHelper;
import com.swayamauth.listener.DialogClickListener;
import com.swayamauth.model.LoginResponse;
import com.swayamauth.network.ApiClient;
import com.swayamauth.network.ApiInterfaceService;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setClickListener();

        getSupportActionBar().hide();

        animatedChanges();

    }

    private void animatedChanges() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!Utils.isStringNull(Prefs.getString(LoginActivity.this, Constants.KEY_USER_ID, ""))) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    binding.llTextField.setVisibility(View.VISIBLE);
                    LayoutTransition lt = new LayoutTransition();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        lt.disableTransitionType(LayoutTransition.DISAPPEARING);
                    }
                    binding.llTextField.setLayoutTransition(lt);

                    binding.llTextField.animate()
                            .alpha(1.0f)
                            .setDuration(1500)
                            .setListener(null);
                }
            }
        }, 4000);
    }

    // Set click listener
    private void setClickListener() {
        binding.btnLogin.setOnClickListener(this);
        binding.btnForgotPassword.setOnClickListener(this);
        binding.btnSignUp.setOnClickListener(this);
    }

    // Handle Click event
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin :
                if(isValid()) {
                    if(Utils.isNetworkAvailable(LoginActivity.this)) {
                        loginAPI();
                    }
                }
                break;

            case R.id.btnForgotPassword :
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                break;

            case R.id.btnSignUp :
                Intent intentSignup = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intentSignup);
                break;
        }
    }

    // Call Login API
    private void loginAPI() {
        if(binding.prgLoader != null) {
            binding.btnLogin.setVisibility(View.GONE);
            binding.prgLoader.setVisibility(View.VISIBLE);
        }
        ApiInterfaceService mApiService = ApiClient.getClient().create(ApiInterfaceService.class);

        String mPassword = binding.etPassword.getText().toString();
        String mEmail = binding.etEmailAddress.getText().toString();

        HashMap<String, Object> payloadLogin = new HashMap<String, Object>();

        payloadLogin.put("email", mEmail);
        payloadLogin.put("password", mPassword);

        Call<LoginResponse> call = mApiService.Login(payloadLogin);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (binding.prgLoader != null)
                    binding.btnLogin.setVisibility(View.VISIBLE);
                    binding.prgLoader.setVisibility(View.GONE);
                try {
                    if (response.body().getSuccess().equals("yes") && response.body().getData() != null) {

                        DialogAlertHelper.show_dialog_OK_response(LoginActivity.this, response.body().getMessage(), new DialogClickListener() {
                            @Override
                            public void onOkPress() {

                                Prefs.putString(LoginActivity.this, Constants.KEY_USER_ID, response.body().getData().getId());
                                Prefs.putString(LoginActivity.this, Constants.KEY_FULL_NAME, response.body().getData().getFull_name());
                                Prefs.putString(LoginActivity.this, Constants.KEY_EMAIL, response.body().getData().getEmail());

                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
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
                        showErrorDialog(LoginActivity.this.getString(R.string.error), response.body().getMessage());
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    showErrorDialog(LoginActivity.this.getString(R.string.error) ,  getResources().getString(R.string.some_thing_went_wrong));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                binding.btnLogin.setVisibility(View.VISIBLE);
                if (binding.prgLoader != null)
                    binding.prgLoader.setVisibility(View.GONE);
                showErrorDialog(LoginActivity.this.getString(R.string.error),  getResources().getString(R.string.some_thing_went_wrong));
            }
        });
    }

    // Check all fields validation
    boolean isValid() {
        boolean isValid = true;
        String title = "";
        String message = "";

        String email = binding.etEmailAddress.getText().toString();
        String password = binding.etPassword.getText().toString();

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
        } else if(Utils.isStringNull(password)){
            isValid = false;
            message = getString(R.string.error_blank_validation_password);
            title = getString(R.string.error_required_password);
            binding.etPassword.requestFocus();
        }
        if(!isValid)
            showErrorDialog(title, message);
        return isValid;
    }
}
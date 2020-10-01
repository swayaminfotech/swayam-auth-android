package com.swayamauth.activity;

/**
 * Created by swayam infotech
 */

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.swayamauth.BaseActivity;
import com.swayamauth.MainActivity;
import com.swayamauth.R;
import com.swayamauth.Utils.Prefs;
import com.swayamauth.Utils.Utils;
import com.swayamauth.databinding.ActivitySignUpBinding;
import com.swayamauth.helper.Constants;
import com.swayamauth.helper.DialogAlertHelper;
import com.swayamauth.listener.DialogClickListener;
import com.swayamauth.model.RegisterResponse;
import com.swayamauth.network.ApiClient;
import com.swayamauth.network.ApiInterfaceService;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    ActivitySignUpBinding binding;
    private String profileImageFilePath;
    private Uri fileUri;
    public static final int REQUEST_CODE_CAPTURE_FROM_CAMERA = 475;
    public static final int REQUEST_CODE_SELECT_IMAGE_FROM_GALLERY = 476;

    public static final int REQUEST_CODE_CAMERA_PERMISSION = 485;
    public static final int REQUEST_CODE_GALLERY_PERMISSION = 486;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setClickListener();
        binding.header.tvTitle.setText(this.getString(R.string.sign_up));
    }

    // click listener
    private void setClickListener() {
        binding.btnSignUp.setOnClickListener(this);
        binding.flImageChoose.setOnClickListener(this);
        binding.ivImageChoose.setOnClickListener(this);
        binding.header.ivBack.setOnClickListener(this);
        binding.tvTermsOfUse.setOnClickListener(this);
    }

    // Handle click event
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignUp:

                if(isValid()) {
                    if(Utils.isNetworkAvailable(SignUpActivity.this)) {
                        registerAPI();
                    }
                }

                break;

            case R.id.flImageChoose:

                selectImage();

                break;

            case R.id.ivImageChoose:

                selectImage();

                break;

            case R.id.ivBack:

                finish();

                break;

            case R.id.tvTermsOfUse:

                Intent intent = new Intent(SignUpActivity.this, WebUrlViewActivity.class);
                intent.putExtra("url", Constants.TERMS_OF_USE_URL);
                intent.putExtra("title", SignUpActivity.this.getString(R.string.terms_use));
                startActivity(intent);

                break;
        }
    }

    // Request capture image from camera
    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = FileProvider.getUriForFile(this, "com.swayamauth.provider", Utils.getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, REQUEST_CODE_CAPTURE_FROM_CAMERA);
    }

    // Request choose image from gallery
    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE_FROM_GALLERY);
    }

    // Handle image selected from gallery and camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAPTURE_FROM_CAMERA) {
                if (fileUri != null) {

                    profileImageFilePath = new File(fileUri.getPath()).getAbsolutePath();

                    String extension = Utils.getMimeExtension(profileImageFilePath.replace(" ", ""));
                    switch (extension) {
                        case "jpeg":
                        case "jpg":
                        case "png":
                            RequestOptions options = new RequestOptions()
                                    .transform(new CircleCrop())
                                    .placeholder(R.drawable.baseline_account_circle_white)
                                    .error(R.drawable.baseline_account_circle_white);

                            Glide.with(SignUpActivity.this).load(fileUri).apply(options).into(binding.ivProfile);

                            break;
                        default:
                            showErrorDialog(SignUpActivity.this.getString(R.string.error) , getResources().getString(R.string.error_choose_image_format));
                            break;
                    }

                }
            } else if (requestCode == REQUEST_CODE_SELECT_IMAGE_FROM_GALLERY) {
                Uri selectedImage = data.getData();
                String filePath[] = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);

                if (c != null) {
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    profileImageFilePath = c.getString(columnIndex);

                    c.close();
                } else if (!Utils.isStringNull(selectedImage.toString())) {
                    String mPicturepath = selectedImage.toString();
                    this.profileImageFilePath = mPicturepath.replace("file://", "").replace("content://", "");
                }

                String extension = Utils.getMimeExtension(profileImageFilePath.replace(" ", ""));
                switch (extension) {
                    case "jpeg":
                    case "jpg":
                    case "png":
                        RequestOptions options = new RequestOptions()
                                .transform(new CircleCrop())
                                .placeholder(R.drawable.baseline_account_circle_white)
                                .error(R.drawable.baseline_account_circle_white);

                        Glide.with(SignUpActivity.this).load(Uri.fromFile(new File(profileImageFilePath))).apply(options).into(binding.ivProfile);

                        break;

                    default:
                        showErrorDialog(SignUpActivity.this.getString(R.string.error) , getResources().getString(R.string.error_choose_image_format));
                        break;
                }
            }
        }
    }

    // Select image dialog
    private void selectImage() {

        final CharSequence[] items = {getResources().getString(R.string.capture_image_from_camera),getResources().getString(R.string.select_image_from_gallery),getResources().getString(R.string.cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle(getResources().getString(R.string.dialog_name));

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals(getResources().getString(R.string.capture_image_from_camera))) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                    REQUEST_CODE_CAMERA_PERMISSION);
                            return;
                        } else {
                            captureImage();
                        }
                    } else {
                        captureImage();
                    }
                } else if (items[i].equals(getResources().getString(R.string.select_image_from_gallery))) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                    REQUEST_CODE_GALLERY_PERMISSION);

                            return;
                        } else {
                            pickImage();
                        }
                    } else {
                        pickImage();
                    }

                } else if (items[i].equals(getResources().getString(R.string.cancel))) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    // Handle camera and write and read external storage permission
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA_PERMISSION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    captureImage();
                }
                return;
            }
            case REQUEST_CODE_GALLERY_PERMISSION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImage();

                }
                return;
            }

        }
    }

    // Call register API
    private void registerAPI() {
        if(binding.prgLoader != null) {
            binding.btnSignUp.setVisibility(View.GONE);
            binding.prgLoader.setVisibility(View.VISIBLE);
        }
        ApiInterfaceService mApiService = ApiClient.getClient().create(ApiInterfaceService.class);

        String mPassword = binding.etPassword.getText().toString();
        String mFirstName = binding.etFirstName.getText().toString();
        String mLastName = binding.etLastName.getText().toString();
        String mEmail = binding.etEmailAddress.getText().toString();
        String mGender = "";
        if(binding.radMale.isChecked()) {
            mGender = "Male";
        } else if(binding.radFemale.isChecked()) {
            mGender = "Female";
        }
        String mAboutUs = binding.etAboutUs.getText().toString();

        MultipartBody.Part first_name = MultipartBody.Part.createFormData("full_name", mFirstName);
        MultipartBody.Part email = MultipartBody.Part.createFormData("email", mEmail);
        MultipartBody.Part about_us = MultipartBody.Part.createFormData("about_us", mAboutUs);
        MultipartBody.Part last_name = MultipartBody.Part.createFormData("last_name", mLastName);
        MultipartBody.Part gender = MultipartBody.Part.createFormData("gender", mGender);
        MultipartBody.Part password = MultipartBody.Part.createFormData("password", Utils.get_SHA_512_SecurePassword("", mPassword));
        MultipartBody.Part device_token = MultipartBody.Part.createFormData("device_token", "android");
        MultipartBody.Part device_type = MultipartBody.Part.createFormData("device_type", "0");
        MultipartBody.Part body = null;
        File profileImageFile = null;
        try {
            profileImageFile = new File(profileImageFilePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse(Utils.getMimeType(profileImageFilePath)), profileImageFile);
            body = MultipartBody.Part.createFormData("profile_picture", profileImageFile.getName(), requestFile);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        Call<RegisterResponse> call = mApiService.SignUp(first_name, last_name,  email, about_us, password, gender, (profileImageFile == null || !profileImageFile.exists()) ? null : body, device_token, device_type);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {

                if (binding.prgLoader != null)
                    binding.btnSignUp.setVisibility(View.VISIBLE);
                    binding.prgLoader.setVisibility(View.GONE);
                try {
                    if (response.body().getSuccess().equals("yes") && response.body().getData() != null) {

                        DialogAlertHelper.show_dialog_OK_response(SignUpActivity.this, response.body().getMessage(), new DialogClickListener() {
                            @Override
                            public void onOkPress() {

                                Prefs.putString(SignUpActivity.this, Constants.KEY_USER_ID, response.body().getData().getId());
                                Prefs.putString(SignUpActivity.this, Constants.KEY_FULL_NAME, response.body().getData().getFull_name());
                                Prefs.putString(SignUpActivity.this, Constants.KEY_EMAIL, response.body().getData().getEmail());

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
                        showErrorDialog(SignUpActivity.this.getString(R.string.error) , response.body().getMessage());
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    showErrorDialog(SignUpActivity.this.getString(R.string.error) ,  getResources().getString(R.string.some_thing_went_wrong));
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                if (binding.prgLoader != null)
                    binding.btnSignUp.setVisibility(View.VISIBLE);
                    binding.prgLoader.setVisibility(View.GONE);
                showErrorDialog(SignUpActivity.this.getString(R.string.error) ,  getResources().getString(R.string.some_thing_went_wrong));
            }
        });
    }

    // Check All fileds validation
    boolean isValid() {
        boolean isValid = true;
        String title = "";
        String message = "";

        String password = binding.etPassword.getText().toString();
        String confirmPassword = binding.etConfirmPassword.getText().toString();
        String email = binding.etEmailAddress.getText().toString();

        if(Utils.isStringNull(binding.etFirstName.getText().toString())) {
            isValid = false;
            message = getString(R.string.error_blank_validation_firstname);
            title = getString(R.string.error_required_firstname);
            binding.etFirstName.requestFocus();
        } else if(Utils.isStringNull(binding.etLastName.getText().toString())){
            isValid = false;
            message = getString(R.string.error_blank_validation_lastname);
            title = getString(R.string.error_required_lastname);
            binding.etLastName.requestFocus();
        } else if(Utils.isStringNull(email)){
            isValid = false;
            message = getString(R.string.error_blank_validation_email_address);
            title = getString(R.string.error_required_email_address);
            binding.etEmailAddress.requestFocus();
        } else if(!Utils.isValidEmail(email)){
            isValid = false;
            message = getString(R.string.error_validation_valid_email_address);
            title = getString(R.string.error_required_valid_email_address);
            binding.etEmailAddress.requestFocus();
        } else if(Utils.isStringNull(binding.etAboutUs.getText().toString())){
            isValid = false;
            message = getString(R.string.error_blank_validation_about_us);
            title = getString(R.string.error_required_about_us);
            binding.etAboutUs.requestFocus();
        } else if(Utils.isStringNull(password)){
            isValid = false;
            message = getString(R.string.error_blank_validation_password);
            title = getString(R.string.error_required_password);
            binding.etPassword.requestFocus();
        } else if(password.length() < 8){
            isValid = false;
            message = getString(R.string.error_validation_length_password);
            title = getString(R.string.error_required_password);
            binding.etPassword.requestFocus();
        } else if(Utils.isStringNull(confirmPassword)){
            isValid = false;
            message = getString(R.string.error_blank_validation_confirm_password);
            title = getString(R.string.error_required_confirm_password);
            binding.etConfirmPassword.requestFocus();
        }  else if(!confirmPassword.equals(password)){
            title = getString(R.string.error_required_confirm_password);
            isValid = false;
            message = getString(R.string.error_mismatch_password);
            binding.etConfirmPassword.requestFocus();
        } else if(binding.checkBox.isChecked()) {
            title = getString(R.string.error);
            isValid = false;
            message = getString(R.string.error_please_aggree_terms_and_conditions);
        } else if(Utils.isStringNull(profileImageFilePath)) {
            DialogAlertHelper.show_dialog_OK(SignUpActivity.this, SignUpActivity.this.getString(R.string.please_select_profile_image), "");
        }
        if(!isValid) {
            showErrorDialog(title, message);
        }

        return isValid;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

}
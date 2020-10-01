package com.swayamauth.fragment.editprofile;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.swayamauth.R;
import com.swayamauth.Utils.Utils;
import com.swayamauth.databinding.FragmentEditProfileBinding;
import com.swayamauth.fragment.BaseFragment;
import com.swayamauth.helper.DialogAlertHelper;
import com.swayamauth.listener.DialogClickListener;
import com.swayamauth.model.EditProfileResponse;
import com.swayamauth.model.UserProfileResponse;
import com.swayamauth.network.ApiClient;
import com.swayamauth.network.ApiInterfaceService;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends BaseFragment implements View.OnClickListener {

    FragmentEditProfileBinding binding;
    private EditProfileViewModel editProfileViewModel;
    private String profileImageFilePath;
    private Uri fileUri;
    public static final int REQUEST_CODE_CAPTURE_FROM_CAMERA = 475;
    public static final int REQUEST_CODE_SELECT_IMAGE_FROM_GALLERY = 476;

    public static final int REQUEST_CODE_CAMERA_PERMISSION = 485;
    public static final int REQUEST_CODE_GALLERY_PERMISSION = 486;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        editProfileViewModel =
                new ViewModelProvider(this).get(EditProfileViewModel.class);

        editProfileViewModel.setContext(getActivity());

        setClickListener();

        initData();

        return root;
    }

    private void initData() {
        binding.flLoader.flLoader.setVisibility(View.VISIBLE);
        editProfileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<UserProfileResponse>() {
            @Override
            public void onChanged(UserProfileResponse userProfileResponse) {
                binding.flLoader.flLoader.setVisibility(View.GONE);
                if(userProfileResponse != null && userProfileResponse.getSuccess() != null && userProfileResponse.getSuccess().equals("yes")) {
                    setData(userProfileResponse);
                } else {
                    if(userProfileResponse != null && !Utils.isStringNull(userProfileResponse.getMessage())) {
                        showErrorDialog(getActivity().getString(R.string.error), userProfileResponse.getMessage());
                    } else {
                        showErrorDialog(getActivity().getString(R.string.error), getActivity().getString(R.string.some_thing_went_wrong));
                    }
                }
            }
        });
    }

    // Set User details
    private void setData(UserProfileResponse profileResponse) {
        UserProfileResponse.UserProfile userProfile = profileResponse.getData();
        if(userProfile != null) {
            binding.etFirstName.setText(userProfile.getFirst_name());
            binding.etLastName.setText(userProfile.getLast_name());
            binding.etAboutUs.setText(userProfile.getAbout_me());
            binding.etEmailAddress.setText(userProfile.getEmail());

            if (!Utils.isStringNull(userProfile.getGender())) {
                if (userProfile.getGender().equalsIgnoreCase("male")) {
                    binding.radMale.setChecked(true);
                } else if (userProfile.getGender().equalsIgnoreCase("female")) {
                    binding.radFemale.setChecked(true);
                }
            }
            RequestOptions options = new RequestOptions()
                    .transform(new CircleCrop())
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder);
            Glide.with(getActivity()).load(userProfile.getProfile_image()).apply(options).into(binding.ivProfile);
        }
    }

    // set click listener
    private void setClickListener() {
        binding.btnEdit.setOnClickListener(this);
        binding.flImageChoose.setOnClickListener(this);
        binding.ivImageChoose.setOnClickListener(this);
    }

    // Handle click event
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEdit:
                if(isValid()) {
                    if(Utils.isNetworkAvailable(getActivity())) {
                        updateProfileAPI();
                    }
                }
                break;

            case R.id.flImageChoose:
                selectImage();
                break;

            case R.id.ivImageChoose:
                selectImage();
                break;
        }
    }

    // Request Capture image from camera
    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = FileProvider.getUriForFile(getActivity() , "com.swayamauth.provider", Utils.getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, REQUEST_CODE_CAPTURE_FROM_CAMERA);
    }

    // Request Choose image from gallery
    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE_FROM_GALLERY);
    }

    // Handle image of choose image from gallery and camera
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getActivity().RESULT_OK) {
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
                                    .placeholder(R.drawable.profile_placeholder)
                                    .error(R.drawable.profile_placeholder);

                            Glide.with(getActivity()).load(fileUri).apply(options).into(binding.ivProfile);

                            break;
                        default:
                            showErrorDialog(getActivity().getString(R.string.error) , getResources().getString(R.string.error_choose_image_format));
                            break;
                    }

                }
            } else if (requestCode == REQUEST_CODE_SELECT_IMAGE_FROM_GALLERY) {
                Uri selectedImage = data.getData();
                String filePath[] = {MediaStore.Images.Media.DATA};
                Cursor c = getActivity().getContentResolver().query(selectedImage, filePath, null, null, null);

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
                                .placeholder(R.drawable.profile_placeholder)
                                .error(R.drawable.profile_placeholder);

                        Glide.with(getActivity()).load(Uri.fromFile(new File(profileImageFilePath))).apply(options).into(binding.ivProfile);

                        break;

                    default:
                        showErrorDialog(getActivity().getString(R.string.error) , getResources().getString(R.string.error_choose_image_format));
                        break;
                }
            }
        }
    }

    // Choose image dialog open
    private void selectImage() {

        final CharSequence[] items = {getResources().getString(R.string.capture_image_from_camera),getResources().getString(R.string.select_image_from_gallery),getResources().getString(R.string.cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.dialog_name));

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals(getResources().getString(R.string.capture_image_from_camera))) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
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
                        if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
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

    // Handle permission request
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA_PERMISSION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    captureImage();

                } else {

                }
                return;
            }
            case REQUEST_CODE_GALLERY_PERMISSION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    pickImage();

                } else {

                }
                return;
            }

        }
    }

    // Update API Call
    private void updateProfileAPI() {
        if(binding.prgLoader != null) {
            binding.btnEdit.setVisibility(View.GONE);
            binding.prgLoader.setVisibility(View.VISIBLE);
        }
        ApiInterfaceService mApiService = ApiClient.getClient().create(ApiInterfaceService.class);

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
        MultipartBody.Part device_token = MultipartBody.Part.createFormData("device_token", "android");
        MultipartBody.Part device_type = MultipartBody.Part.createFormData("device_type", "0");
        MultipartBody.Part body = null;
        File profileImageFile = null;
        try {
            if(!Utils.isStringNull(profileImageFilePath)) {
                profileImageFile = new File(profileImageFilePath);
                RequestBody requestFile = RequestBody.create(MediaType.parse(Utils.getMimeType(profileImageFilePath)), profileImageFile);
                body = MultipartBody.Part.createFormData("profile_picture", profileImageFile.getName(), requestFile);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        Call<EditProfileResponse> call = mApiService.EditProfile(first_name, last_name,  email, about_us, gender, (profileImageFile == null || !profileImageFile.exists()) ? null : body, device_token, device_type);

        call.enqueue(new Callback<EditProfileResponse>() {
            @Override
            public void onResponse(Call<EditProfileResponse> call, Response<EditProfileResponse> response) {

                if (binding.prgLoader != null) {
                    binding.prgLoader.setVisibility(View.GONE);
                    binding.btnEdit.setVisibility(View.VISIBLE);
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
                        showErrorDialog(getActivity().getString(R.string.error) , response.body().getMessage());
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    showErrorDialog(getActivity().getString(R.string.error) ,  getResources().getString(R.string.some_thing_went_wrong));
                }
            }

            @Override
            public void onFailure(Call<EditProfileResponse> call, Throwable t) {
                if (binding.prgLoader != null) {
                    binding.prgLoader.setVisibility(View.GONE);
                    binding.btnEdit.setVisibility(View.VISIBLE);
                }
                showErrorDialog(getActivity().getString(R.string.error) ,  getResources().getString(R.string.some_thing_went_wrong));
            }
        });
    }

    // Check fields validation
    boolean isValid() {
        boolean isValid = true;
        String title = "";
        String message = "";

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
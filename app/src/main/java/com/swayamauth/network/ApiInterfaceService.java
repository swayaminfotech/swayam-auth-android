package com.swayamauth.network;

import com.swayamauth.model.AlertSuccessResponse;
import com.swayamauth.model.EditProfileResponse;
import com.swayamauth.model.LoginResponse;
import com.swayamauth.model.RegisterResponse;
import com.swayamauth.model.UserProfileResponse;

import java.util.HashMap;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterfaceService {

    @Multipart
    @POST("authentication/registration")
    Call<RegisterResponse> SignUp(@Part MultipartBody.Part first_name,
                                  @Part MultipartBody.Part last_name,
                                  @Part MultipartBody.Part email,
                                  @Part MultipartBody.Part about_us,
                                  @Part MultipartBody.Part password,
                                  @Part MultipartBody.Part gender,
                                  @Part MultipartBody.Part profile_picture,
                                  @Part MultipartBody.Part device_token,
                                  @Part MultipartBody.Part device_type);
    @Multipart
    @POST("authentication/update_profile")
    Call<EditProfileResponse> EditProfile(@Part MultipartBody.Part first_name,
                                          @Part MultipartBody.Part last_name,
                                          @Part MultipartBody.Part email,
                                          @Part MultipartBody.Part about_us,
                                          @Part MultipartBody.Part gender,
                                          @Part MultipartBody.Part profile_picture,
                                          @Part MultipartBody.Part device_token,
                                          @Part MultipartBody.Part device_type);

    @POST("authentication/login")
    Call<LoginResponse> Login(@Body HashMap<String, Object> payload);

    @POST("authentication/ForgotPassword")
    Call<AlertSuccessResponse> ForgotPassword(@Body HashMap<String, Object> payload);

    @POST("authentication/user_info")
    Call<UserProfileResponse> getUserDetails(@Body HashMap<String, Object> payload);

    @POST("authentication/changePassword")
    Call<AlertSuccessResponse> ChangePassword(@Body HashMap<String, Object> payload);
}

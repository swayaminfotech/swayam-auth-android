package com.swayamauth.model;

/**
 * Created by swayam infotech
 */

public class RegisterResponse {
    String success;
    String message;
    UserProfile data;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public UserProfile getData() {
        return data;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class UserProfile{
        String id;
        String first_name;
        String last_name;
        String full_name;
        String email;
        String about_me;
        String gender;
        String profile_image;

        public String getId() {
            return id;
        }

        public String getFirst_name() {
            return first_name;
        }

        public String getLast_name() {
            return last_name;
        }

        public String getFull_name() {
            return full_name;
        }

        public String getEmail() {
            return email;
        }

        public String getAbout_me() {
            return about_me;
        }

        public String getGender() {
            return gender;
        }

        public String getProfile_image() {
            return profile_image;
        }
    }

}

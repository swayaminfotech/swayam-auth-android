package com.swayamauth.Utils;

/**
 * Created by swayam infotech
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import com.swayamauth.R;
import com.swayamauth.helper.DialogAlertHelper;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    // Check Value is Null or Blank
    public static boolean isStringNull(String value) {
        Boolean result = true;
        if (value != null && !value.equals("null") && !value.equals("n/a") && !value.equals("N/A") && !value.equals("")
                && !value.equals("[ ]") && !value.equals("[]") && !value.equals("{}") && value != null) {
            result = false;
        }
        return result;
    }

    // Check Email is valid or not
    public static boolean isValidEmail(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Check internet connection is available or not
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if(!isConnected) {
            DialogAlertHelper.show_dialog_OK(context, context.getString(R.string.internet_connection_not_available), "");
        }
        return isConnected;
    }

    // get mime type from file path
    public static String getMimeType(String filePath) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
        if (extension == null) {
            if (filePath.lastIndexOf(".") != -1)
                extension = filePath.substring(filePath.lastIndexOf(".") + 1);
        }
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    // get output file
    public static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "caches"); //NON-NLS
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            return null;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()); //NON-NLS
        return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg"); //NON-NLS NON-NLS
    }

    //get mime extension from file path
    public static String getMimeExtension(String filepath) {

        String tempUrl = filepath.replaceAll("[^a-zA-Z0-9\\.\\/]", "");
        String extension = MimeTypeMap.getFileExtensionFromUrl(tempUrl);

        if (extension == null) {
            if (filepath.lastIndexOf(".") != -1)
                extension = filepath.substring(filepath.lastIndexOf(".") + 1);
        }

        return extension;
    }

    // get SHA-512 converted Password
    public static String get_SHA_512_SecurePassword(String passwordToHash, String salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

}

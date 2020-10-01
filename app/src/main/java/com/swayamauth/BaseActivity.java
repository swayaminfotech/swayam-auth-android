package com.swayamauth;

/**
 * Created by swayam infotech
 */

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            getCurrentFocus().clearFocus();
        }
        return super.dispatchTouchEvent(ev);
    }

    public void showErrorDialog(String errorTitle, String errorMessage) {
        final Dialog dialog = new Dialog(BaseActivity.this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_error_message, null);
        ((TextView) view.findViewById(R.id.tvErrorTitle)).setText(errorTitle);
        ((TextView) view.findViewById(R.id.tvErrorLabel)).setText(errorMessage);
        dialog.setContentView(view);
        dialog.show();
        WindowManager.LayoutParams windowParams = dialog.getWindow().getAttributes();
        windowParams.dimAmount = 0.00f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        Window windowView = dialog.getWindow();
        windowView.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        windowParams.gravity = Gravity.TOP | Gravity.LEFT;
        windowParams.y = 0;
        dialog.getWindow().setAttributes(windowParams);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 2000);
    }

}

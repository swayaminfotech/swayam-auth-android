package com.swayamauth.fragment;

/**
 * Created by swayam infotech
 */

import android.app.Dialog;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.swayamauth.R;

public class BaseFragment extends Fragment {

    // Error dialog show
    public void showErrorDialog(String errorTitle, String errorMessage) {
        final Dialog dialog = new Dialog(getActivity(), R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_error_message, null);

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
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }, 2000);
    }

}

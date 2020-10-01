package com.swayamauth.helper;

/**
 * Created by swayam infotech
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.swayamauth.listener.DialogClickListener;

public class DialogAlertHelper {

    public static void show_dialog_OK(final Context context, final String message, final String title) {
        AlertDialog.Builder alerDialog = new AlertDialog.Builder(context)
                .setNegativeButton(context.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setMessage(message);

        AlertDialog alert = alerDialog.create();
        alert.show();
    }

    public static void show_dialog_OK_response(final Context context, final String message, final DialogClickListener dialogClickListener) {

        AlertDialog.Builder alerDialog = new AlertDialog.Builder(context)
                .setCancelable(false)
                .setNegativeButton(context.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogClickListener.onOkPress();
                        dialog.dismiss();
                    }
                })
                .setMessage(message);

        AlertDialog alert = alerDialog.create();
        alert.show();

    }
}

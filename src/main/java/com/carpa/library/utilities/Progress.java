package com.carpa.library.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.carpa.library.R;


public class Progress {
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private ProgressDialog progressDialog;
    private Context context;
    private boolean cancelable;
    private boolean cancelableOnOut;

    public Progress(Context context, boolean cancelable, boolean cancelableOnOut) {
        this.context = context;
        this.cancelable = cancelable;
        this.cancelableOnOut = cancelableOnOut;
    }

    public void show(String message) {
        try {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(false);
            progressDialog.setCanceledOnTouchOutside(cancelableOnOut);
            progressDialog.setCancelable(cancelable);
            progressDialog.setMessage(message != null ? message : "NO_MESSAGE");
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(String message) {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.setMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

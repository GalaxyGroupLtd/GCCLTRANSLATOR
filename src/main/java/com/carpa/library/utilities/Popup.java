package com.carpa.library.utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.carpa.library.R;


public class Popup {
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private Context context;

    public Popup(Context context) {
        this.context = context;
    }

    public void show(String title, String message) {
        try {
            builder = new AlertDialog.Builder(context, R.style.SimpleBlackDialog);
            builder.setMessage(message != null ? message : "NO_MESSAGE")
                    .setTitle(title);
            builder.setIcon(R.mipmap.ic_launcher);
            // Add the buttons
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show(String title, String message, final String[] actions, final OnPopAction listener) {
        try {
            if (actions.length <= 0)
                return;

            builder = new AlertDialog.Builder(context, R.style.SimpleBlackDialog);
            builder.setMessage(message != null ? message : "NO_MESSAGE")
                    .setTitle(title);
            builder.setIcon(R.mipmap.ic_launcher);

            builder.setPositiveButton(actions[0], new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    listener.popAction(true, actions[0]);
                    dialog.dismiss();
                }
            });
            for (final String action : actions) {
                builder.setNeutralButton(action, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.popAction(true, action);
                        dialog.dismiss();
                    }
                });
            }
            dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        try {
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnPopAction {
        void popAction(boolean isAction, String action);
    }
}

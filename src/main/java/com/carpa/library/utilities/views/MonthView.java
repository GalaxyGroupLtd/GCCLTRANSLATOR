package com.carpa.library.utilities.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.carpa.library.R;
import com.carpa.library.models.GeneralListModel;
import com.carpa.library.utilities.adapter.GeneralAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 12/25/2017.
 */

public class MonthView implements GeneralAdapter.OnGeneralAdapter {
    private OnMonthView mListener;
    private Context context;
    private GeneralAdapter generalAdapter;
    private Dialog dialog;
    private List<GeneralListModel> monthsList;

    public MonthView(OnMonthView mListener, Context context, List<GeneralListModel> monthsList) {
        this.mListener = mListener;
        this.context = context;
        this.monthsList = monthsList;
    }

    public void dimissYearBox() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    public void showYear() throws Exception {
        try {
            dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setContentView(R.layout.custom_box);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    dialogInterface.dismiss();
                    if (mListener != null)
                        mListener.onMonthView(false, false, null, null);
                }
            });

            Resources res = context.getResources();
            Drawable background = res.getDrawable(R.drawable.bck_popup);
            RelativeLayout layout = dialog.findViewById(R.id.custBoxHolder);
            background.setAlpha(120);
            layout.setBackground(background);

            final TextView boxTitle = (TextView) dialog.findViewById(R.id.dialogTitle);
            ImageView close = (ImageView) dialog.findViewById(R.id.icClose);
            LinearLayout boxContent = (LinearLayout) dialog.findViewById(R.id.dialogContent);

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    mListener.onMonthView(false, false, view, null);
                }
            });
            boxTitle.setText("Months");
            generalAdapter = new GeneralAdapter(MonthView.this, monthsList);

            //setting box content
            RecyclerView recyclerView = new RecyclerView(context);
            recyclerView.setClipToPadding(false);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 3);//new LinearLayoutManager(getContext());//new GridLayoutManager(getContext(), 2);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(generalAdapter);

            boxContent.addView(recyclerView);

            dialog.show();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public void onGeneralAdapter(boolean isClicked, boolean isOverFlow, View view, Object object) {
        mListener.onMonthView(isClicked, isOverFlow, view, object);
    }

    public interface OnMonthView {
        void onMonthView(boolean isClicked, boolean isOverFlow, View view, Object object);
    }
}

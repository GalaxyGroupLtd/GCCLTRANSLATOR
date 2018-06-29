package com.carpa.library.utilities.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carpa.library.R;
import com.carpa.library.utilities.Popup;
import com.carpa.library.utilities.UtilModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {
    private OnMainAdapater mListener;
    private Context context;
    private List<Object> mContent;
    private List<Object> tempList;

    public MainAdapter(OnMainAdapater mListener, Context context, List<Object> mContent) {
        this.mListener = mListener;
        this.context = context;
        this.mContent = mContent;
        this.tempList = new ArrayList<>();
        this.tempList.addAll(mContent);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.style_main, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Object mContent = this.mContent.get(position);
        final UtilModel utilModel = (UtilModel) mContent;
        holder.label.setText(utilModel.display());

        holder.label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onMainAdapater(true, holder.label, mContent);
            }
        });

        holder.overFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Popup popup = new Popup(context);
                popup.show("Details", utilModel.details());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContent.size();
    }

    public void refreshAdapter(List<Object> mContent) {
        this.mContent.clear();
        this.tempList.clear();
        this.mContent.addAll(mContent);
        this.tempList.addAll(mContent);
        notifyDataSetChanged();
    }

    public void filter(String charText) {
        try {
            mContent.clear();
            if (charText.trim().length() == 0) {
                mContent.addAll(tempList);
            } else {
                charText = charText.toLowerCase(Locale.getDefault());
                for (Object generalListModel : tempList) {
                    if (generalListModel.toString().toLowerCase(Locale.getDefault()).contains(charText)) {
                        mContent.add(generalListModel);
                    }
                }
            }
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnMainAdapater {
        void onMainAdapater(boolean isClicked, View view, Object object);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CardView holder;
        TextView label;
        ImageView overFlow;

        MyViewHolder(View view) {
            super(view);
            overFlow = view.findViewById(R.id.overflow);
            holder = view.findViewById(R.id.holder);
            label = view.findViewById(R.id.label);
        }
    }
}

package com.carpa.library.utilities.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carpa.library.R;
import com.carpa.library.models.GeneralListModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 12/11/2017.
 */

public class GeneralAdapter extends RecyclerView.Adapter<GeneralAdapter.MyViewHolder> {
    private OnGeneralAdapter mListener;
    private List<GeneralListModel> mContent;
    private List<GeneralListModel> tempList;
    private boolean addedList = false;

    public GeneralAdapter(OnGeneralAdapter mListener, List<GeneralListModel> mContent) {
        this.mListener = mListener;
        this.mContent = mContent;
        this.tempList = new ArrayList<>();
        this.tempList.addAll(mContent);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.style_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final GeneralListModel mContent = this.mContent.get(position);

        holder.logo.setText(mContent.getLogo());
        holder.title.setText("M: "+mContent.getTitle());
        if(!mContent.isOverFlow())
            holder.overFlow.setVisibility(View.GONE);
        holder.holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onGeneralAdapter(true, mContent.isOverFlow(), holder.overFlow, mContent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContent.size();
    }

    public void refreshAdapter(List<GeneralListModel> mContent) {
        this.mContent.clear();
        this.mContent.addAll(mContent);
        this.tempList.addAll(mContent);
        notifyDataSetChanged();
    }

    public void filter(String charText) {
        try {
            mContent.clear();
            if (charText.trim().length() == 0) {
                mContent.addAll(tempList);
            }else{
                charText = charText.toLowerCase(Locale.getDefault());
                for (GeneralListModel generalListModel : tempList) {
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

    class MyViewHolder extends RecyclerView.ViewHolder {
        CardView holder;
        TextView logo;
        TextView title;
        ImageView overFlow;

        MyViewHolder(View view) {
            super(view);
            holder = view.findViewById(R.id.holder);
            logo = view.findViewById(R.id.mainLogo);
            title = view.findViewById(R.id.title);
            overFlow = view.findViewById(R.id.overflow);
        }
    }
    public interface OnGeneralAdapter {
        void onGeneralAdapter(boolean isClicked, boolean isOverFlow, View view, Object object);
    }
}

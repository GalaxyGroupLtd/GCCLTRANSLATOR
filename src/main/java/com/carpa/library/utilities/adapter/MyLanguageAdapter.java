package com.carpa.library.utilities.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carpa.library.R;
import com.carpa.library.entities.Languages;
import com.carpa.library.models.MyLanguageModel;
import com.carpa.library.utilities.Popup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyLanguageAdapter extends RecyclerView.Adapter<MyLanguageAdapter.MyViewHolder> {
    private OnMyLanguage mListener;
    private Context context;
    private List<MyLanguageModel> mContent;
    private List<MyLanguageModel> tempList;
    private Popup popup;

    public MyLanguageAdapter(OnMyLanguage mListener, Context context, List<MyLanguageModel> mContent) {
        this.mListener = mListener;
        this.context = context;
        this.mContent = mContent;
        this.tempList = new ArrayList<>();
        this.tempList.addAll(mContent);
        popup = new Popup(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.style_languages, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final MyLanguageModel mContent = this.mContent.get(position);
        String numMessage = mContent.getMessages();
        holder.title.setText("Messages: "+(numMessage.length() > 4 ? "+999" : numMessage.length() > 3 ? "+99" : numMessage));
        holder.title.setTextSize(15);

        holder.logo.setText(mContent.getLanCode() +"\n"+mContent.lanName());
        holder.logo.setTextSize(18);
        holder.logo.setTypeface(holder.logo.getTypeface(), Typeface.BOLD);

        holder.logo.setOnClickListener(v -> mListener.onMyLanguage(true, holder.logo, mContent));

        holder.overFlow.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mContent.size();
    }

    public void refreshAdapter(List<MyLanguageModel> mContent) {
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
                for (MyLanguageModel generalListModel : tempList) {
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

    public interface OnMyLanguage {
        void onMyLanguage(boolean isClicked, View view, MyLanguageModel language);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CardView holder;
        TextView logo;
        TextView title;
        ImageView overFlow;

        MyViewHolder(View view) {
            super(view);
            overFlow = view.findViewById(R.id.overflow);
            title = view.findViewById(R.id.title);
            logo = view.findViewById(R.id.mainLogo);
            holder = view.findViewById(R.id.holder);
        }
    }
}

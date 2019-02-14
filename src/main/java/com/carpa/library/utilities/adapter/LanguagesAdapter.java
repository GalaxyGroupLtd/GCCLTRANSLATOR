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
import com.carpa.library.entities.Languages;
import com.carpa.library.utilities.Popup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LanguagesAdapter extends RecyclerView.Adapter<LanguagesAdapter.MyViewHolder> {
    private OnLanguageAdapter mListener;
    private Context context;
    private List<Languages> mContent;
    private List<Languages> tempList;
    private Popup popup;

    public LanguagesAdapter(OnLanguageAdapter mListener, Context context, List<Languages> mContent) {
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
        final Languages mContent = this.mContent.get(position);
        holder.title.setText(mContent.display());
        holder.logo.setText(mContent.display().substring(0, 3));

        holder.logo.setOnClickListener(v -> mListener.onLanguageAdapter(true, holder.logo, mContent));

        holder.overFlow.setOnClickListener(v -> popup.show("Details", mContent.details()));
    }

    @Override
    public int getItemCount() {
        return mContent.size();
    }

    public void refreshAdapter(List<Languages> mContent) {
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
                for (Languages generalListModel : tempList) {
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

    public interface OnLanguageAdapter {
        void onLanguageAdapter(boolean isClicked, View view, Languages languages);
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

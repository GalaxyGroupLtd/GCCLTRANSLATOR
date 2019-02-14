package com.carpa.library.utilities.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.carpa.library.R;
import com.carpa.library.entities.Languages;
import com.carpa.library.entities.facade.LanguageFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddLanguageAdapter extends RecyclerView.Adapter<AddLanguageAdapter.MyViewHolder> {
    private OnAddLanguage mListener;
    private Context context;
    private List<Languages> mContent;
    private List<Languages> tempList;

    public AddLanguageAdapter(OnAddLanguage mListener, Context context, List<Languages> mContent) {
        this.mListener = mListener;
        this.context = context;
        this.mContent = mContent;
        this.tempList = new ArrayList<>();
        this.tempList.addAll(mContent);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.style_add_language, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Languages mContent = this.mContent.get(position);
        holder.language.setText(mContent.display());
        try {
            Languages languages = LanguageFacade.findLanguage(mContent.getLanguageName());
            if (languages != null && languages.isMain()) {
                holder.checkBox.setChecked(true);
                mListener.onAddLanguage(true, holder.checkBox, languages);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton v, boolean isChecked) {
                if (!isChecked) {
                    mListener.onAddLanguage(false, v, mContent);
                } else {
                    mListener.onAddLanguage(true, v, mContent);
                }
            }
        });

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

    public interface OnAddLanguage {
        void onAddLanguage(boolean isAdd, View view, Languages language);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CardView holder;
        TextView language;
        CheckBox checkBox;

        MyViewHolder(View view) {
            super(view);
            checkBox = view.findViewById(R.id.checkBox);
            holder = view.findViewById(R.id.holder);
            language = view.findViewById(R.id.language);
        }
    }
}

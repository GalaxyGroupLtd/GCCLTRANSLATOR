package com.carpa.library.utilities.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carpa.library.R;
import com.carpa.library.config.ExtraConfig;
import com.carpa.library.entities.Messages;
import com.carpa.library.utilities.MessageNameFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.MyViewHolder> {
    private OnFavoritesAdapter mListener;
    private Context context;
    private List<Object> mMessageContent;
    private List<Object> tempList;

    public FavoriteAdapter(OnFavoritesAdapter mListener, Context context, List<Object> mMessageContent) {
        this.mListener = mListener;
        this.context = context;
        this.mMessageContent = mMessageContent;
        this.tempList = new ArrayList<>();
        this.tempList.addAll(mMessageContent);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.style_message, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Messages mContent = (Messages) this.mMessageContent.get(position);
        holder.title.setText(mContent.display());
        holder.caption.setText(MessageNameFactory.messageDate(mContent.getMessageName()));

        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFavoriteAdapter(true, holder.title, mContent, ExtraConfig.MESSAGE_PLAY);
            }
        });

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFavoriteAdapter(true, holder.title, mContent, ExtraConfig.MESSAGE_PLAY);
            }
        });
        holder.caption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFavoriteAdapter(true, holder.title, mContent, ExtraConfig.MESSAGE_PLAY);
            }
        });
        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildMenu(v, mContent);
            }
        });
    }

    private void buildMenu(View view, Object object) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.favorite_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(object));
        popup.show();
    }

    @Override
    public int getItemCount() {
        return mMessageContent.size();
    }

    public void refreshAdapter(List<Object> mContent) {
        this.mMessageContent.clear();
        this.tempList.clear();
        this.mMessageContent.addAll(mContent);
        this.tempList.addAll(mContent);
        notifyDataSetChanged();
    }

    public void filter(String charText) {
        try {
            mMessageContent.clear();
            if (charText.trim().length() == 0) {
                mMessageContent.addAll(tempList);
            } else {
                charText = charText.toLowerCase(Locale.getDefault());
                for (Object generalListModel : tempList) {
                    if (generalListModel.toString().toLowerCase(Locale.getDefault()).contains(charText)) {
                        mMessageContent.add(generalListModel);
                    }
                }
            }
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeItem(Object object) {
        if (mMessageContent.contains(object))
            mMessageContent.remove(object);
        if (tempList.contains(object))
            tempList.remove(object);

        notifyDataSetChanged();
    }

    public interface OnFavoritesAdapter {
        void onFavoriteAdapter(boolean isClicked, View view, Object object, String action);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CardView holder;
        TextView title, caption;
        ImageView icon, overflow;

        MyViewHolder(View view) {
            super(view);
            icon = view.findViewById(R.id.icon);
            holder = view.findViewById(R.id.holder);
            title = view.findViewById(R.id.title);
            caption = view.findViewById(R.id.caption);
            overflow = view.findViewById(R.id.overflow);
        }
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        Object object;

        MyMenuItemClickListener(Object object) {
            this.object = object;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.favorite:
                    mListener.onFavoriteAdapter(true, null, object, ExtraConfig.MESSAGE_FAVORITE);
                    return true;
                case R.id.play:
                    mListener.onFavoriteAdapter(true, null, object, ExtraConfig.MESSAGE_PLAY);
                    return true;
                case R.id.info:
                    mListener.onFavoriteAdapter(true, null, object, ExtraConfig.MESSAGE_INFO);
                    return true;
                default:
            }
            return false;
        }
    }
}

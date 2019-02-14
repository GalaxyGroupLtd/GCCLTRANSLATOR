package com.carpa.library.utilities.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.carpa.library.R;
import com.carpa.library.config.ExtraConfig;
import com.carpa.library.entities.Messages;
import com.carpa.library.entities.facade.MessagesFacade;
import com.carpa.library.utilities.DataFactory;
import com.carpa.library.utilities.DirManager;
import com.carpa.library.utilities.DownloadUtil;
import com.carpa.library.utilities.MessageNameFactory;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.Status;
import com.downloader.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CloudMessageAdapter extends RecyclerView.Adapter<CloudMessageAdapter.MyViewHolder> {
    private OnCloudMessageAdapter mListener;
    private Context context;
    private List<Messages> mMessageContent;
    private List<Messages> tempList;

    public CloudMessageAdapter(OnCloudMessageAdapter mListener, Context context, List<Messages> mMessageContent) {
        this.mListener = mListener;
        this.context = context;
        this.mMessageContent = mMessageContent;
        this.tempList = new ArrayList<>();
        this.tempList.addAll(mMessageContent);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.style_cloud_message, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Messages mContent = this.mMessageContent.get(position);
        holder.downloadHolder.setVisibility(View.GONE);
        holder.title.setText(mContent.display());
        String messageDate = DataFactory.formatDate(mContent.getMessageDate());
        holder.caption.setText(messageDate);

        if (mContent.getExtension().equalsIgnoreCase("mp3")) {
            holder.type.setImageResource(R.drawable.ic_audiotrack);
        } else {
            holder.type.setImageResource(R.drawable.ic_file);
        }

        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCloudMessageAdapter(true, holder.title, mContent, ExtraConfig.MESSAGE_STREAM);
            }
        });

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mListener.onCloudMessageAdapter(true, holder.title, mContent, ExtraConfig.MESSAGE_STREAM);
            }
        });
        holder.caption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mListener.onCloudMessageAdapter(true, holder.title, mContent, ExtraConfig.MESSAGE_STREAM);
            }
        });
        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildMenu(v, holder.holder, mContent);
            }
        });
    }

    private void buildMenu(View popContext, View holdContext, Messages messages) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, popContext);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.cloud_message_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(messages, holdContext));
        popup.show();
    }

    @Override
    public int getItemCount() {
        return mMessageContent.size();
    }

    public void refreshAdapter(List<Messages> mContent) {
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
                for (Messages generalListModel : tempList) {
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

    public void removeItem(Messages object) {
        if (mMessageContent.contains(object))
            mMessageContent.remove(object);
        if (tempList.contains(object))
            tempList.remove(object);

        notifyDataSetChanged();
    }

    public interface OnCloudMessageAdapter {
        void onCloudMessageAdapter(boolean isClicked, View view, Object object, String action);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CardView holder;
        TextView title, caption;
        ImageView icon, overflow, type;
        //download progress
        RelativeLayout downloadHolder;
        ProgressBar progress;
        TextView textViewProgress, cancel;

        MyViewHolder(View view) {
            super(view);
            icon = view.findViewById(R.id.icon);
            holder = view.findViewById(R.id.holder);
            title = view.findViewById(R.id.title);
            caption = view.findViewById(R.id.caption);
            overflow = view.findViewById(R.id.overflow);
            type = view.findViewById(R.id.type);

            downloadHolder = view.findViewById(R.id.downloadHolder);
            progress = view.findViewById(R.id.progress);
            textViewProgress = view.findViewById(R.id.textViewProgress);
            cancel = view.findViewById(R.id.cancel);

        }
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener, DownloadUtil.OnDownloadUtil {
        Messages messages;
        View view;
        int downloadId;
        RelativeLayout downloadHolder;
        ProgressBar progressBar;
        TextView textViewProgress, cancel;

        MyMenuItemClickListener(Messages messages, View view) {
            this.messages = messages;
            this.view = view;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.download:
                    if (!DirManager.isFileExist(messages.getFileName()))
                        download();
                    else {
                        //check if the file exist in the database
                        try {
                            Messages mMessage = MessagesFacade.getFileName(messages.getFileName());
                            if (mMessage == null) {
                                messages.setDownloadId(Utils.getUniqueId(messages.getPath(), DirManager.getRoot(), messages.getFileName()) + "");
                                messages.setNew(true);
                                messages.setDownload(true);
                                messages.save();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                    return true;
                case R.id.stream:
                    List<Messages> mMessage = new ArrayList<>();
                    for (Messages message : mMessageContent) {
                        if (message.getMessageName().equals(messages.getMessageName())) {
                            mMessage.add(message);
                        }
                    }
                    try {
                        mListener.onCloudMessageAdapter(true, null, new Messages().deSerialize(mMessage), ExtraConfig.MESSAGE_STREAM);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                case R.id.info:
                    mListener.onCloudMessageAdapter(true, null, messages, ExtraConfig.MESSAGE_INFO);
                    return true;
                default:
            }
            return false;
        }

        public void download() {
            downloadHolder = view.findViewById(R.id.downloadHolder);
            progressBar = view.findViewById(R.id.progress);
            textViewProgress = view.findViewById(R.id.textViewProgress);
            cancel = view.findViewById(R.id.cancel);
            downloadHolder.setVisibility(View.VISIBLE);
            cancel.setOnClickListener(v -> {
                //cancel the download
                if (Status.RUNNING == PRDownloader.getStatus(downloadId)) {
                    PRDownloader.cancel(downloadId);
                }
            });

            if (Status.RUNNING == PRDownloader.getStatus(downloadId)) {
                cancel.setCompoundDrawables(null, ContextCompat.getDrawable(context, R.drawable.ic_close), null, null);
                cancel.setText("Cancel");
                return;
            }

            DownloadUtil downloadUtil = new DownloadUtil(messages.getPath(), DirManager.getRoot(), messages.getFileName(), MyMenuItemClickListener.this);
            downloadId = downloadUtil.startDownload();
        }

        @Override
        public void onDownloadSuccess(int downloadId) {
            messages.setDownloadId(downloadId + "");
            messages.setNew(true);
            messages.setDownload(true);
            messages.save();
            Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show();
            if (downloadHolder != null)
                downloadHolder.setVisibility(View.GONE);
        }

        @Override
        public void onDownloadCanceled(int downloadId) {
            downloadId = 0;
            if (progressBar != null && downloadHolder != null) {
                progressBar.setIndeterminate(false);
                downloadHolder.setVisibility(View.GONE);
            }
        }

        @Override
        public void onProgress(Progress progress) {
            long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
            if (progressBar != null && textViewProgress != null) {
                progressBar.setProgress((int) progressPercent);
                textViewProgress.setText(DirManager.getProgressDisplayLine(progress.currentBytes, progress.totalBytes));
                progressBar.setIndeterminate(false);
            }
        }

        @Override
        public void onDownloadFaillure(int downloadId, Object cause) {
            Toast.makeText(context, "Download Failed", Toast.LENGTH_SHORT).show();
            if (downloadHolder != null)
                downloadHolder.setVisibility(View.GONE);
        }
    }
}

package com.carpa.library.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.carpa.library.R;
import com.carpa.library.entities.Messages;
import com.carpa.library.utilities.DirManager;
import com.carpa.library.utilities.Popup;
import com.carpa.library.utilities.Progress;

import java.io.IOException;
import java.util.List;

import im.delight.android.webview.AdvancedWebView;
import wseemann.media.FFmpegMediaPlayer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnStreamFrag} interface
 * to handle interaction events.
 * Use the {@link StreamFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StreamFrag extends Fragment implements FFmpegMediaPlayer.OnPreparedListener,
        FFmpegMediaPlayer.OnErrorListener {
    private static final String ARG_FILES = "files";
    private String mFilesParam;
    private OnStreamFrag mListener;

    private List<Messages> mMessages;
    private FloatingActionButton fab;
    private TextView tittle;
    private AdvancedWebView webview;
    private Messages pdfMessage;
    private Messages audioMessage;
    private boolean isPlaying;
    private FFmpegMediaPlayer mMediaPlayer = new FFmpegMediaPlayer();
    private Popup popup;
    private Progress progress;

    public StreamFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param files Parameter 1.
     * @return A new instance of fragment StreamFrag.
     */
    public static StreamFrag newInstance(String files) {
        StreamFrag fragment = new StreamFrag();
        Bundle args = new Bundle();
        args.putString(ARG_FILES, files);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFilesParam = getArguments().getString(ARG_FILES);
        }
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.stream_frag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        popup = new Popup(getContext());
        progress = new Progress(getContext(), false, false);

        tittle = view.findViewById(R.id.title);
        fab = view.findViewById(R.id.playPause);
        webview = view.findViewById(R.id.webview);

        fab.setImageResource(R.drawable.ic_hourglass);
        try {
            mMessages = new Messages().serializeList(mFilesParam);
        } catch (Exception e) {
            e.printStackTrace();
            popup.show("Oops!", "Something went wrong while parsing streaming data.");
            return;
        }
        if (mMessages.isEmpty()) {
            popup.show("Oops!", "There is no content to stream.");
            return;
        }

        for (Messages content : mMessages) {
            if (DirManager.isFileExist(content.getFileName()))
                if (content.getFileName().endsWith(".pdf")) {
                    pdfMessage = content;
                } else if (content.getFileName().endsWith(".mp3") ||
                        content.getFileName().endsWith(".aac") ||
                        content.getFileName().endsWith(".aac+") ||
                        content.getFileName().endsWith(".avi") ||
                        content.getFileName().endsWith(".flac") ||
                        content.getFileName().endsWith(".mp2") ||
                        content.getFileName().endsWith(".mp4") ||
                        content.getFileName().endsWith(".ogg") ||
                        content.getFileName().endsWith(".3gp")) {
                    audioMessage = content;
                }
        }

        if (pdfMessage != null) {
            displayPDF();
        } else {
            tittle.setText("No booklet found");
        }

        if (audioMessage != null) {
            fab.setImageResource(R.drawable.ic_pause);
            playAudio();
            fab.setOnClickListener(v -> {
                if (isPlaying && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    fab.setImageResource(R.drawable.ic_play);
                    isPlaying = false;
                } else if (!isPlaying && !mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
                    fab.setImageResource(R.drawable.ic_pause);
                    isPlaying = true;
                }
            });
        } else {
            Toast.makeText(getContext(), "No audio found", Toast.LENGTH_SHORT).show();
        }

        if (pdfMessage == null && audioMessage == null) {
            popup.show("Notification", "Sorry, there is no content to show.");
            return;
        }

        playPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStreamFrag) {
            mListener = (OnStreamFrag) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnStreamFrag");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
    }

    private void displayPDF() {
        tittle.setText(pdfMessage.getMessageName());

        String googleDocs = "http://docs.google.com/gview?embedded=true&url=";
        //String urlPath = DirServices.BASE_URL + DirServices.GET_FILE + "?path=" + fileModel.getPath();
        WebSettings settings = webview.getSettings();
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //settings.setJavaScriptEnabled(true);
        //settings.setAllowFileAccessFromFileURLs(true);
        //settings.setAllowUniversalAccessFromFileURLs(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            settings.setPluginState(WebSettings.PluginState.ON);
        }
        settings.setBuiltInZoomControls(true);
        webview.setWebChromeClient(new WebChromeClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getContext(), description, Toast.LENGTH_SHORT).show();
            }
        });
        pdfMessage.setPath(pdfMessage.getPath().replaceAll(" ", "%20"));
        webview.loadUrl(googleDocs + pdfMessage.getPath());
    }

    private void playAudio() {
        boolean isError = false;
        try {
            //Uri uri = Uri.parse(DirManager.filePath(audioMessage.getFileName()).getAbsolutePath());
            if (mMediaPlayer != null) {
                audioMessage.setPath(audioMessage.getPath().replaceAll(" ", "%20"));

                mMediaPlayer.setDataSource(audioMessage.getPath());
                mMediaPlayer.prepareAsync();//prepareAsync
                mMediaPlayer.setOnPreparedListener(StreamFrag.this);
                mMediaPlayer.setOnErrorListener(StreamFrag.this);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            isError = true;
        } catch (SecurityException e) {
            e.printStackTrace();
            isError = true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            isError = true;
        } catch (IOException e) {
            e.printStackTrace();
            isError = true;
        }
        if (isError && mMediaPlayer == null) {
            popup.show("Notification", "There was some error while trying to load the audio.");
        }
    }

    private void playPause() {
        if (fab != null) {
            if (mMediaPlayer != null)
                if (mMediaPlayer.isPlaying())
                    fab.setImageResource(R.drawable.ic_pause);
                else
                    fab.setImageResource(R.drawable.ic_play);
        }
    }

    @Override
    public boolean onError(FFmpegMediaPlayer mp, int what, int extra) {
        mp.release();
        isPlaying = false;
        if (fab != null)
            fab.setImageResource(R.drawable.ic_play);
        return false;
    }

    @Override
    public void onPrepared(FFmpegMediaPlayer mp) {
        mp.start();
        isPlaying = true;
        if (fab != null)
            fab.setImageResource(R.drawable.ic_pause);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnStreamFrag {
        void onNavigation(Fragment source, Fragment destination, Object extra);
    }
}

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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.carpa.library.R;
import com.carpa.library.entities.Messages;
import com.carpa.library.utilities.DirManager;
import com.carpa.library.utilities.Popup;
import com.carpa.library.utilities.Progress;
import com.example.jean.jcplayer.general.JcStatus;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.service.JcPlayerManagerListener;
import com.example.jean.jcplayer.view.JcPlayerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import im.delight.android.webview.AdvancedWebView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnStreamFrag} interface
 * to handle interaction events.
 * Use the {@link StreamFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StreamFrag extends Fragment implements JcPlayerManagerListener {
    private static final String ARG_FILES = "files";
    private String mFilesParam;
    private OnStreamFrag mListener;

    private List<Messages> mMessages;
    private TextView tittle;
    private AdvancedWebView webview;
    private Messages pdfMessage;
    private Messages audioMessage;
    private boolean isPlaying;
    private ScrollView playerHolder;
    //private FFmpegMediaPlayer mMediaPlayer = new FFmpegMediaPlayer();
    private Popup popup;
    private Progress progress;

    //new Player
    private JcPlayerView jcPlayerView;
    private JcAudio jcAudio;

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

        jcPlayerView = view.findViewById(R.id.jcplayer);
        tittle = view.findViewById(R.id.title);
        webview = view.findViewById(R.id.webview);
        playerHolder = view.findViewById(R.id.audioPlayer);

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

        if (jcPlayerView.isPlaying()) {
            jcPlayerView.setPressed(true);
        } else {
            if (audioMessage != null) {
                playAudio();
            } else {
                Toast.makeText(getContext(), "No audio found", Toast.LENGTH_SHORT).show();
            }
        }

        if (pdfMessage == null && audioMessage == null) {
            popup.show("Notification", "Sorry, there is no content to show.");
            return;
        }
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
        try {
            jcPlayerView.kill();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void displayPDF() {
        if(audioMessage == null){
            playerHolder.setVisibility(View.GONE);
        }
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
        if (pdfMessage == null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)playerHolder.getLayoutParams();
            layoutParams.height = 360;
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            playerHolder.setLayoutParams(layoutParams);
            jcAudio = JcAudio.createFromFilePath(audioMessage.getMessageName(), audioMessage.getPath().replaceAll(" ", "%20"));
        } else {
            jcAudio = JcAudio.createFromFilePath("", audioMessage.getPath().replaceAll(" ", "%20"));
        }
        ArrayList<JcAudio> jcAudios = new ArrayList<>();
        jcAudios.add(jcAudio);
        jcPlayerView.initWithTitlePlaylist(jcAudios, jcAudio.getTitle());
        jcPlayerView.setJcPlayerManagerListener(StreamFrag.this);
        jcPlayerView.createNotification(R.mipmap.ic_launcher);
    }

    //JcPlayer
    @Override
    public void onPreparedAudio(JcStatus jcStatus) {

    }

    @Override
    public void onCompletedAudio() {

    }

    @Override
    public void onPaused(JcStatus jcStatus) {

    }

    @Override
    public void onContinueAudio(JcStatus jcStatus) {

    }

    @Override
    public void onPlaying(JcStatus jcStatus) {

    }

    @Override
    public void onTimeChanged(JcStatus jcStatus) {

    }

    @Override
    public void onJcpError(Throwable throwable) {
        throwable.printStackTrace();
        popup.show("Oops!", "Something went wrong with the audio preparations.");
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

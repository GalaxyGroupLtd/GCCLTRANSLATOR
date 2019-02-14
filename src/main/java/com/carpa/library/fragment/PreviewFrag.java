package com.carpa.library.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.carpa.library.R;
import com.carpa.library.entities.Messages;
import com.carpa.library.entities.facade.MessagesFacade;
import com.carpa.library.utilities.DataFactory;
import com.carpa.library.utilities.DirManager;
import com.carpa.library.utilities.MessageCache;
import com.carpa.library.utilities.Popup;
import com.carpa.library.utilities.Progress;
import com.example.jean.jcplayer.general.JcStatus;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.service.JcPlayerManagerListener;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnPreviewFrag} interface
 * to handle interaction events.
 * Use the {@link PreviewFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreviewFrag extends Fragment implements OnPageChangeListener,
        OnLoadCompleteListener,
        OnPageScrollListener,
        JcPlayerManagerListener {
    private static final String MESSAGE_PARAM = "MESSAGE_PARAM";
    private static final String TAG = PreviewFrag.class.getSimpleName();
    private static int pdfPage;
    private final String PDF_PAGE_POSITION = "PDF_PAGE_POSITION";

    private String messageData;
    private OnPreviewFrag mListener;

    private Popup popup;
    private Progress progress;
    private TextView tittle;
    private ScrollView playerHolder;
    private PDFView pdfView;
    private Integer pageNumber = 0;
    private long currentPosition = 0;
    private long duration = 0;
    private JcStatus.PlayState playState = JcStatus.PlayState.PREPARING;
    private boolean isResuming = false;
    private JcAudio jcAudio;
    private String pdfFileName;
    private Messages message;
    private Messages pdfMessage;
    private Messages audioMessage;
    private List<Messages> mMessages;
    private boolean isPlaying;
    //private FFmpegMediaPlayer mMediaPlayer = new FFmpegMediaPlayer();

    //new Player
    private JcPlayerView jcPlayerView;

    public PreviewFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param message Parameter 1.
     * @return A new instance of fragment PreviewFrag.
     */
    public static PreviewFrag newInstance(String message) {
        PreviewFrag fragment = new PreviewFrag();
        Bundle args = new Bundle();
        args.putString(MESSAGE_PARAM, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            messageData = getArguments().getString(MESSAGE_PARAM);
        }
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.play_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            pageNumber = savedInstanceState.getInt("page");
            currentPosition = savedInstanceState.getLong("position");
            duration = savedInstanceState.getLong("duration");
            isResuming = savedInstanceState.getBoolean("resume");
            try {
                playState = JcStatus.PlayState.valueOf(savedInstanceState.getString("playState"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        popup = new Popup(getContext());
        progress = new Progress(getContext(), false, false);

        jcPlayerView = view.findViewById(R.id.jcplayer);
        tittle = view.findViewById(R.id.title);
        pdfView = view.findViewById(R.id.pdfView);
        playerHolder = view.findViewById(R.id.audioPlayer);

        try {
            message = (Messages) DataFactory.stringToObject(Messages.class, messageData);
        } catch (Exception e) {
            e.printStackTrace();
            popup.show("Oops!", "Sorry, we couldn't parse data for the message");
            return;
        }

        try {
            if (MessageCache.getMessageName(message.getMessageName()).size() < 0)
                mMessages = MessagesFacade.getMessagePerName(message.getMessageName());
            else
                mMessages = MessageCache.getMessageName(message.getMessageName());
        } catch (Exception e) {
            e.printStackTrace();
            popup.show("Oops!", "Sorry, we couldn't solve internal message content.");
            return;
        }

        for (Messages content : mMessages) {
            if (DirManager.isFileExist(content.getFileName()))
                if (content.getFileName().endsWith(".pdf")) {
                    pdfMessage = content;
                    //aac, acc+, avi, flac, mp2, mp3, mp4, ogg, 3gp
                } else if (content.getFileName().endsWith(".mp3")) {
                    audioMessage = content;
                }
        }

        if (pdfMessage != null) {
            displayPDF();
        } else {
            tittle.setText("No booklet found");
        }

        if (jcPlayerView.isPlaying() && isResuming) {
            isResuming = false;
            jcPlayerView.setPressed(true);
        } else {
            if (audioMessage != null) {
                playAudio();
            } else {
                Toast.makeText(getContext(), "No audio found", Toast.LENGTH_SHORT).show();
            }
        }

        if (pdfMessage == null && audioMessage == null) {
            popup.show("Oops!", "Sorry, there is no content on your internal memory.");
            return;
        }
    }

    private void playAudio() {
        try {
            if (pdfMessage == null) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) playerHolder.getLayoutParams();
                layoutParams.height = 360;
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                playerHolder.setLayoutParams(layoutParams);
                jcAudio = JcAudio.createFromFilePath(audioMessage.getMessageName(), DirManager.filePath(audioMessage.getFileName()).getAbsolutePath());
            } else {
                jcAudio = JcAudio.createFromFilePath("", DirManager.filePath(audioMessage.getFileName()).getAbsolutePath());
            }
            ArrayList<JcAudio> jcAudios = new ArrayList<>();
            jcAudios.add(jcAudio);
            jcPlayerView.initWithTitlePlaylist(jcAudios, jcAudio.getTitle());
            jcPlayerView.setJcPlayerManagerListener(PreviewFrag.this);
            jcPlayerView.createNotification(R.mipmap.ic_launcher);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayPDF() {
        if (audioMessage == null) {
            playerHolder.setVisibility(View.GONE);
        }
        Uri uri = Uri.parse(DirManager.filePath(pdfMessage.getFileName()).getAbsolutePath());
        File file = null;
        try {
            file = DirManager.filePath(pdfMessage.getFileName());
            if (!file.exists()) {
                popup.show("Oops!", "We have trouble trying to load the pdf file.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            popup.show("Oops!", "An error has occurred while loading the pdf file.");
        }
        try {
            pdfFileName = message.getMessageName();
            pdfView.fromFile(file)
                    .defaultPage(pageNumber)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .onPageChange(this)
                    .onPageScroll(this)
                    .enableAnnotationRendering(true)
                    .onLoad(PreviewFrag.this)
                    .scrollHandle(new DefaultScrollHandle(getContext()))
                    .spacing(10)
                    .enableAntialiasing(true)
                    .load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //PDF
        pageNumber = pdfView.getCurrentPage();
        savedInstanceState.putInt("page", pageNumber);

        //AUDIO
        savedInstanceState.putLong("position", currentPosition);
        savedInstanceState.putLong("duration", duration);
        savedInstanceState.putString("playState", playState.toString());
        savedInstanceState.putBoolean("resume", true);
    }

    /*@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            int pageNumber = savedInstanceState.getInt("page");
            Log.d("PDF", "");
            mediaPlayer.seekTo(position);
            if (savedInstanceState.getBoolean("isplaying"))
                mediaPlayer.start();
            playPause();
        }
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPreviewFrag) {
            mListener = (OnPreviewFrag) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPreviewFrag");
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
            if (jcPlayerView != null) {
                jcPlayerView.kill();
            }
            if (pdfView != null) {
                pdfView.recycle();
                pdfView.invalidate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadComplete(int nbPages) {
        try {
            PdfDocument.Meta meta = pdfView.getDocumentMeta();
            printBookmarksTree(pdfView.getTableOfContents(), "-");
            //pdfView.zoomTo(3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        try {
            pageNumber = page;
            tittle.setText(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageScrolled(int page, float positionOffset) {
        try {
            pdfView.invalidate();
            Log.d("onPageScroll", String.valueOf(page));
            Log.d("onPageScrollOffset", String.valueOf(positionOffset));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    //JCPlayer error handlers
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
        currentPosition = jcStatus.getCurrentPosition();
        duration = jcStatus.getDuration();
        playState = jcStatus.getPlayState();
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
    public interface OnPreviewFrag {
        void onNavigation(Fragment source, Fragment destination, Object extra);
    }
}

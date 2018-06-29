package com.carpa.library.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.carpa.library.R;
import com.carpa.library.entities.Messages;
import com.carpa.library.entities.facade.MessagesFacade;
import com.carpa.library.utilities.DataFactory;
import com.carpa.library.utilities.DirManager;
import com.carpa.library.utilities.Popup;
import com.carpa.library.utilities.Progress;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.io.IOException;
import java.util.List;

import wseemann.media.FFmpegMediaPlayer;

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
        FFmpegMediaPlayer.OnPreparedListener,
        FFmpegMediaPlayer.OnErrorListener {
    private static final String MESSAGE_PARAM = "MESSAGE_PARAM";
    private static final String TAG = PreviewFrag.class.getSimpleName();
    private static int pdfPage;
    private final String PDF_PAGE_POSITION = "PDF_PAGE_POSITION";

    private String messageData;
    private OnPreviewFrag mListener;

    private Popup popup;
    private Progress progress;
    private FloatingActionButton fab;
    private TextView tittle;
    private PDFView pdfView;
    private Integer pageNumber = 0;
    private String pdfFileName;
    private Messages message;
    private Messages pdfMessage;
    private Messages audioMessage;
    private List<Messages> mMessages;
    private boolean isPlaying;
    private FFmpegMediaPlayer mMediaPlayer = new FFmpegMediaPlayer();

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
        }
        popup = new Popup(getContext());
        progress = new Progress(getContext(), false, false);

        tittle = view.findViewById(R.id.title);
        fab = view.findViewById(R.id.playPause);
        pdfView = view.findViewById(R.id.pdfView);

        fab.setImageResource(R.drawable.ic_hourglass);
        try {
            message = (Messages) DataFactory.stringToObject(Messages.class, messageData);
        } catch (Exception e) {
            e.printStackTrace();
            popup.show("Oops!", "Sorry, we couldn't parse data for the message");
            return;
        }

        try {
            mMessages = MessagesFacade.getMessagePerName(message.getMessageName());
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
            popup.show("Notification", "Sorry, there is no content on your internal memory.");
            return;
        }
    }

    private void playAudio() {
        boolean isError = false;
        try {
            //Uri uri = Uri.parse(DirManager.filePath(audioMessage.getFileName()).getAbsolutePath());
            if (mMediaPlayer != null) {
                mMediaPlayer.setDataSource(DirManager.filePath(audioMessage.getFileName()).getAbsolutePath());
                mMediaPlayer.prepare();//prepareAsync
                mMediaPlayer.setOnPreparedListener(PreviewFrag.this);
                mMediaPlayer.setOnErrorListener(PreviewFrag.this);
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
        playPause();
    }

    private void displayPDF() {
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
        pdfFileName = message.getMessageName();
        pdfView.fromFile(file)
                .defaultPage(pageNumber)
                .enableSwipe(true)

                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(PreviewFrag.this)
                .scrollHandle(new DefaultScrollHandle(getContext()))
                .load();
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
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        pageNumber = pdfView.getCurrentPage();
        savedInstanceState.putInt("page", pageNumber);
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
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        if (pdfView != null) {
            pdfView.recycle();
            pdfView.invalidate();
        }
    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        printBookmarksTree(pdfView.getTableOfContents(), "-");
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        tittle.setText(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    @Override
    public void onPrepared(FFmpegMediaPlayer mp) {
        mp.start();
        isPlaying = true;
        if (fab != null)
            fab.setImageResource(R.drawable.ic_pause);
    }

    @Override
    public boolean onError(FFmpegMediaPlayer mp, int what, int extra) {
        mp.release();
        isPlaying = false;
        if (fab != null)
            fab.setImageResource(R.drawable.ic_play);
        return false;
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

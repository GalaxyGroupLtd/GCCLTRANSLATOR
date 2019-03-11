package com.carpa.library.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carpa.library.R;
import com.carpa.library.config.ExtraConfig;
import com.carpa.library.entities.Messages;
import com.carpa.library.utilities.DataFactory;
import com.carpa.library.utilities.Popup;
import com.carpa.library.utilities.Progress;
import com.carpa.library.utilities.adapter.MessageAdapter;
import com.carpa.library.utilities.loader.LocalMessageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnNewFrag} interface
 * to handle interaction events.
 * Use the {@link NewFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewFrag extends Fragment implements MessageAdapter.OnMessageAdapter, LocalMessageLoader.OnLocalMessagesLoader {
    private OnNewFrag mListener;

    private Popup popup;
    private Progress progress;
    private MessageAdapter adapter;
    private LocalMessageLoader messageLoader;
    private RecyclerView recycler;
    private SwipeRefreshLayout swipe;
    private static List<Messages> mMessages = new ArrayList<>();

    public NewFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewFrag.
     */
    public static NewFrag newInstance() {
        NewFrag fragment = new NewFrag();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.new_frag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        popup = new Popup(getContext());
        progress = new Progress(getContext(), false, false);
        recycler = view.findViewById(R.id.recycler);
        swipe = view.findViewById(R.id.swipe);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                messageLoader = new LocalMessageLoader(NewFrag.this);
                messageLoader.loadNew();
            }
        });

        if (!mMessages.isEmpty()) {
            initAdapter();
        } else {
            progress.show("Loading new messages");
            messageLoader = new LocalMessageLoader(NewFrag.this);
            messageLoader.loadNew();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNewFrag) {
            mListener = (OnNewFrag) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNewFrag");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMessageAdapter(boolean isClicked, View view, Object object, String action) {
        if (action == null)
            return;
        Messages message = (Messages) object;
        switch (action) {
            case ExtraConfig.MESSAGE_FAVORITE:
                message.setFavorite(true);
                long id = message.save();
                if (id < 0) {
                    popup.show("Oops", "Something went wrong and we couldn't save your favorite.");
                } else {
                    Snackbar.make(recycler, message.getMessageName() + " Added to favorites", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
                mMessages.remove(message);
                message.setNew(false);
                adapter.removeItem(message);
                try {
                    message.save();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mListener.onNewDecrement(1);
                break;
            case ExtraConfig.MESSAGE_INFO:
                popup.show("INFO", ((Messages) object).details());
                break;
            case ExtraConfig.MESSAGE_PLAY:
                //navigate to play fragment
                try {
                    mListener.onNavigation(NewFrag.this, PreviewFrag.newInstance(DataFactory.objectToString(object)), null);
                } catch (IOException e) {
                    e.printStackTrace();
                    popup.show("Oops", "Something went wrong and we couldn't view the message for the moment.");
                }
                mMessages.remove(message);
                message.setNew(false);
                adapter.removeItem(message);
                try {
                    message.save();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mListener.onNewDecrement(1);
                break;
        }
    }

    @Override
    public void onLocalMessages(boolean isLoaded, String message, List<Messages> messages) {
        if (progress != null)
            progress.clear();

        if(swipe.isRefreshing())
            swipe.setRefreshing(false);

        if (!isLoaded) {
            popup.show("Oops!", message);
        } else {
            try {
                if (messages.isEmpty())
                    popup.show("Info", "There no new messages yet.");
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            //if(adapter != null && messages.size() > adapter.getItemCount()){
            //  adapter.refreshAdapter(mLanguages);
            //adapter.notifyDataSetChanged();
            //return;
            //}
            mMessages = messages;
            //Initiate an adapter
            initAdapter();
        }
    }

    private void initAdapter() {
        List<Object> mObjects = new ArrayList<>(mMessages);
        adapter = new MessageAdapter(NewFrag.this, getContext(), mObjects);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(mLayoutManager);
        //recycler.setHasFixedSize(true);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);
    }

    public void filter(String charSequence) {
        if (adapter != null) {
            adapter.filter(charSequence);
        }
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
    public interface OnNewFrag {
        void onNavigation(Fragment source, Fragment destination, Object extra);

        void onNewDecrement(int decrement);
    }
}

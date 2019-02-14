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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carpa.library.R;
import com.carpa.library.config.ExtraConfig;
import com.carpa.library.entities.Messages;
import com.carpa.library.utilities.DataFactory;
import com.carpa.library.utilities.Popup;
import com.carpa.library.utilities.Progress;
import com.carpa.library.utilities.adapter.FavoriteAdapter;
import com.carpa.library.utilities.loader.FavoriteLocalLoader;
import com.carpa.library.utilities.loader.LocalMessageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFavorites} interface
 * to handle interaction events.
 * Use the {@link FavoritesFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFrag extends Fragment implements FavoriteAdapter.OnFavoritesAdapter, LocalMessageLoader.OnLocalMessagesLoader {
    private OnFavorites mListener;

    private Popup popup;
    private Progress progress;
    private FavoriteAdapter adapter;
    private RecyclerView recycler;
    private SwipeRefreshLayout swipe;
    private LocalMessageLoader messageLoader;
    private static List<Messages> myFavorites = new ArrayList<>();

    public FavoritesFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FavoritesFrag.
     */
    public static FavoritesFrag newInstance() {
        FavoritesFrag fragment = new FavoritesFrag();
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
        return inflater.inflate(R.layout.favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        popup = new Popup(getContext());
        progress = new Progress(getContext(), false, false);

        swipe = view.findViewById(R.id.swipe);
        recycler = view.findViewById(R.id.recycler);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                messageLoader = new LocalMessageLoader(FavoritesFrag.this);
                messageLoader.loadFavorites();
            }
        });
        if (!myFavorites.isEmpty()) {
            initAdapter();
        } else {
            progress.show("Loading favorites messages");
            messageLoader = new LocalMessageLoader(FavoritesFrag.this);
            messageLoader.loadFavorites();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFavorites) {
            mListener = (OnFavorites) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFavorites");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onFavoriteAdapter(boolean isClicked, View view, Object object, String action) {
        if (action == null)
            return;
        switch (action) {
            case ExtraConfig.MESSAGE_FAVORITE:
                Messages mMessage = (Messages) object;
                FavoriteLocalLoader favoriteLocalLoader = new FavoriteLocalLoader(mMessage, new FavoriteLocalLoader.OnFavoriteLocalLoader() {
                    @Override
                    public void onFavoriteMessages(boolean isLoaded, String message, List<Messages> messages) {
                        if (!isLoaded && !TextUtils.isEmpty(message))
                            popup.show("Oop!", message);

                        if (isLoaded) {
                            Snackbar.make(recycler, mMessage.getMessageName() + " Removed from favorites", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        }
                    }
                });
                favoriteLocalLoader.removeFavorite();
                break;
            case ExtraConfig.MESSAGE_INFO:
                popup.show("INFO", ((Messages) object).details());
                break;
            case ExtraConfig.MESSAGE_PLAY:
                //navigate to play fragment
                try {
                    mListener.onNavigation(FavoritesFrag.this, PreviewFrag.newInstance(DataFactory.objectToString(object)), null);
                } catch (IOException e) {
                    e.printStackTrace();
                    popup.show("Oops", "Something went wrong and we couldn't view the message for the moment.");
                }
                break;
        }
    }

    private void initAdapter() {
        List<Object> mObjects = new ArrayList<>(myFavorites);
        adapter = new FavoriteAdapter(FavoritesFrag.this, getContext(), mObjects);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(mLayoutManager);
        //recycler.setHasFixedSize(true);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);
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
                if (messages.isEmpty()) {
                    popup.show("Info", "There no favorites messages yet.");
                    return;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            this.myFavorites = messages;
            initAdapter();
        }
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
    public interface OnFavorites {
        void onNavigation(Fragment source, Fragment destination, Object extra);
    }
}

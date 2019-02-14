package com.carpa.library.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carpa.library.R;
import com.carpa.library.config.CmdConfig;
import com.carpa.library.entities.Languages;
import com.carpa.library.entities.facade.LanguageFacade;
import com.carpa.library.utilities.DeviceIdentity;
import com.carpa.library.utilities.Popup;
import com.carpa.library.utilities.Progress;
import com.carpa.library.utilities.adapter.LanguagesAdapter;
import com.carpa.library.utilities.loader.FilterLoader;
import com.carpa.library.utilities.loader.LocalLanguagesLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LanguagesFrag.OnLanguageFrag} interface
 * to handle interaction events.
 * Use the {@link LanguagesFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LanguagesFrag extends Fragment implements LocalLanguagesLoader.OnLocalLanguagesLoader,
        FilterLoader.OnFilterLoader,
        LanguagesAdapter.OnLanguageAdapter {
    private OnLanguageFrag mListener;
    private Popup popup;
    private Progress progress;
    private RecyclerView recycler;
    private SwipeRefreshLayout swipe;
    private static List<Languages> mLanguages = new ArrayList<>();
    private LocalLanguagesLoader languagesLoader;
    private LanguagesAdapter adapter;
    private FilterLoader filterLoader;

    public LanguagesFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LanguagesFrag.
     */
    public static LanguagesFrag newInstance() {
        LanguagesFrag fragment = new LanguagesFrag();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.languages_frag, container, false);
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
                filterLoader = new FilterLoader(getContext(), LanguagesFrag.this, CmdConfig.GET_SUPPORTED_LANGUAGES.toString(), DeviceIdentity.getCountryCode(getContext()), "none");
                filterLoader.start();
            }
        });
        if (!mLanguages.isEmpty()) {
            initAdapter();
        } else {
            progress.show("Loading languages");
            //check local database to see if there is supported languages
            if (Languages.count(Languages.class) <= 1) {
                //Request online language
                filterLoader = new FilterLoader(getContext(), LanguagesFrag.this, CmdConfig.GET_SUPPORTED_LANGUAGES.toString(), DeviceIdentity.getCountryCode(getContext()), "none");
                filterLoader.start();
            } else {
                //Request local languages
                languagesLoader = new LocalLanguagesLoader(LanguagesFrag.this);
                languagesLoader.load();
            }
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLanguageFrag) {
            mListener = (OnLanguageFrag) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLanCloud");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onLocalLanguages(boolean isLoaded, String message, List<Languages> languages) {
        if (progress != null)
            progress.clear();
        if (swipe.isRefreshing())
            swipe.setRefreshing(false);

        if (!isLoaded) {
            popup.show("Oops!", message);
            return;
        }
        //Initiate the adapter
        this.mLanguages = languages;
        initAdapter();
    }

    private void initAdapter() {
        //if(adapter != null){
        //  adapter.refreshAdapter(mLanguages);
        //adapter.notifyDataSetChanged();
        //return;
        //}
        adapter = new LanguagesAdapter(LanguagesFrag.this, getContext(), mLanguages);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);//new LinearLayoutManager(getContext());//new GridLayoutManager(getContext(), 2);
        recycler.setLayoutManager(mLayoutManager);
        //recycler.setHasFixedSize(true);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);
    }

    @Override
    public void onFilterLoader(boolean isLoaded, Object response) {
        if (progress != null)
            progress.clear();
        if (swipe != null && swipe.isRefreshing())
            swipe.setRefreshing(false);
        if (!isLoaded) {
            popup.show("Oops!", response.toString());
            return;
        }
        try {
            this.mLanguages = new Languages().serializeList(response.toString());
            for (Languages languages : mLanguages) {
                Languages lan = LanguageFacade.findLanguage(languages.getLanguageName());
                if (lan == null)
                    languages.save();
            }
            initAdapter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLanguageAdapter(boolean isClicked, View view, Languages languages) {
        if (isClicked) {
            //Language to fetch content
            try {
                mListener.onNavigation(LanguagesFrag.this, LanCloudFrag.newInstance(new Languages().deSerialize(languages)), null);
            } catch (IOException e) {
                e.printStackTrace();
                popup.show("Oops!", "We couldn't be able to navigate to this language.");
            }
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
    public interface OnLanguageFrag {
        void onNavigation(Fragment source, Fragment destination, Object extra);
    }
}

package com.carpa.library.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.carpa.library.R;
import com.carpa.library.config.CmdConfig;
import com.carpa.library.entities.Languages;
import com.carpa.library.entities.facade.LanguageFacade;
import com.carpa.library.utilities.DeviceIdentity;
import com.carpa.library.utilities.Popup;
import com.carpa.library.utilities.Progress;
import com.carpa.library.utilities.adapter.AddLanguageAdapter;
import com.carpa.library.utilities.loader.FilterLoader;
import com.carpa.library.utilities.loader.LocalLanguagesLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnAddLanguageFrag} interface
 * to handle interaction events.
 * Use the {@link AddLanguageFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddLanguageFrag extends Fragment implements AddLanguageAdapter.OnAddLanguage,
        LocalLanguagesLoader.OnLocalLanguagesLoader, FilterLoader.OnFilterLoader {
    private OnAddLanguageFrag mListener;

    private AddLanguageAdapter adapter;
    private ConcurrentHashMap<String, Languages> languagesList = new ConcurrentHashMap<>();
    ;
    private static List<Languages> mLanguages = new ArrayList<>();
    private Popup popup;
    private Progress progress;
    private LocalLanguagesLoader languagesLoader;
    private FilterLoader filterLoader;
    private RecyclerView recycler;
    private SwipeRefreshLayout swipe;
    private Button add;

    public AddLanguageFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddLanguageFrag.
     */
    public static AddLanguageFrag newInstance() {
        AddLanguageFrag fragment = new AddLanguageFrag();
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
        return inflater.inflate(R.layout.fragment_add_language, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        popup = new Popup(getContext());
        progress = new Progress(getContext(), false, false);
        recycler = view.findViewById(R.id.recycler);
        add = view.findViewById(R.id.add);
        swipe = view.findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Languages.count(Languages.class) <= 1) {
                    //Query API's supported languages
                    loadApiLan();
                } else {
                    //Query local languages
                    loadLocalLanguages();
                }
            }
        });

        if(!mLanguages.isEmpty()){
            initAdapter();
        }else{
            progress.show("Loading...");
            if (Languages.count(Languages.class) <= 1) {
                //Query API's supported languages
                loadApiLan();
            } else {
                //Query local languages
                loadLocalLanguages();
            }
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (languagesList.size() <= 0) {
                    popup.show("Notification", "It is highly recommended to set at least one language for the app to function properly.");
                } else if (languagesList.size() > 3) {
                    popup.show("Notification", "It is highly recommended not to set more than three languages as it may result into huge data consumption and too much synchronization activities.");
                } else {
                    try {
                        for (Languages languages : mLanguages) {
                            if (languages.isMain()) {
                                languages.setMain(false);
                                languages.save();
                            }
                        }
                        StringBuilder lanAdded = new StringBuilder();
                        for (Map.Entry entry : languagesList.entrySet()) {
                            Languages languages = (Languages) entry.getValue();
                            languages.setMain(true);
                            long id = languages.save();
                            if (id > 0) {
                                lanAdded.append(languages.display()).append(", ");
                            }
                        }
                        if (!TextUtils.isEmpty(lanAdded.toString())) {
                            lanAdded.append(" has been added.");
                            popup.show("Notification", lanAdded.toString());
                        } else {
                            popup.show("Notification", "Something went wrong in the process of adding new language(s)");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        popup.show("Oop!", "Something went wrong while adding your languages");
                    }

                }
            }
        });
    }

    private void loadApiLan() {
        //Query API's supported languages
        filterLoader = new FilterLoader(getContext(), AddLanguageFrag.this, CmdConfig.GET_SUPPORTED_LANGUAGES.toString(), DeviceIdentity.getCountryCode(getContext()), "none");
        filterLoader.start();
    }

    private void initAdapter() {
        adapter = new AddLanguageAdapter(AddLanguageFrag.this, getContext(), mLanguages);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(mLayoutManager);
        //recycler.setHasFixedSize(true);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);
    }

    private void loadLocalLanguages() {
        //Query local languages
        languagesLoader = new LocalLanguagesLoader(AddLanguageFrag.this);
        languagesLoader.load();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddLanguageFrag) {
            mListener = (OnAddLanguageFrag) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAddLanguageFrag");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onAddLanguage(boolean isAdd, View view, Languages language) {
        if (isAdd) {
            if (!languagesList.containsKey(language.getLanguageName())) {
                languagesList.put(language.getLanguageName(), language);
            }
        } else {
            if (languagesList.containsKey(language.getLanguageName())) {
                languagesList.remove(language.getLanguageName());
            }
        }
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
        mLanguages = languages;
        //Init adapter
        initAdapter();
    }

    @Override
    public void onFilterLoader(boolean isLoaded, Object response) {
        if (!isLoaded) {
            if (progress != null)
                progress.clear();
            if (swipe.isRefreshing())
                swipe.setRefreshing(false);
            popup.show("Oops!", "Something went wrong while loading the supported languages.");
            return;
        }
        try {
            List<Languages> mLanguages = new Languages().serializeList(response.toString());
            for (Languages lan : mLanguages) {
                Languages language = LanguageFacade.findLanguage(lan.getLanguageName());
                if (language == null) {
                    lan.save();
                }
            }
            loadLocalLanguages();
        } catch (Exception e) {
            e.printStackTrace();
            popup.show("Oops!", "Something went wrong while parsing the supported languages.");
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
    public interface OnAddLanguageFrag {
        void onNavigation(Fragment source, Fragment destination, Object extra);
    }
}

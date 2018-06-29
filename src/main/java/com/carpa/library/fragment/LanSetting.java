package com.carpa.library.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carpa.library.R;
import com.carpa.library.config.ExtraConfig;
import com.carpa.library.entities.Languages;
import com.carpa.library.utilities.DataFactory;
import com.carpa.library.utilities.Popup;
import com.carpa.library.utilities.Progress;
import com.carpa.library.utilities.UtilModel;
import com.carpa.library.utilities.adapter.MainAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnLanSettings} interface
 * to handle interaction events.
 * Use the {@link LanSetting#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LanSetting extends Fragment implements Popup.OnPopAction, MainAdapter.OnMainAdapater {
    private static final String ARG_LAN = "ARG_LAN";
    private String mLanguagesData;
    private OnLanSettings mListener;

    private List<Languages> languages;
    private Languages defaultLan;
    private String[] popupActions = {"OK", "Cancel"};
    private Popup popup;
    private Progress progress;
    private MainAdapter adapter;
    private RecyclerView recycler;

    public LanSetting() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param languages Parameter 1.
     * @return A new instance of fragment LanSetting.
     */
    public static LanSetting newInstance(String languages) {
        LanSetting fragment = new LanSetting();
        Bundle args = new Bundle();
        args.putString(ARG_LAN, languages);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLanguagesData = getArguments().getString(ARG_LAN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.lan_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        popup = new Popup(getContext());
        progress = new Progress(getContext(), false, false);

        recycler = view.findViewById(R.id.recycler);

        try {
            languages = new ArrayList<>();
            List<Object> objects = DataFactory.stringToObjectList(Languages.class, mLanguagesData);
            if (objects.isEmpty()) {
                popup.show("Oops!", "Sorry, we couldn't find supported languages.");
                return;
            }
            for (Object o : objects) {
                languages.add((Languages) o);
            }

            adapter = new MainAdapter(LanSetting.this, getContext(), objects);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recycler.setLayoutManager(mLayoutManager);
            recycler.setHasFixedSize(true);
            recycler.setItemAnimator(new DefaultItemAnimator());
            recycler.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
            popup.show("Oops!", "Sorry, we couldn't parse supported languages.");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLanSettings) {
            mListener = (OnLanSettings) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLanSettings");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void popAction(boolean isAction, String action) {
        if (isAction && action.equals(popupActions[0])) {
            try {
                defaultLan.setMain(true);
                long id = defaultLan.save();
                if (id < 0) {
                    popup.show("Oops!", "Something went wrong while saving your request");
                } else {
                    mListener.onNavigation(LanSetting.this, null, ExtraConfig.DEFAULT_LAN_SET);
                }
            } catch (Exception e) {
                e.printStackTrace();
                popup.show("Oops!", "Something went wrong while saving your request");
            }
        }
    }

    @Override
    public void onMainAdapater(boolean isClicked, View view, Object object) {
        if (isClicked) {
            this.defaultLan = (Languages) object;
            popup.show("Notification", "Do you really want to make " + ((UtilModel) object).display() + " as your default language?", popupActions, LanSetting.this);
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
    public interface OnLanSettings {
        void onNavigation(Fragment source, Fragment destination, Object extra);
    }
}

package com.carpa.library.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carpa.library.R;
import com.carpa.library.config.CmdConfig;
import com.carpa.library.config.ExtraConfig;
import com.carpa.library.entities.Languages;
import com.carpa.library.entities.Messages;
import com.carpa.library.utilities.CloudMessageCache;
import com.carpa.library.utilities.DataFactory;
import com.carpa.library.utilities.DeviceIdentity;
import com.carpa.library.utilities.MessageNameFactory;
import com.carpa.library.utilities.Popup;
import com.carpa.library.utilities.Progress;
import com.carpa.library.utilities.adapter.CloudMessageAdapter;
import com.carpa.library.utilities.loader.FilterLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnLanCloud} interface
 * to handle interaction events.
 * Use the {@link LanCloudFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LanCloudFrag extends Fragment implements FilterLoader.OnFilterLoader, CloudMessageAdapter.OnCloudMessageAdapter {
    private static final String ARG_LAN = "ARG_LAN";
    private String lanData;
    private OnLanCloud mListener;

    private Languages languages;
    private Popup popup;
    private Progress progress;
    private RecyclerView recycler;
    private SwipeRefreshLayout swipe;
    private FilterLoader filterLoader;
    private static List<Messages> mMessages = new ArrayList<>();
    private CloudMessageAdapter adapter;

    public LanCloudFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param lanParam Parameter 1.
     * @return A new instance of fragment LanCloudFrag.
     */
    public static LanCloudFrag newInstance(String lanParam) {
        LanCloudFrag fragment = new LanCloudFrag();
        Bundle args = new Bundle();
        args.putString(ARG_LAN, lanParam);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lanData = getArguments().getString(ARG_LAN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.language_cloud, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        popup = new Popup(getContext());
        progress = new Progress(getContext(), false, false);
        recycler = view.findViewById(R.id.recycler);
        swipe = view.findViewById(R.id.swipe);
        try {
            languages = new Languages().serialize(lanData);
        } catch (Exception e) {
            e.printStackTrace();
            popup.show("Oops!", "Something went wrong while parsing language input.");
            return;
        }
        //check cache and if empty check online
        mMessages = CloudMessageCache.getMessages(languages.getLanguageName());
        if (mMessages.isEmpty()) {
            //Check online
            progress.show("Loading content");
            filterLoader = new FilterLoader(getContext(), LanCloudFrag.this, CmdConfig.GET_LANGUAGE_CONTENT.toString(), DeviceIdentity.getCountryCode(getContext()), languages.getLanguageName());
            filterLoader.start();
        } else {
            //Init the adapter
            initAdapter();
        }
    }

    private void initAdapter() {
        //if(adapter != null){
        //  adapter.refreshAdapter(mLanguages);
        //adapter.notifyDataSetChanged();
        //return;
        //}
        adapter = new CloudMessageAdapter(LanCloudFrag.this, getContext(), mMessages);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(mLayoutManager);
        recycler.setHasFixedSize(true);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLanCloud) {
            mListener = (OnLanCloud) context;
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
            List<Messages> messagesList = new Messages().serializeList(response.toString());
            for (Messages messages : messagesList) {
                messages.setMessageName(MessageNameFactory.name(messages.getFileName()));
                mMessages.add(messages);
            }
            CloudMessageCache.add(languages.getLanguageName(), mMessages);
            initAdapter();
        } catch (Exception e) {
            e.printStackTrace();
            popup.show("Oops!", "Something went wrong while parsing language cloud content.");
        }
    }

    @Override
    public void onCloudMessageAdapter(boolean isClicked, View view, Object object, String action) {
        if (action == null)
            return;
        //look into the actions
        switch (action) {
            case ExtraConfig.MESSAGE_INFO:
                popup.show("INFO", ((Messages) object).details());
                break;
            case ExtraConfig.MESSAGE_STREAM:
                //navigate to Stream fragment
                try {
                    List<Messages> messagesList = new Messages().serializeList(object.toString());
                    mListener.onNavigation(LanCloudFrag.this, StreamFrag.newInstance(DataFactory.objectToString(messagesList)), null);
                } catch (IOException e) {
                    e.printStackTrace();
                    popup.show("Oops", "Something went wrong and we couldn't view the message for the moment.");
                }
                break;
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
    public interface OnLanCloud {
        void onNavigation(Fragment source, Fragment destination, Object extra);
    }
}

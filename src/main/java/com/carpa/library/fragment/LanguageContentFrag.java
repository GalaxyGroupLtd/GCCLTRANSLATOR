package com.carpa.library.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.carpa.library.R;
import com.carpa.library.entities.Languages;
import com.carpa.library.entities.Messages;
import com.carpa.library.entities.facade.LanguageFacade;
import com.carpa.library.models.GeneralListModel;
import com.carpa.library.models.MyLanguageModel;
import com.carpa.library.services.RectifierService;
import com.carpa.library.utilities.DownloadTaskListener;
import com.carpa.library.utilities.MessageCache;
import com.carpa.library.utilities.Popup;
import com.carpa.library.utilities.Progress;
import com.carpa.library.utilities.adapter.MyLanguageAdapter;
import com.carpa.library.utilities.loader.FilterLoader;
import com.carpa.library.utilities.loader.LocalLanguagesLoader;
import com.carpa.library.utilities.views.MonthView;
import com.carpa.library.utilities.views.YearView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnLanguageContent} interface
 * to handle interaction events.
 * Use the {@link LanguageContentFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LanguageContentFrag extends Fragment implements LocalLanguagesLoader.OnLocalLanguagesLoader,
        FilterLoader.OnFilterLoader,
        MyLanguageAdapter.OnMyLanguage,
        YearView.OnYearView,
        MonthView.OnMonthView {
    private OnLanguageContent mListener;
    private Popup popup;
    private Progress progress;
    private RecyclerView recycler;
    private SwipeRefreshLayout swipe;
    private ImageView icn;
    private static List<Languages> mLanguages = new ArrayList<>();
    private LocalLanguagesLoader languagesLoader;
    private MyLanguageAdapter adapter;
    private FilterLoader filterLoader;
    private MyLanguageModel selectLanguage;
    private Integer selectedYear;
    private String selectedMonth;
    private YearView yearView;
    private MonthView monthView;
    private HashMap<String, List<Messages>> monthsMessages;
    private Intent alarmIntent;
    private PendingIntent pendingIntent;
    private AlarmManager alarm;

    public LanguageContentFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LanguagesFrag.
     */
    public static LanguageContentFrag newInstance() {
        LanguageContentFrag fragment = new LanguageContentFrag();
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
        icn = view.findViewById(R.id.icn);
        icn.setImageResource(R.drawable.ic_home);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                languagesLoader = new LocalLanguagesLoader(LanguageContentFrag.this);
                languagesLoader.loadMyLanguages();
            }
        });

        if (!mLanguages.isEmpty()) {
            initAdapter();
        } else {
            progress.show("Loading languages");
            //Request local languages
            languagesLoader = new LocalLanguagesLoader(LanguageContentFrag.this);
            languagesLoader.loadMyLanguages();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLanguageContent) {
            mListener = (OnLanguageContent) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLanCloud");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        scheduleAlarm();
    }

    @Override
    public void onStop() {
        super.onStop();
        cancelAlarm();
    }

    public void scheduleAlarm() {
        DownloadTaskListener.setSchedule(true);
        Log.d("BOOT", "Scheduling download task");
        Calendar cal = Calendar.getInstance();
        alarmIntent = new Intent(getActivity(), RectifierService.class);
        alarmIntent.setAction(RectifierService.ACTION_RECT);
        pendingIntent = PendingIntent.getService(getActivity(),
                999,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarm = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), RectifierService.PERIOD, pendingIntent);
    }

    public void cancelAlarm() {
        if (alarm != null && pendingIntent != null) {
            alarm.cancel(pendingIntent);
        }
    }

    @Override
    public void onLocalLanguages(boolean isLoaded, String message, List<Languages> languages) {
        if (progress != null)
            progress.clear();

        if (swipe != null && swipe.isRefreshing())
            swipe.setRefreshing(false);

        if (!isLoaded) {
            popup.show("Oops!", message);
            return;
        }
        if (languages.isEmpty()) {
            popup.show("Oops!", "There no language set");
            return;
        }
        //Initiate the adapter
        this.mLanguages = languages;
        //List<Object> objects = new ArrayList<>(languages);
        //LanguageCache.ADD(LanguageCache.SET_LANGUAGE, objects);
        initAdapter();
    }

    private void initAdapter() {
        //if(adapter != null){
        //  adapter.refreshAdapter(mLanguages);
        //adapter.notifyDataSetChanged();
        //return;
        //}
        List<MyLanguageModel> myLanguageModels = new ArrayList<>();
        for (Languages lan : mLanguages) {
            myLanguageModels.add(new MyLanguageModel(lan.getLanguageName(), lan.getLanguageCode(), MessageCache.getLanGroupedMessages(lan.getLanguageCode()).size() + ""));
        }
        adapter = new MyLanguageAdapter(LanguageContentFrag.this, getContext(), myLanguageModels);
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
    public void onMyLanguage(boolean isClicked, View view, MyLanguageModel language) {
        if (language != null) {
            this.selectLanguage = language;
            //Fetch and Popup Year box
            HashMap<Integer, List<Messages>> lanYearGroupedMessages = MessageCache.getLanYearGroupedMessages(language.getLanCode());
            List<GeneralListModel> yearList = new ArrayList<>();
            for (Map.Entry<Integer, List<Messages>> entry : lanYearGroupedMessages.entrySet()) {
                yearList.add(new GeneralListModel(entry.getKey().toString(), String.valueOf(entry.getValue().size()), false));
            }
            yearView = new YearView(LanguageContentFrag.this, getContext(), yearList);
            try {
                yearView.showYear();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Couldn't show years.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onYearView(boolean isClicked, boolean isOverFlow, View view, Object object) {
        if (yearView != null)
            yearView.dimissYearBox();
        if (!isClicked)
            return;

        GeneralListModel generalListModel = (GeneralListModel) object;
        this.selectedYear = Integer.parseInt(generalListModel.getLogo());
        //Fetch and Popup Month box
        this.monthsMessages = MessageCache.getLanYearMonthGroupedMessages(selectLanguage.getLanCode(), selectedYear);
        List<GeneralListModel> monthList = new ArrayList<>();
        for (Map.Entry<String, List<Messages>> entry : monthsMessages.entrySet()) {
            monthList.add(new GeneralListModel(entry.getKey(), String.valueOf(entry.getValue().size()), false));
        }
        monthView = new MonthView(LanguageContentFrag.this, getContext(), monthList);
        try {
            monthView.showYear();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Couldn't show years.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMonthView(boolean isClicked, boolean isOverFlow, View view, Object object) {
        if (monthView != null)
            monthView.dimissYearBox();
        if (!isClicked)
            return;

        GeneralListModel generalListModel = (GeneralListModel) object;
        this.selectedMonth = generalListModel.getLogo();
        //Get list of Messages to be passed to HomeFrag
        mListener.onNavigation(LanguageContentFrag.this, HomeFrag.newInstance(selectLanguage.getLanCode(), selectedYear + "", selectedMonth, monthsMessages.get(selectedMonth)), null);
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
    public interface OnLanguageContent {
        void onNavigation(Fragment source, Fragment destination, Object extra);
    }
}

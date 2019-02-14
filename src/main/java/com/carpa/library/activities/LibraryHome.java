package com.carpa.library.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.carpa.library.R;
import com.carpa.library.config.BroadcastConfig;
import com.carpa.library.entities.Messages;
import com.carpa.library.fragment.AddLanguageFrag;
import com.carpa.library.fragment.DownloadedMessagesFrag;
import com.carpa.library.fragment.FavoritesFrag;
import com.carpa.library.fragment.HomeFrag;
import com.carpa.library.fragment.LanCloudFrag;
import com.carpa.library.fragment.LanguageContentFrag;
import com.carpa.library.fragment.LanguagesFrag;
import com.carpa.library.fragment.NewFrag;
import com.carpa.library.fragment.PreviewFrag;
import com.carpa.library.fragment.StreamFrag;
import com.carpa.library.services.CloudService;
import com.carpa.library.utilities.ApplicationInitiator;
import com.carpa.library.utilities.CountDrawable;
import com.carpa.library.utilities.DownloadTaskListener;
import com.carpa.library.utilities.MessageCache;
import com.carpa.library.utilities.Popup;
import com.carpa.library.utilities.Progress;
import com.carpa.library.utilities.loader.LocalMessageLoader;

import java.util.Calendar;
import java.util.List;

public class LibraryHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFrag.OnHomeFrag,
        PreviewFrag.OnPreviewFrag,
        FavoritesFrag.OnFavorites,
        NewFrag.OnNewFrag,
        LocalMessageLoader.OnLocalMessagesLoader,
        LanguagesFrag.OnLanguageFrag,
        LanCloudFrag.OnLanCloud,
        StreamFrag.OnStreamFrag,
        DownloadedMessagesFrag.OnDownloadedMessagesFrag,
        AddLanguageFrag.OnAddLanguageFrag,
        LanguageContentFrag.OnLanguageContent,
        ApplicationInitiator.OnAppInitiated {

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Popup popup;
    private Progress progress;
    private MenuItem item;
    private Menu defaultMenu;
    private LocalMessageLoader messageLoader;

    private Intent alarmIntent;
    private PendingIntent pendingIntent;
    private AlarmManager alarm;

    private boolean initiating = true, showAppInitProgress = false;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String action = intent.getAction();
            if (action.equals(BroadcastConfig.NEW_MESSAGE[0])) {
                Log.d("receiver", "Got action: " + action);
                try {
                    //Refresh home fragment if it is on front
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (fragment != null && fragment instanceof HomeFrag) {
                        //make an update
                        ((HomeFrag) fragment).onNewMessage();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_home);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        popup = new Popup(LibraryHome.this);
        progress = new Progress(LibraryHome.this, false, true);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //load all message into the cache
        if (!initiating) {
            progress.show("Initiating...");
            messageLoader = new LocalMessageLoader(LibraryHome.this);
            messageLoader.loadAll();
        }

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            if (!initiating)
                fragmentHandler(LanguageContentFrag.newInstance());
            ApplicationInitiator ai = new ApplicationInitiator(LibraryHome.this, this);
            ai.start();
        }

        //schedule activity
        if (!DownloadTaskListener.isScheduled()) {
            Log.d("SCHEDULE", "Missed Boot, reinitialise download tasks");
            scheduleAlarm();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(LibraryHome.this).registerReceiver(
                mMessageReceiver, new IntentFilter(BroadcastConfig.NEW_MESSAGE[0]));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(LibraryHome.this).unregisterReceiver(
                mMessageReceiver);
    }

    @Override
    public void onBackPressed() {
        final String[] actions = {"Yes", "No"};
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1)
                super.onBackPressed();
            else {
                popup.show("Exit", "Do you really want to exit the application?", actions, new Popup.OnPopAction() {
                    @Override
                    public void popAction(boolean isAction, String action) {
                        if (isAction && action.equals(actions[0])) {
                            //exit the app
                            finish();
                        }
                    }
                });
            }
        }
        homeContext(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
    }

    public void scheduleAlarm() {
        DownloadTaskListener.setSchedule(true);
        Log.d("SCHEDULE", "Scheduling download task");
        Calendar cal = Calendar.getInstance();
        alarmIntent = new Intent(LibraryHome.this, CloudService.class);
        alarmIntent.setAction(CloudService.ACTION_SYNC);
        pendingIntent = PendingIntent.getService(LibraryHome.this,
                999,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), CloudService.PERIOD, pendingIntent);
    }

    private void fragmentHandler(Fragment fragment) {
        if (fragment == null)
            return;

        homeContext(fragment);
        String backStateName = fragment.getClass().getSimpleName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped && fragmentManager.findFragmentByTag(backStateName) == null) { //fragment not in back stack, create it.
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment, backStateName);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.library_home, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        this.item = searchItem;
        this.defaultMenu = menu;
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Query the list of element within a proper fragment.
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (fragment instanceof HomeFrag) {
                    item.setVisible(true);
                    ((HomeFrag) fragment).filter(newText);
                } else if (fragment instanceof FavoritesFrag) {
                    item.setVisible(true);
                    ((FavoritesFrag) fragment).filter(newText);
                } else if (fragment instanceof NewFrag) {
                    item.setVisible(true);
                    ((NewFrag) fragment).filter(newText);
                } else if (fragment instanceof DownloadedMessagesFrag) {
                    item.setVisible(true);
                    ((DownloadedMessagesFrag) fragment).filter(newText);
                } else if (fragment instanceof LanCloudFrag) {
                    item.setVisible(true);
                    ((LanCloudFrag) fragment).filter(newText);
                } else {
                    item.setVisible(false);
                }

                return true;
            }
        });
        // Define the listener
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                //isSearch = true;
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                //isSearch = false;
                return true;
            }
        });
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof HomeFrag) {
            searchItem.setVisible(true);
        } else if (fragment instanceof FavoritesFrag) {
            searchItem.setVisible(true);
        } else if (fragment instanceof NewFrag) {
            searchItem.setVisible(true);
        } else if (fragment instanceof DownloadedMessagesFrag) {
            searchItem.setVisible(true);
        } else if (fragment instanceof LanCloudFrag) {
            searchItem.setVisible(true);
        } else {
            searchItem.setVisible(false);
        }

        //Initiate badge count
        onNewMessage();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.notifications) {
            fragmentHandler(NewFrag.newInstance());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            fragmentHandler(LanguageContentFrag.newInstance());
        } else if (id == R.id.nav_favorites) {
            fragmentHandler(FavoritesFrag.newInstance());
        } else if (id == R.id.nav_new) {
            fragmentHandler(NewFrag.newInstance());
        } else if (id == R.id.nav_download) {
            fragmentHandler(DownloadedMessagesFrag.newInstance());
        } else if (id == R.id.nav_cloud) {
            fragmentHandler(LanguagesFrag.newInstance());
        } else if (id == R.id.nav_settings) {
            fragmentHandler(AddLanguageFrag.newInstance());
        } else if (id == R.id.nav_about) {
            //About
            about();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void homeContext(Fragment fragment) {
        if (fragment instanceof HomeFrag) {
            if (item != null)
                item.setVisible(true);
        } else if (fragment instanceof FavoritesFrag) {
            if (item != null)
                item.setVisible(true);
        } else if (fragment instanceof NewFrag) {
            if (item != null)
                item.setVisible(true);
        } else if (fragment instanceof DownloadedMessagesFrag) {
            if (item != null)
                item.setVisible(true);
        } else if (fragment instanceof LanCloudFrag) {
            if (item != null)
                item.setVisible(true);
        } else {
            if (item != null)
                item.setVisible(false);
        }
    }

    @Override
    public void onNavigation(Fragment source, Fragment direction, Object extra) {
        fragmentHandler(direction);
    }

    @Override
    public void onNewDecrement(int decrement) {
        loadMessagesForBadge();
    }

    private void loadMessagesForBadge() {
        messageLoader = new LocalMessageLoader(new LocalMessageLoader.OnLocalMessagesLoader() {
            @Override
            public void onLocalMessages(boolean isLoaded, String message, List<Messages> messages) {
                if (!isLoaded)
                    return;
                else {
                    if (messages.isEmpty())
                        setCount("0");
                    else
                        setCount(String.valueOf(messages.size()));
                }
            }
        });
        messageLoader.loadNew();
    }

    @Override
    public void onNewMessage() {
        messageLoader = new LocalMessageLoader(LibraryHome.this);
        messageLoader.loadAll();
    }

    @Override
    public void onLocalMessages(boolean isLoaded, String message, List<Messages> messages) {
        initiating = false;
        if (progress != null)
            progress.clear();

        if (!isLoaded) {
            if (!TextUtils.isEmpty(message)) {
                popup.show("Oop!", message);
            }
            return;
        }
        if (!MessageCache.isAdding)
            MessageCache.addAll(messages);

        loadMessagesForBadge();

        if (!initiating) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment != null && !(fragment instanceof PreviewFrag)) {
                //make an update
                fragmentHandler(LanguageContentFrag.newInstance());
            } else if (fragment == null) {
                fragmentHandler(LanguageContentFrag.newInstance());
            }

        }
    }

    public void setCount(String numOfNewMessages) {
        MenuItem menuItem = defaultMenu.findItem(R.id.notifications);
        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();

        CountDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_group_count);
        if (reuse != null && reuse instanceof CountDrawable) {
            badge = (CountDrawable) reuse;
        } else {
            badge = new CountDrawable(LibraryHome.this);
        }

        badge.setCount(numOfNewMessages);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_group_count, badge);
    }

    public void about() {
        try {
            PackageInfo pInfo = LibraryHome.this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int verCode = pInfo.versionCode;
            StringBuilder sb = new StringBuilder();
            sb.append("LIBRARY APP").append("\n\n");
            sb.append("Version name: ").append(version).append("\n");
            sb.append("Version code: ").append(verCode).append("\n\n");
            sb.append("If you found any bug or have an idea on improvement, feel free to send us an email on:").append("\n");

            SpannableString s = new SpannableString("iaubain@yahoo.fr");
            Linkify.addLinks(s, Linkify.EMAIL_ADDRESSES);
            sb.append("-").append(s).append("\n");
            s = new SpannableString("ethangraphic@gmail.com");
            Linkify.addLinks(s, Linkify.EMAIL_ADDRESSES);
            sb.append("-").append(s).append("\n");
            sb.append("or contact us on\n");
            s = new SpannableString("+250 785 534 672");
            Linkify.addLinks(s, Linkify.PHONE_NUMBERS);
            sb.append(s).append("\n");

            popup.show("About", sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(LibraryHome.this, "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAppInitiated(boolean isInitiated, String message) {
        if(!showAppInitProgress)
            return;
        if(!isInitiated)
            popup.show("Oops!", message);
        else{
            popup.show("Info", message);
        }
    }

    @Override
    public void onInitiationProgress(int progre, Object extra) {
        if(!showAppInitProgress)
            return;
        if(progress != null){
            progress.update(extra.toString());
        }
    }
}

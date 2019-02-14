package com.carpa.library.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.carpa.library.R;
import com.carpa.library.config.CmdConfig;
import com.carpa.library.config.ExtraConfig;
import com.carpa.library.entities.facade.LanguageFacade;
import com.carpa.library.fragment.LanSetting;
import com.carpa.library.utilities.DeviceIdentity;
import com.carpa.library.utilities.DirManager;
import com.carpa.library.utilities.Popup;
import com.carpa.library.utilities.Progress;
import com.carpa.library.utilities.loader.FilterLoader;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class Home extends AppCompatActivity implements Popup.OnPopAction, FilterLoader.OnFilterLoader, LanSetting.OnLanSettings {
    private Toolbar toolbar;
    private Popup popup;
    private Progress progress;
    private FilterLoader filterLoader;
    private String[] popupActions = {"OK", "Cancel"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        popup = new Popup(Home.this);
        progress = new Progress(Home.this, false, false);

        //Check and request for permissions
        initPermissions();

        //Check root directory
        try {
            DirManager.rootDir();
        } catch (Exception e) {
            e.printStackTrace();
            popup.show("Oops!", e.getMessage());
        }

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            //check if a language is registered
            try {
                if (!LanguageFacade.isLanguageSet()) {
                    //Load all supported languages
                    progress.show("Loading...");
                    filterLoader = new FilterLoader(Home.this, Home.this, CmdConfig.GET_SUPPORTED_LANGUAGES.toString(), DeviceIdentity.getCountryCode(Home.this), "None");
                    filterLoader.start();
                } else {
                    //start library activity
                    startLibrary();
                }
            } catch (Exception e) {
                popup.show("Oops!", e.getMessage());
            }
            //initial fragment
            //fragmentHandler(Provider.LOGIN());
        }

        /*//test message
        try {
            if(MessagesFacade.getMessagePerName("KIN20180614-test-message").isEmpty()){
                Messages messages = new Messages(IdGen.NUMBER(),
                        "KIN20180614-test-message",
                        "KIN20180614-test-message.pdf",
                        "pdf",
                        "http://kinyarwanda.afrisatt.info/KIN20180614-test-message.pdf",
                        "9000",
                        "2018-06-14 20:55");
                long id = messages.save();
                if(id < 0)
                    popup.show("Oops!", "We couldn't save pdf");
                messages = new Messages(IdGen.NUMBER(),
                        "KIN20180614-test-message",
                        "KIN20180614-test-message.mp3",
                        "mp3",
                        "http://kinyarwanda.afrisatt.info/KIN20180614-test-message.mp3",
                        "9000",
                        "2018-06-14 20:57");
                id = messages.save();
                if(id < 0)
                    popup.show("Oops!", "We couldn't save mp3");
            }
        } catch (Exception e) {
            e.printStackTrace();
            popup.show("Oops!", "We couldn't load test message");
        }*/
    }

    private void initPermissions() {
        Dexter.withActivity(Home.this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.isAnyPermissionPermanentlyDenied()) {
                    String message = "My library application needs those permissions to better serve its purpose:\n";
                    int deniedPermissions = report.getDeniedPermissionResponses().size();
                    int count = 1;
                    for (PermissionDeniedResponse response : report.getDeniedPermissionResponses()) {
                        if (deniedPermissions == count)
                            message += " and " + response.getPermissionName() + ".";
                        else
                            message += response.getPermissionName() + ",";
                        count++;
                    }
                    popup.show("Permissions", message, popupActions, Home.this);
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    private void fragmentHandler(Fragment fragment) {
        if (fragment == null)
            return;
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
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            super.onBackPressed();
        } else {
            finish();
        }
    }

    @Override
    public void popAction(boolean isAction, String action) {
        if (isAction && action.equals(popupActions[0])) {
            initPermissions();
        }
    }

    @Override
    public void onFilterLoader(boolean isLoaded, Object response) {
        if (progress != null)
            progress.clear();
        if (!isLoaded)
            popup.show("Oops!", response.toString());
        else {
            //Show a fragment with a list to choose the default language
            try {
                System.out.print("Resp:" + response.toString());
                fragmentHandler(LanSetting.newInstance(response.toString()));
            } catch (Exception e) {
                e.printStackTrace();
                popup.show("Oops!", "Something went wrong parsing languages");
            }
        }
    }

    @Override
    public void onNavigation(Fragment source, Fragment destination, Object extra) {
        if (extra.equals(ExtraConfig.DEFAULT_LAN_SET)) {
            //start library activity
            startLibrary();
        }
    }

    private void startLibrary() {
        //start library activity
        Intent intent = new Intent(Home.this, LibraryHome.class);
        startActivity(intent);
        finish();
    }
}

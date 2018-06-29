package com.carpa.library.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.carpa.library.config.BroadcastConfig;
import com.carpa.library.entities.Messages;
import com.carpa.library.utilities.loader.LocalMessageLoader;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class RectifierService extends IntentService implements LocalMessageLoader.OnLocalMessagesLoader {
    public static final String ACTION_RECT = "com.carpa.library.services.action.ACTION_RECT";
    public static final String EXTRA_RECT = "com.carpa.library.services.extra.EXTRA_RECT";
    public static final long PERIOD = 1000 * 60 * 3;
    private LocalMessageLoader messageLoader;

    public RectifierService() {
        super("RectifierService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startRectification(Context context, String rect) {
        Intent intent = new Intent(context, RectifierService.class);
        intent.putExtra(EXTRA_RECT, rect);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_RECT.equals(action)) {
                final String param2 = intent.getStringExtra(EXTRA_RECT);
                handleRectification(param2);
            }
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleRectification(String rectParam) {
        try {
            messageLoader = new LocalMessageLoader(RectifierService.this);
            messageLoader.loadAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocalMessages(boolean isLoaded, String message, List<Messages> messages) {
        if (!isLoaded)
            return;
        sendBroadcastProfileModel();
    }

    private void sendBroadcastProfileModel() {
        try {
            Intent intent = new Intent(BroadcastConfig.NEW_MESSAGE[0]);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

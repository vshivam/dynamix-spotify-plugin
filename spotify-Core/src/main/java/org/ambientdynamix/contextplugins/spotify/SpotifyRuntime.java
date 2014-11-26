/*
 * Copyright (C) The Ambient Dynamix Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ambientdynamix.contextplugins.spotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import org.ambientdynamix.api.contextplugin.ContextListenerInformation;
import org.ambientdynamix.api.contextplugin.ContextPluginRuntime;
import org.ambientdynamix.api.contextplugin.ContextPluginSettings;
import org.ambientdynamix.api.contextplugin.PowerScheme;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Example auto-reactive plug-in that detects the device's battery level.
 *
 * @author shivam
 */
public class SpotifyRuntime extends ContextPluginRuntime {
    private static final int VALID_CONTEXT_DURATION = 60000;
    // Static logging TAG
    private final String TAG = this.getClass().getSimpleName();
    // Our secure context
    private Context context;

    ArrayList<UUID> spotifyListeners;

    private SpotifyBroadcastReceiver spotifyBroadcastReceiver;

    // A BroadcastReceiver variable that is used to receive battery status updates from Android
    private class SpotifyBroadcastReceiver extends BroadcastReceiver {
        final class BroadcastTypes {
            static final String SPOTIFY_PACKAGE = "com.spotify.music";
            static final String PLAYBACK_STATE_CHANGED = SPOTIFY_PACKAGE + ".playbackstatechanged";
            static final String QUEUE_CHANGED = SPOTIFY_PACKAGE + ".queuechanged";
            static final String METADATA_CHANGED = SPOTIFY_PACKAGE + ".metadatachanged";
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // This is sent with all broadcasts, regardless of type. The value is taken from
            // System.currentTimeMillis(), which you can compare to in order to determine how
            // old the event is.
            Log.d(TAG, "New Broadcast Received : " + intent.getAction());
            long timeSentInMs = intent.getLongExtra("timeSent", 0);

            if (intent.getAction().equals(BroadcastTypes.METADATA_CHANGED)) {
                for (UUID uuid : spotifyListeners) {
                    sendContextEvent(uuid, new MySpotifyInfo(intent),VALID_CONTEXT_DURATION);
                }
                Log.d(TAG, intent.getStringExtra("track"));
            } else if (intent.getAction().equals(BroadcastTypes.PLAYBACK_STATE_CHANGED)) {
                boolean playing = intent.getBooleanExtra("playing", false);
                int positionInMs = intent.getIntExtra("playbackPosition", 0);
                Log.d(TAG, "Playing Status >> " + playing + "\nPosition " + positionInMs);
            } else if (intent.getAction().equals(BroadcastTypes.QUEUE_CHANGED)) {
                // Sent only as a notification, your app may want to respond accordingly.
            }
        }
    }

    /**
     * Called once when the ContextPluginRuntime is first initialized. The implementing subclass should acquire the
     * resources necessary to run. If initialization is unsuccessful, the plug-ins should throw an exception and release
     * any acquired resources.
     */
    @Override
    public void init(PowerScheme powerScheme, ContextPluginSettings settings) throws Exception {
        // Set the power scheme
        this.setPowerScheme(powerScheme);
        // Store our secure context
        this.context = this.getSecuredContext();
    }

    /**
     * Called by the Dynamix Context Manager to start (or prepare to start) context sensing or acting operations.
     */
    @Override
    public void start() {
        Log.d(TAG, "Started!");
        spotifyListeners = new ArrayList<UUID>();
    }

    /**
     * Called by the Dynamix Context Manager to stop context sensing or acting operations; however, any acquired
     * resources should be maintained, since start may be called again.
     */
    @Override
    public void stop() {
        // Unregister battery level changed notifications
        try {
            context.unregisterReceiver(spotifyBroadcastReceiver);
        } catch (IllegalArgumentException e) {

        }
        Log.d(TAG, "Stopped!");
    }

    /**
     * Stops the runtime (if necessary) and then releases all acquired resources in preparation for garbage collection.
     * Once this method has been called, it may not be re-started and will be reclaimed by garbage collection sometime
     * in the indefinite future.
     */
    @Override
    public void destroy() {
        this.stop();
        context = null;
        Log.d(TAG, "Destroyed!");
    }

    @Override
    public void handleContextRequest(UUID requestId, String contextType) {

        // Check for proper context type
        if (contextType.equals(MySpotifyInfo.CONTEXT_TYPE)) {
            if (spotifyBroadcastReceiver == null) {
                spotifyBroadcastReceiver = new SpotifyBroadcastReceiver();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("com.spotify.music.playbackstatechanged");
                intentFilter.addAction("com.spotify.music.metadatachanged");
                intentFilter.addAction("com.spotify.music.queuechanged");
                context.registerReceiver(spotifyBroadcastReceiver, intentFilter);
                Log.i(TAG, "Registering receiver");
            } else {
                Log.i(TAG, "Spotify broadcast receiver already registered");
            }
            sendContextRequestSuccess(requestId);
        } else {
            Log.e(TAG, "Unsupported context type : " + contextType);
        }
    }

    @Override
    public void handleConfiguredContextRequest(UUID requestId, String contextType, Bundle config) {
        // Warn that we don't handle configured requests
        Log.w(TAG, "handleConfiguredContextRequest called, but we don't support configuration!");
        // Drop the config and default to handleContextRequest
        handleContextRequest(requestId, contextType);
    }

    @Override
    public void updateSettings(ContextPluginSettings settings) {
        // Not supported
    }

    @Override
    public void setPowerScheme(PowerScheme scheme) {
        // Not supported
    }


    @Override
    public boolean addContextlistener(ContextListenerInformation listenerInfo) {
        Log.d(TAG, "Saving spotify listener with id >> " + listenerInfo.getListenerId());
        spotifyListeners.add(listenerInfo.getListenerId());
        return true;
    }

}
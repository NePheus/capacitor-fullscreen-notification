package nepheus.capacitor.fullscreennotification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class MessagingService extends Service {
    public static final int NOTIFICATION_ID = 33330;
    private static BroadcastReceiver receiver;
    private static final int pendingIntentRequestCode = 33331;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        registerReceiver();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (Build.VERSION.SDK_INT >= 26 && intent != null) {
            String channelId = intent.getStringExtra("notificationChannelId");
            String channelName = intent.getStringExtra("notificationChannelName");
            NotificationChannel channel = new NotificationChannel(channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_LOW);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, channelId)
                    .setContentTitle("")
                    .setContentText("")
                    .build();

            startForeground(1, notification);
        }

        return START_STICKY;
    }

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                RemoteMessage remoteMessage = intent.getParcelableExtra("remoteMessage");
                Map data = remoteMessage.getData();
                String fullScreenId = data.get("fullScreenId") != null ? data.get("fullScreenId").toString() : null;

                if (fullScreenId != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

                    // Create settings
                    String title = data.get("title") != null ? data.get("title").toString() : null;
                    String text = data.get("text") != null ? data.get("text").toString() : null;
                    Integer timeout = data.get("timeout") != null ? Integer.parseInt(data.get("timeout").toString()) : null;
                    JSONArray actionButtons = new JSONArray();
                    if (data.get("actionButtons") != null) {
                        try {
                            actionButtons = new JSONArray(data.get("actionButtons").toString());
                        } catch (JSONException e) {
                            Logger.error("Could not deserialize buttons", e);
                        }
                    }

                    // Create channel
                    NotificationChannel channel = createChannel(manager, data);

                    // Create notification
                    JSObject notificationIntentData = new JSObject();
                    notificationIntentData.put("fullScreenId", fullScreenId);
                    if (timeout != null) {
                        notificationIntentData.put("timeout", timeout);
                    }
                    NotificationCompat.Builder notification = new NotificationCompat.Builder(context.getApplicationContext(), channel.getId())
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_CALL)
                            .setSmallIcon(context.getResources().getIdentifier("ic_launcher", "mipmap", context.getPackageName()))
                            .setContentTitle(title)
                            .setContentText(text)
                            .setAutoCancel(true)
                            .setOngoing(true)
                            .setFullScreenIntent(createPendingIntent(context, pendingIntentRequestCode, notificationIntentData), true);

                    if (timeout != null) {
                        notification.setTimeoutAfter(timeout);
                    }

                    // Add action buttons to notification
                    for (int i = 0; i < actionButtons.length(); i++) {
                        try {
                            JSONObject actionButton = actionButtons.getJSONObject(i);
                            JSObject actionIntentData = new JSObject();
                            actionIntentData.put("fullScreenId", fullScreenId);
                            if (timeout != null) {
                                actionIntentData.put("timeout", timeout);
                            }
                            actionIntentData.put("actionId", actionButton.getString("id"));
                            notification.addAction(
                                    0,
                                    actionButton.getString("text"),
                                    createPendingIntent(context, pendingIntentRequestCode + i + 1, actionIntentData)
                            );
                        } catch (JSONException e) {
                            Logger.error("Could not add action button", e);
                        }
                    }

                    manager.notify(NOTIFICATION_ID, notification.build());
                }
            }
        };

        IntentFilter filter = new IntentFilter(getResources().getString(R.string.intent_filter_action_pushnotification));
        registerReceiver(receiver, filter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationChannel createChannel(NotificationManager manager, Map data) {
        String channelId = data.get("channelId") != null ? data.get("channelId").toString() : null;
        String channelName = data.get("channelName") != null ? data.get("channelName").toString() : null;
        String channelDescription = data.get("channelDescription") != null ? data.get("channelDescription").toString() : null;
        String vibrationPatternString = data.get("vibrationPattern") != null ? data.get("vibrationPattern").toString() : null;

        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(channelDescription);

        if (vibrationPatternString != null) {
            try {
                ArrayList<Long> s = new ArrayList<Long>(Arrays.asList());
                JSONArray arr = new JSONArray(vibrationPatternString);
                for (int i = 0; i < arr.length(); i++) {
                    s.add(arr.getLong(i));
                }
                long[] pattern = new long[s.size()];
                for (int i = 0; i < s.size(); i++) {
                    pattern[i] = s.get(i);
                }
                channel.setVibrationPattern(pattern);
            } catch (JSONException e) {
                Logger.error("Could not deserialize vibrationPattern", e);
            }
        }

        manager.createNotificationChannel(channel);

        return channel;
    }

    private PendingIntent createPendingIntent(Context context, int requestCode, JSONObject data) {
        Intent intent = getPackageManager()
                .getLaunchIntentForPackage(getPackageName())
                .setPackage(null)
                .setAction(getResources().getString(R.string.intent_filter_action_call))
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.replaceExtras(new Bundle());
        if (data != null) {
            Iterator<String> keys = data.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                try {
                    intent.putExtra(key, data.get(key).toString());
                } catch (JSONException e) {
                    Logger.error("Could not put '" + key + "' to intent extras", e);
                }
            }
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
}

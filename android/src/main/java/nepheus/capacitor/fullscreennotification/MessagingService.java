package nepheus.capacitor.fullscreennotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MessagingService extends com.capacitorjs.plugins.pushnotifications.MessagingService {
    public static final int NOTIFICATION_ID = 33330;
    private static final int PENDING_INTENT_REQUEST_CODE = 33331;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        this.processPush(remoteMessage);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    private void processPush(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String fullScreenId = data.get("fullScreenId") != null ? Objects.requireNonNull(data.get("fullScreenId")) : null;

        if (!TextUtils.isEmpty(fullScreenId) && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Create settings
            String title = data.get("title") != null ? Objects.requireNonNull(data.get("title")) : null;
            String text = data.get("text") != null ? Objects.requireNonNull(data.get("text")) : null;
            Integer timeout = data.get("timeout") != null ? Integer.parseInt(Objects.requireNonNull(data.get("timeout"))) : null;
            JSONArray actionButtons = new JSONArray();
            if (data.get("actionButtons") != null) {
                try {
                    actionButtons = new JSONArray(Objects.requireNonNull(data.get("actionButtons")));
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
            NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), channel.getId())
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setSmallIcon(getResources().getIdentifier("ic_launcher", "mipmap", getPackageName()))
                    .setContentTitle(title)
                    .setContentText(text)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setFullScreenIntent(createPendingIntent(this, PENDING_INTENT_REQUEST_CODE, notificationIntentData), true);

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
                            createPendingIntent(this, PENDING_INTENT_REQUEST_CODE + i + 1, actionIntentData)
                    );
                } catch (JSONException e) {
                    Logger.error("Could not add action button", e);
                }
            }

            manager.notify(NOTIFICATION_ID, notification.build());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationChannel createChannel(NotificationManager manager, Map<String, String> data) {
        String channelId = data.get("channelId") != null ? Objects.requireNonNull(data.get("channelId")) : null;
        String channelName = data.get("channelName") != null ? Objects.requireNonNull(data.get("channelName")) : null;
        String channelDescription = data.get("channelDescription") != null ? Objects.requireNonNull(data.get("channelDescription")) : null;
        String vibrationPatternString = data.get("vibrationPattern") != null ? Objects.requireNonNull(data.get("vibrationPattern")) : null;

        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(channelDescription);

        if (vibrationPatternString != null) {
            try {
                ArrayList<Long> s = new ArrayList<>(List.of());
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
        Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName())
                .setPackage(null)
                .setAction(context.getResources().getString(R.string.intent_filter_action_fullscreennotification_trigger))
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
        return PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
}

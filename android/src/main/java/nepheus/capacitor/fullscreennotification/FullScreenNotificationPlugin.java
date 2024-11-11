package nepheus.capacitor.fullscreennotification;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.service.notification.StatusBarNotification;

import androidx.activity.result.ActivityResult;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.ActivityCallback;

@CapacitorPlugin(name = "FullScreenNotification")
public class FullScreenNotificationPlugin extends Plugin {
    @PluginMethod
    public void cancelNotification(PluginCall call) {
        ((NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE)).cancel(MessagingService.NOTIFICATION_ID);
        call.resolve();
    }

    @PluginMethod
    public void canUseFullScreenIntent(PluginCall call) {
        JSObject ret = new JSObject();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ret.put("result", ((NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE)).canUseFullScreenIntent());
        }
        call.resolve(ret);
    }

    @PluginMethod
    public void openFullScreenIntentSettings(PluginCall call) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            Intent intent = new Intent(
                    android.provider.Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT,
                    Uri.parse("package:" + getActivity().getPackageName())
            );
            startActivityForResult(call, intent, "activityResult");
        } else {
            JSObject ret = new JSObject();
            ret.put("result", false);
            call.resolve(ret);
        }
    }

    @ActivityCallback
    private void activityResult(PluginCall call, ActivityResult result) {
        JSObject ret = new JSObject();
        ret.put("result", true);
        call.resolve(ret);
    }

    /**
     * Listen for our intent action
     *
     */
    @Override
    protected void handleOnNewIntent(Intent intent) {
        super.handleOnNewIntent(intent);

        if (intent.getAction().equals(getContext().getResources().getString(R.string.intent_filter_action_fullscreennotification_trigger))) {
            JSObject ret = new JSObject();
            ret.put("fullScreenId", intent.getStringExtra("fullScreenId"));
            NotificationManager manager = ((NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE));
            StatusBarNotification[] notifications = manager.getActiveNotifications();
            boolean isNotificationActive = false;
            for (StatusBarNotification notification : notifications) {
                if (notification.getId() == MessagingService.NOTIFICATION_ID) {
                    isNotificationActive = true;
                    break;
                }
            }
            ret.put("isNotificationActive", isNotificationActive);
            String timeoutString = intent.getStringExtra("timeout");
            if (timeoutString != null) {
                ret.put("timeout", Integer.parseInt(timeoutString));
            }
            String actionId = intent.getStringExtra("actionId");
            if (actionId != null) {
                ret.put("actionId", actionId);
            }
            notifyListeners("launch", ret, true);
        }
    }
}

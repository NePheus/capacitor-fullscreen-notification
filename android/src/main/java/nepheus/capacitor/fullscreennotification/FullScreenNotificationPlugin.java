package nepheus.capacitor.fullscreennotification;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.service.notification.StatusBarNotification;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "FullScreenNotification")
public class FullScreenNotificationPlugin extends Plugin {
    @PluginMethod
    public void cancelNotification(PluginCall call) {
        ((NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE)).cancel(MessagingService.NOTIFICATION_ID);
        call.resolve();
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

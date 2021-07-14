package nepheus.capacitor.fullscreennotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class MessagingServiceStarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent serviceIntent = new Intent(context, MessagingService.class);
            serviceIntent.putExtra("notificationChannelId", "FullScreenNotificationForegroundChannel");
            serviceIntent.putExtra("notificationChannelName", "Fullscreen notifications");
            context.startForegroundService(serviceIntent);
        }
    }
}
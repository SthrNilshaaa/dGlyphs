package org.duhen.dglyphs;

import android.content.SharedPreferences;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class GlyphNotificationListener extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (!sbn.isClearable()) return;

        SharedPreferences prefs = getSharedPreferences(getString(R.string.pref_file), MODE_PRIVATE);

        if (!prefs.getBoolean("master_allow", false)) return;
        if (SleepGuard.isBlocked(prefs)) return;
        if (prefs.getBoolean("lockscreen_only", false) && GlyphManager.isUserActive(this)) return;

        String style = prefs.getString(MainActivity.PREF_BLINK_STYLE, null);
        if (style == null || style.isEmpty()) {
            try {
                String[] files = getAssets().list("notification");
                if (files == null || files.length == 0) return;
                for (String f : files) {
                    if (f.endsWith(".csv")) {
                        style = f.replace(".csv", "");
                        break;
                    }
                }
            } catch (Exception e) {
                return;
            }
        }
        int brightness = prefs.getInt("brightness", 2048);
        android.os.Vibrator vibrator = getSystemService(android.os.Vibrator.class);
        GlyphEffects.play(this, "notification", style, vibrator, brightness);
    }
}
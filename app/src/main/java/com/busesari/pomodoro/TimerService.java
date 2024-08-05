package com.busesari.pomodoro;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public class TimerService {

    private Context context;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    // Context yerine Activity yerine genel Context kullanımı daha uygun olabilir
    public TimerService(Context context) {
        this.context = context.getApplicationContext(); // Daha genel bir Context kullanımı
    }

    public void startTimer(final int durationInSeconds) {
        final int[] secondsRemaining = {durationInSeconds};

        runnable = new Runnable() {
            @Override
            public void run() {
                secondsRemaining[0]--;
                String timeText = String.format("%02d:%02d", secondsRemaining[0] / 60, secondsRemaining[0] % 60);
                NotificationHelper.showNotification(context, timeText);
                if (secondsRemaining[0] > 0) {
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(runnable);
    }

    public void stopTimer() {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
        // Clear notification when timer stops
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(1); // Ensure ID matches the one used for notification
        }
    }
}

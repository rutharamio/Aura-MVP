package com.example.aura.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.example.aura.services.EmergencyService;

public class PowerButtonReceiver extends BroadcastReceiver {
    private static final String TAG = "PowerButtonReceiver";

    // Para conteo de pulsaciones
    private static int count = 0;
    private static final long WINDOW_MS = 2000; // 2 segundos para agrupar
    private static Handler handler = new Handler();
    private static final Runnable reset = () -> count = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "onReceive: " + action);
        if (Intent.ACTION_SCREEN_OFF.equals(action) || Intent.ACTION_SCREEN_ON.equals(action)) {
            count++;
            handler.removeCallbacks(reset);
            handler.postDelayed(reset, WINDOW_MS);

            if (count >= 3) { // triple press
                Log.d(TAG, "Triple press detectado -> iniciar EmergencyService");
                // arrancar servicio en foreground
                Intent svc = new Intent(context, EmergencyService.class);
                svc.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startForegroundService(svc);
                // reset
                count = 0;
            }
        }
    }
}


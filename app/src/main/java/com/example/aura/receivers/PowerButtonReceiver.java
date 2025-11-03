package com.example.aura.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.example.aura.services.EmergencyService;

public class PowerButtonReceiver extends BroadcastReceiver {
    private static final String TAG = "PowerButtonReceiver";

    private static int pressCount = 0;
    private static final long TIME_WINDOW = 1500; // 1.5 segundos
    private static Handler handler = new Handler();

    private static final Runnable resetCounter = () -> pressCount = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "Acción detectada: " + action);

        if (Intent.ACTION_USER_PRESENT.equals(action)) {
            pressCount++;
            handler.removeCallbacks(resetCounter);
            handler.postDelayed(resetCounter, TIME_WINDOW);

            if (pressCount >= 3) {
                Log.d(TAG, "Triple desbloqueo detectado → Iniciando servicio");
                Intent svc = new Intent(context, EmergencyService.class);
                svc.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startForegroundService(svc);
                pressCount = 0;
            }
        }
    }
}

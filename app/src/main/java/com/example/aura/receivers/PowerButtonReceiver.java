package com.example.aura.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.example.aura.services.EmergencyService;

public class PowerButtonReceiver extends BroadcastReceiver {
    private static final String TAG = "PowerButtonReceiver";

    private static int count = 0;
    private static final long WINDOW_MS = 2500; // 2.5s para agrupar
    private static final Handler handler = new Handler();
    private static final Runnable reset = () -> count = 0;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {

            count++;
            Log.d(TAG, "Pantalla apagada. Conteo: " + count);

            handler.removeCallbacks(reset);
            handler.postDelayed(reset, WINDOW_MS);

            if (count >= 3) {
                Log.d(TAG, "Triple apagado detectado → iniciar EmergencyService");

                Intent svc = new Intent(context, EmergencyService.class);
                svc.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                try {
                    context.startForegroundService(svc); // Android 8+
                } catch (Exception e) {
                    Log.e(TAG, "Error iniciando servicio", e);
                }

                count = 0;
            }
        }
    }
}

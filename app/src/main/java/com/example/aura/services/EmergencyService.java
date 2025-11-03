package com.example.aura.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.aura.R;
import com.example.aura.utils.WhatsAppUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import android.location.Location;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;

public class EmergencyService extends Service {

    public static final String TAG = "EmergencyService";
    public static final String CHANNEL_ID = "aura_emergency_channel";
    private static final int NOTIF_ID = 1001;

    private FusedLocationProviderClient fusedLocationClient;

    // CAMBIAR ESTE NÚMERO POR EL QUE VAS A USAR (CON +595)
    private static final String EMERGENCY_NUMBER = "+595985706930"; // <<--- revisá

    @Override
    public void onCreate() {
        super.onCreate();
        createChannel();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        startForeground(NOTIF_ID, buildNotification("Emergencia activada", "Obteniendo ubicación..."));

        fetchLocationAndSendAlerts();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                    CHANNEL_ID,
                    "Aura - Servicio de emergencia",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(ch);
        }
    }

    private Notification buildNotification(String title, String text) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_alert) // si no tenés, usa ic_dialog_alert
                .setOngoing(true)
                .build();
    }

    private void fetchLocationAndSendAlerts() {

        // verificar permisos
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            updateNotification("Permiso requerido", "Activa permiso de ubicación.");
            return;
        }

        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> t) {

                double lat = 0;
                double lon = 0;
                boolean gotLocation = false;

                if (t.isSuccessful() && t.getResult() != null) {
                    Location location = t.getResult();
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                    Log.d(TAG, "Ubicación: " + lat + ", " + lon);
                    gotLocation = true;
                }

                String message = gotLocation
                        ? "¡EMERGENCIA! Esta es mi ubicación: https://maps.google.com/?q=" + lat + "," + lon
                        : "¡EMERGENCIA! No pude obtener mi ubicación, revisame por favor.";

                sendSms(message);

                // enviar WhatsApp solo si se obtuvo ubicación
                if (gotLocation) {
                    WhatsAppUtils.sendAlert(getApplicationContext(), EMERGENCY_NUMBER, lat, lon);
                }

                updateNotification("Alerta enviada", "SMS y mensaje listo en WhatsApp ✅");
            }
        });
    }

    private void sendSms(String message) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            updateNotification("Error", "Permiso para SMS no concedido.");
            return;
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(EMERGENCY_NUMBER, null, message, null, null);
            Log.d(TAG, "SMS enviado: " + message);
        } catch (Exception e) {
            Log.e(TAG, "Error al enviar SMS", e);
            updateNotification("Error", "No se pudo enviar el SMS.");
        }
    }

    private void updateNotification(String title, String text) {
        NotificationManager nm = getSystemService(NotificationManager.class);
        if (nm != null)
            nm.notify(NOTIF_ID, buildNotification(title, text));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Servicio finalizado");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

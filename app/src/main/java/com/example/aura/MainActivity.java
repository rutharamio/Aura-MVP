package com.example.aura;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.aura.databinding.ActivityMainBinding;
import com.example.aura.receivers.PowerButtonReceiver;
import com.example.aura.services.EmergencyService;
import com.example.aura.ui.PermissionActivity;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private PowerButtonReceiver dynamicReceiver;
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setContentView(R.layout.activity_main);


        binding.btnRequestPermissions.setOnClickListener(v -> {
            startActivity(new Intent(this, PermissionActivity.class));
        });

        binding.btnRegisterReceiver.setOnClickListener(v -> {
            // Registro dinámico para pruebas (no modifica Manifest)
            if (dynamicReceiver == null) {
                dynamicReceiver = new PowerButtonReceiver();
                IntentFilter filter = new IntentFilter();
                filter.addAction(Intent.ACTION_SCREEN_ON);
                filter.addAction(Intent.ACTION_SCREEN_OFF);
                registerReceiver(dynamicReceiver, filter);
                Toast.makeText(this, "Receiver registrado (modo test)", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Receiver registrado dinámicamente (test)");
            } else {
                try {
                    unregisterReceiver(dynamicReceiver);
                } catch (Exception e) { /* ignore */ }
                dynamicReceiver = null;
                Toast.makeText(this, "Receiver desregistrado", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Receiver desregistrado (test)");
            }
        });

        binding.btnSimulateAlert.setOnClickListener(v -> {
            // Inicia el servicio para simular la alerta (emulador / debug)
            Intent i = new Intent(this, EmergencyService.class);
            try {
                startService(i);
                Toast.makeText(this, "Servicio de emergencia iniciado (simulación)", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "EmergencyService started (sim)");
            } catch (Exception ex) {
                // Si Android requiere startForegroundService en tu API:
                startForegroundService(i);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dynamicReceiver != null) {
            try { unregisterReceiver(dynamicReceiver); } catch (Exception e) {}
            dynamicReceiver = null;
        }
    }
}

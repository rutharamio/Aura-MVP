package com.example.aura.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.util.Log;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.aura.R;
import com.example.aura.services.EmergencyService;

public class EmergencyModuleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_module);

        Button btnStartService = findViewById(R.id.btnStartService);
        btnStartService.setOnClickListener(v -> {
            Log.d("EmergencyModule", "Botón presionado, intentando iniciar servicio");
            Intent serviceIntent = new Intent(this, EmergencyService.class);
            ContextCompat.startForegroundService(this, serviceIntent);
        });
    }
}



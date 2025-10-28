package com.example.aura;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aura.core.Prefs;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etName, etPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        etName  = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);

        findViewById(R.id.btnContinue).setOnClickListener(v -> onContinue());
        findViewById(R.id.btnSkip).setOnClickListener(v -> goToMain());
    }

    private void onContinue() {
        String name  = etName.getText()  == null ? "" : etName.getText().toString().trim();
        String phone = etPhone.getText() == null ? "" : etPhone.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Ingresá tu nombre", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!phone.isEmpty() && !Patterns.PHONE.matcher(phone).matches()) {
            Toast.makeText(this, "Teléfono inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        Prefs.saveProfile(this, name, phone);
        goToMain();
    }

    private void goToMain() {
        startActivity(new android.content.Intent(this, MainActivity.class));
        finish(); // cerrar login para que el back no vuelva acá
    }
}


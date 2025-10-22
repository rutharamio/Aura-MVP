package com.example.aura.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import com.example.aura.data.AppDatabase;
import com.example.aura.data.entities.Contact;
import com.example.aura.databinding.ActivityAddContactBinding;

import java.util.List;

public class AddContactActivity extends AppCompatActivity {

    private ActivityAddContactBinding binding;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddContactBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Crear o abrir la base de datos Room (singleton)
        db = com.example.aura.data.AppDatabaseSingleton.getInstance(this);

        binding.saveButton.setOnClickListener(v -> {
            String name = binding.nameInput.getText().toString();
            String phone = binding.phoneInput.getText().toString();
            String relation = binding.relationInput.getText().toString();
            String priority = binding.priorityInput.getText().toString();

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Completá los campos obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            // Mostrar confirmación antes de guardar
            new AlertDialog.Builder(this)
                    .setTitle("Guardar contacto")
                    .setMessage("¿Deseas guardar este contacto de emergencia?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        // Verificar cantidad actual de contactos
                        List<Contact> allContacts = db.contactDao().getAllContacts();
                        if (allContacts.size() >= 5) {
                            Toast.makeText(this, "Solo se permiten 5 contactos de emergencia", Toast.LENGTH_LONG).show();
                            return;
                        }

                        try {
                            Contact contact = new Contact();
                            contact.name = name;
                            contact.phone = phone;
                            contact.relation = relation;
                            contact.priority = priority;

                            db.contactDao().insert(contact);

                            int total = db.contactDao().getAllContacts().size();
                            Log.d("ROOM_TEST", "Contacto insertado: " + name);
                            Log.d("ROOM_TEST", "Total de contactos en DB: " + total);

                            Toast.makeText(this, "Contacto guardado correctamente", Toast.LENGTH_SHORT).show();
                            finish();

                        } catch (Exception e) {
                            Log.e("ROOM_ERROR", "Error al insertar contacto: " + e.getMessage());
                            Toast.makeText(this, "Error al guardar contacto", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }
}

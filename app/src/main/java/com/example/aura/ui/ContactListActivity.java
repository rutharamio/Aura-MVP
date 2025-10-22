package com.example.aura.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.aura.data.AppDatabaseSingleton;
import com.example.aura.data.entities.Contact;
import com.example.aura.databinding.ActivityContactListBinding;
import com.example.aura.ui.adapters.ContactAdapter;
import java.util.List;

public class ContactListActivity extends AppCompatActivity {

    private ActivityContactListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        updateList();

        // Botón para agregar contacto
        binding.fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddContactActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    private void updateList() {
        var db = AppDatabaseSingleton.getInstance(this);
        List<Contact> contactList = db.contactDao().getAllContacts();

        binding.recyclerContacts.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerContacts.setAdapter(new ContactAdapter(contactList, this));

        updateEmptyState(contactList);

        // Ocultar el botón si hay 5 o más contactos
        if (contactList.size() >= 5) {
            binding.fabAdd.hide();
        } else {
            binding.fabAdd.show();
        }
    }

    private void updateEmptyState(List<Contact> list) {
        if (list == null || list.isEmpty()) {
            binding.emptyText.setVisibility(android.view.View.VISIBLE);
        } else {
            binding.emptyText.setVisibility(android.view.View.GONE);
        }
    }
}

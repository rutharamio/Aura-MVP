package com.example.aura.ui.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aura.R;
import com.example.aura.data.AppDatabaseSingleton;
import com.example.aura.data.entities.Contact;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private final List<Contact> contactList;
    private final Context context;

    public ContactAdapter(List<Contact> contactList, Context context) {
        this.contactList = contactList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.tvName.setText(contact.name);
        holder.tvPhone.setText(contact.phone);
        holder.tvRelation.setText(contact.relation);

        // Mantener presionado para eliminar contacto
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Eliminar contacto")
                    .setMessage("¿Deseas eliminar a " + contact.name + "?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        AppDatabaseSingleton.getInstance(context).contactDao().delete(contact);
                        contactList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Contacto eliminado", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvRelation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvRelation = itemView.findViewById(R.id.tvRelation);
        }
    }
}

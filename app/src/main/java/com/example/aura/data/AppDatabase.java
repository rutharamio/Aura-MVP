package com.example.aura.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.aura.data.dao.ContactDao;
import com.example.aura.data.entities.Contact;
import com.example.aura.ReportDao;
import com.example.aura.ReportEntity;

/**
 * Base de datos unificada de Aura.
 * Contiene:
 *  - Contactos de emergencia (Ruth)
 *  - Reportes de zonas inseguras (Ana)
 */
@Database(
        entities = {Contact.class, ReportEntity.class},
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    // DAOs de cada módulo
    public abstract ContactDao contactDao();
    public abstract ReportDao reportDao();

    // Singleton para acceder a la DB
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "aura.db"
                            )
                            .fallbackToDestructiveMigration() // evita errores de versión
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

package com.example.aura.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "user")
public class User {

    @PrimaryKey(autoGenerate = true)
    private int id; // Identificador único del usuario (autoincremental)

    @ColumnInfo(name = "email")
    private String email; // Correo electrónico del usuario

    @ColumnInfo(name = "name")
    private String name; // Nombre del usuario

    @ColumnInfo(name = "password")
    private String password; // Contraseña del usuario (asegurarse de cifrarla antes de guardar)

    // Constructor
    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

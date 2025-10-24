package com.example.aura.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.aura.data.entities.User;

@Dao
public interface UserDao {

    @Insert
    void insert(User user);  // Insertar un nuevo usuario

    @Query("SELECT * FROM user WHERE email = :email AND password = :password")
    User getUserByEmailAndPassword(String email, String password);  // Verificar credenciales

    @Query("SELECT * FROM user LIMIT 1")
    User getCurrentUser();  // Obtener el usuario actual
}

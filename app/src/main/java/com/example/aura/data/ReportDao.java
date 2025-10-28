package com.example.aura.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReportDao {
    @Insert
    long insert(ReportEntity report);

    @Query("SELECT * FROM reports ORDER BY createdAt DESC")
    List<ReportEntity> getAll();

    // Opcional: por proximidad (1 km ~ 0.009 deg aprox. en lat)
    @Query("SELECT * FROM reports WHERE lat BETWEEN :minLat AND :maxLat AND lon BETWEEN :minLon AND :maxLon")
    List<ReportEntity> getInBoundingBox(double minLat, double maxLat, double minLon, double maxLon);
}


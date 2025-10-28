package com.example.aura;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aura.core.Prefs;                  // <- si aún no lo tienes, quita estas dos líneas
import com.example.aura.utils.HaversineUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gmap;
    private FusedLocationProviderClient fused;
    private static final int REQ_LOCATION = 1001;

    // ===== Reportes simulados (para la vista de “cercanos”) =====
    private static class Report {
        final LatLng pos; final String title;
        Report(double lat, double lon, String t){ pos = new LatLng(lat, lon); title = t; }
    }

    private final List<Report> mockReports = Arrays.asList(
            new Report(-25.2815, -57.6358, "Alerta 1"),
            new Report(-25.2899, -57.6281, "Alerta 2"),
            new Report(-25.3002, -57.6405, "Alerta 3 (lejos)")
    );

    private void showNearbyReports(LatLng me){
        int count = 0;
        for (Report r : mockReports) {
            double d = HaversineUtils.distanceKm(
                    me.latitude, me.longitude, r.pos.latitude, r.pos.longitude);
            if (d <= 1.0) {
                count++;
                gmap.addMarker(new MarkerOptions()
                        .position(r.pos)
                        .title(r.title + " • " + String.format(Locale.US, "%.2f km", d)));
            }
        }
        android.util.Log.d("REPORTS", "Cercanos dibujados: " + count);
    }
    // ===== Fin simulados =====

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Si NO tienes Prefs/LoginActivity aún, borra este bloque:
        if (!Prefs.hasProfile(this)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        findViewById(R.id.btnRecenter).setOnClickListener(v -> {
            if (gmap != null) {
                LatLng asuncion = new LatLng(-25.281, -57.635);
                gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(asuncion, 14f));
            }
        });

        fused = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gmap = googleMap;

        // Controles UI del mapa
        gmap.getUiSettings().setZoomControlsEnabled(true);
        gmap.getUiSettings().setMyLocationButtonEnabled(true);

        // Centro inicial en Asunción
        LatLng asuncion = new LatLng(-25.281, -57.635);
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(asuncion, 13f));
        // Dibuja reportes simulados tomando Asunción como referencia (así “ves algo” desde el inicio)
        showNearbyReports(asuncion);

        // Long-press para crear un reporte nuevo
        gmap.setOnMapLongClickListener(this::showCreateReportDialog);

        // Activa ubicación real (si el permiso está concedido)
        enableMyLocationIfGranted();
    }

    // ----- Permisos + centrado -----
    @SuppressLint("MissingPermission")
    private void enableMyLocationIfGranted() {
        if (gmap == null) return;
        if (androidx.core.content.ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == android.content.pm.PackageManager.PERMISSION_GRANTED) {

            gmap.setMyLocationEnabled(true);
            fetchLastLocationAndCenter();

        } else {
            androidx.core.app.ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQ_LOCATION
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_LOCATION && grantResults.length > 0
                && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            enableMyLocationIfGranted();
        }
    }

    @SuppressLint("MissingPermission")
    private void fetchLastLocationAndCenter() {
        if (androidx.core.content.ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) return;

        fused.getLastLocation().addOnSuccessListener(loc -> {
            LatLng target = (loc != null)
                    ? new LatLng(loc.getLatitude(), loc.getLongitude())
                    : new LatLng(-25.281, -57.635); // fallback Asunción
            if (gmap != null) {
                gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(target, 16f));
                showNearbyReports(target);
            }
        });
    }

    // ----- Diálogo para crear reporte (sin Room por ahora) -----
    private void showCreateReportDialog(LatLng pos) {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Describe por qué es peligroso…");
        input.setMinLines(2);
        input.setMaxLines(4);

        String[] levels = {"Bajo", "Medio", "Alto"};
        final int[] selected = {1}; // 1=bajo

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Nuevo reporte")
                .setView(input)
                .setSingleChoiceItems(levels, 0, (d, which) -> selected[0] = which + 1)
                .setPositiveButton("Guardar", (d, w) -> {
                    String comment = input.getText().toString().trim();
                    saveReport(pos, comment, selected[0]); // ahora sí existe
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Stub temporal: guarda “en memoria” y pinta (más adelante lo cambiamos a Room)
    private void saveReport(LatLng pos, String comment, int severity) {
        if (comment.isEmpty()) comment = "(sin comentario)";
        float hue = BitmapDescriptorFactory.HUE_YELLOW;
        if (severity == 2) hue = BitmapDescriptorFactory.HUE_ORANGE;
        if (severity == 3) hue = BitmapDescriptorFactory.HUE_RED;

        gmap.addMarker(new MarkerOptions()
                .position(pos)
                .title(comment)
                .snippet("Riesgo: " + severity)
                .icon(BitmapDescriptorFactory.defaultMarker(hue)));

        android.widget.Toast.makeText(this, "Reporte creado", android.widget.Toast.LENGTH_SHORT).show();
    }
}





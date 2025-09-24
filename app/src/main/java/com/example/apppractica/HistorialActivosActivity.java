package com.example.apppractica;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class HistorialActivosActivity extends AppCompatActivity {
    //Hecho por Yaxchel
    private RecyclerView recyclerViewHistorial;
    private MaterialCardView cardBotonRegresarHistorial;
    private HistorialAdapter historialAdapter;
    private List<ComputadoraHistorial> listaComputadoras;
    private AdminSQLiteOpenHelper adminDbHelper;

    // Constantes para nombres de la base de datos y columnas
    private static final String DATABASE_NAME = "inventarioActivos.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_INVENTARIO_ACTIVOS = "inventarioActivos";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_AGENCIA = "agencia";
    private static final String COLUMN_EQUIPO = "equipo";
    private static final String COLUMN_ENCARGADO = "encargado";
    private static final String COLUMN_WINDOWS = "windows";
    private static final String COLUMN_RAM = "ram";
    private static final String COLUMN_ANTIVIRUS = "antivirus";
    private static final String COLUMN_IP = "ip";
    private static final String COLUMN_ACTIVO = "activo";
    private static final String COLUMN_FECHA_CAMBIO = "fecha_cambio";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.historial_activos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Iniciar AdminSQLiteOpenHelper
        adminDbHelper = new AdminSQLiteOpenHelper(this, DATABASE_NAME, null, DATABASE_VERSION);

        recyclerViewHistorial = findViewById(R.id.recyclerView_historial);
        recyclerViewHistorial.setLayoutManager(new LinearLayoutManager(this));

        listaComputadoras = new ArrayList<>();
        historialAdapter = new HistorialAdapter(listaComputadoras);
        recyclerViewHistorial.setAdapter(historialAdapter);

        cardBotonRegresarHistorial = findViewById(R.id.card_historial_boton_regresar);
        if (cardBotonRegresarHistorial != null) {
            cardBotonRegresarHistorial.setOnClickListener(v -> {
                finish();
            });
        }

        cargarDatosDelHistorial();
    }

    private void cargarDatosDelHistorial() {
        listaComputadoras.clear();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = adminDbHelper.getReadableDatabase();
            // Obtener todos los registros de la tabla inventarioActivos
            String query = "SELECT i.id, i.equipo, i.activo, i.agencia, i.ip, i.windows, i.ram, i.antivirus, e.nombre AS nombre_encargado, i.fecha_cambio" +
                    " FROM inventarioActivos i" +
                    " LEFT JOIN encargadoEquipo e On i.idencargado = e.idencargado " +
                    " ORDER BY i.fecha_cambio DESC";

            Log.d("HistorialActivity", "Ejecutando consulta: " + query); // Para depuración
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                    String nombreEquipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EQUIPO));
                    String numeroActivo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACTIVO));
                    String agencia = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AGENCIA));
                    String encargado = cursor.getString(cursor.getColumnIndexOrThrow("nombre_encargado"));
                    String ip = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IP));
                    String windows = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WINDOWS));
                    String ram = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RAM));
                    String antivirus = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANTIVIRUS));
                    String fechaCambio = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA_CAMBIO));

                    ComputadoraHistorial computadora = new ComputadoraHistorial(id, nombreEquipo, numeroActivo, agencia, encargado, ip, windows, ram, antivirus, fechaCambio);
                    listaComputadoras.add(computadora);
                } while (cursor.moveToNext());
            } else {
                Toast.makeText(this, "No hay activos registrados.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("HistorialActivity", "Error al cargar datos del historial: " + e.getMessage(), e);
            Toast.makeText(this, "Error al cargar el historial.", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        historialAdapter.actualizarLista(listaComputadoras);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Recargar los datos cuando el activity se reanude,
        // por si se hicieron cambios en otras actividades o se añadió un nuevo registro.
        Log.d("HistorialActivity", "onResume: recargando datos del historial.");
        cargarDatosDelHistorial();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adminDbHelper != null) {
            adminDbHelper.close();
            Log.d("HistorialActivity", "AdminSQLiteOpenHelper cerrado en onDestroy.");
        }
    }
}
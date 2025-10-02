package com.example.apppractica;

import android.annotation.SuppressLint;
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
    private List<ComputadoraHistorial> listaHistorial;
    private AdminSQLiteOpenHelper adminDbHelper;

    // Constantes para nombres de la base de datos y columnas
    private static final String DATABASE_NAME = "inventarioActivos.db";
    private static final int DATABASE_VERSION = 6;

    private static final String COLUMN_ID="id";




    private static final String COLUMN_EQUIPO = "equipo";
    private static final String COLUMN_ACTIVO = "activo";
    private static final String COLUMN_IP = "ip";
    private static final String COLUMN_WINDOWS = "windows";
    private static final String COLUMN_RAM = "ram";
    private static final String COLUMN_ANTIVIRUS = "antivirus";
    private static final String COLUMN_FECHA_CAMBIO = "fecha_cambio";

    private static final String COLUMN_TIPO = "tipo";
    private static final String COLUMN_MARCA = "marca";
    private static final String COLUMN_MODELO = "modelo";
    private static final String COLUMN_SERIE = "serie";
    private static final String COLUMN_ESTADO = "estado";
    private static final String COLUMN_FECHA_REGISTRO = "fecha_registro";
    private static final String COLUMN_FECHA_ADQUISICION = "fecha_adquisicion";
    private static final String ALIAS_NOMBRE_AGENCIA_COMPUTADORA ="nombre_agencia_computadora";
            private static final String ALIAS_NOMBRE_AGENCIA_UTILITARIO = "nombre_agencia_utilitario";
            private static final String ALIAS_NOMBRE_ENCARGADO = "nombre_encargado";

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

        listaHistorial = new ArrayList<>();
        historialAdapter = new HistorialAdapter(listaHistorial);
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
        listaHistorial.clear();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = adminDbHelper.getReadableDatabase();

            cargarComputadoras(db);
            cargarUtilitarios(db);
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
        historialAdapter.actualizarLista(listaHistorial);
        if (listaHistorial.isEmpty()) {
            Toast.makeText(this, "No hay activos registrados.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Cargados." + listaHistorial.size() + "registros", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarComputadoras(SQLiteDatabase db) {
        Cursor cursor = null;
        try {

            // Obtener todos los registros de la tabla inventarioActivos
            String query =  "SELECT i.id, i.equipo, i.activo, i.id_agencia, ta.nombre_agencia AS " + ALIAS_NOMBRE_AGENCIA_COMPUTADORA + ", " +
                    "i.ip, i.windows, i.ram, i.antivirus, e.nombre AS " + ALIAS_NOMBRE_ENCARGADO + ", i.fecha_cambio " +
                    "FROM inventarioActivos i " +
                    "LEFT JOIN encargadoEquipo e ON i.idencargado = e.idencargado " +
                    "LEFT JOIN tbAgencias ta ON i.id_agencia = ta.id_agencia " +
                    "ORDER BY i.fecha_cambio DESC";

            Log.d("HistorialActivity", "Ejecutando consulta: " + query); // Para depuración
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                    String nombreEquipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EQUIPO));
                    String numeroActivo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACTIVO));
                    String agencia = cursor.getString(cursor.getColumnIndexOrThrow(ALIAS_NOMBRE_AGENCIA_COMPUTADORA));
                    String encargado = cursor.getString(cursor.getColumnIndexOrThrow(ALIAS_NOMBRE_ENCARGADO));
                    String ip = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IP));
                    String windows = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WINDOWS));
                    String ram = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RAM));
                    String antivirus = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANTIVIRUS));
                    String fechaCambio = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA_CAMBIO));

                    ComputadoraHistorial computadora = new ComputadoraHistorial(id, nombreEquipo, numeroActivo, agencia, encargado, ip, windows, ram, antivirus, fechaCambio);

                    listaHistorial.add(computadora);
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
        }
    }

    private void cargarUtilitarios(SQLiteDatabase db) {
        Cursor cursor = null;
        try {
            String query = "SELECT u.id, u.activo, u.tipo, u.marca, u.modelo, u.serie, " +
                    "u.id_agencia, ta.nombre_agencia AS " + ALIAS_NOMBRE_AGENCIA_UTILITARIO + ", " +
                    "e.nombre AS " + ALIAS_NOMBRE_ENCARGADO + ", u.estado, " +
                    "u.fecha_registro, u.fecha_adquisicion " +
                    "FROM inventarioUtilitarios u " +
                    "LEFT JOIN encargadoEquipo e ON u.idencargado = e.idencargado " +
                    "LEFT JOIN tbAgencias ta ON u.id_agencia = ta.id_agencia " +
                    "ORDER BY u.fecha_registro DESC";

            Log.d("HistorialActivity", "Ejecutando consulta de utilitarios: " + query);
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                    String activo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACTIVO));
                    String tipoUtilitario = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO));
                    String marca = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MARCA));
                    String modelo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODELO));
                    String serie = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERIE));
                    String agencia = cursor.getString(cursor.getColumnIndexOrThrow(ALIAS_NOMBRE_AGENCIA_UTILITARIO));
                    String encargado = cursor.getString(cursor.getColumnIndexOrThrow("nombre_encargado"));
                    String estado = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESTADO));
                    String fechaRegistro = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA_REGISTRO));
                    String fechaAdquisicion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA_ADQUISICION));


                    ComputadoraHistorial utilitario = new ComputadoraHistorial(id, activo, tipoUtilitario, marca, modelo, serie, agencia, encargado, estado, fechaRegistro, fechaAdquisicion);
                    listaHistorial.add(utilitario);

                } while (cursor.moveToNext());
            }else {
                Toast.makeText(this, "No hay activos registrados.", Toast.LENGTH_SHORT).show();
            }

            } catch(Exception e){
                Log.e("HistorialActivity", "Error al cargar datos del historial: " + e.getMessage(), e);

            } finally{
                if (cursor != null) {
                    cursor.close();
                }
            }
        }


        @Override
        protected void onResume () {
            super.onResume();
            // Recargar los datos cuando el activity se reanude,
            // por si se hicieron cambios en otras actividades o se añadió un nuevo registro.
            Log.d("HistorialActivity", "onResume: recargando datos del historial.");
            cargarDatosDelHistorial();
        }

        @Override
        protected void onDestroy () {
            super.onDestroy();
            if (adminDbHelper != null) {
                adminDbHelper.close();
                Log.d("HistorialActivity", "AdminSQLiteOpenHelper cerrado en onDestroy.");
            }
        }
    }

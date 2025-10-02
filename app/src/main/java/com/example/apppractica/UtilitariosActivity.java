package com.example.apppractica;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;


import java.util.ArrayList;
import java.util.Calendar;

public class UtilitariosActivity extends AppCompatActivity {

    private TextInputEditText activo, marca, modelo, serie, encargado, estado, fechaRegistro, fechaAdquisicion;
    private AutoCompleteTextView tipo, agencia;
    private MaterialButton btnGuardar;
    private AdminSQLiteOpenHelper dbHelper;


    private ArrayList<String> listaAgencias;
    private ArrayAdapter<String> adapterAgencias;
    private long idAgenciaSeleccionada =-1;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utilitarios);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar base de datos
        dbHelper = new AdminSQLiteOpenHelper(this, "inventarioActivos.db", null, 6);

        inicializarVistas();
        configurarDesplegable();
        configurarListeners();
    }

        private void inicializarVistas() {

            // Inicializar vistas
            activo = findViewById(R.id.activo_utilitario);
            tipo = findViewById(R.id.tipo_utilitario);
            marca = findViewById(R.id.marca_utilitario);
            modelo = findViewById(R.id.modelo_utilitario);
            serie = findViewById(R.id.serie_utilitario);
            encargado = findViewById(R.id.encargado_utilitario);
            agencia = findViewById(R.id.agencia_utilitario);
            estado = findViewById(R.id.estado_utilitario);
            fechaRegistro = findViewById(R.id.registrar_utilitario);
            fechaAdquisicion = findViewById(R.id.fecha_adquisicion);
            btnGuardar = findViewById(R.id.btn_guardar);




            listaAgencias = new ArrayList<>();
            adapterAgencias = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, listaAgencias);
            agencia.setAdapter(adapterAgencias);
        }

        private void configurarDesplegable() {

            cargarAgencias();

            String[] tipos = {"Lector de DPI", "Impresora", "Digitalizador de Huella", "Escáner", "Lector de Tarjeta", "Digitalizador de Firma"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tipos);
            tipo.setAdapter(adapter);

        }

        private void cargarAgencias() {

            try {
                listaAgencias.clear();
                listaAgencias.addAll(AgenciaHelper.obtenerAgencias(dbHelper));
                adapterAgencias.notifyDataSetChanged();
            } catch (Exception e) {
                Toast.makeText(this, "Error al cargar las agencias", Toast.LENGTH_SHORT).show();
            }
        }

        private void configurarListeners() {
        agencia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String agenciaSeleccionada = (String) parent.getItemAtPosition(position);
                if (agenciaSeleccionada != null) {
                    idAgenciaSeleccionada = AgenciaHelper.obtenerIdAgencia(dbHelper, agenciaSeleccionada);
                } else {
                    idAgenciaSeleccionada = -1;
                }
            }

                });


        // Selección de fechas
        fechaRegistro.setOnClickListener(v -> mostrarDatePicker(fechaRegistro));
        fechaAdquisicion.setOnClickListener(v -> mostrarDatePicker(fechaAdquisicion));

        // Botón guardar
        btnGuardar.setOnClickListener(v -> guardarUtilitario());

        // Botón regresar
        ImageView btnRegresar = findViewById(R.id.btn_regresar);
        btnRegresar.setOnClickListener(v -> finish());
    }

    private void mostrarDatePicker(TextInputEditText campoFecha) {
        Calendar c = Calendar.getInstance();
        int dia = c.get(Calendar.DAY_OF_MONTH);
        int mes = c.get(Calendar.MONTH);
        int anio = c.get(Calendar.YEAR);

        DatePickerDialog dpd = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String fecha = dayOfMonth + "/" + (month + 1) + "/" + year;
            campoFecha.setText(fecha);
        }, anio, mes, dia);
        dpd.show();
    }

    private void guardarUtilitario() {
        String sActivo = activo.getText().toString().trim();
        String sTipo = tipo.getText().toString().trim();
        String sMarca = marca.getText().toString().trim();
        String sModelo = modelo.getText().toString().trim();
        String sSerie = serie.getText().toString().trim();
        String sEncargado = encargado.getText().toString().trim();
        String sAgencia = agencia.getText().toString().trim();
        String sEstado = estado.getText().toString().trim();
        String sFechaRegistro = fechaRegistro.getText().toString().trim();
        String sFechaAdquisicion = fechaAdquisicion.getText().toString().trim();

       if (!validarDatos(sActivo, sTipo, sEncargado)) {
           return;
       }


        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

       try {
           long idEncargado = guardarObtenerEncargado(db, sEncargado);
           if (idEncargado == -1) {
              throw new Exception("Encargado no encontrado");
           }

            if (idAgenciaSeleccionada == -1) {
                agencia.setError("Seleccione una agencia válida");
                        agencia.requestFocus();
                return;
            }



        ContentValues values = new ContentValues();
        values.put("activo", sActivo);
        values.put("tipo", sTipo);
        values.put("marca", sMarca);
        values.put("modelo", sModelo);
        values.put("serie", sSerie);
        values.put("idencargado", idEncargado);
        values.put("id_agencia", idAgenciaSeleccionada);
        values.put("estado", sEstado);
        values.put("fecha_registro", sFechaRegistro.isEmpty()? obtenerFechaActual(): sFechaRegistro);
        values.put("fecha_adquisicion", sFechaAdquisicion);


        long id = db.insert("inventarioUtilitarios", null, values);

        if (id != -1) {
            db.setTransactionSuccessful();
            Toast.makeText(this,"Utilitario guardado correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos();
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
           }

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    private boolean validarDatos(String activo, String tipo, String encargado) {
        if (activo.isEmpty()) {
            this.activo.setError("Este campo es requerido");
            this.activo.requestFocus();
            return false;
        }
        if (tipo.isEmpty()) {
            this.tipo.setError("Este campo es requerido");
            this.tipo.requestFocus();
            return false;
        }
        if (encargado.isEmpty()) {
            this.marca.setError("Este campo es requerido");
            this.marca.requestFocus();
            return false;
        }
        return true;
    }

    private long guardarObtenerEncargado(SQLiteDatabase db, String nombreEncargado)  {
        Cursor cursor = null;
        try{
            cursor = db.rawQuery(
                    "SELECT idencargado FROM encargadoEquipo WHERE nombre = ?",
                    new String[]{nombreEncargado}
            );
            if (cursor.moveToFirst()) {
                long id = cursor.getLong(0);
                return id;
                } else {
                ContentValues values = new ContentValues();
                values.put("nombre", nombreEncargado);
                return db.insert("encargadoEquipo", null, values);
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

        private String obtenerFechaActual() {
            Calendar c = Calendar.getInstance();
            int dia = c.get(Calendar.DAY_OF_MONTH);
            int mes = c.get(Calendar.MONTH) + 1;
            int anio = c.get(Calendar.YEAR);
            return dia + "/" + mes + "/" + anio;
        }


        private void limpiarCampos() {
        activo.setText("");
        tipo.setText("");
        marca.setText("");
        modelo.setText("");
        serie.setText("");
        encargado.setText("");
        agencia.setText("");
        estado.setText("");
        fechaRegistro.setText("");
        fechaAdquisicion.setText("");

        idAgenciaSeleccionada = -1;
    }

}

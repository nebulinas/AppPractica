package com.example.apppractica;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
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

import java.util.Calendar;

public class UtilitariosActivity extends AppCompatActivity {

    private TextInputEditText activo, marca, modelo, serie, encargado, agencia, estado, fechaRegistro, fechaAdquisicion;
    private AutoCompleteTextView tipo;
    private MaterialButton btnGuardar;
    private AdminSQLiteOpenHelper dbHelper;

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
        dbHelper = new AdminSQLiteOpenHelper(this, "basedatos", null, 4);

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

        // Configurar AutoCompleteTextView con tipos de utilitarios
        String[] tipos = {"Herramienta", "Electrónico", "Mobiliario", "Otro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tipos);
        tipo.setAdapter(adapter);

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

        if (sActivo.isEmpty() || sTipo.isEmpty()) {
            Toast.makeText(this, "Debe llenar los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("activo", sActivo);
        values.put("tipo", sTipo);
        values.put("marca", sMarca);
        values.put("modelo", sModelo);
        values.put("serie", sSerie);
        values.put("encargado", sEncargado);
        values.put("agencia", sAgencia);
        values.put("estado", sEstado);
        values.put("fecha_registro", sFechaRegistro);
        values.put("fecha_adquisicion", sFechaAdquisicion);

        long id = db.insert("inventarioUtilitarios", null, values);
        db.close();

        if (id > 0) {
            Toast.makeText(this, "Utilitario guardado correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos();
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
        }
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
    }
}

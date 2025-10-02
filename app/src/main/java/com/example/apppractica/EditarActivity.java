package com.example.apppractica;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EditarActivity extends AppCompatActivity {

    // Campos de búsqueda y edición

    private static final String DATABASE_NAME="inventarioActivos.db";
    private static final int DATABASE_VERSION= 6;

    private TextInputEditText buscarEquipo, nombreEquipo, encargado, numeroActivo, antivirus, ip;
    private AutoCompleteTextView agencia, windows, ram;

    // Botones
    private MaterialButton btnBuscar, btnGuardar, btnCancelar;

    // Base de datos
    private AdminSQLiteOpenHelper adminSQLiteOpenHelper;
    private String equipoId = "";
    private long idAgenciaSeleccionada = -1;


    private ArrayList<String> listaNombresAgencias;
    private ArrayAdapter<String> adapterAgencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar);

        // Inicializar base de datos
        adminSQLiteOpenHelper = new AdminSQLiteOpenHelper(this, "inventarioActivos.db", null, 6);

        // Conectar variables con layout
        conectarVistas();

        // Configurar dropdowns
        configurarListas();

        // Configurar botones
        configurarBotones();


    }

    private void conectarVistas() {
        // Campos de búsqueda
        buscarEquipo = findViewById(R.id.buscar_equipo);

        // Campos de edición
        nombreEquipo = findViewById(R.id.nombre_equipo);
        encargado = findViewById(R.id.encargado);
        numeroActivo = findViewById(R.id.numero_activo);
        antivirus = findViewById(R.id.version_antivirus);
        ip = findViewById(R.id.direccion_ip);

        // Dropdowns
        agencia = findViewById(R.id.agencia_editar);
        windows = findViewById(R.id.version_windows);
        ram = findViewById(R.id.memoria_ram);

        // Botones
        btnBuscar = findViewById(R.id.boton_buscar);
        btnGuardar = findViewById(R.id.boton_guardar);
        btnCancelar = findViewById(R.id.boton_cancelar);
    }

    private void configurarListas() {
        // Lista de agencias
        cargarAgencias();

        agencia.setOnItemClickListener((parent, view, position, id) -> {
                    String agenciaSeleccionada = (String) parent.getItemAtPosition(position);

                    if (agenciaSeleccionada != null) {
                        idAgenciaSeleccionada = AgenciaHelper.obtenerIdAgencia(adminSQLiteOpenHelper, agenciaSeleccionada);
                    } else {
                        idAgenciaSeleccionada = -1;
                    }
                });



        // Lista de Windows
        String[] windowsList = {"Windows 10 Pro", "Windows 11 Pro", "Windows 10 Home", "Windows 11 Home"};
        ArrayAdapter<String> adapterWindows = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, windowsList);
        windows.setAdapter(adapterWindows);

        // Lista de RAM
        String[] ramList = {"2 GB", "4 GB", "8 GB", "16 GB", "32 GB", "64 GB"};
        ArrayAdapter<String> adapterRam = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, ramList);
        ram.setAdapter(adapterRam);
    }

    private void cargarAgencias() {
        ArrayList<String> agenciasList = AgenciaHelper.obtenerAgencias(adminSQLiteOpenHelper);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, agenciasList);
        agencia.setAdapter(adapter);
    }

    private void configurarBotones() {
        // Botón Buscar
        btnBuscar.setOnClickListener(v -> buscarEquipo());

        // Botón Guardar
        btnGuardar.setOnClickListener(v -> guardarCambios());

        // Botón Cancelar
        btnCancelar.setOnClickListener(v -> limpiarFormulario());

        // Botón Regresar
        findViewById(R.id.card_boton_regresar).setOnClickListener(v -> finish());
        findViewById(R.id.boton_regresar).setOnClickListener(v -> finish());
    }

    private void buscarEquipo() {
        String busqueda = buscarEquipo.getText().toString().trim();

        if (busqueda.isEmpty()) {
            Toast.makeText(this, "Ingrese nombre o número de activo", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = adminSQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT i.*, e.nombre as encargado_nombre, a.nombre_agencia " +
                        "FROM inventarioActivos i " +
                        "LEFT JOIN encargadoEquipo e ON i.idencargado = e.idencargado " +
                        "LEFT JOIN tbAgencias a ON i.id_agencia = a.id_agencia " +
                        "WHERE i.equipo LIKE ? OR i.activo LIKE ?",
                new String[]{"%" + busqueda + "%", "%" + busqueda + "%"}
        );

        if (cursor.moveToFirst()) {
            // Llenar formulario con datos encontrados
            llenarFormulario(cursor);
            habilitarEdicion(true);
            Toast.makeText(this, "Equipo encontrado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Equipo no encontrado", Toast.LENGTH_LONG).show();
        }

        cursor.close();
        db.close();
    }

    private void llenarFormulario(Cursor cursor) {
        equipoId = cursor.getString(cursor.getColumnIndexOrThrow("id"));

        nombreEquipo.setText(cursor.getString(cursor.getColumnIndexOrThrow("equipo")));
        encargado.setText(cursor.getString(cursor.getColumnIndexOrThrow("encargado_nombre")));
        numeroActivo.setText(cursor.getString(cursor.getColumnIndexOrThrow("activo")));

        String nombreAgencia = cursor.getString(cursor.getColumnIndexOrThrow("nombre_agencia"));
        agencia.setText(nombreAgencia, false);
        idAgenciaSeleccionada = cursor.getLong(cursor.getColumnIndexOrThrow("id_agencia"));

        windows.setText(cursor.getString(cursor.getColumnIndexOrThrow("windows")), false);
        ram.setText(cursor.getString(cursor.getColumnIndexOrThrow("ram")), false);
        antivirus.setText(cursor.getString(cursor.getColumnIndexOrThrow("antivirus")));
        ip.setText(cursor.getString(cursor.getColumnIndexOrThrow("ip")));
    }

    private void guardarCambios() {
        if (equipoId.isEmpty()) {
            Toast.makeText(this, "Primero busque un equipo", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validarDatos()) {
            return;
        }

        if (idAgenciaSeleccionada == -1) {
            agencia.setError("Seleccione una agencia");
            agencia.requestFocus();
            return;
        }

        SQLiteDatabase db = adminSQLiteOpenHelper.getWritableDatabase();

        try {
            // 1. Guardar o obtener ID del encargado
            String nombreEncargado = encargado.getText().toString().trim();
            long idEncargado = obtenerIdEncargado(db, nombreEncargado);

            // 2. Obtener ID de la agencia


            // 3. Actualizar equipo
            ContentValues datos = new ContentValues();
            datos.put("equipo", nombreEquipo.getText().toString().trim());
            datos.put("idencargado", idEncargado);
            datos.put("id_agencia", idAgenciaSeleccionada);
            datos.put("windows", windows.getText().toString());
            datos.put("ram", ram.getText().toString());
            datos.put("antivirus", antivirus.getText().toString().trim());
            datos.put("ip", ip.getText().toString().trim());
            datos.put("fecha_cambio", obtenerFechaActual());

            int resultado = db.update("inventarioActivos", datos, "id=?", new String[]{equipoId});

            if (resultado > 0) {
                Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show();
                limpiarFormulario();
            } else {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            db.close();
        }
    }

    private long obtenerIdEncargado(SQLiteDatabase db, String nombre) {
        // Buscar si ya existe
        Cursor cursor = db.rawQuery("SELECT idencargado FROM encargadoEquipo WHERE nombre=?",
                new String[]{nombre});

        if (cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            cursor.close();
            return id;
        }
        cursor.close();

        // Si no existe, crear nuevo
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        return db.insert("encargadoEquipo", null, values);
    }

    private long obtenerIdAgencia(SQLiteDatabase db, String nombre) {
        return AgenciaHelper.obtenerIdAgencia(adminSQLiteOpenHelper, nombre);
        }




    private boolean validarDatos() {
        if (nombreEquipo.getText().toString().trim().isEmpty()) {
            mostrarError(nombreEquipo, "Ingrese nombre del equipo");
            return false;
        }

        if (encargado.getText().toString().trim().isEmpty()) {
            mostrarError(encargado, "Ingrese nombre del encargado");
            return false;
        }

        if (agencia.getText().toString().trim().isEmpty()) {
            mostrarError(agencia, "Seleccione una agencia");
            return false;
        }

        return true;
    }

    private void mostrarError(AutoCompleteTextView campo, String mensaje) {
        campo.setError(mensaje);
        campo.requestFocus();
    }

    private void mostrarError(TextInputEditText campo, String mensaje) {
        campo.setError(mensaje);
        campo.requestFocus();
    }

    private void habilitarEdicion(boolean habilitar) {
        nombreEquipo.setEnabled(habilitar);
        encargado.setEnabled(habilitar);
        agencia.setEnabled(habilitar);
        windows.setEnabled(habilitar);
        ram.setEnabled(habilitar);
        antivirus.setEnabled(habilitar);
        ip.setEnabled(habilitar);
        btnGuardar.setEnabled(habilitar);
        btnCancelar.setEnabled(habilitar);
    }

    private void limpiarFormulario() {
        buscarEquipo.setText("");
        nombreEquipo.setText("");
        encargado.setText("");
        numeroActivo.setText("");
        agencia.setText("");
        windows.setText("");
        ram.setText("");
        antivirus.setText("");
        ip.setText("");

        // Limpiar errores
        nombreEquipo.setError(null);
        encargado.setError(null);
        agencia.setError(null);

        equipoId = "";
        idAgenciaSeleccionada = -1;
        habilitarEdicion(false);
    }

    private String obtenerFechaActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
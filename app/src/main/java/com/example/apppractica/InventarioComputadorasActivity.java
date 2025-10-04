package com.example.apppractica;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class InventarioComputadorasActivity extends AppCompatActivity {

    private static final String DATABASE_NAME = "inventarioActivos.db";
    private static final int DATABASE_VERSION = 6;

    private static final String TABLE_INVENTARIO= "inventarioActivos";

    public static final String COLUMN_EQUIPO = "equipo";
    public static final String COLUMN_WINDOWS = "windows";
    public static final String COLUMN_IDENCARGADO = "idencargado";
    public static final String COLUMN_RAM = "ram";
    public static final String COLUMN_ANTIVIRUS = "antivirus";
    public static final String COLUMN_IP = "ip";
    public static final String COLUMN_ACTIVO = "activo";

    public static final String TABLE_ENCARGADO = "encargadoEquipo";
    public static final String COLUMN_ENCARGADO_NOMBRE = "nombre";


    public static final String COLUMN_ID_AGENCIA = "id_agencia";




    private AutoCompleteTextView etNombreAgencia;
    private TextInputEditText etNombreEquipo, etNombreEncargado, etDireccionIp, etNoActivo;
    private AutoCompleteTextView acMemoriaRam, acVersionWindows, acVersionAntivirus;
    private MaterialButton btnGuardar;
    private MaterialCardView cardBotonRegresar;

    private AdminSQLiteOpenHelper adminSQLiteOpenHelper;
    private ArrayList<String> listaAgencias;
    private ArrayAdapter<String> adapterAgencias;
    private long idAgenciaSeleccionada = -1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.inventario_computadoras);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        adminSQLiteOpenHelper = new AdminSQLiteOpenHelper(this, DATABASE_NAME, null, DATABASE_VERSION);

        etNombreAgencia = findViewById(R.id.nombre_agencia);
        etNombreEquipo = findViewById(R.id.nombre_equipo);
        etNombreEncargado = findViewById(R.id.nombre_encargado);
        acVersionWindows = findViewById(R.id.version_windows);
        acMemoriaRam = findViewById(R.id.memoria_ram);
        acVersionAntivirus = findViewById(R.id.version_antivirus);
        etDireccionIp = findViewById(R.id.direccion_ip);
        etNoActivo = findViewById(R.id.no_activo);
        btnGuardar = findViewById(R.id.boton_guardar);
        cardBotonRegresar = findViewById(R.id.card_boton_regresar);


       listaAgencias = new ArrayList<>();
       adapterAgencias = new ArrayAdapter<> (this,
               android.R.layout.simple_dropdown_item_1line, listaAgencias);

        etNombreAgencia.setAdapter(adapterAgencias);
        etNombreAgencia.setThreshold(1);

        cargarAgenciasDropdown();

        etNombreAgencia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String agenciaSeleccionada = (String) parent.getItemAtPosition(position);
                if (agenciaSeleccionada != null) {
                    idAgenciaSeleccionada = AgenciaHelper.obtenerIdAgencia(adminSQLiteOpenHelper, agenciaSeleccionada);
                    Log.d("AgenciaSeleccionada","ID:" + idAgenciaSeleccionada + "Nombre:" + agenciaSeleccionada);
                } else {
                    idAgenciaSeleccionada = -1;
                }
            }
        });

        String[] opcionesRam = {"2 GB", "4 GB", "8 GB", "16 GB", "32 GB", "64 GB"};
        ArrayAdapter<String> adapterRam = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, opcionesRam);
        acMemoriaRam.setAdapter(adapterRam);

        String[] versionesWindows = {"Windows 10 Pro", "Windows 11 Pro", "Windows 10 Home", "Windows 11 Home"};
        ArrayAdapter<String> adapterWindows = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, versionesWindows);
        acVersionWindows.setAdapter(adapterWindows);

        String[] versionAntivirus = {"Eset 12.0", "Eset 10.0"};
        ArrayAdapter<String> adapterAntivirus = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, versionAntivirus);
        acVersionAntivirus.setAdapter(adapterAntivirus);


        btnGuardar.setOnClickListener(v -> capturarYGuardarDatos());

        if (cardBotonRegresar != null) {
            cardBotonRegresar.setOnClickListener(v -> finish());
        }

    }


        private void cargarAgenciasDropdown () {
            listaAgencias.clear();
            listaAgencias.addAll(AgenciaHelper.obtenerAgencias(adminSQLiteOpenHelper));
            adapterAgencias.notifyDataSetChanged();
        }

    private void capturarYGuardarDatos() {
        String equipo = etNombreEquipo.getText().toString().trim();
        String encargado = etNombreEncargado.getText().toString().trim();
        String windows = acVersionWindows.getText().toString().trim();
        String ram = acMemoriaRam.getText().toString().trim();
        String antivirus = acVersionAntivirus.getText().toString().trim();
        String ip = etDireccionIp.getText().toString().trim();
        String activo = etNoActivo.getText().toString().trim();

        if (idAgenciaSeleccionada == -1) {
            etNombreAgencia.setError("Este campo es requerido");
            etNombreAgencia.requestFocus();
            return;
        } else {
            etNombreAgencia.setError(null);

        }


        if (equipo.isEmpty()) {
            etNombreEquipo.setError("Este campo es requerido");
            etNombreEquipo.requestFocus();
            return;
        } else {
            etNombreEquipo.setError(null);
        }


        if (encargado.isEmpty()) {
            etNombreEncargado.setError("Este campo es requerido");
            etNombreEncargado.requestFocus();
            return;
        } else {
            etNombreEncargado.setError(null);
        }

        SQLiteDatabase db = null;
        long idEncargado = -1;
        long idInventario = -1;


        try {
            db = adminSQLiteOpenHelper.getWritableDatabase();
            db.beginTransaction();

            ContentValues valuesEncargado = new ContentValues();
            valuesEncargado.put(COLUMN_ENCARGADO_NOMBRE, encargado);
            idEncargado = db.insertWithOnConflict(TABLE_ENCARGADO, null, valuesEncargado, SQLiteDatabase.CONFLICT_IGNORE);
            if (idEncargado == -1) {
                Cursor cursorEncargado = null;
                try {
                    cursorEncargado = db.query(TABLE_ENCARGADO,
                            new String[]{COLUMN_IDENCARGADO},
                            COLUMN_ENCARGADO_NOMBRE + " = ?",
                            new String[]{encargado},
                            null, null, null);
                    if (cursorEncargado != null && cursorEncargado.moveToFirst()) {
                        idEncargado = cursorEncargado.getLong(cursorEncargado.getColumnIndexOrThrow(COLUMN_IDENCARGADO));
                    }
                } finally {
                    if (cursorEncargado != null) {
                        cursorEncargado.close();
                    }
                }
                if (idEncargado == -1) {
                    throw new Exception("Error al obtener el registro.");
                }
            }


            ContentValues values = new ContentValues();
            values.put(COLUMN_ID_AGENCIA, idAgenciaSeleccionada);
            values.put(COLUMN_EQUIPO, equipo);
            values.put(COLUMN_IDENCARGADO, idEncargado);
            values.put(COLUMN_WINDOWS, windows);
            values.put(COLUMN_RAM, ram);
            values.put(COLUMN_ANTIVIRUS, antivirus);
            values.put(COLUMN_IP, ip);
            values.put(COLUMN_ACTIVO, activo);

            idInventario = db.insert(TABLE_INVENTARIO, null, values);

            if (idInventario == -1) {
                throw new Exception("Error al insertar el inventario.");
            }

            db.setTransactionSuccessful();

        } catch (Exception e) {
            Log.e("InventarioComputadoras", "Error al insertar en la base de datos", e);
            Toast.makeText(this, "Error al guardar:" + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null) {
                db.endTransaction();
            }
        }

        if (idInventario != -1) {
            Toast.makeText(this, "Registro guardado con éxito", Toast.LENGTH_SHORT).show();
            limpiarCampos();
            etNombreAgencia.requestFocus();
        }
    }

    private void limpiarCampos() {

            idAgenciaSeleccionada = -1;
        etNombreAgencia.setText("", false);
        etNombreEquipo.setText("");
        etNombreEncargado.setText("");
        acVersionWindows.setText("", false);
        acMemoriaRam.setText("", false);
        acVersionAntivirus.setText("", false);
        etDireccionIp.setText("");
        etNoActivo.setText("");

        etNombreAgencia.setError(null);
        etNombreEquipo.setError(null);
        etNombreEncargado.setError(null);
    }

    @Override
    protected void onDestroy() {
        if (adminSQLiteOpenHelper != null) {
            adminSQLiteOpenHelper.close();
        }
        super.onDestroy();
    }
}



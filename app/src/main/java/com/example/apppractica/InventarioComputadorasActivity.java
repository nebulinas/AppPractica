package com.example.apppractica;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class InventarioComputadorasActivity extends AppCompatActivity {

    private static final String DATABASE_NAME = "inventarioActivos.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_INVENTARIO= "inventarioActivos";
    public static final String COLUMN_AGENCIA = "agencia";
    public static final String COLUMN_EQUIPO = "equipo";
    public static final String COLUMN_WINDOWS = "windows";
    public static final String COLUMN_IDENCARGADO = "idencargado";
    public static final String COLUMN_RAM = "ram";
    public static final String COLUMN_ANTIVIRUS = "antivirus";
    public static final String COLUMN_IP = "ip";
    public static final String COLUMN_ACTIVO = "activo";

    public static final String TABLE_ENCARGADO = "encargadoEquipo"; //conectarlo con la tabla encargadoEquipo
    public static final String COLUMN_ENCARGADO_NOMBRE = "nombre";

    private TextInputEditText etNombreAgencia, etNombreEquipo, etNombreEncargado,  etDireccionIp, etNoActivo;
    private AutoCompleteTextView  acMemoriaRam, acVersionWindows, acVersionAntivirus;
    private MaterialButton btnGuardar;
    private MaterialCardView cardBotonRegresar;

    private AdminSQLiteOpenHelper adminSQLiteOpenHelper;



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


        String[] opcionesRam ={"2 GB", "4 GB", "8 GB", "16 GB", "32 GB", "64 GB" };
        ArrayAdapter<String> adapterRam = new ArrayAdapter<>
                (this, android.R.layout.simple_dropdown_item_1line, opcionesRam);
        acMemoriaRam.setAdapter(adapterRam);

        String[] versionesWindows = {"Windows 10 Pro", "Windows 11 Pro", "Windows 10 Home", "Windows 11 Home" };
        ArrayAdapter<String> adapterWindows = new ArrayAdapter<>
                (this, android.R.layout.simple_dropdown_item_1line, versionesWindows);
        acVersionWindows.setAdapter(adapterWindows);

        String[] versionAntivirus = {"Eset 12.0", "Eset 10.0"};
        ArrayAdapter<String> adapterAntivirus = new ArrayAdapter<>
                (this, android.R.layout.simple_dropdown_item_1line, versionAntivirus);
        acVersionAntivirus.setAdapter(adapterAntivirus);


        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturarYGuardarDatos();
            }
        });


        if (cardBotonRegresar != null) {
            cardBotonRegresar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    finish();
                }
            });

        }
    }

    private void capturarYGuardarDatos() {
        String agencia = etNombreAgencia.getText().toString().trim();
        String equipo = etNombreEquipo.getText().toString().trim();
        String encargado = etNombreEncargado.getText().toString().trim();
        String windows = acVersionWindows.getText().toString().trim();
        String ram = acMemoriaRam.getText().toString().trim();
        String antivirus = acVersionAntivirus.getText().toString().trim();
        String ip = etDireccionIp.getText().toString().trim();
        String activo = etNoActivo.getText().toString().trim();


        if (agencia.isEmpty()) {
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
        }else {
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
            idEncargado = db.insertOrThrow(TABLE_ENCARGADO, null, valuesEncargado);
            if (idEncargado == -1) {
                throw new android.database.SQLException("Error");
            }

            ContentValues values = new ContentValues();

        values.put(COLUMN_AGENCIA, agencia);
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
        Toast.makeText(this,"Error al guardar:"+ e.getMessage(), Toast.LENGTH_LONG).show();
    } finally {
       if (db   != null)
        db.endTransaction();
        db.close();

        }

        if (idInventario != -1) {
            Toast.makeText(this, "Registro guardado con éxito", Toast.LENGTH_SHORT).show();
            limpiarCampos();
            etNombreAgencia.requestFocus();
        } else {
            Toast.makeText(this, "Error al guardar el registro", Toast.LENGTH_SHORT).show();
        }
    }
            private void limpiarCampos() {
                etNombreAgencia.setText("");
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


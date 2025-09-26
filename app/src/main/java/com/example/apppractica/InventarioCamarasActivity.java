package com.example.apppractica;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;



public class InventarioCamarasActivity extends AppCompatActivity {


    private static final String DATABASE_NAME = "inventarioActivos.db";
    private static final int DATABASE_VERSION = 3;
    private static final String TABLE_INVENTARIO = "inventarioActivosCamaras";
    public static final String COLUMN_ACTIVO_ALTA = "ActivoAlta";
    public static final String COLUMN_ACTIVO_BAJA = "ActivoBaja";
    public static final String COLUMN_AREA_INSTALACION = "areaInstalacion";
    public static final String COLUMN_AGENCIA_INSTALACION="AgenciaInstalacion";

    private AdminSQLiteOpenHelper adminSQLiteOpenHelper;

    private TextInputEditText etActivoAlta, etActivoBaja, etAreaInstalacion, etAgenciaInstalacion;
    private MaterialButton btnGuardar;
    private MaterialCardView cardBotonRegresar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.inventario_camaras);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        adminSQLiteOpenHelper = new AdminSQLiteOpenHelper(this, DATABASE_NAME, null, DATABASE_VERSION);

        etActivoAlta = findViewById(R.id.activo_camara_alta);
        etActivoBaja = findViewById(R.id.activo_camara_baja);
        etAreaInstalacion = findViewById(R.id.area_instalacion);
        etAgenciaInstalacion=findViewById(R.id.agencia_camaras);
        cardBotonRegresar = findViewById(R.id.card_regresar);
        btnGuardar = findViewById(R.id.botn_guardar);



        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuardarDatos();
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
        private void GuardarDatos() {
            String activoAlta = etActivoAlta.getText().toString().trim();
            String activoBaja = etActivoBaja.getText().toString().trim();
            String areaInstalacion = etAreaInstalacion.getText().toString().trim();
            String agenciaInstalacion = etAgenciaInstalacion.getText().toString().trim();


            if (activoAlta.isEmpty()) {
                etActivoAlta.setError("Este campo es requerido");
                etActivoAlta.requestFocus();
                return;
            } else {
                etActivoAlta.setError(null);
            }

            if (activoBaja.isEmpty()) {
                etActivoBaja.setError("Este campo es requerido");
                etActivoBaja.requestFocus();
                return;
            } else {
                etActivoBaja.setError(null);
            }

            if (areaInstalacion.isEmpty()) {
                etAreaInstalacion.setError("Este campo es requerido");
                etAreaInstalacion.requestFocus();
                return;
            } else {
                etAreaInstalacion.setError(null);
            }
            if (agenciaInstalacion.isEmpty()) {
                etAgenciaInstalacion.setError("Este campo es requerido");
                etAgenciaInstalacion.requestFocus();
                return;
            } else {
                etAgenciaInstalacion.setError(null);
            }

            SQLiteDatabase db= null;
            long idInventario = -1;
            try {
                db = adminSQLiteOpenHelper.getWritableDatabase();
                db.beginTransaction();

                ContentValues values = new ContentValues();
                values.put(COLUMN_ACTIVO_ALTA, activoAlta);
                values.put(COLUMN_ACTIVO_BAJA, activoBaja);
                values.put(COLUMN_AREA_INSTALACION, areaInstalacion);
                values.put(COLUMN_AGENCIA_INSTALACION, agenciaInstalacion);

                idInventario = db.insert(TABLE_INVENTARIO, null, values);

                if (idInventario == -1) {
                    throw new Exception("Error al insertar el inventario.");
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
            }

            if (idInventario != -1) {
               Toast.makeText(this, "Registro guardado con éxito", Toast.LENGTH_SHORT).show();
                limpiarCampos();
                etActivoAlta.requestFocus();
            } else {
                Toast.makeText(this, "Error al guardar el registro", Toast.LENGTH_SHORT).show();
            }

            }

        private void limpiarCampos() {
            etActivoAlta.setText("");
            etActivoBaja.setText("");
            etAreaInstalacion.setText("");
            etAgenciaInstalacion.setText("");

            etActivoAlta.setError(null);
            etActivoBaja.setError(null);
            etAreaInstalacion.setError(null);
            etAgenciaInstalacion.setError(null);


    }

    @Override
    protected void onDestroy() {
        if (adminSQLiteOpenHelper != null) {
            adminSQLiteOpenHelper.close();
        }
        super.onDestroy();
    }
}


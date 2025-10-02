package com.example.apppractica;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class EliminarActivity extends AppCompatActivity {
    //Echo por herielis
    // Variables para la UI (interfaz de usuario)
    private TextInputEditText buscarEquipo, nombreEquipoMostrar, encargadoMostrar,
            windowsMostrar, ramMostrar, antivirusMostrar,
            ipMostrar, activoMostrar;
    private MaterialButton botonBuscar, botonCancelar, botonEliminar;
    private MaterialCardView botonRegresar;
    private ImageView imgRegresar;

    // Variables para la base de datos
    private static final int DATABASE_VERSION = 6;
    private AdminSQLiteOpenHelper dbHelper;
    private SQLiteDatabase bd;
    private String equipoActualId = "";
    private boolean equipoEncontrado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.eliminar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar componentes
        inicializarComponentes();
        configurarEventos();

        // Inicializar base de datos
        dbHelper = new AdminSQLiteOpenHelper(this, "inventarioActivos.db", null, DATABASE_VERSION);

        // Inicialmente deshabilitar el botón de eliminar
        botonEliminar.setEnabled(false);
    }

    private void inicializarComponentes() {
        // Campo de búsqueda
        buscarEquipo = findViewById(R.id.buscar_equipo);

        // Campo de información
        nombreEquipoMostrar = findViewById(R.id.nombre_equipo_mostrar);
        encargadoMostrar = findViewById(R.id.encargado_mostrar);
        windowsMostrar = findViewById(R.id.windows_mostrar);
        ramMostrar = findViewById(R.id.ram_mostrar);
        antivirusMostrar = findViewById(R.id.antivirus_mostrar);
        ipMostrar = findViewById(R.id.ip_mostrar);
        activoMostrar = findViewById(R.id.activo_mostrar);

        // Botones
        botonBuscar = findViewById(R.id.boton_buscar);
        botonCancelar = findViewById(R.id.boton_cancelar);
        botonEliminar = findViewById(R.id.boton_eliminar);
        botonRegresar = findViewById(R.id.card_boton_regresar);
        imgRegresar = findViewById(R.id.boton_regresar);
    }

    private void configurarEventos() {
        // Botón de búsqueda
        botonBuscar.setOnClickListener(v -> buscarEquipo());

        // Botón de regresar
        botonRegresar.setOnClickListener(v -> finish());
        imgRegresar.setOnClickListener(v -> finish());

        // Botón de cancelar
        botonCancelar.setOnClickListener(v -> {
            limpiarCampos();
            equipoActualId = "";
            equipoEncontrado = false;
            botonEliminar.setEnabled(false);
            Toast.makeText(this, "Operación cancelada", Toast.LENGTH_SHORT).show();
        });

        // Botón de eliminar
        botonEliminar.setOnClickListener(v -> mostrarDialogoConfirmacion());
    }

    private void buscarEquipo() {
        String terminoBusqueda = buscarEquipo.getText().toString().trim();

        if (terminoBusqueda.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese un término de búsqueda", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT i.id, i.equipo, e.nombre as encargado, i.windows, i.ram, " +
                        "i.antivirus, i.ip, i.activo, a.nombre_agencia " +
                        "FROM inventarioActivos i " +
                        "LEFT JOIN encargadoEquipo e ON i.idencargado = e.idencargado " +
                        "LEFT JOIN tbAgencias a ON i.id_agencia = a.id_agencia " +
                        "WHERE i.equipo LIKE ? OR i.activo LIKE ?",
                new String[]{"%" + terminoBusqueda + "%", "%" + terminoBusqueda + "%"}
        );


        if (cursor.moveToFirst()) {
            cargarDatosEquipo(cursor);
            equipoEncontrado = true;
            botonEliminar.setEnabled(true);
            Toast.makeText(this, "Equipo encontrado", Toast.LENGTH_SHORT).show();
        } else {
            limpiarCampos();
            equipoEncontrado = false;
            botonEliminar.setEnabled(false);
            Toast.makeText(this, "No se encontró el equipo", Toast.LENGTH_LONG).show();
        }

        cursor.close();

    }

    private void cargarDatosEquipo(Cursor cursor) {
        // Guardar el ID
        equipoActualId = cursor.getString(cursor.getColumnIndexOrThrow("id"));

        if (nombreEquipoMostrar != null) {
            nombreEquipoMostrar.setText(cursor.getString(cursor.getColumnIndexOrThrow("equipo")));
        }
        String encargadoCursor = cursor.getString(cursor.getColumnIndexOrThrow("encargado"));;
        if (encargadoMostrar != null) {
            if (encargadoCursor !=null && !encargadoCursor.trim().isEmpty()) {
                encargadoMostrar.setText(encargadoCursor);
        } else {
            encargadoMostrar.setText("no asignado");
        }
            }
        windowsMostrar.setText(cursor.getString(cursor.getColumnIndexOrThrow("windows")));
        ramMostrar.setText(cursor.getString(cursor.getColumnIndexOrThrow("ram")));
        antivirusMostrar.setText(cursor.getString(cursor.getColumnIndexOrThrow("antivirus")));
        ipMostrar.setText(cursor.getString(cursor.getColumnIndexOrThrow("ip")));
        activoMostrar.setText(cursor.getString(cursor.getColumnIndexOrThrow("activo")));
    }

    private void mostrarDialogoConfirmacion() {
        if (!equipoEncontrado || equipoActualId.isEmpty()) {
            Toast.makeText(this, "Primero debe buscar un equipo para eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombreEquipo = nombreEquipoMostrar.getText().toString();
        String numeroActivo = activoMostrar.getText().toString();




        new AlertDialog.Builder(this)
                .setTitle("⚠️ Confirmar Eliminación")
                .setMessage("¿Está seguro de eliminar permanentemente?\n\n" +
                        "Equipo: " + nombreEquipo + "\n" +
                        "Activo: " + numeroActivo + "\n\n" +
                        "Esta acción NO se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarEquipo())
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .show();


    }

    private void eliminarEquipo() {
        bd = dbHelper.getWritableDatabase();

        try {
            int filasEliminadas = bd.delete("inventarioActivos", "id=?", new String[]{equipoActualId});

            if (filasEliminadas > 0) {
                Toast.makeText(this, "Equipo eliminado exitosamente", Toast.LENGTH_LONG).show();

                mostrarDialogoEliminacionExitosa();

                limpiarCampos();
                equipoActualId = "";
                equipoEncontrado = false;
                botonEliminar.setEnabled(false);
// por si existe algun error de eliminacion
            } else {
                Toast.makeText(this, "Error al eliminar el equipo", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error de base de datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            bd.close();
        }
    }

    private void mostrarDialogoEliminacionExitosa() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("✅ Eliminación Exitosa");
        builder.setMessage("El equipo ha sido eliminado permanentemente del inventario.");
        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.holo_green_dark));
    }

    private void limpiarCampos() {
        // Limpiar campo de búsqueda
        buscarEquipo.setText("");

        // Limpiar todos los campos de información
        nombreEquipoMostrar.setText("");
        encargadoMostrar.setText("");
        windowsMostrar.setText("");
        ramMostrar.setText("");
        antivirusMostrar.setText("");
        ipMostrar.setText("");
        activoMostrar.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bd != null && bd.isOpen()) {
            bd.close();
        }
    }
}
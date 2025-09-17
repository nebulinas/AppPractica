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

    // Variables para la UI
    private TextInputEditText buscarEquipo, nombreEquipoMostrar, encargadoMostrar,
            windowsMostrar, ramMostrar, antivirusMostrar,
            ipMostrar, activoMostrar;
    private MaterialButton botonBuscar, botonCancelar, botonEliminar;
    private MaterialCardView botonRegresar;
    private ImageView imgRegresar;

    // Variables para la base de datos
    private AdminSQLiteOpenHelper admin;
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
        admin = new AdminSQLiteOpenHelper(this, "inventario.db", null, 1);

        // Inicialmente deshabilitar el botón de eliminar
        botonEliminar.setEnabled(false);
    }

    private void inicializarComponentes() {
        // Campo de búsqueda
        buscarEquipo = findViewById(R.id.buscar_equipo);

        // Campos de información (solo lectura)
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

        bd = admin.getReadableDatabase();

        // Buscar por nombre del equipo o número de activo
        Cursor cursor = bd.rawQuery(
                "SELECT * FROM inventarioActivos WHERE equipo LIKE ? OR activo LIKE ?",
                new String[]{"%" + terminoBusqueda + "%", "%" + terminoBusqueda + "%"}
        );

        if (cursor.moveToFirst()) {
            // Encontró el equipo, cargar los datos
            cargarDatosEquipo(cursor);
            equipoEncontrado = true;
            botonEliminar.setEnabled(true);
            Toast.makeText(this, "Equipo encontrado", Toast.LENGTH_SHORT).show();
        } else {
            // No encontró el equipo
            limpiarCampos();
            equipoEncontrado = false;
            botonEliminar.setEnabled(false);
            Toast.makeText(this, "No se encontró el equipo", Toast.LENGTH_LONG).show();
        }

        cursor.close();
        bd.close();
    }

    private void cargarDatosEquipo(Cursor cursor) {
        // Guardar el ID del equipo actual
        equipoActualId = cursor.getString(cursor.getColumnIndexOrThrow("id"));

        // Cargar los datos en los campos de solo lectura
        nombreEquipoMostrar.setText(cursor.getString(cursor.getColumnIndexOrThrow("equipo")));
        encargadoMostrar.setText(cursor.getString(cursor.getColumnIndexOrThrow("encargado")));
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

        // Crear el diálogo de confirmación
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("⚠️ Confirmar Eliminación");
        builder.setMessage("¿Está seguro de que desea eliminar permanentemente el siguiente equipo?\n\n" +
                "Nombre: " + nombreEquipo + "\n" +
                "Número de Activo: " + numeroActivo + "\n\n" +
                "Esta acción NO SE PUEDE DESHACER.");

        // Configurar botones del diálogo
        builder.setPositiveButton("Eliminar", (dialog, which) -> {
            eliminarEquipo();
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            dialog.dismiss();
        });

        // Personalizar colores
        AlertDialog dialog = builder.create();
        dialog.show();

        // Cambiar color del botón "Eliminar" a rojo
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(android.R.color.darker_gray));
    }

    private void eliminarEquipo() {
        bd = admin.getWritableDatabase();

        try {
            // Realizar la eliminación
            int filasEliminadas = bd.delete("inventarioActivos", "id=?", new String[]{equipoActualId});

            if (filasEliminadas > 0) {
                // Eliminación exitosa
                Toast.makeText(this, "Equipo eliminado exitosamente", Toast.LENGTH_LONG).show();

                // Mostrar diálogo de confirmación de eliminación
                mostrarDialogoEliminacionExitosa();

                // Limpiar los campos y restablecer el estado
                limpiarCampos();
                equipoActualId = "";
                equipoEncontrado = false;
                botonEliminar.setEnabled(false);

            } else {
                // Error en la eliminación
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

        // Cambiar color del botón a verde
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
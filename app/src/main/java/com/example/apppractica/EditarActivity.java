package com.example.apppractica;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditarActivity extends AppCompatActivity {
    //Echo por Herielis
    // Variables para la UI
    private TextInputEditText buscarEquipo, nombreEquipo, encargado, numeroActivo,
            versionAntivirus, direccionIp;
    private AutoCompleteTextView versionWindows, memoriaRam;
    private MaterialButton botonBuscar, botonCancelar, botonGuardarCambios;
    private MaterialCardView botonRegresar;
    private ImageView imgRegresar;
    private TextView textoUltimaModificacion, textoModificadoPor;

    // Variables para la base de datos
    private AdminSQLiteOpenHelper admin;
    private SQLiteDatabase bd;
    private String equipoActualId = "";

    // Arrays para los dropdowns
    private String[] versionesWindows = {"Windows 10", "Windows 11", "Windows Server 2019",
            "Windows Server 2022", "Otro"};
    private String[] opcionesRam = {"4 GB", "8 GB", "16 GB", "32 GB", "64 GB", "Otro"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.editar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar componentes
        inicializarComponentes();
        configurarDropdowns();
        configurarEventos();

        // Inicializar base de datos
        admin = new AdminSQLiteOpenHelper(this, "inventario.db", null, 1);
    }

    private void inicializarComponentes() {
        // Campo de texto
        buscarEquipo = findViewById(R.id.buscar_equipo);
        nombreEquipo = findViewById(R.id.nombre_equipo);
        encargado = findViewById(R.id.encargado);
        numeroActivo = findViewById(R.id.numero_activo);
        versionAntivirus = findViewById(R.id.version_antivirus);
        direccionIp = findViewById(R.id.direccion_ip);

        // Dropdowns
        versionWindows = findViewById(R.id.version_windows);
        memoriaRam = findViewById(R.id.memoria_ram);

        // Botones
        botonBuscar = findViewById(R.id.boton_buscar);
        botonCancelar = findViewById(R.id.boton_cancelar);
        botonGuardarCambios = findViewById(R.id.boton_guardar);
        botonRegresar = findViewById(R.id.card_boton_regresar);
        imgRegresar = findViewById(R.id.boton_regresar);


        textoUltimaModificacion = findViewById(R.id.texto_ultima_modificacion);
        textoModificadoPor = findViewById(R.id.texto_modificado_por);

        // Inicialmente deshabilitar los campos de edición
        habilitarCamposEdicion(false);
    }

    private void configurarDropdowns() {
        // Configuracion para adaptar versiones de Windows
        ArrayAdapter<String> adapterWindows = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, versionesWindows);
        versionWindows.setAdapter(adapterWindows);

        // Configuracion  para adapter memoria RAM
        ArrayAdapter<String> adapterRam = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, opcionesRam);
        memoriaRam.setAdapter(adapterRam);
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
            habilitarCamposEdicion(false);
            equipoActualId = "";
            Toast.makeText(this, "Edición cancelada", Toast.LENGTH_SHORT).show();
        });

        // Botón de guardar cambios
        botonGuardarCambios.setOnClickListener(v -> guardarCambios());
    }

    private void buscarEquipo() {
        String terminoBusqueda = buscarEquipo.getText().toString().trim();

        if (terminoBusqueda.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese un término de búsqueda", Toast.LENGTH_SHORT).show();
            return;
        }

        bd = admin.getReadableDatabase();

        // Este es para buscar por nombre del equipo o número de activo
        Cursor cursor = bd.rawQuery(
                "SELECT * FROM inventarioActivos WHERE equipo LIKE ? OR activo LIKE ?",
                new String[]{"%" + terminoBusqueda + "%", "%" + terminoBusqueda + "%"}
        );

        if (cursor.moveToFirst()) {
            // Este es para cuando encontro equipo para cargar datos
            cargarDatosEquipo(cursor);
            habilitarCamposEdicion(true);
            Toast.makeText(this, "Equipo encontrado", Toast.LENGTH_SHORT).show();
        } else {
            // No encontro el equipo
            limpiarCampos();
            habilitarCamposEdicion(false);
            Toast.makeText(this, "No se encontró el equipo", Toast.LENGTH_LONG).show();
        }

        cursor.close();
        bd.close();
    }

    private void cargarDatosEquipo(Cursor cursor) {
        // Guardar el ID del equipo actual
        equipoActualId = cursor.getString(cursor.getColumnIndexOrThrow("id"));

        // Cargar los datos en los campos
        nombreEquipo.setText(cursor.getString(cursor.getColumnIndexOrThrow("equipo")));
        encargado.setText(cursor.getString(cursor.getColumnIndexOrThrow("encargado")));
        numeroActivo.setText(cursor.getString(cursor.getColumnIndexOrThrow("activo")));
        versionWindows.setText(cursor.getString(cursor.getColumnIndexOrThrow("windows")), false);
        memoriaRam.setText(cursor.getString(cursor.getColumnIndexOrThrow("ram")), false);
        versionAntivirus.setText(cursor.getString(cursor.getColumnIndexOrThrow("antivirus")));
        direccionIp.setText(cursor.getString(cursor.getColumnIndexOrThrow("ip")));

        // Actualizar información de modificación
        actualizarInfoModificacion();
    }

    private void guardarCambios() {
        if (equipoActualId.isEmpty()) {
            Toast.makeText(this, "Primero debe buscar un equipo para editar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar campos requeridos
        if (!validarCampos()) {
            return;
        }

        bd = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();
        registro.put("equipo", nombreEquipo.getText().toString().trim());
        registro.put("encargado", encargado.getText().toString().trim());
        registro.put("windows", versionWindows.getText().toString());
        registro.put("ram", memoriaRam.getText().toString());
        registro.put("antivirus", versionAntivirus.getText().toString().trim());
        registro.put("ip", direccionIp.getText().toString().trim());

        int filasAfectadas = bd.update("inventarioActivos", registro, "id=?", new String[]{equipoActualId});

        if (filasAfectadas > 0) {
            Toast.makeText(this, "Equipo actualizado exitosamente", Toast.LENGTH_SHORT).show();
            actualizarInfoModificacion();
            limpiarCampos();
            habilitarCamposEdicion(false);
            equipoActualId = "";
        } else {
            Toast.makeText(this, "Error al actualizar el equipo", Toast.LENGTH_SHORT).show();
        }

        bd.close();
    }

    private boolean validarCampos() {
        // Validar nombre del equipo
        if (nombreEquipo.getText().toString().trim().isEmpty()) {
            nombreEquipo.setError("Este campo es requerido");
            nombreEquipo.requestFocus();
            return false;
        }

        // Validar encargado
        if (encargado.getText().toString().trim().isEmpty()) {
            encargado.setError("Este campo es requerido");
            encargado.requestFocus();
            return false;
        }

        // Validar formato de IP
        String ip = direccionIp.getText().toString().trim();
        if (!ip.isEmpty() && !esIPValida(ip)) {
            direccionIp.setError("Formato de IP inválido");
            direccionIp.requestFocus();
            return false;
        }

        return true;
    }

    private boolean esIPValida(String ip) {
        // Validación básica de formato IP
        String[] partes = ip.split("\\.");
        if (partes.length != 4) return false;

        try {
            for (String parte : partes) {
                int numero = Integer.parseInt(parte);
                if (numero < 0 || numero > 255) return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void habilitarCamposEdicion(boolean habilitar) {
        nombreEquipo.setEnabled(habilitar);
        encargado.setEnabled(habilitar);
        versionWindows.setEnabled(habilitar);
        memoriaRam.setEnabled(habilitar);
        versionAntivirus.setEnabled(habilitar);
        direccionIp.setEnabled(habilitar);
        botonGuardarCambios.setEnabled(habilitar);
        botonCancelar.setEnabled(habilitar);

        // Es para que el número de activo siempre permanesca deshabilitado
        numeroActivo.setEnabled(false);
    }

    private void limpiarCampos() {
        buscarEquipo.setText("");
        nombreEquipo.setText("");
        encargado.setText("");
        numeroActivo.setText("");
        versionWindows.setText("", false);
        memoriaRam.setText("", false);
        versionAntivirus.setText("");
        direccionIp.setText("");

        // Limpiar errores
        nombreEquipo.setError(null);
        encargado.setError(null);
        direccionIp.setError(null);

        // Resetear información de modificación
        textoUltimaModificacion.setText("Último cambio: --/--/---- --:--");
        textoModificadoPor.setText("Modificado por: ---");
    }

    private void actualizarInfoModificacion() {
        // Obtener fecha y hora actual
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String fechaHora = sdf.format(new Date());

        textoUltimaModificacion.setText("Último cambio: " + fechaHora);
        textoModificadoPor.setText("Modificado por: Usuario Actual"); // Aquí podrías usar el usuario actual del sistema
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bd != null && bd.isOpen()) {
            bd.close();
        }
    }
}

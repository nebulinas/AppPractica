package com.example.apppractica;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
    private AdminSQLiteOpenHelper adminSQLiteOpenHelper;
    private SQLiteDatabase bd;
    private String equipoActualId = "";

    private static final String DATABASE_NAME = "inventarioActivos.db";
    private static final int DATABASE_VERSION = 3;
    private static final String TABLE_INVENTARIO= "inventarioActivos";
    public static final String COLUMN_AGENCIA = "agencia";
    public static final String COLUMN_EQUIPO = "equipo";
    public static final String COLUMN_IDENCARGADO = "idencargado";
    public static final String COLUMN_WINDOWS = "windows";
    public static final String COLUMN_RAM = "ram";
    public static final String COLUMN_ANTIVIRUS = "antivirus";
    public static final String COLUMN_IP = "ip";
    public static final String COLUMN_ACTIVO = "activo";

    public static final String TABLE_ENCARGADO = "encargadoEquipo"; //conectarlo con la tabla encargadoEquipo
    public static final String COLUMN_ENCARGADO_NOMBRE = "nombre";


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
        adminSQLiteOpenHelper = new AdminSQLiteOpenHelper(
                this, "inventarioActivos.db",
                null, DATABASE_VERSION);
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
        Log.d("BUSQUEDA", "Termino a buscar: " + terminoBusqueda + "");

        if (terminoBusqueda.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese un término de búsqueda", Toast.LENGTH_SHORT).show();
            return;
        }

        bd = adminSQLiteOpenHelper.getReadableDatabase();

        // Este es para buscar por nombre del equipo o número de activo
        // Cambio hecho por yaxchel xol
        String query = "SELECT " +
                "i.id, i.equipo, i.idencargado, i.windows, i.ram, i.antivirus, i.ip, i.activo, " +
                "e.nombre AS nombre_encargado " +
                "FROM inventarioActivos i " +
                "INNER JOIN encargadoEquipo e ON i.idencargado = e.idencargado " +
                "WHERE LOWER(i.equipo) LIKE LOWER(?) OR LOWER(i.activo) LIKE LOWER(?)";

        Cursor cursor = bd.rawQuery(query, new String[]{"%" + terminoBusqueda + "%", "%" + terminoBusqueda + "%"});

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
        encargado.setText(cursor.getString(cursor.getColumnIndexOrThrow("nombre_encargado")));
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
//Cambio hecho por yaxchel
        bd = adminSQLiteOpenHelper.getWritableDatabase();
        bd.beginTransaction();

        try {
            String nuevoEncargadoNombre = encargado.getText().toString().trim();
            long idEncargado;

            Cursor cursorEncargado = null;
            try {
                cursorEncargado = bd.rawQuery("SELECT idencargado FROM encargadoEquipo WHERE nombre = ?", new String[]{nuevoEncargadoNombre});

                if (cursorEncargado.moveToFirst()) {
                    idEncargado = cursorEncargado.getLong(cursorEncargado.getColumnIndexOrThrow(COLUMN_IDENCARGADO));
                } else {
                    // Por si el encargado no existe
                    ContentValues valuesEncargado = new ContentValues();
                    valuesEncargado.put("nombre", nuevoEncargadoNombre);
                    idEncargado = bd.insert("encargadoEquipo", null, valuesEncargado);
                }
            } finally {
                if (cursorEncargado != null) {
                    cursorEncargado.close();
                }
            }

            if (idEncargado == -1) {
                throw new Exception("No se pudo encontrar al encargado");
            }

        ContentValues registro = new ContentValues();
        registro.put("equipo", nombreEquipo.getText().toString().trim());
        registro.put(COLUMN_IDENCARGADO, idEncargado);
        registro.put("windows", versionWindows.getText().toString());
        registro.put("ram", memoriaRam.getText().toString());
        registro.put("antivirus", versionAntivirus.getText().toString().trim());
        registro.put("ip", direccionIp.getText().toString().trim());

        int filasAfectadas = bd.update("inventarioActivos", registro, "id=?", new String[]{equipoActualId});

        if (filasAfectadas > 0) {
            bd.setTransactionSuccessful();
            Toast.makeText(this, "Equipo actualizado exitosamente", Toast.LENGTH_SHORT).show();
            actualizarInfoModificacion();
            limpiarCampos();
            habilitarCamposEdicion(false);
            equipoActualId = "";
        } else {
            Toast.makeText(this, "Error al actualizar el equipo", Toast.LENGTH_SHORT).show();
        }

        } catch (Exception e) {
            Log.e("EditarActivity", "Error al actualizar el equipo", e);
            Toast.makeText(this, "Error al actualizar el equipo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            bd.endTransaction(); //Termina la transacción.
            bd.close();
        }
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

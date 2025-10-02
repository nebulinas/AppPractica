package com.example.apppractica;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

public class ConsultaActivos extends AppCompatActivity {

    private TextInputEditText editTextBusqueda;
    private MaterialButton botonBuscar;
    private TextView textoResultado;
    private AdminSQLiteOpenHelper adminDbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_activos);

        adminDbHelper = new AdminSQLiteOpenHelper(this, "inventarioActivos.db", null, 6);

        editTextBusqueda = findViewById(R.id.busqueda_activos);
        botonBuscar = findViewById(R.id.boton_buscar);




        textoResultado = findViewById(R.id.texto_resultado);

        botonBuscar.setOnClickListener(v -> realizarBusqueda());

        editTextBusqueda.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                realizarBusqueda();
                return true;
            }
            return false;
        });
    }

    private void realizarBusqueda() {
        String textoBusqueda = editTextBusqueda.getText().toString().trim();

        if (TextUtils.isEmpty(textoBusqueda)) {
            Toast.makeText(this, "Ingresa un término de búsqueda", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder resultado = new StringBuilder();
        SQLiteDatabase db = adminDbHelper.getReadableDatabase();

        try {
            // Verificar si es búsqueda por encargado
            if (esEncargado(db, textoBusqueda)) {
                buscarPorEncargado(db, textoBusqueda, resultado);
            } else {
                resultado.append("=== COMPUTADORAS ===\n\n");
                buscarComputadoras(db, textoBusqueda, resultado, false);

                resultado.append("\n=== UTILITARIOS ===\n\n");
                buscarUtilitarios(db, textoBusqueda, resultado, false);
            }

            textoResultado.setText(resultado.toString());
            Toast.makeText(this, "Búsqueda completada", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Error en la búsqueda", Toast.LENGTH_SHORT).show();
            textoResultado.setText("Error: " + e.getMessage());
        } finally {
            db.close();
        }
    }

    private boolean esEncargado(SQLiteDatabase db, String busqueda) {
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM encargadoEquipo WHERE nombre LIKE ?",
                new String[]{"%" + busqueda + "%"}
        );
        boolean existe = cursor.moveToFirst() && cursor.getInt(0) > 0;
        cursor.close();
        return existe;
    }

    private void buscarPorEncargado(SQLiteDatabase db, String encargado, StringBuilder resultado) {
        Cursor cursor = db.rawQuery(
                "SELECT nombre FROM encargadoEquipo WHERE nombre LIKE ? LIMIT 1",
                new String[]{"%" + encargado + "%"}
        );

        String nombreEncargado = encargado;
        if (cursor.moveToFirst()) {
            nombreEncargado = cursor.getString(0);
        }
        cursor.close();

        resultado.append("ASIGNACIONES DE: ").append(nombreEncargado).append("\n\n");

        resultado.append("--- COMPUTADORAS ---\n");
        buscarComputadoras(db, nombreEncargado, resultado, true);

        resultado.append("\n--- UTILITARIOS ---\n");
        buscarUtilitarios(db, nombreEncargado, resultado, true);
    }

    private void buscarComputadoras(SQLiteDatabase db, String busqueda, StringBuilder resultado, boolean esPorEncargado) {
        String query = esPorEncargado ?
                "SELECT i.equipo, i.activo, a.nombre_agencia, i.ip, i.windows, i.ram, i.antivirus " +
                        "FROM inventarioActivos i " +
                        "LEFT JOIN encargadoEquipo e ON i.idencargado = e.idencargado " +
                        "LEFT JOIN tbAgencias a ON i.id_agencia = a.id_agencia " +
                        "WHERE e.nombre = ? ORDER BY i.equipo" :

                "SELECT i.equipo, i.activo, a.nombre_agencia, e.nombre, i.ip, i.windows, i.ram, i.antivirus " +
                        "FROM inventarioActivos i " +
                        "LEFT JOIN encargadoEquipo e ON i.idencargado = e.idencargado " +
                        "LEFT JOIN tbAgencias a ON i.id_agencia = a.id_agencia " +
                        "WHERE i.activo LIKE ? OR e.nombre LIKE ? OR i.equipo LIKE ? OR a.nombre_agencia LIKE ? " +
                        "ORDER BY i.equipo";

        Cursor cursor = esPorEncargado ?
                db.rawQuery(query, new String[]{busqueda}) :
                db.rawQuery(query, new String[]{"%" + busqueda + "%", "%" + busqueda + "%", "%" + busqueda + "%", "%" + busqueda + "%"});

        int count = 0;
        while (cursor.moveToNext()) {
            count++;
            if (esPorEncargado) {
                resultado.append("• ").append(cursor.getString(0)).append("\n");
                resultado.append("  Activo: ").append(cursor.getString(1)).append("\n");
                resultado.append("  Agencia: ").append(cursor.getString(2)).append("\n");
                resultado.append("  IP: ").append(cursor.getString(3)).append("\n");
                resultado.append("  Windows: ").append(cursor.getString(4)).append("\n");
                resultado.append("  RAM: ").append(cursor.getString(5)).append("\n");
                resultado.append("  Antivirus: ").append(cursor.getString(6)).append("\n\n");
            } else {
                resultado.append("COMPUTADORA #").append(count).append("\n");
                resultado.append("• Equipo: ").append(cursor.getString(0)).append("\n");
                resultado.append("• Activo: ").append(cursor.getString(1)).append("\n");
                resultado.append("• Agencia: ").append(cursor.getString(2)).append("\n");
                resultado.append("• Encargado: ").append(cursor.getString(3)).append("\n");
                resultado.append("• IP: ").append(cursor.getString(4)).append("\n");
                resultado.append("• Windows: ").append(cursor.getString(5)).append("\n");
                resultado.append("• RAM: ").append(cursor.getString(6)).append("\n");
                resultado.append("• Antivirus: ").append(cursor.getString(7)).append("\n");
                resultado.append("----------------------------\n\n");
            }
        }

        if (count == 0) {
            resultado.append(esPorEncargado ? "Sin computadoras asignadas\n" : "No se encontraron computadoras\n");
        } else if (!esPorEncargado) {
            resultado.append("Total: ").append(count).append(" computadoras\n\n");
        }

        cursor.close();
    }

    private void buscarUtilitarios(SQLiteDatabase db, String busqueda, StringBuilder resultado, boolean esPorEncargado) {
        String query = esPorEncargado ?
                "SELECT u.tipo, u.activo, u.marca, u.modelo, u.serie, a.nombre_agencia, u.estado " +
                        "FROM inventarioUtilitarios u " +
                        "LEFT JOIN encargadoEquipo e ON u.idencargado = e.idencargado " +
                        "LEFT JOIN tbAgencias a ON u.id_agencia = a.id_agencia " +
                        "WHERE e.nombre = ? ORDER BY u.tipo" :

                "SELECT u.tipo, u.activo, u.marca, u.modelo, u.serie, a.nombre_agencia, e.nombre, u.estado " +
                        "FROM inventarioUtilitarios u " +
                        "LEFT JOIN encargadoEquipo e ON u.idencargado = e.idencargado " +
                        "LEFT JOIN tbAgencias a ON u.id_agencia = a.id_agencia " +
                        "WHERE u.activo LIKE ? OR e.nombre LIKE ? OR u.tipo LIKE ? OR a.nombre_agencia LIKE ? " +
                        "ORDER BY u.tipo";

        Cursor cursor = esPorEncargado ?
                db.rawQuery(query, new String[]{busqueda}) :
                db.rawQuery(query, new String[]{"%" + busqueda + "%", "%" + busqueda + "%", "%" + busqueda + "%", "%" + busqueda + "%"});

        int count = 0;
        while (cursor.moveToNext()) {
            count++;
            if (esPorEncargado) {
                resultado.append("• ").append(cursor.getString(0)).append("\n");
                resultado.append("  Activo: ").append(cursor.getString(1)).append("\n");
                resultado.append("  Marca: ").append(cursor.getString(2)).append("\n");
                resultado.append("  Modelo: ").append(cursor.getString(3)).append("\n");
                resultado.append("  Serie: ").append(cursor.getString(4)).append("\n");
                resultado.append("  Agencia: ").append(cursor.getString(5)).append("\n");
                resultado.append("  Estado: ").append(cursor.getString(6)).append("\n\n");
            } else {
                resultado.append("UTILITARIO #").append(count).append("\n");
                resultado.append("• Tipo: ").append(cursor.getString(0)).append("\n");
                resultado.append("• Activo: ").append(cursor.getString(1)).append("\n");
                resultado.append("• Marca: ").append(cursor.getString(2)).append("\n");
                resultado.append("• Modelo: ").append(cursor.getString(3)).append("\n");
                resultado.append("• Serie: ").append(cursor.getString(4)).append("\n");
                resultado.append("• Agencia: ").append(cursor.getString(5)).append("\n");
                resultado.append("• Encargado: ").append(cursor.getString(6)).append("\n");
                resultado.append("• Estado: ").append(cursor.getString(7)).append("\n");
                resultado.append("----------------------------\n\n");
            }
        }

        if (count == 0) {
            resultado.append(esPorEncargado ? "Sin utilitarios asignados\n" : "No se encontraron utilitarios\n");
        } else if (!esPorEncargado) {
            resultado.append("Total: ").append(count).append(" utilitarios\n");
        }

        cursor.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adminDbHelper != null) {
            adminDbHelper.close();
        }
    }
}



package com.example.apppractica;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InventarioComputadorasActivity extends AppCompatActivity {

    private EditText etNombreAgencia, etNombreEquipo, etNombreEncargado, etVersionWindows, etMemoriaRam, etVersionAntivirus, etDireccionIp, etNoActivo;
    private Button etBotonGuardar;
    private Spinner spMemoriaRam, spVersionAntivirus;
    private LinearLayout layout_inventario;



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

        etNombreAgencia = findViewById(R.id.nombre_agencia);
        etNombreEquipo = findViewById(R.id.nombre_equipo);
        etNombreEncargado = findViewById(R.id.nombre_encargado);
        etVersionWindows = findViewById(R.id.version_windows);
        etMemoriaRam = findViewById(R.id.memoria_ram);
        etVersionAntivirus = findViewById(R.id.version_antivirus);
        etDireccionIp = findViewById(R.id.direccion_ip);
        etNoActivo = findViewById(R.id.no_activo);
        etBotonGuardar = findViewById(R.id.boton_guardar);
    }

}

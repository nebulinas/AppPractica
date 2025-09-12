package com.example.apppractica;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MenuComputadoraActivity extends AppCompatActivity {

    private ImageView btnEditar, btnRegistrar, btnEliminar, btnRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.menu_computadora);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar los botones (ImageView)
        inicializarBotones();

        // Configurar los listeners
        configurarListeners();
    }

    private void inicializarBotones() {
        // Aquí debes poner los IDs correctos de tus ImageView del archivo XML
        btnEditar = findViewById(R.id.btn_editar); // Cambia por el ID real
        btnRegistrar = findViewById(R.id.btn_registrar); // Cambia por el ID real
        btnEliminar = findViewById(R.id.btn_eliminar); // Cambia por el ID real
        btnRegresar = findViewById(R.id.boton_regresar); // Cambia por el ID real
    }

    private void configurarListeners() {
        // Botón Editar
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuComputadoraActivity.this, EditarActivity.class);
                startActivity(intent);
            }
        });

        // Botón Registrar
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuComputadoraActivity.this, InventarioComputadorasActivity.class);
                startActivity(intent);
            }
        });

        // Botón Eliminar
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuComputadoraActivity.this, EliminarActivity.class);
                startActivity(intent);
            }
        });

        // Botón Regresar
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuComputadoraActivity.this, MainActivity.class);
                startActivity(intent);
                // Opcional: cerrar esta actividad
                finish();
            }
        });
    }
}
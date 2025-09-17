package com.example.apppractica;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {
//hecho por Yaxchel
    public MaterialCardView btnComputadoras, btnCamaras, btnHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnComputadoras = findViewById(R.id.card_activos_computadoras);
        btnCamaras = findViewById(R.id.card_activos_camaras);
        btnHistorial = findViewById(R.id.card_historial_mantenimiento);

        btnComputadoras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MenuComputadoraActivity.class);
                startActivity(intent);
            }
        });

        btnCamaras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InventarioCamarasActivity.class);
                startActivity(intent);
            }
        });

        btnHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistorialActivosActivity.class);
                startActivity(intent);
            }
        });
    }

}
package com.example.apppractica;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "inventarioActivos.db";
    private static final int DATABASE_VERSION = 6;
    //Hecho por Hab'il
    public AdminSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE tbAgencias("+
                "id_agencia INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "nombre_agencia VARCHAR(50) UNIQUE " +
                ")");


        //Yaxchel Xol
        db.execSQL("CREATE TABLE encargadoEquipo("+
                "idencargado INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "nombre VARCHAR(50) UNIQUE" +
                ")");

        db.execSQL("CREATE TABLE inventarioActivos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "id_agencia INTEGER, " +
                "equipo VARCHAR, " +
                "idencargado INTEGER, " +
                "windows TEXT, " +
                "ram TEXT, " +
                "antivirus TEXT ," +
                "ip TEXT, "+
                "activo VARCHAR(20)," +
                "fecha_cambio TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "Foreign Key (idencargado) REFERENCES encargadoEquipo(idencargado)," +
                "Foreign Key (id_agencia) REFERENCES tbAgencias(id_agencia)" +
                ")");

        db.execSQL("CREATE TABLE inventarioUtilitarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "activo VARCHAR(50)," +
                "tipo VARCHAR(50)," +
                "marca VARCHAR(50)," +
                "modelo VARCHAR(50)," +
                "serie VARCHAR(50)," +
                "idencargado INTEGER," +
                "id_agencia INTEGER," +
                "estado VARCHAR(50)," +
                "fecha_registro TEXT," +
                "fecha_adquisicion TEXT," +
                "FOREIGN KEY (idencargado) REFERENCES encargadoEquipo(idencargado)," +
                "FOREIGN KEY (id_agencia) REFERENCES tbAgencias(id_agencia)" +
                ")");


            precargarAgencias(db);
    }

    private void precargarAgencias(SQLiteDatabase db) {
        String[] nombresAgencia = {
                "Administración", "Ag. Central", "Ag. Sololá", "Ag. Novillero", "Ag. San Pedro",
                "Ag. Nahualá", "Ag. La Esperanza", "Ag. Joyabaj", "Ag. Santa Clara",
                "Ag. Santa Lucía", "Ag. Panajachel", "Ag. Chichicastenango", "Ag. Santiago",
                "Ag. Ixtahuacán", "Ag. El Carmen", "Ag. La Concordia", "Ag. Los Encuentros",
                "Ag. El Calvario", "Ag. Santo Tomás", "Ag. San Juan", "Ag. Zacualpa",
                "Ag. Quiché", "Ag. Guineales", "Ag. San Andrés", "Ag. Concepción"
    };
        ContentValues agenciaValues = new ContentValues();
        for(String nombre: nombresAgencia){
            agenciaValues.put("nombre_agencia", nombre);
            try {
                db.insert("tbAgencias", null, agenciaValues);
            } catch (Exception e) {
                e.printStackTrace();
            }
            agenciaValues.clear();
        }
        }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }
    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 6) {
            db.execSQL("DROP TABLE IF EXISTS tbAgencias");
            db.execSQL("DROP TABLE IF EXISTS encargadoEquipo");
            db.execSQL("DROP TABLE IF EXISTS inventarioActivos");
            db.execSQL("DROP TABLE IF EXISTS inventarioUtilitarios");
            onCreate(db);
        }
    }
}
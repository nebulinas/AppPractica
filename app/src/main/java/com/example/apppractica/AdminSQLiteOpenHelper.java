package com.example.apppractica;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;

    //Hecho por Hab'il
    public AdminSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {



        //Yaxchel Xol

        db.execSQL("CREATE TABLE encargadoEquipo("+
                "idencargado INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "nombre VARCHAR(50)" +
                ")");

        db.execSQL("CREATE TABLE inventarioActivos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "agencia VARCHAR(50)," +
                "equipo VARCHAR, " +
                "idencargado INTEGER, " +
                "windows TEXT, " +
                "ram TEXT, " +
                "antivirus TEXT ," +
                "ip TEXT, "+
                "activo VARCHAR(20)," +
                "fecha_cambio TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "Foreign Key (idencargado) REFERENCES encargadoEquipo(idencargado)" +
                ")");

        db.execSQL("CREATE TABLE inventarioUtilitarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "activo VARCHAR(50)," +
                "tipo VARCHAR(50)," +
                "marca VARCHAR(50)," +
                "modelo VARCHAR(50)," +
                "serie VARCHAR(50)," +
                "encargado VARCHAR(50)," +
                "agencia VARCHAR(50)," +
                "estado VARCHAR(50)," +
                "fecha_registro TEXT," +
                "fecha_adquisicion TEXT" +
                ")");




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
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE inventarioActivos ADD COLUMN fecha_cambio TEXT;");
        }
    }




}
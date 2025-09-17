package com.example.apppractica;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {
//Hecho por Hab'il
    public AdminSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE inventarioActivos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "agencia TEXT," +
                "equipo TEXT, " +
                "encargado TEXT, " +
                "windows TEXT, " +
                "ram TEXT, " +
                "antivirus TEXT ," +
                "ip TEXT, "+
                "activo VARCHAR(20)" +
                ")");
     //Tabla Inventario de Cámaras hecho por Yaxchel Xol
        db.execSQL("CREATE TABLE inventarioActivosCámaras (" +
                "id INTERGER PRIMARY KEY AUTOINCREMENT,"+
                "ActivoAlta VARCHAR(20),"+
                "ActivoBaja VARCHAR(20), "+
                "areaInstalacion TEXT"+
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
    }
}
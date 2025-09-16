package com.example.apppractica;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE inventarioActivos (" +
                "id INTEGER PRIMARY KEY,"+
                "agencia text," +
                "equipo text, " +
                "encargado text, " +
                "windows text, " +
                "ram text, " +
                "antivirus text ," +
                "ip integer, "+
                "activo varchar(20)" +
                ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
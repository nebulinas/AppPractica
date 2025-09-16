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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS inventarioActivos");
        onCreate(db);
    }
}
package com.example.apppractica;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;

public class AgenciaHelper {

    private Context context;

    public static ArrayList<String> obtenerAgencias(AdminSQLiteOpenHelper dbHelper) {
        ArrayList<String> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT nombre_agencia FROM tbAgencias ORDER BY nombre_agencia",
                null
        );

        while (cursor.moveToNext()) {
            lista.add(cursor.getString(0));
        }

        cursor.close();
        db.close();
        return lista;
    }

    public static long obtenerIdAgencia(AdminSQLiteOpenHelper dbHelper, String nombreAgencia) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id_agencia FROM tbAgencias WHERE nombre_agencia = ?",
                new String[]{nombreAgencia}
        );

        long id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getLong(0);
        }

        cursor.close();
        db.close();
        return id;
    }
}
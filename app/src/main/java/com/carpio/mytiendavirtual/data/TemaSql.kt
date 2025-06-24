
package com.carpio.mytiendavirtual.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TemaSql(context: Context) : SQLiteOpenHelper(context, "tema.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                  "tema(id INTEGER PRIMARY KEY, " +
                  "modo TEXT)")
        db.execSQL("INSERT INTO " + "tema(modo)" + " VALUES('oscuro')")
                // Por defecto oscuro
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun getTema(): String {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT modo FROM tema LIMIT 1", null)
        var modo = "oscuro"
        if (cursor.moveToFirst()) {
            modo = cursor.getString(0)
        }
        cursor.close()
        return modo
    }

    fun setTema(modo: String) {
        val db = writableDatabase
        db.execSQL("UPDATE tema SET modo = ?", arrayOf(modo))
    }
}
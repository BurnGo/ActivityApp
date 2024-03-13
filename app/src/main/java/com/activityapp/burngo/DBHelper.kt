package com.activityapp.burngo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context):SQLiteOpenHelper(context, "app.sqlite", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table Users (username TEXT primary key, email TEXT, password TEXT)");
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table if exists Users");
    }

    fun insertData(username: String, email: String, password: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("username", username)
        contentValues.put("email", email)
        contentValues.put("password", password)
        val result = db.insert("users", null, contentValues)
        return result != -1L
    }

    fun checkUserpass(username: String, password: String): Boolean {
        val db = this.writableDatabase;
        val query = "SELECT * from users where username = '$username' and password = '$password'";
        val cursor = db.rawQuery(query, null);
        if(cursor.count <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
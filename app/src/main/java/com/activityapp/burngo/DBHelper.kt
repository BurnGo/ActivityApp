package com.activityapp.burngo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context):SQLiteOpenHelper(context, "app.sqlite", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE Transportation_types" +
                    "(id_Transportation_types integer PRIMARY KEY AUTOINCREMENT NOT NULL ," +
                    "name char (10) NOT NULL);"
        )

        db?.execSQL(
            "create table Users (id_User integer PRIMARY KEY AUTOINCREMENT NOT NULL , username TEXT NOT NULL, " +
                    "email TEXT NOT NULL, password TEXT NOT NULL, currency_balance double DEFAULT 0 NOT NULL," +
                    "height double DEFAULT 0 NOT NULL," +
                    "weight double DEFAULT 0 NOT NULL," +
                    "transportation_type integer," +
                    "FOREIGN KEY(transportation_type) REFERENCES Transportation_types (id_Transportation_types))"
        );
        db?.execSQL(
            "CREATE TABLE Steps" +
                    "(" +
                    "id_Step integer PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "date date NOT NULL," +
                    "steps_count integer NOT NULL," +
                    "distance double NOT NULL," +
                    "calories_burned integer NOT NULL," +
                    "co2_saved_mg double NOT NULL," +
                    "time double NOT NULL," +
                    "fk_Userid_User integer NOT NULL," +
                    "CONSTRAINT Gains FOREIGN KEY(fk_Userid_User) REFERENCES Users (id_User)" +
                    ");"
        )

        db?.execSQL(
            "create table ActiveUser (id_User integer PRIMARY KEY NOT NULL , username TEXT NOT NULL, " +
                    "currency_balance double DEFAULT 0 NOT NULL," +
                    "height double DEFAULT 0 NOT NULL," +
                    "weight double DEFAULT 0 NOT NULL," +
                    "transportation_type integer," +
                    "id integer," +
                    "FOREIGN KEY(transportation_type) REFERENCES Transportation_types (id_Transportation_types))"
        );

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table if exists Transportation_types");
        db?.execSQL("drop table if exists Users");
        db?.execSQL("drop table if exists Steps");
    }

    fun insertData(username: String, email: String, password: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("username", username)
        contentValues.put("email", email)
        contentValues.put("password", password)
        val result = db.insert("Users", null, contentValues)
        return result != 1L
    }

    fun loginUser(id_User: Int, username: String): Boolean{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("id", 1)
        contentValues.put("id_User", id_User)
        contentValues.put("username", username)
        val result = db.insert("ActiveUser", null, contentValues)
        db.close()
        return result != 1L
    }

    fun logoutUser(): Int{
        val db = this.writableDatabase
        val id = 1
        val result = db.delete("ActiveUser", "id=?", arrayOf(id.toString()))
        db.close()
        return result
    }

    fun getUserIdByName(username: String): Int{
        val db = this.readableDatabase
        var userId: Int = -1
        val query = "SELECT id_User FROM Users WHERE username = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(username))
        cursor.use {
            if (it.moveToFirst()) {
                userId = it.getColumnIndex("id_User")
            }
        }
        db.close()
        return userId
    }

    fun initialDataInsertion(name: String): Boolean{
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put("name", name)
        val result = db.insert("Transportation_types", null, cv)
        db.close()
        return result != 1L
    }



    fun checkUserpass(username: String, password: String): Boolean {
        val db = this.writableDatabase;
        val query = "SELECT * from Users where username = '$username' and password = '$password'";
        val cursor = db.rawQuery(query, null);
        if(cursor.count <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        db.close()
        return true;
    }
}
package com.activityapp.burngo

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import org.mindrot.jbcrypt.BCrypt
import java.text.SimpleDateFormat
import java.time.LocalDate

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

    fun insertData(username: String, email: String, password: String, context: Context){
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("username", username)
        contentValues.put("email", email)
        val securePass = BCrypt.hashpw(password, BCrypt.gensalt())
        contentValues.put("password", securePass)

        serverRegister(username, email, password, context){ isSuccess ->
            if(isSuccess){
                db.insert("Users", null, contentValues)
                Toast.makeText(context, "Registration was successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
            }
            else{
                Toast.makeText(context, "Registration was not successful", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun addFull(username: String, email: String, password: String, balance: Double, height: Double,
                weight: Double, transportation: Int): Boolean{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("username", username)
        contentValues.put("email", email)
        val securePass = BCrypt.hashpw(password, BCrypt.gensalt())
        contentValues.put("password", securePass)
        contentValues.put("currency_balance", balance)
        contentValues.put("height", height)
        contentValues.put("weight", weight)
        contentValues.put("transportation_type", transportation)
        val result = db.insert("Users", null, contentValues)
        return result != 1L
    }

    fun registerLocally(username: String, email: String, password: String): Boolean{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("username", username)
        contentValues.put("email", email)
        val securePass = BCrypt.hashpw(password, BCrypt.gensalt())
        contentValues.put("password", securePass)
        val result = db.insert("Users", null, contentValues)
        return result != 1L
    }

    fun loginUser(id_User: Int, username: String, balance: Double, height: Double,
                  weight: Double, transportation: Int): Boolean{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("id", 1)
        contentValues.put("id_User", id_User)
        contentValues.put("username", username)
        /*contentValues.put("currency_balance", balance)
        contentValues.put("height", height)
        contentValues.put("weight", weight)
        contentValues.put("transportation_type", transportation)*/
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

    fun checkActiveUsersCount(): Boolean{
        val db = this.readableDatabase
        var count = 0

        val query = "SELECT COUNT(*) FROM ActiveUser"
        val cursor = db.rawQuery(query, null)

        cursor?.use {
            if (it.moveToFirst()) {
                count = it.getInt(0) // Retrieves the count from the first column
            }
        }

        cursor?.close()
        db.close()
        if(count > 0){
            return true
        }
        else{
            return false
        }
    }

    fun getActiveUserID(): Int{
        val db = this.readableDatabase
        var userId: Int = -1
        val query = "SELECT id_User FROM ActiveUsers WHERE id = 1"
        val cursor: Cursor = db.rawQuery(query, null)
        cursor.use {
            if (it.moveToFirst()) {
                userId = it.getColumnIndex("id_User")
            }
        }
        db.close()
        return userId
    }

    fun addSteps(stepsCount: Int, distance: Double, caloriesBurned: Int, co2_saved: Double, time: Double): Boolean{
        val db = this.writableDatabase
        val today: LocalDate = LocalDate.now()
        // Format the date as a string
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val dateString = dateFormat.format(today)
        val cv = ContentValues()
        cv.put("date", dateString)
        cv.put("steps_count", stepsCount)
        cv.put("distance", distance)
        cv.put("calories_burned", caloriesBurned)
        cv.put("co2_saved_mg", co2_saved)
        cv.put("time", time)
        val UserID = getActiveUserID()
        cv.put("fk_Userid_User", UserID)
        val result = db.insert("Steps", null, cv)
        db.close()
        return result != 1L
    }

    fun getUserIdByName(username: String): Int{
        val db = this.readableDatabase
        var userId: Int = -1
        val query = "SELECT id_User FROM Users WHERE username = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(username))
        cursor.use {
            if (it.moveToFirst()) {
                userId = it.getColumnIndex("id_User")
                Log.d("MALDAUJU", userId.toString())
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



    fun checkUserpass(username: String, password: String, context: Context): Boolean {
        val db = this.readableDatabase;
        var pass: String? = null;
        val query = "SELECT password from Users where username = ?";
        val cursor = db.rawQuery(query, arrayOf(username));
        if(cursor.count > 0){
            cursor.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndex("password")
                    if (columnIndex != -1) {
                        pass = it.getString(columnIndex)
                    } else {
                        // Log an error if the column index is -1
                        Log.e("MyDatabaseHelper", "Column 'password' not found in cursor")
                    }
                } else {
                    // Log a message if no rows were returned by the query
                    Log.d("MyDatabaseHelper", "No rows returned for username: $username")
                }
            }
            cursor.close();
            val passwordMatch = BCrypt.checkpw(password, pass)
            if(passwordMatch){
                Toast.makeText(context, "Login was successful", Toast.LENGTH_SHORT).show()
            }
            return passwordMatch
        }

        cursor.close();
        db.close()
        return false;
    }

    fun serverRegister(name: String, email: String, pass: String, context: Context, callback: (Boolean) -> Unit){
        val queue = Volley.newRequestQueue(context)
        val url = "http://10.0.2.2/register.php"

        val params = HashMap<String, String>()
        params["name"] = name
        params["pass"] = pass
        params["email"] = email

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                if(response.equals("success")){
                    callback(true)
                }
                else{
                    Log.d("SERVASresponse", response)
                    callback(false)
                }
            },
            Response.ErrorListener { error ->

                Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
                Log.d("SERVASklaida", error.message.toString())
                callback(false)
            })

        {
            override fun getParams(): Map<String, String> {
                return params
            }

        }

        queue.add(stringRequest)
        //return successBool
    }

    //NENAUDOTI
    fun serverLogin(name: String, pass: String, context: Context, callback: (Boolean) -> Unit){
        val queue = Volley.newRequestQueue(context)
        val url = "http://10.0.2.2/login.php"

        val params = HashMap<String, String>()
        params["name"] = name
        params["pass"] = pass

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                val jsonObj = JSONObject(response)
                val status = jsonObj.getString("status")
                if(status.equals("success")){
                    val name = jsonObj.getString("name")
                    val email = jsonObj.getString("email")
                    val pass = jsonObj.getString("password")
                    val balance = jsonObj.getDouble("balance")
                    val height = jsonObj.getDouble("height")
                    val weight = jsonObj.getDouble("weight")
                    val transportation = jsonObj.getInt("transportation")
                    addFull(name, email, pass, balance, height, weight, transportation)
                    Log.d("SERVASresponse", jsonObj.getString("message"))
                    callback(true)
                }
                else if(status.equals("failure")) {
                    Log.d("SERVASresponse", jsonObj.getString("message"))
                    callback(false)
                }
                else{
                    Log.d("SERVASresponse", response)
                    callback(false)
                }
            },
            Response.ErrorListener { error ->

                Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
                Log.d("SERVASklaida", error.message.toString())
                callback(false)
            })

        {
            override fun getParams(): Map<String, String> {
                return params
            }

        }

        queue.add(stringRequest)
        //return successBool
    }




}
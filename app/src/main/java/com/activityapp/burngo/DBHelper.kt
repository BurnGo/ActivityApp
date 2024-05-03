package com.activityapp.burngo

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class DBHelper(context: Context):SQLiteOpenHelper(context, "app.sqlite", null, 1) {
    lateinit var session: Session

    override fun onCreate(db: SQLiteDatabase?) {

        /*
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
        );*/

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        /*
        db?.execSQL("drop table if exists Transportation_types");
        db?.execSQL("drop table if exists Steps");*/
    }

    fun performRegistration(username: String, email: String, password: String, context: Context){
        serverRegister(username, email, password, context){ isSuccess ->
            if(isSuccess){
                //Toast.makeText(context, "Registration was successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
            }
            else{
                //Toast.makeText(context, "Registration was not successful", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun performLogin(username: String, password: String, context: Context){

        serverLogin(username, password, context){ isSuccess ->
            if(isSuccess){
                //Toast.makeText(context, "Login was successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }
            else{
                //Toast.makeText(context, "Login was not successful", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun performUpdateUser(name: String, balance: Double, height: Double, context: Context,
                          weight: Double, transportation: Int){
        serverUpdateUser(name, balance, height, context, weight, transportation){ isSuccess ->
            Log.d("success", isSuccess.toString())
        }

    }

    fun performAddSteps(stepsCount: Int, distance: Double, caloriesBurned: Int,
                        co2_saved: Double, time: Double, userID: Int, context: Context){
        serverAddSteps(stepsCount, distance, caloriesBurned, co2_saved, time, userID, context){ isSuccess ->
            Log.d("success", isSuccess.toString())
        }
    }


    fun serverRegister(name: String, email: String, pass: String, context: Context, callback: (Boolean) -> Unit){
        val queue = Volley.newRequestQueue(context)
        val url = "http://20.215.225.10/register.php"

        val params = HashMap<String, String>()
        params["name"] = name
        params["pass"] = pass
        params["email"] = email

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                if(response.equals("Registration was successful")){
                    Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show()
                    callback(true)
                }
                else{
                    Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show()
                    Log.d("SERVASresponse", response)
                    callback(false)
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                //Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
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


    fun serverLogin(name: String, pass: String, context: Context, callback: (Boolean) -> Unit){
        val queue = Volley.newRequestQueue(context)
        val url = "http://20.215.225.10/login.php"

        val params = HashMap<String, String>()
        params["name"] = name
        params["pass"] = pass

        session = Session(context)

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                val jsonObj = JSONObject(response)
                val status = jsonObj.getString("status")
                if(status.equals("success")){
                    Log.d("SERVASresponse", jsonObj.getString("message"))
                    Toast.makeText(context, jsonObj.getString("message"), Toast.LENGTH_SHORT).show()
                    val userID = jsonObj.getInt("userID")
                    val name = jsonObj.getString("name")
                    val balance = jsonObj.getDouble("balance")
                    var height = 0.0
                    var weight = 0.0
                    var transportation = -1
                    if(!jsonObj.get("height").equals(null)){
                        height = jsonObj.getDouble("height")
                    }
                    if(!jsonObj.get("weight").equals(null)){
                        weight = jsonObj.getDouble("weight")
                    }
                    if(!jsonObj.get("transportation").equals(null)){
                        transportation = jsonObj.getInt("transportation")
                    }
                    session.createSession(userID, name, balance, height, weight, transportation)
                    callback(true)
                }
                else if(status.equals("failure")) {
                    Toast.makeText(context, jsonObj.getString("message"), Toast.LENGTH_SHORT).show()
                    Log.d("SERVASresponse", jsonObj.getString("message"))
                    callback(false)
                }
                else{
                    Toast.makeText(context, jsonObj.getString("message"), Toast.LENGTH_SHORT).show()
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
    }

    fun serverUpdateUser(name: String, balance: Double, height: Double, context: Context,
                         weight: Double, transportation: Int, callback: (Boolean) -> Unit){

        val queue = Volley.newRequestQueue(context)
        val url = "http://20.215.225.10/updateUser.php"

        val params = HashMap<String, String>()
        params["name"] = name
        params["balance"] = balance.toString()
        params["height"] = height.toString()
        params["weight"] = weight.toString()
        params["transportation"] = transportation.toString()

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                if(response.equals("Success")){
                    Log.d("SERVASresponse", response.toString())
                    callback(true)
                }
                else if(response.equals("Unsuccessful")) {
                    Log.d("SERVASresponse", response.toString())
                    callback(false)
                }
                else{
                    Log.d("SERVASresponse", response.toString())
                    callback(false)
                }
            },
            Response.ErrorListener { error ->
                callback(false)
            })

        {
            override fun getParams(): Map<String, String> {
                return params
            }

        }

        queue.add(stringRequest)
    }

    fun serverAddSteps(stepsCount: Int, distance: Double, caloriesBurned: Int,
        co2_saved: Double, time: Double, userID: Int, context: Context, callback: (Boolean) -> Unit){

        val queue = Volley.newRequestQueue(context)
        val url = "http://20.215.225.10/addSteps.php"

        val params = HashMap<String, String>()
        params["steps"] = stepsCount.toString()
        params["distance"] = distance.toString()
        params["calories"] = caloriesBurned.toString()
        params["co2"] = co2_saved.toString()
        params["time"] = time.toString()
        params["userID"] = userID.toString()

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                if(response.equals("Success")){
                    Log.d("SERVASresponse", response.toString())
                    callback(true)
                }
                else if(response.equals("Unsuccessful")) {
                    Log.d("SERVASresponse", response.toString())
                    callback(false)
                }
                else{
                    Log.d("SERVASresponse", response.toString())
                    callback(false)
                }
            },
            Response.ErrorListener { error ->
                callback(false)
            })

        {
            override fun getParams(): Map<String, String> {
                return params
            }

        }

        queue.add(stringRequest)
    }

    /*
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
    }*/




}
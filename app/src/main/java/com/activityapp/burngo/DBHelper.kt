package com.activityapp.burngo

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


interface VolleyCallback {
    fun onSuccessResponse(result: JSONArray?)
}

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



    fun getAllUsersWithSteps(context: Context, callback: (List<User>) -> Unit){
        val queue = Volley.newRequestQueue(context);
        val url = "http://20.215.225.10/getAllNames.php"

        //var names = mutableListOf<String>()
        //var ids = mutableListOf<Int>()
        val users = mutableListOf<User>()
        val request = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val name = obj.getString("username")
                    val id = obj.getInt("id_User")
                    getStepsById(id, context){steps ->
                        val user = User(id, name, steps)
                        users.add(user)
                    }
                    //names.add(name)
                    //ids.add(id)

                }
                callback(users)


            },
            { error ->
                Log.d("Negerai", error.message.toString())
            }
        )
        queue.add(request)

    }

    fun getStepsById(Id: Int, context: Context, callback: (List<Steps>) -> Unit){
        val queue = Volley.newRequestQueue(context);
        val url = "http://20.215.225.10/getStepsByID.php?id=${Id}"
        val steps = mutableListOf<Steps>()

        val request = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    for (i in 0 until response.length()){
                        val obj = response.getJSONObject(i)
                        val step = obj.getInt("steps_count")
                        var date = obj.getString("date")
                        date = date.trim()
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val dateTime = LocalDate.parse(date, formatter)
                        val stepObj = Steps(step, dateTime)
                        steps.add(stepObj)

                    }
                    callback(steps)
                }
                catch(e: Exception){
                    Log.d("Oopsie", e.message.toString())
                }
            },
            { error ->
                Log.d("Negerai", error.message.toString())
            }
        )
        queue.add(request)
    }
}

data class User(val id: Int, val name: String, val steps: List<Steps>)

data class Steps(val stepsC: Int, val date: LocalDate)
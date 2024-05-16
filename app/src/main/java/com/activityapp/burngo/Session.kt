package com.activityapp.burngo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

class Session {
    lateinit var pref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var con: Context
    var PRIVATEMODE: Int = 0

    constructor(context: Context){
        this.con = context
        pref = context.getSharedPreferences(PREF_NAME, PRIVATEMODE)
        editor = pref.edit()
    }

    companion object{
        val PREF_NAME = "Session_pref"
        val IS_LOGIN = "isLoggedin"
        val KEY_ID = "userID"
        val KEY_USERNAME = "username"
        val KEY_CURRENCY = "currency_balance"
        val KEY_HEIGHT = "height"
        val KEY_WEIGHT = "weight"
        val KEY_TRANS = "transportation"
        val KEY_STEPS = "steps"
    }

    fun createSession(userID: Int, username: String, currency: Double, height: Double, weight: Double, transportation: Int){
        editor.putBoolean(IS_LOGIN, true)
        editor.putInt(KEY_ID, userID)
        editor.putString(KEY_USERNAME, username)
        editor.putFloat(KEY_CURRENCY, currency.toFloat())
        editor.putFloat(KEY_HEIGHT, height.toFloat())
        editor.putFloat(KEY_WEIGHT, weight.toFloat())
        editor.putInt(KEY_TRANS, transportation)
        editor.putInt(KEY_STEPS, 0)
        editor.commit()
    }


    fun isLoggedIn(): Boolean{
        return pref.getBoolean(IS_LOGIN, false)
    }

    fun logout(){
        editor.clear()
        editor.commit()
        con.startActivity(Intent(con, LoadingPage::class.java))
    }

    fun getUserDetails(): HashMap<String, Any>{
        var user = HashMap<String, Any>()
        user.put(KEY_ID, pref.getInt(KEY_ID, 0))
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null)!!)
        user.put(KEY_CURRENCY, pref.getFloat(KEY_CURRENCY, 0.0.toFloat()))
        user.put(KEY_HEIGHT, pref.getFloat(KEY_HEIGHT, 0.0.toFloat()))
        user.put(KEY_WEIGHT, pref.getFloat(KEY_WEIGHT, 0.0.toFloat()))
        user.put(KEY_TRANS, pref.getInt(KEY_TRANS, 0))
        return user
    }

    fun updateUserDetails(height: Double, weight: Double, transportation: Int){
        editor.putFloat(KEY_HEIGHT, height.toFloat())
        editor.putFloat(KEY_WEIGHT, weight.toFloat())
        editor.putInt(KEY_TRANS, transportation)
        editor.commit()
    }

    fun updateStepsCount(steps: Int){
        editor.putInt(KEY_STEPS, steps)
        editor.commit()
    }

    fun resetSteps(){
        editor.putInt(KEY_STEPS, 0)
    }

    fun getSteps(): Int{
        val steps = pref.getInt(KEY_STEPS, 0)
        return steps
    }

    fun getId(): Int{
        val id = pref.getInt(KEY_ID, 0)
        return id
    }



}
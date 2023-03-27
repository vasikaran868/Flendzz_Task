package com.example.flendzztask

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.example.flendzztask.datamodels.Employee
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val extras = intent.extras
        if (extras != null) {
            val value = extras.getInt("id")
            request(id = value)
        }

    }

    fun request(id: Int){
        if (isConnected()){
            getEmployee(id)
        }
        else{
            showFailedSnackbar("No Internet Connection")
        }
    }

    fun getEmployee(id:Int){
        GlobalScope.launch {
            try {
                val result = FlendzApi.retrofitService.getEmployee(id)
                result.enqueue(
                    object : Callback<String> {
                        override fun onResponse(
                            call: Call<String>,
                            response: Response<String>
                        ) {
                            Log.v("MyActivity","${response}")
                            if (response.code() == 200){
                                response.body()?.let {
                                    val data = Gson().fromJson(response.body(), Employee::class.java)
                                    Log.v("MyActivity","${data}")
                                    findViewById<TextView>(R.id.id_tv).text = "Employee id: " + data.id.toString()
                                    var name = data.name.replace(" ","")
                                    name = name.replaceFirstChar { it.lowercase() }
                                    findViewById<TextView>(R.id.name_tv).text = "Name: " + name
                                    findViewById<TextView>(R.id.email_tv).apply {
                                        text = data.email.toLowerCase()
                                        setOnClickListener {
                                            val intent = Intent(Intent.ACTION_SEND);
                                            intent.setType("text/plain");
                                            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(data.email));
                                            startActivity(intent);
                                        }
                                    }
                                    findViewById<TextView>(R.id.addess).text = "Address: " + data.address.suite + " , " + data.address.street + " , " + data.address.city + " , " + data.address.zipcode
                                    findViewById<TextView>(R.id.phone_tv).apply {
                                        text = data.phone
                                        setOnClickListener {
                                            val intent = Intent(Intent.ACTION_DIAL)
                                            intent.data = Uri.parse("tel:${data.phone}")
                                            startActivity(intent)
                                        }
                                    }
                                    findViewById<TextView>(R.id.company_name_tv).text = "Company name: " +  data.company.name
                                    findViewById<TextView>(R.id.company_web_tv).text = "Company website: " + data.website

                                    findViewById<LinearLayoutCompat>(R.id.linearLayoutCompat).visibility = View.VISIBLE
                                    findViewById<LinearLayoutCompat>(R.id.linearLayoutCompat2).visibility = View.VISIBLE
                                }
                            }
                            else{

                            }
                        }
                        override fun onFailure(call: Call<String>, t: Throwable) {

                        }
                    }
                )
            }catch (e :Exception){
                Log.v("MyActivity","$e")

            }
        }

    }

    fun showFailedSnackbar(msg: String){
        val snackbar = Snackbar.make(findViewById(R.id.linearLayoutCompat),"  $msg", Snackbar.LENGTH_SHORT)
        val snackbarView = snackbar.view
        val tv = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        tv.gravity = Gravity.CENTER
        tv.setTextColor(resources.getColor(R.color.white))
        tv.setTypeface(null, Typeface.BOLD)
        snackbarView.setBackgroundColor(resources.getColor(R.color.red))
        snackbar.setAnchorView(findViewById(R.id.anchor))
        snackbar.show()
    }

    @Throws(InterruptedException::class, IOException::class)
    fun isConnected(): Boolean {
        val command = "ping -c 1 google.com"
        return Runtime.getRuntime().exec(command).waitFor() == 0
    }
}
package com.example.flendzztask

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flendzztask.adapter.EmployeeListRecViewAdapter
import com.example.flendzztask.datamodels.Employee
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.InetAddress


class MainActivity : AppCompatActivity() {

    lateinit var adapter :EmployeeListRecViewAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recView = findViewById<RecyclerView>(R.id.employees_rec_view)
        val reloadBtn = findViewById<Button>(R.id.reload_btn)

        adapter = EmployeeListRecViewAdapter(
            navigateAction = {
                Log.v("MyActivity","item called")

                val intent = Intent(this, DetailsActivity::class.java)
                intent.putExtra("id",it.id)
                startActivity(intent)
            },
            toMailAction = {
                val intent = Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(it.email));
                startActivity(intent);
            }
        )

        recView.adapter = adapter
        recView.layoutManager = LinearLayoutManager(this)

        request()

        reloadBtn.setOnClickListener {
            request()
        }

    }

    fun request(){
        if (isConnected()){
            getEmployeeList()
        }
        else{
            showFailedSnackbar("No Internet Connection")
        }
    }

    fun getEmployeeList(){
        GlobalScope.launch {
            try {
                val result = FlendzApi.retrofitService.getEmployeeList()
                result.enqueue(
                    object : Callback<String> {
                        override fun onResponse(
                            call: Call<String>,
                            response: Response<String>
                        ) {
                            Log.v("MyActivity","${response}")
                            if (response.code() == 200){
                                response.body()?.let {
                                    val data = Gson().fromJson(response.body(), Array<Employee>::class.java).toList()
                                    Log.v("MyActivity","${data}")
                                    adapter.submitList(data)
                                }
                            }
                        }
                        override fun onFailure(call: Call<String>, t: Throwable) {
                            Log.v("MyActivity","$t")
                        }
                    }
                )
            }catch (e :Exception){
                Log.v("MyActivity","$e")

            }
        }
    }

    fun showFailedSnackbar(msg: String){
        val snackbar = Snackbar.make(findViewById(R.id.employees_rec_view),"  $msg", Snackbar.LENGTH_SHORT)
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
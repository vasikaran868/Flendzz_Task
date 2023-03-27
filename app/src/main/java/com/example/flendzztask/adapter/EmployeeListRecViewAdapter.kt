package com.example.flendzztask.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.flendzztask.R
import com.example.flendzztask.databinding.EmployeeVhBinding
import com.example.flendzztask.datamodels.Employee

class EmployeeListRecViewAdapter (val navigateAction : (Employee) -> Unit, val toMailAction:(Employee) -> Unit ):
    ListAdapter<Employee, EmployeeListRecViewAdapter.EmployeeViewHolder>(
        Diffcallback
    ) {


    class EmployeeViewHolder(private var binding: EmployeeVhBinding): RecyclerView.ViewHolder(binding.root){

        val emailTv = binding.employeeEmailTv

        fun bind(employee: Employee){
            binding.apply {
                employeeNameTv.text = employee.name
                employeeEmailTv.text = employee.email.toLowerCase()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        return EmployeeViewHolder(
            EmployeeVhBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        var employee = getItem(position)

        holder.bind(employee)

        holder.itemView.setOnClickListener {
            navigateAction(employee)
        }

        holder.emailTv.setOnClickListener {
            toMailAction(employee)
        }
    }
    companion object{
        private val Diffcallback = object: DiffUtil.ItemCallback<Employee>(){
            override fun areItemsTheSame(oldItem: Employee, newItem:Employee): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean {
                return oldItem == newItem
            }
        }
    }

}
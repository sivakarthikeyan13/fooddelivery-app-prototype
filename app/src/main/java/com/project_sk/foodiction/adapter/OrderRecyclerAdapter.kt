package com.project_sk.foodiction.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project_sk.foodiction.R
import com.project_sk.foodiction.model.Dish

class OrderRecyclerAdapter(val context: Context, val orderList: ArrayList<Dish>): RecyclerView.Adapter<OrderRecyclerAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_single_order, parent, false)

        return OrderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val dish = orderList[position]
        holder.txtOrderName.text = dish.dishName
        holder.txtOrderPrice.text = dish.dishCost
    }

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtOrderName: TextView = view.findViewById(R.id.txtOrderName)
        val txtOrderPrice: TextView = view.findViewById(R.id.txtOrderPrice)
    }
}
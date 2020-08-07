package com.project_sk.foodiction.adapter

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.gson.Gson
import com.project_sk.foodiction.R
import com.project_sk.foodiction.database.OrderDatabase
import com.project_sk.foodiction.database.OrderEntity
import com.project_sk.foodiction.model.Dish

class DishRecyclerAdapter(val context: Context, val itemList: ArrayList<Dish>) :
    RecyclerView.Adapter<DishRecyclerAdapter.DishViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_single_dish, parent, false)

        return DishViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        val dish = itemList[position]
        holder.txtDishNumber.text = (position + 1).toString()
        holder.txtDishName.text = dish.dishName
        holder.txtDishPrice.text = dish.dishCost

        holder.btnAdd.setOnClickListener {

            val gson = Gson()
            val foodItem = gson.toJson(dish)
            val dishEntity = OrderEntity(
                dish.dishId.toInt(),
                foodItem
            )

            val checkDish = ItemsOfCart(
                context,
                dishEntity,
                1
            ).execute()
            val isDish = checkDish.get()
            if (isDish) {
                holder.btnAdd.text = "remove"
                val removeColor = ContextCompat.getColor(
                    context,
                    R.color.removeCart
                )
                holder.btnAdd.setBackgroundColor(removeColor)
            } else {
                holder.btnAdd.text = "add"
                val addColor = ContextCompat.getColor(
                    context,
                    R.color.colorPrimary
                )
                holder.btnAdd.setBackgroundColor(addColor)
            }
            if (!isDish) {
                val addFav =
                    ItemsOfCart(
                        context,
                        dishEntity,
                        2
                    ).execute()
                val result = addFav.get()

                if (result) {
                    holder.btnAdd.text = "remove"
                    val removeColor = ContextCompat.getColor(
                        context,
                        R.color.removeCart
                    )
                    holder.btnAdd.setBackgroundColor(removeColor)
                }
            } else {
                val removeDish =
                    ItemsOfCart(
                        context,
                        dishEntity,
                        3
                    ).execute()
                val result = removeDish.get()

                if (result) {
                    holder.btnAdd.text = "add"
                    val addColor = ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                    holder.btnAdd.setBackgroundColor(addColor)
                }
            }
        }


    }

    class DishViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDishNumber: TextView = view.findViewById(R.id.txtDishNumber)
        val txtDishName: TextView = view.findViewById(R.id.txtDishName)
        val txtDishPrice: TextView = view.findViewById(R.id.txtDishPrice)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)
    }

    class ItemsOfCart(
        context: Context,
        val orderEntity: OrderEntity,
        val mode: Int
    ) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db").build()


        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    val dish: OrderEntity? =
                        db.orderDao().getDishById(orderEntity.dish_id.toString())
                    db.close() //to close the database
                    return dish != null //if dish is null it retuns false
                }
                2 -> {
                    db.orderDao().insertDish(orderEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.orderDao().deleteDish(orderEntity)
                    db.close()
                    return true
                }
            }

            return false
        }

    }


}
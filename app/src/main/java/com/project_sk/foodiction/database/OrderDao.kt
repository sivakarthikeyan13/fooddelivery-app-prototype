package com.project_sk.foodiction.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OrderDao {

    @Insert
    fun insertDish(orderEntity: OrderEntity)

    @Delete
    fun deleteDish(orderEntity: OrderEntity)

    @Query("SELECT * FROM cart")
    fun getAllDish(): List<OrderEntity>

    @Query("SELECT * FROM cart WHERE dish_id = :dishId")
    fun getDishById(dishId: String): OrderEntity

    @Query("DELETE FROM cart")
    fun delAllDish()
}
package com.project_sk.foodiction.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class OrderEntity(
    @PrimaryKey val dish_id: Int,
    @ColumnInfo(name = "food_item") val foodItem: String
) {
}
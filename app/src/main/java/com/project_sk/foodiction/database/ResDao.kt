package com.project_sk.foodiction.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ResDao {

    @Insert //to use this function to insert
    fun insertRes(resEntity: ResEntity)

    @Delete
    fun deleteRes(resEntity: ResEntity)
    /*for insert and delete operation the room library takes
    care of it(inbuilt in room). For other operations we need to write the SQL query*/

    @Query("SELECT * FROM restaurants")  //to return all entries of database
    fun getAllRes(): List<ResEntity>

    @Query("SELECT * FROM restaurants WHERE res_id = :resId") //: is added to indicate that the value will come from the function below when it is called
    fun getResById(resId: String): ResEntity



    /* the function body is not specified because the functions are
     spedified in database class and the room library takes care of it
     Hence the dao is an interface and not class*/
}
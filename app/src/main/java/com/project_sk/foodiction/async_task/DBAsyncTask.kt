package com.project_sk.foodiction.async_task

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room
import com.project_sk.foodiction.database.ResEntity
import com.project_sk.foodiction.database.RestaurantDatabase

class DBAsyncTask(val context: Context, val resEntity: ResEntity, val mode: Int): AsyncTask<Void, Void, Boolean>() {
    /*mode 1 -> Check DB if the restaurant is in favourites or not
  mode 2 -> Save the restaurant into DB as favourites
  mode 3 -> Remove the favourite restaurant*/

    val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()

    override fun doInBackground(vararg params: Void?): Boolean {

        when(mode){
            1 -> {
                val res: ResEntity? = db.resDao().getResById(resEntity.res_id.toString())
                db.close() //to close the database
                return res != null //if book is null it retuns false
            }

            2 -> {
                db.resDao().insertRes(resEntity)
                db.close()
                return true
            }

            3 -> {
                db.resDao().deleteRes(resEntity)
                db.close()
                return true
            }
        }
        return false
    }
}
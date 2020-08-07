package com.project_sk.foodiction.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.project_sk.foodiction.R
import com.project_sk.foodiction.activity.CartPageActivity
import com.project_sk.foodiction.activity.MainActivity
import com.project_sk.foodiction.activity.RestaurantDetailsActivity
import com.project_sk.foodiction.async_task.DBAsyncTask
import com.project_sk.foodiction.database.OrderDatabase
import com.project_sk.foodiction.database.ResEntity
import com.project_sk.foodiction.database.RestaurantDatabase
import com.project_sk.foodiction.fragment.FavouritesPageFragment
import com.project_sk.foodiction.fragment.OrderHistoryFragment
import com.project_sk.foodiction.model.Restaurant
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_single_row.view.*

class HomeRecyclerAdapter(
    val context: Context,
    val itemList: ArrayList<Restaurant>,
    val fav: Boolean
) :
    RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_single_row, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val res = itemList[position]
        holder.txtResName.text = res.resName
        holder.txtResRating.text = res.resRating
        holder.txtResPrice.text = res.resCost
        Picasso.get().load(res.resImage).error(R.drawable.ic_default_res_image)
            .into(holder.imgResImage)

        val resEntity = ResEntity(
            res.resId.toInt(),
            res.resName,
            res.resRating,
            res.resCost,
            res.resImage
        )

        val checkFav = DBAsyncTask(
            context,
            resEntity,
            1
        ).execute()

        val isFav = checkFav.get()
        if (isFav) {
            holder.btnAddFav.setBackgroundResource(R.drawable.ic_favourite_red)
        } else {
            holder.btnAddFav.setBackgroundResource(R.drawable.ic_favorite_border)
        }

        holder.btnAddFav.setOnClickListener {

            if (!isFav) {
                val addFav =
                    DBAsyncTask(
                        context,
                        resEntity,
                        2
                    ).execute()
                val result = addFav.get()

                if (result) {
                    holder.btnAddFav.setBackgroundResource(R.drawable.ic_favourite_red)
                    this.notifyDataSetChanged()
                    Toast.makeText(
                        context,
                        "Restaurant added to favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val removeFav =
                    DBAsyncTask(
                        context,
                        resEntity,
                        3
                    ).execute()
                val result = removeFav.get()

                if (result) {
                    holder.btnAddFav.setBackgroundResource(R.drawable.ic_favorite_border)
                    this.notifyDataSetChanged()
                    if (fav){
                        itemList.removeAt(position)
                        this.notifyItemRemoved(position)
                        this.notifyItemRangeChanged(position,itemList.size)
                    }

                    Toast.makeText(
                        context,
                        "Restaurant removed from favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        holder.llContent.setOnClickListener {
            val intent = Intent(context, RestaurantDetailsActivity::class.java)
            intent.putExtra("res_id", res.resId)
            intent.putExtra("res_name", res.resName)
            val checkFav = DBAsyncTask(
                context,
                resEntity,
                1
            ).execute()

            val isFav = checkFav.get()
            intent.putExtra("is_fav", isFav)
            val delCart = CartPageActivity.DelCartAsync(context).execute().get()
            context.startActivity(intent) //used to open the activity
        }


    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtResName: TextView = view.findViewById(R.id.txtResName)
        val txtResRating: TextView = view.findViewById(R.id.txtResRating)
        val txtResPrice: TextView = view.findViewById(R.id.txtResPrice)
        val imgResImage: ImageView = view.findViewById(R.id.imgResImage)
        val btnAddFav: Button = view.findViewById(R.id.btnAddFav)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)


    }

    class DelCartAsync(val context: Context) : AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {

            val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db")
                .build() //intialise the database
            db.orderDao().delAllDish()
            return true
        }

    }


}
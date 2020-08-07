package com.project_sk.foodiction.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

import com.project_sk.foodiction.R
import com.project_sk.foodiction.adapter.HomeRecyclerAdapter
import com.project_sk.foodiction.database.ResEntity
import com.project_sk.foodiction.database.RestaurantDatabase
import com.project_sk.foodiction.model.Restaurant

/**
 * A simple [Fragment] subclass.
 */
class FavouritesPageFragment : Fragment() {

    lateinit var recyclerFav: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var rlFav: RelativeLayout
    lateinit var rlNoFav: RelativeLayout
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    var favInfoList = ArrayList<Restaurant>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourites_page, container, false)

        rlFav = view.findViewById(R.id.rlFav)
        rlNoFav = view.findViewById(R.id.rlNoFav)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE
        setUpRecycler(view)
        return view
    }

    private fun setUpRecycler(view: View) {
        recyclerFav = view.findViewById(R.id.recyclerFavourites)


        /*In case of favourites, simply extract all the data from the DB and send to the adapter.
        * Here we can reuse the adapter created for the home fragment. This will save our time and optimize our app as well*/
        val backgroundList = FavouritesAsync(activity as Context).execute().get()
        if (backgroundList.isEmpty()) {
            progressLayout.visibility = View.GONE //to make the progresslayout visible
            rlFav.visibility = View.GONE
            rlNoFav.visibility = View.VISIBLE
        } else {
            rlFav.visibility = View.VISIBLE
            progressLayout.visibility = View.GONE //to make the progresslayout visible
            rlNoFav.visibility = View.GONE
            for (i in backgroundList) {
                favInfoList.add(
                    Restaurant(
                        i.res_id.toString(),
                        i.resName,
                        i.resRating,
                        i.resCost,
                        i.resImage
                    )
                )
            }

            if (activity != null) {
                recyclerAdapter = HomeRecyclerAdapter(
                    activity as Context,
                    favInfoList,
                    true
                ) //sending the list to adapter class
                layoutManager = LinearLayoutManager(activity) //for linear recyler layout
                recyclerFav.layoutManager = layoutManager
                recyclerFav.itemAnimator = DefaultItemAnimator()
                recyclerFav.adapter = recyclerAdapter
                recyclerFav.setHasFixedSize(true)

            }
            /*allRestaurantsAdapter = AllRestaurantsAdapter(favInfoList, activity as Context)
            val mLayoutManager = LinearLayoutManager(activity)
            recyclerRestaurant.layoutManager = mLayoutManager
            recyclerRestaurant.itemAnimator = DefaultItemAnimator()
            recyclerRestaurant.adapter = allRestaurantsAdapter
            recyclerRestaurant.setHasFixedSize(true)*/
        }

    }

    class FavouritesAsync(val context: Context) : AsyncTask<Void, Void, List<ResEntity>>() {
        override fun doInBackground(vararg params: Void?): List<ResEntity> {
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db")
                .build() //intialise the database

            return db.resDao().getAllRes()
        }

    }
}

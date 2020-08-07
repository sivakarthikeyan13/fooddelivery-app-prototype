package com.project_sk.foodiction.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.content.SharedPreferences
import android.provider.Settings
import android.widget.FrameLayout
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.project_sk.foodiction.R
import com.project_sk.foodiction.fragment.*
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    var previousMenuItem: MenuItem? = null

    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.DrawerLayout)
        coordinatorLayout = findViewById(R.id.CoordinatorLayout)
        toolbar = findViewById(R.id.Toolbar)
        frameLayout = findViewById(R.id.frame)
        navigationView = findViewById(R.id.NavigationView)
        setUpToolbar()
        openHome()


        val actionBarDrawerToggle = ActionBarDrawerToggle(this@MainActivity,
        drawerLayout,
        R.string.navigation_drawer_open,
        R.string.navigation_drawer_close)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem!=null){
                previousMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when(it.itemId){
                R.id.home -> {
                    drawerLayout.closeDrawers()
                    openHome()
                }
                R.id.myProfile -> {
                    drawerLayout.closeDrawers()
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            ProfilePageFragment()
                        )
                        .commit()
                    supportActionBar?.title = "My Profile"
                }

                R.id.favouriteRestaurants -> {
                    drawerLayout.closeDrawers()
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FavouritesPageFragment()
                        )
                        .commit()
                    supportActionBar?.title = "Favourite Restaurants"
                }
                R.id.orderHistory -> {
                    drawerLayout.closeDrawers()
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            OrderHistoryFragment()
                        )
                        .commit()
                    supportActionBar?.title = "Order History"

                }
                R.id.faqs -> {
                    drawerLayout.closeDrawers()
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FaqPageFragment()
                        )
                        .commit()
                    supportActionBar?.title = "FAQs"

                }
                R.id.logOut -> {
                    val dialog = AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you sure you want to log out?")
                    dialog.setPositiveButton("Yes") { text, listener ->
                        sharedPreferences =
                            getSharedPreferences(getString(R.string.profile_file_name), Context.MODE_PRIVATE)
                        sharedPreferences.edit().clear().apply()
                        val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(loginIntent)
                        finish()

                    }
                    dialog.setNegativeButton("No") { text, listener ->
                        openHome()
                        drawerLayout.closeDrawers()
                    }
                    dialog.create()
                    dialog.show()

                }

            }

            return@setNavigationItemSelectedListener true
        }

    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if(id==android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    fun openHome() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.frame,
                HomePageFragment()
            )
            .commit()
        supportActionBar?.title = "Dashboard"
        navigationView.setCheckedItem(R.id.home)
    }

    override fun onBackPressed() {
            val frag = supportFragmentManager.findFragmentById(R.id.frame)
            when (frag) {
                !is HomePageFragment -> openHome()

                else -> super.onBackPressed()
            }
    }
}

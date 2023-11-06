package com.example.courseer2

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.content.res.ColorStateList
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.core.content.ContextCompat

import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import com.google.android.material.navigation.NavigationView
import com.example.courseer2.databinding.ActivityUserViewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView


class UserView : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var selectedNavItem = R.id.bottom_prof
    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityUserViewBinding
    private lateinit var viewModel: YourViewModel
    private lateinit var dataBaseHandler: DataBaseHandler
    private lateinit var imageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var strandTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserViewBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

// Assuming you have defined the color 'gold' in your resources
        val goldColor = ContextCompat.getColor(this, R.color.gold)

// Set icon tint color
        bottomNavigationView.itemIconTintList = ColorStateList.valueOf(goldColor)

// Set text color
        bottomNavigationView.itemTextColor = ColorStateList.valueOf(goldColor)

        dataBaseHandler = DataBaseHandler(this)
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.nav_open,
            R.string.nav_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.drawerArrowDrawable.setDirection(DrawerArrowDrawable.ARROW_DIRECTION_END)

        binding.navigationDrawer.setNavigationItemSelectedListener(this)
        val navigationView = findViewById<NavigationView>(R.id.navigation_drawer)
        val displayMetrics = resources.displayMetrics
        val width = (displayMetrics.widthPixels * 0.7).toInt()


        val params = navigationView.layoutParams as DrawerLayout.LayoutParams
        params.width = width
        navigationView.layoutParams = params
        bottomNavigationView.itemIconSize = resources.getDimensionPixelSize(R.dimen.bottom_nav_icon_size)
        bottomNavigationView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
        binding.bottomNavigation.background = null
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.bottom_prof -> openFragment(UserProfile())
                R.id.bottom_apt -> openFragment(Aptitude())
                R.id.bottom_recom -> openFragment(CombinedRecommendFragment())
                R.id.bottom_fav -> openFragment(Favorites())

            }
            val view = bottomNavigationView.findViewById<View>(item.itemId)
            onBottomNavItemClicked(view)
            true
        }

        fragmentManager = supportFragmentManager
        openFragment(UserProfile())

        binding.reset.setOnClickListener {
            // Create an AlertDialog.Builder
            val builder = AlertDialog.Builder(this)

            // Set the title and message for the dialog
            builder.setTitle("Confirmation")
            builder.setMessage("Are you sure you want to reset your profile?")

            // Set positive and negative buttons
            builder.setPositiveButton("Yes") { _, _ ->
                // User clicked Yes, clear the database
                clearDatabase()
                Toast.makeText(this, "Profile is reset", Toast.LENGTH_SHORT).show()
            }

            builder.setNegativeButton("No") { dialog, _ ->
                // User clicked No, dismiss the dialog
                dialog.dismiss()
            }

            // Create and show the AlertDialog
            val alertDialog = builder.create()
            alertDialog.show()
        }

        val headerView = navigationView.getHeaderView(0)

        imageView = headerView.findViewById<ImageView>(R.id.imageView)
        nameTextView = headerView.findViewById<TextView>(R.id.nameTextView)
        strandTextView = headerView.findViewById<TextView>(R.id.strandTextView)
        loadImageAndUserDataFromDatabase()

        dataBaseHandler = DataBaseHandler(this)

        // Observe changes in your LiveData for user data
        binding.toolbar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.END)
            binding.toolbar.navigationIcon = toggle.drawerArrowDrawable
        }

        val darkerGrayColor = ContextCompat.getColor(this, android.R.color.darker_gray)

// Set icon tint color selector
        val iconTintList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            ),
            intArrayOf(goldColor, darkerGrayColor)
        )

        bottomNavigationView.itemIconTintList = iconTintList

    }
    private fun clearDatabase() {
        // Perform the database clearing logic here
        val dataBaseHandler = DataBaseHandler(this)
        dataBaseHandler.clearAllData()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_FIRST_RUN, true)
        startActivity(intent)

    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        clearBottomNavigationSelection()

        when (item.itemId) {
            R.id.nav_programs -> {
                openFragment(Programs())
                selectedNavItem = R.id.nav_programs
            }
            R.id.nav_scholarship -> {
                openFragment(Scholarship())
                selectedNavItem = R.id.nav_scholarship
            }
            R.id.nav_feedback -> {
                openFragment(Feedback())
                selectedNavItem = R.id.nav_feedback
            }
            R.id.nav_faqs -> {
                openFragment(FAQs())
                selectedNavItem = R.id.nav_faqs
            }
            R.id.menu_item_close_app -> {

                finish()
                true
            }
        }

        // Close the drawer properly
        binding.drawerLayout.closeDrawer(GravityCompat.END)

        return true
    }



    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun openFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    private fun clearBottomNavigationSelection() {
        binding.bottomNavigation.menu.findItem(selectedNavItem)?.isChecked = false
    }

    private fun loadImageAndUserDataFromDatabase() {
        val db: SQLiteDatabase = dataBaseHandler.readableDatabase

        // Assuming you want to retrieve the last row's data
        val query = "SELECT image, name, strand FROM User ORDER BY userid DESC LIMIT 1"
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            val imageData = cursor.getBlob(cursor.getColumnIndex("image"))
            val name = cursor.getString(cursor.getColumnIndex("name"))
            val strand = cursor.getString(cursor.getColumnIndex("strand"))

            // Set the image
            val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
            imageView.setImageBitmap(bitmap)

            // Set the name and strand to TextViews
            nameTextView.text = name
            strandTextView.text = strand
        }

        cursor.close()
        db.close()
    }

    fun onBottomNavItemClicked(view: View) {
        // Handle item clicks here
        val scaleFactor = 1.2f // You can adjust the scale factor as needed

        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat("scaleX", scaleFactor),
            PropertyValuesHolder.ofFloat("scaleY", scaleFactor)
        )

        scaleUp.duration = 200 // Adjust the duration of the animation as needed
        scaleUp.interpolator = OvershootInterpolator()

        scaleUp.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // Perform the actual fragment transaction after the animation ends
                when (view.id) {
                    R.id.bottom_prof -> openFragment(UserProfile())
                    R.id.bottom_apt -> openFragment(Aptitude())
                    R.id.bottom_recom -> openFragment(CombinedRecommendFragment())
                    R.id.bottom_fav -> openFragment(Favorites())
                }

                // Scale down to the original size after the fragment transaction
                val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                    view,
                    PropertyValuesHolder.ofFloat("scaleX", 1f),
                    PropertyValuesHolder.ofFloat("scaleY", 1f)
                )
                scaleDown.duration = 200 // Adjust the duration of the animation as needed
                scaleDown.interpolator = AccelerateInterpolator()
                scaleDown.start()
            }
        })

        scaleUp.start()
    }




}

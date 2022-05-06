package com.kardabel.go4lunch.ui.main

import android.Manifest.permission
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.kardabel.go4lunch.AuthenticationActivity
import com.kardabel.go4lunch.R
import com.kardabel.go4lunch.databinding.MainActivityBinding
import com.kardabel.go4lunch.di.ViewModelFactory
import com.kardabel.go4lunch.ui.autocomplete.PredictionViewState
import com.kardabel.go4lunch.ui.autocomplete.PredictionsAdapter
import com.kardabel.go4lunch.ui.detailsview.RestaurantDetailsActivity
import com.kardabel.go4lunch.ui.setting.SettingActivity
import com.kardabel.go4lunch.util.PermissionsViewAction

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    PredictionsAdapter.OnPredictionItemClickedListener {

    private var _binding: MainActivityBinding? = null
    private val binding get() = _binding!!

    private var mainActivityViewModel: MainActivityViewModel? = null
    private var toolbar: Toolbar? = null
    private var drawerLayout: DrawerLayout? = null
    private var appBarConfiguration: AppBarConfiguration? = null
    private var restaurantId: String? = null
    private var currentUserRestaurantChoiceStatus = 0
    private var adapter: PredictionsAdapter? = null

    private val locationPermissionCode = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // CONFIGURE VIEWMODEL
        val listViewModelFactory = ViewModelFactory.getInstance()
        mainActivityViewModel = ViewModelProvider(this, listViewModelFactory)[MainActivityViewModel::class.java]

        drawerLayout = binding.drawerLayout

        // CONFIGURE ALL VIEWS
        configureToolBar()
        configureDrawerLayout()
        configureNavigationView()
        configureDrawerMenu()

        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        val navController = findNavController(this,
            R.id.nav_host_fragment_activity_main)
        setupActionBarWithNavController(this, navController,
            appBarConfiguration!!)
        setupWithNavController(navView, navController)

        configureViewModel()
        updateUIWhenCreating()
        configureYourLunch()
        configureRecyclerView()
    }

    private fun configureToolBar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun configureDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawerLayout,
            toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        with(drawerLayout) {
            this?.addDrawerListener(toggle)
        }
        toggle.syncState()
    }

    private fun configureNavigationView() {
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun configureDrawerMenu() {
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration.Builder(
            R.id.navigation_mapview, R.id.navigation_listview, R.id.navigation_workmates)
            .setOpenableLayout(drawerLayout)
            .build()
    }

    private fun configureViewModel() {
        mainActivityViewModel!!.actionSingleLiveEvent.observe(this){ action ->
            when(action){
                PermissionsViewAction.PERMISSION_ASKED -> {
                    ActivityCompat.requestPermissions(this,
                        arrayOf(permission.ACCESS_FINE_LOCATION),
                        locationPermissionCode)
                    Toast.makeText(this, getString(R.string.need_your_position), Toast.LENGTH_SHORT).show()
                }
                PermissionsViewAction.PERMISSION_DENIED -> {
                    val alertDialogBuilder = MaterialAlertDialogBuilder(this)
                    alertDialogBuilder.setTitle(getString(R.string.title_alert))
                    alertDialogBuilder.setMessage(getString(R.string.rational))
                    alertDialogBuilder.show()
                }
                else -> {}
            }
        }
    }

    private fun updateUIWhenCreating() {
        val header = binding.navigationView.getHeaderView(0)
        val profilePicture = header.findViewById<ImageView>(R.id.header_picture)
        val profileUsername = header.findViewById<TextView>(R.id.header_name)
        val profileUserEmail = header.findViewById<TextView>(R.id.header_email)
        val currentUser = FirebaseAuth.getInstance().currentUser

        currentUser?.let { user ->
            //Get picture URL from Firebase
            val photoUrl = user.photoUrl
            photoUrl?.let { photo ->
                Glide.with(this)
                    .load(photo)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePicture)
            }
        }

        //Get email & username from Firebase
        val email =
            if (TextUtils.isEmpty(currentUser!!.email)) getString(R.string.info_no_email_found) else currentUser.email!!
        val username =
            if (TextUtils.isEmpty(currentUser.displayName)) getString(R.string.info_no_username_found) else currentUser.displayName!!

        //Update views with data
        profileUsername.text = username
        profileUserEmail.text = email
    }

    private fun configureYourLunch() {
        mainActivityViewModel!!.getUserRestaurantChoice()
        mainActivityViewModel!!.mainActivityYourLunchViewStateMediatorLiveData.observe(this){ userLunch ->
            restaurantId = userLunch.restaurantId
            currentUserRestaurantChoiceStatus = userLunch.currentUserRestaurantChoiceStatus
        }
    }

    private fun configureRecyclerView() {
        adapter = PredictionsAdapter(this)
        val recyclerView = findViewById<RecyclerView>(R.id.predictions_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        mainActivityViewModel!!.predictionsMediatorLiveData.observe(this) { predictions ->
            adapter!!.submitList(predictions)
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.your_lunch -> checkUserRestaurantChoice()
            R.id.settings -> { startActivity(Intent(this, SettingActivity::class.java)) }
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, AuthenticationActivity::class.java))
                finish()
            }
        }
        drawerLayout!!.closeDrawer(GravityCompat.START)
        return true
    }

    private fun checkUserRestaurantChoice() {
        when(currentUserRestaurantChoiceStatus){
            0 -> Toast.makeText(this, this.getString(R.string.no_restaurant_selected), Toast.LENGTH_SHORT).show()
            1 -> startActivity(RestaurantDetailsActivity.navigate(this, restaurantId!!))
        }
    }

    override fun onPredictionItemClicked(predictionText: String?) {
        mainActivityViewModel!!.userSearch(predictionText)
        initAutocomplete()
    }

    // CLEAR THE AUTOCOMPLETE ADAPTER WITH EMPTY LIST WHEN USER FINISHED HIS RESEARCH
    // TO CLEAN THE AUTOCOMPLETE RECYCLERVIEW
    private fun initAutocomplete() {
        val emptyList: List<PredictionViewState> = ArrayList()
        adapter!!.submitList(emptyList)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(this,
            R.id.nav_host_fragment_activity_main)
        return (navigateUp(navController, appBarConfiguration!!) || super.onSupportNavigateUp())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // INFLATE MENU
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)
        val item = menu!!.findItem(R.id.search)

        // GET SEARCHVIEW
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.maxWidth = Int.MAX_VALUE
        searchView.setBackgroundColor(Color.WHITE)
        val editText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        editText.setTextColor(Color.BLACK)
        editText.setHintTextColor(Color.GRAY)

        searchView.setIconifiedByDefault(false)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean { return false }

            override fun onQueryTextChange(newText: String): Boolean {
                mainActivityViewModel!!.sendTextToAutocomplete(newText)
                return false
            }
        })

        // WHEN USER LEAVES THE SEARCHVIEW, RESET AND CLOSE THE SEARCHVIEW
        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean { return true }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                mainActivityViewModel!!.userSearch("")
                return true
            }
        })

        return true
    }

    // WHEN VIEW IS ON RESUME CHECK THE PERMISSION STATE IN VIEWMODEL (AND PASSED THE ACTIVITY
    // FOR THE ALERTDIALOG EVEN IF ITS NOT THE GOOD WAY TO USE A VIEWMODEL,
    // WE DON'T HAVE OTHER CHOICE)
    override fun onResume() {
        super.onResume()
        mainActivityViewModel!!.checkPermission(this)
    }


}
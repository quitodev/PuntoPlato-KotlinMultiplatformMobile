package com.servirunplatomas.android.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.servirunplatomas.android.R
import com.servirunplatomas.android.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setBottomNavigation()
    }

    private fun setBottomNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainFragment) as NavHostFragment
        val navController = navHostFragment.navController
        //val navView: BottomNavigationView = findViewById(R.id.bottomNavigationMenu)
        //navView.setupWithNavController(navController)
    }
}
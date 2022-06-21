package com.servirunplatomas.android.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.servirunplatomas.android.R
import com.servirunplatomas.android.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        showSplashAnimation()
    }

    private fun showSplashAnimation() {
        val intent = Intent(this, MainActivity::class.java)
        val animation = AnimationUtils.loadAnimation(this, R.anim.animation_splash)
        binding.imageSplash.startAnimation(animation)

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) { }
            override fun onAnimationEnd(animation: Animation?) {
                startActivity(intent)
                finish()
            }
            override fun onAnimationStart(animation: Animation?) { }
        })
    }
}
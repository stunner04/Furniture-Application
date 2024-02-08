package com.example.furnitureapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import com.bumptech.glide.Glide
import com.example.furnitureapp.databinding.ActivitySplashScreennBinding

@Suppress("DEPRECATION")
@SuppressLint("CustomSplashScreen")
class SplashScreennActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreennBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        // Initialize view binding
        binding = ActivitySplashScreennBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val gifUrl = "https://drive.google.com/file/d/1s8ZlfDhGqMQo2gq5DrXssjkRB9PogvXy/view?usp=sharing"
//        binding.apply {
//
//            Glide.with(this@SplashScreennActivity)
//                .asGif()
//                .load(gifUrl)
//                .into(imgSofaGif)
//        }

        // Fade animation
        binding.imgAppIcon.alpha = 0f
        binding.imgAppIcon.animate().setDuration(2000).alpha(1f).withEndAction{
            val intent = Intent(this@SplashScreennActivity,LoginRegisterActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }

//        Handler().postDelayed({
//            val mainIntent = Intent(this@SplashScreennActivity, LoginRegisterActivity::class.java)
//            finish()
//            startActivity(mainIntent)
//        }, 2000)
    }
}
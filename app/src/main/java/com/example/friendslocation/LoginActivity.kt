package com.example.friendslocation

import android.os.Bundle

import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.friendslocation.Fragments.LoginFragment
import com.example.friendslocation.Fragments.OtpFragment
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var flFragment: FrameLayout
    private lateinit var loginFragment: LoginFragment
    private lateinit var otpFragment: OtpFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initializeComponents()


    }

    private fun initializeComponents() {
        flFragment = findViewById(R.id.flFragment)
        loginFragment = LoginFragment()
        otpFragment = OtpFragment()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, loginFragment)
            commit()
        }

        val text: TextView
        text = findViewById<TextView?>(R.id.appTV)
        text.setOnClickListener{
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, OtpFragment())
                commit()
            }
        }

    }


}


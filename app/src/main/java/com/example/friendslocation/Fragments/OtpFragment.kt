package com.example.friendslocation.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.friendslocation.R
import com.google.android.material.textfield.TextInputEditText

class OtpFragment : Fragment(R.layout.fragment_otp) {

    private lateinit var textOtp: TextInputEditText
    private lateinit var submitOTPBtn: Button
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeComponenetes(view);
        setClickListners()
    }


    private fun initializeComponenetes(view: View) {
        textOtp = view.findViewById(R.id.Otp_ET)
        submitOTPBtn = view.findViewById(R.id.Otp_buttonn)
    }

    private fun setClickListners() {
        submitOTPBtn.setOnClickListener {
            Toast.makeText(activity, "Inside OTP Fragment", Toast.LENGTH_SHORT).show()
        }
    }

}
package com.example.friendslocation.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import com.example.friendslocation.HelperClasses.PhoneAuthHelperImpl
import com.example.friendslocation.Interfaces.AuthenticationManager

import com.example.friendslocation.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential


class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var textInputEditText: TextInputEditText
    private lateinit var textInputLayout: TextInputLayout
    private lateinit var loginBtn: Button
    private lateinit var number: String
    private val authenticationManager: AuthenticationManager = PhoneAuthHelperImpl()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiatlizeComponents(view)
        setListners(view)


    }


    private fun initiatlizeComponents(view: View) {
        textInputLayout = view.findViewById(R.id.phone_number_input_layout)
        textInputEditText = view.findViewById(R.id.phone_number)
        loginBtn = view.findViewById(R.id.login_button)


    }

    private fun setListners(view: View) {
        loginBtn.setOnClickListener {
            startVerification();
        }

    }

    private fun startVerification() {
        number = textInputEditText.text!!.trim().toString()
        if (number.length == 10) {
            textInputLayout.isErrorEnabled = false
            hideKeyboard()



        } else {
            textInputLayout.isErrorEnabled = true
            textInputLayout.error = "Please Enter a Valid Number"
        }
    }


    fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
        auth: FirebaseAuth
    ) {
        auth.signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                activity?.let {
                    Snackbar.make(
                        it.findViewById(android.R.id.content),
                        "Login Successful", Snackbar.LENGTH_LONG
                    ).show()
                    Toast.makeText(
                        activity, "Login Success", Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    // The verification code entered was invalid
                    Toast.makeText(
                        activity, "Invalid Verification Code", Toast.LENGTH_SHORT
                    ).show()
                }
                // Update UI
            }
        }


    }


    fun hideKeyboard() {
        val inputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = view ?: return
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
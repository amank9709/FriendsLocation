package com.example.friendslocation.HelperClasses

import android.app.Activity
import com.example.friendslocation.Interfaces.AuthenticationCallback
import com.example.friendslocation.Interfaces.AuthenticationManager
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

private val auth: FirebaseAuth = FirebaseAuth.getInstance()

private var storedVerificationId: String? = null

class PhoneAuthHelperImpl() : AuthenticationManager  {
    override fun signInWithPhoneNumber(phoneNumber: String, callback: AuthenticationCallback) {
         val options = PhoneAuthOptions.newBuilder(auth)
             .setPhoneNumber(phoneNumber)
             .setTimeout(60L, TimeUnit.SECONDS)
             .setCallbacks(phoneAuthCallbacks)
             .build()
         PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun verifyOtp(otp: String, callback: AuthenticationCallback) {
         val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, otp)
         auth.signInWithCredential(credential)
             .addOnCompleteListener { task ->
                 if (task.isSuccessful) {
                     callback.onAuthenticationSuccess()
                 } else {
                     callback.onAuthenticationFailure()
                 }
             }
    }


    override fun signOut() {
        auth.signOut()
    }


    private val phoneAuthCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // Automatically handle verification when SMS is received
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback.onAuthenticationSuccess()
                    } else {
                        callback.onAuthenticationFailure()
                    }
                }
        }

        override fun onVerificationFailed(exception: FirebaseException) {
            // Handle verification failure
            callback.onAuthenticationFailure()
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            // Store the verification ID for later use
            var storedVerificationId = verificationId
        }
    }


}

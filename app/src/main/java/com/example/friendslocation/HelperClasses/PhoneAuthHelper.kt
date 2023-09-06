package com.example.friendslocation.HelperClasses

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneAuthHelper() {

    private lateinit var firebaseAuth: FirebaseAuth

    constructor(firebaseAuth: FirebaseAuth) : this() {
        this.firebaseAuth = firebaseAuth
    }

    open fun startPhoneNumberVerification(phoneNumber: String, activity: Activity) {
        // Start the phone number verification process.
        val phoneAuthProvider = PhoneAuthProvider.getInstance()
        val phoneAuthOptions = PhoneAuthOptions.Builder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60, TimeUnit.SECONDS)
            .setActivity(activity)
            .build()
        //phoneAuthProvider.verifyPhoneNumber(phoneAuthOptions)
    }

    fun onVerificationCompleted(credential: PhoneAuthCredential) {
        // Sign in the user with the phone credential.
        firebaseAuth.signInWithCredential(credential)
    }

    fun onVerificationFailed(exception: FirebaseException) {
        // Handle the verification failure.
    }
}

private fun PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions: PhoneAuthOptions) {

}

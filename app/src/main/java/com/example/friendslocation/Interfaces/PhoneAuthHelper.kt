package com.example.friendslocation.Interfaces

import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential

interface AuthenticationCallback {
    fun onAuthenticationSuccess()
    fun onAuthenticationFailure()
}

interface AuthenticationManager {
    fun signInWithPhoneNumber(phoneNumber: String, callback: AuthenticationCallback)
    fun verifyOtp(otp: String, callback: AuthenticationCallback)
    fun signOut()
}
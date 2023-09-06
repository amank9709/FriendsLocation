package com.example.friendslocation.HelperClasses

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.friendslocation.Fragments.LoginFragment
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


private lateinit var auth: FirebaseAuth
private var storedVerificationId: String? = ""
private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
@SuppressLint("StaticFieldLeak")
private lateinit var loginFragment: LoginFragment;

class FirebaseAuthHelper(private val activity: Activity) : ActivityCompat() {

    fun sendOtpForVerification(phoneNumber: String) {
        loginFragment = LoginFragment();
        auth = FirebaseAuth.getInstance()
        var output = 0
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Toast.makeText(activity, "Verification Successful", Toast.LENGTH_SHORT).show()
                loginFragment.signInWithPhoneAuthCredential(credential, auth)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(activity, "Invalid Request $e", Toast.LENGTH_SHORT).show()

                } else if (e is FirebaseTooManyRequestsException) {
                    Toast.makeText(
                        activity,
                        "The SMS quota for the project has been exceeded",
                        Toast.LENGTH_SHORT
                    ).show()

                } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                    Toast.makeText(
                        activity,
                        "reCAPTCHA verification attempted with null Activity",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Toast.makeText(
                    activity, "Some Issue Occurred $e", Toast.LENGTH_LONG
                ).show()

            }

            override fun onCodeSent(
                verificationId: String, token: PhoneAuthProvider.ForceResendingToken
            ) {

                Toast.makeText(
                    activity,
                    "OTP Sent Successfully : $verificationId AND  TOKEN : $token",
                    Toast.LENGTH_LONG
                ).show()
                storedVerificationId = verificationId
                resendToken = token
                loginFragment.afterCodeSent(activity)
            }

        }
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phoneNumber") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)


    }


    // [START sign_in_with_phone]

}
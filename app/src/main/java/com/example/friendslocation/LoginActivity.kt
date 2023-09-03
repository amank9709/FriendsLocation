package com.example.friendslocation

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.ktx.appCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import java.util.concurrent.TimeUnit

class LoginActivity : ComponentActivity() {

    private lateinit var numberEditText: TextInputEditText;
    private lateinit var OtpEditText: TextInputEditText;
    private lateinit var otpInputLayout: TextInputLayout;
    private lateinit var loginButton: Button
    private lateinit var OtpButton: Button
    private lateinit var auth: FirebaseAuth
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        InitializeComponents();
        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )
        var user = Firebase.auth.currentUser

        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    private fun InitializeComponents() {
        numberEditText = findViewById(R.id.phone_number)
        OtpEditText = findViewById(R.id.Otp_number)
        loginButton = findViewById(R.id.login_button)
        OtpButton = findViewById(R.id.Otp_buttonn)
        otpInputLayout = findViewById(R.id.Otp_input_layout)


        auth = Firebase.auth

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
                Toast.makeText(this@LoginActivity, "Verification Successful", Toast.LENGTH_SHORT)
                    .show()
                loginButton.visibility = View.GONE
                otpInputLayout.visibility = View.VISIBLE
                OtpButton.visibility = View.VISIBLE


                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(this@LoginActivity, "Invalid Request $e", Toast.LENGTH_SHORT)
                        .show()

                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(
                        this@LoginActivity,
                        "The SMS quota for the project has been exceeded",
                        Toast.LENGTH_SHORT
                    ).show()

                } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                    // reCAPTCHA verification attempted with null Activity
                    Toast.makeText(
                        this@LoginActivity,
                        "reCAPTCHA verification attempted with null Activity",
                        Toast.LENGTH_SHORT
                    ).show()


                }
                Toast.makeText(
                    this@LoginActivity, "Some Issue OCCURED $e", Toast.LENGTH_LONG
                ).show()
                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                loginButton.visibility = View.GONE
                otpInputLayout.visibility = View.VISIBLE
                OtpButton.visibility = View.VISIBLE
                Log.d(TAG, "onCodeSent:$verificationId")
                Toast.makeText(
                    this@LoginActivity,
                    "OTP Sent Successfully : $verificationId AND  TOKEN : $token",
                    Toast.LENGTH_LONG
                ).show()


                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token

                // below line is used for getting
                // credentials from our verification id and code.
                // below line is used for getting
                // credentials from our verification id and code.

                // after getting credential we are
                // calling sign in method.

                // after getting credential we are
                // calling sign in method.
            }

        }


        loginButton.setOnClickListener {
            // Your code here
            // This block will be executed when the button is clicked
            SentOTPforVerification(numberEditText.text?.trim())
            //Toast.makeText(this, "Testing", Toast.LENGTH_SHORT).show()
        }

        OtpButton.setOnClickListener {
            // Your code here
            // This block will be executed when the button is clicked
            VerifyCode(OtpEditText.text.toString())
            //Toast.makeText(this, "Testing", Toast.LENGTH_SHORT).show()
        }

    }

    private fun VerifyCode(code: String) {
        // credentials from our verification id and code.
        // credentials from our verification id and code.
        val credential = storedVerificationId?.let { PhoneAuthProvider.getCredential(it, code) }

        // after getting credential we are
        // calling sign in method.

        // after getting credential we are
        // calling sign in method.
        if (credential != null) {
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun SentOTPforVerification(phoneNumber: CharSequence?) {
        Toast.makeText(this, "Number : $phoneNumber", Toast.LENGTH_SHORT).show()
        var number = phoneNumber
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phoneNumber") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)


    }

    // [START sign_in_with_phone]
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    Toast.makeText(
                        this@LoginActivity, "Login Success", Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(
                            this@LoginActivity, "Invalid Verification Code", Toast.LENGTH_SHORT
                        ).show()
                    }
                    // Update UI
                }
            }
    }
// [END sign_in_with_phone]


}


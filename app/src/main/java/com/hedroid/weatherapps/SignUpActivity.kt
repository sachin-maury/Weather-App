@file:Suppress("DEPRECATION")

package com.hedroid.weatherapps

import android.content.Intent
import android.os.Bundle
import android.widget.Button

import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.hedroid.weatherapps.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)

        setContentView(binding.root)
        //Sweet alert dialog box
        findViewById<Button>(R.id.sign_up_button)

        //For Email id Password login
        auth = FirebaseAuth.getInstance()

        binding.signInButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.registerButton.setOnClickListener {
            //get text form edit text field
            val email = binding.email.text.toString()
            val userName = binding.userName.text.toString()
            val password = binding.Password.text.toString()
            val repeatPassword = binding.repeatPassword.text.toString()


            // Check if any field is blank
            if (email.isEmpty() || userName.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Please fill all the Details!")
                    .show()
            } else if (password != repeatPassword) {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Repeat password must be Same!")
                    .show()

            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Registration Successful!")
                                .show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        } else {
                            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Registration Failed! : ${task.exception?.message}")
                                .show()
                        }
                    }

            }
        }


        //with google login

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth
        binding.google.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }


    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if (task.isSuccessful) {
                    val account: GoogleSignInAccount? = task.result
                    val credencial = GoogleAuthProvider.getCredential(account?.idToken, null)
                    auth.signInWithCredential(credencial).addOnCompleteListener {
                        if (it.isSuccessful) {
                            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Successful!")
                                .show()
                            //     Toast.makeText(this,"successful!", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Failed")
                                .show()
                            // Toast.makeText(this,"Failed", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Failed")
                    .show()
                //  Toast.makeText(this,"Failed", Toast.LENGTH_LONG).show()

            }
        }
}
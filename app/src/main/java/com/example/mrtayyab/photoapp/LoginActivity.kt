package com.example.mrtayyab.photoapp

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast


class LoginActivity : AppCompatActivity() {

    lateinit var mLoginBtn : Button
    lateinit var mCreateUser : TextView
    lateinit var mForgetPass : TextView
    lateinit var mLoginEmail : EditText
    lateinit var mLoginPass : EditText
    lateinit var mProgressbar : ProgressDialog

    lateinit var mAuth  : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        mCreateUser = findViewById(R.id.loginCreateBtn)
        mLoginBtn = findViewById(R.id.postBtn)
        mLoginEmail = findViewById(R.id.loginEmail)
        mLoginPass = findViewById(R.id.loginPassword)
        mForgetPass = findViewById(R.id.loginForgetPass)
        mProgressbar = ProgressDialog(this)

        mAuth = FirebaseAuth.getInstance()

        mForgetPass.setOnClickListener {
            val forGetIntent = Intent(applicationContext , ForgetPassActivity::class.java)
            startActivity(forGetIntent)
            finish()

        }

        mCreateUser.setOnClickListener {

            val regiterIntent = Intent(applicationContext , RegisterActivity::class.java)
            startActivity(regiterIntent)
            finish()
        }


        mLoginBtn.setOnClickListener {

            val email = mLoginEmail.text.toString().trim()
            val password = mLoginPass.text.toString().trim()

            if(TextUtils.isEmpty(email)){
                mLoginEmail.error = " Enter Email"
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(password)){
                mLoginEmail.error = " Enter Password"
                return@setOnClickListener
            }

            loginUser(email , password)

        }


    }

    private fun loginUser(email: String, password: String) {
        mProgressbar.setMessage("Please wait..")
        mProgressbar.show()

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        mProgressbar.dismiss()
                        val startIntent  = Intent(applicationContext , MainActivity::class.java)
                        startActivity(startIntent)
                        finish()
                    } else {

                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()

                    }

                  mProgressbar.dismiss()
                }
    }
}

package com.example.mrtayyab.photoapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class ForgetPassActivity : AppCompatActivity() {

    lateinit var mEmail : EditText
    lateinit var mForgetBtn : Button

    lateinit var mAuth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_pass)


        mEmail = findViewById(R.id.fotgetEmail)
        mForgetBtn = findViewById(R.id.postBtn)

        mAuth = FirebaseAuth.getInstance()


        mForgetBtn.setOnClickListener {

            val email = mEmail.text.toString().trim()

            if(email.isEmpty()){
                mEmail.error = " Enter Email"
                return@setOnClickListener
            }

            forGetPassword(email)

        }



    }

    private fun forGetPassword(email: String) {

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener { task: Task<Void> ->

            if(task.isComplete){

                val loginIntent = Intent(this , LoginActivity::class.java)
                startActivity(loginIntent)
                finish()

                Toast.makeText(applicationContext , "Please check your inbox Reset Password and Login" , Toast.LENGTH_LONG).show()

            }
        }
    }

}
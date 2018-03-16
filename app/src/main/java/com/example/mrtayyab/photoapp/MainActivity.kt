package com.example.mrtayyab.photoapp

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

   lateinit var mAuth : FirebaseAuth
    lateinit var mToolbar : Toolbar
    lateinit var mFloatBtn : FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       mAuth = FirebaseAuth.getInstance()

        mToolbar = findViewById(R.id.mainToolbar)
        mFloatBtn = findViewById(R.id.floatingActionButton)

      setSupportActionBar(mToolbar)
        supportActionBar!!.setTitle("Photo App")
        mToolbar.setTitleTextColor(Color.WHITE)


        mFloatBtn.setOnClickListener {
            Toast.makeText(applicationContext, "Hello world " , Toast.LENGTH_LONG).show()
        }


    }


    override fun onStart() {
        super.onStart()

        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser == null){

            val intent = Intent(applicationContext , LoginActivity::class.java)
            startActivity(intent)
            finish()

        }else{


        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflatet = menuInflater
        inflatet.inflate(R.menu.main_menu , menu)
        return true


    }
//

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item?.itemId == R.id.menu_Logout){
            FirebaseAuth.getInstance().signOut()
            val startIntent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(startIntent)
            finish()
        }
        if(item?.itemId == R.id.menu_Profile){
           val userProfile = Intent(applicationContext , UserProfileActivity::class.java)
            startActivity(userProfile)
//            finish()

        }

        if(item?.itemId == R.id.menu_friend){

            val userFriend  = Intent(applicationContext ,FriendsListActivity::class.java)
            startActivity(userFriend)

        }

     return true
    }


}

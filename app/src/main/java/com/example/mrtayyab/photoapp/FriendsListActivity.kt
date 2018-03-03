package com.example.mrtayyab.photoapp

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.friend_list_layout.view.*

class FriendsListActivity : AppCompatActivity() {

    lateinit var mFriendName : EditText
    lateinit var mFriendList : RecyclerView
    lateinit var mUserToolBar : Toolbar
    lateinit var mFirebaseDatabase : DatabaseReference

    lateinit var FirebaseRecyclerAdapter : FirebaseRecyclerAdapter<FriendsListModel , FriendsViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_list)

        mFriendName = findViewById(R.id.friendNameText)

        mFriendList = findViewById(R.id.friendListView)

        mUserToolBar = findViewById(R.id.userToolbar)
        setSupportActionBar(mUserToolBar)
        supportActionBar!!.setTitle("Seach User")

        mUserToolBar.setTitleTextColor(Color.WHITE)

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference("Users")


        mFriendList.setHasFixedSize(true)
        mFriendList.setLayoutManager(LinearLayoutManager(this))


        mFriendName.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val username = mFriendName.text.toString().trim()

                loadFriends(username)
            }

        })

    }

    private fun loadFriends(name : String) {


        if(name.isEmpty()){

            FirebaseRecyclerAdapter.cleanup()
            mFriendList.adapter = FirebaseRecyclerAdapter

        }else {

            val firebaseSearchQuery = mFirebaseDatabase.orderByChild("name").startAt(name).endAt(name + "\uf8ff")


            FirebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<FriendsListModel, FriendsViewHolder>(

                    FriendsListModel::class.java,
                    R.layout.friend_list_layout,
                    FriendsViewHolder::class.java,
                    firebaseSearchQuery
            ) {
                override fun populateViewHolder(viewHolder: FriendsViewHolder, model: FriendsListModel?, position: Int) {

                    viewHolder.mView.userName.setText(model?.name)
                    viewHolder.mView.userStatus.setText(model?.status)

                    Picasso.with(applicationContext).load(model?.image).into(viewHolder.mView.userImageView)

                }

            }

        }

        mFriendList.adapter = FirebaseRecyclerAdapter

    }



    // Friends View Holder
    class FriendsViewHolder(var mView : View) : RecyclerView.ViewHolder(mView) {

    }

}

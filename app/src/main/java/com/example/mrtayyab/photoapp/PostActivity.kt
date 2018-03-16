package com.example.mrtayyab.photoapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.googlecode.mp4parser.authoring.Edit
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import id.zelory.compressor.Compressor
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.ByteArrayOutputStream
import java.io.File

class PostActivity : AppCompatActivity() , EasyPermissions.PermissionCallbacks {

    lateinit var mToolbar : Toolbar
    lateinit var mPostImage : ImageView
    lateinit var mPostText : EditText
    lateinit var mPostButton : Button

    // Firebase
    lateinit var mDatabase : DatabaseReference
    lateinit var mAuth : FirebaseAuth
    lateinit var mProgressbar : ProgressDialog
    // Firebase  Gallery Pick
    private val GALLERY_PICK = 1
    /// Firebase Image Storage
    lateinit var mImageStorage : StorageReference
    // For User Permission on Run Time
    private val LOCATION_AND_CONTACTS = arrayOf<String>(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE ,android.Manifest.permission.CAMERA)
    private val RC_CAMERA_PERM = 123
    private val RC_LOCATION_CONTACTS_PERM = 124


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        // Tool bar
        mToolbar = findViewById(R.id.postToolbar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.setTitle("Post")
        mToolbar.setTitleTextColor(Color.WHITE)
        // Post item
        mPostImage = findViewById(R.id.postImageView)
        mPostText = findViewById(R.id.postText)
        mPostButton = findViewById(R.id.postBtn)
        // ProgressBar
        mProgressbar = ProgressDialog(this)

        // Firebase
        mAuth = FirebaseAuth.getInstance()
        //Firebase current user ID
        val uid  = mAuth.currentUser?.uid

        mDatabase = FirebaseDatabase.getInstance().getReference("Posts").child(uid)
        mImageStorage = FirebaseStorage.getInstance().getReference()



        // here we recivce data from Firebase  database

        mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(data: DataSnapshot?) {

                val name = data?.child("post")?.value!!.toString().trim()
//                val status = data?.child("status")?.value!!.toString().trim()
                val image = data.child("image")?.value!!.toString().trim()

                mPostText.setText(name)

                Picasso.with(applicationContext).load(image).placeholder(R.drawable.image).into(mPostImage)

                if(!image.equals("default")){
                    Picasso.with(applicationContext).load(image).placeholder(R.drawable.image).into(mPostImage)

                }
            }

        } )


        mPostImage.setOnClickListener {
            Toast.makeText(applicationContext, "Select Image" , Toast.LENGTH_LONG).show()
            if (hasLocationAndContactsPermissions()) {
                // Have permissions, do the thing!
//                Toast.makeText(this, "TODO: Location and Contacts things", Toast.LENGTH_LONG).show();
                val galleryIntent = Intent()
                galleryIntent.type = "image/*"
                galleryIntent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK)
            } else {
                // Ask for both permissions
                EasyPermissions.requestPermissions(
                        this,
                        "This app needs access to your location and contacts to know where and who you are.",
                        RC_LOCATION_CONTACTS_PERM,
                        *LOCATION_AND_CONTACTS);
            }

        }


        /// Post Button
        mPostButton.setOnClickListener {
            Toast.makeText(applicationContext, "Hello Button" , Toast.LENGTH_LONG).show()

            /// Here get Text for editText
            val post = mPostText.text.toString().trim()
            if(TextUtils.isEmpty(post)){
                mPostText.error = " Enter Something ..."
                return@setOnClickListener
            }
            // Post data function for upload data
            postData(post)
        }


    }
    // Post data function for upload data
    private fun postData(post: String) {
        mProgressbar.setMessage("Posting  Wait..")
        mProgressbar.show()
        val postMap = HashMap<String ,Any>()
        postMap["post"] = post
        postMap["status"] = "Write Something about post "
        mDatabase.updateChildren(postMap).addOnCompleteListener { task ->
            if(task.isSuccessful){
                // Back to mainActivity
                val intent = Intent(applicationContext , MainActivity::class.java)
                startActivity(intent)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                finish()
            }
        }
    }


    // On Result Activity

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_PICK && resultCode == Activity.RESULT_OK) {

            val imageUri = data.data

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
//                    .setMinCropWindowSize(500, 500)
                    .setMinCropWindowSize(700, 500)

                    .start(this)

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            val result = CropImage.getActivityResult(data)

            if (resultCode == Activity.RESULT_OK) {

                mProgressbar = ProgressDialog(this)
                mProgressbar.setTitle("Uploading Image...")
                mProgressbar.setMessage("Please Wait....")
                mProgressbar.setCanceledOnTouchOutside(false)
                mProgressbar.show()


                val resultUri = result.uri
                //compressor bit map
                val thumb_filePath = File(resultUri.path)

//               / val current_user_id = mCurrentUser.getUid()

                //Compressor
                val thumb_bitmap = Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_filePath)
//
                val baos = ByteArrayOutputStream()
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val thumb_byte = baos.toByteArray()


                mAuth = FirebaseAuth.getInstance()
                val uid = mAuth.currentUser?.uid

                val filepath = mImageStorage.child("Post_images").child(uid + ".jpg")
                val thumb_filepath = mImageStorage.child("Post_images").child("Thumbs").child(uid + ".jpg")

                //
                //
                // StorageTask<UploadTask.TaskSnapshot> image =
                filepath.putFile(resultUri).addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val download_url = task.result.downloadUrl!!.toString()

                        val uploadTask = thumb_filepath.putBytes(thumb_byte)

                        uploadTask.addOnCompleteListener { thumb_task ->
                            val thumb_downloadUrl = thumb_task.result.downloadUrl!!.toString()
                            if (thumb_task.isSuccessful) {

                                val update_hashMap = HashMap<String , Any>()

//                                update_hashMap.put("image", download_url)
//                                update_hashMap.put("thumb_image", thumb_downloadUrl)
                                update_hashMap["image"] = download_url
                                update_hashMap["thumb_image"] = thumb_downloadUrl

                                mDatabase.updateChildren(update_hashMap).addOnCompleteListener(OnCompleteListener<Void> { task ->
                                    if (task.isSuccessful) {
                                        mProgressbar.dismiss()
                                        Toast.makeText(this, "Profile Picture is Uploaded Successfully ", Toast.LENGTH_SHORT).show()

                                    }
                                })

                            } else {

                                mProgressbar.dismiss()
                                Toast.makeText(this, "Error is Uploading ", Toast.LENGTH_SHORT).show()

                            }
                        }


                    } else {
                        mProgressbar.dismiss()
                        Toast.makeText(this, "Error is Uploading ", Toast.LENGTH_SHORT).show()
                    }
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                val error = result.error
            }
        }

    }


    /////----------------------------- Permission Functions  -----------------
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()

        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {


    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun hasLocationAndContactsPermissions(): Boolean {
        return EasyPermissions.hasPermissions(this, *LOCATION_AND_CONTACTS )
    }

    //////////--------------------- Permission Functions -------------- //////////

}

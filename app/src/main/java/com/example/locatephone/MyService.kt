package com.example.locatephone

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class MyService:Service(){
    var databaseRef: DatabaseReference?=null
    override fun onBind(intent: Intent?): IBinder? {
        return null!!
    }

    override fun onCreate() {
        super.onCreate()
        databaseRef = FirebaseDatabase.getInstance().reference
        isServiceRunning = true
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var myLocationListener = MyLocationListener()
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f,myLocationListener)

        //Listen to request
        var userData = UserData(this)
        val phoneNumber = userData.loadPhoneNumber()
        databaseRef!!.child("Users").child(phoneNumber).child("request").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val df = SimpleDateFormat("yyyy/MMM/dd HH:MM:ss")
                val date = Date()
                if(myLocation == null){
                    return
                }
                databaseRef!!.child("Users").child(phoneNumber).child("location").child("latitude").setValue(myLocation!!.latitude)
                databaseRef!!.child("Users").child(phoneNumber).child("location").child("longitude").setValue(myLocation!!.longitude)
                databaseRef!!.child("Users").child(phoneNumber).child("location").child("lastOnline").setValue(df.format(date).toString())


            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        return Service.START_NOT_STICKY
    }
    companion object{
        var isServiceRunning = false
    }

    var myLocation: Location?=null
    inner class MyLocationListener: LocationListener {
        constructor():super(){
            myLocation = Location("me")
            myLocation!!.longitude = 0.0
            myLocation!!.latitude = 0.0

        }
        override fun onLocationChanged(location: Location) {
            myLocation = location

        }

    }

}
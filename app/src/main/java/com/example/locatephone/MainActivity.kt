package com.example.locatephone

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.*
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login_page.*
import kotlinx.android.synthetic.main.activity_my_tracker.*
import kotlinx.android.synthetic.main.contact_ticket.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    var databaseRef:DatabaseReference?=null
    var adapter: ContactAdapter?=null
    var listOfContact=ArrayList<UserContact>()
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userData = UserData(this)
        userData.isFirstTimeLoad()
        databaseRef = FirebaseDatabase.getInstance().reference
        adapter = ContactAdapter(this,listOfContact)
        lvContacts.adapter = adapter
        lvContacts.onItemClickListener= AdapterView.OnItemClickListener{
                parent,view,position,id ->
            val userInfo =listOfContact[position]
            // get datatime
            val df = SimpleDateFormat("yyyy/MMM/dd HH:MM:ss")
            val date = Date()
            // save to database
            databaseRef!!.child("Users").child(userInfo.pNumber!!).child("request").setValue(df.format(date).toString())

            val intent = Intent(applicationContext,MapsActivity::class.java)
            intent.putExtra("phoneNumber",userInfo.pNumber)

            startActivity(intent)

        }
    }
    override fun onResume() {
        super.onResume()

        val userData= UserData(this)
        if (userData.loadPhoneNumber()=="empty"){
            return
        }
        refreshUsers()

        if(MyService.isServiceRunning) return

        checkContactPermissions()
        checkLocationPermissions()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu,menu,)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuAdd->{
                val intent = Intent(this,MyTracker::class.java)
                startActivity(intent)
            }
            R.id.menuHelp ->{
            }
            else ->{
                return super.onOptionsItemSelected(item)
            }

        }
        return true
    }

    fun checkContactPermissions(){
        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_CONTACTS)!=
                    PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS),CONTACT_CODE)
                return
            }
        }
        loadContact()

    }
    val CONTACT_CODE = 123

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            CONTACT_CODE -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    loadContact()
                }
                else{
                    Toast.makeText(this,"Contacts permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            LOCATION_CODE -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getUserLocation()
                }
                else{
                    Toast.makeText(this,"Contacts permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

    }
    var listOfContacts=HashMap<String,String>()
    fun loadContact(){
        try{
            listOfContacts.clear()

            val cursor=contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null)
            cursor!!.moveToFirst()
            do {
                val name=cursor!!.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phoneNumber=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                listOfContacts.put(UserData.formatPhoneNumber(phoneNumber),name)
            }while (cursor!!.moveToNext())
        }catch (ex:Exception){}

    }
    fun refreshUsers(){
        val userData= UserData(this)
        databaseRef!!.child("Users").
        child(userData.loadPhoneNumber()).
        child("Finders").addValueEventListener(object :
            ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    val td = dataSnapshot!!.value as HashMap<String,Any>

                    listOfContact.clear()

                    if (td==null){
                        listOfContact.add(UserContact("NO_USERS","nothing"))
                        adapter!!.notifyDataSetChanged()
                        return
                    }

                    for (key in td.keys){
                        val name = listOfContacts[key]
                        listOfContact.add(UserContact(name.toString() ,key))

                    }

                    adapter!!.notifyDataSetChanged()
                }catch (ex:Exception){
                    listOfContact.clear()
                    listOfContact.add(UserContact("NO_USERS","nothing"))
                    adapter!!.notifyDataSetChanged()
                    return
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
    fun dummpyData(){
        listOfContact.add(UserContact("hussein","3434"))
        listOfContact.add(UserContact("jena","344343"))
        listOfContact.add(UserContact("laya","434543"))
    }

    class ContactAdapter: BaseAdapter {
        var listOfContact=ArrayList<UserContact>()
        var context: Context?=null
        constructor(context: Context, listOfContact:ArrayList<UserContact>){
            this.context=context
            this.listOfContact=listOfContact
        }
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val userContact = listOfContact[p0]

            if (userContact.name.equals("NO_USERS")){
                val myView = LayoutInflater.from(context).inflate(R.layout.no_user, null)
                return myView
            }else {
                val myView = LayoutInflater.from(context).inflate(R.layout.contact_ticket,null)
                myView.tvName.text = userContact.name
                myView.tvNumber.text = userContact.pNumber

                return myView
            }
        }

        override fun getItem(p0: Int): Any {

            return listOfContact[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {

            return listOfContact.size
        }

    }
    fun checkLocationPermissions(){
        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=
                    PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),LOCATION_CODE)
                return
            }
        }
        loadContact()

    }
    val LOCATION_CODE = 234

    @SuppressLint("MissingPermission")
    fun getUserLocation(){
       if(!MyService.isServiceRunning){
           val intent = Intent(baseContext,MyService::class.java)
           startService(intent)
       }


    }

    var myLocation:Location?=null
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
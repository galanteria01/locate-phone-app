package com.example.locatephone

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.*
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_my_tracker.*
import kotlinx.android.synthetic.main.contact_ticket.view.*

class MyTracker : AppCompatActivity() {
    var adapter:contactAdapter?=null
    var listOfContact = ArrayList<UserContact>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_tracker)
        dummyData()
        adapter = contactAdapter(this,listOfContact)
        lvContacts.adapter = adapter
    }

    fun dummyData(){
        listOfContact.add(UserContact("Shanu","100"))
        listOfContact.add(UserContact("Moosewala","101"))
        listOfContact.add(UserContact("Eminem","102"))
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.tracker_menu,menu,)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.addContact ->{
                checkPermissions()
            }
            R.id.finish ->{
                finish()
            }
            else ->{
                return super.onOptionsItemSelected(item)
            }

        }
        return true
    }
    val CONTACT_CODE = 123
    fun checkPermissions(){
        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_CONTACTS)!=
                    PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS),CONTACT_CODE)
            }
        }else{
            pickContact()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            CONTACT_CODE -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickContact()
                }else{
                    Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

    }
    val PICK_CODE = 111
    fun pickContact(){
        var intent = Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent,PICK_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            PICK_CODE ->{
                if(resultCode == Activity.RESULT_OK){
                    var contact = data!!.data
                    var c = contentResolver.query(contact!!,null,null,null,null)

                    if(c!!.moveToFirst()){
                        var id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                        var hasPhone = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                        if(hasPhone.equals("1")){
                            val phone = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+id,null,null)

                            phone!!.moveToFirst()
                            var phoneNumber = phone!!.getString(c.getColumnIndex("data1"))
                            val name = phone!!.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                            listOfContact.add(UserContact(name.toString(),phoneNumber.toString()))
                        }

                    }
                }

            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }

    }

    class contactAdapter: BaseAdapter {
        var context:Context?=null
        var listOfContacts = ArrayList<UserContact>()
        constructor(context: Context,listOfContacts: ArrayList<UserContact>){
            this.context = context
            this.listOfContacts = listOfContacts

        }
        override fun getCount(): Int {
            return this.listOfContacts.size
        }

        override fun getItem(position: Int): Any {
            return this.listOfContacts[position]
        }


        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var user = this.listOfContacts[position]
            var inflater = this.context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var myView = inflater.inflate(R.layout.contact_ticket,null)
            myView.tvName.text = user.name
            myView.tvNumber.text = user.pNumber
            return myView
        }

    }
}
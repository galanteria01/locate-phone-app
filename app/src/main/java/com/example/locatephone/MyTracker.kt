package com.example.locatephone

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.view.View.inflate
import android.widget.BaseAdapter
import androidx.core.content.res.ColorStateListInflaterCompat.inflate
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
                //TODO:Add New Activity
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
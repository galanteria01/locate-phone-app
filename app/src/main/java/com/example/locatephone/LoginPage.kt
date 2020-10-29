package com.example.locatephone

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login_page.*

class LoginPage : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)
        mAuth = FirebaseAuth.getInstance()
    }

    fun buRegister(view: View) {
        var userData = UserData(this)
        userData.saveNumber(etNumber.text.toString())
        finish()
    }
}
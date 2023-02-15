package com.example.chatoto

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        enter_button_login.setOnClickListener {       //код відтворюється в логах після натискання кнопки
            val email = email_login.text.toString()
            val password = password_login.text.toString()

            Log.d("Login", "Attempt login with emeil/pw: $email/***")       //Запис пошти
//            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener()
        }
        back_to_register_textView.setOnClickListener {
            finish()
        }

        }
    }
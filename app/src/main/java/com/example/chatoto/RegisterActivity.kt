package com.example.chatoto

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_button_register.setOnClickListener {       //код відтворюється після натискання кнопки
            performRegister()

        }
        already_have_account_textView.setOnClickListener {
            Log.d("RegisterActivity", "Перехід на головне вікно")

            val intent = Intent(this, LoginActivity::class.java)    //змінна, що приймає класс LoginAvtivity
            startActivity(intent)   //запускає activity по зміннії в якої записан клас
        }
        selectPhoto_register.setOnClickListener {
            Log.d("RegisterActivity", "Оберіть фото")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectPhotoUri: Uri? = null
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && data != null) {
//            requestCode == 0  && requestCode == Activity.RESULT_OK && data != null
            //перевірка наявності фото
            Log.d("RegisterActivity", "Фото обрано")

            selectPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectPhotoUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            selectPhoto_register.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun  performRegister() {
        val email = email_editText_register.text.toString()
        val password = password_editText_register.text.toString()

        //Перевірка на пусте поле та повідомлення
        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Будь-ласка, заповнить email/пароль", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("RegisterActivity", "Email is: $email")       //Запис пошти
        Log.d("RegisterActivity", "Password: $password")        //Запис пароля

        //Firebase для пошти та пароля
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if (!it.isSuccessful) return@addOnCompleteListener
                Log.d("RegisterActivity", "Обліковий запис створен!uid ${it.result.user?.uid}")

                uploadImageToFirebaseStorage()      //Збереження картинки в БД
            }
            .addOnFailureListener{
                Log.d("RegisterActivity", "Помилка створення користувача: ${it.message}") //перевірка правильного введення пошти
                Toast.makeText(this, "Будь-ласка, заповнить email/пароль", Toast.LENGTH_SHORT).show()   //повідомлення щодо помилки
            }
    }

    var FirebaseDatabase = com.google.firebase.database.FirebaseDatabase.getInstance("https://kotlinchato-default-rtdb.europe-west1.firebasedatabase.app/")
    private fun uploadImageToFirebaseStorage(){
        if (selectPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Фото завандажено до БД ${it.metadata?.path}")

                saveUserToFirebaseDatabase(it.toString())
            }
            .addOnFailureListener{
                //додати логін
            }
    }


    private fun saveUserToFirebaseDatabase(profileImageUri: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getReference("/users/$uid")
        val user = User(uid, username_editText_register.text.toString(),profileImageUri)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Ваш обліковий запис збережен в Firebase Database")
                //Після створення облікового запису запускається нове вікно з чатами
                val intent = Intent(this, MessangerActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
    }
}

class User(val uid: String, val username: String, val profileImageUri: String){

}
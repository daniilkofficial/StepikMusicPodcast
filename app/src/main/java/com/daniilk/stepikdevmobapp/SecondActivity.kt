package com.daniilk.stepikdevmobapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        val str = intent.getStringExtra("text")
        val edittext = findViewById<EditText>(R.id.edittext)
        edittext.setText(str)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {

            val i = Intent()
            i.putExtra("text", edittext.text.toString())
            setResult(RESULT_OK, i)
            finish()
        }
    }
}
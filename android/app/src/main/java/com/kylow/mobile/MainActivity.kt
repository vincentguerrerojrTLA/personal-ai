package com.kylow.mobile
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
class MainActivity:AppCompatActivity(){
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContentView(R.layout.activity_main)
  val input=findViewById<EditText>(R.id.messageInput);val response=findViewById<TextView>(R.id.responseText);val status=findViewById<TextView>(R.id.status)
  findViewById<Button>(R.id.sendButton).setOnClickListener{val q=input.text.toString().trim();response.text=if(q.isEmpty())"Say something first." else "I heard you: “$q”\n\nPC connection is not paired yet.";input.text.clear()}
  findViewById<Button>(R.id.pairButton).setOnClickListener{status.text="Pairing setup is ready for the next secure PC-link step.";response.text="Next: securely connect this phone to your Kylow PC node."}
 }}

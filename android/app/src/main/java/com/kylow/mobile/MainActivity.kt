package com.kylow.mobile
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
class MainActivity:AppCompatActivity(){
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContentView(R.layout.activity_main)
  val input=findViewById<EditText>(R.id.messageInput);val response=findViewById<TextView>(R.id.responseText);val status=findViewById<TextView>(R.id.status)
  findViewById<Button>(R.id.sendButton).setOnClickListener{val q=input.text.toString().trim();response.text=if(q.isEmpty())"Type a message and I’ll be right here." else "You: $q\n\nKylow: I’m ready. Connect me to the PC node next so I can answer with the full Kylow system.";input.text.clear()}
  findViewById<Button>(R.id.pairButton).setOnClickListener{status.text="Pairing mode • Waiting for Kylow PC node";response.text="PC pairing selected. Secure connection setup is the next build step."}
  findViewById<Button>(R.id.filesButton).setOnClickListener{response.text="Files selected. Secure file access will activate after permissions and PC pairing are connected."}
  findViewById<Button>(R.id.webButton).setOnClickListener{response.text="Web Search selected. Search transport will activate with the Kylow service connection."}
  findViewById<Button>(R.id.tasksButton).setOnClickListener{response.text="Tasks selected. Task management is ready for the next functional module."}
 }}

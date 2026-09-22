package com.kylow.mobile
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
class MainActivity:AppCompatActivity(){
 override fun onCreate(s:Bundle?){super.onCreate(s);setContentView(R.layout.activity_main)
 val input=findViewById<EditText>(R.id.messageInput);val response=findViewById<TextView>(R.id.responseText);val welcome=findViewById<View>(R.id.welcomePanel);val tools=findViewById<View>(R.id.toolBar);val status=findViewById<TextView>(R.id.status)
 fun show(t:String){welcome.visibility=View.GONE;response.visibility=View.VISIBLE;response.text=t}
 findViewById<Button>(R.id.toolsButton).setOnClickListener{tools.visibility=if(tools.visibility==View.VISIBLE)View.GONE else View.VISIBLE}
 findViewById<Button>(R.id.sendButton).setOnClickListener{val q=input.text.toString().trim();if(q.isNotEmpty()){show("You\n$q\n\nKylow\nI’m ready. Secure PC connection is the next step for full AI responses.");input.text.clear()}}
 findViewById<Button>(R.id.newChatButton).setOnClickListener{response.visibility=View.GONE;welcome.visibility=View.VISIBLE;input.text.clear()}
 findViewById<Button>(R.id.menuButton).setOnClickListener{tools.visibility=View.VISIBLE}
 findViewById<Button>(R.id.pairButton).setOnClickListener{status.text="Pairing mode";show("Secure PC pairing selected.")}
 findViewById<Button>(R.id.filesButton).setOnClickListener{show("Files\nSecure file tools will appear here.")}
 findViewById<Button>(R.id.webButton).setOnClickListener{show("Web Search\nWeb tools will appear here.")}
 findViewById<Button>(R.id.tasksButton).setOnClickListener{show("Tasks\nYour Kylow tasks will appear here.")}
 }}
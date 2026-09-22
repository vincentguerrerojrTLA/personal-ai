package com.kylow.mobile
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity:AppCompatActivity(){
 private val transcript=StringBuilder()
 override fun onCreate(s:Bundle?){super.onCreate(s);setContentView(R.layout.activity_main)
  val input=findViewById<EditText>(R.id.messageInput);val chat=findViewById<TextView>(R.id.chatText);val welcome=findViewById<View>(R.id.welcomePanel);val tools=findViewById<View>(R.id.toolBar);val scroll=findViewById<ScrollView>(R.id.chatScroll)
  fun append(who:String,text:String){welcome.visibility=View.GONE;if(transcript.isNotEmpty())transcript.append("\n\n");transcript.append(who).append("\n").append(text);chat.text=transcript.toString();scroll.post{scroll.fullScroll(View.FOCUS_DOWN)}}
  fun send(){val q=input.text.toString().trim();if(q.isEmpty())return;append("You",q);input.text.clear();append("Kylow",localReply(q))}
  findViewById<Button>(R.id.sendButton).setOnClickListener{send()}
  input.setOnEditorActionListener{_,id,_->if(id==EditorInfo.IME_ACTION_SEND){send();true}else false}
  findViewById<Button>(R.id.toolsButton).setOnClickListener{tools.visibility=if(tools.visibility==View.VISIBLE)View.GONE else View.VISIBLE}
  findViewById<Button>(R.id.menuButton).setOnClickListener{tools.visibility=if(tools.visibility==View.VISIBLE)View.GONE else View.VISIBLE}
  findViewById<Button>(R.id.newChatButton).setOnClickListener{transcript.clear();chat.text="";welcome.visibility=View.VISIBLE;input.text.clear()}
  findViewById<Button>(R.id.filesButton).setOnClickListener{append("Kylow","Files are a local phone capability and do not require a PC connection.")}
  findViewById<Button>(R.id.webButton).setOnClickListener{append("Kylow","Web access is an optional phone capability. Core chat must remain usable without a PC.")}
  findViewById<Button>(R.id.tasksButton).setOnClickListener{append("Kylow","Tasks belong to this Kylow app and are not dependent on a PC connection.")}
 }
 private fun localReply(q:String):String{
  val t=q.lowercase()
  return when{
   t.matches(Regex(".*\\b(hi|hello|hey)\\b.*"))->"Hey! I’m Kylow. I’m running directly in the phone app."
   "who are you" in t->"I’m Kylow — your personal AI. The phone app is a first-class Kylow client, not a remote control for your PC."
   "pc" in t||"computer" in t->"I can optionally work with your PC when you choose, but this app is being built so the PC is not required for normal use."
   else->"I received that locally. The text-first chat interface is working; the next build stage replaces this lightweight local responder with Kylow’s independent on-device AI runtime."
  }
 }
}
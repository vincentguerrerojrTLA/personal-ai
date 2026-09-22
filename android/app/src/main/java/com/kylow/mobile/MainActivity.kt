package com.kylow.mobile
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.UUID

class MainActivity:AppCompatActivity(){
 private lateinit var store:ChatStore; private var current:String?=null; private val transcript=StringBuilder()
 override fun onCreate(s:Bundle?){super.onCreate(s);setContentView(R.layout.activity_main)
  val root=findViewById<View>(R.id.rootLayout);ViewCompat.setOnApplyWindowInsetsListener(root){v,i->val b=i.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime());v.setPadding(b.left,b.top,b.right,b.bottom);i}
  store=ChatStore(this);val input=findViewById<EditText>(R.id.messageInput);val chat=findViewById<TextView>(R.id.chatText);val welcome=findViewById<View>(R.id.welcomePanel);val tools=findViewById<View>(R.id.toolBar);val scroll=findViewById<ScrollView>(R.id.chatScroll)
  fun render(){chat.text=transcript.toString();welcome.visibility=if(transcript.isEmpty())View.VISIBLE else View.GONE;scroll.post{scroll.fullScroll(View.FOCUS_DOWN)}}
  fun persist(){if(transcript.isEmpty())return;val id=current?:UUID.randomUUID().toString().also{current=it};store.save(id,transcript.toString())}
  fun append(who:String,text:String){if(transcript.isNotEmpty())transcript.append("\n\n");transcript.append(who).append("\n").append(text);persist();render()}
  fun send(){val q=input.text.toString().trim();if(q.isEmpty())return;append("You",q);input.text.clear();append("Kylow",localReply(q))}
  findViewById<TextView>(R.id.sendButton).setOnClickListener{send()};input.setOnEditorActionListener{_,id,_->if(id==EditorInfo.IME_ACTION_SEND){send();true}else false}
  findViewById<TextView>(R.id.toolsButton).setOnClickListener{tools.visibility=if(tools.visibility==View.VISIBLE)View.GONE else View.VISIBLE}
  findViewById<TextView>(R.id.menuButton).setOnClickListener{val items=store.list();val labels=mutableListOf("New chat","Files & Library","Plugins","Settings","Check for updates");items.take(20).forEach{labels.add((if(it.pinned)"★ " else "")+it.title)};android.app.AlertDialog.Builder(this).setTitle("Kylow").setItems(labels.toTypedArray()){_,p->when{p==0->{persist();current=null;transcript.clear();render()};p==1->append("Kylow","Library is ready for local files and future connected storage.");p==2->append("Kylow","Plugins are optional extensions. Kylow remains usable without them.");p==3->append("Kylow","Settings will manage privacy, memory, devices and plugins.");p==4->{val updater=AppUpdater(this);updater.check({u->if(u==null)Toast.makeText(this,"Kylow is up to date",Toast.LENGTH_SHORT).show() else android.app.AlertDialog.Builder(this).setTitle("Kylow "+u.versionName+" available").setMessage("Download the approved update now?").setPositiveButton("Update"){_,_->updater.download(u){Toast.makeText(this,"Update downloading",Toast.LENGTH_SHORT).show()}}.setNegativeButton("Later",null).show()},{e->Toast.makeText(this,e,Toast.LENGTH_LONG).show()})};else->{val c=items[p-5];current=c.id;transcript.clear();transcript.append(c.body);render()}}}.show()}
  findViewById<TextView>(R.id.newChatButton).setOnClickListener{persist();current=null;transcript.clear();chat.text="";welcome.visibility=View.VISIBLE;input.text.clear()}
  findViewById<Button>(R.id.filesButton).setOnClickListener{append("Kylow","Local Library is enabled. File importing and plugin-backed storage are the next integration layer.")}
  findViewById<Button>(R.id.webButton).setOnClickListener{append("Kylow","Web is an optional tool; core Kylow remains independent.")}
  findViewById<Button>(R.id.tasksButton).setOnClickListener{append("Kylow","Tasks are part of Kylow and persist independently of a PC.")}
  fun prompt(text:String){input.setText(text);input.setSelection(input.text.length);input.requestFocus()}
  findViewById<Button>(R.id.chatHomeButton).setOnClickListener{input.requestFocus()}
  findViewById<Button>(R.id.tasksHomeButton).setOnClickListener{append("Kylow","Tasks are ready for the next functional integration layer.")}
  findViewById<Button>(R.id.filesHomeButton).setOnClickListener{append("Kylow","Local Library is ready for file integration.")}
  findViewById<Button>(R.id.webHomeButton).setOnClickListener{append("Kylow","Web Search will remain optional; core Kylow stays independent.")}
  findViewById<Button>(R.id.pcHomeButton).setOnClickListener{startActivity(Intent(this,if(RemotePcConfig.paired(this)) RemotePcActivity::class.java else RemotePcPairActivity::class.java))}
  findViewById<Button>(R.id.createHomeButton).setOnClickListener{prompt("Create ")}
  findViewById<Button>(R.id.learnHomeButton).setOnClickListener{prompt("Teach me about ")}
  findViewById<Button>(R.id.moreHomeButton).setOnClickListener{findViewById<TextView>(R.id.menuButton).performClick()}
  findViewById<Button>(R.id.canDoButton).setOnClickListener{prompt("What can you do?")}
  findViewById<Button>(R.id.planButton).setOnClickListener{prompt("Help me plan ")}
  findViewById<Button>(R.id.imageButton).setOnClickListener{prompt("Create an image of ")}
  store.latest()?.let{current=it.id;transcript.append(it.body);render()}
 }
 private fun localReply(q:String)=when{q.lowercase().matches(Regex(".*\\b(hi|hello|hey)\\b.*"))->"Hey! I’m Kylow. I’m running directly on this phone.";else->"I received that locally. Persistent chat storage is active; the independent model runtime is the next major engine layer."}
}
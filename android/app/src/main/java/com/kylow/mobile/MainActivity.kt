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
 private lateinit var store:ChatStore; private var current:String?=null; private val transcript=StringBuilder(); private lateinit var ai:LocalAiRuntime; private lateinit var modelManager:LocalModelManager
 override fun onCreate(s:Bundle?){super.onCreate(s);setContentView(R.layout.activity_main)
  val root=findViewById<View>(R.id.rootLayout);ViewCompat.setOnApplyWindowInsetsListener(root){v,i->val b=i.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime());v.setPadding(b.left,b.top,b.right,b.bottom);i}
  store=ChatStore(this);modelManager=LocalModelManager(this);ai=NativeLocalAiRuntime(modelManager);val input=findViewById<EditText>(R.id.messageInput);val chat=findViewById<TextView>(R.id.chatText);val welcome=findViewById<View>(R.id.welcomePanel);val tools=findViewById<View>(R.id.toolBar);val scroll=findViewById<ScrollView>(R.id.chatScroll)
  fun render(){chat.text=transcript.toString();welcome.visibility=if(transcript.isEmpty())View.VISIBLE else View.GONE;scroll.post{scroll.fullScroll(View.FOCUS_DOWN)}}
  fun persist(){if(transcript.isEmpty())return;val id=current?:UUID.randomUUID().toString().also{current=it};store.save(id,transcript.toString())}
  fun append(who:String,text:String){if(transcript.isNotEmpty())transcript.append("\n\n");transcript.append(who).append("\n").append(text);persist();render()}
  fun send(){val q=input.text.toString().trim();if(q.isEmpty())return;append("You",q);input.text.clear();when(ai.state){RuntimeState.Ready->ai.generate(InferenceRequest(q),{}, {r->runOnUiThread{append("Kylow",r.getOrElse{"Local AI error: "+(it.message?:"unknown error")})}});RuntimeState.Loading->append("Kylow","My local model is still loading.");is RuntimeState.Error->append("Kylow","My local AI runtime needs attention.");RuntimeState.Unavailable->android.app.AlertDialog.Builder(this).setTitle("Set up Kylow AI").setMessage("Kylow needs its local AI model before it can answer. Download the approved model now? The model is about 397 MB and will be stored privately on this device.").setPositiveButton("Download"){_,_->append("Kylow","Downloading and verifying my local AI model…");Thread{val result=ModelProvisioner(modelManager).download(NativeLocalAiRuntime.PINNED_MODEL){p->runOnUiThread{val total=p.totalBytes?.let{" / "+(it/1_000_000)+" MB"}?:"";Toast.makeText(this,"Kylow model: "+(p.downloadedBytes/1_000_000)+" MB"+total,Toast.LENGTH_SHORT).show()}};runOnUiThread{result.fold(onSuccess={ai.close();ai=NativeLocalAiRuntime(modelManager);append("Kylow","Local AI model verified and installed. I'm initializing it now—send your message again in a moment.")},onFailure={append("Kylow","Model setup failed safely: "+(it.message?:"unknown error"))})}}.start()}.setNegativeButton("Not now",null).show()}}
  findViewById<TextView>(R.id.sendButton).setOnClickListener{send()};input.setOnEditorActionListener{_,id,_->if(id==EditorInfo.IME_ACTION_SEND){send();true}else false}
  fun prompt(text:String){input.setText(text);input.setSelection(input.text.length);input.requestFocus()}
  findViewById<TextView>(R.id.toolsButton).setOnClickListener{tools.visibility=if(tools.visibility==View.VISIBLE)View.GONE else View.VISIBLE; if(tools.visibility==View.VISIBLE) scroll.post{scroll.fullScroll(View.FOCUS_DOWN)}}
  findViewById<TextView>(R.id.menuButton).setOnClickListener{val items=store.list();val labels=mutableListOf("New chat","Files & Library","Plugins","Settings","Check for updates");items.take(20).forEach{labels.add((if(it.pinned)"★ " else "")+it.title)};android.app.AlertDialog.Builder(this).setTitle("Kylow").setItems(labels.toTypedArray()){_,p->when{p==0->{persist();current=null;transcript.clear();render()};p==1->append("Kylow","Library is ready for local files and future connected storage.");p==2->append("Kylow","Plugins are optional extensions. Kylow remains usable without them.");p==3->append("Kylow","Settings will manage privacy, memory, devices and plugins.");p==4->{val updater=AppUpdater(this);updater.check({u->if(u==null)Toast.makeText(this,"Kylow is up to date",Toast.LENGTH_SHORT).show() else android.app.AlertDialog.Builder(this).setTitle("Kylow "+u.versionName+" available").setMessage("Download the approved update now?").setPositiveButton("Update"){_,_->updater.download(u){Toast.makeText(this,"Update downloading",Toast.LENGTH_SHORT).show()}}.setNegativeButton("Later",null).show()},{e->Toast.makeText(this,e,Toast.LENGTH_LONG).show()})};else->{val c=items[p-5];current=c.id;transcript.clear();transcript.append(c.body);render()}}}.show()}
  findViewById<TextView>(R.id.newChatButton).setOnClickListener{persist();current=null;transcript.clear();chat.text="";welcome.visibility=View.VISIBLE;input.text.clear()}
  findViewById<Button>(R.id.filesButton).setOnClickListener{findViewById<TextView>(R.id.menuButton).performClick()}
  findViewById<Button>(R.id.webButton).setOnClickListener{prompt("Search the web for ")}
  findViewById<Button>(R.id.tasksButton).setOnClickListener{prompt("Create a task: ")}
  findViewById<Button>(R.id.chatHomeButton).setOnClickListener{input.requestFocus()}
  findViewById<Button>(R.id.tasksHomeButton).setOnClickListener{prompt("Create a task: ")}
  findViewById<Button>(R.id.filesHomeButton).setOnClickListener{findViewById<TextView>(R.id.menuButton).performClick()}
  findViewById<Button>(R.id.webHomeButton).setOnClickListener{prompt("Search the web for ")}
  findViewById<Button>(R.id.pcHomeButton).setOnClickListener{startActivity(Intent(this,if(RemotePcConfig.paired(this)) RemotePcActivity::class.java else RemotePcPairActivity::class.java))}
  findViewById<Button>(R.id.createHomeButton).setOnClickListener{prompt("Help me create ")}
  findViewById<Button>(R.id.learnHomeButton).setOnClickListener{prompt("Teach me about ")}
  findViewById<Button>(R.id.moreHomeButton).setOnClickListener{findViewById<TextView>(R.id.menuButton).performClick()}\n  findViewById<TextView>(R.id.bottomHome).setOnClickListener{transcript.clear();render();scroll.post{scroll.fullScroll(View.FOCUS_UP)}}\n  findViewById<TextView>(R.id.bottomChat).setOnClickListener{input.requestFocus()}\n  findViewById<TextView>(R.id.bottomTasks).setOnClickListener{prompt("Create a task: ")}\n  findViewById<TextView>(R.id.bottomMemories).setOnClickListener{append("Kylow","Memories are stored privately on this device. Memory management is still being completed.")}\n  findViewById<TextView>(R.id.bottomMore).setOnClickListener{findViewById<TextView>(R.id.menuButton).performClick()}
  findViewById<Button>(R.id.canDoButton).setOnClickListener{prompt("What can you do?")}
  findViewById<Button>(R.id.planButton).setOnClickListener{prompt("Help me plan ")}
  findViewById<Button>(R.id.imageButton).setOnClickListener{prompt("Create an image of ")}
  store.latest()?.let{current=it.id;transcript.append(it.body);render()}
 }
 override fun onDestroy(){ai.close();super.onDestroy()}
}
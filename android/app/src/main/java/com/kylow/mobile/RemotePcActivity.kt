package com.kylow.mobile
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import kotlin.concurrent.thread
class RemotePcActivity:AppCompatActivity(){
 private lateinit var status:TextView
 override fun onCreate(b:Bundle?){super.onCreate(b);title="Kylow Remote PC";val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(32,32,32,32)};status=TextView(this).apply{text="PC: not checked";textSize=18f};root.addView(status)
 fun add(label:String,run:()->Unit){root.addView(Button(this).apply{text=label;setOnClickListener{run()}})}
 add("Check PC"){bg{RemotePcClient.health(this)}};add("Open Godot"){act(mapOf("type" to "open_app","app" to "godot"))};add("Open Chrome"){act(mapOf("type" to "open_app","app" to "chrome"))};add("Open VS Code"){act(mapOf("type" to "open_app","app" to "vscode"))};add("PC Status"){act(mapOf("type" to "status"))}
 val input=EditText(this).apply{hint="Type text on PC"};root.addView(input);add("Send Text"){act(mapOf("type" to "type_text","text" to input.text.toString()))};add("Screenshot"){act(mapOf("type" to "screenshot"))};add("Disconnect PC"){RemotePcConfig.clear(this);status.text="PC: disconnected"};setContentView(ScrollView(this).apply{addView(root)})}
 private fun act(m:Map<String,Any?>)=bg{RemotePcClient.action(this,JSONObject(m).toString())}
 private fun bg(call:()->RemotePcClient.Result){status.text="Working...";thread{val x=call();runOnUiThread{status.text=if(x.ok)"PC: connected\n"+x.body else "PC error: "+x.body}}}
}
package com.kylow.mobile

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import androidx.core.content.FileProvider
import org.json.JSONObject
import java.io.File
import java.net.URL

data class UpdateInfo(val versionCode:Int,val versionName:String,val apkUrl:String)

class AppUpdater(private val activity:Activity){
 companion object { const val MANIFEST_URL="https://raw.githubusercontent.com/vincentguerrerojrTLA/personal-ai/main/update/latest.json" }
 fun check(onResult:(UpdateInfo?)->Unit,onError:(String)->Unit){
  Thread {
   try{
    val json=JSONObject(URL(MANIFEST_URL).readText())
    val info=UpdateInfo(json.getInt("versionCode"),json.getString("versionName"),json.getString("apkUrl"))
    val installed=activity.packageManager.getPackageInfo(activity.packageName,0).longVersionCode
    activity.runOnUiThread{onResult(if(info.versionCode>installed)info else null)}
   }catch(e:Exception){activity.runOnUiThread{onError(e.message?:"Update check failed")}}
  }.start()
 }
 fun download(info:UpdateInfo,onStarted:(Long)->Unit){
  val req=DownloadManager.Request(Uri.parse(info.apkUrl)).setTitle("Kylow "+info.versionName).setDescription("Downloading Kylow update").setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED).setDestinationInExternalFilesDir(activity,Environment.DIRECTORY_DOWNLOADS,"Kylow-update.apk")
  val dm=activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
  onStarted(dm.enqueue(req))
 }
 fun installDownloaded(){
  if(!activity.packageManager.canRequestPackageInstalls()){
   activity.startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,Uri.parse("package:"+activity.packageName)));return
  }
  val apk=File(activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"Kylow-update.apk")
  if(!apk.exists())return
  val uri=FileProvider.getUriForFile(activity,activity.packageName+".files",apk)
  activity.startActivity(Intent(Intent.ACTION_VIEW).setDataAndType(uri,"application/vnd.android.package-archive").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK))
 }
}
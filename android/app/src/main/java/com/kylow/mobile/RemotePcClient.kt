package com.kylow.mobile
import android.content.Context
import java.net.HttpURLConnection
import java.net.URL
object RemotePcClient {
 data class Result(val ok:Boolean,val body:String)
 fun health(context:Context):Result=request(context,"GET","/health",null)
 fun action(context:Context,json:String):Result=request(context,"POST","/action",json)
 private fun request(context:Context,method:String,path:String,body:String?):Result{
  val base=RemotePcConfig.endpoint(context)?:return Result(false,"PC not paired")
  val token=RemotePcConfig.token(context)?:return Result(false,"PC not paired")
  return try{
   val c=(URL(base+path).openConnection() as HttpURLConnection).apply{requestMethod=method;connectTimeout=7000;readTimeout=10000;setRequestProperty("Authorization","Bearer $token");setRequestProperty("Content-Type","application/json");doInput=true}
   if(body!=null){c.doOutput=true;c.outputStream.use{it.write(body.toByteArray())}}
   val code=c.responseCode; val s=if(code in 200..299)c.inputStream else c.errorStream
   Result(code in 200..299,s?.bufferedReader()?.use{it.readText()}?:"HTTP $code")
  }catch(e:Exception){Result(false,e.message?:"Connection failed")}
 }
}

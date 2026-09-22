package com.kylow.mobile.gateway

import java.net.URI

object EndpointPolicy {
 fun allowed(url:String):Boolean = runCatching {
  val u=URI(url)
  u.scheme.equals("https",true)&&!u.host.isNullOrBlank()&&u.userInfo==null
 }.getOrDefault(false)
}

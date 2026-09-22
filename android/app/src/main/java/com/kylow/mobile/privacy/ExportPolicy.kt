package com.kylow.mobile.privacy

object ExportPolicy{
 private val forbidden=setOf("password","token","private_key","credential")
 fun include(tag:String)=tag.lowercase() !in forbidden
}

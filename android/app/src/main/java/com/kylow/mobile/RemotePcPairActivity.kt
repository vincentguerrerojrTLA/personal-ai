package com.kylow.mobile

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.kylow.mobile.gateway.DesktopPairingLinkParser

class RemotePcPairActivity:AppCompatActivity(){
    override fun onCreate(b:Bundle?){
        super.onCreate(b)
        val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(32,32,32,32)}
        val link=EditText(this).apply{
            hint="One-time Kylow pairing link"
            minLines=3
        }
        val pair=Button(this).apply{
            text="Verify pairing link"
            setOnClickListener{
                runCatching{ DesktopPairingLinkParser.parse(link.text.toString()) }
                    .onSuccess{
                        Toast.makeText(
                            this@RemotePcPairActivity,
                            "Pairing link verified. Waiting for secure Desktop enrollment.",
                            Toast.LENGTH_LONG
                        ).show()
                        // Do not persist the one-time code. The next bridge slice exchanges it
                        // for a device-specific revocable credential after Desktop verification.
                    }
                    .onFailure{
                        Toast.makeText(
                            this@RemotePcPairActivity,
                            it.message ?: "Invalid pairing link",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }
        root.addView(TextView(this).apply{text="Pair this device with Kylow";textSize=22f})
        root.addView(TextView(this).apply{text="Use the one-time link shown by your trusted Kylow Desktop. Permanent credentials are never entered here."})
        root.addView(link)
        root.addView(pair)
        setContentView(root)
    }
}

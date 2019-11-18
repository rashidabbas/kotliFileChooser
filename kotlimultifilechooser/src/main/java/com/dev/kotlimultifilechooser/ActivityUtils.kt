package com.all.media.kotlin

import android.app.Activity
import android.content.Intent
import com.dev.kotlimultifilechooser.ReturnType

fun <T: KittyFileSelectionListener> Activity.openKittyChooser(delegate:T, vararg mediaTyle: ReturnType){
    
    var mediaTypes = ArrayList<Int>()
    mediaTyle.forEach {
            mediaTypes.add(it.value)
    }

    kittyFileSelectionListener = delegate
    var intent = Intent(this, KittyChooserMain::class.java)
    intent.putExtra("LIST",mediaTypes)
    startActivity(intent)
}
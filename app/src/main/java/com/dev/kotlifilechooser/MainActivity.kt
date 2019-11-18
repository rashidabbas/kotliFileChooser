package com.dev.kotlifilechooser

import Models.ResultModel
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.all.media.kotlin.KittyFileSelectionListener
import com.all.media.kotlin.openKittyChooser
import com.dev.kotlimultifilechooser.ReturnType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), KittyFileSelectionListener {

    override fun onKittySelection(result: ArrayList<ResultModel>) {

        result.forEach {
            Log.d("FINALRESULT",it.itemFile.path)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chooserButton.setOnClickListener {
            openKittyChooser(this, ReturnType.IMAGES,ReturnType.VIDEOS,ReturnType.AUDIOS,ReturnType.DOCUMENTS)
        }

    }
}

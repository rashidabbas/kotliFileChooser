package com.all.media.kotlin

import Fragments.MainFragment
import Models.Gallery_Buckets_Model
import Models.ResultModel
import android.app.Activity
import android.app.ProgressDialog
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.loader.content.CursorLoader
import com.dev.kotlimultifilechooser.R
import com.dev.kotlimultifilechooser.ReturnType
import kotlinx.android.synthetic.main.kitty_chooser_main.*
import java.io.File
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap



var kittyFileSelectionListener: KittyFileSelectionListener? = null

class KittyChooserMain : AppCompatActivity() {

    var sum = 0
    lateinit var selectedList: ArrayList<ResultModel>
    lateinit var paths : LinkedHashMap<String,ArrayList<Gallery_Buckets_Model>>

    lateinit var progressDialog1: ProgressDialog
    lateinit var progressDialog2: ProgressDialog
    lateinit var progressDialog3: ProgressDialog


    internal var context: Activity? = null
    lateinit var Bucketids: java.util.ArrayList<String>
    var BucketId: String = ""
    lateinit var BucketName: String
    lateinit var ImagePath: String
    var TotalVideos: Int = 0
    var BucketSize: Int = 0
    private var BucketIdColumnIndex: Int = 0
    private var BucketNameColumnIndex: Int = 0
    private var CursorSize: Int = 0
    private var DataColumnIndex: Int = 0

    var isDocFragment : Boolean = false

    var list: ArrayList<Int>? = null
    lateinit var imagesList: ArrayList<Gallery_Buckets_Model>
    lateinit var videosList: ArrayList<Gallery_Buckets_Model>
    lateinit var audiosList: ArrayList<Gallery_Buckets_Model>
    lateinit var docsList: ArrayList<Gallery_Buckets_Model>

    var openFragmentButton : TextView   ? = null
    var Name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kitty_chooser_main)

        openFragmentButton = Button(this)
        context = this

        IsStorageReadWriteAllow(onPermissionAccepted =
        {
            IsCameraAllow(onPermissionAccepted = {
                init()
            },onPermissionDenied = {
                Toast.makeText(this,"YOU MUST ALLOW PERMISSION TO PROCEED",Toast.LENGTH_LONG).show()
                finish()
            })
        },onPermissionDenied = {
            Toast.makeText(this,"YOU MUST ALLOW PERMISSION TO PROCEED",Toast.LENGTH_LONG).show()
            finish()
        })

    }


    fun initProgress() : ProgressDialog{
        var progress = ProgressDialog(this,R.style.AppCompatAlertDialogStyle)
        progress.setCancelable(false)
        progress.setMessage("Loading Items...")
        return progress
    }

    fun fragmentTransaction(fragment: Fragment, fragmentName: String, bundle: Bundle? = null) {
        val fragmentManager = supportFragmentManager

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            android.R.anim.slide_in_left, android.R.anim.slide_out_right
        )
        fragment.arguments = bundle
        fragmentTransaction.add(R.id.Container, fragment)
        fragmentTransaction.addToBackStack(fragmentName)
        fragmentTransaction.commit()
    }

    private inner class LoadingImagesTask(var uri: Uri, var list: ArrayList<Gallery_Buckets_Model>, var type: Int) :
        AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg voids: Void): Void? {

            runOnUiThread {
                try {
                    var iCursor = initializeCursorLoader(uri, null)
                    ShowWithFolders(iCursor!!, list!!, type)
                } catch (e: Exception) {
                    Log.d("TAGIMAGE",e.message.toString())
//                Toast.makeText(context,"IMAGE Exception",Toast.LENGTH_LONG).show()
                }
            }

            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()

            progressDialog1.show()
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            progressDialog1.dismiss()
            fragmentTransaction(MainFragment(), "MainFragment")
        }
    }

    private inner class LoadingAudioTask : AsyncTask<Void, Void, Void>() {

        override fun onPreExecute() {
            super.onPreExecute()

            progressDialog2.show()
        }

        override fun doInBackground(vararg p0: Void?): Void? {
            try {
                var list = getAudioDirectories()
                var sum = 0
                list.forEach {
                    audiosList.add(
                        Gallery_Buckets_Model(
                            it.dir.substringAfterLast('/'),
                            it.songInfo.size,
                            it.dir,
                            ""
                        )
                    )
                    sum = sum + it.songInfo.size

                }
//                audiosList.add(0, Gallery_Buckets_Model("All Audios", sum, "", ""))
            } catch (e: Exception) {

            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            progressDialog2.dismiss()
            fragmentTransaction(MainFragment(), "MainFragment")
        }
    }

    private inner class LoadingDocTask : AsyncTask<Void, Void, Void>() {

        override fun onPreExecute() {
            super.onPreExecute()


            progressDialog3.show()

        }

        override fun doInBackground(vararg p0: Void?): Void? {

            try {
                GetAllDocumentsFromSDCARD(Environment.getExternalStorageDirectory().absoluteFile)
            } catch (e: Exception) {

            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            progressDialog3.dismiss()
            fragmentTransaction(MainFragment(), "MainFragment")
        }
    }

    fun GetAllDocumentsFromSDCARD(dir: File) {
        try {
            val listFile = dir.listFiles()
            if (listFile != null && listFile.size > 0) {
                for (aListFile in listFile) {
                    if (aListFile.isDirectory) {
                        GetAllDocumentsFromSDCARD(aListFile)
                    } else {
                        Name = aListFile.name.toLowerCase()
                        if (Name!!.endsWith(".doc") || Name!!.endsWith(".docx")
                            || Name!!.endsWith(".pdf") || Name!!.endsWith(".ppt")
                            || Name!!.endsWith(".pptx") || Name!!.endsWith(".xls") || Name!!.endsWith(".xlsx")
                            || Name!!.endsWith(".csv") || Name!!.endsWith(".txt") || Name!!.endsWith(".rtf")
                        ) {
                            docsList.add(
                                Gallery_Buckets_Model(
                                    aListFile.name,
                                    (aListFile.length() / 1024).toInt(),
                                    aListFile.path,
                                    "",
                                    "DOC"
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Get all documents Exp\n" + e.message, Toast.LENGTH_SHORT).show()
        }

    }

    data class SongInfo(var songURL: String, var songAuth: String, var songNAme: String)
    data class DirInfo(var dir: String, var songInfo: java.util.ArrayList<SongInfo>)

    fun getAudioDirectories(): java.util.ArrayList<DirInfo> {

        var result = java.util.ArrayList<DirInfo>()

        val directories = LinkedHashMap<String, java.util.ArrayList<SongInfo>>()

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"

        val order = MediaStore.Audio.Media.DATE_MODIFIED + " DESC"

        val cursor = this.contentResolver.query(uri, null, selection, null, order)

        cursor.let {
            it!!.moveToFirst()

            val pathIndex = it!!.getColumnIndex(MediaStore.Images.Media.DATA)

            do {
                val path = it!!.getString(pathIndex)

                val file = File(path)
                if (!file.exists()) {
                    continue
                }

                val fileDir = file.getParent()

                var songURL = it!!.getString(it!!.getColumnIndex(MediaStore.Audio.Media.DATA))
                var songAuth = it!!.getString(it!!.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                var songName = it!!.getString(it!!.getColumnIndex(MediaStore.Audio.Media.TITLE))

                if (directories.containsKey(fileDir)) {
                    var songs = directories.getValue(fileDir);

                    var song = SongInfo(songURL, songAuth, songName)

                    songs.add(song)

                    directories.put(fileDir, songs)
                } else {
                    var song = SongInfo(songURL, songAuth, songName)

                    var songs = java.util.ArrayList<SongInfo>()
                    songs.add(song)

                    directories.put(fileDir, songs)
                }
            } while (it.moveToNext())


            for (dir in directories) {
                var dirInfo: DirInfo = DirInfo(dir.key, dir.value);

                result.add(dirInfo)
            }
        }

        return result
    }

    fun initializeCursorLoader(uriType: Uri, selection: String?): Cursor? {
        val COLUMNS = arrayOf("_id", "_data", "bucket_id", "bucket_display_name")

        val cursorLoader = CursorLoader(
            this!!, // Context
            uriType, // Uri
            COLUMNS, // Projection
            selection,
            null, // Selection Args
            "date_added" + " DESC"
        )// Selection

        return cursorLoader.loadInBackground()
    }

    fun ShowWithFolders(cursor: Cursor, mList: ArrayList<Gallery_Buckets_Model>, type: Int) {
        CursorSize = cursor!!.count
        if (CursorSize > 0) {
//            mList = ArrayList<Gallery_Buckets_Model>()
            Bucketids = java.util.ArrayList()
            TotalVideos = 0
            BucketSize = 0
            DataColumnIndex = cursor!!.getColumnIndex(MediaStore.Video.Media.DATA)
            BucketIdColumnIndex = cursor!!.getColumnIndex(MediaStore.Video.Media.BUCKET_ID)
            BucketNameColumnIndex = cursor!!.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
            for (i in 0 until CursorSize) {
                cursor!!.moveToPosition(i)
                BucketId = cursor!!.getString(BucketIdColumnIndex)
                if (!Bucketids.contains(BucketId)) {
                    ImagePath = cursor!!.getString(DataColumnIndex)
                    BucketName = cursor!!.getString(BucketNameColumnIndex)

                    if (type == 1) {
                        BucketSize = GetBucketSize(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            "bucket_display_name = \"$BucketName\""
                        )
                    } else if (type == 2) {
                        BucketSize = GetBucketSize(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            "bucket_display_name = \"$BucketName\""
                        )
                    } else if (type == 3) {
                        BucketSize = GetBucketSize(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            "bucket_display_name = \"$BucketName\""
                        )
                    }
                    mList.add(Gallery_Buckets_Model(BucketName, BucketSize, ImagePath, BucketId))
                    Bucketids.add(BucketId)
                    TotalVideos += BucketSize
                }
            }
            cursor!!.moveToPosition(0)
            ImagePath = cursor!!.getString(DataColumnIndex)

//            if (type == ReturnType.IMAGES.value) {
//                mList.add(0, Gallery_Buckets_Model("All Images", TotalVideos, ImagePath, "!1@2"))
//            } else if (type == ReturnType.VIDEOS.value) {
//                mList.add(0, Gallery_Buckets_Model("All Videos", TotalVideos, ImagePath, "!1@2"))
//            }

        } else {
        }
    }

    fun GetBucketSize(uriType: Uri, selection: String): Int {
        var BucketSize = 0
        var cursor: Cursor? = null
        try {
            val cursorLoader = CursorLoader(
                this!!, // Context
                uriType,
                null, // Projection
                selection,
                null, // Selection Args
                "date_added" + " DESC"
            )// Uri
            // Selection
            cursor = cursorLoader.loadInBackground()
            if (cursor!!.count > 0) {
                BucketSize = cursor.count
            }
        } catch (e: Exception) {
            BucketSize = 0
            Toast.makeText(context, "Count Bucket Size Exp\n" + e.message, Toast.LENGTH_SHORT).show()
        } finally {
            cursor?.close()
        }
        return BucketSize
    }


    fun init(){

        progressDialog1 = initProgress()
        progressDialog2 = initProgress()
        progressDialog3 = initProgress()
        selectedList = ArrayList<ResultModel>()
        paths = LinkedHashMap<String,ArrayList<Gallery_Buckets_Model>>()

        list = intent.getIntegerArrayListExtra("LIST")

        if(list.isNullOrEmpty()){
            finish()
        }

        if(list!![0] == 4){
            Iv_selectAllDoc.visibility = View.VISIBLE
        }


        list = list!!.distinct() as ArrayList<Int>

        if(list!!.contains(ReturnType.IMAGES.value)) {

            imagesList = ArrayList<Gallery_Buckets_Model>()
            LoadingImagesTask(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imagesList, 1).execute()

        }
        if(list!!.contains(ReturnType.VIDEOS.value)) {

            videosList = ArrayList<Gallery_Buckets_Model>()
            LoadingImagesTask(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videosList, 2).execute()

        }
        if(list!!.contains(ReturnType.AUDIOS.value)) {

            audiosList = ArrayList<Gallery_Buckets_Model>()
            LoadingAudioTask().execute()

        }
        if(list!!.contains(ReturnType.DOCUMENTS.value)) {

            docsList = ArrayList<Gallery_Buckets_Model>()
            LoadingDocTask().execute()
        }


        bt_Button.setOnClickListener {

            paths.forEach {
                val finalList = it.value.filter { it.isSelected }
                finalList.forEach {
                    selectedList.add(ResultModel(it.stringType,File(it.ImagePath)))
                }

            }
            kittyFileSelectionListener!!.onKittySelection(selectedList)
            finish()
//            selectedList.forEach {
//                Log.d("FINAL","TYPE = " + it.type + " --- PATH = " + it.itemFile.path)
//            }
        }

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onPermissionsResult(grantResults)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Iv_selectAll.visibility = View.GONE
    }
}

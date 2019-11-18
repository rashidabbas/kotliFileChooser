package Fragments

import Adapters.IVG_GalleryAdapter
import Models.Gallery_Buckets_Model
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.loader.content.CursorLoader
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.kitty_chooser_main.*
import kotlinx.android.synthetic.main.fragment_gallery_ivg.*
import java.io.File
import kotlin.collections.ArrayList
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.DocumentsContract
import android.util.Log
import androidx.core.content.FileProvider
import com.all.media.kotlin.KittyChooserMain
import com.dev.kotlimultifilechooser.R
import com.dev.kotlimultifilechooser.ReturnType
import java.io.IOException
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*


class IVG_Fragment : Fragment() {

    private var mCurrentPhotoPath: String? = null;
    internal var context: Activity? = null
    lateinit var RvItems: RecyclerView
    lateinit var bucketList: ArrayList<Gallery_Buckets_Model>

    var BucketId: String = ""
    lateinit var BucketName: String
    lateinit var ImagePath: String

    private var cursor: Cursor? = null
    private var CursorSize: Int = 0
    private var DataColumnIndex: Int = 0
    internal var type: Int = 0

    private var PICK_Camera_IMAGE = 1;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_gallery_ivg, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Init(view)
    }


    @SuppressLint("RestrictedApi")
    private fun Init(view: View) {

        try {
            context = activity!!

            type = arguments!!.getInt("Type", 0)
            BucketName = arguments!!.getString("BucketName", "")
            bucketList = ArrayList<Gallery_Buckets_Model>()

            RvItems = view.findViewById(R.id.RvItems) as RecyclerView

            //// This condition will become true when ViewPager Load this fragment to show Directories
            if(BucketName.equals("")) {
                if (type == ReturnType.IMAGES.value) {
                    setFolderRecyclerView((activity!! as KittyChooserMain).imagesList,ReturnType.IMAGES.value)
                    fb_open_camera.visibility = View.VISIBLE;
                } else if (type == ReturnType.VIDEOS.value) {
                    setFolderRecyclerView((activity!! as KittyChooserMain).videosList,ReturnType.VIDEOS.value)
                } else if (type == ReturnType.AUDIOS.value) {
                    setFolderRecyclerView((activity!! as KittyChooserMain).audiosList,ReturnType.AUDIOS.value)
                } else{//ReturnType.DOCUMENTS.value
                    setFolderRecyclerView((activity!! as KittyChooserMain).docsList,ReturnType.DOCUMENTS.value)
                    (activity!! as KittyChooserMain).paths.put("DOC",(activity!! as KittyChooserMain).docsList!!)

                    (activity!! as KittyChooserMain).Iv_selectAllDoc.setOnClickListener {
                        if ((context as KittyChooserMain).docsList!!.any { !it.isSelected }) {

                            (context as KittyChooserMain).Iv_selectAllDoc.setImageResource(R.drawable.all_selected)

                            (context as KittyChooserMain).docsList!!.forEach {
                                if (!it.isSelected) {
                                    it.isSelected = true
                                    (context as KittyChooserMain).sum++
                                }
                            }
                        } else {

                            (context as KittyChooserMain).Iv_selectAllDoc.setImageResource(R.drawable.select)

                            (context as KittyChooserMain).docsList!!.forEach {
                                if (it.isSelected) {
                                    it.isSelected = false
                                    (context as KittyChooserMain).sum--
                                }
                            }
                        }
                        RvItems.adapter!!.notifyDataSetChanged()
                        (context as KittyChooserMain).tv_count.setText((context as KittyChooserMain).sum.toString())
                    }
                }


            //// Else part will execute when this fragment shows files
            }else{


                // Path of directory to show its files
                var path = arguments!!.getString("PATH")

                if (type == ReturnType.IMAGES.value) {

                    //// If subDirectory is opened 1st time to select something
                    if(!(activity!! as KittyChooserMain).paths.containsKey(path)) {
                        setImageOrVideoItems(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "bucket_display_name = \"$BucketName\"")
                        showFiles("IMAGE",2)
                        (activity!! as KittyChooserMain).paths.put(path!!,bucketList!!)


                    //// Resume State of Fragment where files were selected
                    }else{
                        RvItems.layoutManager = GridLayoutManager(context, 2) as GridLayoutManager
                        RvItems.adapter = IVG_GalleryAdapter(context!!, (activity!! as KittyChooserMain).paths[path]!!,  type,true)
                    }
                } else if (type == ReturnType.VIDEOS.value) {

                    //// If subDirectory is opened 1st time to select something
                    if(!(activity!! as KittyChooserMain).paths.containsKey(path)) {
                        setImageOrVideoItems(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "bucket_display_name = \"$BucketName\"")
                        showFiles("VIDEO",2)
                        (activity!! as KittyChooserMain).paths.put(path!!,bucketList!!)


                    //// Resume State of Fragment where files were selected
                    }else{
                        RvItems.layoutManager = GridLayoutManager(context, 2)
                        RvItems.adapter = IVG_GalleryAdapter(context!!, (activity!! as KittyChooserMain).paths[path]!!,  type,true)
                    }

                } else if (type == ReturnType.AUDIOS.value) {

                    //// If subDirectory is opened 1st time to select something
                    if(!(activity!! as KittyChooserMain).paths.containsKey(path)) {


                        var list = (activity!! as KittyChooserMain).getAudioDirectories()
                        var position = arguments!!.getInt("POSITION")
                        list[position].songInfo.forEach {
                            bucketList.add(Gallery_Buckets_Model(it.songNAme,0,it.songURL,"","AUDIO"))
                        }

                        (activity!! as KittyChooserMain).paths.put(path!!,bucketList!!)
                        RvItems.layoutManager = GridLayoutManager(activity!!,2)
                        RvItems.adapter = IVG_GalleryAdapter(context!!, bucketList, 3,true)


                    //// Resume State of Fragment where files were selected
                    }else{
                        RvItems.layoutManager = GridLayoutManager(activity!!,2)
                        RvItems.adapter = IVG_GalleryAdapter(context!!, (activity!! as KittyChooserMain).paths[path]!!, 3,true)
                    }

                }
                (activity as KittyChooserMain).Iv_selectAll.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            Toast.makeText(context, "IVG_FRAGMENT Init Exp\n" + e.message, Toast.LENGTH_SHORT).show()
        } finally {
            if (cursor != null) {
                cursor!!.close()
            }
        }


        fb_open_camera.setOnClickListener {

            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        null
                    }
                    // Continue only if the File was successfully created

                    try{
                        photoFile?.also {
                            val photoURI: Uri = FileProvider.getUriForFile(
                                activity!!,
                                "com.all.media.kotlin.fileprovider",
                                it
                            )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            startActivityForResult(takePictureIntent, PICK_Camera_IMAGE)
                        }
                    }catch (e : Exception){
                        Log.d("TAGIMAGE",e.message.toString())
                        Toast.makeText(context,"TAG",Toast.LENGTH_LONG).show()
                    }
                }
            }
//            val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            val file: File = createFile()
//
//            var str = file.absoluteFile
//
//            val uri: Uri = FileProvider.getUriForFile(
//               activity!!,
//                "com.example.android.fileprovider",
//                file
//            )
//            intent.putExtra(MediaStore.EXTRA_OUTPUT,uri)
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            startActivityForResult(intent, PICK_Camera_IMAGE)
        }
    }


    fun unselectAll(){
        bucketList.forEach { it.isSelected = false }
        (activity!! as KittyChooserMain).docsList.forEach { it.isSelected = false}
        (activity!! as KittyChooserMain).paths.forEach { it.value.forEach { it.isSelected = false } }
        (activity!! as KittyChooserMain).sum = 0
        (activity!! as KittyChooserMain).tv_count.setText((activity!! as KittyChooserMain).sum.toString())
        RvItems.adapter!!.notifyDataSetChanged()
    }


    fun setImageOrVideoItems(uri :Uri,selection : String?){
            cursor = (activity!! as KittyChooserMain).initializeCursorLoader(uri, selection)
    }

    fun setFolderRecyclerView(list : ArrayList<Gallery_Buckets_Model>,type : Int){

        if(list.size > 0) {
            RvItems.layoutManager = GridLayoutManager(context, 2)
            if (type == ReturnType.DOCUMENTS.value) {
                RvItems.adapter = IVG_GalleryAdapter(context!!, list, type, true)
            } else {
                RvItems.adapter = IVG_GalleryAdapter(context!!, list, type, false)
            }
        }else{
            Iv_bin.visibility = View.VISIBLE
            RvItems.visibility = View.GONE
        }
    }

    fun showFiles(stringType : String,gridSize : Int){

        CursorSize = cursor!!.getCount()
        if (CursorSize > 0) {

            DataColumnIndex = cursor!!.getColumnIndexOrThrow("_data")
            for (i in 0 until CursorSize) {
                cursor!!.moveToPosition(i)
                ImagePath = cursor!!.getString(DataColumnIndex)
                bucketList.add(Gallery_Buckets_Model(ImagePath.substringAfterLast("/"), 0, ImagePath, BucketId,stringType))
            }
            RvItems.layoutManager = GridLayoutManager(context, gridSize)
            RvItems.adapter = IVG_GalleryAdapter(context!!, bucketList,  type,true)
        }
    }

    companion object {


        fun newInstance(type: Int): IVG_Fragment {
            val bundle = Bundle()
            bundle.putInt("Type", type)
            val fragment = IVG_Fragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == PICK_Camera_IMAGE && resultCode == Activity.RESULT_OK) {
            val auxFile = File(mCurrentPhotoPath)

            var camera = (activity!! as KittyChooserMain).imagesList.filter {
                it.BucketName == "Camera"
            }
            var cameraItem = camera.get(0)
            cameraItem.ImagePath = auxFile.path
            cameraItem.BucketSize++
            RvItems.adapter!!.notifyDataSetChanged()


            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                val f = File(mCurrentPhotoPath)

                mediaScanIntent.data = Uri.fromFile(f)
                activity!!.sendBroadcast(mediaScanIntent)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = File(Environment.getExternalStorageDirectory().path + "/DCIM/Camera")
//        val storageDir: File = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = absolutePath
        }
    }

}
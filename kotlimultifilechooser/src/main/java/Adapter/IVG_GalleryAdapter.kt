package Adapters

import Fragments.IVG_Fragment
import Fragments.MainFragment
import Models.Gallery_Buckets_Model
import Models.ResultModel
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.StringSignature
import kotlinx.android.synthetic.main.kitty_chooser_main.*
import java.io.File
import android.util.DisplayMetrics
import androidx.cardview.widget.CardView
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import com.all.media.kotlin.KittyChooserMain
import com.dev.kotlimultifilechooser.R
import com.dev.kotlimultifilechooser.ReturnType


public class IVG_GalleryAdapter(
    var context: Context,
    var bucketList: ArrayList<Gallery_Buckets_Model>,
    var type: Int,
    var isSelection : Boolean
) :
    RecyclerView.Adapter<IVG_GalleryAdapter.ViewHolder>() {

    var stringType = ""

    init {
        if(type == ReturnType.IMAGES.value) {
            stringType = "IMAGE"
        }
        else if(type == ReturnType.VIDEOS.value) {
            stringType = "VIDEO"
        }
        else if(type == ReturnType.AUDIOS.value) {
            stringType = "AUDIO"
        }
        else if(type == ReturnType.DOCUMENTS.value) {
            stringType = "DOC"
        }


        if(bucketList.any { !it.isSelected }){
            (context as KittyChooserMain).Iv_selectAll.setImageResource(R.drawable.select)
        }else{
            (context as KittyChooserMain).Iv_selectAll.setImageResource(R.drawable.all_selected)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.gallery_item,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {

            val displayMetrics = context.resources.displayMetrics
//            val dpHeight = displayMetrics.heightPixels / displayMetrics.density
//            val dpWidth = displayMetrics.widthPixels / displayMetrics.density

            Log.d("HEIGHT",(displayMetrics.widthPixels/2).toInt().toString())

            var params = holder.itemContainer!!.layoutParams
            params.height = (displayMetrics.widthPixels/2).toInt()
            holder!!.itemContainer!!.layoutParams = params
            val galleryBucketsModel = bucketList[position]


            // Show Name of Item
            holder.TvBucketTitle!!.text = galleryBucketsModel.BucketName


            // Documents tab does not have directories so remove size TextView
            if(type == ReturnType.DOCUMENTS.value){
                holder.TvBucketSize!!.visibility = View.GONE
            }


            // If folder then show number of files
            // If files then hide size
            if(!(galleryBucketsModel.BucketSize == 0)) {
                holder.TvBucketSize!!.text = galleryBucketsModel.BucketSize.toString()
            }else{
                holder.TvBucketSize!!.visibility = View.GONE
            }


            ////SELECTED OR NOT
            if(bucketList[position].isSelected){
                holder.VBackground!!.visibility = View.VISIBLE
            }else{
                holder.VBackground!!.visibility = View.GONE
            }


            /// SETTING IMAGES
            val file = File(galleryBucketsModel.ImagePath)
            if(type == ReturnType.IMAGES.value){
                    setThumbnail(file,holder)
            } else if(type == ReturnType.VIDEOS.value) {
                setThumbnail(file,holder)
            } else if(type == ReturnType.AUDIOS.value) {
                holder.IvThumbnail!!.setPadding(100,100,100,100)
                setIcon(R.drawable.headset,holder)
            } else{
                holder.IvThumbnail!!.setPadding(150,100,150,100)
                setIcon(checkResource(bucketList[position].BucketName),holder)
            }


        if(isSelection){
            selectAll(bucketList)
        }

        } catch (e: Exception) {
            Toast.makeText(context, "on bind Exp\n" + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return bucketList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var IvThumbnail: ImageView? = null
        var TvBucketSize: TextView? = null
        var TvBucketTitle: TextView? = null
        var RelativeLayout: android.widget.RelativeLayout? = null
        var VBackground: ImageView? = null
        var itemContainer : CardView? = null

        init {
            try {
                IvThumbnail = itemView.findViewById(R.id.IvThumbnail) as ImageView
                TvBucketSize = itemView.findViewById(R.id.TvBucketSize) as TextView
                TvBucketTitle = itemView.findViewById(R.id.TvBucketTitle) as TextView
                RelativeLayout = itemView.findViewById(R.id.RelativeLayout) as android.widget.RelativeLayout
                VBackground = itemView.findViewById(R.id.selected)
                itemContainer = itemView.findViewById(R.id.itemContainer)

                RelativeLayout!!.setOnClickListener {

                    if(!isSelection) {
                        if (type > 0 && type <= 3) {
                            val bundle: Bundle
                            bundle = Bundle()
                            val galleryBucketsModel = bucketList[adapterPosition]
                            bundle.putString("BucketId", galleryBucketsModel.BucketId)
                            bundle.putString("BucketName", galleryBucketsModel.BucketName)
                            bundle.putString("PATH",galleryBucketsModel.ImagePath)

                            if (type == 1) {
                                bundle.putInt("Type", ReturnType.IMAGES.value)
                            } else if (type == 2) {
                                bundle.putInt("Type", ReturnType.VIDEOS.value)
                            } else if (type == 3) {
                                bundle.putInt("Type", ReturnType.AUDIOS.value)
//                                bundle.putString("FilePath", bucketList[adapterPosition].ImagePath)
                                bundle.putInt("POSITION", adapterPosition)
                            }
                            (context as KittyChooserMain).fragmentTransaction(IVG_Fragment(), "IVG_Fragment", bundle);
                        }
                    }else{
                        selectUnselect(adapterPosition)
                    }
                }

                RelativeLayout!!.setOnLongClickListener {

                    if(isSelection){
                            selectUnselect(adapterPosition)
                    }
                    true
                }
            } catch (e: Exception) {
                Toast.makeText(context, "view holder Exp\n" + e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun selectUnselect(adapterPosition: Int){
        if(bucketList[adapterPosition].isSelected) {
            bucketList[adapterPosition].isSelected = false
            (context as KittyChooserMain).sum--
        }else{
            bucketList[adapterPosition].isSelected = true
            (context as KittyChooserMain).sum++
        }
        notifyItemChanged(adapterPosition)
        (context as KittyChooserMain).tv_count.setText((context as KittyChooserMain).sum.toString())



        Log.d("PATH", "SUM  = " + (context as KittyChooserMain).sum)
    }

    fun selectAll(bucketList: ArrayList<Gallery_Buckets_Model>){


         (context as KittyChooserMain).Iv_selectAll.setOnClickListener {
                if (bucketList.any { !it.isSelected }) {

                    (context as KittyChooserMain).Iv_selectAll.setImageResource(R.drawable.all_selected)

                    bucketList.forEach {
                        if (!it.isSelected) {
                            it.isSelected = true
                            (context as KittyChooserMain).sum++
                        }
                    }
                } else {


                    (context as KittyChooserMain).Iv_selectAll.setImageResource(R.drawable.select)

                    bucketList.forEach {
                        if (it.isSelected) {
                            it.isSelected = false
                            (context as KittyChooserMain).sum--
                        }
                    }
                }
                notifyDataSetChanged()
                (context as KittyChooserMain).tv_count.setText((context as KittyChooserMain).sum.toString())
            }
    }
    fun setIcon(imageSrc : Int,holder : ViewHolder){
        Glide.with(context)
            .load("")
            .diskCacheStrategy(DiskCacheStrategy.RESULT)
            .placeholder(imageSrc)
            .into(holder.IvThumbnail)
    }
    fun setThumbnail(file : File,holder : ViewHolder){
        Glide.with(context!!)
            .load(file)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.RESULT)
            .into(holder.IvThumbnail)
    }
    fun checkResource(Name : String) : Int{
        if (Name!!.endsWith(".doc") || Name!!.endsWith(".docx")){
            return R.drawable.doc1
        }else if(Name!!.endsWith(".pdf")){
            return R.drawable.pdf
        }else if(Name!!.endsWith(".ppt") || Name!!.endsWith(".pptx")){
            return R.drawable.ppt1
        }else if(Name!!.endsWith(".xls") || Name!!.endsWith(".xlsx") || Name!!.endsWith(".csv") || Name!!.endsWith(".rtf")){
            return R.drawable.xls
        }else if(Name!!.endsWith(".txt")){
            return R.drawable.txt
        }else{
            return R.mipmap.ic_launcher_round
        }
    }
}

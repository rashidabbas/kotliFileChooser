package Adapters

import Fragments.IVG_Fragment
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SectionsPagerAdapter(fm: FragmentManager,var list: ArrayList<Int>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {

        return IVG_Fragment.newInstance(list[position])

//        return if (position == 0) {
//            IVG_Fragment.newInstance(ReturnType.IMAGES.value)
//        } else if (position == 1) {
//            IVG_Fragment.newInstance(ReturnType.VIDEOS.value)
//        } else if (position == 2) {
//            IVG_Fragment.newInstance(ReturnType.AUDIOS.value)
//        } else {
//            IVG_Fragment.newInstance(ReturnType.DOCUMENTS.value)
//        }
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
                when (list[position]) {
                    1 -> return "IMAGES"
                    2 -> return "VIDEOS"
                    3 -> return "AUDIOS"
                    4 -> return "DOCS"
                }
//        when (position) {
//            0 -> return "PHOTOS"
//            1 -> return "VIDEOS"
//            2 -> return "AUDIOS"
//            3 -> return "DOCUMENTS"
//        }
        return null
    }
}
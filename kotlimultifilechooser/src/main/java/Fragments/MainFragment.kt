package Fragments


import Adapters.SectionsPagerAdapter
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.all.media.kotlin.KittyChooserMain
import com.dev.kotlimultifilechooser.R
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.kitty_chooser_main.*
import java.sql.Time
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class MainFragment : Fragment() {


    lateinit var  context: Activity
    private var tabs: TabLayout? = null
    private var container: ViewPager? = null
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null


    var _rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("FRAG S = TAG", Calendar.getInstance().timeInMillis.toString())
        Init(view)
        Log.d("Frag E = TAG",Calendar.getInstance().timeInMillis.toString())
    }

    private fun Init(view :View) {
        try {
            context = activity!!
            tabs = view!!.findViewById(R.id.tabs) as TabLayout
            container = view!!.findViewById(R.id.container) as ViewPager

            mSectionsPagerAdapter = SectionsPagerAdapter(childFragmentManager,(activity!! as KittyChooserMain).list!!)


            tabs!!.setTabMode(TabLayout.MODE_FIXED)
            container!!.setOffscreenPageLimit(3)
            container!!.setAdapter(mSectionsPagerAdapter)
            tabs!!.setTabGravity(TabLayout.GRAVITY_FILL);
            tabs!!.setupWithViewPager(container)


            tabs!!.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
                override fun onTabReselected(p0: TabLayout.Tab?) {

                }
                override fun onTabUnselected(p0: TabLayout.Tab?) {

                }
                override fun onTabSelected(p0: TabLayout.Tab?) {
                    if (p0!!.text!!.equals("DOCS")){
                        (activity!! as KittyChooserMain).Iv_selectAllDoc.visibility = View.VISIBLE
                        (activity!! as KittyChooserMain).isDocFragment = true
                    } else{
                        (activity!! as KittyChooserMain).Iv_selectAllDoc.visibility = View.GONE
                        (activity!! as KittyChooserMain).isDocFragment = false
                    }
                }
            })

        } catch (e: Exception) {
            Toast.makeText(context, "Open Gallery Init Exp\n" + e.message, Toast.LENGTH_SHORT).show()
        }

    }




}

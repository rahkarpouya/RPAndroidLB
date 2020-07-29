package ir.rahkarpouya.rpandroidlib.bottomNavigation

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(fm: FragmentManager, private val mFragments: MutableList<Fragment>) :
    FragmentPagerAdapter(
        fm,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

    private var currentFragment: Fragment? = null

    override fun getItem(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getCount(): Int {
        return mFragments.size
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        if (currentFragment !== `object`) {
            currentFragment = `object` as Fragment
        }
        super.setPrimaryItem(container, position, `object`)
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }
}

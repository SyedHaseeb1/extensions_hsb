package com.hsb.extensions_hsb.tabViewPager

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Developed by Syed Haseeb
 * gtihub.com/syedhaseeb1
 * @2024
 */
object TabViewPager {
    fun TabLayout.setupWithViewPager(
        viewPager2: ViewPager2,
        activity: AppCompatActivity,
        fragments: List<TabFragment>,
        smoothScroll: Boolean = true,
        callback: ((visibleFragment: Fragment) -> Unit)? = null
    ) {
        try {
            viewPager2.offscreenPageLimit = fragments.size

            val genericPagerAdapter = GenericPagerAdapter(
                activity.supportFragmentManager,
                activity.lifecycle,
                fragments,
                viewPager2,
                this
            )
            genericPagerAdapter.initialize(smoothScroll)
            genericPagerAdapter.getFragment.observe(activity) { visibleFragment ->
                callback?.invoke(visibleFragment)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inline fun <R> safeLetNew(vararg nullableVars: Any?, block: (List<Any>) -> R): R? {
        return if (nullableVars.all { it != null }) {
            block(nullableVars.filterNotNull())
        } else {
            null
        }
    }

    inline fun <T1 : Any, T2 : Any, R : Any> doubleSafeLet(
        p1: T1?,
        p2: T2?,
        block: (T1, T2) -> R?
    ): R? {
        return if (p1 != null && p2 != null) block(p1, p2) else null
    }
}

data class TabFragment(val name: String, val icon: Int? = null, val fragment: Fragment)
private class GenericPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val fragments: List<TabFragment>,
    private val viewPager: ViewPager2,
    private val tabLayout: TabLayout
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    var getFragment = MutableLiveData<Fragment>()
    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        getFragment.postValue(fragments[position].fragment)
        return fragments[position].fragment

    }
    fun initialize(smoothScroll: Boolean) {
        viewPager.adapter = this

        TabLayoutMediator(tabLayout, viewPager, true, smoothScroll) { tab, position ->
            tab.text = fragments[position].name
            tab.setIcon(fragments[position].icon?.let {
                ContextCompat.getDrawable(
                    viewPager.context,
                    it
                )
            })
        }.attach()

    }

}


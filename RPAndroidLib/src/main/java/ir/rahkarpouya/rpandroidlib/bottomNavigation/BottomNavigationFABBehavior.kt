package ir.rahkarpouya.rpandroidlib.bottomNavigation

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import ir.rahkarpouya.rpandroidlib.bottomNavigation.BottomNavigation

class BottomNavigationFABBehavior(navigationBarHeight: Int) :
    CoordinatorLayout.Behavior<FloatingActionButton?>() {
    private var navigationBarHeight = 0
    private var lastSnackbarUpdate: Long = 0
    fun layoutDependsOn(
        parent: CoordinatorLayout?,
        child: FloatingActionButton?,
        dependency: View?
    ): Boolean {
        if (dependency != null && dependency is Snackbar.SnackbarLayout) {
            return true
        } else if (dependency != null && dependency is BottomNavigation) {
            return true
        }
        return super.layoutDependsOn(parent!!, child!!, dependency!!)
    }

    fun onDependentViewChanged(
        parent: CoordinatorLayout?,
        child: FloatingActionButton?,
        dependency: View?
    ): Boolean {
        updateFloatingActionButton(child, dependency)
        return super.onDependentViewChanged(parent!!, child!!, dependency!!)
    }

    /**
     * Update floating action button bottom margin
     */
    private fun updateFloatingActionButton(
        child: FloatingActionButton?,
        dependency: View?
    ) {
        if (child != null && dependency != null && dependency is Snackbar.SnackbarLayout) {
            lastSnackbarUpdate = System.currentTimeMillis()
            val p = child.layoutParams as MarginLayoutParams
            val fabDefaultBottomMargin = p.bottomMargin
            child.y = dependency.y - fabDefaultBottomMargin
        } else if (child != null && dependency != null && dependency is BottomNavigation) {
            // Hack to avoid moving the FAB when the AHBottomNavigation is moving (showing or hiding animation)
            if (System.currentTimeMillis() - lastSnackbarUpdate < 30) {
                return
            }
            val p = child.layoutParams as MarginLayoutParams
            val fabDefaultBottomMargin = p.bottomMargin
            child.y = dependency.getY() - fabDefaultBottomMargin
        }
    }

    init {
        this.navigationBarHeight = navigationBarHeight
    }
}
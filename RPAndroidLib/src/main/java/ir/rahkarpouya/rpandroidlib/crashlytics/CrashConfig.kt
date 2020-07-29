package ir.rahkarpouya.rpandroidlib.crashlytics

import androidx.annotation.IntDef
import androidx.annotation.Nullable
import java.io.Serializable

class CrashConfig : Serializable {

    @IntDef(BACKGROUND_MODE_CRASH, BACKGROUND_MODE_SHOW_CUSTOM, BACKGROUND_MODE_SILENT)

    @Retention(AnnotationRetention.SOURCE)
    private annotation class BackgroundMode {
        //I hate empty blocks
    }

    private var backgroundMode = BACKGROUND_MODE_SHOW_CUSTOM
    private var enabled = true
    private var showRestartButton = true
    private var logErrorOnRestart = true
    private var trackActivities = true
    private var minTimeBetweenCrashesMs = 3000
    private var errorActivityClass: Class<*>? = null
    private var restartActivityClass: Class<*>? = null
    private var eventListener: CustomActivityOnCrash.EventListener? = null

    @BackgroundMode
    fun getBackgroundMode(): Int {
        return backgroundMode
    }

    fun isEnabled(): Boolean {
        return enabled
    }


    fun isShowRestartButton(): Boolean {
        return showRestartButton
    }


    fun isLogErrorOnRestart(): Boolean {
        return logErrorOnRestart
    }

    fun isTrackActivities(): Boolean {
        return trackActivities
    }

    fun getMinTimeBetweenCrashesMs(): Int {
        return minTimeBetweenCrashesMs
    }

    @Nullable
    fun getErrorActivityClass(): Class<*>? {
        return errorActivityClass
    }

    @Nullable
    fun getRestartActivityClass(): Class<*>? {
        return restartActivityClass
    }

    fun setRestartActivityClass(@Nullable restartActivityClass: Class<*>?) {
        this.restartActivityClass = restartActivityClass
    }

    @Nullable
    fun getEventListener(): CustomActivityOnCrash.EventListener? {
        return eventListener
    }

    companion object{
        const val BACKGROUND_MODE_SILENT: Int = 0
        const val BACKGROUND_MODE_SHOW_CUSTOM: Int = 1
        const val BACKGROUND_MODE_CRASH = 2
    }
}
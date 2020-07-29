package ir.rahkarpouya.rpandroidlib

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Build
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import ir.rahkarpouya.rpandroidlib.dialog.MessageDialog
import ir.rahkarpouya.rpandroidlib.dialog.QuestionDialog
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.text.DecimalFormat

class RPA {

    companion object {

        var userId = 0

        /*----------------------------- add font from app ----------------------------------------*/
        private var fontPath__RPA: String = ""

        fun replaceFont(context: Context, newTypeface: String) {
            fontPath__RPA = newTypeface
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val newMap = HashMap<String, Typeface>()
                newMap["sans-serif"] = Typeface.createFromAsset(context.assets, fontPath__RPA)
                newMap["sans-serif-smallcaps"] =
                    Typeface.createFromAsset(context.assets, fontPath__RPA)
                newMap["sans-serif-medium"] =
                    Typeface.createFromAsset(context.assets, fontPath__RPA)
                newMap["monospace"] = Typeface.createFromAsset(context.assets, fontPath__RPA)
                newMap["sans-serif-light"] = Typeface.createFromAsset(context.assets, fontPath__RPA)
                newMap["sans-serif-black"] = Typeface.createFromAsset(context.assets, fontPath__RPA)
                newMap["serif-monospace"] = Typeface.createFromAsset(context.assets, fontPath__RPA)
                newMap["sans-serif-condensed"] =
                    Typeface.createFromAsset(context.assets, fontPath__RPA)
                newMap["sans-serif-condensed-light"] =
                    Typeface.createFromAsset(context.assets, fontPath__RPA)
                newMap["sans-serif-condensed-medium"] =
                    Typeface.createFromAsset(context.assets, fontPath__RPA)
                newMap["sans-serif-thin"] = Typeface.createFromAsset(context.assets, fontPath__RPA)
                try {
                    val staticField = Typeface::class.java.getDeclaredField("sSystemFontMap")
                    staticField.isAccessible = true
                    staticField.set(null, newMap)
                } catch (e: NoSuchFieldException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            } else {
                try {
                    val staticField = Typeface::class.java.getDeclaredField(fontPath__RPA)
                    staticField.isAccessible = true
                    staticField.set(null, Typeface.createFromAsset(context.assets, fontPath__RPA))
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

    }

    /*---------------------------------- message -------------------------------------------------*/
    object Message {
        const val Success: Int = 1
        const val Info: Int = 2
        const val Error: Int = 3
        const val Stop: Int = 4

        /*--------------------------- message Dialog ---------------------------------------------*/
        fun showMessageDialog(
            mContext: Context,
            title: String,
            text_btn: String = "تأیید",
            message_type: Int,
            listener: View.OnClickListener? = null
        ) {
            val dialog = MessageDialog(mContext, title, text_btn, message_type, listener)
            dialog.show()
        }

        fun showMessageDialog(
            mContext: Context,
            title: String,
            message_type: Int,
            listener: View.OnClickListener? = null
        ) {
            val dialog = MessageDialog(mContext, title, null, message_type, listener)
            dialog.show()
        }


        /*-------------------------- question Dialog ---------------------------------------------*/
        fun showQuestionDialog(
            context: Context,
            desc: String,
            txt_ok: String = "تأیید",
            txt_no: String = "انصراف",
            message_type: Int = 0,
            listener_ok: View.OnClickListener? = null,
            listener_no: View.OnClickListener? = null
        ) {
            val dialog =
                QuestionDialog(
                    context,
                    desc,
                    txt_ok,
                    txt_no,
                    message_type,
                    listener_ok,
                    listener_no
                )
            dialog.show()
        }

        fun showQuestionDialog(
            context: Context,
            desc: String,
            listener_ok: View.OnClickListener
        ) {
            val dialog = QuestionDialog(context, desc, "تأیید", "انصراف", 0, listener_ok)
            dialog.show()
        }


        /*--------------------------- message SnackBar -------------------------------------------*/

        fun showMessage(
            activity: Activity,
            title: String = "خطا در ارتباط با شبکه",
            @ColorRes backgroundColorSnack: Int = R.color.colorPrimaryDark,
            @ColorRes textColorSnack: Int = android.R.color.white
        ) {
            val snackBar = Snackbar.make(activity.findViewById(android.R.id.content), title, 3000)

            val textSnackBar: TextView = snackBar.view.findViewById(R.id.snackbar_text)
            textSnackBar.typeface = Typeface.createFromAsset(activity.assets, fontPath__RPA)
            textSnackBar.setTextColor(ContextCompat.getColor(activity, textColorSnack))
            snackBar.setBackgroundTint(ContextCompat.getColor(activity, backgroundColorSnack))

            snackBar.show()
        }

        fun showMessage(
            view: View,
            title: String = "خطا در ارتباط با شبکه",
            @ColorRes backgroundColorSnack: Int = R.color.colorPrimaryDark,
            @ColorRes textColorSnack: Int = android.R.color.white
        ) {
            val snackBar = Snackbar.make(view, title, 3000)

            val textSnackBar: TextView = snackBar.view.findViewById(R.id.snackbar_text)
            textSnackBar.setTextColor(ContextCompat.getColor(view.context, textColorSnack))
            snackBar.setBackgroundTint(ContextCompat.getColor(view.context, backgroundColorSnack))

            snackBar.show()
        }

        fun showMessage(
            activity: Activity,
            title: String,
            btnTitle: String,
            onClickListener: View.OnClickListener,
            duration: Int = Snackbar.LENGTH_INDEFINITE,
            @ColorRes backgroundColorSnack: Int = R.color.colorPrimaryDark,
            @ColorRes textColorSnack: Int = android.R.color.white,
            @ColorRes textColorActionSnack: Int = R.color.colorPrimaryDark
        ) {
            val snackBar = Snackbar.make(
                activity.findViewById(android.R.id.content), title, duration
            ).setAction(btnTitle, onClickListener)

            val textSnackBar: TextView = snackBar.view.findViewById(R.id.snackbar_text)
            val textSnackBarAction: TextView = snackBar.view.findViewById(R.id.snackbar_action)
            textSnackBar.typeface = Typeface.createFromAsset(activity.assets, fontPath__RPA)
            textSnackBarAction.setTextColor(ContextCompat.getColor(activity, textColorActionSnack))
            textSnackBarAction.setBackgroundColor(ContextCompat.getColor(activity, textColorSnack))
            textSnackBarAction.setTypeface(
                Typeface.createFromAsset(activity.assets, fontPath__RPA),
                Typeface.BOLD
            )
            textSnackBar.setTextColor(ContextCompat.getColor(activity, textColorSnack))
            snackBar.setBackgroundTint(ContextCompat.getColor(activity, backgroundColorSnack))

            snackBar.show()
        }

        fun showMessage(
            view: View,
            title: String,
            btnTitle: String,
            onClickListener: View.OnClickListener,
            duration: Int = Snackbar.LENGTH_INDEFINITE,
            @ColorRes backgroundColorSnack: Int = R.color.colorPrimaryDark,
            @ColorRes textColorSnack: Int = android.R.color.white,
            @ColorRes textColorActionSnack: Int = R.color.colorPrimaryDark
        ) {
            val snackBar = Snackbar.make(view, title, duration).setAction(btnTitle, onClickListener)

            val textSnackBar: TextView = snackBar.view.findViewById(R.id.snackbar_text)
            val textSnackBarAction: TextView = snackBar.view.findViewById(R.id.snackbar_action)
            textSnackBar.typeface =
                Typeface.createFromAsset(view.context.assets, fontPath__RPA)
            textSnackBarAction.setTypeface(
                Typeface.createFromAsset(
                    view.context.assets,
                    fontPath__RPA
                ), Typeface.BOLD
            )
            textSnackBarAction.setTextColor(
                ContextCompat.getColor(
                    view.context,
                    textColorActionSnack
                )
            )
            textSnackBarAction.setBackgroundColor(
                ContextCompat.getColor(
                    view.context,
                    textColorSnack
                )
            )
            textSnackBar.setTextColor(ContextCompat.getColor(view.context, textColorSnack))
            snackBar.setBackgroundTint(ContextCompat.getColor(view.context, backgroundColorSnack))

            snackBar.show()
        }


        /*--------------------------- message Toast -------------------------------------------*/
        fun messageToast(
            context: Context, title: String,
            duration: Int = Toast.LENGTH_SHORT,
            recourseBackground: Int = R.color.colorPrimary,
            @ColorRes colorText: Int = R.color.colorTextLight,
            padding: Int = 8,
            gravity: Int = Gravity.BOTTOM,
            xOffset: Int = 0,
            yOffset: Int = 0
        ) {
            val toast = Toast.makeText(context, title, duration)
            toast.setGravity(gravity, xOffset, yOffset)
            val myToast = toast.view.findViewById(android.R.id.message) as TextView
            if (fontPath__RPA != "")
                myToast.typeface = Typeface.createFromAsset(context.assets, fontPath__RPA)
            myToast.setTextColor(ContextCompat.getColor(context, colorText))
            myToast.setPadding(padding, padding, padding, padding)
            myToast.textSize = 14f

            val view = toast.view
            view.setBackgroundResource(recourseBackground)
            toast.show()
        }
    }

    /*------------------------------ END MESSAGE -------------------------------------------------*/


    /*-------------------------- option Text and EditText ----------------------------------------*/
    object Text {

        fun copyTextToClipboard(context: Context, information: String): Boolean {
            if (information.isEmpty())
                return false

            return try {
                val clipboard =
                    context.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Error information", information)
                clipboard.setPrimaryClip(clip)
                true
            } catch (e: java.lang.Exception) {
                Log.i("Error_Clipboard", e.message!!)
                false
            }
        }

        fun hideKeyboard(activity: Activity) {
            val handler = Handler()
            handler.postDelayed({
                val view = activity.findViewById<View>(android.R.id.content)
                if (view != null) {
                    val imm =
                        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }, 300)
        }

        fun showKeyboard(activity: Activity) {
            val inputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        fun addSeparatorTextWatcher(editText: EditText) {
            editText.addTextChangedListener(RPTextWatcherForThousand(editText))
        }

        fun replaceSeparatorTextWatcher(text: String) {
            RPTextWatcherForThousand.trimCommaOfString(text.trim())
        }

        fun addSeparatorPrice(
            dataText: Any,
            showRial: Boolean = false,
            rial: String = " تومان"
        ): String =
            if (showRial) DecimalFormat("#,###.##").format(dataText) + rial
            else DecimalFormat("#,###.##").format(dataText)

    }

    /*-------------------------------- END TEXT --------------------------------------------------*/


    /*----------------------------- image and gallery --------------------------------------------*/

    object ImageFile {
        fun convertImageToBase64(filePath: String?): String {
            if (filePath == "") return ""
            val bm = BitmapFactory.decodeFile(filePath)
            var compress = 75
            val size = bm.byteCount
            //if size more than 1MB use more compress
            if (size.toFloat() / 10000000 > 1.0) compress = 50
            if (size.toFloat() / 10000000 > 2.0) compress = 40
            if (size.toFloat() / 10000000 > 4.0) compress = 10
            val bos = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, compress, bos)
            val bytesArray = bos.toByteArray()
            return Base64.encodeToString(bytesArray, Base64.DEFAULT)
        }

        fun convertFileToBase64(selectedPath: String?): String {
            val audioBytes: ByteArray
            val bos = ByteArrayOutputStream()
            val fis = FileInputStream(File(selectedPath!!))
            val buf = ByteArray(1024)
            val n: Int = fis.read(buf)
            while (-1 != n)
                bos.write(buf, 0, n)
            audioBytes = bos.toByteArray()
            return Base64.encodeToString(audioBytes, Base64.DEFAULT)
        }

    }
    /*-------------------------- END IMAGE AND GALLERY -------------------------------------------*/


    /*---------------------------- Check Rooted Device -------------------------------------------*/
    fun isRootedDevice(): Boolean {
        val buildTags = Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true
        }

        // check device is emulator or mobile
        if (isProbablyEm())
            return true

        // check if /system/app/Superuser.apk is present
        try {
            val file = File("/system/app/Superuser.apk")
            if (file.exists()) {
                return true
            }
        } catch (e1: java.lang.Exception) {
        }
        // try executing commands
        return (canExecuteCommand("/system/xbin/which su")
                || canExecuteCommand("/system/bin/which su") || canExecuteCommand("which su"))
    }

    // executes a command on the system
    private fun canExecuteCommand(command: String): Boolean {
        return try {
            Runtime.getRuntime().exec(command)
            true
        } catch (e: java.lang.Exception) {
            false
        }
    }

    private fun isProbablyEm(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.BOARD === "QC_Reference_Phone" //bluestacks
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.HOST.startsWith("Build") //MSI App Player
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || "google_sdk" === Build.PRODUCT)
    }
    /*---------------------------- Check Rooted Device -------------------------------------------*/

}

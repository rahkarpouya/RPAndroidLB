package ir.rahkarpouya.rpandroidlib.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import ir.rahkarpouya.rpandroidlib.RPA.Message.Error
import ir.rahkarpouya.rpandroidlib.RPA.Message.Info
import ir.rahkarpouya.rpandroidlib.RPA.Message.Stop
import ir.rahkarpouya.rpandroidlib.RPA.Message.Success
import ir.rahkarpouya.rpandroidlib.R

class MessageDialog(
    private var mContext: Context,
    private val title: String,
    private val text_btn: String? = null,
    private val message_type: Int,
    private val listener: View.OnClickListener? = null
) : Dialog(mContext) {

    private lateinit var dmIvTitle: ImageView
    private lateinit var dmTxtTitle: TextView
    private lateinit var dmCardBtn: CardView
    private lateinit var dmTxtOk: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_message)
        setCancelable(false)
//        window!!.attributes.windowAnimations = R.style.PauseDialog
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.attributes.width = ViewGroup.LayoutParams.MATCH_PARENT
        initView()

        dmTxtTitle.text = title

        if (dmTxtTitle.text.toString().trim { it <= ' ' }.isEmpty())
            dmTxtTitle.visibility = View.GONE
        else
            dmTxtTitle.visibility = View.VISIBLE

        dmTxtOk.text = text_btn ?: "تأیید"
        dmTxtOk.setOnClickListener {
            listener?.onClick(dmTxtOk)
            dismiss()
        }
        when (message_type) {
            Success -> {
                dmIvTitle.setImageResource(R.drawable.message_ok)
                dmIvTitle.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorOk))
                dmCardBtn.setCardBackgroundColor(ContextCompat.getColor(mContext,R.color.colorOk))
            }
            Info -> {
                dmIvTitle.setImageResource(R.drawable.message_info)
                dmIvTitle.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorInfo))
                dmCardBtn.setCardBackgroundColor(ContextCompat.getColor(mContext,R.color.colorInfo))
            }
            Error -> {
                dmIvTitle.setImageResource(R.drawable.message_error)
                dmIvTitle.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorError))
                dmCardBtn.setCardBackgroundColor(ContextCompat.getColor(mContext,R.color.colorError))
            }
            Stop -> {
                dmIvTitle.setImageResource(R.drawable.message_stop)
                dmIvTitle.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorStop))
                dmCardBtn.setCardBackgroundColor(ContextCompat.getColor(mContext,R.color.colorStop))
            }
            else -> {
                dmIvTitle.setImageResource(R.drawable.message_stop)
                dmIvTitle.setBackgroundColor(ContextCompat.getColor(mContext,message_type))
                dmCardBtn.setCardBackgroundColor(ContextCompat.getColor(mContext,message_type))
            }
        }
    }

    private fun initView() {
        dmIvTitle = findViewById(R.id.dm_iv_title)
        dmTxtTitle = findViewById(R.id.dm_txt_title)
        dmCardBtn = findViewById(R.id.dm_card_btn)
        dmTxtOk = findViewById(R.id.dm_txt_ok)
    }

    override fun onBackPressed() {
        if (message_type != 3)
            if (isShowing)
                dismiss()
            else
                super.onBackPressed()
    }
}

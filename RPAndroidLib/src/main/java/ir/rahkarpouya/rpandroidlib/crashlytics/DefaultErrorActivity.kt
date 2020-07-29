package ir.rahkarpouya.rpandroidlib.crashlytics

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import ir.rahkarpouya.rpandroidlib.ApiCall
import ir.rahkarpouya.rpandroidlib.R
import ir.rahkarpouya.rpandroidlib.RPA
import org.ksoap2.serialization.SoapObject

class DefaultErrorActivity : AppCompatActivity() {

    private lateinit var linearProgress: LinearLayoutCompat
    private lateinit var restartButton: AppCompatButton
    private lateinit var closeButton: AppCompatButton
    private lateinit var moreInfoButton: AppCompatButton


    @SuppressLint("PrivateResource")
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val a = obtainStyledAttributes(R.styleable.AppCompatTheme)
        if (!a.hasValue(R.styleable.AppCompatTheme_windowActionBar)) {
            setTheme(R.style.Theme_AppCompat_Light_DarkActionBar)
        }
        a.recycle()
        setContentView(R.layout.activity_default_error)
        linearProgress = findViewById(R.id.linear_progress)
        val config =
            CustomActivityOnCrash.getConfigFromIntent(
                intent
            )

        restartButton = findViewById(R.id.crash_restart_button)
        closeButton = findViewById(R.id.crash_close_button)
        moreInfoButton = findViewById(R.id.crash_more_info_button)

        restartButton.setOnClickListener {
            CustomActivityOnCrash.restartApplication(this@DefaultErrorActivity, config)
        }

        closeButton.setOnClickListener {
            CustomActivityOnCrash.closeApplication(this@DefaultErrorActivity, config)
        }

        moreInfoButton.setOnClickListener {
            setCrashReport(
                SetCrashReport(
                    CustomActivityOnCrash.getAllErrorDetailsFromIntent(
                        this@DefaultErrorActivity,
                        intent
                    )
                )
            )
        }

        Handler().postDelayed({
            setCrashReport(
                SetCrashReport(
                    CustomActivityOnCrash.getAllErrorDetailsFromIntent(
                        this@DefaultErrorActivity,
                        intent
                    )
                )
            )
        }, 300)

    }

    fun setCrashReport(setCrashReport: SetCrashReport) {
        val hashMap = HashMap<String, Any>()
        hashMap["BuildVersion"] = setCrashReport.BuildVersion
        hashMap["Device"] = setCrashReport.Device
        hashMap["StackTrace"] = setCrashReport.StackTrace
        hashMap["UserAction"] = setCrashReport.UserAction
        hashMap["UserID"] = setCrashReport.UserID
        ApiCall.CallSoap<GetCrashReport>()
            .async(
                "SetCrashReport",
                GetCrashReport(), hashMap
            )
            .call(object : ApiCall.CallWebService<GetCrashReport> {
                override fun connection(model: GetCrashReport, response: SoapObject) {
                    model.ResultCode = response.getPropertyAsString("ResultCode").toInt()
                    model.ResultDescription = response.getPropertyAsString("ResultDescription")
                }

                override fun noConnection(result: String) {
                    linearProgress.visibility = View.GONE
                    moreInfoButton.visibility = View.GONE
                    RPA.Message.showMessage(
                        this@DefaultErrorActivity,
                        result,
                        "سعی مجدد",
                        View.OnClickListener {
                            setCrashReport(setCrashReport)
                        })
                }

                override fun failure(e: Throwable) {
                    RPA.Message.showMessage(this@DefaultErrorActivity, e.message!!)
                }

                override fun complete(model: GetCrashReport) {
                    if (model.ResultCode != 0)
                        RPA.Message.showMessage(
                            this@DefaultErrorActivity,
                            model.ResultDescription
                        )
                    linearProgress.visibility = View.GONE
                    moreInfoButton.visibility = View.GONE

                }
            })
    }

}

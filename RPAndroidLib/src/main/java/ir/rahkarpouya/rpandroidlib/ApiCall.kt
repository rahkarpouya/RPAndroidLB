package ir.rahkarpouya.rpandroidlib

import android.util.Log
import kotlinx.coroutines.*
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.PropertyInfo
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import java.lang.Runnable

class ApiCall(soap_address: String, target_name: String) {

    companion object {
        private var SOAP_ADDRESS = ""
        private var WSDL_TARGET_NAMESPACE = ""
    }

    init {
        SOAP_ADDRESS = soap_address
        WSDL_TARGET_NAMESPACE = target_name
    }

    class CallSoap<T> {
        private var response: SoapObject? = null
        private var callWebService: CallWebService<T>? = null

        private val parentJob = Job()
        private val coroutineExceptionHandler: CoroutineExceptionHandler =
            CoroutineExceptionHandler { _, throwable ->
                GlobalScope.launch {
                    callWebService!!.failure(throwable)
                    Log.i("Response_Error", throwable.message!!)
                    coroutineScope.cancel()
                }
            }
        private val coroutineScope =
            CoroutineScope(Dispatchers.Main + parentJob + coroutineExceptionHandler)

        fun async(
            funcName: String,
            model: T,
            any: HashMap<String, Any>? = null
        ): CallSoap<T> {
            coroutineScope.launch(Dispatchers.Main) {
                if (doAsync(funcName, model, any)) {
                    callWebService!!.complete(model)
                }
//                coroutineScope.cancel()
            }
            return this
        }

        private suspend fun doAsync(
            funcName: String,
            model: T,
            any: HashMap<String, Any>? = null
        ): Boolean =
            withContext(Dispatchers.IO) {
                val request = SoapObject(WSDL_TARGET_NAMESPACE, funcName)

                if (any != null) {
                    for (an in any) {
                        if (an.value is MutableList<*>) {
                            val list: MutableList<*> = an.value as MutableList<*>
                            val soapCompanies = SoapObject(WSDL_TARGET_NAMESPACE, an.key)
                            for (i in 0 until list.size) {
                                soapCompanies.addProperty("string", i)
                            }
                            request.addSoapObject(soapCompanies)
                            Log.i("Request_list", "$funcName - ${an.value}")
                        } else {
                            val pi = PropertyInfo()
                            pi.setName(an.key)
                            pi.value = an.value
                            if (an.value is String)
                                pi.setType(String::class.java)
                            if (an.value is Boolean)
                                pi.setType(Boolean::class.java)
                            if (an.value is Int)
                                pi.setType(Int::class.java)
                            request.addProperty(pi)
                        }
                    }
                    Log.i("Request", "$funcName - $any")
                }

                //endregion

                val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
                envelope.dotNet = true

                envelope.setOutputSoapObject(request)

                val httpTransport = HttpTransportSE(SOAP_ADDRESS)

                val myThread = Thread(Runnable {
                    try {
                        httpTransport.call(WSDL_TARGET_NAMESPACE + funcName, envelope)
                        response = envelope.response as SoapObject
                        Log.i("Response_ok", funcName + " - " + response.toString())
                    } catch (exception: Exception) {

                        if (exception.message != null) {
                            Log.i("Response_no", funcName + " - " + exception.message)
                            if (exception.message!!.contains("Unable to resolve host")) {
                                val error = "عدم اتصال به اینترنت"
                                callWebService!!.noConnection(error)
                            } else {
                                val error = "خطا در برقراری ارتباط با سرور"
                                callWebService!!.noConnection(error)
                            }
                        }

                    }
                })
                myThread.start()
                //wait
                try {
                    myThread.join()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                if (response != null) {
                    callWebService!!.connection(model, response!!)
                    Log.i("Response_Data", funcName + " - " + model.toString())
                    return@withContext true
                }
                return@withContext false
            }

        fun call(callWebService: CallWebService<T>) {
            this.callWebService = callWebService
        }

    }

    interface CallWebService<T> {
        fun connection(model: T, response: SoapObject)
        fun noConnection(result: String)
        fun failure(e: Throwable)
        fun complete(model: T)
    }

}
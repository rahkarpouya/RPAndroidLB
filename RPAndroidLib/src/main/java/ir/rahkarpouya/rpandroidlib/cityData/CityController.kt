package ir.rahkarpouya.rpandroidlib.cityData

import android.content.Context
import android.util.Log
import ir.rahkarpouya.rpandroidlib.cityData.DBOpenHelper.Companion.city_Id
import ir.rahkarpouya.rpandroidlib.cityData.DBOpenHelper.Companion.city_PId
import ir.rahkarpouya.rpandroidlib.cityData.DBOpenHelper.Companion.city_Title
import kotlinx.coroutines.*
import java.util.*

class CityController(private var context: Context) {

    interface CreateCityTable{
        fun create()
        fun complete()
    }

    private val parentJob = Job()
    private val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            GlobalScope.launch {
                Log.i("CityDB_Error", throwable.message!!)
                coroutineScope.cancel()
            }
        }
    private val coroutineScope =
        CoroutineScope(Dispatchers.Main + parentJob + coroutineExceptionHandler)

    private suspend fun selectCity(): Boolean =
        withContext(Dispatchers.IO) {
            val objCity = DBOpenHelper(context)
            val lstCity = objCity.findAll()
            if (lstCity.isNotEmpty()) {
                parentJob.complete()
                return@withContext true
            }
            return@withContext parseCity(objCity)
        }

    private suspend fun parseCity(objCity: DBOpenHelper): Boolean =
        withContext(Dispatchers.Default) {
            val xmlParserCity = CityParser(context)
            val lst = xmlParserCity.getDataList()
            for (item in lst)
                objCity.add(City(item.ID, item.PID, item.Title))
            return@withContext true
        }

    fun createCityTable(listener : CreateCityTable) {
        listener.create()
        coroutineScope.launch(Dispatchers.Main) {
            selectCity()
            listener.complete()
        }
    }

    fun getCitesOfState(stateTitle: String): MutableList<String> {
        val objCity = DBOpenHelper(context)
        val lst = objCity.findFiltered("PID = 14", "")
        val lstCityTitle = ArrayList<String>()
        lstCityTitle.add(0, "همه شهرها")
        var selectedStateID = -1
        for (state in lst) {//find Selected State ID
            if (state.Title == stateTitle) {
                selectedStateID = state.ID
                break
            }
        }

        return getCitesOfStateById(selectedStateID)
    }

    fun getCitesOfStateById(stateId: Int = 14): MutableList<String> {
        val objCity = DBOpenHelper(context)
        val lst = objCity.findFiltered("$city_PId = $stateId", city_Title)
        val lstTitle = ArrayList<String>()
        if (stateId == 14)
            lstTitle.add(0, "همه استان")
        else
            lstTitle.add(0, "همه شهرها")

        for (item in lst)
            lstTitle.add(item.Title)

        return lstTitle
    }

    fun getCityId(title: String, isState: Boolean): Int {
        val objCity = DBOpenHelper(context)
        val query: String = if (isState)
            "$city_Title = '$title' AND $city_PId = '14'"
        else
            "$city_Title = '$title' AND $city_PId <> '14'"
        val lst = objCity.findFiltered(query, "")
        return if (lst.isNotEmpty()) lst[0].ID else 0

    }

    fun getPositionOfCityId(cityId: Int): Int {
        val objCity = DBOpenHelper(context)
        var query: String
        query = "$city_Id = $cityId"
        val lstCity = objCity.findFiltered(query, "")
        if (lstCity.isNotEmpty()) {
            query = city_PId + " = " + lstCity[0].PID
            val lstCities = objCity.findFiltered(query, city_Title)
            for (i in 0 until lstCities.size + 1) {
                if (lstCities[i].ID == cityId)
                    return i + 1
            }
        }
        return 0
    }

    fun getCityTitle(cityId: Int): String {
        val objCity = DBOpenHelper(context)
        val query = "$city_Id = '$cityId'"
        val lst = objCity.findFiltered(query, "")
        return if (lst.isNotEmpty()) lst[0].Title else ""

    }

}

package ir.rahkarpouya.rpandroidlib.cityData

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBOpenHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(tbl_city)
        Log.i(logTag, "Database Created")
    }

    override fun onUpgrade(db: SQLiteDatabase, i: Int, i1: Int) {
        db.execSQL("DROP TABLE IF EXISTS $tblCity")
        onCreate(db)

        Log.i(logTag, "Database Updated")
    }

    private fun addToList(cursor: Cursor): MutableList<City> {
        val lst: MutableList<City> = mutableListOf()
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                lst.add(
                    City(
                        cursor.getInt(cursor.getColumnIndex(city_Id)),
                        cursor.getInt(cursor.getColumnIndex(city_PId)),
                        cursor.getString(cursor.getColumnIndex(city_Title))
                    )
                )
            }
        }
        return lst
    }

    fun findAll(): MutableList<City> {
        val database = readableDatabase
        val query = "SELECT * FROM $tblCity"
        val cursor = database.rawQuery(query, null)
        val lst = addToList(cursor)
        cursor.close()
        return lst
    }

    fun findFiltered(selection: String, orderBy: String): MutableList<City> {
        val database = readableDatabase
        val cursor = database.query(
            tblCity, cityColumn,
            selection, null, null, null, orderBy
        )

        val lst = addToList(cursor)
        cursor.close()
        return lst
    }

    fun add(_city: City) {
        val database = writableDatabase
        val values = ContentValues()
        values.put(city_Id, _city.ID)
        values.put(city_PId, _city.PID)
        values.put(city_Title, _city.Title)

        database.insert(tblCity, null, values)
    }

    fun update(item: City): Int {
        val database = writableDatabase
        val values = ContentValues()
        values.put(city_Id, item.ID)
        values.put(city_PId, item.PID)
        values.put(city_Title, item.Title)

        val criteria = city_Id + "=" + item.ID
        return database.update(tblCity, values, criteria, null)
    }

    companion object {
        private val logTag = DBOpenHelper::class.java.name
        private const val DATABASE_NAME = "CityDataBase"
        private const val DATABASE_VERSION = 1

        const val tblCity = "CITY"

        const val city_Id = "Id"
        const val city_PId = "pId"
        const val city_Title = "title"

        const val tbl_city = "CREATE TABLE " + tblCity + " (" +
                city_Id + " INTEGER PRIMARY KEY, " +
                city_PId + " INTEGER, " +
                city_Title + " NVARCHAR(200) " +
                ")"

        private val cityColumn = arrayOf(city_Id, city_PId, city_Title)
    }

}

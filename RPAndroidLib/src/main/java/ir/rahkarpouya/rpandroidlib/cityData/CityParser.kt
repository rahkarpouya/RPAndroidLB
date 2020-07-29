package ir.rahkarpouya.rpandroidlib.cityData

import android.content.Context
import ir.rahkarpouya.rpandroidlib.R
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.SAXException
import java.io.IOException
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

class CityParser(private val context: Context) {

    fun getDataList(): MutableList<City> {
        val empList: MutableList<City> = mutableListOf()
        try {
            val stream = context.resources.openRawResource(R.raw.city)
            val builderFactory = DocumentBuilderFactory.newInstance()
            val docBuilder = builderFactory.newDocumentBuilder()
            val doc = docBuilder.parse(stream)
            val nList = doc.getElementsByTagName(City_TAG)
            for (i in 0 until nList.length) {
                if (nList.item(0).nodeType == Node.ELEMENT_NODE) {
                    val element = nList.item(i) as Element
                    empList.add(
                        City(
                            getNodeValue(City_ID, element).toInt(),
                            getNodeValue(City_PID, element).toInt(),
                            getNodeValue(City_Title, element)
                        )
                    )
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
        } catch (e: SAXException) {
            e.printStackTrace()
        }
        return empList
    }

    private fun getNodeValue(tag: String, element: Element): String {
        val nodeList = element.getElementsByTagName(tag)
        val node = nodeList.item(0)
        if (node != null) {
            if (node.hasChildNodes()) {
                val child = node.firstChild
                while (child != null) {
                    if (child.nodeType == Node.TEXT_NODE) {
                        return child.nodeValue
                    }
                }
            }
        }
        return ""
    }

    companion object {
        private const val City_TAG = "city"
        private const val City_ID = "ID"
        private const val City_PID = "PID"
        private const val City_Title = "Title"
    }
}
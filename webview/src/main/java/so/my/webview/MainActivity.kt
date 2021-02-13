package so.my.webview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebViewClient
import androidx.annotation.RestrictTo
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var doc: org.jsoup.nodes.Document
    lateinit var currentCurrency : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CoroutineScope(Dispatchers.Main).launch{
            withContext(Dispatchers.IO) {
                getWeb()
            }
            textView.text = currentCurrency
        }
    }

    private fun getWeb(){
        try {
            doc = Jsoup.connect("https://minfin.com.ua/currency/").get()
            val currency = getInfo(doc,0)
            currentCurrency = splitParcedCurrency(currency)
            Log.i("MyLog", currency?.text().toString())
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun getInfo(doc: org.jsoup.nodes.Document, number : Int): org.jsoup.nodes.Element? {
        val tables = doc.getElementsByTag("tbody")
        val ourTable = tables.get(0)
        val ourTableElements =  ourTable.children()
        val chosenElement = ourTableElements.get(number)
        val currency = chosenElement.children().get(1)
        return currency
    }

    private fun splitParcedCurrency(currency : org.jsoup.nodes.Element?): String {
        val splitedCurrency = currency?.text()?.split(" ")
        val neededCurrency = splitedCurrency?.get(0)
        val result = neededCurrency?.replace(",",".",true)
        return result as String
    }


}

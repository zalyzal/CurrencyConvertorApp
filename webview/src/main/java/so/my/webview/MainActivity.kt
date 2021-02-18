package so.my.webview

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.doOnTextChanged
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {


    lateinit var doc: org.jsoup.nodes.Document
    var currentCurrency : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val date : Date = Date()
        textView3.text = SimpleDateFormat("d-MM, HH:mm", Locale.getDefault()).format(date.time)
        if(isOnline()){
            CoroutineScope(Dispatchers.IO).launch {
                downloadActualInfo()
            }
        }
        else{
            bottom_currency.setText("28")
        }
        convert()
    }


    private fun dateUpdate(){

    }

    private fun getWeb(){
        try {
            doc = Jsoup.connect("https://minfin.com.ua/currency/").get()
            val currency = getInfo(doc,0)
            val currencyInt = getIntValue(currency)
            currentCurrency = splitParcedCurrency(currencyInt).toString()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
    // парсинг даних
    private fun getInfo(doc: org.jsoup.nodes.Document, number : Int): org.jsoup.nodes.Element? {
        val tables = doc.getElementsByTag("tbody")
        val ourTable = tables.get(0)
        val ourTableElements =  ourTable.children()
        val chosenElement = ourTableElements.get(number)
        return chosenElement
    }

    private fun getIntValue(doc: org.jsoup.nodes.Element?): org.jsoup.nodes.Element? {
        val currency = doc?.children()?.get(2)
        return currency
    }


   /*З спарсених даних виймаємо числ значення   */
    private fun splitParcedCurrency(currency : org.jsoup.nodes.Element?): Double? {
        val splitedCurrency = currency?.text()?.split(" ")
        val neededInfo = splitedCurrency?.get(0)?.toDouble()
        return neededInfo?.toBigDecimal()?.setScale(2, RoundingMode.UP)?.toDouble()
    }

    fun changeCurrency(view: View) {
        first_currency_text.text = second_currency_text.text.also { second_currency_text.text = first_currency_text.text }
        first_currency_img.setImageBitmap(second_currency_img.drawable.toBitmap().also { second_currency_img.setImageBitmap(first_currency_img.drawable.toBitmap())})
        top_currency.text = down_currency.text.also { down_currency.text = top_currency.text }
    }


    fun isOnline(): Boolean {
        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }


    private suspend fun downloadActualInfo(){
        CoroutineScope(Dispatchers.IO).launch{
            getWeb()
            withContext(Dispatchers.Main) {
                bottom_currency.setText(currentCurrency)
            }
        }
    }

    private fun convert(){
        upper_currency.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //bottom_currency.setText((s.toString().toDouble()*bottom_currency.text.toString().toDouble()).toString())
                bottom_currency.setText(s.toString())
            }
        })
    }

}

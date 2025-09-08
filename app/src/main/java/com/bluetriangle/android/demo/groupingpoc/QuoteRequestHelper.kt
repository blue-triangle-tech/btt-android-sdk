package com.bluetriangle.android.demo.groupingpoc

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.okhttp.BlueTriangleOkHttpInterceptor
import com.bluetriangle.android.demo.R
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.resumeWithException

class QuoteRequestHelper {

    private val okHttpClient: OkHttpClient = createOkHttpClient()

    companion object {
        private var _instance: QuoteRequestHelper? = null

        val instance: QuoteRequestHelper
            get() {
                if (_instance == null) {
                    _instance = QuoteRequestHelper()
                }
                return _instance!!
            }
    }

    data class Quote(val quote: String, val author: String)

    fun setupQuoteUI(lifecycleScope: CoroutineScope, view: View) {
        val getQuoteButton = view.findViewById<Button>(R.id.get_quote_button)
        val quoteCard = view.findViewById<MaterialCardView>(R.id.quote_card)
        val quoteText = view.findViewById<TextView>(R.id.quote_text)
        val authorText = view.findViewById<TextView>(R.id.author_text)

        getQuoteButton.setOnClickListener {
            lifecycleScope.launch(IO) {
                val quote = getQuote()

                withContext(Main) {
                    quoteCard.visibility = View.VISIBLE
                    quoteText.text = quote.quote
                    authorText.text = quote.author
                }
            }
        }
    }

    suspend fun getQuote() = suspendCancellableCoroutine<Quote> { continuation ->
        val quoteRequest = getQuoteRequest()

        okHttpClient.newCall(quoteRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code != 200) {
                    continuation.resumeWith(Result.failure(IOException("Invalid response code: ${response.code}")))
                    return
                }

                val body = response.body
                if (body == null) {
                    continuation.resumeWith(Result.failure(IOException("Response body is null!")))
                    return
                }

                val quoteJson = JSONObject(body.string())
                continuation.resumeWith(
                    Result.success(
                        Quote(
                            quoteJson.getString("quote"),
                            quoteJson.getString("author")
                        )
                    )
                )
            }
        })
    }

    private fun getQuoteRequest(): Request {
        val randomNumber = (1 + Math.random() * 1000).toInt()
        val quoteUrl =
            "https://raw.githubusercontent.com/IsmailAloha/dev-quotes/main/quotes/quote${randomNumber}.json"
        return Request.Builder()
            .url(quoteUrl)
            .get()
            .build()
    }

    private fun createOkHttpClient() = OkHttpClient.Builder().apply {
        Tracker.instance?.let {
            addInterceptor(BlueTriangleOkHttpInterceptor(it.configuration))
        }
    }.build()
}
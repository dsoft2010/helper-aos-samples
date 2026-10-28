package kr.ds.helper.sample_web

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import kr.ds.helper.web.WebBridge
import kr.ds.helper.web.DefaultWebBridge

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    private val webBridge = DefaultWebBridge().apply {
        setHandler("show", this@MainActivity)
        setHandler("history", object : WebBridge.WebHandler {
            override fun back() {
                if (webView.canGoBack()) webView.goBack() else finish()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(webBridge, WebBridge.DEFAULT_WEB_BRIDGE_NAME)
        webView.loadUrl("https://example.com")
    }

    @Keep
    fun native(data: NativeData) {
        // args가 있는 호출 처리
        android.widget.Toast.makeText(this, data.type, android.widget.Toast.LENGTH_SHORT).show()
    }

    @Keep
    fun logout() {
        // args가 없는 호출 처리
    }

    data class NativeData(val type: String?)
}

package kr.ds.helper.sample_hybrid_activity

import android.app.Application
import android.os.Bundle
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import kr.ds.helper.hybrid_app_activity_base.BaseMainActivity
import kr.ds.helper.web.WebBridge
import kr.ds.helper.web.DefaultWebBridge

class SampleApplication : Application() {
    // AppConfiguration 구현은 실제 프로젝트에서 필요합니다.
    // 이 샘플은 최소한의 구조만 제공합니다.
}

class MainActivity : BaseMainActivity(application) {

    private val handlerWebBridge = DefaultWebBridge().apply {
        setHandler("show", this@MainActivity)
        setHandler("history", object : WebBridge.WebHandler {
            override fun back() {
                if (webView.canGoBack()) webView.goBack() else finish()
            }
        })
    }

    override val webBridge: WebBridge get() = handlerWebBridge
    override val webBridgeName: String get() = WebBridge.DEFAULT_WEB_BRIDGE_NAME
    override val webViewClient: android.webkit.WebViewClient get() = android.webkit.WebViewClient()
    override val webChromeClient: android.webkit.WebChromeClient get() = android.webkit.WebChromeClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @Keep
    fun native(data: NativeData) {
        when (data.type) {
            "setting" -> {
                // 설정 화면으로 이동
            }
        }
    }

    data class NativeData(val type: String?)
}

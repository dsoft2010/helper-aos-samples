# Web–Native 브리지 가이드

이 문서는 DS Helper Android 라이브러리의 웹-네이티브 브리지(`helper:web`)를 요약한 가이드입니다.  
원본 문서는 [`helper_aos/docs/web-bridge-guide.md`](https://github.com/dsoft2010/helper_aos/blob/develop/docs/web-bridge-guide.md)에서 확인할 수 있습니다.

> 이 저장소의 샘플 앱(`sample-web`, `sample-hybrid-activity`, `sample-hybrid-fragment`)에서 브리지를 사용할 때 참조하세요.

---

## 1. 개요

WebBridge는 Android WebView에서 JavaScript와 네이티브 코드 간의 양방향 통신을 제공하는 브리지 라이브러리입니다.  
웹 페이지에서 `window.AppInterface.postMessage()`로 전달한 JSON 메시지를 네이티브가 수신하여 **group / function / args** 단위로 디스패치합니다.

**의존성:** `kr.ds.helper:web:1.5.5` (GitHub Packages)

### 주요 클래스

| 클래스 | 설명 |
|--------|------|
| `WebBridge` | 추상 클래스. `@JavascriptInterface postMessage()` 진입점 제공 |
| `DefaultWebBridge` | 기본 구현체. `setHandler()`로 등록된 핸들러로 라우팅 |
| `WebMessage` | `{ group, function, args }` 구조의 data class |
| `WebHandler` | `back()` 메서드를 가진 인터페이스. `"history"` 그룹 처리용 |

---

## 2. 의존성 추가

`settings.gradle.kts`에 GitHub Packages 저장소를 등록합니다(상세: [`docs/integration-guide.md`](integration-guide.md)).

모듈의 `build.gradle.kts`에 의존성을 추가합니다.

```kotlin
implementation("kr.ds.helper:web:1.5.5")
```

---

## 3. JavaScript 연동

웹 페이지에서 아래 형식으로 메시지를 전송합니다.

```javascript
window.AppInterface.postMessage(
    JSON.stringify({
        group: "groupName",
        function: "functionName",
        args: { /* ... */ }
    })
);
```

### 헬퍼 함수 예시

```javascript
var dsmobileapp = {
    web2app: function (groupname, funcname, args) {
        var dict = {
            group: groupname,
            function: funcname,
            args: args
        };

        if (/* Android */) {
            window.AppInterface.postMessage(JSON.stringify(dict));
        } else if (/* iOS */) {
            webkit.messageHandlers.AppInterface.postMessage(dict);
        }
    }
};
```

### args 전달 형태

```javascript
// 객체
dsmobileapp.web2app("show", "native", { type: "setting" });

// 배열
dsmobileapp.web2app("logEvent", "addGoods", [{ goodsNm: "상품명", price: 50000 }]);

// null (무인자 메서드)
dsmobileapp.web2app("logEvent", "nullTest", null);

// 빈 객체 (무인자 메서드로 fallback)
dsmobileapp.web2app("logEvent", "emptyTest", {});
```

---

## 4. 네이티브 WebBridge 설정

### 4.1 DefaultWebBridge 생성 및 핸들러 등록

`DefaultWebBridge`를 생성하고 `setHandler(group, handler)`로 그룹별 핸들러 객체를 등록합니다.

```kotlin
val webBridge = DefaultWebBridge().apply {
    setHandler("show", this@MyActivity)        // Activity 자체를 핸들러로
    setHandler("login", loginHandler)           // 별도 객체를 핸들러로
}
```

**핸들러 메서드 규칙**

- 난독화 방지를 위해 `@Keep` 어노테이션을 붙입니다.
- `function` 이름과 동일한 메서드명을 사용합니다.
- `args`가 있으면 **단일 파라미터** 메서드, `args`가 null이면 **무인자** 메서드로 매핑됩니다.
- 파라미터 타입은 Gson으로 JSON을 역직렬화할 수 있는 모든 타입을 사용할 수 있습니다.

```kotlin
data class NativeData(val type: String?)

class MyActivity : AppCompatActivity() {

    @Keep
    fun native(data: NativeData) {
        when (data.type) {
            "setting" -> // 설정 화면으로 이동
        }
    }

    @Keep
    fun logout() {
        // args 없는 호출 처리
    }
}
```

### 4.2 WebView에 브릿지 연결

#### 직접 WebView를 사용하는 경우

```kotlin
// 방법 1: addJavascriptInterface (간단)
webView.addJavascriptInterface(webBridge, "AppInterface")

// 방법 2: 확장 함수 사용
webView.addWebViewBridge(webBridge)

// 방법 3: WebMessageListener (Android X, origin 필터링 지원)
if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_MESSAGE_LISTENER)) {
    WebViewCompat.addWebMessageListener(
        webView,
        "AppInterface",
        allowedOriginRules  // Set<String>, 예: setOf("https://example.com")
    ) { _, message, _, _, _ ->
        message.data?.let { webBridge.postMessage(it) }
    }
}
```

`WebViewCompat.addWebMessageListener` 방식은 `allowedOriginRules`로 특정 출처만 허용할 수 있어 보안에 유리합니다.  
규칙이 비어있으면 `addJavascriptInterface` 방식으로 fallback됩니다.

### 4.3 WebHandler를 통한 뒤로가기 처리

`"history"` 그룹은 `WebHandler` 인터페이스를 이용해 처리할 수 있습니다.

```kotlin
interface WebHandler {
    @Keep
    fun back()
}

webBridge.setHandler("history", object : WebHandler {
    override fun back() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            finish()
        }
    }
})
```

웹에서의 호출:

```javascript
dsmobileapp.web2app("history", "back", null);
```

---

## 5. 하이브리드 베이스 모듈과 통합

### 5.1 hybrid-app-activity-base 사용

`BaseMainActivity`를 상속하면 `IWebViewDelegate` 인터페이스를 구현하여 WebBridge를 제공합니다.

```kotlin
class MainActivity : BaseMainActivity(application) {

    private val handlerWebBridge = DefaultWebBridge().apply {
        setHandler("show", this@MainActivity)
        setHandler("login", object : LoginHandler {
            @Keep
            override fun ended(data: LoginData) {
                // 로그인 완료 처리
            }
            @Keep
            override fun logout() {
                // 로그아웃 처리
            }
        })
    }

    override val webBridge: WebBridge get() = handlerWebBridge
    override val webBridgeName: String get() = WebBridge.DEFAULT_WEB_BRIDGE_NAME
    override val webViewClient: WebViewClient get() = defaultWebViewClient
    override val webChromeClient: WebChromeClient get() = defaultChromeClient

    @Keep
    fun native(data: NativeData) {
        when (data.type) {
            "setting" -> startActivity(Intent(this, SettingActivity::class.java))
        }
    }
}
```

### 5.2 hybrid-app-fragment-base 사용

`BaseMainFragment`를 상속하여 동일한 패턴을 사용합니다.

```kotlin
class MainFragment : BaseMainFragment() {

    private val handlerWebBridge = DefaultWebBridge().apply {
        setHandler("show", this)
        setHandler("login", object : LoginHandler {
            @Keep
            override fun ended(data: LoginData) { /* ... */ }
            @Keep
            override fun logout() { /* ... */ }
        })
    }

    override val webBridge: WebBridge get() = handlerWebBridge
    override val webBridgeName: String get() = WebBridge.DEFAULT_WEB_BRIDGE_NAME
    override val webViewClient: WebViewClient get() = defaultWebViewClient
    override val webChromeClient: WebChromeClient get() = nestedChromeClient
}
```

---

## 6. 독립형 WebView 사용 예제

하이브리드 베이스 모듈 없이 `helper:web`만 사용하는 경우의 완전한 예제입니다.

```kotlin
class WebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    private val webBridge = DefaultWebBridge().apply {
        setHandler("show", this@WebViewActivity)
        setHandler("history", object : WebHandler {
            @Keep
            override fun back() {
                if (webView.canGoBack()) webView.goBack()
                else finish()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        webView = findViewById(R.id.webView)
        webView.apply {
            settings.javaScriptEnabled = true
            addJavascriptInterface(webBridge, WebBridge.DEFAULT_WEB_BRIDGE_NAME)
            loadUrl("https://example.com")
        }
    }

    @Keep
    fun native(data: NativeData) {
        Toast.makeText(this, data.type, Toast.LENGTH_SHORT).show()
    }
}
```

웹 페이지:

```javascript
dsmobileapp.web2app("show", "native", { type: "hello" });
// -> 토스트 메시지 "hello" 출력
```

---

## 7. 주의사항

1. **@Keep 어노테이션**  
   ProGuard/R8 난독화 시 `@JavascriptInterface`와 핸들러 메서드가 사라지지 않도록 `@Keep` 또는 난독화 예외 규칙을 추가해야 합니다.

2. **파라미터 개수**  
   `DefaultWebBridge`는 메서드 이름과 **파라미터 개수(0 또는 1)** 만으로 호출할 메서드를 식별합니다.  
   동일 이름의 오버로딩된 메서드가 있더라도 1개짜리 메서드만 선택됩니다.

3. **Gson 역직렬화**  
   `args`는 내부적으로 `Gson.fromJson()`으로 역직렬화되므로, 핸들러 메서드의 파라미터 타입은 Gson이 변환 가능한 타입이어야 합니다.

4. **args = {} fallback**  
   `args: {}`로 호출하면 `DefaultWebBridge`는 먼저 단일 파라미터 메서드를 찾고, 없으면 무인자 메서드를 다시 찾습니다.  
   의도치 않은 fallback 호출을 방지하려면 핸들러에서 명확히 구분하세요.

5. **UI 스레드**  
   `postMessage()`는 WebView 내부 스레드에서 호출되지만, `DefaultWebBridge.onWebMessage()`는 `Dispatchers.Main`에서 실행됩니다.  
   핸들러 메서드 안에서 UI 작업을 별도의 스레드 전환 없이 바로 수행할 수 있습니다.

6. **Origin 검증**  
   `addJavascriptInterface`는 모든 출처의 JavaScript에서 브리지를 호출할 수 있습니다.  
   보안이 중요하다면 `WebMessageListener` 방식과 `allowedOriginRules`를 사용하세요.

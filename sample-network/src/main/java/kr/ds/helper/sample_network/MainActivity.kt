package kr.ds.helper.sample_network

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kr.ds.network.RetrofitClient
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.http.GET

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.textView)

        // RetrofitClient.createService 시그니처 예시
        val service = RetrofitClient.createService(
            "https://jsonplaceholder.typicode.com/",
            ApiService::class.java,
            true,
            null,
            listOf()
        )

        lifecycleScope.launch {
            try {
                val posts = service.getPosts()
                textView.text = "Posts: ${posts.size}"
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                textView.text = "Error: ${e.message}"
            }
        }
    }

    interface ApiService {
        @GET("posts")
        suspend fun getPosts(): List<Post>
    }

    data class Post(
        val id: Int,
        val title: String,
        val body: String
    )
}

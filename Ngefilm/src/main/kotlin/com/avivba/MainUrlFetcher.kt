import com.lagradost.cloudstream3.app
import khttp.get 

object MainUrlFetcher {
    private var url: String? = null

    suspend fun getOrFetchUrl(): String {
        val murl = url
        if (murl != null) {
            return murl
        } else {
            val document = app.get("https://ngefilm21.pw").document
            val url = document.selectFirst("a")!!.attr("href")
            this.url = url
            return url
        }
    }
}